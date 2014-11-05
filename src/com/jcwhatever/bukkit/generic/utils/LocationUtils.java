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

import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;

/**
 * Location utilities.
 */
public class LocationUtils {

    private LocationUtils () {}

    private static final BlockFace[] YAW_FACES = new BlockFace[] {
            BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST,
            BlockFace.WEST,  BlockFace.WEST_NORTH_WEST,  BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST,
            BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST,
            BlockFace.EAST,  BlockFace.EAST_SOUTH_EAST,  BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST,
            BlockFace.SOUTH
    };

    /**
     * Get a location centered on the X and Z axis of the block
     * represented by the provided location.
     *
     * @param location  The location.
     */
    public static Location getCenteredLocation(Location location) {
        PreCon.notNull(location);

        return new Location(location.getWorld(),
                location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5,
                location.getYaw(), location.getPitch());
    }

    /**
     * Teleport an entity to the location centered on the
     * X and Z axis.
     *
     * @param entity    The entity to teleport.
     * @param location  The teleport location.
     */
    public static boolean teleportCentered(Entity entity, Location location) {
        PreCon.notNull(entity);
        PreCon.notNull(location);

        Location adjusted = getCenteredLocation(location);
        return entity.teleport(adjusted, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Convert a location into a block location (remove numbers to the right of the floating point value)
     * and remove the yaw and pitch values.
     *
     * @param location  The location to convert.
     */
    public static Location getBlockLocation(Location location) {
        PreCon.notNull(location);

        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Add noise to a location. Changes tho another point within the specified radius of the original
     * location randomly.
     *
     * @param location  The location.
     * @param radiusX   The max radius on the X axis.
     * @param radiusY   The max radius on the Y axis.
     * @param radiusZ   The max radius on the Z axis.
     */
    public static Location addNoise(Location location, int radiusX, int radiusY, int radiusZ) {
        PreCon.notNull(location);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        location = location.clone();

        int noiseX = 0, noiseY = 0, noiseZ = 0;

        if (radiusX > 0) {
            noiseX = Rand.getInt(radiusX * 2) - radiusX;
        }

        if (radiusY > 0) {
            noiseY = Rand.getInt(radiusY * 2) - radiusY;
        }

        if (radiusZ > 0) {
            noiseZ = Rand.getInt(radiusZ * 2) - radiusZ;
        }

        return location.add(noiseX, noiseY, noiseZ);
    }


    /**
     * Determine if 2 locations can be considered the same using the specified
     * precision. The precision is used as: location1 is about the same as location2 +/- precision.
     *
     * @param location1  The first location to compare.
     * @param location2  The second location to compare.
     * @param precision  The precision.
     */
    public static boolean isLocationMatch(Location location1, Location location2, double precision) {
        PreCon.notNull(location1);
        PreCon.notNull(location2);

        double xDelta = Math.abs(location1.getX() - location2.getX());
        double zDelta = Math.abs(location1.getZ() - location2.getZ());
        double yDelta = Math.abs(location1.getY() - location2.getY());

        return xDelta < precision && zDelta < precision && yDelta < precision;
    }

    /**
     * Parse a location from a formatted string.
     * <p>
     *     Format of string : x,y,z
     * </p>
     *
     * @param world   The world the location is for.
     * @param coordinates  The string coordinates.
     *
     * @return  null if a location could not be parsed.
     */
    @Nullable
    public static Location parseSimpleLocation(World world, String coordinates) {
        PreCon.notNull(world);
        PreCon.notNull(coordinates);

        String[] parts = TextUtils.PATTERN_COMMA.split(coordinates);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Input string must contain only x, y, and z");
        }

        Double x = parseDouble(parts[0]);
        Double y = parseDouble(parts[1]);
        Double z = parseDouble(parts[2]);

        if (x != null && y != null && z != null)
            return new Location(world, x, y, z);

        return null;
    }

    /**
     * Parse a location from a formatted string.
     * <p>
     *     Format of string: x,y,z,yawF,pitchF,worldName
     * </p>
     *
     * @param coordinates  The string coordinates.
     *
     * @return  null if the string could not be parsed.
     */
    @Nullable
    public static Location parseLocation(String coordinates) {
        PreCon.notNull(coordinates);

        String[] parts =  TextUtils.PATTERN_COMMA.split(coordinates);
        if (parts.length != 6)
            return null;

        Double x = parseDouble(parts[0]);
        if (x == null)
            return null;

        Double y = parseDouble(parts[1]);
        if (y == null)
            return null;

        Double z = parseDouble(parts[2]);
        if (z == null)
            return null;

        Float yaw = parseFloat(parts[3]);
        if (yaw == null)
            return null;

        Float pitch = parseFloat(parts[4]);
        if (pitch == null)
            return null;

        World world = Bukkit.getWorld(parts[5]);
        if (world == null)
            return null;

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Convert a location to a parsable string.
     *
     * @param location  The location to convert.
     */
    public static String locationToString(Location location) {
        PreCon.notNull(location);

        return String.valueOf(location.getBlockX()) + ',' +
                location.getBlockY() + ',' + location.getBlockZ() + ',' + location.getYaw() + ',' +
                location.getPitch() + ',' + location.getWorld().getName();
    }

    /**
     * Convert a locations yaw angle to a {@code BlockFace}.
     *
     * @param location  The location to convert.
     */
    public static BlockFace getBlockFacingYaw(Location location) {
        PreCon.notNull(location);

        float yaw = (location.getYaw() + (location.getYaw() < 0 ? 360 : 0)) % 360;

        int i = (int)(yaw / 22.5);

        if (i > YAW_FACES.length - 1 || i < 0) {
            i = YAW_FACES.length - 1;
        }

        return YAW_FACES[i];
    }

    /**
     * Find a surface block (solid block that can be walked on) location below the provided
     * search location.
     * <p>
     *     If the search location is a surface block, the search location
     *     is returned.
     * </p>
     *
     * @param searchLoc  The search location.
     *
     * @return  null if the search reaches below 0 on the Y axis.
     */
    @Nullable
    public static Location findSurfaceBelow(Location searchLoc) {
        PreCon.notNull(searchLoc);

        searchLoc = getBlockLocation(searchLoc);

        if (!MaterialExt.isTransparent(searchLoc.getBlock().getType()))
            return searchLoc;

        searchLoc.add(0, -1, 0);
        Block current = searchLoc.getBlock();

        while (!MaterialExt.isSurface(current.getType())) {
            searchLoc.add(0, -1, 0);
            current = searchLoc.getBlock();

            if (searchLoc.getY() < 0) {
                return null;
            }
        }
        return searchLoc;
    }

    // helper to convert a string number to a double.
    @Nullable
    private static Double parseDouble(String s) {
        s = s.trim();

        try {
            return Double.parseDouble(s);
        }
        catch (Exception ignore) {}

        try {
            return (double) Integer.parseInt(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    // helper to convert a string number to a float.
    @Nullable
    private static Float parseFloat(String s) {
        try {
            return Float.parseFloat(s.trim());
        }
        catch (Exception e) {
            return null;
        }
    }



}
