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


package com.jcwhatever.bukkit.generic.storage.settings;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * An interface for a settings manager used to manage an objects settings.
 *
 * <p>Useful for dynamically changing settings.</p>
 *
 * <p>
 *     An example would be having multiple class types with each having its own set
 *     of settings and new types can be added with different settings. Using a settings
 *     manager makes it easier to dynamically parse available settings to display to
 *     a user and make changes without having to hard code methods of changing the
 *     settings for each type.
 * </p>
 * <p>
 *     A settings manager can also be used to interface an external/foreign data storage system
 *     with the system in use.
 * </p>
 */
public interface ISettingsManager {

    /**
     * Get an immutable map of {@code PropertyDefinition}'s
     * that the settings manager manages.
     *
     * <p>The map is keyed to the property names.</p>
     */
    Map<String, PropertyDefinition> getDefinitions();

    /**
     * Determine if a property name is a valid
     * setting name.
     *
     * @param propertyName  The property name to check.
     */
    boolean isProperty(String propertyName);

    /**
     * Set the value of a property.
     *
     * @param propertyName  The property name.
     * @param value         The value to set.
     * @return
     */
    boolean set(String propertyName, @Nullable Object value);

    /**
     * Get the value of a property.
     *
     * @param propertyName  The property name to check.
     *
     * @param <T>  The value type.
     *
     * @return  Null if the value is null or the property does not exist.
     */
    @Nullable
    <T> T get(String propertyName);

    /**
     * Get the value of a property and un-convert it using the
     * property's converter.
     *
     * <p>If there is no converter, the raw value is returned.</p>
     *
     * @param propertyName  The property name.
     *
     * @param <T>  The value type.
     *
     * @return  Null if the value is null or the property does not exist.
     */
    @Nullable
    <T> T getUnconverted(String propertyName);

    /**
     * Get a boolean property value.
     *
     * @param propertyName  The name of the property.
     *
     * @return The value or false.
     */
    boolean getBoolean(String propertyName);

    /**
     * Get a property value as an integer.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or 0.
     */
    int getInteger(String propertyName);

    /**
     * Get a property value as a long.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or 0;
     */
    long getLong(String propertyName);

    /**
     * Get a property value as a double.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or 0.
     */
    double getDouble(String propertyName);

    /**
     * Get a property value as a string.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null.
     */
    @Nullable
    String getString(String propertyName);

    /**
     * Get a property value as a location.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null.
     */
    @Nullable
    Location getLocation(String propertyName);

    /**
     * Get a property value as an item stack.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null.
     */
    @Nullable
    ItemStack[] getItemStacks(String propertyName);

    /**
     * Get a property value as a UUID.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null.
     */
    @Nullable
    UUID getUUID(String propertyName);

    /**
     * Get a property value as an enum.
     *
     * @param propertyName  The name of the property.
     * @param type          The enum type class.
     *
     * @param <T>  The enum type.
     *
     * @return  The value or null.
     */
    @Nullable
    <T extends Enum<T>> T getEnum(String propertyName, Class<T> type);

    /**
     * Add a callback that runs when a setting is changed.
     *
     * @param runnable  The callback runnable to run.
     */
    void onSettingsChanged(Runnable runnable);

    /**
     * Remove a callback that runs when a setting is changed.
     *
     * @param runnable  The callback runnable to remove.
     */
    void removeOnSettingsChanged(Runnable runnable);
}
