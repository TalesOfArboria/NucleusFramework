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

import com.jcwhatever.nucleus.regions.file.IRegionFileData;
import com.jcwhatever.nucleus.regions.file.IRegionFileFactory;
import com.jcwhatever.nucleus.regions.file.IRegionFileFormat;
import com.jcwhatever.nucleus.regions.file.IRegionFileLoader.LoadSpeed;
import com.jcwhatever.nucleus.regions.file.IRegionFileLoader.LoadType;
import com.jcwhatever.nucleus.regions.file.IRegionFileWriter;
import com.jcwhatever.nucleus.regions.file.basic.BasicFileFactory;
import com.jcwhatever.nucleus.regions.file.basic.BasicRegionFileFormat;
import com.jcwhatever.nucleus.regions.file.basic.WorldBuilder;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;
import com.jcwhatever.nucleus.utils.file.SerializableFurnitureEntity;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;

/**
 * Abstract implementation for a region that is savable to and restorable from disk.
 */
public abstract class RestorableRegion extends BuildableRegion {

    private boolean _isRestoring = false;
    private boolean _isSaving = false;

    private IRegionFileFormat _fileFormat;
    private IRegionFileFactory _fileFactory;

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
     * Get the region file format.
     */
    public IRegionFileFormat getFileFormat() {

        if (_fileFormat == null)
            _fileFormat = new BasicRegionFileFormat();

        return _fileFormat;
    }

    /**
     * Get the regions file factory.
     */
    public IRegionFileFactory getFileFactory() {

        if (_fileFactory == null)
            _fileFactory = new BasicFileFactory();

        return _fileFactory;
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

        Collection<IChunkCoords> chunks = this.getChunkCoords();

        if (chunks.size() == 0)
            return new FutureAgent()
                    .cancel("Cannot save region because there are no chunks to save.");

        if (isSaving() || isRestoring())
            return new FutureAgent()
                    .cancel("Cannot save region while it is already saving or restoring.");

        _isSaving = true;
        onPreSave();

        IRegionFileWriter writer = getFileFormat().getWriter(this, getFileFactory());
        return writer.save().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable CharSequence message) {
                _isSaving = false;
                onSaveComplete();
            }
        });
    }

    /**
     * Determine if region data files exist and can be restored.
     */
    public boolean canRestore() {
        return getFileFormat().getLoader(this, getFileFactory()).canRead();
    }

    /**
     * Restore region from disk.
     *
     * @param loadSpeed  The speed that the region is loaded and restored.
     *
     * @return  A future to receive the results of the restore operation.
     *
     * @throws IOException
     */
    public IFuture restoreData(LoadSpeed loadSpeed) throws IOException {

        Collection<IChunkCoords> chunks = getChunkCoords();

        if (chunks.size() == 0)
            return new FutureAgent().cancel("No chunks to restore.");

        if (isSaving())
            return new FutureAgent().cancel("Region is still saving.");

        if (isRestoring())
            return new FutureAgent().cancel("Region is still restoring.");

        if (!canRestore())
            return new FutureAgent().cancel("Region cannot restore without restore files.");

        _isRestoring = true;
        onPreRestore();

        removeEntities(Item.class, Monster.class, Animals.class);
        removeEntities(SerializableFurnitureEntity.getFurnitureClasses());

        IRegionFileData fileData = new WorldBuilder(getPlugin(), getWorld());

        return getFileFormat()
                .getLoader(this, getFileFactory())
                .load(LoadType.MISMATCHED, loadSpeed, fileData)
                .onStatus(new FutureSubscriber() {
                    @Override
                    public void on(FutureStatus status, @Nullable CharSequence message) {
                        _isRestoring = false;
                        onRestoreComplete();
                    }
                });
    }

    /**
     * Delete region data from disk.
     */
    public boolean deleteData() throws IOException {
        return getFileFormat().getWriter(this, getFileFactory()).deleteData();
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
     * Set the regions file format.
     *
     * @param fileFormat  The file format.
     */
    protected void setFileFormat(IRegionFileFormat fileFormat) {
        PreCon.notNull(fileFormat);

        _fileFormat = fileFormat;
    }

    /**
     * Set the regions file factory.
     *
     * @param fileFactory  The file factory.
     */
    protected void setFileFactory(IRegionFileFactory fileFactory) {
        PreCon.notNull(fileFactory);

        _fileFactory = fileFactory;
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
    protected void onRestoreComplete() {}

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
}


