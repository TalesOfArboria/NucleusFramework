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

import com.jcwhatever.nucleus.mixins.ILoadable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.coords.SyncLocation;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a key/value data storage node.
 */
public interface IDataNode extends Iterable<IDataNode>, ILoadable, IPluginOwned {

    /**
     * Specifies auto save settings for an {@link IDataNode}.
     */
    enum AutoSaveMode {
        DEFAULT,
        DISABLED,
        ENABLED
    }

    /**
     * Determine if the data is loaded.
     */
    @Override
    boolean isLoaded();

    /**
     * Get the name of the node.
     *
     * @return  The node name or empty if the node is root.
     */
    String getName();

    /**
     * Get the path of the node.
     */
    String getNodePath();

    /**
     * Get the root node.
     *
     * @return  The root node or self if the instance is the root node.
     */
    IDataNode getRoot();

    /**
     * Determine if the {@link IDataNode} is the root node.
     */
    boolean isRoot();

    /**
     * Get the parent node.
     *
     * @return  The parent node or null if the node is root.
     */
    @Nullable
    IDataNode getParent();

    /**
     * Load the data.
     */
    boolean load();

    /**
     * Load the data asynchronously.
     *
     * @return A future object to get the operation result status.
     */
    IFuture loadAsync();

    /**
     * Save the data from the main thread.
     *
     * <p>It is best to save synchronously when the server is shutting down since
     * async threads may not run.</p>
     *
     * @return  True if the data was saved successfully.
     */
    boolean saveSync();

    /**
     * Save the data.
     *
     * <p>The implementation should save on an async thread if saving causes blocking.</p>
     *
     * <p>This method is expected to prevent excessive saving. If save is called multiple times
     * in a row within a short amount of time, the save operation should only be completed once.</p>
     *
     * @return  A future object to get the operation result status.
     */
    IFuture save();

    /**
     * Save the data to the specified file from the main thread.
     *
     * <p>Not all implementation will support this. If not, false
     * is always returned.</p>
     *
     * @param destination  The destination file.
     *
     * @return  True if the save was completed successfully.
     */
    boolean saveSync(File destination);

    /**
     * Save the data to the specified file asynchronously.
     *
     * <p>Not all implementations will support this. If not, the future
     * is cancelled.</p>
     *
     * @param destination  The destination file.
     *
     * @return  A future object to get the operation result status.
     */
    IFuture save(File destination);

    /**
     * Determine if the node contains unsaved changes.
     */
    boolean isDirty();

    /**
     * Determine if auto save is enabled for the node.
     */
    AutoSaveMode getAutoSaveMode();

    /**
     * Get the default auto save mode.
     */
    AutoSaveMode getDefaultAutoSaveMode();

    /**
     * Set auto save mode for the node.
     *
     * @param mode  The auto save mode.
     */
    void setAutoSaveMode(AutoSaveMode mode);

    /**
     * Get the number of direct child data nodes.
     *
     * <p>Direct child nodes are the immediate children of the node and do
     * not include children of the immediate children.</p>
     */
    int size();

    /**
     * Determine if the current node has the specified node.
     *
     * @param nodePath  The node path to check.
     */
    boolean hasNode(String nodePath);

    /**
     * Get a child node.
     *
     * @param nodePath  The relative path of the child node.
     *
     * @return  The data node. Guaranteed to return a node even if it does not exist.
     */
    IDataNode getNode(String nodePath);

    /**
     * Get the names of the direct child nodes.
     *
     * <p>Direct child nodes are the immediate children of the node and do
     * not include children of the immediate children.</p>
     */
    Collection<String> getSubNodeNames();

    /**
     * Get the names of the direct child nodes.
     *
     * <p>Direct child nodes are the immediate children of the node and do
     * not include children of the immediate children.</p>
     *
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<String>> T getSubNodeNames(T output);

    /**
     * Get the names of the direct child nodes of the specified child node.
     *
     * <p>Direct child nodes are the immediate children of the node and do
     * not include children of the immediate children.</p>
     *
     * @param nodePath  The relative path of the child node.
     */
    Collection<String> getSubNodeNames(String nodePath);

    /**
     * Get the names of the direct child nodes of the specified child node.
     *
     * <p>Direct child nodes are the immediate children of the node and do
     * not include children of the immediate children.</p>
     *
     * @param nodePath  The relative path of the child node.
     * @param output    The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<String>> T getSubNodeNames(String nodePath, T output);

    /**
     * Clear all data in the node.
     */
    void clear();

