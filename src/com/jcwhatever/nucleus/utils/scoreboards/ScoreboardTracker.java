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

package com.jcwhatever.nucleus.utils.scoreboards;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the scoreboards that players have viewed so they can be re-shown.
 *
 * <p>Helps reduce plugin scoreboard conflicts by acting as the central
 * scoreboard manager. Each plugin can simply apply their scoreboards via
 * {@link ManagedScoreboard} and {@link ScoreboardTracker} ensures that when
 * a plugin is done showing a scoreboard to a player, the previous scoreboard is
 * re-shown.</p>
 */
public final class ScoreboardTracker {

    private ScoreboardTracker() {}

    private static final Map<UUID, PlayerScoreboards> _playerMap =
            new PlayerMap<PlayerScoreboards>(Nucleus.getPlugin(), 35);

    private static final Object _sync = new Object();

    /**
     * Apply the scoreboard to the specified player.
     *
     * @param player  The player.
     *
     * @return  True if applied or transient, false if already applied.
     */
    public static boolean apply(Player player, IManagedScoreboard scoreboard) {
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
     * occurrence of the instance from the tracked stack of scoreboards the
     * player is viewing. If the removed scoreboard is the one the player
     * is currently viewing and if there is a previous scoreboard in the stack,
     * the previous scoreboard will be applied to the player automatically.</p>
     *
     * @param player  The player.
     */
    public static boolean remove(Player player, IManagedScoreboard scoreboard) {
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

    private static void setScoreboard(Player player, Scoreboard scoreboard) {
        player.setScoreboard(scoreboard);
    }

    private static class PlayerScoreboards extends LinkedList<IManagedScoreboard> {}
}
