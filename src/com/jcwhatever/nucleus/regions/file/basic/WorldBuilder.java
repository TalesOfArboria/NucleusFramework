/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.regions.file.basic;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.regions.file.IRegionFileData;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.SerializableBlockEntity;
import com.jcwhatever.nucleus.utils.file.SerializableFurnitureEntity;
import com.jcwhatever.nucleus.utils.materials.Materials;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;
import com.jcwhatever.nucleus.utils.performance.queued.TaskConcurrency;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Builds region data into a world.
 *
 * <p>Basic implementation of {@link IRegionFileData}</p>
 */
public class WorldBuilder implements IRegionFileData, IPluginOwned {

    private final Plugin _plugin;
    private final World _world;
    private final Object _sync = new Object();

    private Builder _builder;

    /**
     * Constructor.
     *
     * @param plugin       The owning plugin.
     * @param world        The world to build in.
     */
    public WorldBuilder(Plugin plugin, World world) {
        PreCon.notNull(plugin);
        PreCon.notNull(world);

        _plugin = plugin;
        _world = world;
        _builder = new Builder(plugin, world);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    public World getWorld() {
        return _world;
    }

    @Override
    public void addBlock(int x, int y, int z, Material material, int data, int light, int skylight) {
        synchronized (_sync) {
            _builder.blockQueue.addLast(new BlockInfo(x, y, z, material, data));
        }
    }

    @Override
    public void addBlockEntity(SerializableBlockEntity blockEntity) {
        synchronized (_sync) {
            _builder.tileQueue.addLast(blockEntity);
        }
    }

    @Override
    public void addEntity(SerializableFurnitureEntity entity) {
        synchronized (_sync) {
            _builder.entityQueue.addLast(entity);
        }
    }

    @Override
    @Nullable
    public QueueTask commit() {

        Builder builder;

        synchronized (_sync) {
            builder = _builder;
            _builder = new Builder(getPlugin(), getWorld());
        }

        return builder;
    }

    private static class BlockInfo implements Comparable<BlockInfo> {

        final int x;
        final int y;
        final int z;
        final Material material;
        final int data;

        BlockInfo(int x, int y, int z, Material material, int data) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
            this.data = data;
        }

        @Override
        public int compareTo(BlockInfo o) {
            //noinspection SuspiciousNameCombination
            return Integer.compare(this.y, o.y);
        }
    }

    /*
     * Restore blocks from blockInfo stack on the main thread.
     */
    private static class Builder extends QueueTask {

        World world;
        LinkedList<BlockInfo> blockQueue = new LinkedList<>();
        LinkedList<SerializableBlockEntity> tileQueue = new LinkedList<>();
        LinkedList<SerializableFurnitureEntity> entityQueue = new LinkedList<>();

        /**
         * Constructor.
         *
         * @param plugin  The owning plugin.
         * @param world   The world to build in.
         */
        public Builder(Plugin plugin, World world) {
            super(plugin, TaskConcurrency.MAIN_THREAD);

            this.world = world;
        }

        @Override
        protected void onRun() {

            LinkedList<BlockInfo> multiBlocks = new LinkedList<>();

            while (!blockQueue.isEmpty()) {
                BlockInfo info = blockQueue.removeFirst();

                // skip multi-blocks and restore afterwards
                if (Materials.isMultiBlock(info.material)) {
                    multiBlocks.add(info);
                    continue;
                }

                restoreBlock(info);
            }

            // Restore block Pairs
            // keyed to block x, y z value as a string
            Multimap<String, BlockInfo> placedMultiBlocks =
                    MultimapBuilder.hashKeys(multiBlocks.size()).hashSetValues(3).build();

            // Get block pairs
            while (!multiBlocks.isEmpty()) {
                BlockInfo info = multiBlocks.remove();

                int x = info.x;
                int y = info.y;
                int z = info.z;

                String lowerKey = getKey(x, y - 1, z);
                Collection<BlockInfo> lowerBlock = placedMultiBlocks.get(lowerKey);

                if (lowerBlock != null) {
                    placedMultiBlocks.put(lowerKey, info);
                    continue;
                }

                String upperKey = getKey(x, y + 1, z);
                Collection<BlockInfo> upperBlock = placedMultiBlocks.get(upperKey);
                if (upperBlock != null) {
                    placedMultiBlocks.put(upperKey, info);
                    continue;
                }

                placedMultiBlocks.put(getKey(x, y, z), info);
            }

            // Restore pairs
            Set<String> keys = placedMultiBlocks.keySet();

            for (String key : keys) {

                Collection<BlockInfo> multiBlockSetSet = placedMultiBlocks.get(key);
                if (multiBlockSetSet == null)
                    continue;

                List<BlockInfo> multiBlockSet = new ArrayList<>(multiBlockSetSet);

                Collections.sort(multiBlockSet);

                for (BlockInfo info : multiBlockSet) {
                    restoreBlock(info);
                }
            }

            // restore tiles
            while (!tileQueue.isEmpty()) {
                SerializableBlockEntity meta = tileQueue.removeFirst();
                meta.apply();
            }

            // restore entities
            while (!entityQueue.isEmpty()) {
                SerializableFurnitureEntity meta = entityQueue.removeFirst();

                meta.spawn();
            }

            complete();
        }

        /*
         * Get a block map key using its coordinates
         */
        private String getKey(int x, int y, int z) {
            return String.valueOf(x) + '.' + y + '.' + z;
        }

        /*
         * Restore a block
         */
        private void restoreBlock(BlockInfo info) {

            int x = info.x;
            int y = info.y;
            int z = info.z;

            Block block = this.world.getBlockAt(x, y, z);
            BlockState state = block.getState();

            state.setType(info.material);
            state.setRawData((byte) info.data);

            state.update(true);
        }
    }
}
