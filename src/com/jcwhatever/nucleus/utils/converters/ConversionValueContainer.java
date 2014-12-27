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


package com.jcwhatever.nucleus.utils.converters;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Contains a value to be converted.
 *
 * <p>Also contains converters that have attempted to convert the value
 * already in order to prevent infinite loops within converters that
 * use other converters to obtain a value.</p>
 */
public class ConversionValueContainer {

    private ConversionValueContainer _parentContainer;
    private ValueConverter<?, ?> _parentConverter;
    private Set<ValueConverter<?, ?>> _converters;
    private Object _value;

    /**
     * Constructor.
     *
     * @param parentConverter  The converter creating the instance.
     * @param value            The value
     */
    public ConversionValueContainer(ValueConverter<?, ?> parentConverter, @Nullable Object value) {
        this(parentConverter, value, null);
    }

    /**
     * Constructor.
     *
     * @param parentConverter  The converter creating the instance.
     * @param value            The value.
     * @param container        The parent container.
     */
    public ConversionValueContainer(ValueConverter<?, ?> parentConverter, @Nullable Object value,
                                    @Nullable ConversionValueContainer container) {
        PreCon.notNull(parentConverter);

        _parentContainer = container;
        _value = value;

        addConverter(parentConverter);
    }

    /**
     * Get the value.
     */
    @Nullable
    public Object getValue() {
        return _value;
    }

    /**
     * Determine if a converter is already in the hierarchy of converters
     * that have handled the value conversion path.
     *
     * @param converter  The converter to check.
     */
    public boolean hasConverter(ValueConverter<?, ?> converter) {
        PreCon.notNull(converter);

        if (converter == _parentConverter)
            return true;

        ConversionValueContainer topContainer = this;
        while (topContainer._parentContainer != null) {
            topContainer = topContainer._parentContainer;
        }

        return topContainer._converters != null && topContainer._converters.contains(converter);
    }


    void addConverter(ValueConverter<?, ?> converter) {

        // set parent converter if one is not set
        if (_parentConverter == null) {
            _parentConverter = converter;
        }

        // use parent container values if parent container exists
        ConversionValueContainer topContainer = this;
        while (topContainer._parentContainer != null) {
            topContainer = topContainer._parentContainer;
        }

        // add to list of converters
        if (topContainer._converters == null)
            topContainer._converters = new HashSet<>(5);

        topContainer._converters.add(converter);
    }

}
