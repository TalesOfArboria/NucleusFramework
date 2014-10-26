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
