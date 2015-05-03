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

import org.bukkit.Bukkit;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Retrieve singleton instances per current thread.
 */
public class ThreadSingletons<S> {

    private final S _mainSingleton;
    private final ISingletonFactory<S> _factory;
    private Map<Thread, S> _singletons;

    /**
     * Constructor.
     *
     * @param factory  The singleton instance factory.
     */
    public ThreadSingletons(ISingletonFactory<S> factory) {
        PreCon.notNull(factory);

        _factory = factory;
        _mainSingleton = factory.create();
    }

    /**
     * Get the singleton instance for the current thread.
     */
    public S get() {

        if (Bukkit.isPrimaryThread())
            return _mainSingleton;

        if (_singletons == null)
            _singletons = new WeakHashMap<>(10);

        Thread thread = Thread.currentThread();

        S singleton = _singletons.get(thread);

        if (singleton == null) {
            singleton = _factory.create();
            _singletons.put(Thread.currentThread(), singleton);
        }

        return singleton;
    }

    /**
     * Singleton instance factory.
     *
     * @param <S>  The singleton type.
     */
    public interface ISingletonFactory<S> {

        /**
         * Create a new singleton instance.
         */
        S create();
    }
}
