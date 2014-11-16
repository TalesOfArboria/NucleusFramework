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


package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.events.EventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventManager;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;

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
import java.util.List;

/**
 * Provide scripts with an event registration API
 */
@IScriptApiInfo(
        variableName = "events",
        description = "Provide scripts with a Bukkit event registration API.")
public class ScriptApiEvents extends GenericsScriptApi {

    private ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
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
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        private final Plugin _plugin;
        private final List<RegisteredBukkitEvent> _registeredBukkit = new ArrayList<>(15);
        private final List<RegisteredGenericsEvent> _registeredGenerics = new ArrayList<>(15);
        private Listener _dummyBukkitListener = new Listener() {};

        ApiObject(Plugin plugin) {
            _plugin = plugin;
        }

        @Override
        public void reset() {

            // unregister Bukkit event handlers
            for (RegisteredBukkitEvent registered : _registeredBukkit) {
                registered._handlerList.unregister(registered._registeredListener);
            }
            _registeredBukkit.clear();
            _dummyBukkitListener = new Listener() {};

            // unregister Generics event handlers
            for (RegisteredGenericsEvent registered : _registeredGenerics) {
                GenericsEventManager.getGlobal().unregister(registered._eventClass, registered._handler);
            }
            _registeredGenerics.clear();
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
        public boolean on(String eventName, String priority, final IScriptEventHandler handler) {
            PreCon.notNullOrEmpty(eventName);
            PreCon.notNullOrEmpty(priority);
            PreCon.notNull(handler);

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
                return registerBukkitEvent(bukkitEventClass, priority, handler);
            }
            else {
                return registerGenericsEvent(eventClass, priority, handler);
            }
        }

        /*
         * Register Generics event.
         */
        private boolean registerGenericsEvent(Class<?> event, String priority,
                                              final IScriptEventHandler handler) {

            GenericsEventPriority eventPriority = GenericsEventPriority.NORMAL;

            try {
                eventPriority = GenericsEventPriority.valueOf(priority.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }

            EventHandler eventHandler = new EventHandler() {
                @Override
                public void call(Object event) {
                    handler.onEvent(event);
                }
            };

            GenericsEventManager.getGlobal().register(event, eventPriority, eventHandler);

            _registeredGenerics.add(new RegisteredGenericsEvent(event, eventHandler));

            return true;
        }

        /*
         * Register Bukkit event
         */
        private boolean registerBukkitEvent(Class<? extends Event> event, String priority,
                                            final IScriptEventHandler handler) {
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
                    eventExecutor, eventPriority, _plugin, true);

            handlerList.register(registeredListener);

            _registeredBukkit.add(new RegisteredBukkitEvent(handlerList, registeredListener));

            return true;
        }

    }

    public static interface IScriptEventHandler {
        public void onEvent(Object event);
    }

    private static class RegisteredBukkitEvent {
        HandlerList _handlerList;
        RegisteredListener _registeredListener;

        RegisteredBukkitEvent(HandlerList handlerList, RegisteredListener listener) {
            _handlerList = handlerList;
            _registeredListener = listener;
        }
    }

    private static class RegisteredGenericsEvent {
        Class<?> _eventClass;
        EventHandler _handler;

        RegisteredGenericsEvent(Class<?> eventClass, EventHandler handler) {
            _eventClass = eventClass;
            _handler = handler;
        }
    }
}
