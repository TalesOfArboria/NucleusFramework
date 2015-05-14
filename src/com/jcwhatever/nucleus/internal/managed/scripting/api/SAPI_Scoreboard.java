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

package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerSet;
import com.jcwhatever.nucleus.managed.scoreboards.IManagedScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboardExtension;
import com.jcwhatever.nucleus.managed.scoreboards.ScoreboardLifespan;
import com.jcwhatever.nucleus.mixins.IDisposable;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Scoreboard script API object.
 */
public class SAPI_Scoreboard implements IDisposable {

    private boolean _isDisposed;

    private Map<IManagedScoreboard, Void> _scoreboards = new WeakHashMap<>(10);

    /**
     * Create a new managed scoreboard using a new Bukkit scoreboard.
     */
    public IManagedScoreboard create(@Nullable Runnable onApply, @Nullable Runnable onRemove) {

        IManagedScoreboard managedScoreboard = Nucleus.getScoreboardTracker()
                .create(ScoreboardLifespan.PERSISTENT,
                        new ScoreboardExtension(onApply, onRemove));

        _scoreboards.put(managedScoreboard, null);
        return managedScoreboard;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        for (IManagedScoreboard scoreboard : _scoreboards.keySet()) {

            ScoreboardExtension extension = (ScoreboardExtension)scoreboard.getExtension();
            assert extension != null;

            List<Player> players = new ArrayList<>(extension.visible);
            for (Player player : players) {
                scoreboard.remove(player);
                scoreboard.dispose();
            }
            extension.visible.clear();
        }

        _scoreboards.clear();

        _isDisposed = true;
    }

    private static class ScoreboardExtension implements IScoreboardExtension {

        final Runnable apply;
        final Runnable remove;
        final Set<Player> visible = new PlayerSet(Nucleus.getPlugin());

        ScoreboardExtension(@Nullable Runnable apply, @Nullable Runnable remove) {
            this.apply = apply;
            this.remove = remove;
        }

        @Override
        public void onAttach(IManagedScoreboard scoreboard) {
            // do nothing
        }

        @Override
        public void onApply(Player player, IManagedScoreboard scoreboard) {
            visible.add(player);

            if (apply != null)
                apply.run();
        }

        @Override
        public void onRemove(Player player, IManagedScoreboard scoreboard) {
            visible.remove(player);

            if (remove != null)
                remove.run();
        }
    }
}
