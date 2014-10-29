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
import com.jcwhatever.bukkit.generic.utils.PreCon;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of setting definitions.
 */
public class SettingDefinitions extends HashMap<String, SettingDefinition> {

    private static final long serialVersionUID = -6642577546249886916L;

    private Map<String, ValueConverter<?, ?>> _converters;
    private Map<String, ISettingValidator> _validators;
    private List<SettingDefinition> _sortedList;
    private Set<String> _banned;

    /**
     * Constructor.
     */
    public SettingDefinitions() {
        super();
    }

    /**
     * Constructor.
     *
     * @param size  Initial capacity.
     */
    public SettingDefinitions(int size) {
        super(size);
    }

    /**
     * Constructor.
     *
     * @param definitions  Initial setting definitions
     */
    public SettingDefinitions(Map<String, SettingDefinition> definitions) {
        super(definitions);
    }

    /**
     * Define a new setting property and its value type.
     *
     * @param settingName  The name of the setting.
     * @param type         The value type.
     *
     * @return  Self for chainable calls.
     */
    public SettingDefinitions set(String settingName, Class<?> type) {
        if (!canAdd(settingName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(settingName);
        }

        ISettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(settingName);
        }

        SettingDefinition definition = new SettingDefinition(settingName, type, converter, validator);

        put(settingName, definition);

        _sortedList = null;

        return this;
    }

    /**
     * Define a new setting property, its default value, and value type.
     *
     * @param settingName  The name of the setting.
     * @param defaultVal   The default value.
     * @param type         The value type.
     *
     * @param <T>  The value type
     *
     * @return  Self for chainable calls.
     */
    public <T> SettingDefinitions set(String settingName, @Nullable T defaultVal, Class<T> type) {
        PreCon.notNullOrEmpty(settingName);
        PreCon.notNull(type);

        if (!canAdd(settingName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(settingName);
        }

        ISettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(settingName);
        }

        SettingDefinition definition = new SettingDefinition(settingName, defaultVal, type, converter, validator);

        this.put(settingName, definition);

        _sortedList = null;

        return this;
    }

    /**
     * Define a new setting property, its value type, and description.
     *
     * @param settingName  The name of the setting.
     * @param type         The value type.
     * @param description  The setting description.
     *
     * @return  Self for chainable calls.
     */
    public SettingDefinitions set(String settingName, Class<?> type, String description) {
        PreCon.notNullOrEmpty(settingName);
        PreCon.notNull(type);
        PreCon.notNull(description);

        if (!canAdd(settingName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(settingName);
        }

        ISettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(settingName);
        }

        SettingDefinition definition = new SettingDefinition(settingName, type, description, converter, validator);

        this.put(settingName, definition);

        _sortedList = null;

        return this;
    }

    /**
     * Define a new setting property, its default value, value type, and description.
     *
     * @param settingName  The name of the setting.
     * @param defaultVal   The default value.
     * @param type         The value type.
     * @param description  The setting description.
     *
     * @param <T>  The value type.
     *
     * @return  Self for chainable calls.
     */
    public <T> SettingDefinitions set(
            String settingName, @Nullable T defaultVal, Class<T> type, String description) {

        PreCon.notNullOrEmpty(settingName);
        PreCon.notNull(type);
        PreCon.notNull(description);

        if (!canAdd(settingName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(settingName);
        }

        ISettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(settingName);
        }

        SettingDefinition definition = new SettingDefinition(
                settingName, defaultVal, type, description, converter, validator);

        this.put(settingName, definition);

        _sortedList = null;

        return this;
    }

    /**
     * Set a setting definitions value converter.
     *
     * @param settingName  The name of the setting.
     * @param converter    The converter to use.
     *
     * @return  Self for chainable calls.
     */
    public SettingDefinitions setValueConverter(String settingName, ValueConverter<?, ?> converter) {
        PreCon.notNullOrEmpty(settingName);
        PreCon.notNull(converter);

        if (!canAdd(settingName))
            return this;

        SettingDefinition def = this.get(settingName);

        if (def == null) {
            if (_converters == null)
                _converters = new HashMap<String, ValueConverter<?, ?>>(3);

            _converters.put(settingName, converter);
        }
        else {
            def.setValueConverter(converter);
        }

        _sortedList = null;

        return this;
    }

