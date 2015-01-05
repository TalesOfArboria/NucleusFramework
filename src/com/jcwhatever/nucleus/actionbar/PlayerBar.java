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

import com.jcwhatever.nucleus.collections.timed.TimeScale;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/*
 * 
 */
final class PlayerBar {
    private final PersistentActionBar _bar;
    private final Player _player;
    private final long _expires;
    private volatile long _nextUpdate;

    PlayerBar(Player player, PersistentActionBar bar, int duration, @Nullable TimeScale timeScale) {
        _player = player;
        _bar = bar;

        _expires = timeScale != null && bar instanceof TimedActionBar
                ? System.currentTimeMillis() + (duration * timeScale.getTimeFactor())
                : 0;
    }

    public PersistentActionBar bar() {
        return _bar;
    }

    public Player player() {
        return _player;
    }

    public long expires() {
        return _expires;
    }

    public long nextUpdate() {
        return _nextUpdate;
    }

    public synchronized void send() {
        _nextUpdate = BarSender.send(_player, _bar);
    }

    @Override
    public int hashCode() {
        return _bar.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerBar && ((PlayerBar) obj)._bar.equals(_bar);
    }
}
