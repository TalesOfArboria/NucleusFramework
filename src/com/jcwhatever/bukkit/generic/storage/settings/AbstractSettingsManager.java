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

import com.jcwhatever.bukkit.generic.converters.ValueConverter;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.utils.EnumUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract implementation of {@code ISettingsManager}.
 */
public abstract class AbstractSettingsManager implements ISettingsManager {

    private List<Runnable> _onSettingsChanged = new ArrayList<Runnable>(5);

    @Override
    public ValidationResults set(String settingName, @Nullable Object value) {
        PreCon.notNull(settingName);

        SettingDefinitions definitions = getPossibleSettings();

        // make sure definitions have been defined.
        if (definitions == null)
            return ValidationResults.FALSE;

        SettingDefinition def = definitions.get(settingName);

        // make sure the property definition is defined.
        if (def == null)
            return ValidationResults.FALSE;

        // set null value
        if (value == null) {
            ValidationResults result = onSet(settingName, null);
            if (result.isValid()) {
                runSettingsChangedCallback();
            }

            return result;
        }

        // get the properties value converter.
        ValueConverter<?, ?> converter = def.getValueConverter();
        if (converter != null) {

            value = converter.convert(value);
            if (value == null)
                return ValidationResults.FALSE;
        }

        // get the properties value type.
        Class<?> type = def.getValueType();

        // BOOLEAN
        if (type.isAssignableFrom(Boolean.class)) {

            if (!(value instanceof Boolean))
                value = Boolean.parseBoolean(value.toString());
        }

        // UUID
        else if (type.isAssignableFrom(UUID.class)) {

            if (!(value instanceof UUID)) {
                try {
                    value = UUID.fromString(value.toString());
                }
                catch (IllegalArgumentException iae) {
                    return ValidationResults.FALSE;
                }
            }
        }

        // STRING
        else if (type.isAssignableFrom(String.class)) {

            if (!(value instanceof String))
                value = value.toString();
        }

        // BYTE, SHORT, INTEGER
        else if (type.isAssignableFrom(Byte.class) ||
                type.isAssignableFrom(Short.class) ||
                type.isAssignableFrom(Integer.class)) {

            try {
                value = Integer.parseInt(value.toString());
            }
            catch (NumberFormatException nfe) {
                return ValidationResults.FALSE;
            }

        }

        // DOUBLE
        else if (type.isAssignableFrom(Double.class)) {
            try {
                value = Double.parseDouble(value.toString());
            }
            catch (NumberFormatException nfe) {
                return ValidationResults.FALSE;
            }
        }

        // ITEM STACK
        else if (type.isAssignableFrom(ItemStack.class)) {

            if (value instanceof ItemStack) {
                value = new ItemStack[] { (ItemStack)value };
            }
            else if (value instanceof String) {
                value = ItemStackHelper.parse((String)value);
                if (value == null)
                    return ValidationResults.FALSE;
            }
        }

        // ENUM
        else if (type.isEnum()) {

            if (value instanceof String) { // convert string to enum

                String constantName = (String)value;

                @SuppressWarnings("unchecked")
                Enum<?> e = EnumUtils.searchGenericEnum(constantName, (Class<Enum<?>>)type, null);
                if (e == null) {
                    return ValidationResults.FALSE;
                }
                else {
                    value = e;
                }
            }
        }

        // get the properties validator, if any.
        ISettingValidator validator = def.getValidator();
        if (validator != null) {
            ValidationResults results = validator.validate(value);

            if (!results.isValid())
                return results;
        }

        // call onSet and receive validation results.
        ValidationResults result = onSet(settingName, value);
        if (result.isValid()) {
            runSettingsChangedCallback();
        }

        return result;
    }

    @Override
    @Nullable
    public <T> T get(String settingName) {
        return get(settingName, false);
    }


