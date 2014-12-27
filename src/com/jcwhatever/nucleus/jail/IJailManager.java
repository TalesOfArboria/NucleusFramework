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

package com.jcwhatever.nucleus.jail;

import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Jail manager interface.
 */
public interface IJailManager {

    /**
     * Register a jail instance.
     *
     * @param jail  The jail to register.
     */
    void registerJail(Jail jail);

    /**
     * Unregister a jail instance.
     *
     * @param jail  The jail to unregister.
     */
    void unregisterJail(Jail jail);

    /**
     * Get a jail by plugin and name.
     *
     * @param plugin  The plugin.
     * @param name    The jail name.
     *
     * @return  Null if not found.
     */
    @Nullable
    Jail getJail(Plugin plugin, String name);

    /**
     * Get all registered jails.
     * @return
     */
    List<Jail> getJails();

    /**
     * Register a jail session.
     *
     * @param jail      The jail.
     * @param playerId  The ID of the imprisoned player.
     * @param minutes   The number of minutes the session will last.
     *
     * @return  Null if the player is already in a jail session.
     */
    @Nullable
    JailSession registerJailSession(Jail jail, UUID playerId, int minutes);

    /**
     * Register a jail session.
     *
     * @param jail      The jail.
     * @param playerId  The ID of the imprisoned player.
     * @param expires   The date/time the imprisonment ends.
     *
     * @return  Null if the player is already in a jail session.
     */
    @Nullable
    JailSession registerJailSession(Jail jail, UUID playerId, Date expires);

    /**
     * Unregister a player from their current jail session.
     *
     * @param playerId  The id of the imprisoned player.
     */
    void unregisterJailSession(UUID playerId);

    /**
     * Get a players current jail session.
     *
     * @param playerId  The id of the player.
     *
     * @return  Null if the player is not imprisoned.
     */
    @Nullable
    JailSession getSession(UUID playerId);

    /**
     * Get all current jail sessions.
     */
    List<JailSession> getSessions();

    /**
     * Determine if a player is imprisoned in any registered jails.
     *
     * @param playerId  The ID of the player to check.
     */
    boolean isPrisoner(UUID playerId);

    /**
     * Release a player from their current imprisonment session.
     *
     * @param playerId  The ID of the player to release.
     *
     * @return  True if released, false if release failed or player is not imprisoned.
     */
    boolean release(UUID playerId);

    /**
     * Determine if the player is pending release on their next login.
     *
     * @param playerId  The ID of the player to check.
     */
    boolean isLateRelease(UUID playerId);
}
