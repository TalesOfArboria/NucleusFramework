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

import com.jcwhatever.nucleus.actionbar.ActionBar;
import com.jcwhatever.nucleus.actionbar.PersistentActionBar;
import com.jcwhatever.nucleus.actionbar.TimedActionBar;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.dynamic.DynamicTextBuilder;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;

@ScriptApiInfo(
        variableName = "actionbars",
        description = "Add NucleusFramework action bar scripting API support.")
public class ScriptApiActionBar extends NucleusScriptApi {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiActionBar(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject {

        Map<PersistentActionBar, Void> _actionBars = new WeakHashMap<>(10);
        boolean _isDisposed;

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {
            for (PersistentActionBar actionBar : _actionBars.keySet()) {
                actionBar.hideAll();
            }
        }

        /**
         * Show a quick action bar message.
         *
         * @param player     The player to show the message to.
         * @param message    The message to show.
         */
        public void show(Object player, Object message) {
            PreCon.notNull(player, "player");
            PreCon.notNull(message, "message");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            IDynamicText dynamicText = getDynamicText(message);

            ActionBar actionBar = new ActionBar(dynamicText);
            actionBar.show(p);
        }

        /**
         * Show a quick action bar message.
         *
         * @param player     The player to show the message to.
         * @param message    The message to show.
         */
        public void showTimed(Object player, int ticks, Object message) {
            PreCon.notNull(player, "player");
            PreCon.notNull(message, "message");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            IDynamicText dynamicText = getDynamicText(message);

            TimedActionBar timedActionBar = new TimedActionBar(dynamicText, ticks, TimeScale.TICKS);
            timedActionBar.show(p);

            _actionBars.put(timedActionBar, null);
        }

        /**
         * Create a new {@link PersistentActionBar} instance.
         *
         * @param text  The action bar text.
         */
        public PersistentActionBar createPersistent(Object text) {
            PreCon.notNull(text, "text");

            IDynamicText dynamicText = getDynamicText(text);

            PersistentActionBar actionBar = new PersistentActionBar(dynamicText);

            _actionBars.put(actionBar, null);

            return actionBar;
        }

        /**
         * Create a new {@link TimedActionBar} instance.
         *
         * @param ticks  The number of ticks the bar is visible for.
         * @param text   The action bar text.
         */
        public TimedActionBar createTimed(int ticks, Object text) {
            PreCon.greaterThanZero(ticks, "ticks");
            PreCon.notNull(text);

            IDynamicText dynamicText = getDynamicText(text);

            TimedActionBar actionBar = new TimedActionBar(dynamicText, ticks, TimeScale.TICKS);

            _actionBars.put(actionBar, null);

            return actionBar;
        }


        private IDynamicText getDynamicText(Object text) {
            if (text instanceof String) {
                return new DynamicTextBuilder().append(text).build();
            }
            else if (text instanceof IDynamicText) {
                return (IDynamicText)text;
            }
            else {
                throw new RuntimeException("Invalid action bar text.");
            }
        }
    }
}

