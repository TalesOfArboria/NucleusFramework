/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.performance.queued.Iteration3DTask;
import com.jcwhatever.bukkit.generic.performance.queued.QueueProject;
import com.jcwhatever.bukkit.generic.performance.queued.QueueTask;
import com.jcwhatever.bukkit.generic.performance.queued.QueueWorker;
import com.jcwhatever.bukkit.generic.performance.queued.TaskConcurrency;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * An abstract implementation of a region that can easily
 * build within itself.
 */
public abstract class BuildableRegion extends Region {

    private boolean _isBuilding;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public BuildableRegion(Plugin plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the region.
     */
    public BuildableRegion(Plugin plugin, String name) {
        super(plugin, name);
        _plugin = plugin;
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the region.
     * @param settings  The regions data node.
     */
    public BuildableRegion(Plugin plugin, String name, IDataNode settings) {
        super(plugin, name, settings);
        _plugin = plugin;
    }

    /**
     * Determine if the region is in the process of building.
     */
    public final boolean isBuilding() {
        return _isBuilding;
    }

    /**
     * Get an empty 3D array representing the region which
     * can be used to specify what to build.
     */
    public final ItemStack[][][] getBuildArray() {
        return new ItemStack[getXBlockWidth()][getYBlockHeight()][getZBlockWidth()];
    }

    /**
     * Build in the region using the specified chunk snapshots.
     *
     * @param buildMethod  The method of building. (Speed vs Performance)
     * @param snapshots    The snapshots representing the build.
     */
    public final boolean build(BuildMethod buildMethod, Collection<? extends ChunkSnapshot> snapshots) {

        // already building
        if (_isBuilding)
            return false;

        if (!isDefined())
            return false;

        _isBuilding = true;

        QueueProject project = new QueueProject(_plugin);

        for (ChunkSnapshot snapshot : snapshots) {

            // get region chunk section calculations
            RegionChunkSection section = new RegionChunkSection(this, snapshot);

            // get build chunk iterator task
            BuildChunkIterator iterator = new BuildChunkIterator(this, snapshot, -1,
                    section.getStartChunkX(), section.getStartY(), section.getStartChunkZ(),
                    section.getEndChunkX(), section.getEndY(), section.getEndChunkZ());

            // add task to project
            project.addTask(iterator);
        }

        switch (buildMethod) {
            case PERFORMANCE:
                QueueWorker.get().addTask(project);
                break;

            case BALANCED:
                project.run();
                break;

            case FAST:
                List<QueueTask> tasks = project.getTasks();
                for (QueueTask task : tasks) {
                    task.run();
                }
                break;
        }

        project.getResult().onEnd(new Runnable() {
            @Override
            public void run() {
                _isBuilding = false;
            }
        });

        return true;
    }

    /*
     * Iteration worker for restoring a region area within the
     * specified chunk.
     */
    private final class BuildChunkIterator extends Iteration3DTask {

        private final ChunkSnapshot snapshot;
        private final Chunk chunk;
        private final LinkedList<BlockState> blocks = new LinkedList<>();

        public BuildChunkIterator (Region region, ChunkSnapshot snapshot, long segmentSize,
                                   int xStart, int yStart, int zStart,
                                   int xEnd, int yEnd, int zEnd) {

            super(_plugin, TaskConcurrency.ASYNC, segmentSize, xStart, yStart, zStart, xEnd, yEnd, zEnd);

            this.snapshot = snapshot;

            //noinspection ConstantConditions
            this.chunk = region.getWorld().getChunkAt(snapshot.getX(), snapshot.getZ());
        }

        /*
         * Store blocks to be changed so they can be updated all at once.
         */
        @Override
        public void onIterateItem(int x, int y, int z) {

            Material type = Material.getMaterial(snapshot.getBlockTypeId(x, y, z));
            int data = snapshot.getBlockData(x, y, z);

            Block block = chunk.getBlock(x, y, z);
            BlockState state = block.getState();

            if (state.getType() != type || (type != Material.AIR && state.getData().getData() != data)) {
                state.setType(type);
                state.setRawData((byte)data);
                this.blocks.add(state);
            }
        }

        @Override
        protected void onPreComplete() {

            // schedule block update
            Scheduler.runTaskSync(_plugin, 1, new UpdateBlocks());
        }

        /**
         * Update block states for chunk all at once on the
         * the main thread.
         */
        final class UpdateBlocks implements Runnable {

            @Override
            public final void run() {

                while (!blocks.isEmpty()) {
                    BlockState state = blocks.removeFirst();
                    state.update(true, false);
                }
            }
        }
    }

}