    @Override
    @Nullable
    public <T> T get(String settingName, boolean unconvertValue) {

        // get possible settings
        SettingDefinitions definitions = getPossibleSettings();
        if (definitions == null)
            return null;

        // get setting definition
        SettingDefinition def = definitions.get(settingName);
        if (def == null)
            return null;

        // get setting type
        Class<?> type = def.getValueType();

        Object value;

        // BOOLEAN
        if (type.isAssignableFrom(Boolean.class))
            value = getBoolean(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (Boolean)def.getDefaultVal() : false);

            // INTEGER
        else if (type.isAssignableFrom(Integer.class))
            value = getInteger(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (Integer)def.getDefaultVal() : 0);

            // LONG
        else if (type.isAssignableFrom(Long.class))
            value = getLong(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (Long)def.getDefaultVal() : 0);

            // DOUBLE
        else if (type.isAssignableFrom(Double.class))
            value = getDouble(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (Double)def.getDefaultVal() : 0.0D);

            // STRING
        else if (type.isAssignableFrom(String.class))
            value = getString(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (String)def.getDefaultVal() : null);

            // LOCATION
        else if (type.isAssignableFrom(Location.class))
            value = getLocation(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (Location)def.getDefaultVal() : null);

            // ITEMSTACK
        else if (type.isAssignableFrom(ItemStack.class))
            value = getItemStacks(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (ItemStack[])def.getDefaultVal() : null);

            // UUID
        else if (type.isAssignableFrom(UUID.class))
            value = getUUID(settingName, def.hasDefaultVal() && def.getDefaultVal() != null
                    ? (UUID)def.getDefaultVal() : null);

        else if (type.isEnum()) {

            @SuppressWarnings("unchecked") Class<Enum<?>> e = (Class<Enum<?>>) type;

            value = getGenericEnum(settingName,
                    (Enum<?>) (def.hasDefaultVal() && def.getDefaultVal() != null
                            ? type.cast(def.getDefaultVal())
                            : null), e);

        } else {
            value = getObject(settingName);
            if (value == null) {
                @SuppressWarnings("unchecked") T result = (T) def.getDefaultVal();
                return result;
            }
        }

        // unconvert store value
        if (unconvertValue) {
            ValueConverter<?, ?> converter = def.getValueConverter();
            if (converter != null) {
                value = converter.unconvert(value);
            }
        }

        @SuppressWarnings("unchecked") T result = (T)value;
        return result;
    }

    @Override
    public void addOnSettingsChanged(Runnable runnable, boolean run) {
        _onSettingsChanged.add(runnable);

        if (run) {
            runnable.run();
        }

    }

    /**
     * Called to set a value.
     *
     * @param settingName  The property name.
     * @param value     The value to set.
     *
     * @return  Setting of setting the value.
     */
    protected abstract ValidationResults onSet(String settingName, @Nullable Object value);

    /**
     * Called to get a boolean setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    protected abstract Boolean getBoolean(String settingName, boolean defaultVal);

    /**
     * Called to get an integer setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    protected abstract Integer getInteger(String settingName, int defaultVal);

    /**
     * Called to get a long setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    protected abstract Long getLong(String settingName, long defaultVal);

    /**
     * Called to get a double setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    protected abstract Double getDouble(String settingName, double defaultVal);

    /**
     * Called to get a string setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    @Nullable
    protected abstract String getString(String settingName, @Nullable String defaultVal);

    /**
     * Called to get a location setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    @Nullable
    protected abstract Location getLocation(String settingName, @Nullable Location defaultVal);

    /**
     * Called to get an item stack array setting value.
     *
     * @param settingName    The property name.
     * @param defaultVal  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    @Nullable
    protected abstract ItemStack[] getItemStacks(String settingName, @Nullable ItemStack[] defaultVal);

    /**
     * Called to get a unique ID setting value.
     *
     * @param settingName      The property name.
     * @param defaultValue  The value to return if the property is not set or found.
     *
     * @return  Setting value or default value.
     */
    @Nullable
    protected abstract UUID getUUID(String settingName, @Nullable UUID defaultValue);

    /**
     * Called to get an enum setting value.
     *
     * @param settingName      The property name.
     * @param defaultValue  The value to return if the property is not set or found.
     * @param type          The enum type.
     *
     * @return  Setting value or default value.
     */
    @Nullable
    protected abstract Enum<?> getGenericEnum(
            String settingName, @Nullable Enum<?> defaultValue, @Nullable Class<Enum<?>> type);

    /**
     * Called to get a setting value of any type.
     *
     * @param settingName  The property name.
     *
     * @return  Setting value or null.
     */
    @Nullable
    protected abstract Object getObject(String settingName);

    /*
     * Run callbacks that listen to setting changed event
     */
    private void runSettingsChangedCallback() {
        for (Runnable runnable : _onSettingsChanged) {
            runnable.run();
        }
    }

}
