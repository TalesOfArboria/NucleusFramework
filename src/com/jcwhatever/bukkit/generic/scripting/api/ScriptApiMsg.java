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
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Provide scripts with api access to generics messenger.
 */
@ScriptApiInfo(
        variableName = "msg",
        description = "Provide scripts with API access to chat messenger.")
public class ScriptApiMsg extends GenericsScriptApi {

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
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject {

        private Object _chatPrefix;
        private boolean _isDisposed;

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {
            _chatPrefix = null;
            _isDisposed = true;
        }

        /**
         * Set the chat prefix to use.
         *
         * @param prefix  The prefix.
         */
        public void setChatPrefix(@Nullable Object prefix) {
            _chatPrefix = prefix;
        }

        /**
         * Broadcast a message to all players on the server.
         *
         * @param message  The message to broadcast.
         */
        public void broadcast(String message) {
            PreCon.notNull(message);

            Messenger.broadcast(null, message);
        }

        /**
         * Tell a player a message.
         *
         * @param player   The player to tell.
         * @param message  The message to send.
         */
        public void tell(Object player, String message) {
            PreCon.notNull(player);
            PreCon.notNull(message);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tell(LineWrapping.DISABLED, _chatPrefix, p, message);
        }

        /**
         * Tell a player a message without a plugin tag in the message.
         *
         * @param player   The player to tell.
         * @param message  The message to send.
         */
        public void tellAnon(Object player, String message) {
            PreCon.notNull(player);
            PreCon.notNull(message);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tell(LineWrapping.DISABLED, (Object)null, p, message);
        }

        /**
         * Tell a player a message and prevent spamming of the same message.
         *
         * @param player   The player to tell.
         * @param timeout  The spam timeout, the amount of time to wait before the message can be seen again.
         * @param message  The message to send.
         */
        public void tellNoSpam(Object player, int timeout, String message) {
            PreCon.notNull(player);
            PreCon.greaterThanZero(timeout);
            PreCon.notNull(message);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tellNoSpam(LineWrapping.DISABLED, _chatPrefix, p, timeout, message);
        }

        /**
         * Tell a player a message and prevent spamming of the same message
         * without a plugin tag in the message.
         *
         * @param player        The player to tell.
         * @param timeout  The spam timeout, the amount of time to wait before the message can be seen again.
         * @param message  The message to send.
         */
        public void tellNoSpamAnon(Object player, int timeout, String message) {
            PreCon.notNull(player);
            PreCon.greaterThanZero(timeout);
            PreCon.notNull(message);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Messenger.tellNoSpam(LineWrapping.DISABLED, null, p, timeout, message);
        }

        /**
         * Send scripting debug message to console.
         *
         * @param message  The message to send.
         */
        public void debug(String message) {
            PreCon.notNull(message);

            Messenger.warning(_chatPrefix, " [SCRIPT DEBUG] " + message);
        }
    }
}
