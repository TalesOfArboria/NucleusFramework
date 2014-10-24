package com.jcwhatever.bukkit.generic.storage.settings;

import com.jcwhatever.bukkit.generic.converters.ValueConverter;

public class SettingDefinition implements Comparable<SettingDefinition> {
	
	private final String _configName;
	private final Class<?> _type;
	private final String _descr;
	private Object _defaultVal;
	private boolean _hasDefaultVal;
	private ValueConverter<?, ?> _converter;
	private SettingValidator _validator;
	
	public SettingDefinition(String configName, Class<?> type, ValueConverter<?, ?> converter) {
		_configName = configName;
		_type = type;
		_descr = "";
		_converter = converter;
	}
	
	public SettingDefinition(String configName, Class<?> type, ValueConverter<?, ?> converter, SettingValidator validator) {
		_configName = configName;
		_type = type;
		_descr = "";
		_converter = converter;
		_validator = validator;
	}
	
	public SettingDefinition(String configName, Object defaultVal, Class<?> type, ValueConverter<?, ?> converter) {
		_configName = configName;
		_defaultVal = defaultVal;
		_type = type;
		_descr = "";
		_converter = converter;
		_hasDefaultVal = true;
	}
	
	public SettingDefinition(String configName, Object defaultVal, Class<?> type, ValueConverter<?, ?> converter, SettingValidator validator) {
		_configName = configName;
		_defaultVal = defaultVal;
		_type = type;
		_descr = "";
		_converter = converter;
		_validator = validator;
		_hasDefaultVal = true;
	}
	
	public SettingDefinition(String configName, Class<?> type, String descr, ValueConverter<?, ?> converter) {
		_configName = configName;
		_type = type;
		_descr = descr;
		_converter = converter;
	}
	
	public SettingDefinition(String configName, Class<?> type, String descr, ValueConverter<?, ?> converter, SettingValidator validator) {
		_configName = configName;
		_type = type;
		_descr = descr;
		_converter = converter;
		_validator = validator;
	}
	
	public SettingDefinition(String configName, Object defaultVal, Class<?> type, String descr, ValueConverter<?, ?> converter) {
		_configName = configName;
		_defaultVal = defaultVal;
		_type = type;
		_descr = descr;
		_converter = converter;
		_hasDefaultVal = true;
	}
	
	public SettingDefinition(String configName, Object defaultVal, Class<?> type, String descr, ValueConverter<?, ?> converter, SettingValidator validator) {
		_configName = configName;
		_defaultVal = defaultVal;
		_type = type;
		_descr = descr;
		_converter = converter;
		_validator = validator;
		_hasDefaultVal = true;
	}
	
	public String getConfigName() {
		return _configName;
	}
	
	public Class<?> getValueType() {
		return _type;
	}
	
	public Object getDefaultVal() {
		return _defaultVal;
	}
	
	public boolean hasDefaultVal() {
		return _hasDefaultVal;
	}
	
	public String getDescription() {
		return _descr;
	}
	
	public ValueConverter<?, ?> getValueConverter() {
		return _converter;
	}
	
	public void setValueConverter(ValueConverter<?, ?> converter) {
		_converter = converter;
	}
	
	public SettingValidator getValidator() {
		return _validator;
	}
	
	public void setValidator(SettingValidator validator) {
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
		return getConfigName().compareTo(o.getConfigName());
	}
}
