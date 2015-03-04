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


package com.jcwhatever.nucleus.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberLinkedList;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.nucleus.utils.observer.script.IScriptEventSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptEventSubscriber;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Provide scripts with an event registration API
 */
@ScriptApiInfo(
        variableName = "events",
        description = "Provide scripts with a Bukkit event registration API.")
public class ScriptApiEvents extends NucleusScriptApi {

    private ApiObject _api;

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiEvents(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject(getPlugin());

        return _api;
    }

    public void reset() {
        if (_api != null)
            _api.dispose();
    }

    public static class ApiObject implements IScriptApiObject {

        private final Plugin _plugin;
        private final List<RegisteredBukkitEvent> _registeredBukkit = new ArrayList<>(10);
        private final Deque<ISubscriber> _subscribers = new SubscriberLinkedList<>();
        private Listener _dummyBukkitListener = new Listener() {};
        private boolean _isDisposed;

        ApiObject(Plugin plugin) {
            _plugin = plugin;
        }

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {

            // unregister Bukkit event handlers
            for (RegisteredBukkitEvent registered : _registeredBukkit) {
                registered._handlerList.unregister(registered._registeredListener);
            }
            _registeredBukkit.clear();
            _dummyBukkitListener = new Listener() {};

            // unregister nucleus event handlers
            while (!_subscribers.isEmpty()) {
                ISubscriber subscriber = _subscribers.remove();
                subscriber.dispose();
            }

            _isDisposed = true;
        }

        /**
         * Registers an event handler.
         *
         * @param eventName  The event class name.
         * @param priority   The event priority as a string.
         * @param handler    The event handler.
         *
         * @return True if successfully registered.
         */
        public boolean on(String eventName, String priority, final IScriptEventSubscriber handler) {
            PreCon.notNullOrEmpty(eventName);
            PreCon.notNullOrEmpty(priority);
            PreCon.notNull(handler);

            String[] priorityComp = TextUtils.PATTERN_COLON.split(priority);
            boolean ignoreCancelled = false;

            if (priorityComp.length == 2) {
                if (priorityComp[1].equalsIgnoreCase("ignoreCancelled")) {
                    ignoreCancelled = true;
                    priority = priorityComp[0];
                }
            }

            Class<?> eventClass;

            try {
                eventClass = Class.forName(eventName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            // check for and register bukkit events
            if (Event.class.isAssignableFrom(eventClass)) {
                Class<? extends Event> bukkitEventClass = eventClass.asSubclass(Event.class);
                return registerBukkitEvent(bukkitEventClass, priority, ignoreCancelled, handler);
            }
            else {
                return registerNucleusEvent(eventClass, priority, ignoreCancelled, handler);
            }
        }

        /*
         * Register NucleusFramework event.
         */
        private boolean registerNucleusEvent(Class<?> event, String priority, boolean ignoreCancelled,
                                             final IScriptEventSubscriber handler) {

            EventSubscriberPriority eventPriority = EventSubscriberPriority.NORMAL;

            try {
                eventPriority = EventSubscriberPriority.valueOf(priority.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ScriptEventSubscriber subscriber = new ScriptEventSubscriber(handler);
            subscriber.setPriority(eventPriority);
            subscriber.setCancelIgnored(ignoreCancelled);

            //noinspection unchecked
            Nucleus.getEventManager().register(_plugin, event, subscriber);

            _subscribers.add(subscriber);

            return true;
        }

        /*
         * Register Bukkit event
         */
        private boolean registerBukkitEvent(Class<? extends Event> event, String priority,
                                            boolean ignoreCancelled,
                                            final IScriptEventSubscriber handler) {
            EventPriority eventPriority = EventPriority.NORMAL;

            try {
                eventPriority = EventPriority.valueOf(priority.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Object result;

            try {
                result = event.getMethod("getHandlerList").invoke(null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return false;
            }

            if (!(result instanceof HandlerList))
                return false;

            final HandlerList handlerList = (HandlerList)result;

            EventExecutor eventExecutor = new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    handler.onEvent(event);
                }
            };

            RegisteredListener registeredListener = new RegisteredListener(_dummyBukkitListener,
                    eventExecutor, eventPriority, _plugin, ignoreCancelled);

            handlerList.register(registeredListener);

            _registeredBukkit.add(new RegisteredBukkitEvent(handlerList, registeredListener));

            return true;
        }

    }

    private static class RegisteredBukkitEvent {
        HandlerList _handlerList;
        RegisteredListener _registeredListener;

        RegisteredBukkitEvent(HandlerList handlerList, RegisteredListener listener) {
            _handlerList = handlerList;
            _registeredListener = listener;
        }
    }
}
