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

package com.jcwhatever.nucleus.regions.file;

import com.jcwhatever.nucleus.utils.observer.future.IFuture;

import java.io.IOException;

/**
 * Interface for a region data file loader.
 */
public interface IRegionFileLoader extends IRegionFileAccess {

    /**
     * Specifies what blocks are loaded from the file.
     */
    enum LoadType {
        /**
         * Loads all blocks info from the file
         */
        ALL_BLOCKS,
        /**
         * Loads blocks that do no match the current chunk.
         */
        MISMATCHED
    }

    /**
     * Specifies the loading speed of the file or files.
     */
    enum LoadSpeed {
        /**
         * Attempts to load slowly to prevent/reduce server lag.
         */
        PERFORMANCE,
        /**
         * Loads faster than performance but still slower to reduce lag.
         */
        BALANCED,
        /**
         * Loads as fast as possible.
         */
        FAST
    }

    /**
     * Determine if the regions files exist and can be read.
     */
    boolean canRead();

    /**
     * Load the regions data from files.
     *
     * @param loadType   The load type.
     * @param loadSpeed  The load speed.
     * @param data       The {@link IRegionFileData} to put the loaded data into.
     *
     * @return  A future that returns the overall result status of the load operation.
     *
     * @throws IOException
     */
    IFuture load(LoadType loadType, LoadSpeed loadSpeed, IRegionFileData data) throws IOException;
}
