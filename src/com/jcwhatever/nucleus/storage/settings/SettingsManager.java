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

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.converters.Converter;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.managed.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * An implementation of {@link ISettingsManager}.
 */
public class SettingsManager implements ISettingsManager, IDisposable {

    private final IDataNode _dataNode;
    private final NamedUpdateAgents _agents = new NamedUpdateAgents();
    private Map<String, PropertyDefinition> _definitions;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Initializes manager without property definitions.</p>
     *
     * @param dataNode  The data node to manage.
     */
    public SettingsManager(IDataNode dataNode) {
        this(dataNode, null);
    }

    /**
     * Constructor.
     *
     * @param dataNode     The data node to manage.
     * @param definitions  Property definitions.
     */
    public SettingsManager(IDataNode dataNode, @Nullable Map<String, PropertyDefinition> definitions) {

        _dataNode = dataNode;

        _definitions = Collections.unmodifiableMap(
                definitions == null ? new HashMap<String, PropertyDefinition>(0) : definitions);
    }

    @Override
    public Map<String, PropertyDefinition> getDefinitions() {
        return _definitions;
    }

    @Override
    public boolean isProperty(String propertyName) {
        PreCon.notNull(propertyName);

        return _definitions.containsKey(propertyName);
    }

    @Override
    public boolean set(String propertyName, @Nullable Object value) {
        PreCon.notNull(propertyName);

        PropertyDefinition definition = _definitions.get(propertyName);
        if (definition == null)
            return false;

        Converter<?> converter = definition.getConverter();
        if (converter != null) {
            value = converter.convert(value);
        }

        IValidator<Object> validator = definition.getValidator();
        if (!(validator != null && !validator.isValid(value)) && _dataNode.set(propertyName, value)) {

            if (_agents.hasAgent("onChange"))
                _agents.update("onChange", new PropertyValue(this, definition, value));

            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public <T> T get(String propertyName) {
        PreCon.notNull(propertyName);

        PropertyDefinition definition = _definitions.get(propertyName);
        if (definition == null)
            return null;

        @SuppressWarnings("unchecked")
        T result = (T)_dataNode.get(propertyName);

        if (result == null) {

            @SuppressWarnings("unchecked")
            T defaultValue = (T) definition.getDefaultValue();

            return defaultValue;
        }
        else {
            switch (definition.getValueType().getType()) {
                case BOOLEAN:
                    if (result instanceof String) {

                        @SuppressWarnings("unchecked")
                        T bool = (T)(Boolean) TextUtils.parseBoolean((String) result);
                        return bool;
                    }
                    break;
                case INTEGER:
                    if (result instanceof String) {

                        @SuppressWarnings("unchecked")
                        T integer = (T)(Integer)TextUtils.parseInt((String)result, 0);
                        return integer;
                    }
                    else if (result instanceof Long) {

                        @SuppressWarnings("unchecked")
                        T integerByte = (T)(Integer)((Long)result).intValue();
                        return integerByte;
                    }
                    break;

                case LONG:
                    if (result instanceof String) {

                        @SuppressWarnings("unchecked")
                        T l = (T)(Long)TextUtils.parseLong((String)result, 0L);
                        return l;
                    }
                    else if (result instanceof Integer) {

                        @SuppressWarnings("unchecked")
                        T longInteger = (T)(Long)((Integer)result).longValue();
                        return longInteger;
                    }
                    break;

                case ITEM_STACK_ARRAY:
                    if (result instanceof String) {

                        try {
                            @SuppressWarnings("unchecked")
                            T itemStacks = (T) ItemStackUtils.parse((String) result);
                            return itemStacks;
                        } catch (InvalidItemStackStringException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    break;

                case LOCATION:
                    if (result instanceof String) {

                        @SuppressWarnings("unchecked")
                        T location = (T) LocationUtils.parseLocation((String) result);
                        return location;
                    }
                    break;

                case UNIQUE_ID:
                    if (result instanceof String) {

                        @SuppressWarnings("unchecked")
                        T uuid = (T) TextUtils.parseUUID((String) result);
                        return uuid;
                    }
                    break;

                case ENUM:
                    if (result instanceof String) {

                        @SuppressWarnings("unchecked")
                        T enumeration = (T) EnumUtils.searchEnum(
                                (String) result, (Class<Enum>) definition.getValueType().getTypeClass());

                        return enumeration;
                    }
                    break;
            }
        }

        return result;
    }

    @Nullable
    @Override
    public <T> T getUnconverted(String propertyName) {
        PreCon.notNull(propertyName);

        PropertyDefinition definition = _definitions.get(propertyName);
        if (definition == null)
            return null;

        Object result = get(propertyName);
        if (result == null)
            return null;

        if (definition.getUnconverter() != null) {
            @SuppressWarnings("unchecked")
            T unconverted = (T)definition.getUnconverter().convert(result);
            return unconverted;
        }

        @SuppressWarnings("unchecked")
        T casted = (T)result;

        return casted;
    }

    @Override
    public boolean getBoolean(String propertyName) {
        PreCon.notNull(propertyName);

        Boolean result = getDefaultValue(propertyName);
        return _dataNode.getBoolean(propertyName, result != null ? result : false);
    }

    @Override
    public int getInteger(String propertyName) {
        PreCon.notNull(propertyName);

        Integer i = getDefaultValue(propertyName);
        return _dataNode.getInteger(propertyName, i != null ? i : 0);
    }

    @Override
    public long getLong(String propertyName) {
        PreCon.notNull(propertyName);

        Long l = getDefaultValue(propertyName);
        return _dataNode.getLong(propertyName, l != null ? l : 0);
    }

    @Override
    public double getDouble(String propertyName) {
        PreCon.notNull(propertyName);

        Double d = getDefaultValue(propertyName);
        return _dataNode.getDouble(propertyName, d != null ? d : 0);
    }

    @Nullable
    @Override
    public String getString(String propertyName) {
        PreCon.notNull(propertyName);

        return _dataNode.getString(propertyName, (String) getDefaultValue(propertyName));
    }

    @Nullable
    @Override
    public Location getLocation(String propertyName) {
        PreCon.notNull(propertyName);

        return _dataNode.getLocation(propertyName, (Location) getDefaultValue(propertyName));
    }

    @Nullable
    @Override
    public ItemStack[] getItemStacks(String propertyName) {
        PreCon.notNull(propertyName);

        return _dataNode.getItemStacks(propertyName, (ItemStack[]) getDefaultValue(propertyName));
    }

    @Nullable
    @Override
    public UUID getUUID(String propertyName) {
        PreCon.notNull(propertyName);

        return _dataNode.getUUID(propertyName, (UUID) getDefaultValue(propertyName));
    }

    @Nullable
    @Override
    public <T extends Enum<T>> T getEnum(String propertyName, Class<T> type) {
        PreCon.notNull(propertyName);
        PreCon.notNull(type);

        @SuppressWarnings("unchecked")
        T result = _dataNode.getEnum(propertyName, (T)getDefaultValue(propertyName), type);

        return result;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        _isDisposed = true;

        _agents.disposeAgents();
    }

    /**
     * Add a subscriber that is updated whenever a setting is changed.
     *
     * @param subscriber  The subscriber to add.
     */
    public void onChange(IUpdateSubscriber<PropertyValue> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onChange").addSubscriber(subscriber);
    }

    @Nullable
    private <T> T getDefaultValue(String propertyName) {
        PropertyDefinition definition = _definitions.get(propertyName);
        if (definition == null)
            return null;

        @SuppressWarnings("unchecked")
        T result = (T)definition.getDefaultValue();

        return result;
    }

    /**
     * Contains a property and a value for the property.
     */
    public static class PropertyValue {

        public final SettingsManager manager;
        public final PropertyDefinition definition;
        public final Object value;

        PropertyValue(SettingsManager manager, PropertyDefinition definition, @Nullable Object value) {
            this.manager = manager;
            this.definition = definition;
            this.value = value;
        }
    }
}