    /**
     * Set a setting definitions value validator.
     *
     * @param settingName  The name of the setting.
     * @param validator    The validator to use.
     *
     * @return  Self for chainable calls.
     */
    public SettingDefinitions setValidator(String settingName, ISettingValidator validator) {
        PreCon.notNullOrEmpty(settingName);
        PreCon.notNull(validator);

        if (!canAdd(settingName))
            return this;

        SettingDefinition def = this.get(settingName);

        if (def == null) {
            if (_validators == null)
                _validators = new HashMap<String, ISettingValidator>(3);

            _validators.put(settingName, validator);
        }
        else {
            def.setValidator(validator);
        }

        return this;
    }

    /**
     * Merge another {@code SettingDefinitions} map into this one.
     *
     * <p>
     *     Adds all setting definitions, converters and validators
     *     from the provided {@code SettingDefinitions} with the exception
     *     of settings that have been banned in the current {@code SettingDefinitions}.
     * </p>
     * <p>
     *     Settings that are already defined in the current {@code SettingDefinitions}
     *     are not merged. However, if the current setting does not have
     *     a converter or validator that the provided {@code SettingDefinitions}
     *     has, the convert and/or validator is merged.
     * </p>
     * <p>
     *     Banned settings from the provided {@code SettingDefinitions} are merged. If the
     *     current {@code SettingDefinitions} contains any banned settings, they are
     *     removed.
     * </p>
     *
     * @param definitions  The definitions to merge.
     *
     * @return  Self for chainable calls.
     */
    public SettingDefinitions merge(SettingDefinitions definitions) {

        if (definitions == null)
            return this;

        Set<String> keys = definitions.keySet();

        // merge banned
        if (definitions._banned != null) {
            if (_banned == null)
                _banned = new HashSet<String>(3);

            _banned.addAll(_banned);
        }

        // merge settings
        for (String key : keys) {
            if (this.containsKey(key) || !canAdd(key))
                continue;

            SettingDefinition setting = definitions.get(key);
            if (setting == null)
                continue;

            this.put(key, setting);
        }

        // merge converters
        if (definitions._converters != null) {
            keys = definitions._converters.keySet();

            if (_converters == null)
                _converters = new HashMap<String, ValueConverter<?, ?>>(3);


            for (String key : keys) {
                if (_converters.containsKey(key) || !canAdd(key))
                    continue;

                ValueConverter<?, ?> converter = definitions._converters.get(key);
                if (converter == null)
                    continue;

                _converters.put(key, converter);
            }

        }

        // merge validators
        if (definitions._validators != null) {
            keys = definitions._validators.keySet();

            if (_validators == null)
                _validators = new HashMap<String, ISettingValidator>(3);


            for (String key : keys) {
                if (_validators.containsKey(key) || !canAdd(key))
                    continue;

                ISettingValidator validator = definitions._validators.get(key);
                if (validator == null)
                    continue;

                _validators.put(key, validator);
            }
        }

        // remove banned
        if (_banned != null) {
            for (String banKey : _banned) {
                remove(banKey);
            }
        }

        _sortedList = null;

        return this;
    }

    /**
     * Ban a settings definition by setting name. This prevents
     * the setting from being added or merged.
     *
     * @param settingName  The name of the setting.
     *
     * @return  Self for chainable calls.
     */
    public SettingDefinitions ban(String settingName) {
        remove(settingName);

        if (_banned == null)
            _banned = new HashSet<String>(3);

        _banned.add(settingName);

        return this;
    }

    @Override
    @Nullable
    public SettingDefinition put(String settingName, SettingDefinition def) {
        if (!canAdd(settingName))
            return null;

        _sortedList = null;
        return super.put(settingName, def);
    }

    @Override
    @Nullable
    public SettingDefinition remove(Object settingName) {
        _sortedList = null;
        return super.remove(settingName);
    }

    @Override
    public void clear() {
        _sortedList = null;
        super.clear();
    }

    @Override
    public Collection<SettingDefinition> values() {
        if (_sortedList == null) {
            List<SettingDefinition> defs = new ArrayList<SettingDefinition>(super.values());
            Collections.sort(defs);
            _sortedList = defs;
        }
        return new ArrayList<SettingDefinition>(_sortedList);
    }

    /*
     * Determine if a definition can be added.
     */
    private boolean canAdd(String settingName) {
        return _banned == null || !_banned.contains(settingName);

    }
}
