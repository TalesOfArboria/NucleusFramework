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

package com.jcwhatever.nucleus.managed.scoreboards;

import com.jcwhatever.nucleus.mixins.IDisposable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * Wraps a {@link org.bukkit.scoreboard.Scoreboard} so that it can be tracked
 * by an {@link IScoreboardTracker}.
 *
 * <p>Scoreboards applied to a player are tracked. If another scoreboard is applied
 * then removed, the previous scoreboard is applied. This does not apply to transient
 * scoreboards which are removed when another scoreboard is shown.</p>
 *
 * <p>When the scoreboard is no longer in use, invoke the {@link #dispose} method to
 * unregister objectives and flag the {@link IManagedScoreboard} as disposed.</p>
 *
 * @see IScoreboardTracker
 */
public interface IManagedScoreboard extends IDisposable {

    /**
     * Determine how the scoreboards lifespan is handled.
     */
    ScoreboardLifespan getLifespan();

    /**
     * Apply the scoreboard to the specified player.
     *
     * <p>Use this method instead of directly setting the scoreboard on the player.</p>
     *
     * @param player  The player.
     *
     * @return  True if applied or transient, false if already applied.
     */
    boolean apply(Player player);

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
     * @param player  The player.
     *
     * @return  True if found and removed, otherwise false.
     */
    boolean remove(Player player);

    /**
     * Get the scoreboard extension.
     *
     * @see IScoreboardExtension
     */
    @Nullable
    IScoreboardExtension getExtension();

    /**
     * Register an get a new scoreboard objective.
     *
     * @param name      The name of the objective.
     * @param criteria  The objective criteria.
     *
     * @return  The new objective.
     */
    IObjective registerNewObjective(String name, String criteria);

    /**
     * Get a registered objective by name.
     *
     * @param name  The name of the objective.
     *
     * @return  The objective or null if not found.
     */
    IObjective getObjective(String name);

    /**
     * Get all registered objectives with the specified criteria.
     *
     * @param criteria  The criteria to search for.
     */
    Set<IObjective> getObjectivesByCriteria(String criteria);

    /**
     * Get all registered objectives.
     */
    Set<IObjective> getObjectives();

    /**
     * Get the objective assigned to the specified display slot.
     *
     * @param slot  The display slot.
     */
    IObjective getObjective(DisplaySlot slot);

    /**
     * Get scores for the specified entry.
     *
     * @param entry  The entry.
     */
    Set<IScore> getScores(String entry);

    /**
     * Reset the scores for a specified entry.
     *
     * @param entry  The entry.
     */
    void resetScores(String entry);

    /**
     * Register a new team.
     *
     * @param name  The name of the team.
     */
    ITeam registerNewTeam(String name);

    /**
     * Get the team a player is on.
     *
     * @param player  The player to check.
     */
    ITeam getPlayerTeam(OfflinePlayer player);

    /**
     * Get a team by name.
     *
     * @param name  The name of the team.
     */
    ITeam getTeam(String name);

    /**
     * Get all registered teams.
     */
    Set<ITeam> getTeams();

    /**
     * Get all score entries.
     */
    Set<String> getEntries();

    /**
     * Clear display slot.
     *
     * @param slot  The display slot.
     */
    void clearSlot(DisplaySlot slot);
}
