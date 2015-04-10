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

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.IBinarySerializable;
import com.jcwhatever.nucleus.utils.file.IByteReader;
import com.jcwhatever.nucleus.utils.file.IByteWriter;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * 2D immutable coordinates.
 */
public class Coords2D implements ICoords2D, IDataNodeSerializable, IBinarySerializable {

    /**
     * Get a {@link Coords2D} from a {@link org.bukkit.Chunk}.
     *
     * @param chunk  The chunk to convert.
     */
    public static Coords2D fromChunk(Chunk chunk) {
        return new Coords2D(chunk.getX(), chunk.getZ());
    }

    /**
     * Get the distance from source coordinates to target coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distance(ICoords2D source, ICoords2D target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        return Math.sqrt(distanceSquared(source, target));
    }

    /**
     * Get the distance from source coordinates to target coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distance(ICoords2D source, ICoords2Di target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        return Math.sqrt(distanceSquared(source, target));
    }

    /**
     * Get distance squared between two coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distanceSquared(ICoords2D source, ICoords2D target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        double deltaX = target.getX() - source.getX();
        double deltaZ = target.getZ() - source.getZ();

        return deltaX * deltaX + deltaZ * deltaZ;
    }

    /**
     * Get distance squared between two coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distanceSquared(ICoords2D source, ICoords2Di target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        double deltaX = target.getX() - source.getX();
        double deltaZ = target.getZ() - source.getZ();

        return deltaX * deltaX + deltaZ * deltaZ;
    }

    /**
     * Get a {@link org.bukkit.Chunk} from the specified {@link org.bukkit.World}
     * at the specified coordinates.
     *
     * @param coords  The chunk coordinates.
     * @param world   The world the chunk is in.
     */
    public static Chunk getChunk(ICoords2D coords, World world) {
        PreCon.notNull(world);

        return world.getChunkAt(coords.getFloorX(), coords.getFloorZ());
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param coords  The coords to copy from.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location copyTo(ICoords2D coords, Location output) {
        PreCon.notNull(coords);
        PreCon.notNull(output);

        output.setX(coords.getX());
        output.setZ(coords.getZ());
        return output;
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param world   The {@link org.bukkit.World} to put into the output {@link org.bukkit.Location}.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location copyTo(ICoords2D coords, @Nullable World world, Location output) {
        copyTo(coords, output);
        output.setWorld(world);
        return output;
    }

    private double _x;
    private double _z;
    private boolean _isImmutable;

    /**
     * Constructor.
     *
     * @param x  The x coordinates.
     * @param z  The z coordinates.
     */
    public Coords2D(double x, double z) {
        _x = x;
        _z = z;
        seal();
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates.</p>
     *
     * @param source  The source coordinates.
     */
    public Coords2D(ICoords2D source) {
        this(source.getX(), source.getZ());
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates and adds delta values.</p>
     *
     * @param source  The source coordinates.
     * @param deltaX  The X coordinate values to add to the source coordinates.
     * @param deltaZ  The Z coordinate values to add to the source coordinates.
     */
    public Coords2D(ICoords2D source, double deltaX, double deltaZ) {
        this(source.getX() + deltaX, source.getZ() + deltaZ);
    }

    /**
     * Protected constructor for serialization.
     */
    protected Coords2D() {}

    /**
     * Determine if object is immutable.
     */
    public boolean isImmutable() {
        return _isImmutable;
    }

    @Override
    public double getX() {
        return _x;
    }

    @Override
    public double getZ() {
        return _z;
    }

    @Override
    public int getFloorX() {
        return getFloorValue(_x);
    }

    @Override
    public int getFloorZ() {
        return getFloorValue(_z);
    }

    /**
     * Get the distance from this coordinates to target coordinates.
     *
     * @param target  The target coordinates.
     */
    public double distance(ICoords2D target) {
        return distance(this, target);
    }

    /**
     * Get the distance from this coordinates to target coordinates.
     *
     * @param target  The target coordinates.
     */
    public double distance(ICoords2Di target) {
        return distance(this, target);
    }

    /**
     * Get the distance from this coordinates to target coordinates squared.
     *
     * @param target  The target coordinates.
     */
    public double distanceSquared(ICoords2D target) {
        return distanceSquared(this, target);
    }

    /**
     * Get the distance from this coordinates to target coordinates squared.
     *
     * @param target  The target coordinates.
     */
    public double distanceSquared(ICoords2Di target) {
        return distanceSquared(this, target);
    }

    /**
     * Create delta coordinates by subtracting other coordinates from
     * this coordinates.
     *
     * @param coords  The other coordinates.
     */
    public Coords2D getDelta(ICoords2D coords) {
        PreCon.notNull(coords);

        double deltaX = getX() - coords.getX();
        double deltaZ = getZ() - coords.getZ();

        return new Coords2D(deltaX, deltaZ);
    }

    /**
     * Create delta coordinates by subtracting other coordinates from
     * this coordinates.
     *
     * @param coords  The other coordinates.
     * @param output  The {@link MutableCoords2D} to put the results into.
     *
     * @return  The output {@link MutableCoords2D}.
     */
    public MutableCoords2D getDelta(ICoords2D coords, MutableCoords2D output) {
        PreCon.notNull(coords);

        double deltaX = getX() - coords.getX();
        double deltaZ = getZ() - coords.getZ();

        output.setX(deltaX);
        output.setZ(deltaZ);

        return output;
    }

    /**
     * Get a {@link org.bukkit.Chunk} from the specified {@link org.bukkit.World}
     * at the coordinates represented by the {@link Coords2D} instance.
     *
     * @param world  The world the chunk is in.
     */
    public Chunk getChunk(World world) {
        return getChunk(this, world);
    }

    /**
     * Create a new {@link Coords2Di} instance using the floor
     * values.
     */
    public Coords2Di to2Di() {
        return new Coords2Di(getFloorX(), getFloorZ());
    }

    /**
     * Create a new {@link Coords3D} instance using the x and
     * z coordinate values and the specified y coordinate values.
     *
     * @param y  The coordinate value.
     */
    public Coords3D to3D(double y) {
        return new Coords3D(getX(), y, getZ());
    }

    /**
     * Create a new {@link Coords3Di} instance using the x and
     * z coordinate values and the specified y coordinate values.
     *
     * @param y  The coordinate value.
     */
    public Coords3Di to3Di(int y) {
        return new Coords3Di(getFloorX(), y, getFloorZ());
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public Location copyTo(Location output) {
        return copyTo(this, output);
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param world   The {@link org.bukkit.World} to put into the output {@link org.bukkit.Location}.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public Location copyTo(@Nullable World world, Location output) {
        return copyTo(this, world, output);
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("x", _x);
        dataNode.set("z", _z);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _x = dataNode.getDouble("x");
        _z = dataNode.getDouble("z");
        seal();
    }

    @Override
    public void serialize(IByteWriter writer) throws IOException {
        writer.write(_x);
        writer.write(_z);
    }

    @Override
    public void deserialize(IByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException {

        _x = reader.getDouble();
        _z = reader.getDouble();
        seal();
    }

    @Override
    public int hashCode() {
        return (int)_x ^ (int)_z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof ICoords2D) {
            ICoords2D other = (ICoords2D)obj;

            return other.getX() == _x &&
                    other.getZ() == _z;
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { x:" + _x + ", z:" + _z + '}';
    }

    /**
     * Set the X coordinate.
     *
     * @param x  The X coordinate.
     *
     * @throws java.lang.IllegalStateException if the object is immutable.
     */
    protected void setX(double x) {
        if (_isImmutable)
            throw new IllegalStateException("Coordinate is immutable.");

        _x = x;
    }

    /**
     * Set the Z coordinate.
     *
     * @param z  The Z coordinate.
     *
     * @throws java.lang.IllegalStateException if the object is immutable.
     */
    protected void setZ(double z) {
        if (_isImmutable)
            throw new IllegalStateException("Coordinate is immutable.");

        _z = z;
    }

    /**
     * Invoked to make the object immutable.
     */
    protected void seal() {
        _isImmutable = true;
    }

    /**
     * Calculate a floor value.
     *
     * @param value  The value to floor.
     *
     * @return  The floored value.
     */
    protected int getFloorValue(double value) {
        int floor = (int) value;
        return (double) floor == value
                ? floor
                : floor - (int) (Double.doubleToRawLongBits(value) >>> 63);
    }
}

