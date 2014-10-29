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

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.sun.istack.internal.Nullable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * {@code ISettingsManager} implementation.
 */
public class SettingsManager extends AbstractSettingsManager {

    private IDataNode _dataNode;
    private SettingDefinitions _definitions;

    /**
     * Constructor.
     */
    public SettingsManager() {
        super();
    }

    /**
     * Constructor.
     *
     * @param dataNode     The data node being managed.
     * @param definitions  The setting definitions that define possible settings.
     */
    public SettingsManager (IDataNode dataNode, @Nullable SettingDefinitions definitions) {
        super();
        PreCon.notNull(dataNode);

        _dataNode = dataNode;
        _definitions = definitions;
    }

    @Override
    public SettingDefinitions getPossibleSettings() {
        return _definitions == null
                ? new SettingDefinitions()
                : new SettingDefinitions(_definitions);
    }

    @Override
    public boolean isSetting(String settingName) {
        return _definitions != null && _definitions.containsKey(settingName);
    }

    @Override
    public <T> T get(String settingName) {
        return get(settingName, false);
    }

    @Override
    protected ValidationResults onSet(String settingName, Object value) {
        if (_dataNode == null) {
            return new ValidationResults(false, "Settings can't be saved because no data storage available.");
        }

        _dataNode.set(settingName, value);

        return _dataNode.save() ? ValidationResults.TRUE : ValidationResults.FALSE;
    }

    @Override
    protected Boolean getBoolean(String settingName, boolean defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getBoolean(settingName, defaultVal);
    }

    @Override
    protected Integer getInteger(String settingName, int defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getInteger(settingName, defaultVal);
    }

    @Override
    protected Long getLong(String settingName, long defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getLong(settingName, defaultVal);
    }

    @Override
    protected Double getDouble(String settingName, double defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getDouble(settingName, defaultVal);
    }

    @Override
    protected String getString(String settingName, String defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getString(settingName, defaultVal);
    }

    @Override
    protected Location getLocation(String settingName, Location defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getLocation(settingName, defaultVal);
    }

    @Override
    protected ItemStack[] getItemStacks(String settingName, ItemStack[] defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getItemStacks(settingName, defaultVal);
    }

    @Override
    protected UUID getUUID(String settingName, UUID defaultVal) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getUUID(settingName, defaultVal);
    }

    @Override
    protected Enum<?> getGenericEnum(String settingName, Enum<?> defaultVal, Class<Enum<?>> type) {
        if (_dataNode == null)
            return defaultVal;

        return _dataNode.getEnumGeneric(settingName, defaultVal, type);
    }

    @Override
    protected Object getObject(String settingName) {
        if (_dataNode == null)
            return null;

        return _dataNode.get(settingName);
    }
}
