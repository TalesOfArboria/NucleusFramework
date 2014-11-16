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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.extended.serializable.SerializableBlockEntity;
import com.jcwhatever.bukkit.generic.extended.serializable.SerializableFurnitureEntity;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.performance.queued.QueueProject;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.performance.queued.QueueTask;
import com.jcwhatever.bukkit.generic.performance.queued.QueueWorker;
import com.jcwhatever.bukkit.generic.performance.queued.TaskConcurrency;
import com.jcwhatever.bukkit.generic.regions.RegionChunkFileLoader.LoadType;
import com.jcwhatever.bukkit.generic.regions.data.ChunkBlockInfo;
import com.jcwhatever.bukkit.generic.storage.IDataNode;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Abstract implementation for a region that
 * is savable and restorable from disk.
 */
public abstract class RestorableRegion extends BuildableRegion {

    private boolean _isRestoring = false;
    private boolean _isSaving = false;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the region.
     */
    public RestorableRegion(Plugin plugin, String name) {
        super(plugin, name);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the region.
     * @param dataNode  The regions data node.
     */
    public RestorableRegion(Plugin plugin, String name, @Nullable IDataNode dataNode) {
        super(plugin, name, dataNode);
    }

    /**
     * Determine if region is currently
     * int the process of being restored
     * from disk.
     */
    public final boolean isRestoring() {
        return _isRestoring;
    }

    /**
     * Determine if region is currently
     * in the process of saving to disk.
     */
    public final boolean isSaving() {
        return _isSaving;
    }

    /**
     * Save region data to disk
     */
    public Future saveData() throws IOException {
        return saveData("");
    }

    /*
     * Save region data to disk
     */
    protected Future saveData(String snapshotName) throws IOException {

        List<Chunk> chunks = this.getChunks();

        QueueProject project = new QueueProject(getPlugin());

        if (chunks.size() == 0) {
            project.cancel("Cannot save region because there are no chunks to save.");
            return project.getResult();
        }

        if (isSaving() || isRestoring()) {
            project.cancel("Cannot save region while it is already saving or restoring.");
            return project.getResult();
        }

        _isSaving = true;
        onSave();

        Messenger.debug(GenericsLib.getLib(), "RestorableRegion: saving data");

        for (Chunk chunk : chunks) {
            RegionChunkFileWriter writer = new RegionChunkFileWriter(this, chunk);
            writer.saveData(getChunkFile(chunk, snapshotName, true), project);
        }

        QueueWorker.get().addTask(project);

        return project.getResult()
                .onEnd(new Runnable() {
                    @Override
                    public void run() {
                        _isSaving = false;
                        onSaveComplete();
                        Messenger.info(GenericsLib.getLib(), "Restorable Region save complete.");
                    }
                });

    }

    /**
     * Determine if region data files exist
     * and can be restored.
     */
    public boolean canRestore() {
        return canRestore("");
    }

    /**
     * Determine if region data files for a
     * saved version of the region can be restored.
     *
     * @param version  The name of the restore version.
     */
    protected boolean canRestore(String version) {
        List<Chunk> chunks = getChunks();

        for (Chunk chunk : chunks) {
            File file;

            try {
                file = getChunkFile(chunk, version, false);
            } catch (IOException e) {
                return false;
            }

            if (file == null || !file.exists())
                return false;
        }
        return true;
    }

    /**
     * Restore region from disk
     *
     * @param buildMethod  The method used to restore.
     *
     * @throws IOException
     */
    public Future restoreData(BuildMethod buildMethod) throws IOException {
        return restoreData(buildMethod, "");
    }

    /**
     * Restore a saved version of the region from disk.
     *
     * @param buildMethod  The method used to restore.
     * @param version      The name of the restore version.
     *
     * @throws IOException
     */
    protected Future restoreData(BuildMethod buildMethod, String version) throws IOException {

        List<Chunk> chunks = getChunks();

        QueueProject restoreProject = new QueueProject(getPlugin());

        if (chunks.size() == 0) {
            return restoreProject.cancel("Restore cancelled. No chunks to restore.");
        }

        if (isSaving()) {
            return restoreProject.cancel("Restore cancelled: Region is still saving.");
        }

        if (isRestoring()) {
            return restoreProject.cancel("Restore cancelled: Region is still restoring.");
        }

        if (!canRestore()) {
            return restoreProject.cancel("Restore cancelled: Region cannot restore without restore files.");
        }

        _isRestoring = true;
        onRestore();

        removeEntities(Item.class, Monster.class, Animals.class);
        removeEntities(SerializableFurnitureEntity.getFurnitureClasses());

        for (Chunk chunk : chunks) {

            // create project for chunk
            QueueProject chunkProject = new QueueProject(getPlugin());

            // create chunk loader
            RegionChunkFileLoader loader = new RegionChunkFileLoader(this, chunk);

            // add load task to chunk project
            loader.loadInProject(getChunkFile(chunk, version, false), chunkProject, LoadType.MISMATCHED);

            // add restore blocks to chunk project
            chunkProject.addTask(new RestoreBlocks(loader));

            // add chunk project to restore project
            restoreProject.addTask(chunkProject);
        }

        switch (buildMethod) {
            case PERFORMANCE:
                QueueWorker.get().addTask(restoreProject);
                break;

            case BALANCED:
                restoreProject.run();
                break;

            case FAST:

                // get chunk projects
                List<QueueTask> tasks = restoreProject.getTasks();
                for (QueueTask task : tasks) {
                    task.run();
                }
                break;
        }

        return restoreProject.getResult().onEnd(new Runnable() {
            @Override
            public void run() {
                _isRestoring = false;
                onRestoreComplete();
                Messenger.info(getPlugin(), "Restorable Region restoration complete.");
            }
        });
    }

    /**
     * Delete region data from disk
     */
    public boolean deleteData() throws IOException {
        File regionData = getRegionDataFolder();
        return regionData.exists() && regionData.delete();
    }

    /**
     * Called when the coordinates are changed.
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     *
     * @throws IOException
     */
    @Override
    protected void onCoordsChanged(Location p1, Location p2) throws IOException {
        super.onCoordsChanged(p1, p2);

        if (this.canRestore()) {
            deleteData();
            saveData();
        }
    }

    /**
     * Called to get the save file prefix.
     *
     * <p>The prefix is used to distinguish the file from
     * other regions that might contain the same chunk(s). The prefix should
     * have a unique identifier for the region such as the region name.</p>
     */
    protected abstract String getFilePrefix();

    /**
     * Get the region base data folder.
     *
     * @throws IOException
     */
    protected final File getDataFolder() throws IOException {
        File folder = new File(getPlugin().getDataFolder(), "region-data");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create region data folder.");
        }
        return folder;
    }

