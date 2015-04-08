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

package com.jcwhatever.nucleus.internal.managed.reflection;

import com.jcwhatever.nucleus.managed.reflection.IReflectedField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IReflectedField}.
 */
class ReflectedField implements IReflectedField {

    private Field _field;
    private CachedReflectedType _type;
    private int _modifiers;

    /**
     * Constructor.
     *
     * @param type   The cached reflected type.
     * @param field  The field to encapsulate.
     */
    ReflectedField(CachedReflectedType type, Field field) {
        _type = type;
        _field = field;
        _modifiers = field.getModifiers();

        field.setAccessible(true);
    }

    @Override
    public ReflectedType getReflectedType() {

        CachedReflectedType type = ReflectionContext._typeCache.get(_field.getType());
        if (type == null) {

            type = new CachedReflectedType(_field.getType());
            ReflectionContext._typeCache.put(_field.getType(), type);
        }

        return new ReflectedType(type);
    }

    @Override
    public String getName() {
        return _field.getName();
    }

    @Override
    public int getModifiers() {
        return _modifiers;
    }

    @Override
    public int getCurrentModifiers() {
        return _field.getModifiers();
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(_modifiers);
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(_modifiers);
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(_modifiers);
    }

    @Override
    public boolean isNative() {
        return Modifier.isNative(_modifiers);
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(_modifiers);
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(_modifiers);
    }

    @Override
    public boolean isStrict() {
        return Modifier.isStrict(_modifiers);
    }

    @Override
    public boolean isTransient() {
        return Modifier.isTransient(_modifiers);
    }

    @Override
    public boolean isVolatile() {
        return Modifier.isVolatile(_modifiers);
    }

    @Override
    public Object get(@Nullable Object instance) {
        try {
            return _field.get(instance);
        } catch (IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
            if (instance != null) {
                throw new RuntimeException("Failed to get field value. The field might be static.");
            }
            else {
                throw new RuntimeException("Failed to get field value. The field might not be static.");
            }
        }
    }

    @Override
    public void set(@Nullable Object instance, @Nullable Object value) {
        try {
            _field.set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set field value.");
        }
    }

    @Override
    public Field getHandle() {
        try {
            return _type.getHandle().getField(_field.getName());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get field.");
        }
    }
}
