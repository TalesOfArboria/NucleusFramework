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

package com.jcwhatever.nucleus.internal.actionbar;

import com.jcwhatever.nucleus.managed.actionbar.ITimedActionBar;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

/**
 * A {@link PersistentActionBar} that is automatically removed when the
 * specified duration ends.
 */
class TimedActionBar extends PersistentActionBar implements ITimedActionBar {

    /**
     * Constructor.
     *
     * <p>The default duration of the {@link TimedActionBar}
     * is 3 seconds.</p>
     *
     * @param text  The action bar text.
     */
    public TimedActionBar(CharSequence text) {
        super(text);
    }

    /**
     * Constructor.
     *
     * <p>The default duration of the {@link TimedActionBar}
     * is 3 seconds.</p>
     *
     * @param dynamicText  The action bar dynamic text.
     */
    public TimedActionBar(IDynamicText dynamicText) {
        super(dynamicText);
    }

    /**
     * Constructor.
     *
     * @param text         The action bar text.
     * @param duration     The default duration the bar is shown for.
     * @param timeScale    The default duration time scale.
     */
    public TimedActionBar(CharSequence text, int duration, TimeScale timeScale) {
        super(text, duration, timeScale);
    }

    /**
     * Constructor.
     *
     * @param dynamicText  The action bar dynamic text.
     * @param duration     The default duration the bar is show for.
     * @param timeScale    The default duration time scale.
     */
    public TimedActionBar(IDynamicText dynamicText, int duration, TimeScale timeScale) {
        super(dynamicText, duration, timeScale);
    }

    /**
     * Get the {@link PersistentActionBar} default time slice
     * duration used when a player is shown more than 1
     * {@link PersistentActionBar}.
     */
    @Override
    public int getDuration() {
        return getMinDuration();
    }
}
