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

import com.jcwhatever.nucleus.utils.converters.ValueConverter;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Used to build a {@link PropertyDefinition} map.
 */
public class SettingsBuilder {

    private final Map<String, PropertyDefinition> _definitions = new HashMap<>(10);
    private Set<String> _banned = new HashSet<>(10);

    /**
     * Set a new property definition.
     *
     * @param propertyName  The name of the property.
     * @param type          The property type.
     * @param description   The property description.
     *
     * @return  Self for chaining.
     */
    public SettingsBuilder set(String propertyName, PropertyValueType type, String description) {

        PropertyDefinition definition = new PropertyDefinition(propertyName, type, description);
        _definitions.put(propertyName, definition);

        return this;
    }

    /**
     * Set a new property definition.
     *
     * @param propertyName  The name of the property.
     * @param type          The property type class.
     * @param defaultValue  The default value.
     * @param description   The property description.
     *
     * @param <T>  The property type.
     *
     * @return  Self for chaining.
     */
    public <T> SettingsBuilder set(String propertyName, PropertyValueType<T> type,
                                  @Nullable T defaultValue, String description) {

        PropertyDefinition definition = new PropertyDefinition(propertyName, type, defaultValue, description);
        _definitions.put(propertyName, definition);

        return this;
    }

    /**
     * Set a value converter on a property that has already been set.
     *
     * @param propertyName  The name of the property.
     * @param converter     The converter to set.
     *
     * @return  Self for chaining.
     */
    public SettingsBuilder setValueConverter(String propertyName, ValueConverter<?, ?> converter) {
        PreCon.notNullOrEmpty(propertyName);
        PreCon.notNull(converter);

        PropertyDefinition definition = _definitions.get(propertyName);
        if (definition == null)
            throw new RuntimeException("A property named '" + propertyName + "' has not been set yet.");

        definition.setValueConverter(converter);

        return this;
    }

    /**
     * Set a value validator on a property that has already been set.
     *
     * @param propertyName  The name of the property.
     * @param validator     The validator to set.
     *
     * @return  Self for chaining.
     */
    public SettingsBuilder setValidator(String propertyName, IValidator<Object> validator) {
        PreCon.notNullOrEmpty(propertyName);
        PreCon.notNull(validator);

        PropertyDefinition definition = _definitions.get(propertyName);
        if (definition == null)
            throw new RuntimeException("A property named '" + propertyName + "' has not been set yet.");

        definition.setValidator(validator);

        return this;
    }


    /**
     * Merge a collection of {@link PropertyDefinition} into the {@link SettingsBuilder}.
     *
     * <p>If the {@link SettingsBuilder} already contains the property, it is not added.</p>
     *
     * @param definitions  The collection of property definitions.
     */
    public SettingsBuilder merge(Collection<PropertyDefinition> definitions) {
        PreCon.notNull(definitions);

        for (PropertyDefinition definition : definitions) {
            if (!_definitions.containsKey(definition.getName()) &&
                    !_banned.contains(definition.getName())) {
                _definitions.put(definition.getName(), definition);
            }
        }

        return this;
    }

    /**
     * Remove and prevent {@link PropertyDefinition}'s from being added.
     *
     * <p>Useful before or after merging to prevent specific properties
     * from being added.</p>
     *
     * @param propertyNames  The names of the properties to ban.
     */
    public SettingsBuilder ban(String... propertyNames) {

        for (String banned : propertyNames) {
            _banned.add(banned);

            _definitions.remove(banned);
        }

        return this;
    }

    /**
     * Build a new map of {@link PropertyDefinition}'s.
     *
     * <p>The map is keyed to the property name.</p>
     */
    public Map<String, PropertyDefinition> buildDefinitions() {

        Map<String, PropertyDefinition> map = new HashMap<>(_definitions.size());
        map.putAll(_definitions);

        return map;
    }

    /**
     * Build a new settings manager using the {@link PropertyDefinition}'s
     * that were set and the specified data node.
     *
     * @param dataNode  The data node the manager will manager.
     */
    public SettingsManager buildManager(IDataNode dataNode) {
        Map<String, PropertyDefinition> map = buildDefinitions();
        return new SettingsManager(dataNode, map);
    }
}
