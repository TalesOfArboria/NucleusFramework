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


package com.jcwhatever.nucleus.storage.settings;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * An interface for a settings manager used to manage an objects settings using
 * predefined properties.
 *
 * <p>Allows dynamically retrieving and changing possible settings.</p>
 */
public interface ISettingsManager {

    /**
     * Get an immutable map of {@link PropertyDefinition}'s that the settings
     * manager manages.
     *
     * <p>The map is keyed to the property names.</p>
     */
    Map<String, PropertyDefinition> getDefinitions();

    /**
     * Determine if a property name is a valid setting name.
     *
     * @param propertyName  The property name to check.
     */
    boolean isProperty(String propertyName);

    /**
     * Set the value of a property.
     *
     * @param propertyName  The property name.
     * @param value         The value to set. The value must be the type defined by the properties
     *                      {@link PropertyDefinition} or a type that the properties optional converter
     *                      can convert.
     *
     * @return  True if the value was set, otherwise false.
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
     * Get the value of a property and un-convert it using the property's converter,
     * if any.
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
     * @return The value or false if the type was not found or is not a boolean.
     */
    boolean getBoolean(String propertyName);

    /**
     * Get a property value as an integer.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or 0 if the type was not found or is not an int.
     */
    int getInteger(String propertyName);

    /**
     * Get a property value as a long.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or 0 if the type was not found or is not a long.
     */
    long getLong(String propertyName);

    /**
     * Get a property value as a double.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or 0 if the type was not found or is not a double.
     */
    double getDouble(String propertyName);

    /**
     * Get a property value as a string.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null if not found or the type is not a {@link String}.
     */
    @Nullable
    String getString(String propertyName);

    /**
     * Get a property value as a location.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null if not found or the type is not a {@link Location}.
     */
    @Nullable
    Location getLocation(String propertyName);

    /**
     * Get a property value as an item stack.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null if not found or the type is not an {@link ItemStack[]}.
     */
    @Nullable
    ItemStack[] getItemStacks(String propertyName);

    /**
     * Get a property value as a UUID.
     *
     * @param propertyName  The name of the property.
     *
     * @return  The value or null if not found or the type is not a {@link UUID}.
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
     * @return  The value or null if not found or the type is not an enum.
     */
    @Nullable
    <T extends Enum<T>> T getEnum(String propertyName, Class<T> type);
}
