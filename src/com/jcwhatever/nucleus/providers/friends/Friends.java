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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Static convenience methods for accessing the Friend providers
 * default context.
 */
public final class Friends {

    private Friends() {}

    /**
     * Get friends of the specified player.
     *
     * @param player  The player.
     */
    public static Collection<IFriend> getAll(Player player) {
        PreCon.notNull(player);

        return provider().getDefaultContext().getAll(player.getUniqueId());
    }

    /**
     * Get friends of the specified player.
     *
     * @param playerId  The ID of the player.
     */
    public static Collection<IFriend> getAll(UUID playerId) {
        return provider().getDefaultContext().getAll(playerId);
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param playerId  The ID of the player whose friend list is to be checked.
     * @param friendId  The ID of the player to check.
     */
    public static boolean isFriend(UUID playerId, UUID friendId){
        return provider().getDefaultContext().isFriend(playerId, friendId);
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param player    The Player whose friend list is to be checked.
     * @param friendId  The ID of the player to check.
     */
    public static boolean isFriend(Player player, UUID friendId){
        return provider().getDefaultContext().isFriend(player.getUniqueId(), friendId);
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param playerId  The ID of the player whose friend list is to be checked.
     * @param friend    The player to check.
     */
    public static boolean isFriend(UUID playerId, Player friend){
        return provider().getDefaultContext().isFriend(playerId, friend.getUniqueId());
    }

    /**
     * Determine if the specified friend is in the specified
     * players friend list.
     *
     * @param player    The player whose friend list is to be checked.
     * @param friend    The player to check.
     */
    public static boolean isFriend(Player player, Player friend){
        return provider().getDefaultContext().isFriend(player.getUniqueId(), friend.getUniqueId());
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
    public static IFriend get(UUID playerId, UUID friendId) {
        return provider().getDefaultContext().get(playerId, friendId);
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
    public static IFriend get(Player player, UUID friendId) {
        return provider().getDefaultContext().get(player.getUniqueId(), friendId);
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
    public static IFriend get(UUID playerId, Player friend) {
        return provider().getDefaultContext().get(playerId, friend.getUniqueId());
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
    public static IFriend get(Player player, Player friend) {
        return provider().getDefaultContext().get(player.getUniqueId(), friend.getUniqueId());
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
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(UUID playerId, UUID friendId, IFriendLevel level) {
        return provider().getDefaultContext().add(playerId, friendId, level);
    }

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
    public static IFriend add(UUID playerId, UUID friendId, int rawLevel) {
        return provider().getDefaultContext().add(playerId, friendId, rawLevel);
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
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(Player player, UUID friendId, IFriendLevel level) {
        return provider().getDefaultContext().add(player.getUniqueId(), friendId, level);
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param player    The player to add a friend to.
     * @param friendId  The ID of the player to become friends with.
     * @param rawLevel  The raw value level of friendship.
     *
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(Player player, UUID friendId, int rawLevel) {
        return provider().getDefaultContext().add(player.getUniqueId(), friendId, rawLevel);
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
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(UUID playerId, Player friend, IFriendLevel level) {
        return provider().getDefaultContext().add(playerId, friend.getUniqueId(), level);
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param playerId  The ID of the player to add a friend to.
     * @param friend    The player to become friends with.
     * @param rawLevel  The raw value level of friendship.
     *
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(UUID playerId, Player friend, int rawLevel) {
        return provider().getDefaultContext().add(playerId, friend.getUniqueId(), rawLevel);
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
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(Player player, Player friend, IFriendLevel level) {
        return provider().getDefaultContext().add(player.getUniqueId(), friend.getUniqueId(), level);
    }

    /**
     * Add a friend to the specified player. If the specified friend
     * is already a friend of the player, the existing friend is
     * returned.
     *
     * @param player    The player to add a friend to.
     * @param friend    The player to become friends with.
     * @param rawLevel  The raw value level of friendship.
     *
     * @return  The new or current {@link IFriend} object.
     */
    public static IFriend add(Player player, Player friend, int rawLevel) {
        return provider().getDefaultContext().add(player.getUniqueId(), friend.getUniqueId(), rawLevel);
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param playerId  The ID of the player that the friend is being removed from.
     * @param friendId  The ID of the player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean remove(UUID playerId, UUID friendId) {
        return provider().getDefaultContext().remove(playerId, friendId);
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param player    The player that the friend is being removed from.
     * @param friendId  The ID of the player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean remove(Player player, UUID friendId) {
        return provider().getDefaultContext().remove(player.getUniqueId(), friendId);
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param playerId  The ID of the player that the friend is being removed from.
     * @param friend    The player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean remove(UUID playerId, Player friend) {
        return provider().getDefaultContext().remove(playerId, friend.getUniqueId());
    }

    /**
     * Remove a friend from the specified player.
     *
     * @param player  The player that the friend is being removed from.
     * @param friend  The player friend being removed.
     *
     * @return  True if the friend was found and removed.
     */
    public static boolean remove(Player player, Player friend) {
        return provider().getDefaultContext().remove(player.getUniqueId(), friend.getUniqueId());
    }

    private static IFriendsProvider provider() {
        return Nucleus.getProviderManager().getFriendsProvider();
    }
}
