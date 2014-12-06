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


package com.jcwhatever.bukkit.generic.events;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.TimeScale;
import com.jcwhatever.bukkit.generic.collections.TimedHashSet;
import com.jcwhatever.bukkit.generic.events.exceptions.EventManagerDisposedException;
import com.jcwhatever.bukkit.generic.events.exceptions.HandlerAlreadyRegisteredException;
import com.jcwhatever.bukkit.generic.events.exceptions.ListenerAlreadyRegisteredException;
import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.utils.DateUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Generics event manager.
 *
 * <p>Generics events are primarily intended for use in a mostly self contained
 * system with many contexts that benefit from each having their own event manager. This
 * can reduce code and the number of checks by guaranteeing events for a specific event
 * manager are only called in response to a specific context. In can also potentially improve
 * performance by reducing the number of event handlers called to the ones that are subscribed
 * to the specific context of the event manager.</p>
 *
 * <p>The Generics event manager can take any type as an event including Bukkit events.
 * It can also have a parent manager that receives calls made to the child manager.
 * By default, the global Generics event manager is the parent manager, however
 * a different parent manager or none at all can be set.</p>
 *
 * <p>The global Generics event manager also receives certain Bukkit events so event handlers
 * can be used that subscribe to those Bukkit events.</p>
 *
 * <p>Event managers cannot receive the same event more than once. This is because of the combination of event
 * forwarding to specific manager contexts and event bubbling. It is possible to forward an event from an event
 * manager that is higher in the hierarchy to a manager that is lower, which would then bubble to the
 * manager that forwarded the event in the first place. To prevent this managers will drop calls to event
 * instances that they have already called.</p>
 *
 * <p>In order to prevent undesired dropping of events, the event type should not override the equals method, or
 * if it does, should compare instances using ==.</p>
 *
 */
public class GenericsEventManager implements IDisposable {

    private final Map<Class<?>, EventHandlerCollection> _handlerMap = new HashMap<>(100);
    private final Map<IGenericsEventListener, ListenerContainer> _listeners = new HashMap<>(100);
    private final List<IEventHandler> _callHandlers = new ArrayList<>(10);
    private final GenericsEventManager _parent;
    private boolean _isDisposed;

    private Map<Object, Void> _calledEvents = new WeakHashMap<>(30);

    // BUG WORKAROUND interact event getting called twice - Minecraft/Bukkit/Spigot issue
    private TimedHashSet<EventWrapper> _recentBukkitEvents = new TimedHashSet<>(20, 3, TimeScale.MILLISECONDS);

    /**
     * Constructor.
     *
     * <p>Create a new event manager using the global event manager as parent.</p>
     */
    public GenericsEventManager() {
        this(GenericsLib.getEventManager());
    }

    /**
     * Constructor.
     *
     * @param parent  The parent event manager. The parent receives all
     *                event calls the child receives.
     */
    public GenericsEventManager(@Nullable GenericsEventManager parent) {
        _parent = parent;
    }

    /**
     * Register an event handler for the specified event.
     *
     * @param eventClass  The event class.
     * @param priority    The event priority.
     * @param handler     The event handler.
     */
    public void register(Class<?> eventClass,
                         GenericsEventPriority priority, IEventHandler handler) {
        register(eventClass, priority, false, handler);
    }

    /**
     * Register an event handler for the specified event.
     *
     * @param eventClass       The event class.
     * @param priority         The event priority.
     * @param ignoreCancelled  True to run the handler event if the event is cancelled.
     * @param handler          The event handler.
     */
    public void register(Class<?> eventClass, GenericsEventPriority priority,
                         boolean ignoreCancelled, IEventHandler handler) {
        PreCon.notNull(eventClass);
        PreCon.notNull(priority);
        PreCon.notNull(handler);

        // cannot use a disposed event manager
        if (_isDisposed)
            throw new EventManagerDisposedException();

        // get event handler collection for the event
        EventHandlerCollection handlers =_handlerMap.get(eventClass);

        // add an event handler collection if one does not exist
        if (handlers == null) {
            handlers = new EventHandlerCollection();
            _handlerMap.put(eventClass, handlers);
        }

        // add the handler to the handler collection
        if (!handlers.add(handler, priority, ignoreCancelled)) {
            throw new HandlerAlreadyRegisteredException(handler);
        }
    }

    /**
     * Register an event listener.
     *
     * @param eventListener  The event listener.
     */
    public void register(IGenericsEventListener eventListener) {
        PreCon.notNull(eventListener);

        // cannot use a disposed event manager
        if (_isDisposed)
            throw new EventManagerDisposedException();

        // listeners can only be registered once.
        if (_listeners.containsKey(eventListener)) {
            throw new ListenerAlreadyRegisteredException(eventListener);
        }

        // create a listener container
        ListenerContainer listener = new ListenerContainer(eventListener);
        _listeners.put(eventListener, listener);

        // get all methods from listener so we can filter out the event handlers
        Method[] methods = eventListener.getClass().getDeclaredMethods();

        // filter out the event handlers
        for (Method method : methods) {

            // event handlers must have a special annotation
            GenericsEventHandler annotation = method.getAnnotation(GenericsEventHandler.class);
            if (annotation == null)
                continue;

            // event handlers must have exactly one parameter
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes == null || paramTypes.length != 1)
                continue;

            Class<?> eventClass = paramTypes[0];

            // get the event handler collection for the event
            EventHandlerCollection handlers = _handlerMap.get(eventClass);

            // create a new event handler collection if one is not present
            if (handlers == null) {
                handlers = new EventHandlerCollection();
                _handlerMap.put(eventClass, handlers);
            }

            // add the event handler to the handlers collection
            try {
                handlers.add(eventListener, eventClass, method, annotation);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            // add the handler to the listener container
            listener.addHandlers(handlers);
        }
    }

