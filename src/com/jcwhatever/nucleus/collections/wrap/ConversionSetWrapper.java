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

package com.jcwhatever.nucleus.collections.wrap;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * An abstract implementation of a {@link Set} wrapper
 * designed to convert between the encapsulated set element type and
 * an externally visible type. The wrapper is optionally synchronized via a sync
 * object or {@link java.util.concurrent.locks.ReadWriteLock} passed into the constructor.
 *
 * <p>The actual list is provided to the abstract implementation by
 * overriding and returning it from the {@link #set} method.</p>
 *
 * <p>The {@link #convert} and {@link #unconvert} abstract methods are used to
 * convert between the internal set element type and the external type.</p>
 *
 * <p>When using the {@link #unconvert} method, a {@link ClassCastException} can be
 * thrown to indicate the value cannot be converted. The exception is caught and handled
 * where it is appropriate to do so.</p>
 */
public abstract class ConversionSetWrapper<E, T>
        extends ConversionCollectionWrapper<E, T> implements Set<E> {

    /**
     * Constructor.
     */
    public ConversionSetWrapper() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public ConversionSetWrapper(@Nullable Object sync) {
        super(sync);
    }

    /**
     * Invoked from a synchronized block to get the encapsulated {@code Set}.
     */
    protected abstract Set<T> set();

    @Override
    protected final Collection<T> collection() {
        return set();
    }
}