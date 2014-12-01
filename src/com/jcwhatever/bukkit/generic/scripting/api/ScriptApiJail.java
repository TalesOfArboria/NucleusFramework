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

import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.jail.JailSession;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provide scripts with API access to default jail.
 */
@ScriptApiInfo(
        variableName = "jail",
        description = "Provide scripts with API access to the default jail.")
public class ScriptApiJail extends GenericsScriptApi {

    private static ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiJail(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject();

        return _api;
    }

    public void reset() {
        if (_api != null)
            _api.dispose();
    }

    public static class ApiObject implements IScriptApiObject {

        @Override
        public void dispose() {
            // do nothing
        }

        /**
         * Send a player to prison for the specified amount of minutes.
         *
         * @param player   The player to send to jail
         * @param minutes  The time in minutes to spend in jail.
         */
        public boolean imprison(Object player, int minutes) {
            PreCon.notNull(player);
            PreCon.greaterThanZero(minutes);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            return JailManager.getDefault().imprison(p, minutes) != null;
        }

        /**
         * Release a prisoner from jail.
         *
         * @param player  The player to release.
         *
         * @return True if released, false if not in prison.
         */
        public boolean release(Object player) {
            PreCon.notNull(player);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            JailSession session = JailManager.getDefault().getJailSession(p.getUniqueId());
            if (session == null || session.isExpired() || session.isReleased())
                return false;

            session.release(true);

            return true;
        }

        /**
         * Determine if a player is in the default prison.
         * @param player  The player to check.
         */
        public boolean isImprisoned(Object player) {
            PreCon.notNull(player);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            JailSession session = JailManager.getDefault().getJailSession(p.getUniqueId());
            return  session != null && !session.isExpired() && !session.isReleased();
        }
    }

}
