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


package com.jcwhatever.nucleus.utils.player;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.InternalPlayerTracker;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Provides {@link org.bukkit.entity.Player} related utilities.
 */
public final class PlayerUtils {

    private PlayerUtils() {}

    /**
     * Get the time the {@link Player} logged in for the current session.
     *
     * @param player  The {@link Player} to check.
     *
     * @return  The {@link Date} or null if the player is not logged in.
     */
    @Nullable
    public static Date getLoginDate(Player player) {
        return InternalPlayerTracker.get().getLoginDate(player);
    }

    /**
     * Get the time the {@link Player} last changed worlds in the current session.
     *
     * @param player  The {@link Player} to check.
     *
     * @return  The {@link Date} or null if the player is not logged in.
     */
    @Nullable
    public static Date getLastWorldChangeDate(Player player) {
        return InternalPlayerTracker.get().getLastWorldChangeDate(player);
    }

    /**
     * Get the number of milliseconds the {@link Player} has been on
     * the server during the current login session.
     *
     * @param player  The {@link Player} to check.
     *
     * @return  0 if the {@link Player} is not online.
     */
    public static long getSessionTime(Player player) {
        return InternalPlayerTracker.get().getSessionTime(player);
    }

    /**
     * Get the number of milliseconds the {@link Player} has been in
     * the world they are currently in.
     *
     * @param player  The {@link Player} to check.
     *
     * @return 0 if the {@link Player} is not online.
     */
    public static long getWorldSessionTime(Player player) {
        return InternalPlayerTracker.get().getWorldSessionTime(player);
    }

    /**
     * Get an online {@link Player} by name.
     *
     * @param playerName  The name of the {@link Player}.
     *
     * @return  The {@link Player} or null if not online.
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
     * Get an online {@link Player} by ID.
     *
     * @param playerId  The ID of the {@link Player}.
     *
     * @return  The {@link Player} or null if not online.
     */
    @Nullable
    public static Player getPlayer(UUID playerId) {
        PreCon.notNull(playerId);

        return Bukkit.getServer().getPlayer(playerId);
    }

    /**
     * Get {@link Player} instance from provided object.
     *
     * <p>Attempts to retrieve the {@link Player} object from one of the following
     * types of objects:</p>
     *
     * <ul>
     *     <li>{@link org.bukkit.entity.Player}</li>
     *     <li>{@link IPlayerReference}</li>
     *     <li>{@link java.util.UUID} (player ID)</li>
     *     <li>{@link java.lang.String} (player name)</li>
     * </ul>
     *
     * @param player  The object that represents a {@link Player}.
     *
     * @return  The {@link Player} or null if not found.
     */
    @Nullable
    public static Player getPlayer(Object player) {

        if (player instanceof Player)
            return (Player)player;

        if (player instanceof IPlayerReference)
            return ((IPlayerReference)player).getPlayer();

        if (player instanceof UUID)
            return getPlayer((UUID)player);

        if (player instanceof String)
            return getPlayer((String)player);

        return null;
    }

    /**
     * Gets player ID from provided object.
     *
     * <p>Attempts to retrieve the {@link Player} object from one of the following
     * types of objects:</p>
     *
     * <ul>
     *     <li>{@link org.bukkit.entity.Player}</li>
     *     <li>{@link IPlayerReference}</li>
     *     <li>{@link java.util.UUID} (player ID)</li>
     *     <li>{@link java.lang.String} (player name)</li>
     * </ul>
     *
     * @param player  The object to get the player ID from.
     *
     * @return The players ID or null if not found.
     */
    @Nullable
    public static UUID getPlayerId(Object player) {

        if (player instanceof UUID)
            return (UUID)player;

        if (player instanceof Player)
            return ((Player)player).getUniqueId();

        if (player instanceof IPlayerReference)
            return ((IPlayerReference)player).getPlayer().getUniqueId();

        if (player instanceof String) {

            UUID playerId = TextUtils.parseUUID((String)player);
            if (playerId != null)
                return playerId;

            return getPlayerId((String)player);
        }

        return null;
    }

    /**
     * Gets player ID from name using stored ID to name map if
     * the player is not online.
     *
     * <p>Uses the player lookup provider.</p>
     *
     * @param playerName  The name of the player.
     *
     * @return The player ID or null if not found.
     */
    @Nullable
    public static UUID getPlayerId(String playerName) {
        return Nucleus.getProviderManager().getPlayerLookupProvider().getPlayerId(playerName);
    }

    /**
     * Get the name of a player from the player ID.
     *
     * <p>Uses the player lookup provider.</p>
     *
     * @param playerId  The ID of the player.
     *
     * @return  The players name or null if a record was not found.
     */
    @Nullable
    public static String getPlayerName(UUID playerId) {
        return Nucleus.getProviderManager().getPlayerLookupProvider().getPlayerName(playerId);
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
     * @param player  The {@link Player}.
     */
    public static void resetPlayer(Player player) {
        PreCon.notNull(player);

        InventoryUtils.clearAll(player.getInventory());

        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().clear();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFireTicks(0);
        player.setFallDistance(0);
    }

    /**
     * Get a list of {@link Player}'s that are near the specified {@link Location}.
     *
     * @param loc          The {@link Location} to check.
     * @param chunkRadius  The chunk radius to check in.
     */
    public static List<Player> getNearbyPlayers(Location loc, int chunkRadius) {
        PreCon.notNull(loc);
        PreCon.positiveNumber(chunkRadius);

        return getNearbyPlayers(loc, chunkRadius, null);
    }

    /**
     * Get a list of {@link Player}'s that are near the specified {@link Location}.
     *
     * @param loc          The {@link Location} to check.
     * @param chunkRadius  The chunk radius to check in.
     * @param validator    A validator used to validate if a player is a candidate to return.
     */
    public static List<Player> getNearbyPlayers(Location loc, int chunkRadius,
                                                @Nullable IValidator<Player> validator) {
        PreCon.notNull(loc, "loc");
        PreCon.notNull(loc.getWorld(), "loc world");
        PreCon.positiveNumber(chunkRadius, "chunkRadius");

        World world = loc.getWorld();

        int chunkX = (int)Math.floor(loc.getX() / 16);
        int chunkZ = (int)Math.floor(loc.getZ() / 16);

        if (!world.isChunkLoaded(chunkX, chunkZ))
            return new ArrayList<>(0);

        List<Player> players = new ArrayList<Player>((chunkRadius + 1) * 7);

        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {

                if (!world.isChunkLoaded(chunkX + x, chunkZ + z))
                    continue;

                Entity[] entities = world.getChunkAt(chunkX + x, chunkZ + z).getEntities();

                for (Entity entity : entities) {
                    if (!(entity instanceof Player))
                        continue;

                    if (validator != null && validator.isValid((Player) entity))
                        continue;

                    players.add((Player) entity);
                }
            }
        }

        return players;
    }

    /**
     * Iterates over blocks in the direction the player is looking until the
     * max distance is reached or a block that isn't {@link org.bukkit.Material#AIR}
     * is found.
     *
     * @param player       The {@link Player}.
     * @param maxDistance  The max distance to search.
     *
     * @return  The {@link Block} that was found or null if the max distance was reached.
     */
    @Nullable
    public static Block getTargetBlock(Player player, int maxDistance) {
        PreCon.notNull(player);
        PreCon.positiveNumber(maxDistance);

        BlockIterator bit = new BlockIterator(player, maxDistance);
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
