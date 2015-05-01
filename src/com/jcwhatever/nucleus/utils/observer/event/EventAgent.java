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

package com.jcwhatever.nucleus.utils.observer.event;

import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * An event agent that {@link IEventSubscriber}'s can register with.
 */
public class EventAgent implements IEventAgent, IDisposable {

    private final Set<ISubscriber> _subscribers = new HashSet<>(3);
    private final List<IEventSubscriber> _eventSubscribers = new ArrayList<>(5);
    private boolean _isDisposed;

    @Override
    public synchronized void call(@Nullable Object caller, Object event) {

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed EventProducer");

        ICancellable cancellable = null;
        boolean isCancelled = false;
        Object callEvent;

        if (event instanceof ICancellable) {
            cancellable = (ICancellable)event;
            isCancelled = cancellable.isCancelled();
        }

        callEvent = event instanceof IEventWrapper ? ((IEventWrapper) event).getEvent() : event;

        List<IEventSubscriber> skippedSubscribers = new ArrayList<>(_eventSubscribers.size());
        ListIterator<IEventSubscriber> iterator = new ArrayList<>(_eventSubscribers).listIterator();

        while (iterator.hasNext()) {

            boolean preCancel = isCancelled;

            @SuppressWarnings("unchecked")
            IEventSubscriber<Object> subscriber = iterator.next();

            // check for cancelled event and determine if the subscriber
            // will still be notified of the event.
            if (subscriber.getPriority() != EventSubscriberPriority.WATCHER &&
                    isCancelled && !subscriber.isInvokedForCancelled()) {

                skippedSubscribers.add(subscriber);
                continue;
            }

            // notify the subscriber of the event
            subscriber.onEvent(caller, callEvent);

            // check if the event is cancelled
            if (cancellable != null) {
                isCancelled = cancellable.isCancelled();
            }

            boolean postCancel = isCancelled;

            // Make sure that WATCHER subscribers honor their priority type.
            if (postCancel != preCancel && subscriber.getPriority() == EventSubscriberPriority.WATCHER)
                throw new RuntimeException("Event subscribers with WATCHER priority " +
                        "cannot cancel or un-cancel an event.");

            // if cancelled event is uncancelled, run skipped subscribers next
            if (!preCancel && postCancel && !skippedSubscribers.isEmpty()) {
                for (IEventSubscriber skipped : skippedSubscribers) {
                    iterator.add(skipped);
                }
                skippedSubscribers.clear();
            }
        }
    }

    @Override
    public synchronized boolean addSubscriber(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        if (_subscribers.add(subscriber)) {
            subscriber.registerReference(this);

            if (subscriber instanceof IEventSubscriber) {
                _eventSubscribers.add((IEventSubscriber)subscriber);

                // sort subscribers
                Collections.sort(_eventSubscribers);
            }
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean removeSubscriber(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        if (_subscribers.remove(subscriber)) {
            subscriber.unregisterReference(this);
            //noinspection SuspiciousMethodCalls
            _eventSubscribers.remove(subscriber);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean registerReference(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        if (_subscribers.add(subscriber)) {
            if (subscriber instanceof IEventSubscriber)
                _eventSubscribers.add((IEventSubscriber)subscriber);

            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean unregisterReference(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        //noinspection SuspiciousMethodCalls
        _eventSubscribers.remove(subscriber);
        return _subscribers.remove(subscriber);
    }

    @Override
    public synchronized Set<ISubscriber> getSubscribers() {

        Set<ISubscriber> result = new HashSet<>(_eventSubscribers.size());
        for (IEventSubscriber subscriber : _eventSubscribers) {
            result.add(subscriber);
        }
        return result;
    }

    @Override
    public synchronized boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public synchronized void dispose() {
        if (_isDisposed)
            return;

        Set<ISubscriber> subscribers = getSubscribers();
        for (ISubscriber subscriber : subscribers) {
            subscriber.unregisterReference(this);
        }

        _eventSubscribers.clear();

        _isDisposed = true;
    }
}