package com.jcwhatever.bukkit.generic.storage.settings;

import com.jcwhatever.bukkit.generic.converters.ValueConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingDefinitions extends HashMap<String, SettingDefinition> {

    Map<String, ValueConverter<?, ?>> _converters;
    Map<String, SettingValidator> _validators;

    /**
     *
     */
    private static final long serialVersionUID = -6642577546249886916L;

    private List<SettingDefinition> _sortedList;
    private Set<String> _banned;


    public SettingDefinitions() {
        super();
    }



    public SettingDefinitions(Map<String, SettingDefinition> definitions) {
        super(definitions);
    }

    public SettingDefinitions set(String configName, Class<?> type) {
        if (!canAdd(configName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(configName);
        }

        SettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(configName);
        }

        SettingDefinition definition = new SettingDefinition(configName, type, converter, validator);

        put(configName, definition);

        _sortedList = null;

        return this;
    }

    public <T> SettingDefinitions set(String configName, T defaultVal, Class<T> type) {
        if (!canAdd(configName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(configName);
        }

        SettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(configName);
        }

        SettingDefinition definition = new SettingDefinition(configName, defaultVal, type, converter, validator);

        this.put(configName, definition);

        _sortedList = null;

        return this;
    }

    public SettingDefinitions set(String configName, Class<?> type, String descr) {
        if (!canAdd(configName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(configName);
        }

        SettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(configName);
        }

        SettingDefinition definition = new SettingDefinition(configName, type, descr, converter, validator);

        this.put(configName, definition);

        _sortedList = null;

        return this;
    }

    public <T> SettingDefinitions set(String configName, T defaultVal, Class<T> type, String descr) {
        if (!canAdd(configName))
            return this;

        ValueConverter<?, ?> converter = null;
        if (_converters != null) {
            converter = _converters.get(configName);
        }

        SettingValidator validator = null;
        if (_validators != null) {
            validator = _validators.get(configName);
        }

        SettingDefinition definition = new SettingDefinition(configName, defaultVal, type, descr, converter, validator);

        this.put(configName, definition);

        _sortedList = null;

        return this;
    }

    public SettingDefinitions setValueConverter(String configName, ValueConverter<?, ?> converter) {
        if (!canAdd(configName))
            return this;

        SettingDefinition def = this.get(configName);

        if (def == null) {
            if (_converters == null)
                _converters = new HashMap<String, ValueConverter<?, ?>>(3);

            _converters.put(configName, converter);
        }
        else {
            def.setValueConverter(converter);
        }

        _sortedList = null;

        return this;
    }

    public SettingDefinitions setValidator(String configName, SettingValidator validator) {
        if (!canAdd(configName))
            return this;

        SettingDefinition def = this.get(configName);

        if (def == null) {
            if (_validators == null)
                _validators = new HashMap<String, SettingValidator>(3);

            _validators.put(configName, validator);
        }
        else {
            def.setValidator(validator);
        }

        return this;
    }

    public SettingDefinitions merge(SettingDefinitions defs) {

        if (defs == null)
            return this;

        Set<String> keys = defs.keySet();

        // merge banned
        if (defs._banned != null) {
            if (_banned == null)
                _banned = new HashSet<String>(3);

            _banned.addAll(_banned);
        }



        // merge settings
        for (String key : keys) {
            if (this.containsKey(key) || !canAdd(key))
                continue;

            SettingDefinition setting = defs.get(key);
            if (setting == null)
                continue;

            this.put(key, setting);
        }


        // merge converters
        if (defs._converters != null) {
            keys = defs._converters.keySet();

            if (_converters == null)
                _converters = new HashMap<String, ValueConverter<?, ?>>(3);


            for (String key : keys) {
                if (_converters.containsKey(key) || !canAdd(key))
                    continue;

                ValueConverter<?, ?> converter = defs._converters.get(key);
                if (converter == null)
                    continue;

                _converters.put(key, converter);
            }

        }


        // merge validators
        if (defs._validators != null) {
            keys = defs._validators.keySet();

            if (_validators == null)
                _validators = new HashMap<String, SettingValidator>(3);


            for (String key : keys) {
                if (_validators.containsKey(key) || !canAdd(key))
                    continue;

                SettingValidator validator = defs._validators.get(key);
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

    public SettingDefinitions ban(String key) {
        remove(key);

        if (_banned == null)
            _banned = new HashSet<String>(3);

        _banned.add(key);

        return this;
    }

    @Override
    public SettingDefinition put(String key, SettingDefinition def) {
        if (!canAdd(key))
            return null;

        _sortedList = null;
        return super.put(key, def);
    }

    @Override
    public SettingDefinition remove(Object key) {
        _sortedList = null;
        return super.remove(key);
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

    private boolean canAdd(String key) {
        return _banned == null || !_banned.contains(key);

    }

}
