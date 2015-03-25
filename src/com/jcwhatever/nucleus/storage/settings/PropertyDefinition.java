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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.converters.Converter;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import javax.annotation.Nullable;

/**
 * A definition of a possible configuration property.
 */
public class PropertyDefinition implements Comparable<PropertyDefinition> {

    private final String _name;
    private final PropertyValueType _type;
    private final String _description;

    private final boolean _hasDefaultValue;
    private final Object _defaultValue;

    private Converter<?> _converter;
    private Converter<?> _unconverter;
    private IValidator<Object> _validator;

    /**
     * Constructor.
     *
     * <p>Defines a property without a default value.</p>
     *
     * @param name         The property name.
     * @param type         The value type.
     * @param description  The description of the property.
     */
    public PropertyDefinition(String name, PropertyValueType type, String description) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(type);
        PreCon.notNull(description);

        _name = name;
        _type = type;
        _description = description;

        _hasDefaultValue = false;
        _defaultValue = null;
    }

    /**
     * Constructor.
     *
     * <p>Defines a property with a default value.</p>
     *
     * @param name          The property name.
     * @param type          The value type.
     * @param defaultValue  The default value.
     * @param description   The description of the property.
     */
    public PropertyDefinition(String name, PropertyValueType type,
                              @Nullable Object defaultValue, String description) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(type);
        PreCon.notNull(description);

        _name = name;
        _type = type;
        _description = description;

        _hasDefaultValue = true;
        _defaultValue = defaultValue;

        if (defaultValue != null && !type.isAssignable(defaultValue)) {
            throw new RuntimeException("Invalid default value type. Should be: " + type.getTypeClass().getName());
        }
    }

    /**
     * Get the property name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the value type of the property.
     */
    public PropertyValueType getValueType() {
        return _type;
    }

    /**
     * Get the property description.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Get the default value.
     *
     * <p>Use {@link #hasDefaultValue} to check first.
     * Default value may be null.</p>
     */
    @Nullable
    public Object getDefaultValue() {
        return _defaultValue;
    }

    /**
     * Determine if the setting has a default value.
     *
     * <p>A default value could be null.</p>
     */
    public boolean hasDefaultValue() {
        return _hasDefaultValue;
    }

    /**
     * Get the value converter, if any.
     *
     * @return  Null if not set.
     */
    @Nullable
    public Converter<?> getConverter() {
        return _converter;
    }

    /**
     * Set the value converter.
     *
     * @param converter  The value converter.
     */
    public void setConverter(@Nullable Converter<?> converter) {
        _converter = converter;
    }

    /**
     * Get the value unconverter, if any.
     *
     * @return  Null if not set.
     */
    @Nullable
    public Converter<?> getUnconverter() {
        return _converter;
    }

    /**
     * Set the value unconverter.
     *
     * @param converter  The value converter.
     */
    public void setUnconverter(@Nullable Converter<?> converter) {
        _unconverter = converter;
    }

    /**
     * Get the value validator.
     */
    @Nullable
    public IValidator<Object> getValidator() {
        return _validator;
    }

    /**
     * Set the value validator.
     *
     * @param validator  The value validator.
     */
    public void setValidator(@Nullable IValidator<Object> validator) {
        _validator = validator;
    }

    @Override
    public int compareTo(PropertyDefinition o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public int hashCode() {
        return _name.hashCode() ^ _type.hashCode() ^ _description.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof PropertyDefinition) {
            PropertyDefinition def = (PropertyDefinition)obj;

            return def._name.equals(_name) &&
                    def._type.equals(_type) &&
                    def._description.equals(_description);
        }
        return false;
    }
}
