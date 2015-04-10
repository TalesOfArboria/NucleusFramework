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
import com.jcwhatever.nucleus.regions.file.IRegionFileAccess;
import com.jcwhatever.nucleus.regions.file.IRegionFileFactory;
import com.jcwhatever.nucleus.utils.PreCon;

import java.io.File;
import java.io.IOException;

/**
 * Abstract implementation of {@link IRegionFileAccess}.
 */
class AbstractRegionFileAccess implements IRegionFileAccess {

    private final IRegion _region;
    private final IRegionFileFactory _fileFactory;

    /**
     * Constructor.
     *
     * @param region           The region the instance is for.
     * @param filenameFactory  The factory used to construct a file.
     */
    public AbstractRegionFileAccess(IRegion region, IRegionFileFactory filenameFactory) {

        PreCon.notNull(region, "region");
        PreCon.notNull(filenameFactory, "filenameFactory");

        _region = region;
        _fileFactory = filenameFactory;
    }

    @Override
    public IRegion getRegion() {
        return _region;
    }

    @Override
    public IRegionFileFactory getFileFactory() {
        return _fileFactory;
    }

    /**
     * Get the name of the file used to store data for the specified chunk.
     *
     * @param chunkX   The chunk X coordinates.
     * @param chunkZ   The chunk Z coordinates.
     */
    protected final String getChunkFilename(IRegion region, int chunkX, int chunkZ) {
        String prefix = _fileFactory.getFilename(region);
        return (prefix != null ? prefix : "") + ".chunk." + chunkX + '.' + chunkZ + ".bin";
    }

    /**
     * Get the file used to store data for the specified chunk.
     *
     * @param chunkX            The chunk X coordinates.
     * @param chunkZ            The chunk Z coordinates.
     * @param doDeleteExisting  True to delete existing file.
     *
     * @throws IOException
     */
    protected final File getChunkFile(IRegion region,
                                      int chunkX, int chunkZ, boolean doDeleteExisting)
            throws IOException {

        File file = new File(_fileFactory.getDirectory(region), getChunkFilename(region, chunkX, chunkZ));

        if (!doDeleteExisting)
            return file;

        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete chunk file: " + file.getName());
        }
        return file;
    }
}
