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

package com.jcwhatever.nucleus.storage;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.util.Collection;

/**
 * Specifies a data path relative to a plugins data folder.
 *
 * <p>The data path is typically the relative path of a file with no
 * file extension and slashes replaced with dots.</p>
 *
 * <p>The data provider is responsible for handling file extensions.</p>
 */
public class DataPath {

    private String[] _dataPath;

    /**
     * Constructor.
     *
     * <p>The data path is typically the relative path of a file with no
     * file extension and slashes replaced with dots.</p>
     *
     * <p>ie /plugins/MyPlugin/someDirectory/data.yml = someDirectory.data</p>
     *
     * @param path The data file path.
     */
    public DataPath(String path) {
        PreCon.notNullOrEmpty(path);

        _dataPath = TextUtils.PATTERN_DOT.split(path);
    }

    /**
     * Constructor.
     *
     * <p>The data path is typically the relative path of a file with no
     * file extension and slashes replaced with dots.</p>
     *
     * <p>The pathComponents expected are the path name components split into an array
     * by the dots.</p>
     *
     * <p>ie /plugins/MyPlugin/someDirectory/data.yml = ["someDirectory", "data"]</p>
     *
     * @param pathComponents  The path name components of the data file path.
     */
    public DataPath(String... pathComponents) {
        PreCon.notNull(pathComponents);
        PreCon.isValid(pathComponents.length > 0);

        _dataPath = pathComponents;
    }

    /**
     * Constructor.
     *
     * <p>The data path is typically the relative path of a file with no
     * file extension and slashes replaced with dots.</p>
     *
     * <p>The pathComponents expected are the path name components split into collection elements
     * by the dots.</p>
     *
     * <p>ie /plugins/MyPlugin/someDirectory/data.yml = ["someDirectory", "data"]</p>
     *
     * @param pathComponents  The path name components of the data file path.
     */
    public DataPath(Collection<String> pathComponents) {
        PreCon.notNull(pathComponents);
        PreCon.isValid(pathComponents.size() > 0);

        _dataPath = pathComponents.toArray(new String[pathComponents.size()]);
    }

    /**
     * Get the data file path as an array of path name components.
     */
    public String[] getPath() {
        return _dataPath;
    }

    /**
     * Create a new {@link DataPath} using a path that is relative
     * to the current {@link DataPath}.
     *
     * @param path  The relative path to add to the existing path.
     *
     * @return  A new {@link DataPath}.
     */
    public DataPath getPath(String path) {
        PreCon.notNullOrEmpty(path);

        DataPath newPath = new DataPath();
        String[] relativePath = TextUtils.PATTERN_DOT.split(path);

        int size = _dataPath.length + relativePath.length;

        newPath._dataPath = new String[size];

        System.arraycopy(_dataPath, 0, newPath._dataPath, 0, _dataPath.length);
        System.arraycopy(relativePath, 0, newPath._dataPath, _dataPath.length, relativePath.length);

        return newPath;
    }

    /**
     * Create a new {@link DataPath} using a path that is relative
     * to the current {@link DataPath}.
     *
     * @param pathComponents  The relative path to add to the existing path.
     *
     * @return  A new {@link DataPath}.
     */
    public DataPath getPath(String... pathComponents) {
        PreCon.notNull(pathComponents);
        PreCon.isValid(pathComponents.length > 0);

        DataPath newPath = new DataPath();

        System.arraycopy(_dataPath, 0, newPath._dataPath, 0, _dataPath.length);
        System.arraycopy(pathComponents, 0, newPath._dataPath, _dataPath.length, pathComponents.length);

        return newPath;
    }

    /**
     * Create a new {@link DataPath} using a path that is relative
     * to the current {@link DataPath}.
     *
     * @param pathComponents  The relative path to add to the existing path.
     *
     * @return  A new {@link DataPath}.
     */
    public DataPath getPath(Collection<String> pathComponents) {
        PreCon.notNull(pathComponents);
        PreCon.isValid(pathComponents.size() > 0);

        DataPath newPath = new DataPath();
        String[] relativePath = pathComponents.toArray(new String[pathComponents.size()]);

        int size = _dataPath.length + relativePath.length;

        newPath._dataPath = new String[size];

        System.arraycopy(_dataPath, 0, newPath._dataPath, 0, _dataPath.length);
        System.arraycopy(relativePath, 0, newPath._dataPath, _dataPath.length, relativePath.length);

        return newPath;
    }
}
