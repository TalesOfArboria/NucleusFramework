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

package com.jcwhatever.nucleus.utils.observer.update;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * An agent between update producers and the agents registered
 * {@link IUpdateSubscriber}'s.
 */
public class UpdateAgent<A> implements IUpdateAgent<A>, IDisposable {

    private Set<ISubscriber> _subscribers;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>The internal set that stores subscribers is lazy initialized.</p>
     */
    public UpdateAgent() {}

    /**
     * Constructor.
     *
     * @param capacity  The initial capacity of the internal set that stores subscribers.
     */
    public UpdateAgent(int capacity) {
        _subscribers = new HashSet<>(capacity);
    }

    @Override
    public synchronized void update(@Nullable A argument) {

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed UpdateSubject");

        List<ISubscriber> list = new ArrayList<>(subscribers());

        for (ISubscriber subscriber : list) {

            if (subscriber instanceof IUpdateSubscriber) {

                @SuppressWarnings("unchecked")
                IUpdateSubscriber<A> updateable = (IUpdateSubscriber<A>) subscriber;

                updateable.on(argument);
            }
        }
    }

    @Override
    public synchronized boolean addSubscriber(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed UpdateSubject");

        if (subscribers().add(subscriber)) {
            subscriber.registerReference(this);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeSubscriber(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        if (_subscribers == null)
            return false;

        if (_subscribers.remove(subscriber)) {
            subscriber.unregisterReference(this);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean registerReference(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        return subscribers().add(subscriber);
    }

    @Override
    public synchronized boolean unregisterReference(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        return _subscribers != null && _subscribers.remove(subscriber);
    }

    @Override
    public synchronized Set<ISubscriber> getSubscribers() {
        if (_subscribers == null)
            return new HashSet<>(0);

        return new HashSet<>(_subscribers);
    }

    @Override
    public synchronized boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public synchronized void dispose() {
        if (_isDisposed)
            return;

        if (_subscribers != null) {
            Set<ISubscriber> subscribers = getSubscribers();
            for (ISubscriber subscriber : subscribers) {
                subscriber.unregisterReference(this);
            }

            _subscribers.clear();
        }

        _isDisposed = true;
    }

    private Set<ISubscriber> subscribers() {

        if (_subscribers == null) {
            _subscribers = new HashSet<>(5);
        }

        return _subscribers;
    }
}
