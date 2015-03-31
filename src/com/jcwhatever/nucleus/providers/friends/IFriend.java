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

import java.util.Date;
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
     * Get the ID of the player that the instance was generated for.
     */
    UUID getSourceId();

    /**
     * Get the ID of the friend player.
     */
    UUID getFriendId();

    /**
     * Get the players name.
     */
    String getName();

    /**
     * Get the date/time that the friend was befriended.
     */
    Date getBefriendDate();

    /**
     * Determine if the friendship is still valid. Normally returns true unless
     * the friend-of player has removed the friend player.
     */
    boolean isValid();

    /**
     * Get the reverse {@link IFriend} instance that exists if the friendship is mutual.
     *
     * <p>This is equivalent to invoking {@link IFriendsContext#get} using the
     * {@link #getFriendId()} as the playerId argument and {@link #getSourceId} as the
     * friendId argument.</p>
     *
     * @return  The reverse {@link IFriend} instance or null if the friendship is not mutual.
     */
    @Nullable
    IFriend getMutualFriend();

    /**
     * Get the level of friendship.
     *
     * <p>Attempts to get the registered {@link IFriendLevel} instance of the specified type.</p>
     *
     * @return  The nearest {@link IFriendLevel} instance that matches the specified type and
     * is identical to or lower than the raw friendship level.
     */
    @Nullable
    <T extends IFriendLevel> T getLevel(Class<T> levelClass);

    /**
     * Get the level of friendship.
     *
     * @return  The nearest {@link IFriendLevel} instance that is identical or lower than the
     * raw friendship level.
     */
    IFriendLevel getLevel();

    /**
     * Get the level of friendship.
     *
     * <p>Higher values indicate a higher level of friendship. (closer relationship)</p>
     */
    int getRawLevel();

    /**
     * Set the raw level of friendship.
     *
     * @param level  The level.
     */
    void setRawLevel(int level);
}