    /**
     * Remove the node.
     *
     * <p>The root node cannot be removed.</p>
     */
    void remove();

    /**
     * Remove a child node.
     *
     * @param nodePath  The relative path of the child node.
     */
    void remove(String nodePath);

    /**
     * Set the value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key to set.
     * @param value    The value to set. The accepted values may differ with different implementations.
     *                 The values set should be value types that the interface allows retrieval of. If
     *                 not, the return value should be checked and a fall back used if the implementation
     *                 is unable to set the value.
     *
     * @return  True if the value was successfully set, otherwise false.
     */
    boolean set(String keyPath, @Nullable Object value);

    /**
     * Get the raw value of a node key.
     *
     * <p>Different implementations may store values in different ways and may return unexpected values.
     * For instance a number may be stored as a string in one implementation and as a number in another.
     * For this reason it is recommended you <em>avoid using this method</em>.</p>
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return The value or null if the key is not found.
     */
    @Nullable
    Object get(String keyPath);

    /**
     * Get all node key values in the node including sub node key values.
     */
    Map<String, Object> getAllValues();

    /**
     * Deserialize an {@link IDataNodeSerializable} object from the specified node path.
     *
     * @param nodePath   The node path.
     * @param typeClass  The object class.
     *
     * @param <T>  The object type.
     *
     * @return Null if failed to deserialize or data not present.
     */
    @Nullable
    <T extends IDataNodeSerializable> T getSerializable(String nodePath, Class<T> typeClass);

    /**
     * Get the integer value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or 0.
     */
    int getInteger(String keyPath);

    /**
     * Get the integer value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    int getInteger(String keyPath, int def);

    /**
     * Get the long value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or 0L.
     */
    long getLong(String keyPath);

    /**
     * Get the long value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    long getLong(String keyPath, long def);

    /**
     * Get the double value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or 0.0D
     */
    double getDouble(String keyPath);

    /**
     * Get the double value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    double getDouble(String keyPath, double def);

    /**
     * Get the boolean value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or false.
     */
    boolean getBoolean(String keyPath);

    /**
     * Get the boolean value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    boolean getBoolean(String keyPath, boolean def);

    /**
     * Get the string value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
    String getString(String keyPath);

    /**
     * Get the string value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
    String getString(String keyPath, @Nullable String def);

    /**
     * Get the {@link UUID} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
    UUID getUUID(String keyPath);

    /**
     * Get the {@link UUID} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
    UUID getUUID(String keyPath, @Nullable UUID def);

    /**
     * Get the {@link Date} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
    Date getDate(String keyPath);

    /**
     * Get the {@link Date} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or null.
     */
    @Nullable
    Date getDate(String keyPath, @Nullable Date def);

    /**
     * Get the {@link Location} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
    SyncLocation getLocation(String keyPath);

    /**
     * Get the {@link Location} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
    SyncLocation getLocation(String keyPath, @Nullable Location def);

    /**
     * Get the {@link ItemStack[]} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
    ItemStack[] getItemStacks(String keyPath);

    /**
     * Get the {@link ItemStack[]} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
    ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack def);

    /**
     * Get the {@link ItemStack[]} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
    ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack[] def);

    /**
     * Get the enum value of a node key.
     *
     * @param keyPath    The name or relative path and name of the key.
     * @param enumClass  The enum type of the value to return.
     *
     * @param <T>  The enum type.
     *
     * @return  Value of key or null.
     */
    @Nullable
    <T extends Enum<T>> T getEnum(String keyPath, Class<T> enumClass);

    /**
     * Get the enum value of a node key.
     *
     * @param keyPath    The name or relative path and name of the key.
     * @param def        The default value to return if the key value is not found.
     * @param enumClass  The enum type of the value to return.
     *
     * @param <T>  The enum type.
     *
     * @return  Value of key or default.
     */
    @Nullable
    <T extends Enum<T>> T getEnum(String keyPath, @Nullable T def, Class<T> enumClass);

    /**
     * Get an unknown enum value of a node key.
     *
     * @param keyPath    The name or relative path and name of the key.
     * @param def        The default value to return if the key value is not found.
     * @param enumClass  The enum type of the value to return.
     *
     * @return  Value of key or default.
     */
    @Nullable
    Enum<?> getEnumGeneric(String keyPath, @Nullable Enum<?> def, Class<? extends Enum<?>> enumClass);

    /**
     * Get a list of strings from a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
    List<String> getStringList(String keyPath, @Nullable List<String> def);
}
