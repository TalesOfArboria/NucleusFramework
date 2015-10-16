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

package com.jcwhatever.nucleus.managed.resourcepacks;

import com.jcwhatever.nucleus.utils.managers.INamedManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Resource pack manager interface.
 */
public interface IResourcePackManager extends INamedManager<IResourcePack> {

    /**
     * Get the default resource pack.
     *
     * <p>This is the resource pack specified in the server.properties file and is
     * used whenever all other resource packs are removed from a player.</p>
     *
     * <p>Note that the resource pack cannot be removed, it can only be changed. If there
     * is no default resource pack, players will be stuck with the first resource pack applied
     * whenever all other packs are removed.</p>
     *
     * @return  The default resource pack or null if not set.
     */
    @Nullable
    IResourcePack getDefault();

    /**
     * Get the resource pack used by the specified world.
     *
     * @param world  The world to check.
     *
     * @return  The resource pack. If one is not set null is returned.
     */
    @Nullable
    IResourcePack getWorld(World world);

    /**
     * Set the resource pack to use for a specified world.
     *
     * @param world  The world.
     * @param pack   The resource pack or null to use the default.
     */
    void setWorld(World world, @Nullable IResourcePack pack);

    /**
     * Determine if the specified world requires the player to have its resource
     * pack loaded.
     *
     * @param world  The world to check.
     *
     * @return  True if the world requires a resource pack, otherwise false.
     */
    boolean isRequired(World world);

    /**
     * Set access to a world based on the player accepting and loading the
     * resource pack set for the specified world.
     *
     * @param world       The world
     * @param isRequired  True to require resource pack, otherwise false.
     */
    void setRequired(World world, boolean isRequired);

    /**
     * Create a new managed scoreboard.
     *
     * <p>The resource pack created is transient in nature and is not permanently stored.</p>
     *
     * @param name  The name to identify the resource pack.
     * @param url   The url of the resource pack.
     *
     * @return  The resource pack.
     */
    IResourcePack add(String name, String url);

    /**
     * Get a players {@link IPlayerResourcePacks} instance.
     *
     * @param player  The player.
     */
    IPlayerResourcePacks get(Player player);
}
