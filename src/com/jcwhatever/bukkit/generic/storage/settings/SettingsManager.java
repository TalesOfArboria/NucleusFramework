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
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SettingsManager extends AbstractSettingsManager {

	private IDataNode _settings;
	private SettingDefinitions _definitions;

	public SettingsManager() {
	}

	public SettingsManager (IDataNode settings, SettingDefinitions definitions) {
		_settings = settings;
		_definitions = definitions;
	}
	
	@Override
	public SettingDefinitions getPossibleSettings() {
		if (_definitions == null)
			return new SettingDefinitions();
		else
			return new SettingDefinitions(_definitions);
	}

	@Override
	public boolean isSetting(String property) {
        return _definitions != null && _definitions.containsKey(property);
	}

	@Override
    @SuppressWarnings("unchecked")
	public <T> T get(String property) {
		return (T)get(property, false);
	}


	@Override
	protected ValidationResults onSet(String property, Object value) {
		if (_settings == null) {
			return new ValidationResults(false, "Settings can't be saved because no data storage available.");
		}
				
		_settings.set(property, value);
		
		return _settings.save() ? ValidationResults.TRUE : ValidationResults.FALSE;
	}

	@Override
	protected Boolean getBoolean(String property, boolean defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getBoolean(property, defaultVal);
	}

	@Override
	protected Integer getInteger(String property, int defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getInteger(property, defaultVal);
	}

	@Override
	protected Long getLong(String property, long defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getLong(property, defaultVal);
	}

	@Override
	protected Double getDouble(String property, double defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getDouble(property, defaultVal);
	}

	@Override
	protected String getString(String property, String defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getString(property, defaultVal);
	}

	@Override
	protected Location getLocation(String property, Location defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getLocation(property, defaultVal);
	}

	@Override
	protected ItemStack[] getItemStacks(String property, ItemStack[] defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getItemStacks(property, defaultVal);
	}

	@Override
	protected UUID getUUID(String property, UUID defaultVal) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getUUID(property, defaultVal);
	}

	@Override
	protected Enum<?> getGenericEnum(String property, Enum<?> defaultVal, Class<Enum<?>> type) {
		if (_settings == null)
			return defaultVal;
		
		return _settings.getEnumGeneric(property, defaultVal, type);
	}

	@Override
	protected Object getObject(String property) {
		if (_settings == null)
			return null;
		
		return _settings.get(property);
	}


}
