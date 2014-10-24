package com.jcwhatever.bukkit.generic.converters;

import com.jcwhatever.bukkit.generic.utils.PreCon;

public class PrimitivesConverter {

	public static <T> T getPrimitive(Class<T> primitiveClass, String value) {
		PreCon.notNull(primitiveClass);
		PreCon.notNull(value);
		
		if (String.class.isAssignableFrom(primitiveClass))
			return primitiveClass.cast(value);

		if (!primitiveClass.isPrimitive())
			throw new IllegalArgumentException("primitiveClass must be a primitive type.");

        // Boolean
		if (Boolean.class.isAssignableFrom(primitiveClass)) {
			try {
				Boolean b = Boolean.parseBoolean(value);
				return primitiveClass.cast(b);
			}
			catch (Exception e) {
				return null;
			}
		}

		// Byte
		if (Byte.class.isAssignableFrom(primitiveClass)) {

			try {
				Byte b = Byte.parseByte(value);
				return primitiveClass.cast(b);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}

		// Short
		if (Short.class.isAssignableFrom(primitiveClass)) {
			try {
				Short s = Short.parseShort(value);
				return primitiveClass.cast(s);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}

		// Integer
		if (Integer.class.isAssignableFrom(primitiveClass)) {
			try {
				Integer i = Integer.parseInt(value);
				return primitiveClass.cast(i);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}

		// Long
		if (Long.class.isAssignableFrom(primitiveClass)) {
			try {
				Long l = Long.parseLong(value);
				return primitiveClass.cast(l);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}

		// Double
		if (Double.class.isAssignableFrom(primitiveClass)) {
			try {
				Double d = Double.parseDouble(value);
				return primitiveClass.cast(d);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}

		// Float

		if (Float.class.isAssignableFrom(primitiveClass)) {
			try {
				Float f = Float.parseFloat(value);
				return primitiveClass.cast(f);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}

		// Char
		if (Character.class.isAssignableFrom(primitiveClass)) {
			try {
				Character c = value.charAt(0);
				return primitiveClass.cast(c);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		}


		return null;

	}

}
