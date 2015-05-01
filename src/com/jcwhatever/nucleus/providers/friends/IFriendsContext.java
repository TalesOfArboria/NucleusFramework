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

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface that represents friendships in a specific context.
 */
public interface IFriendsContext {

    /**
     * Get friends of the specified player.
     *
     * @param playerId  The ID of the player.
     */
    Collection<IFriend> getAll(UUID playerId);

    /**
     * Get friends of the specified player.
     *
     * @param playerId  The ID of the player.
     * @param output    The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<IFriend>> T getAll(UUID playerId, T output);

    /**
     * Get the friend object that represents the friend relationship
     * of the specified player to the specified friend player.
     *
     * @param playerId  The ID of the player to get the friend for.
     * @param friendId  The ID of the players friend.
     *
     * @return  The friend or null if the player is not friends with the specified friend.
     */
    @Nullable
    IFriend get(UUID playerId, UUID friendId);

    /**
     * Determine if the specified player has added the second specified player as
     * a friend.
     *
     * @param playerId  The ID of the player whose friend list is to be checked.
     * @param friendId  The ID of the player to check is a friend.
     */
    boolean isFriend(UUID playerId, UUID friendId);

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param playerId  The ID of the player to add a friend to.
     * @param friendId  The ID of the player to become friends with.
     * @param level     The level of friendship.
     *
     * @return  The new or current {@link IFriend} object.
     */
    IFriend add(UUID playerId, UUID friendId, IFriendLevel level);

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param playerId  The ID of the player to add a friend to.
     * @param friendId  The ID of the player to become friends with.
     * @param rawLevel  The raw value level of friendship.
     *
     * @return  The new or current {@link IFriend} object.
     */
    IFriend add(UUID playerId, UUID friendId, int rawLevel);

    /**
     * Remove a friend from the specified player.
     *
     * @param playerId  The ID of the player that the friend is being removed from.
     * @param friendId  The ID of the player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    boolean remove(UUID playerId, UUID friendId);
}
