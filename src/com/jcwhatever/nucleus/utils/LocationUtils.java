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

import com.jcwhatever.nucleus.regions.data.SyncLocation;
import com.jcwhatever.nucleus.utils.materials.Materials;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

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

    private static final Location CENTERED_LOCATION = new Location(null, 0, 0, 0);

    /**
     * Copy the values from a source {@link org.bukkit.Location} to a new
     * {@link org.bukkit.Location}.
     *
     * @param source  The source location.
     *
     * @return  The new location.
     */
    public static Location copy(Location source) {
        PreCon.notNull(source);

        Location destination = new Location(null, 0, 0, 0);

        return copy(source, destination);
    }

    /**
     * Copy the values from a source {@link org.bukkit.Location} to a destination
     * {@link org.bukkit.Location}.
     *
     * @param source       The source location.
     * @param destination  The destination location.
     *
     * @return  The destination location.
     */
    public static Location copy(Location source, Location destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        destination.setWorld(source.getWorld());
        destination.setX(source.getX());
        destination.setY(source.getY());
        destination.setZ(source.getZ());
        destination.setYaw(source.getYaw());
        destination.setPitch(source.getPitch());

        return destination;
    }

    /**
     * Copy the values from a source {@link org.bukkit.Location} to a destination
     * {@link org.bukkit.util.Vector}.
     *
     * @param source       The source location.
     * @param destination  The destination vector.
     *
     * @return  The destination vector.
     */
    public static Vector copy(Location source, Vector destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        destination.setX(source.getX());
        destination.setY(source.getY());
        destination.setZ(source.getZ());

        return destination;
    }

    /**
     * Copy the values from a source {@link org.bukkit.util.Vector} to a new
     * {@link org.bukkit.util.Vector}.
     *
     * @param source       The source location.
     *
     * @return  The new vector.
     */
    public static Vector copy(Vector source) {
        PreCon.notNull(source);

        Vector vector = new Vector(0, 0, 0);

        return copy(source, vector);
    }

    /**
     * Copy the values from a source {@link org.bukkit.util.Vector} to a destination
     * {@link org.bukkit.util.Vector}.
     *
     * @param source       The source location.
     * @param destination  The destination vector.
     *
     * @return  The destination vector.
     */
    public static Vector copy(Vector source, Vector destination) {
        PreCon.notNull(source);
        PreCon.notNull(destination);

        destination.setX(source.getX());
        destination.setY(source.getY());
        destination.setZ(source.getZ());

        return destination;
    }

    /**
     * Get a location centered on the X and Z axis of the block
     * represented by the provided location.
     *
     * @param location  The location.
     *
     * @return  A new {@link org.bukkit.Location} containing the result.
     */
    public static Location getCenteredLocation(Location location) {
        PreCon.notNull(location);

        return getCenteredLocation(location, new Location(null, 0, 0, 0));
    }

    /**
     * Get a location centered on the X and Z axis of the block
     * represented by the provided location.
     *
     * @param location  The location.
     * @param output    The location to put the results into.
     *
     * @return  The output location.
     */
    public static Location getCenteredLocation(Location location, Location output) {
        PreCon.notNull(location);

        output.setWorld(location.getWorld());
        output.setX(location.getBlockX() + 0.5);
        output.setY(location.getY());
        output.setZ(location.getBlockZ() + 0.5);
        output.setYaw(location.getYaw());
        output.setPitch(location.getPitch());

        return output;
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

        Location adjusted = getCenteredLocation(location,
                Bukkit.isPrimaryThread() ? CENTERED_LOCATION : new Location(null, 0, 0, 0));

        return entity.teleport(adjusted, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Convert a location into a block location (remove numbers to the right of the floating point value)
     * and remove the yaw and pitch values.
     *
     * @param location  The location to convert.
     *
     * @return  A new {@link org.bukkit.Location} containing the result.
     */
    public static Location getBlockLocation(Location location) {
        PreCon.notNull(location);

        return getBlockLocation(location, new Location(null, 0, 0, 0));
    }

    /**
     * Convert a location into a block location (remove numbers to the right of the floating point value)
     * and remove the yaw and pitch values.
     *
     * @param location  The location to convert.
     * @param output    The location to put the results into.
     *
     * @return  The output location.
     */
    public static Location getBlockLocation(Location location, Location output) {
        PreCon.notNull(location);

        output.setWorld(location.getWorld());
        output.setX(location.getBlockX());
        output.setY(location.getBlockY());
        output.setZ(location.getBlockZ());

        return output;
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
     * @return  A new {@link org.bukkit.Location} instance.
     */
    public static Location add(Location location, double x, double y, double z) {
        return location.clone().add(x, y, z);
    }

    /**
     * Add values to the locations coordinates without changing the coordinates
     * in the provided location.
     *
     * @param location  The location.
     * @param output    The location to put the results into.
     * @param x         The value to add to the X coordinates.
     * @param y         The value to add to the Y coordinates.
     * @param z         The value to add to the Z coordinates.
     *
     * @return  The output location.
     */
    public static Location add(Location location, Location output, double x, double y, double z) {
        return copy(location, output).add(x, y, z);
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

        return addNoise(location, location.clone(), radiusX, radiusY, radiusZ);
    }

    /**
     * Add noise to a location. Changes to another point within the specified
     * radius of the original location randomly.
     *
     * @param location  The location.
     * @param output    The location to put the results into.
     * @param radiusX   The max radius on the X axis.
     * @param radiusY   The max radius on the Y axis.
     * @param radiusZ   The max radius on the Z axis.
     *
     * @return  The output location.
     */
    public static Location addNoise(Location location, Location output,
                                    double radiusX, double radiusY, double radiusZ) {
        PreCon.notNull(location);
        PreCon.notNull(output);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

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

        return output.add(noiseX, noiseY, noiseZ);
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

        return xDelta <= precision && zDelta <= precision && yDelta <= precision;
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
     * @return  A new {@link org.bukkit.Location} or null if a location could not be parsed.
     */
    @Nullable
    public static Location parseSimpleLocation(World world, String coordinates) {
        return parseSimpleLocation(new Location(null, 0, 0, 0), world, coordinates);
    }

    /**
     * Parse a location from a formatted string.
     * <p>
     *     Format of string : x,y,z
     * </p>
     *
     * @param output       The location place the results in.
     * @param world        The world the location is for.
     * @param coordinates  The string coordinates.
     *
     * @return  The output location or null if a location could not be parsed.
     */
    @Nullable
    public static Location parseSimpleLocation(Location output, World world, String coordinates) {
        PreCon.notNull(output);
        PreCon.notNull(world);
        PreCon.notNull(coordinates);

        String[] parts = TextUtils.PATTERN_COMMA.split(coordinates);
        if (parts.length != 3)
            return null;

        double x = TextUtils.parseDouble(parts[0], Double.MAX_VALUE);
        double y = TextUtils.parseDouble(parts[1], Double.MAX_VALUE);
        double z = TextUtils.parseDouble(parts[2], Double.MAX_VALUE);

        if (x != Double.MAX_VALUE && y != Double.MAX_VALUE && z != Double.MAX_VALUE) {
            output.setWorld(world);
            output.setX(x);
            output.setY(y);
            output.setZ(z);
            return output;
        }

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
     * @return  A new {@link SyncLocation} or null if the string could not be parsed.
     */
    @Nullable
    public static SyncLocation parseLocation(String coordinates) {
        PreCon.notNull(coordinates);

        SyncLocation location = new SyncLocation((World)null, 0, 0, 0);

        return parseLocation(coordinates, location);
    }

    /**
     * Parse a location from a formatted string.
     * <p>
     *     Format of string: x,y,z,yawF,pitchF,worldName
     * </p>
     *
     * @param coordinates  The string coordinates.
     *
     * @return  A new {@link SyncLocation} or null if the string could not be parsed.
     */
    @Nullable
    public static SyncLocation parseLocation(String coordinates, SyncLocation output) {
        PreCon.notNull(coordinates);

        String[] parts =  TextUtils.PATTERN_COMMA.split(coordinates);
        if (parts.length != 6)
            return null;

        double x = TextUtils.parseDouble(parts[0], Double.MAX_VALUE);
        if (x == Double.MAX_VALUE)
            return null;

        double y = TextUtils.parseDouble(parts[1], Double.MAX_VALUE);
        if (y == Double.MAX_VALUE)
            return null;

        double z = TextUtils.parseDouble(parts[2], Double.MAX_VALUE);
        if (z == Double.MAX_VALUE)
            return null;

        float yaw = TextUtils.parseFloat(parts[3], Float.MAX_VALUE);
        if (yaw == Float.MAX_VALUE)
            return null;

        float pitch = TextUtils.parseFloat(parts[4], Float.MAX_VALUE);
        if (pitch == Float.MAX_VALUE)
            return null;

        output.setWorld(parts[5]);
        output.setX(x);
        output.setY(y);
        output.setZ(z);
        output.setYaw(yaw);
        output.setPitch(pitch);

        return output;
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
     * Convert a locations yaw angle to a {@link org.bukkit.block.BlockFace}.
     *
     * @param location  The location to convert.
     */
    public static BlockFace getBlockFacingYaw(Location location) {
        PreCon.notNull(location);

        return getBlockFacingYaw(location.getYaw());
    }

    /**
     * Convert a yaw angle to a {@link org.bukkit.block.BlockFace}.
     *
     * @param yaw  The yaw angle to convert.
     */
    public static BlockFace getBlockFacingYaw(float yaw) {

        yaw = yaw + 11.25f;
        yaw = yaw < 0
                ? 360 - (Math.abs(yaw) % 360)
                : yaw % 360;

        int i = (int)(yaw / 22.5);

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
     * @return  A new {@link org.bukkit.Location} or null if the search reaches below 0 on the Y axis.
     */
    @Nullable
    public static Location findSurfaceBelow(Location searchLoc) {
        return findSurfaceBelow(searchLoc, new Location(null, 0, 0, 0));
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
     * @return  The output location or null if the search reaches below 0 on the Y axis.
     */
    @Nullable
    public static Location findSurfaceBelow(Location searchLoc, Location output) {
        PreCon.notNull(searchLoc);

        getBlockLocation(searchLoc, output);

        if (!Materials.isTransparent(output.getBlock().getType()))
            return searchLoc;

        output.add(0, -1, 0);
        Block current = searchLoc.getBlock();

        while (!Materials.isSurface(current.getType())) {
            output.add(0, -1, 0);
            current = output.getBlock();

            if (output.getY() < 0) {
                return null;
            }
        }
        return output;
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
                                              @Nullable IValidator<Location> validator) {
        PreCon.notNull(sourceLocation);
        PreCon.notNull(locations);

        Location closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Location loc : locations) {
            if (validator != null && !validator.isValid(loc))
                continue;

            double dist;
            if ((dist = sourceLocation.distanceSquared(loc)) < closestDist) {
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

        return rotate(axis, location, new Location(null, 0, 0, 0),
                rotationX, rotationY, rotationZ);
    }

    /**
     * Rotate a location around an axis location.
     *
     * @param axis       The axis location.
     * @param location   The location to move.
     * @param output     The location to put results into.
     * @param rotationX  The rotation around the X axis in degrees.
     * @param rotationY  The rotation around the Y axis in degrees.
     * @param rotationZ  The rotation around the Z axis in degrees.
     *
     * @return  The output location.
     */
    public static Location rotate(Location axis, Location location, Location output,
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

        output.setWorld(location.getWorld());
        output.setX(translateX);
        output.setY(translateY);
        output.setZ(translateZ);
        output.setYaw((float) yaw);
        output.setPitch(location.getPitch());

        return output;
    }

    private static double rotateX(double centerA, double centerB, double a, double b, double rotation) {
        return centerA + Math.cos(rotation) * (a - centerA) - Math.sin(rotation) * (b - centerB);
    }

    private static double rotateZ(double centerA, double centerB, double a, double b, double rotation) {
        return centerB + Math.sin(rotation) * (a - centerA) + Math.cos(rotation) * (b - centerB);
    }
}
