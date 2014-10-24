package com.jcwhatever.bukkit.generic.utils;

public class EnumUtils {


	public static <T extends Enum<T>> T getEnum(String constantName, Class<T> enumClass) {
		return getEnum(constantName, null, enumClass);
	}


	public static <T extends Enum<T>> T getEnum(String constantName, T def, Class<T> enumClass) {
		if (constantName != null && !constantName.isEmpty()) {
			T value;
			try {
				value = Enum.valueOf(enumClass, constantName);
			}
			catch (Exception e) {
				return def;
			}
			return value;
		}
		return def;
	}


	public static Enum<?> getGenericEnum(String constantName, Enum<?> def, Class<? extends Enum<?>> enumClass) {
		if (constantName != null && !constantName.isEmpty()) {

			Enum<?>[] constants = enumClass.getEnumConstants();

			for (Enum<?> constant : constants) {
				if (constant.name().equalsIgnoreCase(constantName))
					return constant;
			}
		}
		return def;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Enum getRawEnum(String constantName, Enum def, Class enumClass) {
		
		if (!enumClass.isEnum())
			throw new IllegalArgumentException("enumClass must be an enum class.");
		
		if (constantName != null && !constantName.isEmpty()) {

			for (Object constant : enumClass.getEnumConstants()) {
				if (constant instanceof Enum) {
					if (((Enum) constant).name().equalsIgnoreCase(constantName)) {
						return (Enum)constant;
					}
				}
			}
		}
		return def;
	}
	
}
