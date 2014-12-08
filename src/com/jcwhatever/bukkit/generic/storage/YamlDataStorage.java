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

import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.bukkit.generic.items.serializer.ItemStackSerializer.SerializerOutputType;
import com.jcwhatever.bukkit.generic.scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.utils.BatchTracker;
import com.jcwhatever.bukkit.generic.utils.EnumUtils;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class YamlDataStorage implements IDataNode {

    private final BatchTracker _batch = new BatchTracker();
    private final Object _sync = new Object();
    private final Plugin _plugin;

    private LinkedList<StorageSaveHandler> _saveHandlers = new LinkedList<StorageSaveHandler>();
    private YamlConfiguration _yaml = new YamlConfiguration();
    private File _file;
    private String _yamlString;
    private Map<String, Boolean> _booleans;
    private Map<String, Long> _numbers;
    private Map<String, Double> _doubles;
    private Map<String, String> _strings;
    private Map<String, ItemStack[]> _items;
    private boolean _isLoaded;


    public YamlDataStorage(Plugin plugin, DataPath storagePath) {
        PreCon.notNull(plugin);
        PreCon.notNull(storagePath);

        _file = convertStoragePathToFile(plugin, storagePath);

        if (!_file.exists()) {
            try {
                if (!_file.createNewFile()) {
                    throw new RuntimeException("Failed to crate initial Yaml file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        _plugin = plugin;
        _booleans = new HashMap<String, Boolean>(50);
        _numbers = new HashMap<String, Long>(100);
        _doubles = new HashMap<String, Double>(100);
        _strings = new HashMap<String, String>(100);
        _items = new HashMap<String, ItemStack[]>(50);
        _yaml.options().indent(2);
    }

    public YamlDataStorage(Plugin plugin, File configFile) {

        _file = configFile;
        _plugin = plugin;
        _booleans = new HashMap<String, Boolean>(50);
        _numbers = new HashMap<String, Long>(100);
        _doubles = new HashMap<String, Double>(100);
        _strings = new HashMap<String, String>(100);
        _items = new HashMap<String, ItemStack[]>(50);
        _yaml.options().indent(2);
    }

    public YamlDataStorage(Plugin plugin, String yamlString) {

        _yamlString = yamlString;
        _plugin = plugin;
        _booleans = new HashMap<String, Boolean>(50);
        _numbers = new HashMap<String, Long>(100);
        _doubles = new HashMap<String, Double>(100);
        _strings = new HashMap<String, String>(100);
        _items = new HashMap<String, ItemStack[]>(50);
        _yaml.options().indent(2);
    }

    public static File convertStoragePathToFile(Plugin plugin, DataPath storagePath) {
        String[] pathComp = storagePath.getPath();

        if (pathComp.length == 0)
            throw new IllegalArgumentException("Storage path cannot be empty.");

        File directory = plugin.getDataFolder();

        for (int i = 0; i < pathComp.length - 1; i++) {
            directory = new File(directory, pathComp[i]);
        }

        if (!directory.exists() && !directory.mkdirs())
            throw new RuntimeException("Failed to create folders corresponding to supplied data path.");

        return new File(directory, pathComp[pathComp.length - 1] + ".yml");
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public boolean isLoaded() {

        return _isLoaded;
    }

    @Override
    public boolean load() {

        synchronized (_sync) {
            try {

                if (_file != null) {
                    if (!_file.exists() && !_file.createNewFile()) {
                        return false;
                    }
                    _yaml.load(_file);
                }
                else if (_yamlString != null) {
                    _yaml.loadFromString(_yamlString);
                }
                else {
                    return false;
                }
                reloadMaps();
            } catch (Exception e) {

                if (_file != null)
                    Msg.severe("The config-file '{0}' failed to load.", _file.getName());

                e.printStackTrace();
                _isLoaded = false;
                return false;
            }
            _isLoaded = true;
            return true;
        }
    }

    @Override
    public void loadAsync() {

        loadAsync(null);
    }

    @Override
    public void loadAsync(@Nullable final StorageLoadHandler loadHandler) {

        if (loadHandler != null && loadHandler._dataNode == null)
            loadHandler._dataNode = this;

        Bukkit.getScheduler().runTaskAsynchronously(_plugin, new Runnable() {

            @Override
            public void run() {

                synchronized (_sync) {

                    boolean loaded = load();

                    if (loadHandler != null) {

                        StorageLoadResult result = new StorageLoadResult(loaded, loadHandler);

                        loadHandler.onFinish(result);
                    }
                }
            }
        });
    }

    public void reloadMaps() {

        synchronized (_sync) {

            Set<String> nodes = _yaml.getKeys(true);

            for (String key : nodes) {
                Object value = _yaml.get(key);

                if (value instanceof Boolean) {
                    _booleans.put(key, (Boolean) value);
                    continue;
                }

                if (value instanceof Integer) {
                    _numbers.put(key, ((Integer) value).longValue());
                    continue;
                }

                if (value instanceof Long) {
                    _numbers.put(key, (Long) value);
                    continue;
                }

                if (value instanceof Double) {
                    _doubles.put(key, (Double) value);
                    continue;
                }

                if (!(value instanceof String))
                    continue;

                String str = (String) value;

                if (str.indexOf("%ItemStack[]% ") == 0) {
                    str = str.substring(14);
                    try {
                        _items.put(key, ItemStackUtils.parse(str));
                    } catch (InvalidItemStackStringException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                _strings.put(key, (String) value);
            }
        }
    }

    @Override
    public boolean save() {

        synchronized (_sync) {

            if (_batch.isRunning())
                return false;

            boolean isSaved;
            try {

                if (_file != null) {
                    _yaml.save(_file);
                }
                else if (_yamlString != null) {
                    _yamlString = _yaml.saveToString();
                }
                else {
                    return false;
                }

                isSaved = true;

            } catch (Exception e) {
                e.printStackTrace();
                isSaved = false;
            }

            if (_saveHandlers.isEmpty())
                return isSaved;

            final boolean saveResult = isSaved;

            // return results on main thread
            Scheduler.runTaskSync(_plugin, new Runnable() {

                @Override
                public void run() {

                    while (!_saveHandlers.isEmpty()) {
                        StorageSaveHandler saveHandler = _saveHandlers.removeFirst();
                        saveHandler.onFinish(new StorageSaveResult(saveResult, saveHandler));
                    }

                }

            });

            return isSaved;
        }
    }

    private ScheduledTask _saveTask;

    @Override
    public void saveAsync(@Nullable final StorageSaveHandler saveHandler) {

        // set data node on save handler
        if (saveHandler != null && saveHandler._dataNode == null)
            saveHandler._dataNode = this;

        // check that 1 or more batch operations are not in progress.
        if (_batch.isRunning()) {

            // put away the save handler for later
            if (saveHandler != null)
                _saveHandlers.add(saveHandler);

            return;
        }

        // check if save operation already scheduled.
        if (_saveTask != null) {
            return;
        }

        if (_plugin.isEnabled()) {

            _saveTask = Scheduler.runTaskLater(_plugin, 5, new Runnable() {
                @Override
                public void run() {

                    _saveTask = null;

                    // save data node on alternate thread
                    Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {

                        @Override
                        public void run() {

                            final boolean isSaved = save();

                            if (saveHandler != null) {
                                // return results on main thread
                                Scheduler.runTaskSync(_plugin, new Runnable() {

                                    @Override
                                    public void run() {

                                        saveHandler.onFinish(new StorageSaveResult(isSaved, saveHandler));
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        else {
            boolean isSaved = save();
            if (saveHandler != null) {
                saveHandler.onFinish(new StorageSaveResult(isSaved, saveHandler));
            }
        }
    }

    @Override
    public boolean save(File destination) {

        synchronized (_sync) {
            try {
                getYamlConfiguration().save(destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public void saveAsync(final File destination, @Nullable final StorageSaveHandler saveHandler) {

        // set data node on save handler
        if (saveHandler != null && saveHandler._dataNode == null)
            saveHandler._dataNode = this;

        // save on alternate thread
        Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {

            @Override
            public void run() {

                final boolean isSaved = save(destination);

                if (saveHandler != null) {

                    // return results on main thread
                    Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable() {

                        @Override
                        public void run() {

                            saveHandler.onFinish(new StorageSaveResult(isSaved, saveHandler));
                        }

                    });

                }

            }

        });
    }

    @Override
    public void runBatchOperation(BatchOperation batch) {

        runBatchOperation(batch, this);
    }

    void runBatchOperation(final BatchOperation batch, IDataNode dataNode) {

        synchronized (_sync) {
            _batch.start();
            batch.run(dataNode);
            _batch.end();

            if (!_batch.isRunning()) {
                saveAsync(new StorageSaveHandler() {

                    @Override
                    public void onFinish(StorageSaveResult result) {

                        batch.onFinish();
                    }
                });
            }
        }
    }

    @Override
    public void preventSave(BatchOperation batch) {

        preventSave(batch, this);
    }

    void preventSave(BatchOperation batch, IDataNode dataNode) {

        synchronized (_sync) {
            _batch.start();
            batch.run(dataNode);
            _batch.end();
        }
    }

    public String getHeader() {

        synchronized (_sync) {
            return _yaml.options().header();
        }
    }

    public void setHeader(String header) {

        synchronized (_sync) {
            _yaml.options().header(header);
        }
    }

    public YamlConfiguration getYamlConfiguration() {

        synchronized (_sync) {
            return _yaml;
        }
    }

    @Override
    public Object get(String keyPath) {

        synchronized (_sync) {
            return _yaml.get(keyPath);
        }
    }

    @Nullable
    @Override
    public Object get(String keyPath, DataType type) {
        PreCon.notNullOrEmpty(keyPath);
        PreCon.notNull(type);

        if (!hasNode(keyPath))
            return null;

        switch (type) {
            case INTEGER:
                return getInteger(keyPath);
            case LONG:
                return getLong(keyPath);
            case DOUBLE:
                return getDouble(keyPath);
            case BOOLEAN:
                return getBoolean(keyPath);
            case STRING:
                return getString(keyPath);
            case UUID:
                return getUUID(keyPath);
            case LOCATION:
                return getLocation(keyPath);
            case ITEMSTACKS:
                return getItemStacks(keyPath);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public <T extends IDataNodeSerializable> T getSerializable(String nodePath, Class<T> typeClass) {
        PreCon.notNull(nodePath);
        PreCon.notNull(typeClass);

        if (!hasNode(nodePath))
            return null;

        T instance;

        try {
            Constructor<T> constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();

            throw new RuntimeException("Failed to instantiate IDataNodeSerializable object. " +
                    "Make sure it has an empty constructor.");
        }

        IDataNode dataNode = getNode(nodePath);
        try {
            instance.deserializeFromDataNode(dataNode);
        } catch (UnableToDeserializeException ignore) {
            return null;
        }

        return instance;
    }

    @Override
    public boolean getBoolean(String keyPath) {

        return getBoolean(keyPath, false);
    }

    @Override
    public boolean getBoolean(String keyPath, boolean def) {

        synchronized (_sync) {
            Boolean result = _booleans.get(keyPath);
            return result != null
                    ? result
                    : def;
        }
    }

    @Override
    public int getInteger(String keyPath) {

        return getInteger(keyPath, 0);
    }

    @Override
    public int getInteger(String keyPath, int def) {

        synchronized (_sync) {
            Long result = _numbers.get(keyPath);
            return result != null
                    ? result.intValue()
                    : def;
        }
    }

    @Override
    public long getLong(String keyPath) {

        return getLong(keyPath, 0);
    }

    @Override
    public long getLong(String keyPath, long def) {

        synchronized (_sync) {
            Long result = _numbers.get(keyPath);
            return result != null
                    ? result
                    : def;
        }
    }

    @Override
    public double getDouble(String keyPath) {

        return getDouble(keyPath, 0.0);
    }

    @Override
    public double getDouble(String keyPath, double def) {

        synchronized (_sync) {
            Double result = _doubles.get(keyPath);
            return result != null
                    ? result
                    : def;
        }
    }

    @Override
    @Nullable
    public String getString(String keyPath) {

        return getString(keyPath, null);
    }

    @Override
    @Nullable
    public String getString(String keyPath, @Nullable String def) {

        synchronized (_sync) {
            String result = _strings.get(keyPath);
            return result != null
                    ? result
                    : def;
        }
    }

    @Override
    @Nullable
    public UUID getUUID(String keyPath) {

        return getUUID(keyPath, null);
    }

    @Override
    @Nullable
    public UUID getUUID(String keyPath, @Nullable UUID def) {

        synchronized (_sync) {
            String result = _strings.get(keyPath);
            if (result == null)
                return def;
            try {
                return UUID.fromString(result);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return def;
            }
        }
    }

    @Override
    @Nullable
    public Location getLocation(String keyPath) {

        return getLocation(keyPath, null);
    }

    @Override
    @Nullable
    public Location getLocation(String keyPath, @Nullable Location def) {

        synchronized (_sync) {
            String coords = getString(keyPath);
            if (coords != null) {
                return LocationUtils.parseLocation(coords);
            }
            return def;
        }
    }

    @Nullable
    @Override
    public String getLocationWorldName(String keyPath) {
        synchronized (_sync) {
            String coords = getString(keyPath);
            if (coords != null) {
                return LocationUtils.parseLocationWorldName(coords);
            }
            return null;
        }
    }

    @Override
    @Nullable
    public ItemStack[] getItemStacks(String keyPath) {

        return getItemStacks(keyPath, (ItemStack[]) null);
    }

    @Override
    @Nullable
    public ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack def) {

        return getItemStacks(keyPath, new ItemStack[]{
                def
        });
    }

    @Override
    @Nullable
    public ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack[] def) {

        synchronized (_sync) {
            ItemStack[] result = _items.get(keyPath);
            return result != null
                    ? result
                    : def;
        }
    }

    @Override
    @Nullable
    public <T extends Enum<T>> T getEnum(String keyPath, Class<T> enumClass) {

        return getEnum(keyPath, null, enumClass);
    }

    @Override
    @Nullable
    public <T extends Enum<T>> T getEnum(String keyPath, @Nullable T def, Class<T> enumClass) {

        String string = getString(keyPath);
        if (string == null)
            return def;

        return EnumUtils.searchEnum(string, enumClass, def);
    }

    @Override
    @Nullable
    public Enum<?> getEnumGeneric(String keyPath, @Nullable Enum<?> def, Class<? extends Enum<?>> enumClass) {

        String string = getString(keyPath);
        if (string == null)
            return def;

        return EnumUtils.searchGenericEnum(string, enumClass, def);
    }

    @Override
    public Set<String> getSubNodeNames() {

        return getSubNodeNames("");
    }

    @Override
    public Set<String> getSubNodeNames(String nodePath) {
        PreCon.notNull(nodePath);

        synchronized (_sync) {
            ConfigurationSection section;
            if (_yaml.get(nodePath) == null) {
                return new HashSet<>(0);
            }
            return (section = _yaml.getConfigurationSection(nodePath)) != null
                    ? section.getKeys(false)
                    : new HashSet<String>(0);
        }
    }

    @Override
    @Nullable
    public List<String> getStringList(String keyPath, @Nullable List<String> def) {

        synchronized (_sync) {
            if (_yaml.get(keyPath) == null) {
                return def;
            }
            return _yaml.getStringList(keyPath);
        }
    }

    @Override
    public boolean set(String keyPath, @Nullable Object value) {

        synchronized (_sync) {
            if (value instanceof UUID) {
                value = String.valueOf(value);
                _strings.put(keyPath, (String) value);
            }
            else if (value instanceof Boolean) {
                _booleans.put(keyPath, (Boolean) value);
            }
            else if (value instanceof Integer) {
                _numbers.put(keyPath, ((Integer) value).longValue());
            }
            else if (value instanceof Long) {
                _numbers.put(keyPath, (Long) value);
            }
            else if (value instanceof Double) {
                _doubles.put(keyPath, (Double) value);
            }
            else if (value instanceof String) {
                _strings.put(keyPath, (String) value);
            }
            else if (value instanceof Location) {
                value = LocationUtils.locationToString((Location) value, 3);
                _strings.put(keyPath, value.toString());
            }
            else if (value instanceof ItemStack) {
                _items.put(keyPath, new ItemStack[]{
                        ((ItemStack) value).clone()
                });
                value = "%ItemStack[]% " + ItemStackUtils.serializeToString((ItemStack) value, SerializerOutputType.RAW);
            }
            else if (value instanceof ItemStack[]) {
                ItemStack[] stored = ((ItemStack[]) value).clone();
                for (int i = 0; i < stored.length; i++) {
                    if (stored[i] != null) {
                        stored[i] = stored[i].clone();
                    }
                }
                _items.put(keyPath, stored);
                value = "%ItemStack[]% " + ItemStackUtils.serializeToString((ItemStack[]) value, SerializerOutputType.RAW);
            }
            else if (value instanceof Enum<?>) {
                Enum<?> e = (Enum<?>) value;
                value = e.name();
                _strings.put(keyPath, e.name());
            }
            else if (value instanceof IDataNodeSerializable) {

                IDataNodeSerializable serializable = (IDataNodeSerializable)value;

                serializable.serializeToDataNode(getNode(keyPath));

                return true;
            }

            if (value == null) {

                // clear cached values
                _booleans.remove(keyPath);
                _numbers.remove(keyPath);
                _doubles.remove(keyPath);
                _strings.remove(keyPath);
                _items.remove(keyPath);

                ConfigurationSection section = _yaml.getConfigurationSection(keyPath);

                if (section != null) {
                    Set<String> nodes = section.getKeys(true);

                    if (nodes != null) {
                        for (String node : nodes) {
                            String fullPath = keyPath + '.' + node;
                            _booleans.remove(fullPath);
                            _numbers.remove(fullPath);
                            _doubles.remove(fullPath);
                            _strings.remove(fullPath);
                            _items.remove(fullPath);
                        }
                    }
                }
            }

            _yaml.set(keyPath, value);
            return true;
        }
    }

    @Override
    public Map<String, Object> getAllValues() {
        return getAllValues("");
    }

    public Map<String, Object> getAllValues(String nodePath) {
        ConfigurationSection subSection = _yaml.getConfigurationSection(nodePath);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>)
                (subSection == null
                        ? Collections.emptyMap()
                        : subSection.getValues(true));

        return result;
    }

    @Override
    public void remove() {

        Set<String> subNodes = getSubNodeNames();
        if (subNodes != null && !subNodes.isEmpty()) {
            for (String subNode : subNodes) {
                remove(subNode);
            }
        }
    }

    @Override
    public void remove(String nodePath) {

        set(nodePath, null);
    }

    public void clear(String path) {

        set(path, null);
    }

    @Override
    public String getNodeName() {

        return null;
    }

    @Override
    public String getNodePath() {
        return "";
    }

    @Override
    public YamlDataStorage getRoot() {
        return this;
    }

    @Override
    public boolean hasNode(String nodePath) {

        return get(nodePath) != null;
    }

    @Override
    public IDataNode getNode(String nodePath) {
        return new YamlDataNode(this, nodePath);
    }

    @Override
    public void clear() {

        synchronized (_sync) {
            Set<String> keys = getSubNodeNames();
            for (String key : keys) {
                set(key, null);
            }
        }
    }

    public void assertNodes(File defaultConfig) {

        assertNodes(defaultConfig, "");
    }

    void assertNodes(File defaultConfig, final String destNode) {

        synchronized (_sync) {

            final YamlDataStorage config = new YamlDataStorage(_plugin, defaultConfig);
            final YamlDataStorage self = this;

            config.loadAsync(new StorageLoadHandler() {

                @Override
                public void onFinish(StorageLoadResult result) {

                    IDataNode dest = destNode.isEmpty()
                            ? self
                            : self.getNode(destNode);

                    Set<String> keys = config.getSubNodeNames();
                    if (keys == null)
                        return;

                    for (String key : keys) {
                        if (dest.get(key) == null)
                            continue;

                        dest.set(key, config.get(key));
                    }

                }

            });

        }
    }
}
