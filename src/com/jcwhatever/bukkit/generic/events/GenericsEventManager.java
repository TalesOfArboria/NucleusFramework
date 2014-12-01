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
import com.jcwhatever.bukkit.generic.events.exceptions.EventManagerDisposedException;
import com.jcwhatever.bukkit.generic.events.exceptions.HandlerAlreadyRegisteredException;
import com.jcwhatever.bukkit.generic.events.exceptions.ListenerAlreadyRegisteredException;
import com.jcwhatever.bukkit.generic.internal.listeners.BlockListener;
import com.jcwhatever.bukkit.generic.internal.listeners.EnchantmentListener;
import com.jcwhatever.bukkit.generic.internal.listeners.EntityListener;
import com.jcwhatever.bukkit.generic.internal.listeners.HangingListener;
import com.jcwhatever.bukkit.generic.internal.listeners.InventoryListener;
import com.jcwhatever.bukkit.generic.internal.listeners.PlayerListener;
import com.jcwhatever.bukkit.generic.internal.listeners.VehicleListener;
import com.jcwhatever.bukkit.generic.internal.listeners.WeatherListener;
import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Generics event manager.
 */
public class GenericsEventManager implements IDisposable {

    private static GenericsEventManager _globalInstance;

    /**
     * Get the global event manager.
     *
     * <p>
     *     Note: Bukkit events called in child event managers are
     *     not passed to the global event manager.
     * </p>
     */
    public static GenericsEventManager getGlobal() {
        if (_globalInstance == null)
            _globalInstance = new GenericsEventManager(true);

        return _globalInstance;
    }

    private final Map<Class<?>, EventHandlerCollection> _handlerMap = new HashMap<>(100);
    private final Map<IGenericsEventListener, ListenerContainer> _listeners = new HashMap<>(100);
    private final List<IEventHandler> _callHandlers = new ArrayList<>(10);
    private final GenericsEventManager _parent;
    private final boolean _isGlobal;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Create a new event manager using the global event manager as parent.</p>
     */
    public GenericsEventManager() {
        this(getGlobal());
    }

    /**
     * Constructor.
     *
     * @param parent  The parent event manager. The parent receives all
     *                event calls the child receives.
     */
    public GenericsEventManager(@Nullable GenericsEventManager parent) {
        _parent = parent;
        _isGlobal = false;
    }

    /**
     * Private constructor for global event manager.
     */
    private GenericsEventManager(boolean isGlobal) {
        _parent = null;
        _isGlobal = isGlobal;

        if (_isGlobal) {

            Scheduler.runTaskLater(GenericsLib.getLib(), new Runnable() {
                @Override
                public void run() {

                    Bukkit.getPluginManager().registerEvents(new BlockListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new EnchantmentListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new EntityListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new HangingListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new InventoryListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new PlayerListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new VehicleListener(), GenericsLib.getLib());
                    Bukkit.getPluginManager().registerEvents(new WeatherListener(), GenericsLib.getLib());
                }
            });
        }
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

        // The global manager cannot unregister all.
        if (_isGlobal)
            throw new RuntimeException("Cannot unregister all handlers at once from the global event manager.");

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

        // call event on parent first unless the parent is the global
        // event manager and the event is a bukkit event. (to prevent inter-plugin conflicts)
        if (_parent != null && !(_parent._isGlobal && event instanceof org.bukkit.event.Event)) {

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

        // The global manager cannot be disposed.
        if (_isGlobal)
            throw new RuntimeException("Cannot dispose the global event manager.");

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

}
