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

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.scheduler.ScheduledTask;
import com.jcwhatever.nucleus.utils.BatchTracker;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.items.serializer.ItemStackSerializer.SerializerOutputType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class YamlDataNode extends AbstractDataNode {

    public static File dataPathToFile(Plugin plugin, DataPath storagePath) {
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

    private final Plugin _plugin;
    private final YamlDataNode _root;
    private final BatchTracker _batch = new BatchTracker();

    private LinkedList<StorageSaveHandler> _saveHandlers = new LinkedList<StorageSaveHandler>();
    private volatile ScheduledTask _saveTask;
    private boolean _isLoaded;

    protected File _file;
    protected String _yamlString;

    // instantiated on root only
    private final ConfigurationSection _section;


    /**
     * Constructor.
     *
     * @param plugin       The owning plugin.
     * @param storagePath  The storage path.
     */
    public YamlDataNode(Plugin plugin, DataPath storagePath) {
        this(plugin);

        PreCon.notNull(storagePath);

        _file = dataPathToFile(plugin, storagePath);

        if (!_file.exists()) {
            try {
                if (!_file.createNewFile()) {
                    throw new RuntimeException("Failed to crate initial Yaml file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param file    The yaml file.
     */
    public YamlDataNode(Plugin plugin, File file) {
        this(plugin);

        _file = file;
    }

    /**
     * Constructor.
     *
     * @param plugin      The owning plugin.
     * @param yamlString  The yaml string.
     */
    public YamlDataNode(Plugin plugin, String yamlString) {
        this(plugin);

        _yamlString = yamlString;

        try {
            ((YamlConfiguration)_section).loadFromString(yamlString);
            _isLoaded = true;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private Constructor. Used by public constructors
     * for common code.
     *
     * @param plugin  The owning plugin.
     */
    private YamlDataNode(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.options().indent(2);
        _section = yaml;
        _root = this;
    }

    /**
     * Private Constructor.  Used for creating child nodes.
     *
     * @param root     The root node.
     * @param path     The full node path.
     */
    private YamlDataNode(YamlDataNode root, String path) {
        super(root, path);
        _root = root;
        _section = null;
        _plugin = root.getPlugin();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public YamlDataNode getRoot() {
        return _root;
    }

    @Override
    public boolean isRoot() {
        return _root == this;
    }

    @Override
    public boolean isLoaded() {
        return _isLoaded;
    }

    @Override
    public boolean load() {

        if (_file == null && _isLoaded)
            return true;

        if (_file == null)
            return false;

        if (!_file.exists())
            return false;

        YamlConfiguration yaml = (YamlConfiguration)getRoot()._section;

        getRoot()._write.lock();
        try {

            yaml.load(_file);
            return _isLoaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            getRoot()._write.unlock();
        }


        if (_file != null)
            NucMsg.severe("The config-file '{0}' failed to load.", _file.getName());

        return _isLoaded = false;
    }

    @Override
    public void loadAsync() {
        loadAsync(null);
    }

    @Override
    public void loadAsync(@Nullable final StorageLoadHandler loadHandler) {

        if (loadHandler != null && loadHandler._dataNode == null)
            loadHandler._dataNode = this;

        Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {

            @Override
            public void run() {

                getRoot()._write.lock();
                try {

                    boolean loaded = load();

                    if (loadHandler != null) {

                        StorageLoadResult result = new StorageLoadResult(loaded, loadHandler);

                        loadHandler.onFinish(result);
                    }
                }
                finally {
                    getRoot()._write.unlock();
                }
            }
        });
    }

    @Override
    public boolean save() {

        YamlConfiguration yaml = (YamlConfiguration)getRoot()._section;

        if (_batch.isRunning())
            return false;

        boolean isSaved;

        getRoot()._write.lock();
        try {

            try {

                if (_file != null) {
                    yaml.save(_file);
                }
                else {
                    _yamlString = yaml.getKeys(false).size() == 0 ? "" : yaml.saveToString();
                }

                isSaved = true;

            } catch (Exception e) {
                e.printStackTrace();
                isSaved = false;
            }

            if (_saveHandlers.isEmpty())
                return isSaved;
        }
        finally {
            getRoot()._write.unlock();
        }

        final boolean saveResult = isSaved;

        // return results on main thread
        Scheduler.runTaskSync(_plugin, new Runnable() {

            @Override
            public void run() {

                getRoot()._write.lock();
                try {

                    while (!_saveHandlers.isEmpty()) {
                        StorageSaveHandler saveHandler = _saveHandlers.removeFirst();
                        saveHandler.onFinish(new StorageSaveResult(saveResult, saveHandler));
                    }
                }
                finally {
                    getRoot()._write.unlock();
                }
            }
        });

        return isSaved;
    }

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
                                        _saveTask = null;
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

        getRoot()._write.lock();
        try {
            try {
                getYamlConfiguration().save(destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finally {
            getRoot()._write.unlock();
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
    public int size() {

        if (_section == null) {
            //noinspection TailRecursion
            return getRoot().size(getFullPath(""));
        }

        getRoot()._read.lock();
        try {
            return _section.getKeys(false).size();
        }
        finally {
            getRoot()._read.unlock();
        }
    }

    public int size(String nodePath) {
        if (_section == null) {
            //noinspection TailRecursion
            return getRoot().size(getFullPath(nodePath));
        }

        getRoot()._read.lock();
        try {

            ConfigurationSection section = _section.getConfigurationSection(nodePath);
            if (section == null)
                return 0;

            return section.getKeys(false).size();
        }
        finally {
            getRoot()._read.unlock();
        }
    }

    @Override
    public void runBatchOperation(DataBatchOperation batch) {

        runBatchOperation(batch, this);
    }

    void runBatchOperation(final DataBatchOperation batch, IDataNode dataNode) {

        getRoot()._write.lock();
        try {
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
        finally {
            getRoot()._write.unlock();
        }

    }

    @Override
    public void preventSave(DataBatchOperation batch) {
        getRoot()._write.lock();
        try {
            _batch.start();
            batch.run(this);
            _batch.end();
        }
        finally {
            getRoot()._write.unlock();
        }
    }

    @Override
    public Set<String> getSubNodeNames() {
        return getSubNodeNames("");
    }

    @Override
    public Set<String> getSubNodeNames(String nodePath) {
        PreCon.notNull(nodePath);

        if (_section == null) {
            //noinspection TailRecursion
            return getRoot().getSubNodeNames(getFullPath(nodePath));
        }

        getRoot()._read.lock();
        try {
            ConfigurationSection section;
            if (_section.get(nodePath) == null) {
                return CollectionUtils.unmodifiableSet();
            }
            //noinspection unchecked
            return (section = _section.getConfigurationSection(nodePath)) != null
                    ? CollectionUtils.unmodifiableSet(section.getKeys(false))
                    : (Set<String>)CollectionUtils.UNMODIFIABLE_EMPTY_SET;
        }
        finally {
            getRoot()._read.unlock();
        }
    }

    @Override
    public Object get(String keyPath) {

        if (_section == null) {
            //noinspection TailRecursion
            return getRoot().get(getFullPath(keyPath));
        }

        getRoot()._read.lock();
        try {
            return _section.get(keyPath);
        }
        finally {
            getRoot()._read.unlock();
        }
    }

    @Override
    public boolean set(String keyPath, @Nullable Object value) {

        if (_section == null) {
            //noinspection TailRecursion
            return getRoot().set(getFullPath(keyPath), value);
        }

        getRoot()._write.lock();
        try {
            if (value instanceof UUID) {
                value = String.valueOf(value);
            }
            else if (value instanceof Location) {
                value = LocationUtils.locationToString((Location) value, 3);
            }
            else if (value instanceof ItemStack) {
                value = ItemStackUtils.serializeToString((ItemStack) value, SerializerOutputType.RAW);
            }
            else if (value instanceof ItemStack[]) {
                ItemStack[] stored = ((ItemStack[]) value).clone();
                for (int i = 0; i < stored.length; i++) {
                    if (stored[i] != null) {
                        stored[i] = stored[i].clone();
                    }
                }
                value = ItemStackUtils.serializeToString((ItemStack[]) value, SerializerOutputType.RAW);
            }
            else if (value instanceof Enum<?>) {
                Enum<?> e = (Enum<?>) value;
                value = e.name();
            }
            else if (value instanceof IDataNodeSerializable) {

                IDataNodeSerializable serializable = (IDataNodeSerializable)value;

                serializable.serialize(getNode(keyPath));

                return true;
            }

            _section.set(keyPath, value);
        }
        finally {
            getRoot()._write.unlock();
        }
        return true;
    }

    @Override
    public Map<String, Object> getAllValues() {
        return getAllValues("");
    }

    public Map<String, Object> getAllValues(String nodePath) {

        getRoot()._read.lock();
        try {

            ConfigurationSection subSection =
                    getRoot()._section.getConfigurationSection(getFullPath(nodePath));

            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>)
                    (subSection == null
                            ? Collections.emptyMap()
                            : subSection.getValues(true));

            Iterator<Entry<String, Object>> iterator = result.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();

                if (entry.getValue() instanceof MemorySection)
                    iterator.remove();
            }

            return result;
        }
        finally {
            getRoot()._read.unlock();
        }
    }

    @Override
    public void remove() {

        if (getRoot() == this)
            throw new UnsupportedOperationException("Cannot remove the root node.");

        getRoot().remove(getFullPath(""));
    }

    @Override
    public void remove(String nodePath) {

        if (getRoot() == this && nodePath.isEmpty())
            throw new UnsupportedOperationException("Cannot remove the root node.");

        set(nodePath, null);
    }

    @Override
    public boolean hasNode(String nodePath) {
        return get(nodePath) != null;
    }

    @Override
    public IDataNode getNode(String nodePath) {
        return new YamlDataNode(getRoot(), getFullPath(nodePath));
    }

    @Override
    public void clear() {

        if (isRoot()) {
            Set<String> keys = getSubNodeNames();
            for (String key : keys) {
                set(key, null);
            }
        }
        else {
            getRoot().set(getFullPath(""), null);
        }
    }

    public YamlConfiguration getYamlConfiguration() {
        return (YamlConfiguration)getRoot()._section;
    }

    public void assertNodes(File defaultConfig) {
        final YamlDataNode config = new YamlDataNode(_plugin, defaultConfig);

        config.loadAsync(new StorageLoadHandler() {

            @Override
            public void onFinish(StorageLoadResult result) {

                IDataNode dest = YamlDataNode.this;

                Set<String> keys = config.getSubNodeNames();
                for (String key : keys) {
                    if (dest.get(key) == null)
                        continue;

                    dest.set(key, config.get(key));
                }
            }
        });
    }
}
