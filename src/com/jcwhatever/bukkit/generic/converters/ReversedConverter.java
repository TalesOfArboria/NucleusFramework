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


package com.jcwhatever.bukkit.generic.converters;

/**
 * A wrapper for a {@code ValueConverter} instance that reverses the
 * convert and unconvert methods.
 *
 * @param <F>  The parent {@code ValueConverter}'s unconvert return value type
 * @param <T>  The parent {@code ValueConverter}'s convert return value type
 */
public class ReversedConverter<F, T> extends ValueConverter<F, T> {

    private ValueConverter<T, F> _parentConverter;

    /**
     * Constructor.
     *
     * @param parentConverter  The parent converter to wrap.
     */
    ReversedConverter(ValueConverter<T, F> parentConverter) {
        _parentConverter = parentConverter;
    }

    /**
     * Convert using the parent converters unconvert method.
     */
    @Override
    protected F onConvert(Object value) {
        return callUnconvert(_parentConverter, value);
    }

    /**
     * Unconvert using the parent converters convert method.
     */
    @Override
    protected T onUnconvert(Object value) {
        return callConvert(_parentConverter, value);
    }

    /**
     * Get the wrapped parent value converter.
     */
    public ValueConverter<T, F> getParent() {
        return _parentConverter;
    }

}
