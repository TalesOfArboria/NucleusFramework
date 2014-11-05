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
