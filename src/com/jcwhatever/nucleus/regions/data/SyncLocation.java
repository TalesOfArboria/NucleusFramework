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

package com.jcwhatever.nucleus.regions.data;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

/**
 * A synchronized extension to Bukkit's {@code Location} class that is
 * partially thread safe. It's not thread safe when getting Bukkit objects such
 * as {@code Block}, {@code World} or {@code Chunk}.
 *
 * <p>Useful for creating/loading a {@code Location} from an asynchronous thread where
 * it's not safe to retrieve the Bukkit {@code World} object. The {@code SyncLocation} can
 * hold the name of the world until it's safe to retrieve the Bukkit {@code World} (safety
 * is determined by the coder using the class).</p>
 */
public class SyncLocation extends Location {

    private String _worldName;
    protected final Object _sync = new Object();

    /**
     * Constructor.
     *
     * @param location The {@code Location} instance to get info from.
     */
    public SyncLocation(Location location) {
        this(location.getWorld(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    /**
     * Constructor.
     *
     * @param location The {@code LocationInfo} instance to get info from.
     */
    public SyncLocation(SyncLocation location) {
        this(location.getWorldName(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    /**
     * Constructor.
     *
     * @param world The world the location is in.
     * @param x     The X coordinates.
     * @param y     The Y coordinates.
     * @param z     The Z coordinates.
     * @param yaw   The yaw angle.
     * @param pitch The pitch angle.
     */
    public SyncLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);

        if (world != null)
            _worldName = world.getName();
    }

    /**
     * Constructor.
     *
     * @param worldName The name of the world the location is in.
     * @param x         The X coordinates.
     * @param y         The Y coordinates.
     * @param z         The Z coordinates.
     * @param yaw       The yaw angle.
     * @param pitch     The pitch angle.
     */
    public SyncLocation(@Nullable String worldName, double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
        _worldName = worldName;
    }

    /**
     * Constructor.
     *
     * @param worldName The name of the world the location is in.
     * @param x         The X coordinate block value.
     * @param y         The Y coordinate block value.
     * @param z         The Z coordinate block value.
     */
    public SyncLocation(@Nullable String worldName, int x, int y, int z) {
        super(null, x, y, z);

        _worldName = worldName;
    }

    /**
     * Constructor.
     *
     * @param world  The world the location is in.
     * @param x      The X coordinate block value.
     * @param y      The Y coordinate block value.
     * @param z      The Z coordinate block value.
     */
    public SyncLocation(@Nullable World world, int x, int y, int z) {
        super(world, x, y, z);

        if (world != null)
            _worldName = world.getName();
    }

    /**
     * Get the name of the world the location is in.
     */
    public String getWorldName() {
        synchronized (_sync) {
            return _worldName;
        }
    }

    @Override
    public World getWorld() {
        synchronized (_sync) {
            if (super.getWorld() == null && _worldName != null) {
                super.setWorld(Bukkit.getWorld(_worldName));
            }

            return super.getWorld();
        }
    }

    @Override
    public void setWorld(@Nullable World world) {
        synchronized (_sync) {
            _worldName = world != null ? world.getName() : null;
            super.setWorld(world);
        }
    }

    /**
     * Get the chunk the location is in.
     *
     * <p>Not thread safe.</p>
     *
     * @return Null if the location does not have a world set.
     */
    @Override
    @Nullable
    public Chunk getChunk() {
        if (getWorld() == null)
            return null;

        return super.getChunk();
    }

    /**
     * Get the block at the location.
     *
     * <p>Not thread safe.</p>
     *
     * @return Null if a world is not set.
     */
    @Override
    @Nullable
    public Block getBlock() {
        if (getWorld() == null)
            return null;

        return super.getBlock();
    }

    @Override
    public double getX() {
        synchronized (_sync) {
            return super.getX();
        }
    }

    @Override
    public void setX(double x) {
        synchronized (_sync) {
            super.setX(x);
        }
    }

    @Override
    public double getY() {
        synchronized (_sync) {
            return super.getY();
        }
    }

    @Override
    public void setY(double y) {
        synchronized (_sync) {
            super.setY(y);
        }
    }

    @Override
    public double getZ() {
        synchronized (_sync) {
            return super.getZ();
        }
    }

    @Override
    public void setZ(double z) {
        synchronized (_sync) {
            super.setZ(z);
        }
    }

    @Override
    public float getYaw() {
        synchronized (_sync) {
            return super.getYaw();
        }
    }

    @Override
    public void setYaw(float yaw) {
        synchronized (_sync) {
            super.setYaw(yaw);
        }
    }

    @Override
    public float getPitch() {
        synchronized (_sync) {
            return super.getPitch();
        }
    }

    @Override
    public void setPitch(float pitch) {
        synchronized (_sync) {
            super.setPitch(pitch);
        }
    }

    @Override
    public int getBlockX() {
        synchronized (_sync) {
            return super.getBlockX();
        }
    }

    @Override
    public int getBlockY() {
        synchronized (_sync) {
            return super.getBlockY();
        }
    }

    @Override
    public int getBlockZ() {
        synchronized (_sync) {
            return super.getBlockZ();
        }
    }

    @Override
    public SyncLocation setDirection(Vector vector) {
        synchronized (_sync) {
            return (SyncLocation)super.setDirection(vector);
        }
    }

    @Override
    public SyncLocation add(Location location) {
        synchronized (_sync) {
            return (SyncLocation)super.add(location);
        }
    }

    @Override
    public SyncLocation add(double x, double y, double z) {
        synchronized (_sync) {
            return (SyncLocation)super.add(x, y, z);
        }
    }

    @Override
    public SyncLocation add(Vector vector) {
        synchronized (_sync) {
            return (SyncLocation)super.add(vector);
        }
    }

    @Override
    public SyncLocation subtract(Location location) {
        synchronized (_sync) {
            return (SyncLocation)super.subtract(location);
        }
    }

    @Override
    public SyncLocation subtract(Vector vector) {
        synchronized (_sync) {
            return (SyncLocation)super.subtract(vector);
        }
    }

    @Override
    public SyncLocation subtract(double x, double y, double z) {
        synchronized (_sync) {
            return (SyncLocation)super.subtract(x, y, z);
        }
    }

    @Override
    public SyncLocation multiply(double factor) {
        synchronized (_sync) {
            return (SyncLocation)super.multiply(factor);
        }
    }

    @Override
    public SyncLocation zero() {
        synchronized (_sync) {
            return (SyncLocation)super.zero();
        }
    }

    @Override
    public double length() {
        synchronized (_sync) {
            return super.length();
        }
    }

    @Override
    public double lengthSquared() {
        synchronized (_sync) {
            return super.lengthSquared();
        }
    }

    @Override
    public double distance(Location location) {
        return Math.sqrt(distanceSquared(location));
    }

    /**
     * Get the distance to the specified coordinates.
     *
     * @param x The X coordinates.
     * @param y The Y coordinates.
     * @param z The Z coordinates.
     */
    public double distance(double x, double y, double z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    @Override
    public double distanceSquared(Location location) {
        PreCon.notNull(location);

        synchronized (_sync) {

            if (location instanceof SyncLocation) {
                SyncLocation immutable = (SyncLocation) location;

                if (_worldName == null || immutable._worldName == null)
                    throw new IllegalArgumentException("Cannot measure distance without a specified world.");

                if (!_worldName.equals(immutable._worldName))
                    throw new IllegalArgumentException("Cannot measure distance between differing worlds.");
            } else {

                if (_worldName == null || location.getWorld() == null)
                    throw new IllegalArgumentException("Cannot measure distance without a specified world.");

                if (!_worldName.equals(location.getWorld().getName()))
                    throw new IllegalArgumentException("Cannot measure distance between differing worlds.");
            }

            return distanceSquared(location.getX(), location.getY(), location.getZ());
        }
    }

    /**
     * Get the distance squared to the specified coordinates.
     *
     * @param x The X coordinates.
     * @param y The Y coordinates.
     * @param z The Z coordinates.
     */
    public double distanceSquared(double x, double y, double z) {

        double dx = getX() - x;
        double dy = getY() - y;
        double dz = getZ() - z;

        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    /**
     * Get a Bukkit {@code Location}.
     *
     * <p>If invoked from an asynchronous thread, the returned {@code Location}'s {@code World}
     * value is null since it's not safe to retrieve the world object from any thread other
     * than the primary.</p>
     */
    public Location getBukkitLocation() {

        World world = super.getWorld();
        if (world == null && Bukkit.isPrimaryThread() && getWorldName() != null)
            world = Bukkit.getWorld(getWorldName());

        return new Location(world, getX(), getY(), getZ(), getYaw(), getPitch());
    }

    @Override
    public Vector toVector() {
        synchronized (_sync) {
            return super.toVector();
        }
    }

    @Override
    public SyncLocation clone() {
        return new SyncLocation(this);
    }

    @Override
    public int hashCode() {
        synchronized (_sync) {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        synchronized (_sync) {

            if (obj instanceof SyncLocation) {
                SyncLocation other = (SyncLocation) obj;

                return ((_worldName == null && other._worldName == null) ||
                        (_worldName != null && _worldName.equals(other._worldName))) &&
                        Double.compare(getX(), other.getX()) == 0 && Double.compare(getY(), other.getY()) == 0 &&
                        Double.compare(getZ(), other.getZ()) == 0 && Float.compare(getYaw(), other.getYaw()) == 0 &&
                        Float.compare(getPitch(), other.getPitch()) == 0;
            }
            else if (obj instanceof Location) {
                Location other = (Location) obj;

                return ((_worldName == null && other.getWorld() == null) ||
                        (_worldName != null && other.getWorld() != null && _worldName.equals(other.getWorld().getName()))) &&
                        Double.compare(getX(), other.getX()) == 0 && Double.compare(getY(), other.getY()) == 0 &&
                        Double.compare(getZ(), other.getZ()) == 0 && Float.compare(getYaw(), other.getYaw()) == 0 &&
                        Float.compare(getPitch(), other.getPitch()) == 0;
            }

            return false;
        }
    }
}