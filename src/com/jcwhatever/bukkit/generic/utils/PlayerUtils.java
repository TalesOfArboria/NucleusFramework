/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.internal.PlayerTracker;
import com.jcwhatever.bukkit.generic.mixins.IPlayerWrapper;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Provides {@code Player} related utilities.
 */
public final class PlayerUtils {

    private PlayerUtils() {}

    /**
     * Determine if a player is online.
     *
     * @param p  The player to check.
     */
    public static boolean isOnline(Player p) {
        PreCon.notNull(p);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player plyr : players) {
            if (plyr.getUniqueId().equals(p.getUniqueId()))
                return true;
        }
        return false;
    }

    /**
     * Get the time the player logged in for the current session.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player is not logged in.
     */
    @Nullable
    public static Date getLoginDate(Player player) {
        return PlayerTracker.get().getLoginDate(player);
    }

    /**
     * Get the time the player last changed worlds in the current session.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player is not logged in.
     */
    @Nullable
    public static Date getLastWorldChangeDate(Player player) {
        return PlayerTracker.get().getLastWorldChangeDate(player);
    }

    /**
     * Get the number of milliseconds the player has been on
     * the server during the current login session.
     *
     * @param player  The player to check.
     *
     * @return  0 if the player is not online.
     */
    public static long getSessionTime(Player player) {
        return PlayerTracker.get().getSessionTime(player);
    }

    /**
     * Get the number of milliseconds the player has been in
     * the world they are currently in.
     *
     * @param player  The player to check.
     *
     * @return 0 if the player is not online.
     */
    public static long getWorldSessionTime(Player player) {
        return PlayerTracker.get().getWorldSessionTime(player);
    }

    /**
     * Get an online player by name.
     *
     * @param playerName  The name of the player.
     */
    @Nullable
    public static Player getPlayer(String playerName) {
        PreCon.notNullOrEmpty(playerName);

        UUID playerId = getPlayerId(playerName);
        if (playerId == null)
            return null;

        return Bukkit.getServer().getPlayer(playerId);
    }

    /**
     * Get an online player by Id.
     *
     * @param playerId  The id of the player.
     */
    @Nullable
    public static Player getPlayer(UUID playerId) {
        PreCon.notNull(playerId);

        return Bukkit.getServer().getPlayer(playerId);
    }

    /**
     * Get player instance from provided object.
     *
     * <p>Attempts to retrieve the player object from one of the following
     * types of objects:</p>
     *
     * <p>{@code Player}, {@code IPlayerWrapper} (handle),
     * {@code UUID} (player id), {@code String} (player name)</p>
     *
     * @param player  The object that represents a player.
     */
    @Nullable
    public static Player getPlayer(Object player) {

        if (player instanceof Player)
            return (Player)player;

        if (player instanceof IPlayerWrapper)
            return ((IPlayerWrapper)player).getHandle();

        if (player instanceof UUID)
            return getPlayer((UUID)player);

        if (player instanceof String)
            return getPlayer((String)player);

        return null;
    }

    /**
     * Gets player Id from name using stored id to name map if
     * the player is not online.
     *
     * <p>Will return an id if the player is not online but
     * has note logged in before.</p>
     *
     * @param playerName  The name of the player
     *
     * @return Null if not found
     */
    @Nullable
    public static UUID getPlayerId(String playerName) {
        return PlayerTracker.get().getPlayerId(playerName);
    }

    /**
     * Get the name of a player from the player Id.
     *
     * <p>Checks the GenericsLib map of player names to player Id's</p>
     *
     * @param playerId  The id of the player.
     *
     * @return  Null if a record was not found.
     */
    @Nullable
    public static String getPlayerName(UUID playerId) {
        return PlayerTracker.get().getPlayerName(playerId);
    }

    /**
     * Reset player state.
     *
     * <ul>
     *     <li>Inventory cleared, including armor.</li>
     *     <li>GameMode set to SURVIVAL</li>
     *     <li>Potion effects cleared</li>
     *     <li>Food level restored</li>
     *     <li>Exp set to 0</li>
     *     <li>Flying turned off</li>
     *     <li>Fire ticks set to 0</li>
     *     <li>Fall distance set to 0</li>
     * </ul>
     *
     * @param p  The player.
     */
    public static void resetPlayer(Player p) {
        PreCon.notNull(p);

        InventoryUtils.clearAll(p.getInventory());

        p.setGameMode(GameMode.SURVIVAL);
        p.getActivePotionEffects().clear();
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setExp(0);
        p.setFlying(false);
        p.setAllowFlight(false);
        p.setFireTicks(0);
        p.setFallDistance(0);
    }

    /**
     * Get a list of players that are near the specified location.
     *
     * @param loc          The location to check.
     * @param chunkRadius  The chunk radius to check in.
     */
    public static List<Player> getClosestPlayers(Location loc, int chunkRadius) {
        PreCon.notNull(loc);
        PreCon.greaterThanZero(chunkRadius);

        return getClosestPlayers(loc, chunkRadius, null);
    }

    /**
     * Get a list of players that are near the specified location.
     *
     * @param loc          The location to check.
     * @param chunkRadius  The chunk radius to check in.
     * @param validator    A validator used to validate if a player is a candidate to return.
     */
    public static List<Player> getClosestPlayers(Location loc, int chunkRadius,
                                                 @Nullable IEntryValidator<Player> validator) {
        PreCon.notNull(loc);
        PreCon.greaterThanZero(chunkRadius);

        Chunk chunk = loc.getChunk();
        List<Player> players = new ArrayList<Player>(20);

        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                Entity[] entities = chunk.getWorld().getChunkAt(chunk.getX() + x, chunk.getZ() + z).getEntities();

                for (Entity entity : entities) {
                    if (!(entity instanceof Player))
                        continue;

                    if (entity.hasMetadata("NPC"))
                        continue;

                    if (validator != null && validator.isValid((Player)entity))
                        continue;

                    players.add((Player)entity);

                }

            }
        }

        return players;
    }

    /**
     * Iterates over blocks in the direction the player is looking
     * until the max distance is reached or a block that isn't
     * {@code Material.AIR} is found.
     *
     * @param p            The player.
     * @param maxDistance  The max distance to search.
     */
    @Nullable
    public static Block getTargetBlock(Player p, int maxDistance) {
        PreCon.notNull(p);
        PreCon.positiveNumber(maxDistance);

        BlockIterator bit = new BlockIterator(p, maxDistance);
        Block next;
        while(bit.hasNext())
        {
            next = bit.next();

            if (next != null && next.getType() != Material.AIR)
                return next;
        }

        return null;
    }
}
