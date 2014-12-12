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


package com.jcwhatever.bukkit.generic.events.manager;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.TimeScale;
import com.jcwhatever.bukkit.generic.collections.TimedHashSet;
import com.jcwhatever.bukkit.generic.collections.WeakHashSetMap;
import com.jcwhatever.bukkit.generic.events.manager.exceptions.EventManagerDisposedException;
import com.jcwhatever.bukkit.generic.events.manager.exceptions.HandlerAlreadyRegisteredException;
import com.jcwhatever.bukkit.generic.events.manager.exceptions.ListenerAlreadyRegisteredException;
import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.utils.DateUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
 * <p>The global Generics event manager also receives certain Bukkit events so event generics
 * handlers can be used for those Bukkit events.</p>
 *
 * <p>You are encouraged to use the event managers {@link #callBukkit} method to call your custom
 * Bukkit events. This will first call the event using Bukkit's event system, then again on the generics
 * event manager to allow the generics event subscribers to handle the event.</p>
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

    private static WeakHashSetMap<Plugin, ListenerContainer> _pluginListeners = new WeakHashSetMap<>(100);
    private static WeakHashSetMap<Plugin, HandlerContainer> _pluginHandlers = new WeakHashSetMap<>(100);
    private static WeakHashSetMap<Plugin, CallHandlerContainer> _pluginCallHandlers = new WeakHashSetMap<>(100);

    /**
     * Remove all registered event handlers and listeners from
     * the specified plugin from all event managers.
     *
     * <p>Automatically called when a plugin is disabled.</p>
     *
     * @param plugin  The plugin.
     */
    public static void unregisterPlugin(Plugin plugin) {

        Set<ListenerContainer> listeners = _pluginListeners.removeAll(plugin);
        for (ListenerContainer container : listeners) {
            container.manager.unregister(container.listener);

            if (container.listener instanceof IDisposable) {
                ((IDisposable) container.listener).dispose();
            }
        }

        Set<HandlerContainer> handlers = _pluginHandlers.removeAll(plugin);
        for (HandlerContainer container : handlers) {
            container.manager.unregister(container.event, container.handler);

            if (container.handler instanceof IDisposable) {
                ((IDisposable) container.handler).dispose();
            }
        }

        Set<CallHandlerContainer> callHandlers = _pluginCallHandlers.removeAll(plugin);
        for (CallHandlerContainer container : callHandlers) {
            container.manager.removeCallHandler(container.handler);

            if (container.handler instanceof IDisposable) {
                ((IDisposable) container.handler).dispose();
            }
        }
    }

    private final GenericsEventManager _parent;

    // maps event class to the event handlers assigned to it
    private final Map<Class<?>, EventHandlerCollection> _handlerMap = new HashMap<>(100);

    // maps event listener to its listener container
    private final Map<IEventListener, ListenerContainer> _listeners = new HashMap<>(100);

    // maps individually registered handlers to its handler  container
    private final Map<IEventHandler, HandlerContainer> _handlers = new HashMap<>(100);

    // global handlers that are called for every called event
    private final Map<IEventCallHandler, CallHandlerContainer> _callHandlers = new HashMap<>(10);

    // stores called events to prevent them from being called again due to bubbling
    private final Map<Object, Void> _calledEvents = new WeakHashMap<>(30);

    private boolean _isDisposed;

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
     * @param plugin      The handlers owning plugin.
     * @param eventClass  The event class.
     * @param priority    The event priority.
     * @param handler     The event handler.
     */
    public <T> void register(Plugin plugin, Class<T> eventClass,
                         GenericsEventPriority priority, IEventHandler<T> handler) {
        register(plugin, eventClass, priority, false, handler);
    }

    /**
     * Register an event handler for the specified event.
     *
     * @param plugin           The handlers owning plugin.
     * @param eventClass       The event class.
     * @param priority         The event priority.
     * @param ignoreCancelled  True to run the handler event if the event is cancelled.
     * @param handler          The event handler.
     */
    public <T> void register(Plugin plugin, Class<T> eventClass, GenericsEventPriority priority,
                         boolean ignoreCancelled, IEventHandler<T> handler) {
        PreCon.notNull(plugin);
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

        HandlerContainer<T> handlerContainer = new HandlerContainer<T>(plugin, this, eventClass, handler, handlers);
        _pluginHandlers.put(plugin, handlerContainer);
        _handlers.put(handler, handlerContainer);
    }

    /**
     * Register an event listener.
     *
     * @param eventListener  The event listener.
     */
    public void register(IEventListener eventListener) {
        PreCon.notNull(eventListener);

        // cannot use a disposed event manager
        if (_isDisposed)
            throw new EventManagerDisposedException();

        // listeners can only be registered once.
        if (_listeners.containsKey(eventListener)) {
            throw new ListenerAlreadyRegisteredException(eventListener);
        }

        // create a listener container
        ListenerContainer listener = new ListenerContainer(this, eventListener);
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
                handlers.add(eventListener, method, annotation);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            // add the handler to the listener container
            listener.handlers.add(handlers);
            _pluginListeners.put(eventListener.getPlugin(), listener);
        }
    }

    /**
     * Unregister an event listener
     *
     * @param eventListener  The event listener to unregister.
     */
    public void unregister(IEventListener eventListener) {
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
        _pluginListeners.removeValue(eventListener.getPlugin(), listener);
    }

    /**
     * Unregister an event handler from the specified event.
     *
     * @param eventClass  The event class.
     * @param handler     The event handler to unregister.
     */
    public <T> void unregister(Class<T> eventClass, IEventHandler<T> handler) {
        PreCon.notNull(eventClass);
        PreCon.notNull(handler);

        // cannot use a disposed event manager.
        if (_isDisposed)
            return;

        HandlerContainer handlerContainer = _handlers.remove(handler);
        if (handlerContainer == null)
            return;

        handlerContainer.collection.removeHandler(handler);

        _pluginHandlers.removeValue(handlerContainer.plugin, handlerContainer);
    }

    /**
     * Unregister all event handlers.
     */
    public void unregisterAll() {

        if (_isDisposed)
            return;

        // clear event handlers on all handler collections
        for (HandlerContainer handler : _handlers.values()) {
            _pluginHandlers.removeValue(handler.plugin, handler);
        }

        // unregister all listeners
        for (ListenerContainer listener : _listeners.values()) {
            _pluginListeners.removeValue(listener.listener.getPlugin(), listener);
        }

        _handlerMap.clear();
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
        for (IEventCallHandler handler : _callHandlers.keySet()) {
            handler.onCall(event);
        }

        return event;
    }

    /**
     * Adds a handler that is called whenever an event is called
     * in the local event manager instance.
     *
     * @param handler  The handler to add.
     */
    public void addCallHandler(IEventCallHandler handler) {
        PreCon.notNull(handler);

        CallHandlerContainer container = new CallHandlerContainer(this, handler);
        _callHandlers.put(handler, container);
        _pluginCallHandlers.put(handler.getPlugin(), container);

    }

    /**
     * Removes a handler that is called whenever an event is
     * called in the local event manager instance.
     *
     * @param handler  The handler to remove.
     */
    public void removeCallHandler(IEventCallHandler handler) {
        PreCon.notNull(handler);

        CallHandlerContainer container = _callHandlers.remove(handler);
        if (container == null)
            return;

        _pluginCallHandlers.removeValue(handler.getPlugin(), container);
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
     * Stores an individually registered handler, the
     * event handler collection it's in and its owning plugin.
     */
    private static class HandlerContainer<T> {
        final Plugin plugin;
        final GenericsEventManager manager;
        final Class<T> event;
        final IEventHandler handler;
        final EventHandlerCollection collection;

        HandlerContainer(Plugin plugin, GenericsEventManager manager,
                         Class<T> event, IEventHandler<T> handler, EventHandlerCollection collection) {
            this.plugin = plugin;
            this.manager = manager;
            this.event = event;
            this.handler = handler;
            this.collection = collection;
        }
    }

    /**
     * Stores an event call handler and the
     * event manager its registered with.
     */
    private static class CallHandlerContainer {
        final IEventCallHandler handler;
        final GenericsEventManager manager;

        CallHandlerContainer(GenericsEventManager manager, IEventCallHandler handler) {
            this.manager = manager;
            this.handler = handler;
        }
    }

    /**
     * Stores the event handler collections that a listeners
     * event handlers have been added to.
     */
    private static class ListenerContainer {

        final GenericsEventManager manager;
        final IEventListener listener;
        final Set<EventHandlerCollection> handlers = new HashSet<>(15);

        ListenerContainer(GenericsEventManager manager, IEventListener listener) {
            this.manager = manager;
            this.listener = listener;
        }

        /**
         * Unregister the listener from the handler collections.
         */
        public void unregister() {
            for (EventHandlerCollection handlerCollection : handlers) {
                handlerCollection.removeListener(listener);
            }
            handlers.clear();
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
