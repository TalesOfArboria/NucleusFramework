/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * A child Yaml data node of a Yaml data storage.
 */
public class YamlDataNode implements IDataNode {

    private static Pattern PATTERN_PATH_SPLITTER = Pattern.compile("\\.");
    private YamlDataStorage _storage;
    private String _rawPath;
    private String _path;
    private String _nodeName;

    /**
     * Constructor.
     *
     * @param storage  The parent data storage.
     * @param path     The data node path.
     */
    YamlDataNode(YamlDataStorage storage, String path) {

        _storage = storage;

        if (path.endsWith(".")) {
            _rawPath = path.substring(0, path.length() - 1);
            _path = path;
        } else {
            _rawPath = path;
            _path = path + '.';
        }

        String[] pathcomp = PATTERN_PATH_SPLITTER.split(_rawPath);
        _nodeName = pathcomp.length > 0
                ? pathcomp[pathcomp.length - 1]
                : "";
    }

    @Override
    public Plugin getPlugin() {
        return _storage.getPlugin();
    }

    @Override
    public boolean isLoaded() {

        return _storage.isLoaded();
    }

    @Override
    public String getNodeName() {

        return _nodeName;
    }

    @Override
    public String getNodePath() {
        return _rawPath;
    }

    @Override
    public YamlDataStorage getRoot() {

        return _storage;
    }

    @Override
    public boolean load() {

        return _storage.load();
    }

    @Override
    public void loadAsync() {

        _storage.loadAsync(null);
    }

    @Override
    public void loadAsync(StorageLoadHandler loadHandler) {

        if (loadHandler != null)
            loadHandler._dataNode = this;

        _storage.loadAsync(loadHandler);
    }

    @Override
    public boolean save() {

        return _storage.save();
    }

    @Override
    public void saveAsync(StorageSaveHandler saveHandler) {

        if (saveHandler != null)
            saveHandler._dataNode = this;

        _storage.saveAsync(saveHandler);
    }

    @Override
    public boolean save(File destination) {

        return _storage.save(destination);
    }

    @Override
    public void saveAsync(File destination, StorageSaveHandler saveHandler) {

        if (saveHandler != null)
            saveHandler._dataNode = this;

        _storage.saveAsync(destination, saveHandler);
    }

    @Override
    public boolean hasNode(String nodePath) {

        return _storage.hasNode(_path + nodePath);
    }

    @Override
    public IDataNode getNode(String nodePath) {

        return new YamlDataNode(_storage, _path + nodePath);
    }

    @Override
    public Set<String> getSubNodeNames() {

        return _storage.getSubNodeNames(_path);
    }

    @Override
    public Set<String> getSubNodeNames(String nodePath) {

        return _storage.getSubNodeNames(_path + nodePath);
    }

    @Override
    public void clear() {

        _storage.clear(_rawPath);
    }

    @Override
    public void remove() {

        if (_rawPath.isEmpty())
            _storage.remove();

        _storage.remove(_rawPath);
    }

    @Override
    public void remove(String nodePath) {

        _storage.remove(_path + nodePath);
    }

    @Override
    public boolean set(String keyPath, Object value) {

        return _storage.set(_path + keyPath, value);
    }

    @Override
    public Object get(String keyPath) {

        return _storage.get(_path + keyPath);
    }

    @Nullable
    @Override
    public Object get(String keyPath, DataType type) {
        return _storage.get(_path + keyPath, type);
    }

    @Override
    public int getInteger(String keyPath) {

        return _storage.getInteger(_path + keyPath);
    }

    @Override
    public int getInteger(String keyPath, int def) {

        return _storage.getInteger(_path + keyPath, def);
    }

    @Override
    public long getLong(String keyPath) {

        return _storage.getLong(_path + keyPath);
    }

    @Override
    public long getLong(String keyPath, long def) {

        return _storage.getLong(_path + keyPath, def);
    }

    @Override
    public double getDouble(String keyPath) {

        return _storage.getDouble(_path + keyPath);
    }

    @Override
    public double getDouble(String keyPath, double def) {

        return _storage.getDouble(_path + keyPath, def);
    }

    @Override
    public boolean getBoolean(String keyPath) {

        return _storage.getBoolean(_path + keyPath);
    }

    @Override
    public boolean getBoolean(String keyPath, boolean def) {

        return _storage.getBoolean(_path + keyPath, def);
    }

    @Override
    public String getString(String keyPath) {

        return _storage.getString(_path + keyPath);
    }

    @Override
    public String getString(String keyPath, String def) {

        return _storage.getString(_path + keyPath, def);
    }

    @Override
    public UUID getUUID(String keyPath) {

        return _storage.getUUID(_path + keyPath, null);
    }

    @Override
    public UUID getUUID(String keyPath, UUID def) {

        return _storage.getUUID(_path + keyPath, def);
    }

    @Override
    public Location getLocation(String keyPath) {

        return _storage.getLocation(_path + keyPath);
    }

    @Override
    public Location getLocation(String keyPath, Location def) {

        return _storage.getLocation(_path + keyPath, def);
    }

    @Nullable
    @Override
    public String getLocationWorldName(String keyPath) {
        return _storage.getLocationWorldName(_path + keyPath);
    }

    @Override
    public ItemStack[] getItemStacks(String keyPath) {

        return _storage.getItemStacks(_path + keyPath);
    }

    @Override
    public ItemStack[] getItemStacks(String keyPath, ItemStack def) {

        return _storage.getItemStacks(_path + keyPath, def);
    }

    @Override
    public ItemStack[] getItemStacks(String keyPath, ItemStack[] def) {

        return _storage.getItemStacks(_path + keyPath, def);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String keyPath, Class<T> enumClass) {

        return _storage.getEnum(_path + keyPath, null, enumClass);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String keyPath, T def, Class<T> enumClass) {

        return _storage.getEnum(_path + keyPath, def, enumClass);
    }

    @Override
    public Enum<?> getEnumGeneric(String keyPath, Enum<?> def, Class<? extends Enum<?>> enumClass) {

        return _storage.getEnumGeneric(_path + keyPath, def, enumClass);
    }

    @Override
    public List<String> getStringList(String keyPath, List<String> def) {

        return _storage.getStringList(_path + keyPath, def);
    }

    @Override
    public void runBatchOperation(BatchOperation batch) {

        _storage.runBatchOperation(batch, this);
    }

    @Override
    public void preventSave(BatchOperation batch) {

        _storage.preventSave(batch, this);
    }

    public void assertNodes(File defaultConfig) {

        _storage.assertNodes(defaultConfig, _path);
    }

}
