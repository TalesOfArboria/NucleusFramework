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


package com.jcwhatever.nucleus.events.manager;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.observer.agent.AgentHashMap;
import com.jcwhatever.nucleus.collections.observer.agent.AgentMap;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberMultimap;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberSetMultimap;
import com.jcwhatever.nucleus.collections.timed.TimedHashSet;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.event.EventAgent;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriber;
import com.jcwhatever.nucleus.utils.observer.event.IEventSubscriber;
import com.jcwhatever.nucleus.utils.observer.event.IEventWrapper;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.UpdateAgent;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Nucleus event manager.
 *
 * <p>Nucleus events are primarily intended for use in a mostly self contained
 * system with many contexts that benefit from each having their own event manager. This
 * can reduce code and the number of checks by guaranteeing event handlers in a specific event
 * manager are only called in response to the managers specific context. In can also potentially
 * improve performance by reducing the number of event handlers called to the ones that are subscribed
 * to the specific context of the event manager. It can also harm performance if not used properly.</p>
 *
 * <p>The Nucleus event manager can take any type as an event including Bukkit events.
 * It can also have a parent manager that receives calls made to the child manager.
 * By default, the global Nucleus event manager is the parent manager, however
 * a different parent manager or none at all can be set.</p>
 *
 * <p>The global Nucleus event manager also receives certain Bukkit events so Nucleus event
 * handlers can be used for those Bukkit events.</p>
 *
 * <p>You are encouraged to use the event managers {@link #callBukkit} method to call your custom
 * Bukkit events. This will first call the event using Bukkit's event system, then again on the Nucleus
 * event manager to allow its event subscribers to handle the event.</p>
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
 * <p>Note: The event manager runs events slightly different from Bukkit events. When an event is cancelled,
 * all event handlers that would have been called after are not called unless they have been set to be invoked
 * even if the event is cancelled. If a handler un-cancels an event, any handler that was skipped is then run.</p>
 */
public class EventManager implements IPluginOwned, IDisposable {

    private static final SubscriberMultimap<Plugin, IEventSubscriber> _pluginEventMap
            = new SubscriberSetMultimap<>(30, 10);

    private static final SubscriberMultimap<Plugin, IUpdateSubscriber> _pluginCallMap
            = new SubscriberSetMultimap<>(7, 3);

    /**
     * Remove all registered event handlers and listeners from
     * the specified plugin from all event managers.
     *
     * <p>Automatically invoked when a plugin is disabled.</p>
     *
     * @param plugin  The plugin.
     */
    public static void unregisterPlugin(Plugin plugin) {
        PreCon.notNull(plugin);

        synchronized (_pluginEventMap) {
            Collection<IEventSubscriber> eventSubscribers = _pluginEventMap.removeAll(plugin);
            for (IEventSubscriber subscriber : eventSubscribers) {
                subscriber.dispose();
            }
        }

        synchronized (_pluginCallMap) {
            Collection<IUpdateSubscriber> callSubscribers = _pluginCallMap.removeAll(plugin);
            for (IUpdateSubscriber subscriber : callSubscribers) {
                subscriber.dispose();
            }
        }
    }

    private final Plugin _plugin;
    private final EventManager _parent;
    private final AgentMap<Class<?>, EventAgent> _eventAgents = new AgentHashMap<>(10);
    private final Map<IEventListener, ListenerInfo> _listeners = new HashMap<>(10);
    private final TimedHashSet<Object> _calledEvents;
    private final UpdateAgent<Object> _callAgent = new UpdateAgent<>();
    private final Object _sync = new Object();

    private volatile boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Create a new event manager using the global event manager as parent.</p>
     *
     * @param plugin  The owning plugin.
     */
    public EventManager(Plugin plugin) {
        this(plugin, Nucleus.getEventManager());
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param parent  The parent event manager. The parent receives all
     *                event calls the child receives.
     */
    public EventManager(Plugin plugin, @Nullable EventManager parent) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _parent = parent;
        _calledEvents = new TimedHashSet<>(plugin, 10, 1, TimeScale.TICKS);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Register an event subscriber.
     *
     * @param plugin      The subscribers owning plugin.
     * @param event       The event to subscribe to.
     * @param subscriber  The event subscriber.
     *
     * @param <T>  The event type.
     */
    public <T> void register(Plugin plugin, Class<T> event, IEventSubscriber<T> subscriber) {
        PreCon.notNull(event);
        PreCon.notNull(subscriber);

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed event manager.");

        EventAgent agent = getEventAgent(event, true);

        agent.addSubscriber(subscriber);


        _pluginEventMap.put(plugin, subscriber);
    }

    /**
     * Register an event listener. Methods from the listener annotated
     * with {@link EventMethod} are registered as event subscribers.
     *
     * <p>The method must have only one parameter whose type is that of
     * the event it subscribes to.</p>
     *
     * <p>The method visibility does not matter. It can be private, protected
     * or public.</p>
     *
     * @param listener  The listener to register.
     */
    public void register(IEventListener listener) {
        PreCon.notNull(listener);

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed event manager.");

        List<IEventSubscriber> subscribers = extractSubscribers(listener);
        if (subscribers.isEmpty())
            return;

        ListenerInfo info = new ListenerInfo(this, listener, subscribers);

        synchronized (_sync) {
            _listeners.put(listener, info);
        }

        for (IEventSubscriber subscriber : subscribers) {

            EventMethodWrapper<?> wrapper = (EventMethodWrapper<?>)subscriber;

            EventAgent agent = getEventAgent(wrapper.event, true);

            //noinspection unchecked
            agent.addSubscriber(subscriber);
        }


        _pluginEventMap.putAll(listener.getPlugin(), subscribers);
    }

    /**
     * Unregister an event subscriber.
     *
     * @param subscriber  The event subscriber.
     */
    public void unregister(IEventSubscriber subscriber) {
        PreCon.notNull(subscriber);

        if (_eventAgents.unregisterAll(subscriber)) {
            synchronized (_pluginEventMap) {
                CollectionUtils.removeValue(_pluginEventMap, subscriber);
            }
        }
    }

    /**
     * Unregister an event listener and all its event
     * subscribers.
     *
     * @param listener  The listener to unregister.
     */
    public void unregister(IEventListener listener) {
        PreCon.notNull(listener);

        synchronized (_sync) {
            ListenerInfo info = _listeners.remove(listener);
            if (info == null)
                return;

            // unregister listeners subscribers from agents
            info.unregister();
        }
    }

    /**
     * Calls a Bukkit event using Bukkits event manager, then calls the
     * event on the {@link EventManager} instance.
     *
     * @param caller  Optional source of the event.
     * @param event   The event.
     *
     * @param <T>  The event type.
     *
     * @return  The event.
     */
    public <T extends Event> T callBukkit(@Nullable Object caller, T event) {

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed event manager.");

        Bukkit.getPluginManager().callEvent(event);

        return call(caller, event);
    }

    /**
     * Call an event.
     *
     * @param caller  Optional source of the event.
     * @param event   The event
     *                .
     * @param <T>  The event type.
     *
     * @return The event.
     */
    public <T> T call(@Nullable Object caller, T event) {

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed event manager.");

        // prevent redirected events from bubbling back to
        // an event manager its was already called on.
        synchronized (_sync) {
            if (_calledEvents.contains(event))
                return event;

            _calledEvents.add(event);
        }

        // call event on parent first
        if (_parent != null) {
            _parent.call(caller, event);
        }

        _callAgent.update(event);

        EventAgent agent = getEventAgent(event.getClass(), false);
        if (agent == null)
            return event;

        // check for Bukkit cancellable event
        if (event instanceof Cancellable) {
            agent.call(caller, new BukkitEventWrapper<T>(event));
        }
        else {
            agent.call(caller, event);
        }

        return event;
    }

    /**
     * Attach an {@link IUpdateSubscriber} that receives all events called.
     *
     * @param plugin      The subscribers owning plugin.
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public EventManager onCall(Plugin plugin, IUpdateSubscriber<?> subscriber) {
        PreCon.notNull(subscriber);

        if (isDisposed())
            throw new RuntimeException("Cannot use a disposed event manager.");

        _callAgent.addSubscriber(subscriber);


        _pluginCallMap.put(plugin, subscriber);

        return this;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        if (_isDisposed)
            return;

        _eventAgents.dispose();
        _callAgent.dispose();
        _isDisposed = true;
    }

    /*
     * Extract all valid event subscribers from a listener.
     */
    private List<IEventSubscriber> extractSubscribers(IEventListener listener) {

        synchronized (_sync) {
            if (_listeners.containsKey(listener))
                throw new RuntimeException("Listener already registered.");
        }

        // get all methods from listener
        Method[] methods = listener.getClass().getDeclaredMethods();
        List<IEventSubscriber> subscribers = new ArrayList<>(methods.length);

        for (Method method : methods) {

            EventMethod annotation = method.getAnnotation(EventMethod.class);
            if (annotation == null)
                continue;

            // event handlers must have exactly one parameter
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes == null || paramTypes.length != 1)
                continue;

            Class<?> eventClass = paramTypes[0];

            IEventSubscriber<?> wrapper = new EventMethodWrapper<>(listener, eventClass, method, annotation);
            subscribers.add(wrapper);
        }

        return subscribers;
    }

    /*
     * Get the event agent for an event type.
     */
    private EventAgent getEventAgent(Class<?> eventClass, boolean create) {

        synchronized (_sync) {
            EventAgent agent = _eventAgents.get(eventClass);
            if (agent == null && create) {
                agent = new EventAgent();
                _eventAgents.put(eventClass, agent);
            }
            return agent;
        }
    }

    /*
     * Stores the event subscribers extracted from a listener.
     */
    private static class ListenerInfo {

        final EventManager manager;
        final IEventListener listener;
        final List<IEventSubscriber> subscribers;

        ListenerInfo(EventManager manager, IEventListener listener, List<IEventSubscriber> methodSubscribers) {
            this.manager = manager;
            this.listener = listener;
            this.subscribers = new ArrayList<>(methodSubscribers);
        }

        /**
         * Unregister the listener from the handler collections.
         */
        public void unregister() {
            for (ISubscriber subscriber: subscribers) {
                subscriber.dispose();
            }
            subscribers.clear();
        }
    }

    /*
     * An event subscriber wrapper for a method extracted from an event listener.
     */
    private static class EventMethodWrapper<E> extends EventSubscriber<E> implements IEventSubscriber<E> {

        final Object listener;
        final Class<?> event;
        final Method method;
        final EventMethod annotation;

        EventMethodWrapper(Object listener, Class<?> event, Method method, EventMethod annotation) {
            this.listener = listener;
            this.event = event;
            this.method = method;
            this.annotation = annotation;
            method.setAccessible(true);

            setPriority(annotation.priority());
            setInvokedForCancelled(annotation.invokeForCancelled());
        }

        @Override
        public void onEvent(@Nullable Object caller, E event) {
            try {
                this.method.invoke(this.listener, event);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    /*
     * A wrapper for Bukkit events used to make the events cancel methods
     * available.
     */
    private static class BukkitEventWrapper<E> implements IEventWrapper<E>, ICancellable {

        final E event;

        public BukkitEventWrapper(E event) {
            this.event = event;
        }

        @Override
        public boolean isCancelled() {
            return event instanceof Cancellable && ((Cancellable) event).isCancelled();
        }

        @Override
        public void setCancelled(boolean isCancelled) {
            if (event instanceof Cancellable)
                ((Cancellable) event).setCancelled(isCancelled);
        }

        @Override
        public E getEvent() {
            return event;
        }
    }
}
