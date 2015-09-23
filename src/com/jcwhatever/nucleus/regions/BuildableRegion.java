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


package com.jcwhatever.nucleus.regions;

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.regions.data.RegionChunkSection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import com.jcwhatever.nucleus.utils.performance.queued.Iteration3DTask;
import com.jcwhatever.nucleus.utils.performance.queued.QueueProject;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;
import com.jcwhatever.nucleus.utils.performance.queued.QueueWorker;
import com.jcwhatever.nucleus.utils.performance.queued.TaskConcurrency;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Queue;


/**
 * An abstract implementation of a region that can easily build within itself.
 */
public abstract class BuildableRegion extends Region {

    private boolean _isBuilding;

    /**
     * Specifies the build speed.
     */
    public enum BuildSpeed {
        /**
         * Maximize server performance at the cost of build speed.
         */
        PERFORMANCE,
        /**
         * Balanced between speed and performance.
         */
        BALANCED,
        /**
         * Build as fast as possible.
         */
        FAST
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the region.
     */
    public BuildableRegion(Plugin plugin, String name) {
        super(plugin, name, null);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the region.
     * @param dataNode  The regions data node.
     */
    public BuildableRegion(Plugin plugin, String name, @Nullable IDataNode dataNode) {
        super(plugin, name, dataNode);
    }

    /**
     * Determine if the region is in the process of building.
     */
    public final boolean isBuilding() {
        return _isBuilding;
    }

    /**
     * Create an empty 3D array representing the region which can be used to specify what to build.
     */
    public final MaterialData[][][] getBuildArray() {
        return new MaterialData[getXBlockWidth()][getYBlockHeight()][getZBlockWidth()];
    }

    /**
     * Build in the region using the specified chunk snapshots.
     *
     * @param buildSpeed  The speed of building.
     * @param snapshots   The snapshots representing the build.
     *
     * @return  True if build started, false if build already in progress or region is not defined.
     */
    public final boolean build(BuildSpeed buildSpeed, Collection<? extends ChunkSnapshot> snapshots) {

        // already building
        if (_isBuilding)
            return false;

        if (!isDefined())
            return false;

        _isBuilding = true;

        QueueProject project = new QueueProject(getPlugin());

        for (ChunkSnapshot snapshot : snapshots) {

            World world = Bukkit.getWorld(snapshot.getWorldName());
            if (world == null) {
                NucMsg.debug(getPlugin(),
                        "Failed to get world named '{0}' while building region '{1}'.",
                        snapshot.getWorldName(), getName());
                continue;
            }

            Chunk chunk = world.getChunkAt(snapshot.getX(), snapshot.getZ());
            if (chunk == null) {
                NucMsg.debug(getPlugin(),
                        "Failed to get chunk ({0}, {1}) in world '{0}' while building region '{1}'.",
                        snapshot.getX(), snapshot.getZ(), snapshot.getWorldName(), getName());
                continue;
            }

            if (!chunk.isLoaded()) {
                chunk.load();
            }

            // get region chunk section calculations
            RegionChunkSection section = new RegionChunkSection(this, snapshot);

            // get build chunk iterator task
            BuildChunkIterator iterator = new BuildChunkIterator(this, snapshot, -1,
                    section.getStartChunkX(), section.getStartY(), section.getStartChunkZ(),
                    section.getEndChunkX(), section.getEndY(), section.getEndChunkZ());

            // add task to project
            project.addTask(iterator);
        }

        switch (buildSpeed) {
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

        project.getResult().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable CharSequence message) {
                _isBuilding = false;
            }
        });

        return true;
    }

    /*
     * Iteration worker for building in region area within the specified chunk.
     */
    private static final class BuildChunkIterator extends Iteration3DTask {

        private final ChunkSnapshot snapshot;
        private final Chunk chunk;
        private final Queue<BlockState> blocks;

        public BuildChunkIterator (Region region, ChunkSnapshot snapshot, long segmentSize,
                                   int xStart, int yStart, int zStart,
                                   int xEnd, int yEnd, int zEnd) {

            super(region.getPlugin(), TaskConcurrency.ASYNC, segmentSize, xStart, yStart, zStart, xEnd, yEnd, zEnd);

            this.blocks = new ArrayDeque<>((int)this.getVolume());
            this.snapshot = snapshot;

            //noinspection ConstantConditions
            this.chunk = region.getWorld().getChunkAt(snapshot.getX(), snapshot.getZ());
        }

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
            Scheduler.runTaskSync(getPlugin(), 1, new UpdateBlocks());
        }

        /*
         * Update block states for chunk all at once on the
         * the main thread.
         */
        final class UpdateBlocks implements Runnable {

            @Override
            public final void run() {

                while (!blocks.isEmpty()) {
                    BlockState state = blocks.remove();
                    state.update(true, false);
                }
            }
        }
    }
}
