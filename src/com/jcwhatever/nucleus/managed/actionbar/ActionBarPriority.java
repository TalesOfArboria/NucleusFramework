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

package com.jcwhatever.nucleus.managed.actionbar;

/**
 * Specifies the priority of an action bar.
 *
 * <p>If an action bar with a higher priority is already being displayed to a
 * player, the action bar is not displayed.</p>
 *
 * <p>Action bars with equal priority are given a time slice to share time on
 * the players screen or simply display over the existing once, depending on
 * the action bar type.</p>
 */
public enum ActionBarPriority {
    /**
     * Low priority.
     *
     * <p>Used for unimportant, generally global, status messages that would not
     * cause problems if a player did not see it.</p>
     */
    LOW     (5),
    /**
     * The default priority.
     */
    DEFAULT (10),
    /**
     * High priority.
     *
     * <p>Used for very important messages such as NPC dialog for which the player
     * may not receive vital information if the dialog were to be overridden by
     * another action bar.</p>
     */
    HIGH   (15);

    private final int _value;

    ActionBarPriority (int value) {
        _value = value;
    }

    /**
     * Determine if the current priority is higher than the specified priority.
     *
     * @param priority  The priority to check.
     */
    public boolean isHigherPriority(ActionBarPriority priority) {
        return _value > priority._value;
    }
}
