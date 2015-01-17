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

package com.jcwhatever.nucleus.actionbar;

import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.dynamic.DynamicTextBuilder;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

/**
 * An action bar that can be displayed to players.
 */
public class ActionBar {

    private final IDynamicText _text;
    private final int _hash;
    private volatile PersistentActionBar _persistent;

    /**
     * Constructor.
     *
     * @param text  The action bar text.
     */
    public ActionBar(String text) {
        this(new DynamicTextBuilder().append(text).build());
    }

    /**
     * Constructor.
     *
     * @param dynamicText  The action bar dynamic text.
     */
    public ActionBar(IDynamicText dynamicText) {
        PreCon.notNull(dynamicText);

        _text = dynamicText;
        _hash = dynamicText.hashCode();
    }

    /**
     * Get the action bar text.
     */
    public IDynamicText getText() {
        return _text;
    }

    /**
     * Show the action bar to a player.
     *
     * <p>If the player is viewing 1 or more {@code PersistentActionBar}'s,
     * a {@code TimedActionBar} is shown instead to ensure the text is given
     * a proper time slice among the persisted bars.</p>
     *
     * @param player  The player to show the bar to.
     */
    public void show(Player player) {

        // make sure the player isn't looking at 1 or more
        // persistent action bars.
        if (PersistentActionBar.isViewing(player)) {

            // need to show as a persistent action bar
            // or it wont get very much screen time, if any.

            if (_persistent == null)
                _persistent = new TimedActionBar(_text.nextText());

            _persistent.show(player, 4, TimeScale.SECONDS);
        }
        else {

            BarSender.send(player, this);
        }
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object object) {
        return object == this ||
                (object instanceof ActionBar && ((ActionBar) object)._text.equals(_text));
    }
}
