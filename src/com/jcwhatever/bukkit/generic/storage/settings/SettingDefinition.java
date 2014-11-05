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
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Defines a setting name and value type and optionally a
 * default value, description, converter and/or validator.
 */
public class SettingDefinition implements Comparable<SettingDefinition> {

    private final String _configName;
    private final Class<?> _type;
    private final String _description;
    private Object _defaultVal;
    private boolean _hasDefaultVal;
    private ValueConverter<?, ?> _converter;
    private ISettingValidator _validator;

    /**
     * Constructor.
     *
     * @param settingName  The name of the setting.
     * @param type         The setting type.
     * @param converter    The setting converter.
     */
    public SettingDefinition(String settingName, Class<?> type, @Nullable ValueConverter<?, ?> converter) {
        this(settingName, null, type, "", converter, null);
    }

    /**
     * Constructor.
     *
     * @param settingName  The name of the setting.
     * @param type         The setting type.
     * @param converter    The setting converter.
     * @param validator    The setting value validator.
     */
    public SettingDefinition(String settingName, Class<?> type,
                             @Nullable ValueConverter<?, ?> converter, @Nullable ISettingValidator validator) {

        this(settingName, null, type, "", converter, validator);
    }

    /**
     * Constructor.
     *
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     * @param type          The setting type.
     * @param converter     The setting converter.
     */
    public SettingDefinition(String settingName, @Nullable Object defaultValue, Class<?> type,
                             @Nullable ValueConverter<?, ?> converter) {

        this(settingName, defaultValue, type, "", converter, null);
        _hasDefaultVal = true;
    }

    /**
     * Constructor.
     *
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     * @param type          The setting type.
     * @param converter     The setting converter.
     * @param validator     The setting value validator.
     */
    public SettingDefinition(String settingName, @Nullable Object defaultValue, Class<?> type,
                             @Nullable ValueConverter<?, ?> converter, @Nullable ISettingValidator validator) {

        this(settingName, defaultValue, type, "", converter, validator);
        _hasDefaultVal = true;
    }

    /**
     * Constructor.
     *
     * @param settingName  The name of the setting.
     * @param type         The setting type.
     * @param description  The setting description.
     * @param converter    The setting converter.
     */
    public SettingDefinition(String settingName, Class<?> type, String description, ValueConverter<?, ?> converter) {
        this(settingName, null, type, description, converter, null);
    }

    /**
     * Constructor.
     *
     * @param settingName  The name of the setting.
     * @param type         The setting type.
     * @param description  The setting description.
     * @param converter    The setting converter.
     * @param validator    The setting value validator.
     */
    public SettingDefinition(String settingName, Class<?> type, String description,
                             @Nullable ValueConverter<?, ?> converter, @Nullable ISettingValidator validator) {

        this(settingName, null, type, description, converter, validator);
    }

    /**
     * Constructor.
     *
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     * @param type          The setting type.
     * @param description   The setting description.
     * @param converter     The setting converter.
     */
    public SettingDefinition(String settingName, @Nullable Object defaultValue, Class<?> type, String description,
                             @Nullable ValueConverter<?, ?> converter) {

        this(settingName, defaultValue, type, description, converter, null);
        _hasDefaultVal = true;
    }

    /**
     * Constructor.
     *
     * @param settingName   The name of the setting.
     * @param defaultValue  The default value.
     * @param type          The setting type.
     * @param description   The setting description.
     * @param converter     The setting converter.
     * @param validator     The setting value validator.
     */
    public SettingDefinition(String settingName, @Nullable Object defaultValue, Class<?> type, String description,
                             @Nullable ValueConverter<?, ?> converter, @Nullable ISettingValidator validator) {
        PreCon.notNullOrEmpty(settingName);
        PreCon.notNull(type);
        PreCon.notNull(description);

        _configName = settingName;
        _defaultVal = defaultValue;
        _type = type;
        _description = description;
        _converter = converter;
        _validator = validator;
        _hasDefaultVal = true;
    }

    /**
     * Get the name of the setting.
     */
    public String getSettingName() {
        return _configName;
    }

    /**
     * Get the value type.
     */
    public Class<?> getValueType() {
        return _type;
    }

    /**
     * Get the settings default value.
     */
    @Nullable
    public Object getDefaultVal() {
        return _defaultVal;
    }

    /**
     * Determine if the setting has a
     * default value set.
     * <p>
     *     This is determined by which constructor is used. A null value
     *     from {@code getDefaultVal} could mean there is no default value or
     *     it could mean the default value is null.
     * </p>
     */
    public boolean hasDefaultVal() {
        return _hasDefaultVal;
    }

    /**
     * Get the setting description.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Get the settings value converter, if any.
     */
    @Nullable
    public ValueConverter<?, ?> getValueConverter() {
        return _converter;
    }

    /**
     * Set the settings value converter.
     *
     * @param converter  The setting converter.
     */
    public void setValueConverter(@Nullable ValueConverter<?, ?> converter) {
        _converter = converter;
    }

    /**
     * Get the settings value validator.
     */
    @Nullable
    public ISettingValidator getValidator() {
        return _validator;
    }

    /**
     * Set the settings value validator.
     *
     * @param validator  The setting value validator.
     */
    public void setValidator(@Nullable ISettingValidator validator) {
        _validator = validator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            String configName = (String)obj;
            return _configName.equals(configName);
        }
        if (obj instanceof SettingDefinition) {
            SettingDefinition def = (SettingDefinition)obj;
            return def._configName.equals(_configName);
        }
        return false;
    }

    @Override
    public int compareTo(SettingDefinition o) {
        return getSettingName().compareTo(o.getSettingName());
    }
}