    /**
     * Unregister an event listener
     *
     * @param eventListener  The event listener to unregister.
     */
    public void unregister(IGenericsEventListener eventListener) {
        PreCon.notNull(eventListener);

        // cannot use a disposed event manager.
        if (_isDisposed)
            return;

        // get the listener container.
        ListenerContainer listener = _listeners.remove(eventListener);
        if (listener == null)
            return;

        // unregister
        listener.unregister();
    }

    /**
     * Unregister an event handler from the specified event.
     *
     * @param eventClass  The event class.
     * @param handler     The event handler to unregister.
     */
    public void unregister(Class<?> eventClass, IEventHandler handler) {
        PreCon.notNull(eventClass);
        PreCon.notNull(handler);

        // cannot use a disposed event manager.
        if (_isDisposed)
            return;

        // get the handlers collection for the event
        EventHandlerCollection handlers =_handlerMap.get(eventClass);
        if (handlers == null) {
            return;
        }

        // remove the handler
        handlers.removeHandler(handler);
    }

    public void unregisterAll() {

        if (_isDisposed)
            return;

        // clear event handlers on all handler collections
        for (EventHandlerCollection handlers : _handlerMap.values()) {
            handlers.clear();
        }
        _handlerMap.clear();

        // unregister all listeners
        for (ListenerContainer listener : _listeners.values()) {
            listener.unregister();
        }
        _listeners.clear();
    }

    /**
     * Determine if there are event handlers registered
     * for the specified event.
     *
     * @param event  The event class.
     */
    public boolean hasHandlers(Class<?> event) {
        return _handlerMap.containsKey(event);
    }

    /**
     * Used to first call a Bukkit event via the Bukkit
     * plugin manager, then on the generics event manager.
     *
     * @param event  The event to call.
     *
     * @param <T>  The event type.
     */
    public <T extends Event> T callBukkit(T event) {
        Bukkit.getPluginManager().callEvent(event);

        return call(event);
    }

    /**
     * Call an event.
     *
     * @param event  The event to call.
     *
     * @param <T>  The event type.
     */
    public <T> T call(T event) {
        PreCon.notNull(event);

        // cannot use a disposed event manager
        if (_isDisposed)
            throw new EventManagerDisposedException();

        // prevent the same instance of an event from being called twice
        // due to "bubbling" from child event managers.
        if (_calledEvents.containsKey(event))
            return event;

        _calledEvents.put(event, null);

        // BUG WORKAROUND interact event getting called twice - Minecraft/Bukkit/Spigot issue
        if (event instanceof PlayerEvent && !(event instanceof PlayerMoveEvent)) {
            EventWrapper wrapper = new EventWrapper((PlayerEvent) event);
            if (_recentBukkitEvents.contains(wrapper)) {
                return event;
            }
            _recentBukkitEvents.add(wrapper);
        }

        // call event on parent first
        if (_parent != null) {
            _parent.call(event);
        }

        // get event handler collection
        EventHandlerCollection handlers = _handlerMap.get(event.getClass());
        if (handlers != null) {
            // call event on handlers.
            handlers.call(event);
        }

        // run call handlers
        for (IEventHandler handler : _callHandlers) {
            handler.call(event);
        }

        return event;
    }

    /**
     * Adds a handler that is called whenever an event is called
     * in the local event manager instance.
     *
     * @param handler  The handler to add.
     */
    public void addCallHandler(IEventHandler handler) {
        PreCon.notNull(handler);

        _callHandlers.add(handler);
    }

    /**
     * Removes a handler that is called whenever an event is
     * called in the local event manager instance.
     *
     * @param handler  The handler to remove.
     */
    public void removeCallHandler(IEventHandler handler) {
        PreCon.notNull(handler);

        _callHandlers.remove(handler);
    }

    /**
     * Determine if the manager is disposed.
     */
    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    /**
     * Dispose the event manager.
     */
    @Override
    public void dispose() {

        unregisterAll();

        _isDisposed = true;
    }


    /**
     * A container for a generics listener that contains the
     * event handler collections which contain the listeners
     * event handlers.
     */
    private static class ListenerContainer {

        private IGenericsEventListener _listener;
        private Set<EventHandlerCollection> _handlers = new HashSet<>(50);

        /**
         * Constructor.
         *
         * @param listener  The listener to encapsulate.
         */
        ListenerContainer(IGenericsEventListener listener) {
            _listener = listener;
        }

        /**
         * Add an event handlers collection that one of the
         * listeners event handlers have been added to so
         * it will have a means to unregister from the handlers
         * collection.
         *
         * @param handlers  The handler collection to add.
         */
        public void addHandlers(EventHandlerCollection handlers) {
            _handlers.add(handlers);
        }

        /**
         * Unregister the listener from the handler collections.
         */
        public void unregister() {
            for (EventHandlerCollection handlers : _handlers) {
                handlers.removeListener(_listener);
            }
            _handlers.clear();
        }
    }


    // BUG WORKAROUND interact event getting called twice - Minecraft/Bukkit/Spigot issue
    private static class EventWrapper {

        final PlayerEvent event;
        final Date date;

        EventWrapper(PlayerEvent event) {
            this.event = event;
            this.date = new Date();
        }

        @Override
        public int hashCode() {
            return event.getPlayer().getUniqueId().hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof EventWrapper) {

                EventWrapper wrapper = (EventWrapper)obj;
                Date now = new Date();
                Player player = wrapper.event.getPlayer();

                if (!wrapper.event.getClass().equals(event.getClass()))
                    return false;

                if (!player.equals(event.getPlayer()))
                    return false;

                if (DateUtils.getDeltaMilliseconds(wrapper.date, now) > 3) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }
}
