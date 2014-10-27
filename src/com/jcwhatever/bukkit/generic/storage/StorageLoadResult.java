/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.storage;

/**
 * A result passed to the method executed by
 * the {@code StorageLoadHandler} when a data node
 * load is complete.
 */
public class StorageLoadResult {

    private boolean _isLoaded = false;
    private IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param isLoaded     True if the node was successfully loaded.
     * @param loadHandler  The handler.
     */
    StorageLoadResult(boolean isLoaded, StorageLoadHandler loadHandler) {
        _isLoaded = isLoaded;
        _dataNode = loadHandler._dataNode;
    }

    /**
     * Determine if the data node was successfully loaded.
     */
    public boolean isLoaded() {
        return _isLoaded;
    }

    /**
     * Get the data node.
     */
    public IDataNode getDataNode() {
        return _dataNode;
    }

}
