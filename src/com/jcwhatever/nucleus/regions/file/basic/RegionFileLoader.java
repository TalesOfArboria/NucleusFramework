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

import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.file.IRegionFileData;
import com.jcwhatever.nucleus.regions.file.IRegionFileFactory;
import com.jcwhatever.nucleus.regions.file.IRegionFileLoader;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
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
 * Basic region format file loader.
 */
public class RegionFileLoader extends AbstractRegionFileAccess implements IRegionFileLoader {

    public static final MetaKey<Boolean> META_IS_READING= new MetaKey<Boolean>(Boolean.class);

    /**
     * Constructor.
     *
     * @param region           The region the loader is for.
     * @param filenameFactory  The filename factory used to get the filename and path of the regions files.
     */
    public RegionFileLoader(IRegion region, IRegionFileFactory filenameFactory) {
        super(region, filenameFactory);
    }

    @Override
    public boolean canRead() {

        IRegion region = getRegion();

        Collection<IChunkCoords> chunks = region.getChunkCoords();

        for (IChunkCoords chunk : chunks) {
            File file;

            try {
                file = getChunkFile(region, chunk.getX(), chunk.getZ(), false);
            } catch (IOException e) {
                return false;
            }

            if (file == null || !file.exists())
                return false;
        }
        return true;
    }

    @Override
    public IFuture load(LoadType loadType, final LoadSpeed speed, IRegionFileData data) throws IOException {
        PreCon.notNull(loadType);
        PreCon.notNull(data);

        final IRegion region = getRegion();

        Collection<IChunkCoords> chunks = region.getChunkCoords();
        final QueueProject restoreProject = new QueueProject(region.getPlugin());

        if (chunks.size() == 0)
            return restoreProject.cancel("No chunks to read.");

        Boolean isReading = region.getMeta().get(META_IS_READING);

        if (isReading != null && isReading)
            return restoreProject.cancel("Region is still being read.");

        if (!canRead())
            return restoreProject.cancel("Region files not found.");

        region.getMeta().set(META_IS_READING, true);

        for (IChunkCoords chunk : chunks) {

            QueueProject chunkProject = new QueueProject(region.getPlugin());
            RegionChunkFileLoader loader = new RegionChunkFileLoader(region, chunk);

            // add load task to chunk project
            loader.loadInProject(
                    getChunkFile(region, chunk.getX(), chunk.getZ(), false),
                    chunkProject, loadType, data);

            restoreProject.addTask(chunkProject);
        }

        final FutureAgent agent = new FutureAgent();

        runProject(restoreProject, speed);

        restoreProject.getResult().onStatus(new FutureSubscriber() {
            @Override
            public void on(FutureStatus status, @Nullable String message) {

                switch (status) {
                    case ERROR:
                        agent.error(message);
                        break;
                    case CANCEL:
                        agent.cancel(message);
                        break;
                    case SUCCESS:
                        if (restoreProject.getTasks().isEmpty()) {
                            agent.success(message);
                        } else {
                            runProject(restoreProject, speed);
                        }
                        break;
                }
            }
        });

        return agent.getFuture();
    }

    private void runProject(QueueProject project, LoadSpeed speed) {
        switch (speed) {
            case PERFORMANCE:
                QueueWorker.get().addTask(project);
                break;
            case BALANCED:
                project.run();
                break;
            case FAST:
                project.runFast();
                break;
        }
    }
}