    /**
     * Get the region data folder.
     *
     * @throws IOException
     */
    protected final File getRegionDataFolder() throws IOException {
        File dataFolder = new File(getDataFolder(), getFilePrefix());
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IOException("Failed to create region data folder.");
        }
        return dataFolder;
    }

    /**
     * Get the name of the file used to store data for the specified chunk.
     *
     * @param chunk    The chunk snapshot.
     * @param version  The name of the restore version.
     */
    protected final String getChunkFilename(ChunkSnapshot chunk, String version) {
        return getChunkFilename(chunk.getX(), chunk.getZ(), version);
    }

    /**
     * Get the name of the file used to store data for the specified chunk.
     *
     * @param chunk    The chunk snapshot.
     * @param version  The name of the restore version.
     */
    protected final String getChunkFilename(Chunk chunk, String version) {
        return getChunkFilename(chunk.getX(), chunk.getZ(), version);
    }

    /**
     * Get the name of the file used to store data for the specified chunk.
     *
     * @param chunkX   The chunk X coordinates.
     * @param chunkZ   The chunk Z coordinates.
     * @param version  The name of the restore version.
     */
    protected final String getChunkFilename(int chunkX, int chunkZ, String version) {
        version = version == null || version.isEmpty()
                ? ""
                : '.' + version;

        String prefix = getFilePrefix();
        return (prefix != null ? prefix : "") + ".chunk." + chunkX + '.' + chunkZ + version + ".bin";
    }

    /**
     * Get the file used to store data for the specified chunk.
     *
     * @param chunk             The chunk snapshot.
     * @param version           The name of the restore version.
     * @param doDeleteExisting  True to delete existing file.
     *
     * @throws IOException
     */
    protected final File getChunkFile(Chunk chunk, String version, boolean doDeleteExisting) throws IOException {
        return getChunkFile(chunk.getX(), chunk.getZ(), version, doDeleteExisting);
    }

    /**
     * Get the file used to store data for the specified chunk.
     *
     * @param chunkX            The chunk X coordinates.
     * @param chunkZ            The chunk Z coordinates.
     * @param version           The name of the restore version.
     * @param doDeleteExisting  True to delete existing file.
     *
     * @throws IOException
     */
    protected final File getChunkFile(int chunkX, int chunkZ, String version, boolean doDeleteExisting) throws IOException {
        File dir = getRegionDataFolder();
        File file = new File(dir, getChunkFilename(chunkX, chunkZ, version));

        if (!doDeleteExisting)
            return file;

        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete chunk file: " + file.getName());
        }
        return file;
    }

    /**
     * Called before the region is restored.
     */
    protected void onRestore() {}

    /**
     * Called after the region is restored.
     */
    protected void onRestoreComplete() {}

    /**
     * Called before the region is saved.
     */
    protected void onSave() {}

    /**
     * Called after the region is saved.
     */
    protected void onSaveComplete() {}

    /**
     * Restore blocks from blockInfo stack on
     * the main thread.
     */
    private static final class RestoreBlocks extends QueueTask {

        private final RegionChunkFileLoader loader;
        private final Chunk chunk;

        public RestoreBlocks (RegionChunkFileLoader loader) {
            super(loader.getRegion().getPlugin(), TaskConcurrency.MAIN_THREAD);

            this.loader = loader;
            this.chunk = loader.getChunk();
        }

        @Override
        protected void onRun() {

            LinkedList<ChunkBlockInfo> blockInfo = loader.getBlockInfo();
            LinkedList<ChunkBlockInfo> doors = new LinkedList<>();

            while (!blockInfo.isEmpty()) {
                ChunkBlockInfo info = blockInfo.remove();

                // skip doors and restore later
                if (info.getMaterial() == Material.IRON_DOOR_BLOCK ||
                        info.getMaterial() == Material.WOODEN_DOOR) {
                    doors.add(info);
                    continue;
                }

                restoreBlock(info);
            }

            // Restore door block Pairs
            // keyed to block x, y z value as a string
            MultiValueMap<String, ChunkBlockInfo> _placedDoorBlocks = new MultiValueMap<>(doors.size());

            // Get door block pairs
            while (!doors.isEmpty()) {
                ChunkBlockInfo info = doors.remove();

                int x = info.getChunkBlockX();
                int y = info.getY();
                int z = info.getChunkBlockZ();

                String lowerKey = getKey(x, y - 1, z);
                List<ChunkBlockInfo> lowerDoor = _placedDoorBlocks.getValues(lowerKey);

                if (lowerDoor != null) {
                    _placedDoorBlocks.put(lowerKey, info);
                    continue;
                }

                String upperKey = getKey(x, y + 1, z);
                List<ChunkBlockInfo> upperDoor = _placedDoorBlocks.getValues(upperKey);
                if (upperDoor != null) {
                    _placedDoorBlocks.put(upperKey, info);
                    continue;
                }

                _placedDoorBlocks.put(getKey(x, y, z), info);
            }

            // Restore pairs
            Set<String> keys = _placedDoorBlocks.keySet();

            for (String key : keys) {
                List<ChunkBlockInfo> doorPair = _placedDoorBlocks.getValues(key);

                Collections.sort(doorPair);

                for (ChunkBlockInfo info : doorPair) {
                    restoreBlock(info);
                }
            }

            new RestoreBlockEntities(loader).run();
            new RestoreEntities(loader).run();

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
        private void restoreBlock(ChunkBlockInfo info) {

            int x = info.getChunkBlockX();
            int y = info.getY();
            int z = info.getChunkBlockZ();

            Block block = chunk.getBlock(x, y, z);
            BlockState state = block.getState();

            state.setType(info.getMaterial());
            state.setRawData((byte) info.getData());

            state.update(true);
        }

    } // END RestoreBlocks


    /**
     * Restore block entities from file
     */
    final static class RestoreBlockEntities implements Runnable {

        private final LinkedList<SerializableBlockEntity> blockMeta;

        public RestoreBlockEntities(RegionChunkFileLoader loader) {
            this.blockMeta = loader.getBlockEntityInfo();
        }

        @Override
        public void run() {

            while (!blockMeta.isEmpty()) {
                SerializableBlockEntity meta = blockMeta.remove();
                meta.apply();
            }
        }

    } // END RestoreTileEntities

    /**
     * Restore entities from file.
     */
    static final class RestoreEntities implements Runnable {

        private final RegionChunkFileLoader loader;

        public RestoreEntities(RegionChunkFileLoader loader) {
            this.loader = loader;
        }

        @Override
        public void run() {

            LinkedList<SerializableFurnitureEntity> furnitureEntities = loader.getFurnitureEntityInfo();

            while (!furnitureEntities.isEmpty()) {
                SerializableFurnitureEntity meta = furnitureEntities.remove();

                meta.spawn();
            }
        }

    } // END RestoreEntities






}


