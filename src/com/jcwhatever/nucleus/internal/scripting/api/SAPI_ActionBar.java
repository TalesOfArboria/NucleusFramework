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

package com.jcwhatever.nucleus.internal.scripting.api;

import com.jcwhatever.nucleus.managed.actionbar.ActionBars;
import com.jcwhatever.nucleus.managed.actionbar.IActionBar;
import com.jcwhatever.nucleus.managed.actionbar.IPersistentActionBar;
import com.jcwhatever.nucleus.managed.actionbar.ITimedActionBar;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.dynamic.DynamicTextBuilder;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class SAPI_ActionBar implements IDisposable {

    private final Map<IPersistentActionBar, Void> _actionBars = new WeakHashMap<>(10);
    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        for (IPersistentActionBar actionBar : _actionBars.keySet()) {
            actionBar.hideAll();
        }

        _isDisposed = true;
    }

    /**
     * Show a quick action bar message.
     *
     * @param player     The player to show the message to.
     * @param message    The message to show.
     */
    public void show(Object player, String message) {
        PreCon.notNull(player, "player");
        PreCon.notNull(message, "message");

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player");

        IActionBar actionBar = ActionBars.create(message);
        actionBar.showTo(p);
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

        ITimedActionBar timedActionBar = ActionBars.createTimed(dynamicText, ticks, TimeScale.TICKS);
        timedActionBar.showTo(p);

        _actionBars.put(timedActionBar, null);
    }

    /**
     * Create a new {@link IPersistentActionBar} instance.
     *
     * @param text  The action bar text.
     */
    public IPersistentActionBar createPersistent(Object text) {
        PreCon.notNull(text, "text");

        IDynamicText dynamicText = getDynamicText(text);

        IPersistentActionBar actionBar = ActionBars.createPersistent(dynamicText);

        _actionBars.put(actionBar, null);

        return actionBar;
    }

    /**
     * Create a new {@link ITimedActionBar} instance.
     *
     * @param ticks  The number of ticks the bar is visible for.
     * @param text   The action bar text.
     */
    public ITimedActionBar createTimed(int ticks, Object text) {
        PreCon.greaterThanZero(ticks, "ticks");
        PreCon.notNull(text);

        IDynamicText dynamicText = getDynamicText(text);

        ITimedActionBar actionBar = ActionBars.createTimed(dynamicText, ticks, TimeScale.TICKS);

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


