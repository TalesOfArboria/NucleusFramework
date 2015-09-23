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

import com.jcwhatever.nucleus.regions.file.IRegionFileFactory;
import com.jcwhatever.nucleus.regions.file.IRegionFileLoader.LoadSpeed;
import com.jcwhatever.nucleus.regions.file.basic.BasicFileFactory;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of a restorable region
 * with multiple named saved snapshots.
 *
 * <p>The default snapshot name is "default".</p>
 */
public abstract class MultiSnapshotRegion extends RestorableRegion {

    private final SnapshotFileFactory _fileFactory = new SnapshotFileFactory();

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the region.
     */
    public MultiSnapshotRegion(Plugin plugin, String name) {
        super(plugin, name);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the region.
     * @param dataNode  The regions data node.
     */
    public MultiSnapshotRegion(Plugin plugin, String name, IDataNode dataNode) {
        super(plugin, name, dataNode);
    }

    @Override
    public SnapshotFileFactory getFileFactory() {

        if (_fileFactory.fileFactory == null)
            _fileFactory.fileFactory = new BasicFileFactory();

        return _fileFactory;
    }

    @Override
    public void setFileFactory(IRegionFileFactory fileFactory) {
        PreCon.notNull(fileFactory);

        _fileFactory.fileFactory = fileFactory;
    }

    /**
     * Get the name of the current snapshot.
     *
     * <p>The default snapshot name is "default".</p>
     */
    public String getCurrentSnapshot() {
        return getFileFactory().snapshotName;
    }

    /**
     * Set the current snapshot name.
     *
     * @param snapshotName  The snapshot name.
     */
    public void setCurrentSnapshot(String snapshotName) {
        PreCon.notNullOrEmpty(snapshotName);

        getFileFactory().snapshotName = snapshotName;
    }

    /**
     * Get the names of stored snapshots.
     *
     * <p>Snapshot names are retrieved by file name, therefore this
     * only returns names of snapshots that have been saved.</p>
     *
     * @throws IOException
     */
    public Set<String> getSnapshotNames() throws IOException {
        return getSnapshotNames(new HashSet<String>(15));
    }

    /**
     * Get the names of stored snapshots.
     *
     * <p>Snapshot names are retrieved by file name, therefore this
     * only returns names of snapshots that have been saved.</p>
     *
     * @throws IOException
     */
    public <T extends Collection<String>> T getSnapshotNames(T output) throws IOException {

        File folder = _fileFactory.getRegionDirectory(this);

        File[] files = folder.listFiles();
        if (files == null)
            return output;

        for (File file : files) {

            if (!file.isDirectory())
                continue;

            output.add(file.getName());
        }

        return output;
    }

    /**
     * Determine if the specified snapshot can be restored.
     *
     * @param snapshotName  The name of the snapshot.
     */
    public boolean canRestore(String snapshotName) {

        String current = getFileFactory().snapshotName;

        getFileFactory().snapshotName = snapshotName;

        boolean canRestore = getFileFormat().getLoader(this, _fileFactory).canRead();

        getFileFactory().snapshotName = current;

        return canRestore;
    }

    /**
     * Restore the specified snapshot.
     *
     * @param loadSpeed     The speed of the restore.
     * @param snapshotName  The name of the snapshot.
     *
     * @throws IOException
     */
    public IFuture restoreData(LoadSpeed loadSpeed, final String snapshotName) throws IOException {

        final String currentSnapshot = getFileFactory().snapshotName;

        getFileFactory().snapshotName = snapshotName;

        return super.restoreData(loadSpeed)
                .onSuccess(new FutureSubscriber() {
                    @Override
                    public void on(FutureStatus status, @Nullable CharSequence message) {
                        getFileFactory().snapshotName = currentSnapshot;
                    }
                });
    }

    /**
     * Save the regions current state to the specified snapshot.
     *
     * @param snapshotName  The name of the snapshot.
     *
     * @throws IOException
     */
    public IFuture saveData(String snapshotName) throws IOException {

        final String currentSnapshot = getFileFactory().snapshotName;

        getFileFactory().snapshotName = snapshotName;

        return super.saveData().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable CharSequence message) {
                getFileFactory().snapshotName = currentSnapshot;
            }
        });
    }

    /**
     * Delete the specified snapshots data.
     *
     * @param snapshotName  The name of the snapshot.
     *
     * @throws IOException
     */
    public void deleteData(String snapshotName) throws IOException {
        PreCon.notNull(snapshotName);

        final String currentSnapshot = getFileFactory().snapshotName;

        getFileFactory().snapshotName = snapshotName;

        super.deleteData();

        getFileFactory().snapshotName = currentSnapshot;
    }

    public static class SnapshotFileFactory implements IRegionFileFactory {

        String snapshotName = "default";
        IRegionFileFactory fileFactory;

        private SnapshotFileFactory() {}

        @Override
        public String getFilename(IRegion region) {
            return fileFactory.getFilename(region);
        }

        @Override
        public File getDirectory(IRegion region) throws IOException {

            File regionFolder = getRegionDirectory(region);

            File snapshotFolder = new File(regionFolder, snapshotName);
            if (!snapshotFolder.exists() && !snapshotFolder.mkdirs()) {
                throw new IOException("Failed to create snapshot folder.");
            }

            return snapshotFolder;
        }

        public File getRegionDirectory(IRegion region) throws IOException {
            return fileFactory.getDirectory(region);
        }

        public String getSnapshotName() {
            return snapshotName;
        }

        public IRegionFileFactory getInnerFactory() {
            return fileFactory;
        }
    }
}
