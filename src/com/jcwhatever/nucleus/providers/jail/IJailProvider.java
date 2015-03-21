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

package com.jcwhatever.nucleus.providers.jail;

import com.jcwhatever.nucleus.providers.IProvider;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for a jail provider.
 */
public interface IJailProvider extends IProvider {

    /**
     * Get the servers default jail.
     */
    IJail getServerJail();

    /**
     * Create a new jail.
     *
     * @param plugin  The jails owning plugin.
     * @param name    The jails name.
     *
     * @return  The jail or null if failed.
     */
    @Nullable
    IJail createJail(Plugin plugin, String name);

    /**
     * Get a jail created by a plugin.
     *
     * @param plugin  The plugin that created the jail.
     * @param name    The name of the jail.
     *
     * @return  The jail or null if not found.
     */
    @Nullable
    IJail getJail(Plugin plugin, String name);

    /**
     * Get all jails.
     */
    Collection<IJail> getJails();

    /**
     * Get the jail session for a specified player.
     *
     * @param playerId  The ID of the player.
     *
     * @return  The jail session or null if the player is not imprisoned.
     */
    @Nullable
    IJailSession getSession(UUID playerId);

    /**
     * Get all current jail sessions.
     */
    Collection<IJailSession> getSessions();

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
}
