/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.generic.utils.converters;

import javax.annotation.Nullable;

/**
 * Abstract implementation of a value converter.
 *
 * @param <T>  The convert to value type.
 * @param <F>  The convert from value type.
 */
public abstract class ValueConverter<T, F> {

    private ConversionValueContainer _currentContainer;

    @Nullable
    public final T convert(@Nullable Object value) {

        if (value instanceof ConversionValueContainer) {
            _currentContainer = (ConversionValueContainer)value;

            if (_currentContainer.hasConverter(this))
                return null;

            _currentContainer.addConverter(this);
            value = _currentContainer.getValue();
        }
        else {
            _currentContainer = new ConversionValueContainer(this, value);
        }

        T result = onConvert(value);

        _currentContainer = null;

        return result;
    }

    @Nullable
    public final F unconvert(@Nullable Object value) {

        if (value instanceof ConversionValueContainer) {
            _currentContainer = (ConversionValueContainer)value;

            if (_currentContainer.hasConverter(this))
                return null;

            _currentContainer.addConverter(this);
            value = _currentContainer.getValue();
        }
        else {
            _currentContainer = new ConversionValueContainer(this, value);
        }

        F result = onUnconvert(value);

        _currentContainer = null;

        return result;
    }

    public ReversedConverter<F, T> getReverse() {
        return new ReversedConverter<F, T>(this);
    }

    @Nullable
    protected abstract T onConvert(@Nullable Object value);

    @Nullable
    protected abstract F onUnconvert(@Nullable Object value);

    @Nullable
    protected final <V> V callConvert(ValueConverter<V, ?> externalConverter, Object value) {
        ConversionValueContainer container = new ConversionValueContainer(this, value, _currentContainer);
        return externalConverter.convert(container);
    }

    @Nullable
    protected final <V> V callUnconvert(ValueConverter<?, V> externalConverter, Object value) {
        ConversionValueContainer container = new ConversionValueContainer(this, value, _currentContainer);
        return externalConverter.unconvert(container);
    }



}
