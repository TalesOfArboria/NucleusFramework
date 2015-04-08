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

package com.jcwhatever.nucleus.internal.managed.titles;

import com.jcwhatever.nucleus.managed.titles.ITitle;
import com.jcwhatever.nucleus.managed.titles.ITitleManager;

import org.bukkit.entity.Player;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link ITitleManager}.
 */
public final class InternalTitleManager implements ITitleManager {

    @Override
    public ITitle create(String title) {
        return new Title(title, null);
    }

    @Override
    public ITitle create(String title, @Nullable String subTitle) {
        return new Title(title, subTitle);
    }

    @Override
    public ITitle create(String title, int fadeInTime, int stayTime, int fadeOutTime) {
        return new Title(title, null, fadeInTime, stayTime, fadeOutTime);
    }

    @Override
    public ITitle create(String title, @Nullable String subTitle, int fadeInTime, int stayTime, int fadeOutTime) {
        return new Title(title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    @Override
    public void showTo(Player player, String title) {
        create(title).showTo(player);
    }

    @Override
    public void showTo(Player player, String title, @Nullable String subTitle) {
        create(title, subTitle).showTo(player);
    }

    @Override
    public void showTo(Player player, String title,
                       int fadeInTime, int stayTime, int fadeOutTime) {
        create(title, fadeInTime, stayTime, fadeOutTime).showTo(player);
    }

    @Override
    public void showTo(Player player, String title, @Nullable String subTitle,
                       int fadeInTime, int stayTime, int fadeOutTime) {
        create(title, subTitle, fadeInTime, stayTime, fadeOutTime).showTo(player);
    }

    @Override
    public void showTo(Collection<? extends Player> players, String title) {
        create(title).showTo(players);
    }

    @Override
    public void showTo(Collection<? extends Player> players,
                       String title, @Nullable String subTitle) {
        create(title, subTitle).showTo(players);
    }

    @Override
    public void showTo(Collection<? extends Player> players, String title,
                       int fadeInTime, int stayTime, int fadeOutTime) {
        create(title, fadeInTime, stayTime, fadeOutTime).showTo(players);
    }

    @Override
    public void showTo(Collection<? extends Player> players,
                       String title, @Nullable String subTitle,
                       int fadeInTime, int stayTime, int fadeOutTime) {
        create(title, fadeInTime, stayTime, fadeOutTime).showTo(players);
    }
}
