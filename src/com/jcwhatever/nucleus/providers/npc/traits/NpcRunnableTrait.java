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

package com.jcwhatever.nucleus.providers.npc.traits;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * A {@link NpcTrait} that implements {@link java.lang.Runnable} and allows adjusting
 * the interval that the trait runs.
 */
public abstract class NpcRunnableTrait extends NpcTrait implements Runnable {

    private int _interval = 1;
    private int _currentInterval = 1;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    public NpcRunnableTrait(INpc npc, NpcTraitType type) {
        super(npc, type);
    }

    /**
     * Get the interval in ticks that the trait is run at.
     */
    public int getInterval() {
        return _interval;
    }

    /**
     * Set the interval in ticks that the trait is run at.
     *
     * @param interval  The interval in ticks. Must be greater than 0.
     *
     * @return  Self for chaining.
     */
    public NpcRunnableTrait setInterval(int interval) {
        PreCon.greaterThanZero(interval);

        _interval = interval;
        if (_currentInterval > interval)
            _currentInterval = interval;

        return this;
    }

    @Override
    public final void run() {
        if (_currentInterval <= 1) {
            onRun();
            _currentInterval = _interval;
        }
        else {
            _currentInterval--;
        }
    }

    /**
     * Invoked when the trait is run.
     */
    protected abstract void onRun();
}
