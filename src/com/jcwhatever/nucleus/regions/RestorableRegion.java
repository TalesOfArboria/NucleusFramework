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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.regions.file.RegionChunkFileLoader;
import com.jcwhatever.nucleus.regions.file.RegionChunkFileLoader.LoadType;
import com.jcwhatever.nucleus.regions.file.RegionChunkFileWriter;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.coords.ChunkBlockInfo;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;
import com.jcwhatever.nucleus.utils.coords.ICoords2Di;
import com.jcwhatever.nucleus.utils.file.SerializableBlockEntity;
import com.jcwhatever.nucleus.utils.file.SerializableFurnitureEntity;
import com.jcwhatever.nucleus.utils.materials.Materials;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import com.jcwhatever.nucleus.utils.performance.queued.QueueProject;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;
import com.jcwhatever.nucleus.utils.performance.queued.QueueWorker;
import com.jcwhatever.nucleus.utils.performance.queued.TaskConcurrency;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Abstract implementation for a region that is savable to and restorable from disk.
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
     * Determine if region is currently in the process of being restored
     * from disk.
     */
    public final boolean isRestoring() {
        return _isRestoring;
    }

    /**
     * Determine if region is currently in the process of saving to disk.
     */
    public final boolean isSaving() {
        return _isSaving;
    }

    /**
     * Save region data to disk.
     *
     * @return  A future to receive the results of the save operation.
     */
    public IFuture saveData() throws IOException {
        return saveData("");
    }

    /**
     * Save region data to disk using a specific snapshot.
     *
     * @param snapshotName  The name of the snapshot to save.
     *
     * @return  A future to receive the results of the save operation.
     */
    protected IFuture saveData(String snapshotName) throws IOException {

        Collection<IChunkCoords> chunks = this.getChunkCoords();

        QueueProject project = new QueueProject(getPlugin());

        if (chunks.size() == 0)
            return project.cancel("Cannot save region because there are no chunks to save.");

        if (isSaving() || isRestoring())
            return project.cancel("Cannot save region while it is already saving or restoring.");

        _isSaving = true;
        onPreSave();

        NucMsg.debug(getPlugin(), "RestorableRegion: saving data for region '{0}'", getName());

        for (IChunkCoords chunk : chunks) {
            RegionChunkFileWriter writer = new RegionChunkFileWriter(this, chunk);
            writer.saveData(getChunkFile(chunk.getX(), chunk.getZ(), snapshotName, true), project);
        }

        QueueWorker.get().addTask(project);

        return project.getResult().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable String message) {
                _isSaving = false;
                onSaveComplete();
                NucMsg.debug(getPlugin(), "Restorable Region '{0}' save complete.", getName());
            }
        });
    }

    /**
     * Determine if region data files exist and can be restored.
     */
    public boolean canRestore() {
        return canRestore("");
    }

    /**
     * Determine if region data files for a saved snapshot of the region can be restored.
     *
     * @param snapshotName  The name of the snapshot.
     */
    protected boolean canRestore(String snapshotName) {

        Collection<IChunkCoords> chunks = getChunkCoords();

        for (IChunkCoords chunk : chunks) {
            File file;

            try {
                file = getChunkFile(chunk.getX(), chunk.getZ(), snapshotName, false);
            } catch (IOException e) {
                return false;
            }

            if (file == null || !file.exists())
                return false;
        }
        return true;
    }

    /**
     * Restore region from disk.
     *
     * @param buildMethod  The method used to restore.
     *
     * @return  A future to receive the results of the restore operation.
     *
     * @throws IOException
     */
    public IFuture restoreData(BuildMethod buildMethod) throws IOException {
        return restoreData(buildMethod, "");
    }

    /**
     * Restore a saved snapshot of the region from disk.
     *
     * @param buildMethod  The method used to restore.
     * @param version      The name of the restore version.
     *
     * @return  A future to receive the results of the restore operation.
     *
     * @throws IOException
     */
    protected IFuture restoreData(BuildMethod buildMethod, String version) throws IOException {

        Collection<IChunkCoords> chunks = getChunkCoords();
        QueueProject restoreProject = new QueueProject(getPlugin());

        if (chunks.size() == 0)
            return restoreProject.cancel("No chunks to restore.");

        if (isSaving())
            return restoreProject.cancel("Region is still saving.");

        if (isRestoring())
            return restoreProject.cancel("Region is still restoring.");

        if (!canRestore())
            return restoreProject.cancel("Region cannot restore without restore files.");

        _isRestoring = true;
        onPreRestore();

        removeEntities(Item.class, Monster.class, Animals.class);
        removeEntities(SerializableFurnitureEntity.getFurnitureClasses());

        for (IChunkCoords chunk : chunks) {

            QueueProject chunkProject = new QueueProject(getPlugin());
            RegionChunkFileLoader loader = new RegionChunkFileLoader(this, chunk);

            // add load task to chunk project
            loader.loadInProject(
                    getChunkFile(chunk.getX(), chunk.getZ(), version, false),
                    chunkProject, LoadType.MISMATCHED);

            // add restore blocks to chunk project
            chunkProject.addTask(new RestoreBlocks(loader));

            // add chunk project to restore project
            restoreProject.addTask(chunkProject);
        }

        // run project based on build method
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

        return restoreProject.getResult().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable String message) {
                _isRestoring = false;
                onRestore();
            }
        });
    }

    /**
     * Delete region data from disk.
     */
    public boolean deleteData() throws IOException {
        File regionData = getRegionDataFolder();
        return regionData.exists() && regionData.delete();
    }

    @Override
    protected void onCoordsChanged(Location p1, Location p2) {
        super.onCoordsChanged(p1, p2);

        if (this.canRestore()) {
            try {
                deleteData();
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Invoked to get the save file prefix.
     *
     * <p>The prefix is used to distinguish the file from other regions that might
     * contain the same chunk(s). The prefix should have a unique identifier for the
     * region such as the region name.</p>
     */
    protected String getFilePrefix() {
        return getName();
    }

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
     * @param chunkX            The chunk X coordinates.
     * @param chunkZ            The chunk Z coordinates.
     * @param version           The name of the restore version.
     * @param doDeleteExisting  True to delete existing file.
     *
     * @throws IOException
     */
    protected final File getChunkFile(int chunkX, int chunkZ, String version, boolean doDeleteExisting)
            throws IOException {

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
     * Invoked before the region is restored.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onPreRestore() {}

    /**
     * Invoked after the region is restored.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onRestore() {}

    /**
     * Invoked before the region is saved.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onPreSave() {}

    /**
     * Invoked after the region is saved.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onSaveComplete() {}

    /*
     * Restore blocks from blockInfo stack on the main thread.
     */
    private static final class RestoreBlocks extends QueueTask {

        private final RegionChunkFileLoader loader;
        private final Chunk chunk;

        public RestoreBlocks (RegionChunkFileLoader loader) {
            super(loader.getRegion().getPlugin(), TaskConcurrency.MAIN_THREAD);

            this.loader = loader;

            ICoords2Di coords = loader.getChunkCoord();
            World world = loader.getRegion().getWorld();
            assert world != null;

            this.chunk = world.getChunkAt(coords.getX(), coords.getZ());
        }

        @Override
        protected void onRun() {

            LinkedList<ChunkBlockInfo> blockInfo = loader.getBlockInfo();
            LinkedList<ChunkBlockInfo> multiBlocks = new LinkedList<>();

            while (!blockInfo.isEmpty()) {
                ChunkBlockInfo info = blockInfo.remove();

                // skip multi-blocks and restore afterwards
                if (Materials.isMultiBlock(info.getMaterial())) {
                    multiBlocks.add(info);
                    continue;
                }

                restoreBlock(info);
            }

            // Restore block Pairs
            // keyed to block x, y z value as a string
            Multimap<String, ChunkBlockInfo> _placedMultiBlocks =
                    MultimapBuilder.hashKeys(multiBlocks.size()).hashSetValues(3).build();

            // Get block pairs
            while (!multiBlocks.isEmpty()) {
                ChunkBlockInfo info = multiBlocks.remove();

                int x = info.getX();
                int y = info.getY();
                int z = info.getZ();

                String lowerKey = getKey(x, y - 1, z);
                Collection<ChunkBlockInfo> lowerBlock = _placedMultiBlocks.get(lowerKey);

                if (lowerBlock != null) {
                    _placedMultiBlocks.put(lowerKey, info);
                    continue;
                }

                String upperKey = getKey(x, y + 1, z);
                Collection<ChunkBlockInfo> upperBlock = _placedMultiBlocks.get(upperKey);
                if (upperBlock != null) {
                    _placedMultiBlocks.put(upperKey, info);
                    continue;
                }

                _placedMultiBlocks.put(getKey(x, y, z), info);
            }

            // Restore pairs
            Set<String> keys = _placedMultiBlocks.keySet();

            for (String key : keys) {

                Collection<ChunkBlockInfo> multiBlockSetSet = _placedMultiBlocks.get(key);
                if (multiBlockSetSet == null)
                    continue;

                List<ChunkBlockInfo> multiBlockSet = new ArrayList<>(multiBlockSetSet);

                Collections.sort(multiBlockSet);

                for (ChunkBlockInfo info : multiBlockSet) {
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

            int x = info.getX();
            int y = info.getY();
            int z = info.getZ();

            Block block = chunk.getBlock(x, y, z);
            BlockState state = block.getState();

            state.setType(info.getMaterial());
            state.setRawData((byte) info.getData());

            state.update(true);
        }
    }

    /*
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

    }

    /*
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
    }
}


