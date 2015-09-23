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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A JSON based data node.
 */
public class JsonDataNode extends AbstractDataNode {

    /**
     * Convert a {@link DataPath} instance to a {@link java.io.File} which
     * points to a disk based JSON file.
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

        return new File(directory, pathComp[pathComp.length - 1] + ".json");
    }


    private final Plugin _plugin;
    private final Gson _gson;
    private JsonObject _object;
    private boolean _isLoaded;
    private final JsonDataNode _root;
    private final Deque<FutureAgent> _saveAgents;
    private IScheduledTask _saveTask;
    private File _file;
    private String _json;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param file    The json file to load from and/or save to.
     */
    public JsonDataNode(Plugin plugin, File file) {
        _plugin = plugin;
        _gson = new GsonBuilder().setPrettyPrinting().create();
        _object = null;
        _root = this;
        _file = file;
        _object = _gson.fromJson("{}", JsonObject.class);
        _saveAgents = new ArrayDeque<>(5);
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param json    The json string to load.
     */
    public JsonDataNode(Plugin plugin, String json) {
        _plugin = plugin;
        _gson = new GsonBuilder().setPrettyPrinting().create();
        _object = _gson.fromJson(json, JsonObject.class);
        _isLoaded = true;
        _root = this;
        _json = json;
        _saveAgents = new ArrayDeque<>(5);
    }

    /**
     * Private constructor.
     *
     * <p>Used for sub data node.</p>
     *
     * @param root      The root data node.
     * @param basePath  The sub nodes base path.
     */
    private JsonDataNode(JsonDataNode root, String basePath) {
        super(root, basePath);
        _root = root;
        _plugin = root.getPlugin();
        _gson = root._gson;
        _saveAgents = null;
        _isLoaded = true;
    }

    /**
     * Get the saved json text.
     *
     * @return  The json text. Returns all json from root node.
     */
    public String getJson() {
        return _file == null ? _json : _root._gson.toJson(_root._object);
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
    public IDataNode getRoot() {
        return _root;
    }

    @Override
    public boolean isRoot() {
        return _root == this;
    }

    @Override
    public boolean load() {

        if (_file == null)
            return true;

        if (!_file.exists() || !_file.isFile())
            return false;

        FileReader reader;

        try {
            reader = new FileReader(_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            _object = _gson.fromJson(reader, JsonObject.class);
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            return false;
        }

        _isLoaded = true;
        return true;
    }

    @Override
    public IFuture loadAsync() {
        final FutureAgent agent = new FutureAgent();

        Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {
            @Override
            public void run() {
                if (load()) {
                    agent.success();
                }
                else {
                    agent.error("The node was not constructed with a file or the file was not found.");
                }
            }
        });

        return agent.getFuture();
    }

    @Override
    public boolean saveSync() {
        if (_file != null) {
            return saveSync(_file);
        }
        else {
            _json = _gson.toJson(_object);
            clean();
            return true;
        }
    }

    @Override
    public IFuture save() {

        FutureAgent agent = new FutureAgent();
        _root._saveAgents.add(agent);

        if (_file == null || !_plugin.isEnabled()) {
            return saveSync() ? agent.success() : agent.error("Error while saving.");
        }

        if (_root._saveTask != null)
            return agent.getFuture();

        Scheduler.runTaskLaterAsync(_plugin, 2, new Runnable() {
            @Override
            public void run() {

                final boolean isSuccess = saveSync();
                _root._saveTask = null;

                if (_root._saveAgents.isEmpty())
                    return;

                final List<FutureAgent> agents = new ArrayList<FutureAgent>(_root._saveAgents);

                Scheduler.runTaskSync(_plugin, new Runnable() {
                    @Override
                    public void run() {

                        for (FutureAgent agent : agents) {
                            if (isSuccess) {
                                agent.success();
                            } else {
                                agent.error("Failed to save Json to file.");
                            }
                        }
                    }
                });
            }
        });

        return agent.getFuture();
    }

    @Override
    public boolean saveSync(File destination) {

        String json = _gson.toJson(_object);

        if (destination == _file) {
            _json = json;
            clean();
        }

        try {
            if (!destination.exists() && !destination.createNewFile()) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        int written = FileUtils.writeTextFile(destination, StandardCharsets.UTF_8, json);
        if (written == -1) {
            return false;
        } else {
            clean();
            return true;
        }
    }

    @Override
    public IFuture save(final File destination) {

        final FutureAgent agent = new FutureAgent();

        if (_file == null || !_plugin.isEnabled()) {
            return saveSync(destination) ? agent.success() : agent.error("Error while saving.");
        }

        Scheduler.runTaskLaterAsync(_plugin, 1, new Runnable() {
            @Override
            public void run() {

                final boolean isSuccess = saveSync(destination);

                Scheduler.runTaskSync(_plugin, new Runnable() {
                    @Override
                    public void run() {

                        if (isSuccess) {
                            agent.success();
                        } else {
                            agent.error("Failed to save Json to file.");
                        }
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

        JsonElement element = getJsonElement("");
        if (element == null || !element.isJsonObject())
            return 0;

        return element.getAsJsonObject().entrySet().size();
    }

    @Override
    public boolean hasNode(String nodePath) {
        return getJsonElement(nodePath) != null;
    }

    @Override
    public IDataNode getNode(String nodePath) {

        String fullPath = getFullPath(nodePath);
        if (fullPath.isEmpty())
            return _root;

        return new JsonDataNode(_root, fullPath);
    }

    @Override
    public Collection<String> getSubNodeNames() {
        return getSubNodeNames("", new ArrayList<String>(0));
    }

    @Override
    public <T extends Collection<String>> T getSubNodeNames(T output) {
        return getSubNodeNames("", output);
    }

    @Override
    public Collection<String> getSubNodeNames(String nodePath) {
        return getSubNodeNames(nodePath, new ArrayList<String>(0));
    }

    @Override
    public <T extends Collection<String>> T getSubNodeNames(String nodePath, T output) {
        JsonElement current = getJsonElement(nodePath);
        if (current == null || !current.isJsonObject())
            return output;

        Set<Map.Entry<String, JsonElement>> entrySet = current.getAsJsonObject().entrySet();

        if (output instanceof ArrayList) {
            ((ArrayList) output).ensureCapacity(entrySet.size() + output.size());
        }

        for (Map.Entry<String, JsonElement> entry : entrySet) {
            output.add(entry.getKey());
        }

        return output;
    }

    @Override
    public void clear() {
        Collection<String> names = getSubNodeNames("");
        for (String name : names) {
            remove(name);
        }

        if (names.size() > 0)
            markDirty();
    }

    @Override
    public void remove() {
        remove("");
    }

    @Override
    public void remove(String nodePath) {

        String path = getFullPath(nodePath);
        if (path.isEmpty())
            throw new UnsupportedOperationException("Cannot remove the root node.");

        String[] pathElements = TextUtils.PATTERN_DOT.split(path);
        removeKey(pathElements);
    }

    @Override
    public boolean set(String keyPath, @Nullable Object value) {

        keyPath = getFullPath(keyPath);
        String[] path = TextUtils.PATTERN_DOT.split(keyPath);

        if (!keyPath.isEmpty())
            removeKey(path);

        if (value != null) {
            if (value instanceof IDataNodeSerializable) {
                IDataNode node = getNode(keyPath);
                IDataNodeSerializable serializable = (IDataNodeSerializable)value;
                serializable.serialize(node);
                markDirty();
                return true;
            }

            if (keyPath.isEmpty())
                return false;

            if (value instanceof UUID) {
                value = String.valueOf(value);
            }
            else if (value instanceof Date){
                value = ((Date) value).getTime();
            }
            else if (value instanceof Location) {
                value = LocationUtils.serialize((Location)value, 2);
            }
            else if (value instanceof ItemStack) {
                value = ItemStackUtils.serialize((ItemStack)value);
            }
            else if (value instanceof ItemStack[]) {
                value = ItemStackUtils.serialize((ItemStack[]) value);
            }
            else if (value instanceof Enum<?>) {
                Enum<?> e = (Enum<?>) value;
                value = e.name();
            }
            else if (value instanceof Collection) {
                Collection collection = (Collection)value;
                String[] array = new String[collection.size()];

                int i=0;
                for (Object elm : collection) {
                    array[i] = elm == null ? null : String.valueOf(elm);
                    i++;
                }

                value = array;
            }
            else if (value.getClass().isArray() && !(value instanceof String[])) {

                int size = Array.getLength(value);
                String[] array = new String[size];

                for (int i=0; i < size; i++) {
                    Object elm = Array.get(value, i);

                    array[i] = elm == null ? null : String.valueOf(elm);
                }

                value = array;
            }
            else if (value instanceof CharSequence) {
                value = value.toString();
            }

            if (value instanceof String[]) {

                String[] array = (String[])value;
                JsonArray jsonArray = new JsonArray();
                for (String string : array) {
                    jsonArray.add(_gson.toJsonTree(string));
                }
                value = jsonArray;
            }

            addKey(path, value);
        }

        markDirty();
        return true;
    }

    @Nullable
    @Override
    public Object get(String keyPath) {
        PreCon.notNull(keyPath);

        keyPath = getFullPath(keyPath);

        if (keyPath.isEmpty())
            return _object;

        String[] path = TextUtils.PATTERN_DOT.split(keyPath);

        JsonElement element = getJsonElement(path);
        if (element == null || element.isJsonObject())
            return null;

        if (element.isJsonArray()) {
            return getStringList(keyPath, null);
        }

        return element.getAsString();
    }

    @Override
    public Map<String, Object> getAllValues() {

        Map<String, Object> result = new HashMap<>(50);

        JsonElement selfElement = getJsonElement("");
        if (selfElement == null)
            return result;

        if (!selfElement.isJsonObject()) {
            result.put("", selfElement.getAsString());
            return result;
        }

        JsonObject object = selfElement.getAsJsonObject();

        String basePath = "";

        getAllValuesRecursive(object, basePath, result);

        return result;
    }

    @Override
    public int hashCode() {
        return _root._object.hashCode() + _rawPath.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JsonDataNode) {
            JsonDataNode other = (JsonDataNode)obj;
            return other._root._object == _root._object &&
                    other._rawPath.equals(_rawPath);
        }
        return false;
    }

    @Override
    @Nullable
    protected Object getBooleanObject(String keyPath) {
        JsonElement element = getJsonElement(keyPath);
        if (element == null || element.isJsonObject())
            return null;

        return element.getAsBoolean();
    }

    @Override
    @Nullable
    protected Object getNumberObject(String keyPath) {
        JsonElement element = getJsonElement(keyPath);
        if (element == null || element.isJsonObject())
            return null;

        return element.getAsNumber();
    }

    @Override
    @Nullable
    protected Object getStringObject(String keyPath) {
        JsonElement element = getJsonElement(keyPath);
        if (element == null || element.isJsonObject())
            return null;

        return element.getAsString();
    }

    @Override
    @Nullable
    protected Object getCollectionObject(String keyPath) {
        JsonElement element = getJsonElement(keyPath);
        if (element == null || element.isJsonObject() || !element.isJsonArray())
            return null;

        JsonArray array = element.getAsJsonArray();
        List<String> result = new ArrayList<>(array.size());

        for (int i=0; i < array.size(); i++) {
            result.add(array.get(i).getAsString());
        }

        return result;
    }

    private void getAllValuesRecursive(JsonObject object, String basePath, Map<String, Object> result) {

        Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();

        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String name = basePath.isEmpty() ? entry.getKey() : basePath + '.' + entry.getKey();
            JsonElement element = entry.getValue();

            if (element.isJsonObject()) {
                getAllValuesRecursive(element.getAsJsonObject(), name, result);
            }
            else {
                result.put(name, element.getAsString());
            }
        }
    }

    // path requires full path
    private void removeKey(String[] path) {

        if (path.length == 1) {
            _root._object.remove(path[0]);
            return;
        }

        JsonElement rootElement = _root._object.get(path[0]);
        if (rootElement == null || !rootElement.isJsonObject())
            return;

        JsonObject object = rootElement.getAsJsonObject();

        for (int i=1; i < path.length - 1; i++) {

            JsonElement element = object.get(path[i]);
            if (element == null || !element.isJsonObject())
                return;

            object = element.getAsJsonObject();
        }

        object.remove(path[path.length - 1]);
    }

    // path requires full path
    private void addKey(String[] path, Object value) {

        if (path.length == 1) {
            _root._object.add(path[0], _gson.toJsonTree(value));
            return;
        }

        JsonElement rootElement = _root._object.get(path[0]);
        if (rootElement == null || !rootElement.isJsonObject()) {
            _root._object.remove(path[0]);
            _root._object.add(path[0], _gson.toJsonTree(_gson.toJsonTree(new Object())));
            rootElement = _root._object.get(path[0]);
        }

        JsonObject object = rootElement.getAsJsonObject();

        for (int i=1; i < path.length - 1; i++) {

            JsonObject prev = object;

            JsonElement element = object.get(path[i]);
            if (element == null || !element.isJsonObject()) {
                prev.remove(path[i]);
                prev.add(path[i], _gson.toJsonTree(_gson.toJsonTree(new Object())));
                element = prev.get(path[i]);
            }

            object = element.getAsJsonObject();
        }

        if (value instanceof JsonElement) {
            object.add(path[path.length - 1], (JsonElement)value);
        }
        else {
            object.add(path[path.length - 1], _gson.toJsonTree(value));
        }

    }

    private JsonElement getJsonElement(String relativePath) {
        String fullPath = getFullPath(relativePath);

        if (fullPath.isEmpty())
            return _root._object;

        String[] path = TextUtils.PATTERN_DOT.split(fullPath);

        return getJsonElement(path);
    }

    // path requires full path
    private JsonElement getJsonElement(String[] path) {

        if (path.length == 1) {
            return _root._object.get(path[0]);
        }

        JsonElement rootElement = _root._object.get(path[0]);
        if (rootElement == null || !rootElement.isJsonObject())
            return null;

        JsonObject object = rootElement.getAsJsonObject();

        for (int i=1; i < path.length - 1; i++) {

            JsonElement element = object.get(path[i]);
            if (element == null || !element.isJsonObject())
                return null;

            object = element.getAsJsonObject();
        }

        return object.get(path[path.length - 1]);
    }
}
