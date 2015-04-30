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

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.NameTagVisibility;

import java.util.Set;

/*
 * 
 */
public interface ITeam {

    /**
     * Get the team name.
     */
    String getName();

    /**
     * Get the team display name.
     */
    String getDisplayName();

    /**
     * Set the team display name.
     *
     * @param displayName  The display name.
     * @param args         Optional format arguments.
     */
    void setDisplayName(String displayName, Object... args);

    /**
     * Get the team prefix.
     */
    String getPrefix();

    /**
     * Set the team prefix.
     *
     * @param prefix  The prefix.
     * @param args    Optional format arguments.
     */
    void setPrefix(String prefix, Object... args);

    /**
     * Get the team suffix.
     */
    String getSuffix();

    /**
     * Set the team suffix.
     *
     * @param suffix  The team suffix.
     * @param args    Optional format arguments.
     */
    void setSuffix(String suffix, Object... args);

    /**
     * Determine if friendly fire is allowed.
     */
    boolean allowFriendlyFire();

    /**
     * Set friendly fire allowed.
     *
     * @param isAllowed  True to allow, otherwise false.
     */
    void setAllowFriendlyFire(boolean isAllowed);

    /**
     * Determine if team mates can see each other when invisible.
     */
    boolean canSeeFriendlyInvisibles();

    /**
     * Set team mates can see each other when invisible.
     *
     * @param canSee  True to allow, otherwise false.
     */
    void setCanSeeFriendlyInvisibles(boolean canSee);

    /**
     * Get name tag visibility.
     */
    NameTagVisibility getNameTagVisibility();

    /**
     * Set the name tag visibility.
     */
    void setNameTagVisibility(NameTagVisibility nameTagVisibility);

    /**
     * Get players on team.
     */
    Set<OfflinePlayer> getPlayers();

    /**
     * Get all entry names.
     */
    Set<String> getEntries();

    /**
     * Get the number of entries on the team.
     */
    int getSize();

    /**
     * Get the parent scoreboard.
     */
    IManagedScoreboard getScoreboard();

    /**
     * Add a player to the team.
     *
     * @param player  The player to add.
     */
    void addPlayer(OfflinePlayer player);

    /**
     * Add an entry to the team.
     *
     * @param entry  The entry to add.
     */
    void addEntry(String entry);

    /**
     * Remove a player from the team.
     *
     * @param player  The player to remove.
     *
     * @return  True if removed, otherwise false.
     */
    boolean removePlayer(OfflinePlayer player);

    /**
     * Remove an entry from the team.
     *
     * @param entry  The entry to remove.
     *
     * @return  True if removed, otherwise false.
     */
    boolean removeEntry(String entry);

    /**
     * Unregister team from scoreboard.
     */
    void unregister();

    /**
     * Determine if a player is on the team.
     *
     * @param player  The player to check.
     */
    boolean hasPlayer(OfflinePlayer player);

    /**
     * Determine if an entry is on the team.
     *
     * @param entry  The entry to check.
     */
    boolean hasEntry(String entry);
}
