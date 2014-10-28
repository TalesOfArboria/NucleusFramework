/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.player;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.inventory.InventoryHelper;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.mixins.IPlayerWrapper;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.StorageLoadHandler;
import com.jcwhatever.bukkit.generic.storage.StorageLoadResult;
import com.jcwhatever.bukkit.generic.utils.ItemValidator;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Provides {@code Player} related utilities.
 */
public class PlayerHelper {

    private static final Object _sync = new Object();

    private static IDataNode _nameData;

    /**
     * Determine if a player is online.
     *
     * @param p  The player to check.
     */
    public static boolean isOnline(Player p) {
        Player[] players = Bukkit.getServer().getOnlinePlayers();
        for (Player plyr : players) {
            if (plyr.getUniqueId().equals(p.getUniqueId()))
                return true;
        }
        return false;
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
     * Get player object from provided object.
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
     * Gets player Id from name using stored id to name map.
     * Null if not found.
     * Avoid usage due to low performance.
     *
     * @param playerName  The name of the player
     * @return
     */
    public static UUID getPlayerId(String playerName) {
        PreCon.notNull(playerName);

        IDataNode nameData = getNameData();

        Set<String> ids = nameData.getSubNodeNames();
        if (ids == null || ids.isEmpty())
            return null;

        for (String idStr : ids) {
            String name = nameData.getString(idStr);

            if (name != null && name.equalsIgnoreCase(playerName)) {

                try {
                    return UUID.fromString(idStr);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Get the name of a player from the player Id.
     *
     * <p>Checks the GenericsLib map of player names to player Id's</p>
     *
     * @param playerId  The id of the player.
     *
     * @return  Returns the string "[unknown]" if a record was not found.
     */
    public static String getPlayerName(UUID playerId) {

        IDataNode nameData = getNameData();

        String name = nameData.getString(playerId.toString());
        if (name == null)
            return "[unknown]";

        return name;
    }

    /**
     * Update the GenericsLib player name/player id map.
     *
     * @param playerId  The id of the player.
     * @param name      The new player name.
     */
    public static void setPlayerName(UUID playerId, String name) {
        IDataNode nameData = getNameData();

        String currentName = getPlayerName(playerId);

        if (currentName.equals(name))
            return;

        synchronized (_sync) {
            nameData.set(playerId.toString(), name);
        }

        nameData.saveAsync(null);
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
        InventoryHelper.clearAll(p.getInventory());

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
    public static List<Player> getClosestPlayers(Location loc, int chunkRadius, @Nullable ItemValidator<Player> validator) {
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
     * Get the location closest to the specified player.
     *
     * @param p          The player.
     * @param locations  The location candidates.
     */
    public static Location getClosestLocation(Player p, Collection<Location> locations) {
        return getClosestLocation(p, locations, null);
    }

    /**
     * Get the location closest to the specified player.
     *
     * @param p          The player.
     * @param locations  The location candidates.
     * @param validator  The validator used to determine if a location is a candidate.
     */
    public static Location getClosestLocation(Player p, Collection<Location> locations,
                                              @Nullable ItemValidator<Location> validator) {
        PreCon.notNull(p);
        PreCon.notNull(locations);

        Location closest = null;
        double closestDist = 0.0D;

        for (Location loc : locations) {
            if (validator != null && !validator.isValid(loc))
                continue;

            double dist = 0.0D;
            if (closest == null || (dist = p.getLocation().distanceSquared(loc)) < closestDist) {
                closest = loc;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * Get the closest entity to a player.
     *
     * @param p      The player.
     * @param range  The search range.
     */
    @Nullable
    public static Entity getClosestEntity(Player p, double range) {
        return getClosestEntity(p, range, range, range, null);
    }

    /**
     * Get the closest entity to a player.
     *
     * @param p          The player.
     * @param range      The search range.
     * @param validator  The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(Player p, double range, @Nullable ItemValidator<Entity> validator) {
        return getClosestEntity(p, range, range, range, validator);
    }

    /**
     * Get the closest entity to a player.
     *
     * @param p       The player.
     * @param rangeX  The search range on the X axis.
     * @param rangeY  The search range on the Y axis.
     * @param rangeZ  The search range on the Z axis.
     */
    @Nullable
    public static Entity getClosestEntity(Player p, double rangeX, double rangeY, double rangeZ) {
        return getClosestEntity(p, rangeX, rangeY, rangeZ, null);
    }

    /**
     *
     * @param p          The player.
     * @param rangeX     The search range on the X axis.
     * @param rangeY     The search range on the Y axis.
     * @param rangeZ     The search range on the Z axis.
     * @param validator  The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(Player p, double rangeX, double rangeY, double rangeZ,
                                          @Nullable ItemValidator<Entity> validator) {
        PreCon.notNull(p);
        PreCon.positiveNumber(rangeX);
        PreCon.positiveNumber(rangeY);
        PreCon.positiveNumber(rangeZ);

        List<Entity> entities = p.getNearbyEntities(rangeX, rangeY, rangeZ);

        Entity closest = null;
        double closestDist = 0.0D;

        for (Entity entity : entities) {
            if (validator != null && !validator.isValid(entity))
                continue;

            double dist = 0.0D;
            if (closest == null || (dist = p.getLocation().distanceSquared( entity.getLocation() )) < closestDist) {
                closest = entity;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param p      The player.
     * @param range  The search range.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Player p, double range) {
        return getClosestLivingEntity(p, range, range, range, null);
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param p          The player.
     * @param range      The search range.
     * @param validator  The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Player p, double range,
                                                      @Nullable ItemValidator<LivingEntity> validator) {
        return getClosestLivingEntity(p, range, range, range, validator);
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param p       The player.
     * @param rangeX  The search range on the X axis.
     * @param rangeY  The search range on the Y axis.
     * @param rangeZ  The search range on the Z axis.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Player p, double rangeX, double rangeY, double rangeZ) {
        return getClosestLivingEntity(p, rangeX, rangeY, rangeZ, null);
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param p          The player.
     * @param rangeX     The search range on the X axis.
     * @param rangeY     The search range on the Y axis.
     * @param rangeZ     The search range on the Z axis.
     * @param validator  The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Player p, double rangeX, double rangeY, double rangeZ,
                                                      @Nullable ItemValidator<LivingEntity> validator) {
        PreCon.notNull(p);

        List<Entity> entities = p.getNearbyEntities(rangeX, rangeY, rangeZ);

        LivingEntity closest = null;
        double closestDist = 0.0D;

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity))
                continue;

            LivingEntity livingEntity = (LivingEntity)entity;

            if (validator != null && !validator.isValid(livingEntity))
                continue;

            double dist = 0.0D;
            if (closest == null || (dist = p.getLocation().distanceSquared( entity.getLocation() )) < closestDist) {
                closest = livingEntity;
                closestDist = dist;
            }
        }

        return closest;
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

    // get the node that contains player id/name data.
    private static IDataNode getNameData() {

        if (_nameData == null) {

            synchronized (_sync) {

                IDataNode data = DataStorage.getStorage(GenericsLib.getPlugin(), new DataPath("player-names"));
                data.loadAsync(new StorageLoadHandler() {

                    @Override
                    public void onFinish(StorageLoadResult result) {
                        if (!result.isLoaded())
                            Messenger.warning(GenericsLib.getPlugin(), "Failed to load player names file.");

                    }

                });

                _nameData = data;
            }
        }
        return _nameData;
    }
}
