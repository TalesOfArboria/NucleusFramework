/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.extended.serializable.SerializableBlockEntity;
import com.jcwhatever.bukkit.generic.extended.serializable.SerializableFurnitureEntity;
import com.jcwhatever.bukkit.generic.file.GenericsByteWriter;
import com.jcwhatever.bukkit.generic.performance.queued.Iteration3DTask;
import com.jcwhatever.bukkit.generic.performance.queued.QueueProject;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.performance.queued.TaskConcurrency;
import com.jcwhatever.bukkit.generic.regions.data.RegionChunkSection;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import javax.annotation.Nullable;

/**
 * Takes a snapshot of a section of a chunk which
 * represents the portion of a region that is
 * contained within the chunk.
 *
 * <p>Use in conjunction with {@code RegionChunkFileLoader}.</p>
 */
public final class RegionChunkSnapshot implements ChunkSnapshot {

    public static final int RESTORE_FILE_VERSION = 1;

    private Plugin _plugin;
    private Region _region;
    private World _world;
    private ChunkSnapshot _snapshot;
    private RegionChunkSection _section;

    private final LinkedList<SerializableBlockEntity> _tileEntities = new LinkedList<>();
    private final LinkedList<SerializableFurnitureEntity> _entities = new LinkedList<>();

    private boolean _isSaving;

    /**
     * Constructor.
     *
     * @param region  The region the snapshot is for.
     * @param chunkX  The X coordinates of the chunk.
     * @param chunkZ  The Y coordinates of the chunk.
     */
    public RegionChunkSnapshot (Region region, int chunkX, int chunkZ) {
        if (!region.isDefined())
            throw new RuntimeException("Cannot get a snapshot from an undefined region.");

        //noinspection ConstantConditions
        Chunk chunk = region.getWorld().getChunkAt(chunkX, chunkZ);
        init(region, chunk);
    }

    /**
     * Constructor.
     *
     * @param region  The region the snapshot is for.
     * @param chunk   The chunk to snapshot.
     */
    public RegionChunkSnapshot (Region region, Chunk chunk) {
        init(region, chunk);
    }

    /**
     * Get the region the snapshot is for.
     */
    public Region getRegion() {
        return _region;
    }

    /**
     * Determine if the snapshot is in the process
     * of saving to a file.
     */
    public boolean isSaving() {
        return _isSaving;
    }

    /**
     * Save the chunk section snapshot to a file.
     *
     * <p>Runs task immediately.</p>
     *
     * @param file  The file to save to.
     */
    public Future saveData(File file) {
        return saveData(file, null);
    }

    /**
     * Save the chunk section snapshot to a file.
     *
     * <p>Runs task immediately if no {@code QueueProject} is provided.</p>
     *
     * @param file     The file to save to.
     * @param project  The optional project to add tasks to.
     */
    public Future saveData(File file, @Nullable QueueProject project) {
        PreCon.notNull(file);

        boolean runNow = project == null;

        if (project == null)
            project = new QueueProject(_plugin);

        if (isSaving())
            return project.cancel("Cannot save region because it is already saving.");

        _isSaving = true;

        SaveChunkIterator iterator = new SaveChunkIterator(file, 8192,
                _section.getStartChunkX(), _section.getStartY(), _section.getStartChunkZ(),
                _section.getEndChunkX(), _section.getEndY(), _section.getEndChunkZ());

        project.addTask(iterator);

        if (runNow) {
            project.run();
        }

        return project.getResult().onEnd(new Runnable() {
            @Override
            public void run() {
                _isSaving = false;
            }
        });
    }

    @Override
    public Biome getBiome(int x, int z) {
        return _snapshot.getBiome(x, z);
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return _snapshot.getBlockData(x, y, z);
    }

    @Override
    public int getBlockEmittedLight(int x, int y, int z) {
        return _snapshot.getBlockEmittedLight(x, y, z);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return _snapshot.getBlockSkyLight(x, y, z);
    }

    @Override
    public int getBlockTypeId(int x, int y, int z) {
        return _snapshot.getBlockTypeId(x, y, z);
    }

    @Override
    public long getCaptureFullTime() {
        return _snapshot.getCaptureFullTime();
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return _snapshot.getHighestBlockYAt(x, z);
    }

