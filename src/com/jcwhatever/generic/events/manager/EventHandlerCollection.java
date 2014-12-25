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


package com.jcwhatever.generic.events.manager;

import com.jcwhatever.generic.mixins.ICancellable;

import org.bukkit.event.Cancellable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * A collection of event handlers for a specific event.
 */
class EventHandlerCollection {

    private final List<HandlerContainer> _handlers;

    /**
     * Constructor.
     */
    public EventHandlerCollection() {
        _handlers = new ArrayList<HandlerContainer>(20);
    }

    /**
     * Call all event handlers and pass in the provided event instance.
     *
     * @param event  The event instance.
     * @param <T>    The event type.
     */
    <T> T call(T event) {

        ICancellable cancellable = event instanceof ICancellable
                ? (ICancellable)event
                : null;

        Cancellable bukkitCancellable = event instanceof Cancellable
                ? (Cancellable)event
                : null;

        LinkedList<HandlerContainer> skipped = new LinkedList<>();
        List<HandlerContainer> handlers = new ArrayList<>(_handlers);
        // iterate handlers and call them
        for (HandlerContainer handler : handlers) {

            boolean isPreCancelled = (cancellable != null && cancellable.isCancelled()) ||
                    (bukkitCancellable != null && bukkitCancellable.isCancelled());

            // skip handler if the even is cancelled and it is not a watcher.
            if (isPreCancelled && handler.getPriority() != GenericsEventPriority.WATCHER &&
                    !handler.isCancelIgnored()) {

                skipped.add(handler);
                continue;
            }

            // call the handler
            if (!tryCall(handler, event))
                continue;

            // # Run skipped handlers if the event is uncancelled.

            // check if the event is cancelled
            boolean isPostCancelled = (cancellable != null && cancellable.isCancelled()) ||
                    (bukkitCancellable != null && bukkitCancellable.isCancelled());

            if (handler._priority == GenericsEventPriority.WATCHER &&
                    isPreCancelled != isPostCancelled) {
                throw new RuntimeException("You cannot change the state of an event using an " +
                                           "event handler with WATCHER priority.");
            }

            // determine if the event was cancelled during the last handler call.
            if (isPreCancelled && !isPostCancelled) {

                // run handlers that were skipped
                while (!skipped.isEmpty()) {
                    HandlerContainer skippedHandler = skipped.remove();

                    tryCall(skippedHandler, event);
                }
            }
        }

        return event;
    }

    /**
     * Clear all handlers
     */
    void clear() {
        _handlers.clear();
    }

    /**
     * Add an event handler.
     *
     * @param eventHandler  The event handler to add.
     * @param priority      The handler priority.
     *
     * @return  True if the handler was added, False if it's already added.
     */
    boolean add(IEventHandler eventHandler,  GenericsEventPriority priority) {

        return add(eventHandler, priority, false);
    }

    /**
     * Add an event handler.
     *
     * @param eventHandler     The event handler to add.
     * @param priority         The handler priority.
     * @param ignoreCancelled  True to run handler even if event is already cancelled.
     *
     * @return  True if the handler was added, False if it's already added.
     */
    boolean add(IEventHandler eventHandler,  GenericsEventPriority priority, boolean ignoreCancelled) {

        // create handler container and make sure it is not already added.
        HandlerContainer handler = new HandlerContainer(eventHandler, priority, ignoreCancelled);
        if (_handlers.contains(handler))
            return false;

        // add handler
        _handlers.add(handler);

        // sort handlers by priority
        Collections.sort(_handlers);

        return true;
    }

    /**
     * Add an event handler that was retrieved from a {@code GenericsEventListener}.
     *
     * @param listener    The {@code GenericsEventListener} the handler is from.
     * @param method      The reflection {@code Method} that points to the handler method.
     * @param annotation  The handler methods {@code GenericsEventHandler} annotation.
     *
     * @throws IllegalAccessException
     */
    void add(final IEventListener listener,
             final Method method, GenericsEventHandler annotation) throws IllegalAccessException {

        // make the possibly private method accessible
        // and turn off accessibility checks
        method.setAccessible(true);

        // create an event handler that can be used to call the method.
        IEventHandler handler = new IEventHandler() {
            @Override
            public void handle(Object event) {
                try {
                    method.invoke(listener, event);
                    //methodHandle.invoke();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        };

        // add event handler container
        _handlers.add(new HandlerContainer(listener, handler, annotation.priority(), annotation.ignoreCancelled()));

        // sort event handlers by priority
        Collections.sort(_handlers);
    }

    /**
     * Remove a listener and all event handlers associated with it.
     *
     * @param listener  The listener to remove.
     */
    void removeListener(IEventListener listener) {

        // iterate event handler containers
        Iterator<HandlerContainer> iterator = _handlers.iterator();
        while (iterator.hasNext()) {
            HandlerContainer handler = iterator.next();

            // remove handler if it is from the listener
            if (listener.equals(handler.getListener())) {
                iterator.remove();
            }
        }
    }

    /**
     * Remove a handler by instance.
     *
     * @param eventHandler  The event handler to remove.
     */
    void removeHandler(IEventHandler eventHandler) {
        HandlerContainer handler = new HandlerContainer(eventHandler);
        _handlers.remove(handler);
    }

    /*
     * Call an event on an event handler
     */
    private <T> boolean tryCall(HandlerContainer handler, T event) {
        try {
            //noinspection unchecked
            handler.getHandler().handle(event);
            return true;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    /**
     * An event handler container that provides sorting by priority.
     */
    private static class HandlerContainer implements Comparable<HandlerContainer> {

        private final IEventListener _listener;
        private final IEventHandler _handler;
        private final GenericsEventPriority _priority;
        private final boolean _ignoreCancelled;

        /**
         * Constructor.
         */
        public HandlerContainer(IEventListener listener, IEventHandler handler,
                                GenericsEventPriority priority, boolean ignoreCancelled) {
            _listener = listener;
            _handler = handler;
            _priority = priority;
            _ignoreCancelled = ignoreCancelled;
        }

        /**
         * Constructor.
         */
        public HandlerContainer(IEventHandler handler, GenericsEventPriority priority, boolean ignoreCancelled) {
            _handler = handler;
            _listener = null;
            _priority = priority;
            _ignoreCancelled = ignoreCancelled;
        }

        /**
         * Private Constructor. Only used to create a map key.
         */
        private HandlerContainer(IEventHandler handler) {
            _handler = handler;
            _listener = null;
            _priority = null;
            _ignoreCancelled = false;
        }

        /**
         * Get the listener the event handler is from.
         *
         * @return  Null if the handler was registered without a listener.
         */
        @Nullable
        public IEventListener getListener() {
            return _listener;
        }

        /**
         * Get the encapsulated event handler.
         */
        public IEventHandler getHandler() {
            return _handler;
        }

        /**
         * Get the event priority.
         */
        public GenericsEventPriority getPriority() {
            return _priority;
        }

        /**
         * Determine if handler runs even if the
         * event is already cancelled.
         */
        public boolean isCancelIgnored() {
            return _ignoreCancelled;
        }

        /**
         * Compare by event priority.
         */
        @Override
        public int compareTo(HandlerContainer o) {
            return Integer.compare(_priority.getSortOrder(), o.getPriority().getSortOrder());
        }

        @Override
        public int hashCode() {
            return _handler.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof HandlerContainer && ((HandlerContainer) obj).getHandler().equals(_handler);
        }
    }

}
