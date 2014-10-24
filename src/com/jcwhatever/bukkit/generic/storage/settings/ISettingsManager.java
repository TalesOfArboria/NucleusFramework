package com.jcwhatever.bukkit.generic.storage.settings;

public interface ISettingsManager {
	
	SettingDefinitions getPossibleSettings();

	boolean isSetting(String property);

	ValidationResults set(String property, Object value);
	
	<T> T get(String property);

	<T> T get(String property, boolean unconvertValue);
	
	void addOnSettingsChanged(Runnable runnable, boolean run);

}
