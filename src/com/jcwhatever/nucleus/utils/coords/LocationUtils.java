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


package com.jcwhatever.nucleus.utils.coords;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
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
    private static final Location TELEPORT_LOCATION = new Location(null, 0, 0, 0);

    /**
     * Copy the values from a source {@link org.bukkit.Location} to a new
     * {@link org.bukkit.Location}.
     *
     * @param source  The source location.
     *
     * @return  A new {@link org.bukkit.Location}.
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
     * @return  The destination {@link org.bukkit.Location}.
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
     * @return  The destination {@link org.bukkit.util.Vector}.
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
     * @param source  The source location.
     *
     * @return  A new {@link org.bukkit.util.Vector}.
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
     * @return  The destination {@link org.bukkit.util.Vector}.
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
     * Copy a source {@link org.bukkit.Location} and center the X and Z coordinates of the
     * copy to the source locations block.
     *
     * @param source  The source location.
     *
     * @return  A new {@link org.bukkit.Location} containing the result.
     */
    public static Location getCenteredLocation(Location source) {
        PreCon.notNull(source);

        return getCenteredLocation(source, new Location(null, 0, 0, 0));
    }

    /**
     * Copy a source {@link org.bukkit.Location} to an output {@link org.bukkit.Location} and
     * center the X and Z coordinates of the output to the source locations block.
     *
     * @param source  The source location.
     * @param output  The location to put the results into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location getCenteredLocation(Location source, Location output) {
        PreCon.notNull(source);
        PreCon.notNull(output);

        output.setWorld(source.getWorld());
        output.setX(source.getBlockX() + 0.5);
        output.setY(source.getY());
        output.setZ(source.getBlockZ() + 0.5);
        output.setYaw(source.getYaw());
        output.setPitch(source.getPitch());

        return output;
    }

    /**
     * Teleport an entity to a {@link org.bukkit.Location} centered on the X and Z
     * axis of the locations block.
     *
     * @param entity    The entity to teleport.
     * @param location  The teleport location.
     *
     * @return  True if successful.
     */
    public static boolean teleportCentered(Entity entity, Location location) {
        PreCon.notNull(entity);
        PreCon.notNull(location);

        Location adjusted = getCenteredLocation(location,
                Bukkit.isPrimaryThread() ? CENTERED_LOCATION : new Location(null, 0, 0, 0));

        return entity.teleport(adjusted, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleport an entity to a {@link org.bukkit.Location}.
     *
     * <p>Fixes entities/players falling through floor.</p>
     *
     * @param entity    The entity to teleport.
     * @param location  The location to teleport the entity to.
     *
     * @return  True if successful.
     */
    public static boolean teleport(Entity entity, Location location) {
        PreCon.notNull(entity);
        PreCon.notNull(location);

        Location adjusted = Bukkit.isPrimaryThread()
                ? copy(location, TELEPORT_LOCATION).add(0, 0.01D, 0)
                : copy(location).add(0, 0.01D, 0);

        return entity.teleport(adjusted);
    }

    /**
     * Copy a source {@link org.bukkit.Location} and change coordinate values to block
     * coordinates in the copy.
     *
     * <p>Removes yaw and pitch values, converts coordinates to whole numbers.</p>
     *
     * @param source  The source location.
     *
     * @return  A new {@link org.bukkit.Location} containing the result.
     */
    public static Location getBlockLocation(Location source) {
        PreCon.notNull(source);

        return getBlockLocation(source, new Location(null, 0, 0, 0));
    }

    /**
     * Copy a source {@link org.bukkit.Location} to an output location and change coordinate
     * values to block coordinates in the output.
     *
     * <p>Removes yaw and pitch values, converts coordinates to whole numbers.</p>
     *
     * @param source  The source location.
     * @param output  The location to put the results into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location getBlockLocation(Location source, Location output) {
        PreCon.notNull(source);
        PreCon.notNull(output);

        output.setWorld(source.getWorld());
        output.setX(source.getBlockX());
        output.setY(source.getBlockY());
        output.setZ(source.getBlockZ());
        output.setYaw(0);
        output.setPitch(0);

        return output;
    }

    /**
     * Copy a source {@link org.bukkit.Location} and add values to the copy without changing
     * the original {@link org.bukkit.Location}.
     *
     * @param source  The source location.
     * @param x       The value to add to the X coordinates.
     * @param y       The value to add to the Y coordinates.
     * @param z       The value to add to the Z coordinates.
     *
     * @return  A new {@link org.bukkit.Location}.
     */
    public static Location add(Location source, double x, double y, double z) {
        return add(source, x, y, z, new Location(null, 0, 0, 0));
    }

    /**
     * Copy a source {@link org.bukkit.Location} to an output location and add values to the
     * output without changing the original {@link org.bukkit.Location}.
     *
     * @param source  The source location.
     * @param x       The value to add to the X coordinates.
     * @param y       The value to add to the Y coordinates.
     * @param z       The value to add to the Z coordinates.
     * @param output  The location to put the results into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location add(Location source, double x, double y, double z, Location output) {
        return copy(source, output).add(x, y, z);
    }

    /**
     * Copy a source {@link org.bukkit.Location} and add noise to the copy.
     *
     * <p>Translates the location to another random location within the specified
     * radius of the source location randomly.</p>
     *
     * @param source   The location.
     * @param radiusX  The max radius on the X axis.
     * @param radiusY  The max radius on the Y axis.
     * @param radiusZ  The max radius on the Z axis.
     *
     * @return  A new {@link org.bukkit.Location}.
     */
    public static Location addNoise(Location source, double radiusX, double radiusY, double radiusZ) {
        PreCon.notNull(source);

        return addNoise(source, radiusX, radiusY, radiusZ, new Location(null, 0, 0, 0));
    }

    /**
     * Copy a source {@link org.bukkit.Location} to an output location and add noise
     * to the output.
     *
     * <p>Translates the location to another random location within the specified
     * radius of the source location randomly.</p>
     *
     * @param source   The location.
     * @param radiusX  The max radius on the X axis.
     * @param radiusY  The max radius on the Y axis.
     * @param radiusZ  The max radius on the Z axis.
     * @param output   The location to put the results into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location addNoise(Location source,
                                    double radiusX, double radiusY, double radiusZ,
                                    Location output) {
        PreCon.notNull(source);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);
        PreCon.notNull(output);

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

        return copy(source, output).add(noiseX, noiseY, noiseZ);
    }

    /**
     * Determine if 2 locations can be considered the same using the specified
     * precision.
     *
     * <p>The precision is used as: location1 is about the same as location2 +/- precision.</p>
     *
     * @param location1  The first location to compare.
     * @param location2  The second location to compare.
     * @param precision  The precision.
     */
    public static boolean isLocationMatch(Location location1, Location location2, double precision) {
        PreCon.notNull(location1);
        PreCon.notNull(location2);
        PreCon.positiveNumber(precision);

        double xDelta = Math.abs(location1.getX() - location2.getX());
        double zDelta = Math.abs(location1.getZ() - location2.getZ());
        double yDelta = Math.abs(location1.getY() - location2.getY());

        return xDelta <= precision && zDelta <= precision && yDelta <= precision;
    }

    /**
     * Parse a {@link org.bukkit.Location} from a formatted string.
     *
     * <p>Format of string : x,y,z</p>
     *
     * @param world        The world the location is for.
     * @param coordinates  The string coordinates.
     *
     * @return  A new {@link org.bukkit.Location} or null if a location could not be parsed.
     */
    @Nullable
    public static Location parseSimpleLocation(World world, String coordinates) {
        return parseSimpleLocation(world, coordinates, new Location(null, 0, 0, 0));
    }

    /**
     * Parse a {@link org.bukkit.Location} from a formatted string.
     *
     * <p>Format of string : x,y,z</p>
     *
     * @param world        The world the location is for.
     * @param coordinates  The string coordinates.
     * @param output       The location place the results in.
     *
     * @return  The output {@link org.bukkit.Location} or null if a location could not be parsed.
     */
    @Nullable
    public static Location parseSimpleLocation(World world, String coordinates, Location output) {
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
     * Parse a {@link org.bukkit.Location} from a formatted string.
     *
     * <p>Format of string: x,y,z,yawF,pitchF,worldName</p>
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
     *
     * <p>Format of string: x,y,z,yawF,pitchF,worldName</p>
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
     *
     * <p>Format of string: x,y,z,yawF,pitchF,worldName</p>
     *
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
     * Convert a {@link org.bukkit.Location} to a parsable string.
     *
     * @param location  The location to convert.
     */
    public static String serialize(Location location) {
        PreCon.notNull(location);

        return String.valueOf(location.getX()) + ',' + location.getY() + ',' + location.getZ() +
                ',' + location.getYaw() + ',' + location.getPitch() + ',' + location.getWorld().getName();
    }

    /**
     * Convert a {@link org.bukkit.Location} to a parsable string.
     *
     * @param location             The location to convert.
     * @param floatingPointPlaces  The number of places in the floating point values.
     */
    public static String serialize(Location location, int floatingPointPlaces) {
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
    public static BlockFace getYawBlockFace(Location location) {
        PreCon.notNull(location);

        return getYawBlockFace(location.getYaw());
    }

    /**
     * Convert a yaw angle to a {@link org.bukkit.block.BlockFace}.
     *
     * @param yaw  The yaw angle to convert.
     */
    public static BlockFace getYawBlockFace(float yaw) {

        yaw = yaw + 11.25f;
        yaw = yaw < 0
                ? 360 - (Math.abs(yaw) % 360)
                : yaw % 360;

        int i = (int)(yaw / 22.5);

        return YAW_FACES[i];
    }

    /**
     * Find a surface block (solid block that can be walked on) {@link org.bukkit.Location}
     * below the provided search location.
     *
     * @param source  The source location.
     *
     * @return  A new {@link org.bukkit.Location} or null if the search reaches below 0 on
     * the Y axis.
     */
    @Nullable
    public static Location findSurfaceBelow(Location source) {
        return findSurfaceBelow(source, new Location(null, 0, 0, 0));
    }

    /**
     * Find a surface block (solid block that can be walked on) {@link org.bukkit.Location}
     * below the specified source location.
     *
     * @param source  The source location.
     *
     * @return  The output {@link org.bukkit.Location} or null if the search reaches below
     * 0 on the Y axis.
     */
    @Nullable
    public static Location findSurfaceBelow(Location source, Location output) {
        PreCon.notNull(source);
        PreCon.notNull(output);

        output.setWorld(source.getWorld());
        output.setX(source.getX());
        output.setY(source.getBlockY());
        output.setZ(source.getZ());
        output.setYaw(source.getYaw());
        output.setPitch(source.getPitch());

        if (!Materials.isTransparent(output.getBlock().getType()))
            return output;

        output.add(0, -1, 0);
        Block current = source.getBlock();

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
     * Get the {@link org.bukkit.Location} closest to the specified source location.
     *
     * @param source     The source location.
     * @param locations  The location candidates.
     */
    @Nullable
    public static Location getClosestLocation(Location source, Collection<Location> locations) {
        return getClosestLocation(source, locations, null);
    }

    /**
     * Get the {@link org.bukkit.Location} closest to the specified source location.
     *
     * @param source     The source location.
     * @param locations  The location candidates.
     * @param validator  The validator used to determine if a location is a candidate.
     */
    @Nullable
    public static Location getClosestLocation(Location source, Collection<Location> locations,
                                              @Nullable IValidator<Location> validator) {
        PreCon.notNull(source);
        PreCon.notNull(locations);

        Location closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Location loc : locations) {
            if (validator != null && !validator.isValid(loc))
                continue;

            double dist;
            if ((dist = source.distanceSquared(loc)) < closestDist) {
                closest = loc;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * Determine if a target {@link org.bukkit.Location} is within the specified radius of
     * a source location.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param source   The source {@link org.bukkit.Location}.
     * @param target   The target {@link org.bukkit.Location}.
     * @param radiusX  The x-axis radius.
     * @param radiusY  The y-axis radius.
     * @param radiusZ  The z-axis radius.
     */
    public static boolean isInRange(Location source, Location target,
                                          double radiusX, double radiusY, double radiusZ) {
        PreCon.notNull(source);
        PreCon.notNull(target);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        if (Double.compare(radiusX, radiusZ) == 0 &&
                Double.compare(radiusY, radiusZ) == 0) {
            return source.distanceSquared(target) <= radiusX * radiusX;
        }
        else {
            double deltaX = Math.abs(source.getX() - target.getX());
            double deltaY = Math.abs(source.getY() - target.getY());
            double deltaZ = Math.abs(source.getZ() - target.getZ());

            return deltaX <= radiusX && deltaY <= radiusY && deltaZ <= radiusZ;
        }
    }

    /**
     * Get a {@link org.bukkit.Location} that is a specified distance from a source location
     * using the source locations yaw angle to determine the direction of the new location
     * from the source location.
     *
     * <p>The new points Y coordinates are the same as the source location.</p>
     *
     * @param source    The source location.
     * @param distance  The distance from the source location.
     *
     * @return  A new {@link org.bukkit.Location}.
     */
    public static Location getYawLocation(Location source, double distance) {
        return getYawLocation(source, distance, source.getYaw(), new Location(null, 0, 0, 0));
    }

    /**
     * Get a {@link org.bukkit.Location} that is a specified distance from a source location
     * using the source locations yaw angle to determine the direction of the new location
     * from the source location.
     *
     * <p>The new points Y coordinates are the same as the source location.</p>
     *
     * @param source    The source location.
     * @param distance  The distance from the source location.
     * @param output    The {@link org.bukkit.Location} to output the results into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location getYawLocation(Location source, double distance, Location output) {
        return getYawLocation(source, distance, source.getYaw(), output);
    }

    /**
     * Get a {@link org.bukkit.Location} that is a specified distance from a source location
     * using the specified yaw angle to determine the direction of the new location
     * from the source location.
     *
     * <p>The new points Y coordinates are the same as the source location.</p>
     *
     * @param source    The source location.
     * @param distance  The distance from the source location.
     * @param yaw       The minecraft yaw angle (-180 to 180).
     *
     * @return  A new {@link org.bukkit.Location}.
     */
    public static Location getYawLocation(Location source, double distance, float yaw) {
        return getYawLocation(source, distance, yaw, new Location(null, 0, 0, 0));
    }

    /**
     * Get a {@link org.bukkit.Location} that is a specified distance from a source location
     * using the specified yaw angle to determine the direction of the new location
     * from the source location.
     *
     * <p>The new points Y coordinates are the same as the source location.</p>
     *
     * @param source    The source location.
     * @param distance  The distance from the source location.
     * @param yaw       The minecraft yaw angle (-180 to 180).
     * @param output    The {@link org.bukkit.Location} to output the result into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location getYawLocation(Location source, double distance,
                                       float yaw, Location output) {
        PreCon.notNull(source);
        PreCon.notNull(output);

        yaw = clampYaw(yaw);

        double radianYaw = Math.toRadians(-yaw);

        double x = Math.sin(radianYaw) * distance;
        double z = Math.cos(radianYaw) * distance;

        output.setWorld(source.getWorld());
        output.setX(source.getX() + x);
        output.setY(source.getY());
        output.setZ(source.getZ() + z);
        output.setYaw(source.getYaw());
        output.setPitch(source.getPitch());

        return output;
    }

    /**
     * Get the Minecraft yaw angle from the source location towards the target location.
     *
     * @param source  The source {@link org.bukkit.Location}.
     * @param target  The target {@link org.bukkit.Location}.
     */
    public static float getYawAngle(Location source, Location target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        // Y and X to prevent ide warnings on Math.atan2
        double deltaY = target.getX() - source.getX();
        double deltaX = target.getZ() - source.getZ();

        double angle = Math.atan2(deltaY, deltaX);

        return -(float)Math.toDegrees(angle);
    }

    /**
     * Ensure a yaw angle is between -180 and 180 degrees.
     *
     * @param yaw  The yaw angle to clamp.
     *
     * @return Clamped yaw angle.
     */
    public static float clampYaw(float yaw) {
        return yaw >= 0
                ? ((180 + yaw) % 360) - 180
                : 180 - ((Math.abs(yaw) + 180) % 360);
    }

    /**
     * Ensure a pitch angle is no more than 90 degrees and no less than -90 degrees.
     *
     * @param pitch  The pitch angle to limit.
     *
     * @return Limited pitch angle.
     */
    public static float limitPitch(float pitch) {
        return pitch >= 0
                ? Math.min(90, pitch)
                : Math.max(-90, pitch);
    }

    /**
     * Rotate a {@link org.bukkit.Location} around an axis {@link org.bukkit.Location}.
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

        return rotate(axis, location, rotationX, rotationY, rotationZ,
                new Location(null, 0, 0, 0));
    }

    /**
     * Rotate a {@link org.bukkit.Location} around an axis {@link org.bukkit.Location}.
     *
     * @param axis       The axis location.
     * @param location   The location to move.
     * @param rotationX  The rotation around the X axis in degrees.
     * @param rotationY  The rotation around the Y axis in degrees.
     * @param rotationZ  The rotation around the Z axis in degrees.
     * @param output     The location to put results into.
     *
     * @return  The output location.
     */
    public static Location rotate(Location axis, Location location,
                                  double rotationX, double rotationY, double rotationZ,
                                  Location output) {
        PreCon.notNull(axis);
        PreCon.notNull(location);
        PreCon.notNull(output);

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
