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

import com.jcwhatever.bukkit.generic.converters.ValueConverter;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.utils.EnumUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractSettingsManager implements ISettingsManager {

    private List<Runnable> _onSettingsChanged = new ArrayList<Runnable>(5);

    public AbstractSettingsManager() {

    }

    @Override
    public void addOnSettingsChanged(Runnable runnable, boolean run) {
        _onSettingsChanged.add(runnable);

        if (run) {
            //Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, runnable, 5);
            runnable.run();
        }

    }


    @Override
    public ValidationResults set(String property, Object value) {
        SettingDefinitions definitions = getPossibleSettings();

        if (definitions == null)
            return ValidationResults.FALSE;

        SettingDefinition def = definitions.get(property);

        if (def == null)
            return ValidationResults.FALSE;

        if (value == null) {
            return onSet(property, null);
        }

        ValueConverter<?, ?> converter = def.getValueConverter();
        if (converter != null) {
            value = converter.convert(value);
            if (value == null)
                return ValidationResults.FALSE;
        }

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

        SettingValidator validator = def.getValidator();
        if (validator != null) {
            ValidationResults results = validator.validate(value);

            if (!results.isValid())
                return results;
        }

        ValidationResults result = onSet(property, value);
        if (result.isValid()) {
            for (Runnable runnable : _onSettingsChanged) {
                runnable.run();
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String property) {
        return (T)get(property, false);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String property, boolean unconvertValue) {

        SettingDefinitions definitions = getPossibleSettings();

        if (definitions == null)
            return null;


        SettingDefinition def = definitions.get(property);

        if (def == null)
            return null;

        Class<?> type = def.getValueType();
        Object value;

        // BOOLEAN
        if (type.isAssignableFrom(Boolean.class))
            value = getBoolean(property, def.hasDefaultVal() ? (Boolean)def.getDefaultVal() : false);

            // INTEGER
        else if (type.isAssignableFrom(Integer.class))
            value = getInteger(property, def.hasDefaultVal() ? (Integer)def.getDefaultVal() : 0);

            // LONG
        else if (type.isAssignableFrom(Long.class))
            value = getLong(property, def.hasDefaultVal() ? (Long)def.getDefaultVal() : 0);

            // DOUBLE
        else if (type.isAssignableFrom(Double.class))
            value = getDouble(property, def.hasDefaultVal() ? (Double)def.getDefaultVal() : 0.0D);

            // STRING
        else if (type.isAssignableFrom(String.class))
            value = getString(property, def.hasDefaultVal() ? (String)def.getDefaultVal() : null);

            // LOCATION
        else if (type.isAssignableFrom(Location.class))
            value = getLocation(property, def.hasDefaultVal() ? (Location)def.getDefaultVal() : null);

            // ITEMSTACK
        else if (type.isAssignableFrom(ItemStack.class))
            value = getItemStacks(property, def.hasDefaultVal() ? (ItemStack[])def.getDefaultVal() : null);


            // UUID
        else if (type.isAssignableFrom(UUID.class))
            value = getUUID(property, def.hasDefaultVal() ? (UUID)def.getDefaultVal() : null);

        else if (type.isEnum())
            value = getGenericEnum(property, (Enum<?>)(def.hasDefaultVal() ? type.cast(def.getDefaultVal()) : null), (Class<Enum<?>>)type);

        else {
            value = getObject(property);
            if (value == null)
                return (T)def.getDefaultVal();
        }

        if (unconvertValue) {
            ValueConverter<?, ?> converter = def.getValueConverter();
            if (converter != null) {
                value = converter.unconvert(value);
            }
        }

        return (T)value;
    }



    protected abstract ValidationResults onSet(String property, Object value);

    protected abstract Boolean getBoolean(String property, boolean defaultVal);

    protected abstract Integer getInteger(String property, int defaultVal);

    protected abstract Long getLong(String property, long defaultVal);

    protected abstract Double getDouble(String property, double defaultVal);

    protected abstract String getString(String property, String defaultVal);

    protected abstract Location getLocation(String property, Location defaultVal);

    protected abstract ItemStack[] getItemStacks(String property, ItemStack[] defaultVal);

    protected abstract UUID getUUID(String property, UUID defaultValue);

    protected abstract Enum<?> getGenericEnum(String property, Enum<?> defaultValue, Class<Enum<?>> type);

    protected abstract Object getObject(String property);

}
