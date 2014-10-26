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

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a key/value data storage node
 */
public interface IDataNode {

    /**
     * The owning plugin.
     */
    Plugin getPlugin();

    /**
     * Determine if the data is loaded.
     */
	boolean isLoaded();

    /**
     * Get the name of the node.
     *
     * @return  Null if the node is the root node.
     */
    @Nullable
	String getNodeName();

    /**
     * Get the root node.
     */
	IDataNode getRoot();

    /**
     * Load the data synchronously.
     */
	boolean load();

    /**
     * Load the data asynchronously.
     */
	void loadAsync();

    /**
     * Load the data asynchronously.
     *
     * @param loadHandler  The handler to run when the loading is completed.
     */
	void loadAsync(@Nullable StorageLoadHandler loadHandler);

    /**
     * Save the data synchronously.
     *
     * @return  True if the data was saved successfully.
     */
	boolean save();

    /**
     * Save the data asynchronously.
     *
     * @param saveHandler  The handler to run when the save is completed.
     */
	void saveAsync(@Nullable StorageSaveHandler saveHandler);

    /**
     * Save the data to the specified file.
     *
     * @param destination  The destination file.
     *
     * @return  True if the save was completed successfully.
     */
	boolean save(File destination);

    /**
     * Save the data to the specified file asynchronously.
     *
     * @param destination  The destination file.
     * @param saveHandler  The handler to run when the save is completed.
     */
	void saveAsync(File destination, @Nullable StorageSaveHandler saveHandler);

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
     */
    Set<String> getSubNodeNames();

    /**
     * Get the names of the direct child nodes
     * of the specified child node.
     *
     * @param nodePath  The relative path of the child node.
     */
    Set<String> getSubNodeNames(String nodePath);

    /**
     * Clear all data in the node.
     */
    void clear();

    /**
     * Remove the node.
     * <p>
     *     The root node cannot be removed.
     * </p>
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
     * @param value    The value to set.
     *
     * @return  True if the value was successfully set.
     */
    boolean set(String keyPath, @Nullable Object value);

    /**
     * Get the value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key to set.
     *
     * @return Null of the key is not found.
     */
    @Nullable
	Object get(String keyPath);

    /**
     * Get the value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key to set.
     * @param type     The data type expected.
     *
     * @return  Null if the key is not found.
     */
    @Nullable
    Object get(String keyPath, DataType type); // must return null if node not found

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
     * Get the {@code UUID} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
	UUID getUUID(String keyPath);

    /**
     * Get the {@code UUID} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
	UUID getUUID(String keyPath, @Nullable UUID def);

    /**
     * Get the {@code Location} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
	Location getLocation(String keyPath);

    /**
     * Get the {@code Location} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
	Location getLocation(String keyPath, @Nullable Location def);

    /**
     * Get the {@code ItemStack[]} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     *
     * @return  Value of key or null.
     */
    @Nullable
	ItemStack[] getItemStacks(String keyPath);

    /**
     * Get the {@code ItemStack[]} value of a node key.
     *
     * @param keyPath  The name or relative path and name of the key.
     * @param def      The default value to return if the key value is not found.
     *
     * @return  Value of key or default.
     */
    @Nullable
	ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack def);

    /**
     * Get the {@code ItemStack[]} value of a node key.
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

    /**
     * Run a batch operation on the node that prevents
     * saving the data.
     *
     * <p>
     *     The data is saved once the batch operation completes.
     * </p>
     *
     * @param batch  The batch operation handler.
     */
    void runBatchOperation(BatchOperation batch);

    /**
     * Run a batch operation on the node that prevents
     * saving the data.
     *
     * <p>
     *     The data is NOT saved once the batch operation completes.
     * </p>
     *
     * @param batch  The batch operation handler.
     */
	void preventSave(BatchOperation batch);

}
