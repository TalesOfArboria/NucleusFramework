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
import com.jcwhatever.nucleus.utils.coords.SyncLocation;
import com.jcwhatever.nucleus.managed.reflection.Reflection;

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
 *
 * <p>Only the following field types can be serialized: (Primitive wrappers are not supported)</p>
 *
 * <ul>
 *     <li>boolean</li>
 *     <li>byte</li>
 *     <li>short</li>
 *     <li>int</li>
 *     <li>long</li>
 *     <li>float</li>
 *     <li>double</li>
 *     <li>String</li>
 *     <li>instance of {@link Enum}</li>
 *     <li>{@link Location}</li>
 *     <li>{@link ItemStack}</li>
 *     <li>{@link ItemStack[]}</li>
 *     <li>instance of {@link IDataNodeSerializable}</li>
 * </ul>
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
     * <p>Ignores/skips over fields that do not have a corresponding value in the specified
     * data node. This includes values that were saved as null.</p>
     *
     * @param object    The object to deserialize data field values into.
     * @param dataNode  The data node where the values are stored.
     */
    public static void deserializeInto(Object object, IDataNode dataNode) {
        deserializeInto(object, dataNode, true);
    }

    /**
     * Deserialize data field values stored in a data node to the corresponding fields
     * in the specified object.
     *
     * @param object         The object to deserialize data field values into.
     * @param dataNode       The data node where the values are stored.
     * @param ignoreMissing  True to ignore missing/null nodes, false to set the field to
     *                       null or primitive default value.
     */
    public static void deserializeInto(Object object, IDataNode dataNode, boolean ignoreMissing) {

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

            if (ignoreMissing && !dataNode.hasNode(keyName))
                continue;

            if (Modifier.isFinal(field.getModifiers()))
                Reflection.removeFinal(field);

            boolean isStatic = Modifier.isStatic(field.getModifiers());

            try {
                setField(field, isStatic ? null : object, keyName, dataNode);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setField(Field field, Object object, String keyName, IDataNode dataNode)
            throws IllegalAccessException {

        Class<?> clazz = field.getType();

        Object defaultValue = field.get(object);

        if (clazz.equals(boolean.class)) {
            field.setBoolean(object, dataNode.getBoolean(keyName));
        }
        else if (clazz.equals(byte.class)) {
            field.setByte(object, (byte) dataNode.getInteger(keyName));
        }
        else if (clazz.equals(short.class)) {
            field.setShort(object, (short) dataNode.getInteger(keyName));
        }
        else if (clazz.equals(char.class)) {
            field.setChar(object, (char) dataNode.getInteger(keyName));
        }
        else if (clazz.equals(int.class)) {
            field.setInt(object, dataNode.getInteger(keyName));
        }
        else if (clazz.equals(long.class)) {
            field.setLong(object, dataNode.getLong(keyName));
        }
        else if (clazz.equals(float.class)) {
            field.setFloat(object, (float) dataNode.getDouble(keyName));
        }
        else if (clazz.equals(double.class)) {
            field.setDouble(object, dataNode.getDouble(keyName));
        }
        else if (clazz.equals(String.class)) {
            field.set(object, dataNode.getString(keyName));
        }
        else if (clazz.isEnum()) {
            //noinspection unchecked
            field.set(object, dataNode.getEnumGeneric(keyName, null, (Class<? extends Enum<?>>)clazz));
        }
        else if (clazz.equals(Location.class)) {
            SyncLocation syncLocation = dataNode.getLocation(keyName);
            field.set(object, syncLocation != null ? syncLocation.getBukkitLocation() : (Location) defaultValue);
        }
        else if (clazz.equals(ItemStack.class)) {
            ItemStack[] stacks = dataNode.getItemStacks(keyName);
            field.set(object, stacks != null && stacks.length > 0 ? stacks[0] : null);
        }
        else if (clazz.equals(ItemStack[].class)) {
            field.set(object, dataNode.getItemStacks(keyName));
        }
        else if (IDataNodeSerializable.class.isAssignableFrom(clazz)) {
            //noinspection unchecked
            Object result = dataNode.getSerializable(keyName, (Class<? extends IDataNodeSerializable>)clazz);
            field.set(object, result);
        }
    }

    private static boolean canSerialize(Class<?> clazz) {

        return clazz.equals(boolean.class) ||
                clazz.equals(byte.class) ||
                clazz.equals(char.class) ||
                clazz.equals(short.class) ||
                clazz.equals(int.class) ||
                clazz.equals(long.class) ||
                clazz.equals(float.class) ||
                clazz.equals(double.class) ||
                clazz.isEnum() ||
                clazz.equals(String.class) ||
                clazz.equals(Location.class) ||
                clazz.equals(ItemStack.class) ||
                clazz.equals(ItemStack[].class) ||
                IDataNodeSerializable.class.isAssignableFrom(clazz);
    }
}
