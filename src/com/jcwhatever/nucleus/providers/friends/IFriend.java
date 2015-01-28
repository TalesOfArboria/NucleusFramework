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

package com.jcwhatever.nucleus.providers.friends;

import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * An interface for a type that represents a player that is a friend
 * of another player.
 *
 * <p>The friend object should not be a singleton for each player. Each friend
 * object represents a players relationship with another specified player
 * (The friend-of player).</p>
 */
public interface IFriend {

    /**
     * Get the ID of the player that the friend player is a friend of.
     */
    UUID getFriendOfId();

    /**
     * Get the ID of the friend player.
     */
    UUID getPlayerId();

    /**
     * Get the players name.
     */
    String getName();

    /**
     * Get the date/time that the friend-of befriended the friend player.
     */
    Date getBefriendDate();

    /**
     * Determine if the friendship is still valid. Normally returns true unless
     * the friend-of player has removed the friend player.
     */
    boolean isValid();

    /**
     * Get the {@code IFriend} object is the mutual friendship between the
     * two players. (Friend-of player and friend player). Returns null if the
     * friendship is not mutual.
     */
    @Nullable
    IFriend getMutualFriend();

    /**
     * Get a permission added by a specific plugin. Permissions are provided by
     * the plugins for its own use and allow plugins to specify permissions a friend player
     * has been given by the friend-of player.
     *
     * @param plugin  The plugin to get permissions for.
     */
    Set<String> getPermissions(Plugin plugin);

    /**
     * Add a permission to the friend.
     *
     * @param plugin      The plugin that owns the permission.
     * @param permission  The permission to add.
     *
     * @return  True if the permissions were modified.
     */
    boolean addPermission(Plugin plugin, String permission);

    /**
     * Remove a permission from the friend.
     *
     * @param plugin      The plugin that owns the permission.
     * @param permission  The permission to remove.
     *
     * @return  True if the permissions were modified.
     */
    boolean removePermission(Plugin plugin, String permission);
}