    @Override
    public double getRawBiomeRainfall(int x, int z) {
        return _snapshot.getRawBiomeRainfall(x, z);
    }

    @Override
    public double getRawBiomeTemperature(int x, int z) {
        return _snapshot.getRawBiomeTemperature(x, z);
    }

    @Override
    public String getWorldName() {
        return _snapshot.getWorldName();
    }

    @Override
    public int getX() {
        return _snapshot.getX();
    }

    @Override
    public int getZ() {
        return _snapshot.getZ();
    }

    @Override
    public boolean isSectionEmpty(int arg0) {
        return _snapshot.isSectionEmpty(arg0);
    }

    private void init(Region region, Chunk chunk) {
        _plugin = region.getPlugin();
        _region = region;
        _world = region.getWorld();
        _snapshot = chunk.getChunkSnapshot();
        _section = new RegionChunkSection(region, _snapshot);

        // get tile entities from chunk
        BlockState[] tileEntities = chunk.getTileEntities();

        for (BlockState tile : tileEntities) {

            // make sure the tile entity is contained within the section
            if (_section.containsBlockCoords(tile.getX(), tile.getY(), tile.getZ()))
                _tileEntities.add(new SerializableBlockEntity(tile));
        }

        // get entities from chunk
        Entity[] entities = chunk.getEntities();

        for (Entity entity : entities) {

            if (!entity.isValid())
                continue;

            if (!SerializableFurnitureEntity.isFurnitureEntity(entity))
                continue;

            Location entityLoc = entity.getLocation();

            // make sure the entity is contained within the section
            if (!_section.containsBlockCoords(entityLoc.getBlockX(), entityLoc.getBlockY(), entityLoc.getBlockZ()))
                continue;

            _entities.add(new SerializableFurnitureEntity(entity));
        }
    }

    /**
     * Iteration worker for saving a region area within a specified
     * chunk to a file.
     */
    private final class SaveChunkIterator extends Iteration3DTask {

        private GenericsByteWriter writer;
        private final File file;

        public SaveChunkIterator (File file, long segmentSize,
                                  int xStart, int yStart, int zStart,
                                  int xEnd, int yEnd, int zEnd) {
            super(_plugin, TaskConcurrency.ASYNC, segmentSize, xStart, yStart, zStart, xEnd, yEnd, zEnd);

            this.file = file;
        }

        /**
         * Write file header.
         */
        @Override
        protected void onIterateBegin() {

            try {
                writer = new GenericsByteWriter(new FileOutputStream(file));
                writer.write(RESTORE_FILE_VERSION);
                writer.write(_region.getName());
                writer.write(_world.getName());
                writer.write(_region.getP1()); // Location
                writer.write(_region.getP2()); // Location
                writer.write(getVolume());
            }
            catch (IOException io) {
                io.printStackTrace();

                fail("IOException while writing region header.");
                _isSaving = false;
            }
        }


        /**
         * Save section blocks to file
         */
        @Override
        public void onIterateItem(int x, int y, int z) {

            Material type = Material.getMaterial(_snapshot.getBlockTypeId(x,  y, z));
            int data = _snapshot.getBlockData(x,  y,  z);

            try {
                writer.write(type);
                writer.write(data);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();

                fail("IOException while writing region data.");
                _isSaving = false;
            }
        }

        /**
         * Write Block Entities and Entities to end of file
         * after block data is successfully written.
         */
        @Override
        protected void onPreComplete() {
            try {

                // write Block Entities
                writer.write(_tileEntities.size());

                while (!_tileEntities.isEmpty()) {
                    SerializableBlockEntity tileEntity = _tileEntities.remove();
                    writer.write(tileEntity);
                }

                // write Entities
                writer.write(_entities.size());

                while (!_entities.isEmpty()) {
                    SerializableFurnitureEntity entity = _entities.remove();
                    writer.write(entity);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                fail("Failed to write block entities and/or entities to file.");
            }
        }

        @Override
        protected void onEnd() {
            cleanup();
        }

        private void cleanup() {
            _isSaving = false;

            if (writer != null) {
                try {
                    // close chunk file
                    writer.close();
                }
                catch (IOException io) {
                    io.printStackTrace();

                    fail("IOException while closing file input stream.");
                }
            }
        }

    }

}
