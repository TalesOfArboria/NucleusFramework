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

import com.jcwhatever.nucleus.storage.DeserializeException;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.IBinarySerializable;
import com.jcwhatever.nucleus.utils.file.NucleusByteReader;
import com.jcwhatever.nucleus.utils.file.NucleusByteWriter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * 3D immutable integer coordinates with no {@link org.bukkit.World} context.
 */
public class Coords3Di  implements IDataNodeSerializable, IBinarySerializable {

    /**
     * Get {@link Coords3Di} from a {@link org.bukkit.Location}.
     *
     * @param location  The location to convert.
     */
    public static Coords3Di fromLocation(Location location) {
        return new Coords3Di(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get {@link Coords3Di} from a {@link org.bukkit.util.Vector}.
     *
     * @param vector  The vector to convert.
     */
    public static Coords3Di fromVector(Vector vector) {
        return new Coords3Di(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    private int _x;
    private int _y;
    private int _z;

    /**
     * Constructor.
     *
     * @param x  The x coordinates.
     * @param y  The y coordinates.
     * @param z  The z coordinates.
     */
    public Coords3Di(int x, int y, int z) {
        _x = x;
        _y = y;
        _z = z;
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates.</p>
     *
     * @param source  The source coordinates.
     */
    public Coords3Di(Coords3Di source) {
        _x = source._x;
        _y = source._y;
        _z = source._z;
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates and adds delta values.</p>
     *
     * @param source  The source coordinates.
     * @param deltaX  The X coordinate values to add to the source coordinates.
     * @param deltaY  The Y coordinate values to add to the source coordinates.
     * @param deltaZ  The Z coordinate values to add to the source coordinates.
     */
    public Coords3Di(Coords3Di source, int deltaX, int deltaY, int deltaZ) {
        _x = source._x + deltaX;
        _y = source._y + deltaY;
        _z = source._z + deltaZ;
    }

    /**
     * Protected constructor for serialization.
     */
    protected Coords3Di() {}

    /**
     * Get the X coordinates.
     */
    public int getX() {
        return _x;
    }

    /**
     * Get the Y coordinates.
     */
    public int getY() {
        return _y;
    }

    /**
     * Get the Z coordinates.
     */
    public int getZ() {
        return _z;
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param coords  The other coordinates.
     */
    public double distance(Coords3D coords) {
        PreCon.notNull(coords);

        return Math.sqrt(distanceSquared(coords));
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param coords  The other coordinates.
     */
    public double distance(Coords3Di coords) {
        PreCon.notNull(coords);

        return Math.sqrt(distanceSquared(coords));
    }

    /**
     * Get the distance from this coordinates to another coordinates squared.
     *
     * @param coords  The other coordinates.
     */
    public double distanceSquared(Coords3D coords) {
        PreCon.notNull(coords);

        double deltaX = coords.getX() - _x;
        double deltaY = coords.getY() - _y;
        double deltaZ = coords.getZ() - _z;

        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    /**
     * Get the distance from this coordinates to another coordinates squared.
     *
     * @param coords  The other coordinates.
     */
    public double distanceSquared(Coords3Di coords) {
        PreCon.notNull(coords);

        double deltaX = coords._x - _x;
        double deltaY = coords._y - _y;
        double deltaZ = coords._z - _z;

        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    /**
     * Create delta coordinates by subtracting other coordinates from
     * this coordinates.
     *
     * @param coords  The other coordinates.
     */
    public Coords3Di getDelta(Coords3Di coords) {
        PreCon.notNull(coords);

        int deltaX = getX() - coords.getX();
        int deltaY = getY() - coords.getY();
        int deltaZ = getZ() - coords.getZ();

        return new Coords3Di(deltaX, deltaY, deltaZ);
    }

    /**
     * Get a {@link org.bukkit.block.Block} from the specified {@link org.bukkit.World}
     * using this coordinates.
     *
     * @param world  The {@link org.bukkit.World} the block is in.
     */
    public Block getBlock(World world) {
        PreCon.notNull(world);

        return world.getBlockAt(getX(), getY(), getZ());
    }

    /**
     * Create a new {@link org.bukkit.Location} from the coordinates.
     *
     * @param world  The {@link org.bukkit.World} value of the new location.
     */
    public Location toLocation(@Nullable World world) {
        return toLocation(new Location(world, 0, 0, 0));
    }

    /**
     * Copy coordinate values into an output {@link org.bukkit.Location}.
     *
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output location.
     */
    public Location toLocation(Location output) {
        PreCon.notNull(output);

        output.setX(_x);
        output.setY(_y);
        output.setZ(_z);
        return output;
    }

    /**
     * Create a new {@link org.bukkit.util.Vector} from the coordinates.
     */
    public Vector toVector() {
        return toVector(new Vector(0, 0, 0));
    }

    /**
     * Copy coordinate values into an output {@link org.bukkit.util.Vector}.
     *
     * @param output  The output {@link org.bukkit.util.Vector}.
     *
     * @return  The output location.
     */
    public Vector toVector(Vector output) {
        output.setX(_x);
        output.setY(_y);
        output.setZ(_z);
        return output;
    }

    /**
     * Create a new {@link Coords3D} using the coordinate values.
     */
    public Coords3D to3D() {
        return new Coords3D(getX(), getY(), getZ());
    }

    /**
     * Create a new {@link Coords2D} using the coordinate values.
     *
     * <p>Drops the Y coordinate.</p>
     */
    public Coords2D to2D() {
        return new Coords2D(getX(), getZ());
    }

    /**
     * Create a new {@link Coords2Di} using the coordinate values.
     *
     * <p>Drops the Y coordinate.</p>
     */
    public Coords2Di to2Di() {
        return new Coords2Di(getX(), getZ());
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("x", _x);
        dataNode.set("y", _y);
        dataNode.set("z", _z);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _x = dataNode.getInteger("x");
        _y = dataNode.getInteger("y");
        _z = dataNode.getInteger("z");
    }

    @Override
    public void serializeToBytes(NucleusByteWriter writer) throws IOException {
        writer.write(_x);
        writer.write(_y);
        writer.write(_z);
    }

    @Override
    public void deserializeFromBytes(NucleusByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException {

        _x = reader.getInteger();
        _y = reader.getInteger();
        _z = reader.getInteger();
    }

    @Override
    public int hashCode() {
        return _x ^ _y ^ _z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof Coords3Di) {
            Coords3Di other = (Coords3Di)obj;

            return other._x == _x &&
                    other._y == _y &&
                    other._z == _z;
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { x:" + _x + ", y:" + _y + ", z:" + _z + '}';
    }

    protected void deserialize(int x, int y, int z) {
        if (_x == 0 && _y == 0 && _z == 0) {
            _x = x;
            _y = y;
            _z = z;
        }
        else {
            throw new IllegalStateException("Coords3Di is immutable.");
        }
    }
}

