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

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.file.IRegionFileFactory;
import com.jcwhatever.nucleus.regions.file.IRegionFileWriter;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import com.jcwhatever.nucleus.utils.performance.queued.QueueProject;
import com.jcwhatever.nucleus.utils.performance.queued.QueueWorker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Basic region format file writer.
 */
public class RegionFileWriter extends AbstractRegionFileAccess implements IRegionFileWriter {

    public static final MetaKey<Boolean> META_IS_SAVING = new MetaKey<Boolean>(Boolean.class);

    /**
     * Constructor.
     *
     * @param region           The region the writer is for.
     * @param filenameFactory  The filename factory used to get the file(s) to store data in.
     */
    public RegionFileWriter(IRegion region, IRegionFileFactory filenameFactory) {
        super(region, filenameFactory);
    }

    @Override
    public IFuture save() throws IOException {

        final IRegion region = getRegion();

        Collection<IChunkCoords> chunks = region.getChunkCoords();

        QueueProject project = new QueueProject(region.getPlugin());

        if (chunks.size() == 0)
            return project.cancel("Cannot save region because there are no chunks to save.");

        Boolean isSaving = region.getMeta().get(META_IS_SAVING);

        if (isSaving != null && isSaving)
            return project.cancel("Cannot save region while it is already saving.");

        region.getMeta().set(META_IS_SAVING, true);

        for (IChunkCoords chunk : chunks) {
            RegionChunkFileWriter writer = new RegionChunkFileWriter(region, chunk);
            writer.saveData(getChunkFile(region, chunk.getX(), chunk.getZ(), true), project);
        }

        QueueWorker.get().addTask(project);

        return project.getResult().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable String message) {
                region.getMeta().set(META_IS_SAVING, null);
            }
        });
    }

    @Override
    public boolean deleteData() throws IOException {

        IRegion region = getRegion();

        Collection<IChunkCoords> chunks = region.getChunkCoords();

        int failed = 0;

        for (IChunkCoords chunk : chunks) {
            File file;

            try {
                file = getChunkFile(region, chunk.getX(), chunk.getZ(), false);
            } catch (IOException e) {
                failed++;
                continue;
            }

            if (file == null || !file.exists()) {
                failed++;
                continue;
            }

            if (!file.delete()) {
                failed++;
                NucMsg.debug(getRegion().getPlugin(), "Failed to delete region file: {0}", file);
            }
        }
        return failed == 0;
    }
}
