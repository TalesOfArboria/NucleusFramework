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

package com.jcwhatever.nucleus.internal.scoreboards;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scoreboards.IManagedScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboardExtension;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboardTracker;
import com.jcwhatever.nucleus.managed.scoreboards.ScoreboardLifespan;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IScoreboardTracker}.
 */
public class InternalScoreboardTracker implements IScoreboardTracker {

    private final Map<UUID, PlayerScoreboards> _playerMap =
            new PlayerMap<PlayerScoreboards>(Nucleus.getPlugin(), 35);

    private final Object _sync = new Object();

    @Override
    public IManagedScoreboard manage(Scoreboard scoreboard, ScoreboardLifespan lifespan) {
        return manage(scoreboard, lifespan, null);
    }

    @Override
    public IManagedScoreboard manage(Scoreboard scoreboard, ScoreboardLifespan lifespan,
                                     @Nullable IScoreboardExtension extension) {
        PreCon.notNull(scoreboard);
        PreCon.notNull(lifespan);

        return new InternalManagedScoreboard(this, scoreboard, lifespan, extension);
    }

    /**
     * Apply the scoreboard to the specified player.
     *
     * <p>Use this method instead of directly setting the scoreboard on the player.</p>
     *
     * @param player      The player.
     * @param scoreboard  The scoreboard to apply.
     *
     * @return  True if applied or transient, false if already applied.
     */
    public boolean apply(Player player, IManagedScoreboard scoreboard) {
        PreCon.notNull(player);
        PreCon.notNull(scoreboard);

        // get the list of scoreboard instances applied to the player
        PlayerScoreboards scoreboards;

        synchronized (_sync) {

            if (scoreboard.getLifespan() == ScoreboardLifespan.TRANSIENT) {
                player.setScoreboard(scoreboard.getScoreboard());
                return true;
            }

            scoreboards = _playerMap.get(player.getUniqueId());

            // add a new list if one doesn't exist
            if (scoreboards == null) {
                scoreboards = new PlayerScoreboards();
                _playerMap.put(player.getUniqueId(), scoreboards);
            }

            // apply scoreboard
            player.setScoreboard(scoreboard.getScoreboard());

            // make sure the scoreboard isn't already the most recent scoreboard
            if (!scoreboards.isEmpty()) {
                IManagedScoreboard current = scoreboards.peek();
                if (scoreboard.equals(current)) {
                    return false;
                }
            }

            // push scoreboard
            scoreboards.push(scoreboard);
        }

        return true;
    }

    /**
     * Remove the scoreboard from the player.
     *
     * <p>Removes the scoreboard from the players view and removes the last
     * occurrence from the tracked stack of scoreboards the player is viewing.
     * If the removed scoreboard is the one the player is currently viewing and if
     * there is a previous scoreboard in the stack, the previous scoreboard will be
     * applied to the player automatically.</p>
     *
     * <p>Use this method instead of directly setting the scoreboard on the player.</p>
     *
     * @param player      The player.
     * @param scoreboard  The scoreboard to remove.
     */
    public boolean remove(Player player, IManagedScoreboard scoreboard) {
        PreCon.notNull(player);
        PreCon.notNull(scoreboard);

        synchronized (_sync) {

            // get the list of scoreboard instances applied to the player
            PlayerScoreboards scoreboards = _playerMap.get(player.getUniqueId());
            if ((scoreboards == null || scoreboards.isEmpty()) &&
                    scoreboard.getLifespan() != ScoreboardLifespan.TRANSIENT) {
                return false;
            }

            if (scoreboard.getLifespan() == ScoreboardLifespan.TRANSIENT) {

                IManagedScoreboard managed = scoreboards != null && !scoreboards.isEmpty()
                        ? scoreboards.peek()
                        : null;

                if (scoreboard.getScoreboard().equals(player.getScoreboard())) {
                    setScoreboard(player, managed != null ? managed.getScoreboard() : null);
                }
                return true;
            }

            IManagedScoreboard current = scoreboards.peek();
            IManagedScoreboard next = null;
            boolean isRemoved;

            if (scoreboard.equals(current)) {
                scoreboards.pop();
                isRemoved = true;

                if (!scoreboards.isEmpty()) {
                    next = scoreboards.peek();
                }
            } else {
                isRemoved = scoreboards.removeLastOccurrence(scoreboard);
            }

            if (next != null) {
                setScoreboard(player, next.getScoreboard());
            }
            else if (scoreboard.getScoreboard().equals(player.getScoreboard())) {
                setScoreboard(player, null);
            }

            return isRemoved;
        }
    }

    private void setScoreboard(Player player, Scoreboard scoreboard) {
        player.setScoreboard(scoreboard);
    }

    private static class PlayerScoreboards extends LinkedList<IManagedScoreboard> {}
}
