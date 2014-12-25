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


package com.jcwhatever.generic.utils;

import com.jcwhatever.generic.extended.MaterialExt;
import com.jcwhatever.generic.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Location utilities.
 */
public final class LocationUtils {

    private LocationUtils () {}

    // array to help convert yaw to block face
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
     * Add values to the locations coordinates without changing the coordinates
     * in the provided location.
     *
     * @param location  The location.
     * @param x         The value to add to the X coordinates.
     * @param y         The value to add to the Y coordinates.
     * @param z         The value to add to the Z coordinates.
     *
     * @return  A new {@code Location} instance.
     */
    public static Location add(Location location, double x, double y, double z) {
        location = location.clone();
        return location.add(x, y, z);
    }

    /**
     * Add noise to a location. Changes to another point within the specified
     * radius of the original location randomly.
     *
     * @param location  The location.
     * @param radiusX   The max radius on the X axis.
     * @param radiusY   The max radius on the Y axis.
     * @param radiusZ   The max radius on the Z axis.
     */
    public static Location addNoise(Location location, double radiusX, double radiusY, double radiusZ) {
        PreCon.notNull(location);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        location = location.clone();

        double noiseX = 0;
        double noiseY = 0;
        double noiseZ = 0;

        if (radiusX > 0) {
            noiseX = Rand.getDouble(radiusX * 2) - radiusX;
        }

        if (radiusY > 0) {
            noiseY = Rand.getDouble(radiusY * 2) - radiusY;
        }

        if (radiusZ > 0) {
            noiseZ = Rand.getDouble(radiusZ * 2) - radiusZ;
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
     * @param world        The world the location is for.
     * @param coordinates  The string coordinates.
     *
     * @return  Null if a location could not be parsed.
     */
    @Nullable
    public static Location parseSimpleLocation(World world, String coordinates) {
        PreCon.notNull(world);
        PreCon.notNull(coordinates);

        String[] parts = TextUtils.PATTERN_COMMA.split(coordinates);
        if (parts.length != 3)
            return null;

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
     * @return  Null if the string could not be parsed.
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
     * Parse the world name from a from a location formatted string.
     * <p>
     *     Format of string: x,y,z,yawF,pitchF,worldName
     * </p>
     * <p>Useful when the world the location is for is not loaded and
     * the name is needed.</p>
     *
     * @param coordinates  The string coordinates.
     *
     * @return  Null if the string could not be parsed.
     */
    @Nullable
    public static String parseLocationWorldName(String coordinates) {
        PreCon.notNull(coordinates);

        String[] parts =  TextUtils.PATTERN_COMMA.split(coordinates);
        if (parts.length != 6)
            return null;

        return parts[5];
    }

    /**
     * Convert a location to a parsable string.
     *
     * @param location  The location to convert.
     */
    public static String locationToString(Location location) {
        PreCon.notNull(location);

        return String.valueOf(location.getX()) + ',' + location.getY() + ',' + location.getZ() +
                ',' + location.getYaw() + ',' + location.getPitch() + ',' + location.getWorld().getName();
    }

    /**
     * Convert a location to a parsable string.
     *
     * @param location             The location to convert.
     * @param floatingPointPlaces  The number of places in the floating point values.
     */
    public static String locationToString(Location location, int floatingPointPlaces) {
        PreCon.notNull(location);
        PreCon.positiveNumber(floatingPointPlaces);

        BigDecimal x = new BigDecimal(floatingPointPlaces == 0 ? location.getBlockX() : location.getX())
                .setScale(floatingPointPlaces, RoundingMode.HALF_UP);

        BigDecimal y = new BigDecimal(floatingPointPlaces == 0 ? location.getBlockY() : location.getY())
                .setScale(floatingPointPlaces, RoundingMode.HALF_UP);

        BigDecimal z = new BigDecimal(floatingPointPlaces == 0 ? location.getBlockZ() : location.getZ())
                .setScale(floatingPointPlaces, RoundingMode.HALF_UP);

        BigDecimal yaw = new BigDecimal(location.getYaw())
                .setScale(floatingPointPlaces, RoundingMode.HALF_UP);

        BigDecimal pitch = new BigDecimal(location.getPitch())
                .setScale(floatingPointPlaces, RoundingMode.HALF_UP);

        return String.valueOf(x) + ',' + y + ',' + z +
                ',' + yaw + ',' + pitch + ',' + location.getWorld().getName();
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

    /**
     * Get the location closest to the specified player.
     *
     * @param sourceLocation  The location source to search from.
     * @param locations       The location candidates.
     */
    @Nullable
    public static Location getClosestLocation(Location sourceLocation, Collection<Location> locations) {
        return getClosestLocation(sourceLocation, locations, null);
    }

    /**
     * Get the location closest to the specified player.
     *
     * @param sourceLocation  The location source to search from.
     * @param locations       The location candidates.
     * @param validator       The validator used to determine if a location is a candidate.
     */
    @Nullable
    public static Location getClosestLocation(Location sourceLocation, Collection<Location> locations,
                                              @Nullable IEntryValidator<Location> validator) {
        PreCon.notNull(sourceLocation);
        PreCon.notNull(locations);

        Location closest = null;
        double closestDist = 0.0D;

        for (Location loc : locations) {
            if (validator != null && !validator.isValid(loc))
                continue;

            double dist = 0.0D;
            if (closest == null || (dist = sourceLocation.distanceSquared(loc)) < closestDist) {
                closest = loc;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * Rotate a location around an axis location.
     *
     * @param axis       The axis location.
     * @param location   The location to move.
     * @param rotationX  The rotation around the X axis in degrees.
     * @param rotationY  The rotation around the Y axis in degrees.
     * @param rotationZ  The rotation around the Z axis in degrees.
     */
    public static Location rotate(Location axis, Location location,
                                  double rotationX, double rotationY, double rotationZ) {
        PreCon.notNull(axis);
        PreCon.notNull(location);

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double centerX = axis.getX();
        double centerY = axis.getY();
        double centerZ = axis.getZ();

        double translateX = x;
        double translateY = y;
        double translateZ = z;
        double yaw = location.getYaw();

        // rotate on X axis
        if (Double.compare(rotationX, 0.0D) != 0) {
            double rotX = Math.toRadians(rotationX);
            translateY = rotateX(centerY, centerZ, y, z, rotX);
            translateZ = rotateZ(centerY, centerZ, y, z, rotX);
        }

        // rotate on Y axis
        if (Double.compare(rotationY, 0.0D) != 0) {
            double rotY = Math.toRadians(rotationY);
            translateX = rotateX(centerX, centerZ, x, z, rotY);
            translateZ = rotateZ(centerX, centerZ, x, z, rotY);
            yaw += rotationY;
        }

        // rotate on Z axis
        if (Double.compare(rotationZ, 0.0D) != 0) {
            double rotZ = Math.toRadians(rotationZ);
            translateX = rotateX(centerX, centerY, x, y, rotZ);
            translateY = rotateZ(centerX, centerY, x, y, rotZ);
        }

        return new Location(location.getWorld(),
                translateX, translateY, translateZ,
                (float)yaw, location.getPitch());
    }

    // helper to convert a string number to a double.
    @Nullable
    private static Double parseDouble(String s) {
        s = s.trim();

        try {
            return s.indexOf('.') == -1
                    ? Integer.parseInt(s)
                    : Double.parseDouble(s);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    // helper to convert a string number to a float.
    @Nullable
    private static Float parseFloat(String s) {
        try {
            return s.indexOf('.') == -1
                    ? Integer.parseInt(s)
                    : Float.parseFloat(s.trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static double rotateX(double centerA, double centerB, double a, double b, double rotation) {
        return centerA + Math.cos(rotation) * (a - centerA) - Math.sin(rotation) * (b - centerB);
    }

    private static double rotateZ(double centerA, double centerB, double a, double b, double rotation) {
        return centerB + Math.sin(rotation) * (a - centerA) + Math.cos(rotation) * (b - centerB);
    }
}
