package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
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
        variableName = "bukkitEvents",
        description = "Provide scripts with a Bukkit event registration API.")
public class ScriptApiBukkitEvents extends GenericsScriptApi {

    private ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiBukkitEvents(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject(getPlugin());

        return _api;
    }

    @Override
    public void reset() {
        if (_api != null)
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        private final Plugin _plugin;
        private final List<Registered> _registered = new ArrayList<Registered>(15);
        private Listener _dummyListener = new Listener() {};

        ApiObject(Plugin plugin) {
            _plugin = plugin;
        }

        @Override
        public void reset() {
            for (Registered registered : _registered) {
                registered._handlerList.unregister(registered._registeredListener);
            }
            _registered.clear();
            _dummyListener = new Listener() {};
        }

        /**
         * Registers an event handler.
         *
         * @param event     The event class.
         * @param priority  The event priority as a string.
         * @param handler   The event handler.
         *
         * @return True if successfully registered.
         */
        public boolean on(Class<? extends Event> event, String priority, final IScriptEventHandler handler) {


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

            RegisteredListener registeredListener = new RegisteredListener(_dummyListener,
                    eventExecutor, eventPriority, _plugin, true);

            handlerList.register(registeredListener);

            _registered.add(new Registered(handlerList, registeredListener));

            return true;
        }
    }

    public static interface IScriptEventHandler {
        public void onEvent(Object event);
    }

    private static class Registered {
        HandlerList _handlerList;
        RegisteredListener _registeredListener;

        Registered (HandlerList handlerList, RegisteredListener listener) {
            _handlerList = handlerList;
            _registeredListener = listener;
        }
    }
}
