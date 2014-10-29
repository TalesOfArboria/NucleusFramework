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


package com.jcwhatever.bukkit.generic.storage.settings;

import javax.annotation.Nullable;

/**
 * Settings manager used to make it easier to dynamically set setting properties.
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
     * Get {@code SettingDefinitions} used to specify possible
     * settings, their type, default value, and description.
     */
    SettingDefinitions getPossibleSettings();

    /**
     * Determine if a property name is a valid
     * setting name.
     *
     * @param property  The property name to check.
     */
    boolean isSetting(String property);

    /**
     * Set the value of a property.
     *
     * @param property  The property name.
     * @param value     The value to set.
     * @return
     */
    ValidationResults set(String property, @Nullable Object value);

    /**
     * Get the value of a property.
     *
     * @param property  The property name to check.
     *
     * @param <T>  The value type.
     *
     * @return  Null if the value is null or the property does not exist.
     */
    @Nullable
    <T> T get(String property);

    /**
     * Get the value of a property and specify
     * if any value converters, if any, should be used
     * on the property.
     *
     * @param property        The property name.
     * @param unconvertValue  True to unconvert the value before returning it.
     *
     * @param <T>  The value type.
     *
     * @return  Null if the value is null or the property does not exist.
     */
    @Nullable
    <T> T get(String property, boolean unconvertValue);

    /**
     * Add a callback that runs when a setting is changed.
     *
     * @param runnable  The callback runnable to run.
     * @param run       True to immediately run the callback after it is added.
     */
    void addOnSettingsChanged(Runnable runnable, boolean run);

}
