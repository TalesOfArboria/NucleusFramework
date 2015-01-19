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
 * An abstract implementation of a synchronized {@link Set} wrapper. The wrapper is
 * optionally synchronized via a sync object or {@link java.util.concurrent.locks.ReadWriteLock}
 * passed in through the constructor.
 *
 * <p>The actual collection is provided to the abstract implementation by
 * overriding and returning it from the {@link #set} method.</p>
 *
 * <p>In order to make using the wrapper as an extension of a collection easier,
 * several protected methods are provided for optional override. These methods
 * are provided by the superclass {@link CollectionWrapper}.
 */
public abstract class SetWrapper<E> extends CollectionWrapper<E> implements Set<E> {

    /**
     * Constructor.
     */
    public SetWrapper() {
        super(null);
    }

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public SetWrapper(@Nullable Object sync) {
        super(sync);
    }

    /**
     * Invoked from a synchronized block to get the
     * encapsulated {@code Set}.
     */
    protected abstract Set<E> set();

    @Override
    protected Collection<E> collection() {
        return set();
    }
}
