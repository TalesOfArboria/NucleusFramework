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

package com.jcwhatever.nucleus.utils;

/**
 * Retrieve singleton instances per current thread.
 */
public class ThreadSingletons<S> extends ThreadLocal<S> {

    private final ISingletonFactory<S> _factory;

    /**
     * Constructor.
     *
     * @param factory  The singleton instance factory.
     */
    public ThreadSingletons(ISingletonFactory<S> factory) {
        PreCon.notNull(factory);

        _factory = factory;
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public void set(S value) {
        throw new UnsupportedOperationException("Cannot change singleton value.");
    }

    @Override
    protected S initialValue() {
        return _factory.create(Thread.currentThread());
    }

    /**
     * Use by extended implementations to set value.
     * @param value
     */
    protected void reset(S value) {
        super.set(value);
    }

    /**
     * Singleton instance factory.
     *
     * @param <S>  The singleton type.
     */
    public interface ISingletonFactory<S> {

        /**
         * Create a new singleton instance.
         *
         * @param thread  The thread the instance is being created for.
         */
        S create(Thread thread);
    }
}
