package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.messaging.Messenger.LineWrapping;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provide scripts with api access to generics messenger.
 */
@IScriptApiInfo(
        variableName = "msg",
        description = "Provide scripts with API access to chat messenger.")
public class ScriptApiMsg extends GenericsScriptApi {

    private ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiMsg(Plugin plugin) {
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

        ApiObject(Plugin plugin) {
            _plugin = plugin;
        }

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Tell a player a message.
         *
         * @param player   The player to tell.
         * @param message  The message to send.
         * @param params   Optional message formatting parameters.
         */
        public void tell(Object player, String message, Object... params) {
            PreCon.notNull(player);
            PreCon.notNull(message);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tell(LineWrapping.DISABLED, _plugin, p, message, params);
        }

        /**
         * Tell a player a message without a plugin tag in the message.
         *
         * @param player   The player to tell.
         * @param message  The message to send.
         * @param params   Optional message formatting parameters.
         */
        public void tellAnon(Object player, String message, Object... params) {
            PreCon.notNull(player);
            PreCon.notNull(message);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tell(p, message, params);
        }

        /**
         * Tell a player a message and prevent spamming of the same message.
         *
         * @param player   The player to tell.
         * @param timeout  The spam timeout, the amount of time to wait before the message can be seen again.
         * @param message  The message to send.
         * @param params   Optional message formatting parameters.
         */
        public void tellNoSpam(Object player, int timeout, String message, Object... params) {
            PreCon.notNull(player);
            PreCon.greaterThanZero(timeout);
            PreCon.notNull(message);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tellNoSpam(_plugin, p, timeout, message, params);
        }

        /**
         * Tell a player a message and prevent spamming of the same message
         * without a plugin tag in the message.
         *
         * @param player        The player to tell.
         * @param timeout  The spam timeout, the amount of time to wait before the message can be seen again.
         * @param message  The message to send.
         * @param params   Optional message formatting parameters.
         */
        public void tellNoSpamAnon(Object player, int timeout, String message, Object... params) {
            PreCon.notNull(player);
            PreCon.greaterThanZero(timeout);
            PreCon.notNull(message);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tellNoSpam(null, p, timeout, message, params);
        }

        /**
         * Send scripting debug message to console.
         *
         * @param message  The message to send.
         * @param params   Optional message formatting parameters.
         */
        public void debug(String message, Object... params) {
            PreCon.notNull(message);

            Messenger.warning(_plugin, "[SCRIPT DEBUG] " + message, params);
        }
    }
}
