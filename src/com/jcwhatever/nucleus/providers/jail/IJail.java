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

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.regions.Region;
import com.jcwhatever.nucleus.utils.coords.NamedLocation;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;

/**
 * Interface for a jail.
 */
public interface IJail extends IPluginOwned, INamedInsensitive, IDisposable {

    /**
     * Imprison a player in the jail.
     *
     * @param player     The player to imprison.
     * @param duration   The duration to imprison the player for.
     * @param timeScale  The time scale of the specified duration.
     *
     * @return Null if failed to imprison player.
     */
    @Nullable
    IJailSession imprison(Player player, int duration, TimeScale timeScale);

    /**
     * Imprison a player in the jail.
     *
     * @param player   The player to imprison.
     * @param expires  The date and time the session will expire. (prisoner release date)
     *
     * @return Null if failed to imprison player.
     */
    @Nullable
    IJailSession imprison(Player player, Date expires);

    /**
     * Determine if a player is a prisoner of the jail.
     *
     * @param p  The player to check.
     */
    boolean isPrisoner(Player p);

    /**
     * Get the bounding region of the jail.
     */
    Region getRegion();

    /**
     * Add a location the player can be teleported to when imprisoned.
     *
     * @param name      The name of the location.
     * @param teleport  The teleport location.
     *
     * @return  True if the new teleport was added. Will fail if the name is in use.
     */
    boolean addTeleport(String name, Location teleport);

    /**
     * Add a location the player can be teleported to when imprisoned.
     *
     * @param teleport  The teleport location.
     *
     * @return  True if the new teleport was added. Will fail if the name is in use.
     */
    boolean addTeleport(NamedLocation teleport);

    /**
     * Remove a jail teleport location.
     *
     * @param name  The name of the location.
     *
     * @return  True if found and removed.
     */
    boolean removeTeleport(String name);

    /**
     * Get a random jail teleport location.
     */
    @Nullable
    NamedLocation getRandomTeleport();

    /**
     * Get a jail teleport location by name.
     *
     * @param name  The name of the location.
     */
    @Nullable
    NamedLocation getTeleport(String name);

    /**
     * Get all jail teleport locations.
     */
    Collection<NamedLocation> getTeleports();

    /**
     * Get the location a player is teleported to when
     * released.
     */
    @Nullable
    Location getReleaseLocation();

    /**
     * Set the location a player is teleported to when
     * released.
     *
     * @param location  The release location.
     */
    void setReleaseLocation(@Nullable Location location);
}
