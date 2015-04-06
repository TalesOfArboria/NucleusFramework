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

package com.jcwhatever.nucleus.storage.serialize;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.reflection.ReflectionUtils;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Uses reflection to load and save fields from an object into an {@link IDataNode}.
 *
 * <p>The fields that can be serialized must be annotated with {@link DataField}.</p>
 *
 * <p>Reduces boilerplate code needed to load and save an objects settings at
 * the cost of performance.</p>
 */
public final class DataFieldSerializer {

    private DataFieldSerializer() {}

    /**
     * Serialize an object with fields annotated with {@link DataField} to a
     * data node.
     *
     * @param object    The object to serialize.
     * @param dataNode  The data node to store the objects data fields in.
     */
    public static void serialize(Object object, IDataNode dataNode) {
        Class<?> clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            DataField dataValue = field.getAnnotation(DataField.class);
            if (dataValue == null)
                continue;

            String keyName = dataValue.keyName();
            if (keyName.isEmpty())
                keyName = field.getName();

            Class<?> fieldType = field.getType();


            if (!canSerialize(fieldType)) {
                throw new RuntimeException(
                        "Cannot serialize field type: " + fieldType.getName() + " in class: " + clazz.getName());
            }

            boolean isSet;

            try {
                isSet = dataNode.set(keyName, field.get(object));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new AssertionError("Unexpected exception.");
            }

            if (!isSet) {
                throw new RuntimeException(
                        "Failed to serialize field type: " + fieldType.getName() + " in class:" + clazz.getName() +
                                " using IDataNode implementation: " + dataNode.getClass().getName());
            }
        }
    }

    /**
     * Deserialize data field values stored in a data node to the corresponding fields
     * in the specified object.
     *
     * @param object    The object to deserialize data field values into.
     * @param dataNode  The data node where the values are stored.
     */
    public static void deserializeInto(Object object, IDataNode dataNode) {

        Class<?> clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            DataField dataValue = field.getAnnotation(DataField.class);
            if (dataValue == null)
                continue;

            String keyName = dataValue.keyName();
            if (keyName.isEmpty())
                keyName = field.getName();

            if (Modifier.isFinal(field.getModifiers()))
                ReflectionUtils.removeFinal(field);

            try {
                setField(field, object, keyName, dataNode);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setField(Field field, Object object, String keyName, IDataNode dataNode)
            throws IllegalAccessException {

        Class<?> clazz = field.getType();

        Object defaultValue = field.get(object);

        if (clazz.isAssignableFrom(boolean.class) || clazz.isAssignableFrom(Boolean.class)) {
            field.set(object, dataNode.getBoolean(keyName, (boolean) defaultValue));
        }
        else if (clazz.isAssignableFrom(byte.class) || clazz.isAssignableFrom(Byte.class)) {
            field.set(object, (byte)dataNode.getInteger(keyName, (byte)defaultValue));
        }
        else if (clazz.isAssignableFrom(short.class) || clazz.isAssignableFrom(Short.class)) {
            field.set(object, (short)dataNode.getInteger(keyName, (short)defaultValue));
        }
        else if (clazz.isAssignableFrom(char.class) || clazz.isAssignableFrom(Character.class)) {
            field.set(object, (char)dataNode.getInteger(keyName, (char)defaultValue));
        }
        else if (clazz.isAssignableFrom(int.class) || clazz.isAssignableFrom(Integer.class)) {
            field.set(object, dataNode.getInteger(keyName, (int) defaultValue));
        }
        else if (clazz.isAssignableFrom(long.class) || clazz.isAssignableFrom(Long.class)) {
            field.set(object, dataNode.getLong(keyName, (long)defaultValue));
        }
        else if (clazz.isAssignableFrom(float.class) || clazz.isAssignableFrom(Float.class)) {
            field.set(object, (float)dataNode.getDouble(keyName, (float)defaultValue));
        }
        else if (clazz.isAssignableFrom(double.class) || clazz.isAssignableFrom(Double.class)) {
            field.set(object, dataNode.getDouble(keyName, (double)defaultValue));
        }
        else if (clazz.isAssignableFrom(String.class)) {
            field.set(object, dataNode.getString(keyName, (String)defaultValue));
        }
        else if (clazz.isAssignableFrom(Enum.class)) {
            //noinspection unchecked
            field.set(object, dataNode.getEnumGeneric(keyName, (Enum) defaultValue, (Class<? extends Enum<?>>)clazz));
        }
        else if (clazz.isAssignableFrom(Location.class)) {
            field.set(object, dataNode.getLocation(keyName, (Location) defaultValue));
        }
        else if (clazz.isAssignableFrom(ItemStack.class)) {
            ItemStack[] stacks = dataNode.getItemStacks(keyName, (ItemStack) defaultValue);
            field.set(object, stacks != null && stacks.length > 0 ? stacks[0] : null);
        }
        else if (clazz.isAssignableFrom(ItemStack[].class)) {
            field.set(object, dataNode.getItemStacks(keyName, (ItemStack[])defaultValue));
        }
        else if (clazz.isAssignableFrom(IDataNodeSerializable.class)) {
            //noinspection unchecked
            Object result = dataNode.getSerializable(keyName, (Class<? extends IDataNodeSerializable>)clazz);
            if (result != null)
                field.set(object, result);
        }
    }

    private static boolean canSerialize(Class<?> clazz) {

        return clazz.isAssignableFrom(boolean.class) ||
                clazz.isAssignableFrom(Boolean.class) ||
                clazz.isAssignableFrom(byte.class) ||
                clazz.isAssignableFrom(Byte.class) ||
                clazz.isAssignableFrom(char.class) ||
                clazz.isAssignableFrom(Character.class) ||
                clazz.isAssignableFrom(short.class) ||
                clazz.isAssignableFrom(Short.class) ||
                clazz.isAssignableFrom(int.class) ||
                clazz.isAssignableFrom(Integer.class) ||
                clazz.isAssignableFrom(long.class) ||
                clazz.isAssignableFrom(Long.class) ||
                clazz.isAssignableFrom(float.class) ||
                clazz.isAssignableFrom(Float.class) ||
                clazz.isAssignableFrom(double.class) ||
                clazz.isAssignableFrom(Double.class) ||
                clazz.isAssignableFrom(String.class) ||
                clazz.isAssignableFrom(Enum.class) ||
                clazz.isAssignableFrom(Location.class) ||
                clazz.isAssignableFrom(ItemStack.class) ||
                clazz.isAssignableFrom(ItemStack[].class) ||
                clazz.isAssignableFrom(IDataNodeSerializable.class);
    }
}
