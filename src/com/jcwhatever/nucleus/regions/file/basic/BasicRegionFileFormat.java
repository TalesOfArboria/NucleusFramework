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
import com.jcwhatever.nucleus.regions.file.IRegionFileFactory;
import com.jcwhatever.nucleus.regions.file.IRegionFileFormat;
import com.jcwhatever.nucleus.regions.file.IRegionFileLoader;
import com.jcwhatever.nucleus.regions.file.IRegionFileWriter;

/**
 * Basic region file format that is used by default.
 *
 * <p>Stores data in chunk files in a raw binary format.</p>
 */
public class BasicRegionFileFormat implements IRegionFileFormat {

    @Override
    public IRegionFileLoader getLoader(IRegion region, IRegionFileFactory filenameFactory) {
        return new RegionFileLoader(region, filenameFactory);
    }

    @Override
    public IRegionFileWriter getWriter(IRegion region, IRegionFileFactory filenameFactory) {
        return new RegionFileWriter(region, filenameFactory);
    }
}
