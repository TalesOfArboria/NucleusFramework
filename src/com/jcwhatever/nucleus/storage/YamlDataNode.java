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

import com.jcwhatever.nucleus.collections.observer.agent.AgentMultimap;
import com.jcwhatever.nucleus.collections.observer.agent.AgentSetMultimap;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * A YAML based data node.
 */
public class YamlDataNode extends AbstractDataNode {

    /**
     * Convert a {@link DataPath} instance to a {@link java.io.File} which
     * points to a disk based YAML file.
     *
     * @param plugin    The owning plugin. Used to determine the base
     *                  path of the file.
     * @param dataPath  The {@link DataPath} to convert.
     */
    public static File dataPathToFile(Plugin plugin, DataPath dataPath) {
        String[] pathComp = dataPath.getPath();

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

    // instantiated on root only
    private final ConfigurationSection _section;
    private final Map<String, YamlDataNode> _cachedNodes;
    private final AgentMultimap<IDataNode, FutureAgent> _saveAgents;
    private volatile boolean _isLoaded;
    private volatile IScheduledTask _saveTask;
    protected String _yamlString;
    protected File _file;

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
     * for root node.
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
        _saveAgents = new AgentSetMultimap<>();
        _cachedNodes = new HashMap<>(10);
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
        _saveAgents = null;
        _cachedNodes = null;
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
        return getRoot()._isLoaded;
    }

    @Override
    public boolean load() {

        if (getRoot()._file == null && getRoot()._isLoaded)
            return true;

        if (getRoot()._file == null)
            return false;

        if (!getRoot()._file.exists())
            return false;

        YamlConfiguration yaml = (YamlConfiguration)getRoot()._section;

        getRoot()._write.lock();
        try {

            yaml.load(getRoot()._file);
            return getRoot()._isLoaded = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            getRoot()._write.unlock();
        }


        if (getRoot()._file != null)
            NucMsg.severe("The config-file '{0}' failed to load.", getRoot()._file.getName());

        return getRoot()._isLoaded = false;
    }

    @Override
    public IFuture loadAsync() {

        final FutureAgent agent = new FutureAgent();

        Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {

            @Override
            public void run() {

                getRoot()._write.lock();
                try {

                    boolean isLoaded = load();

                    agent.sendStatus(
                            isLoaded
                                    ? FutureStatus.SUCCESS
                                    : FutureStatus.ERROR,
                            null
                    );
                }
                finally {
                    getRoot()._write.unlock();
                }
            }
        });

        return agent.getFuture();
    }

    @Override
    public boolean saveSync() {

        if (!isRoot()) {
            //noinspection TailRecursion
            return getRoot().saveSync();
        }

        YamlConfiguration yaml = (YamlConfiguration)_section;

        boolean isSaved;

        _write.lock();
        try {

            try {

                // save yaml
                if (_file != null) {
                    yaml.save(_file);
                }
                else {
                    _yamlString = yaml.getKeys(false).size() == 0 ? "" : yaml.saveToString();
                }

                isSaved = true;

                // mark dirty nodes as clean
                cleanAll();

            } catch (Exception e) {
                e.printStackTrace();
                isSaved = false;
            }
        }
        finally {
            _write.unlock();
        }

        return isSaved;
    }

    @Override
    public IFuture save() {

        final FutureAgent agent = new FutureAgent();

        getRoot()._saveAgents.put(this, agent);

        // check that 1 or more batch operations are not in progress.
        if (getRoot()._saveTask != null) {
            return agent.getFuture();
        }

        if (_plugin.isEnabled()) {

            getRoot()._saveTask = Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {

                @Override
                public void run() {

                    final boolean isSaved = saveSync();
                    final Collection<FutureAgent> agents =
                            getRoot()._saveAgents.removeAll(YamlDataNode.this);

                    getRoot()._saveTask = null;

                    if (agents.isEmpty())
                        return;

                    // return results on main thread
                    Scheduler.runTaskSync(_plugin, new Runnable() {

                        @Override
                        public void run() {

                            for (FutureAgent agent : agents) {

                                if (isSaved)
                                    agent.success();
                                else
                                    agent.error();
                            }
                        }
                    });
                }
            });

        }
        else {
            if (saveSync()) {
                agent.success();
            } else {
                agent.error();
            }
        }

        return agent.getFuture();
    }

    @Override
    public boolean saveSync(File destination) {

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
    public IFuture save(final File destination) {

        final FutureAgent agent = new FutureAgent();

        // save on alternate thread
        Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {

            @Override
            public void run() {

                final boolean isSaved = saveSync(destination);

                if (!agent.hasSubscribers())
                    return;

                // return results on main thread
                Scheduler.runTaskSync(_plugin, new Runnable() {

                    @Override
                    public void run() {
                        agent.sendStatus(
                                isSaved
                                        ? FutureStatus.SUCCESS
                                        : FutureStatus.ERROR,
                                null);
                    }
                });
            }

        });

        return agent.getFuture();
    }

    @Override
    public AutoSaveMode getDefaultAutoSaveMode() {
        return AutoSaveMode.DISABLED;
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
    public Collection<String> getSubNodeNames() {
        return getSubNodeNames("", new HashSet<String>(0));
    }

    @Override
    public <T extends Collection<String>> T getSubNodeNames(T output) {
        return getSubNodeNames("", output);
    }

    @Override
    public Collection<String> getSubNodeNames(String nodePath) {
        return getSubNodeNames(nodePath, new HashSet<String>(0));
    }

    @Override
    public <T extends Collection<String>> T getSubNodeNames(String nodePath, T output) {
        PreCon.notNull(nodePath);
        PreCon.notNull(output);

        if (_section == null) {
            //noinspection TailRecursion
            return getRoot().getSubNodeNames(getFullPath(nodePath), output);
        }

        getRoot()._read.lock();
        try {

            if (_section.get(nodePath) == null)
                return output;

            ConfigurationSection section = _section.getConfigurationSection(nodePath);
            if (section == null)
                return output;

            output.addAll(section.getKeys(false));
            return output;
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

        markDirty();

        if (_section == null) {
            //noinspection TailRecursion
            if (getRoot().set(getFullPath(keyPath), value)) {

                return true;
            }
            return false;
        }

        getRoot()._write.lock();
        try {

            if (value instanceof UUID) {
                value = String.valueOf(value);
            }
            else if (value instanceof Date) {
                value = ((Date)value).getTime();
            }
            else if (value instanceof Location) {
                value = LocationUtils.serialize((Location) value, 3);
            }
            else if (value instanceof ItemStack) {
                value = ItemStackUtils.serialize((ItemStack) value);
            }
            else if (value instanceof ItemStack[]) {
                value = ItemStackUtils.serialize((ItemStack[]) value);
            }
            else if (value instanceof Enum<?>) {
                Enum<?> e = (Enum<?>) value;
                value = e.name();
            }
            else if (value instanceof IDataNodeSerializable) {

                IDataNodeSerializable serializable = (IDataNodeSerializable)value;

                IDataNode dataNode = getNode(keyPath);
                dataNode.clear();
                serializable.serialize(dataNode);

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
        PreCon.notNull(nodePath);

        String fullPath = getFullPath(nodePath);
        if (fullPath.isEmpty())
            return getRoot();

        YamlDataNode node = getRoot()._cachedNodes.get(fullPath);
        if (node == null) {
            node = new YamlDataNode(getRoot(), fullPath);
            getRoot()._cachedNodes.put(fullPath, node);
        }

        return node;
    }

    @Override
    public void clear() {

        if (isRoot()) {
            Collection<String> keys = getSubNodeNames();
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
}
