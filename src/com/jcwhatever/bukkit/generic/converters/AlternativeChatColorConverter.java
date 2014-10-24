package com.jcwhatever.bukkit.generic.converters;

import org.bukkit.ChatColor;

import javax.annotation.Nullable;

/**
 * Convert between chat color codes that use the '&' character and valid chat color codes.
 */
public class AlternativeChatColorConverter extends ValueConverter<String, String> {

	AlternativeChatColorConverter() {}

    /**
     * Convert chat color codes in a string that use the '&' character into valid chat color codes.
     *
     * @param value  The string to convert
     *
     * @return Null if a string is not provided.
     */
	@Override
    @Nullable
	protected String onConvert(Object value) {
		if (!(value instanceof String))
			return null;
		
		return ChatColor.translateAlternateColorCodes('&', (String)value);
	}

    /**
     * Convert valid chat color codes in a string into '&' codes;
     *
     * @param value  The string to convert
     *
     * @return  Null if a string is not provided.
     */
	@Override
    @Nullable
	protected String onUnconvert(Object value) {
		if (!(value instanceof String))
			return null;
		
		return ((String)value).replaceAll("\\�", "&");
	}

}
