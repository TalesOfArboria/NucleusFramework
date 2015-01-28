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

package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.friends.FriendLevel;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Static methods for accessing the Friends provider.
 */
public class Friends {

    private Friends() {}

    /**
     * Get friends of the specified player.
     *
     * @param player  The player.
     */
    public static Collection<IFriend> getFriends(Player player) {
        PreCon.notNull(player);

        return provider().getFriends(player.getUniqueId());
    }

    /**
     * Get friends of the specified player.
     *
     * @param playerId  The ID of the player.
     */
    public static Collection<IFriend> getFriends(UUID playerId) {
        return provider().getFriends(playerId);
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param playerId  The ID of the player whose friend list is to be checked.
     * @param friendId  The ID of the player to check.
     */
    public static boolean isFriend(UUID playerId, UUID friendId){
        return provider().isFriend(playerId, friendId);
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param player    The Player whose friend list is to be checked.
     * @param friendId  The ID of the player to check.
     */
    public static boolean isFriend(Player player, UUID friendId){
        return provider().isFriend(player.getUniqueId(), friendId);
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param playerId  The ID of the player whose friend list is to be checked.
     * @param friend    The player to check.
     */
    public static boolean isFriend(UUID playerId, Player friend){
        return provider().isFriend(playerId, friend.getUniqueId());
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param player    The player whose friend list is to be checked.
     * @param friend    The player to check.
     */
    public static boolean isFriend(Player player, Player friend){
        return provider().isFriend(player.getUniqueId(), friend.getUniqueId());
    }

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
    public static IFriend getFriend(UUID playerId, UUID friendId) {
        return provider().getFriend(playerId, friendId);
    }

    /**
     * Get the friend object that represents the friend relationship
     * of the specified player to the specified friend player.
     *
     * @param player    The player to get the friend for.
     * @param friendId  The ID of the players friend.
     *
     * @return  The friend or null if the player is not friends with the specified friend.
     */
    @Nullable
    public static IFriend getFriend(Player player, UUID friendId) {
        return provider().getFriend(player.getUniqueId(), friendId);
    }

    /**
     * Get the friend object that represents the friend relationship
     * of the specified player to the specified friend player.
     *
     * @param playerId  The ID of the player to get the friend for.
     * @param friend    The players friend.
     *
     * @return  The friend or null if the player is not friends with the specified friend.
     */
    @Nullable
    public static IFriend getFriend(UUID playerId, Player friend) {
        return provider().getFriend(playerId, friend.getUniqueId());
    }

    /**
     * Get the friend object that represents the friend relationship
     * of the specified player to the specified friend player.
     *
     * @param player    The player to get the friend for.
     * @param friend    The players friend.
     *
     * @return  The friend or null if the player is not friends with the specified friend.
     */
    @Nullable
    public static IFriend getFriend(Player player, Player friend) {
        return provider().getFriend(player.getUniqueId(), friend.getUniqueId());
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param playerId  The ID of the player to add a friend to.
     * @param friendId  The ID of the player to become friends with.
     * @param level     The level of friendship.
     *
     * @return  The new or current {@code IFriend} object.
     */
    public static IFriend addFriend(UUID playerId, UUID friendId, FriendLevel level) {
        return provider().addFriend(playerId, friendId, level);
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param player    The player to add a friend to.
     * @param friendId  The ID of the player to become friends with.
     * @param level     The level of friendship.
     *
     * @return  The new or current {@code IFriend} object.
     */
    public static IFriend addFriend(Player player, UUID friendId, FriendLevel level) {
        return provider().addFriend(player.getUniqueId(), friendId, level);
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param playerId  The ID of the player to add a friend to.
     * @param friend    The player to become friends with.
     * @param level     The level of friendship.
     *
     * @return  The new or current {@code IFriend} object.
     */
    public static IFriend addFriend(UUID playerId, Player friend, FriendLevel level) {
        return provider().addFriend(playerId, friend.getUniqueId(), level);
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param player  The player to add a friend to.
     * @param friend  The player to become friends with.
     * @param level   The level of friendship.
     *
     * @return  The new or current {@code IFriend} object.
     */
    public static IFriend addFriend(Player player, Player friend, FriendLevel level) {
        return provider().addFriend(player.getUniqueId(), friend.getUniqueId(), level);
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param playerId  The ID of the player that the friend is being removed from.
     * @param friendId  The ID of the player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean removeFriend(UUID playerId, UUID friendId) {
        return provider().removeFriend(playerId, friendId);
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param player    The player that the friend is being removed from.
     * @param friendId  The ID of the player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean removeFriend(Player player, UUID friendId) {
        return provider().removeFriend(player.getUniqueId(), friendId);
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param playerId  The ID of the player that the friend is being removed from.
     * @param friend    The player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean removeFriend(UUID playerId, Player friend) {
        return provider().removeFriend(playerId, friend.getUniqueId());
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param player  The player that the friend is being removed from.
     * @param friend  The player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean removeFriend(Player player, Player friend) {
        return provider().removeFriend(player.getUniqueId(), friend.getUniqueId());
    }

    private static IFriendsProvider provider() {
        return Nucleus.getProviderManager().getFriendsProvider();
    }
}
