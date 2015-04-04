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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.NucleusByteReader;
import com.jcwhatever.nucleus.utils.file.NucleusByteWriter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * 3D immutable coordinates with no {@link org.bukkit.World} context.
 */
public class Coords3D extends Coords2D implements ICoords3D {

    /**
     * Get {@link Coords3D} from a {@link org.bukkit.Location}.
     *
     * @param location  The location to convert.
     */
    public static Coords3D fromLocation(Location location) {
        return new Coords3D(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Get {@link Coords3D} from a {@link org.bukkit.util.Vector}.
     *
     * @param vector  The vector to convert.
     */
    public static Coords3D fromVector(Vector vector) {
        return new Coords3D(vector.getX(), vector.getY(), vector.getZ());
    }

    private double _y;
    private boolean _canSeal;

    /**
     * Constructor.
     *
     * @param x  The x coordinates.
     * @param y  The y coordinates.
     * @param z  The z coordinates.
     */
    public Coords3D(double x, double y, double z) {
        super(x, z);
        _y = y;
        _canSeal = true;
        seal();
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates.</p>
     *
     * @param source  The source coordinates.
     */
    public Coords3D(ICoords3D source) {
        this(source.getX(), source.getY(), source.getZ());
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
    public Coords3D(ICoords3D source, double deltaX, double deltaY, double deltaZ) {
        this(source.getX() + deltaX, source.getY() + deltaY, source.getZ() + deltaZ);
    }

    /**
     * Protected constructor for serialization.
     */
    protected Coords3D() {}

    @Override
    public double getY() {
        return _y;
    }

    @Override
    public int getFloorY() {
        return getFloorValue(_y);
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param coords  The other coordinates.
     */
    public double distance(ICoords3D coords) {
        PreCon.notNull(coords);

        return Math.sqrt(distanceSquared(coords));
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param coords  The other coordinates.
     */
    public double distance(ICoords3Di coords) {
        PreCon.notNull(coords);

        return Math.sqrt(distanceSquared(coords));
    }

    /**
     * Get the distance from this coordinates to another coordinates squared.
     *
     * @param coords  The other coordinates.
     */
    public double distanceSquared(ICoords3D coords) {
        PreCon.notNull(coords);

        double deltaX = coords.getX() - getX();
        double deltaY = coords.getY() - _y;
        double deltaZ = coords.getZ() - getZ();

        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    /**
     * Get the distance from this coordinates to another coordinates squared.
     *
     * @param coords  The other coordinates.
     */
    public double distanceSquared(ICoords3Di coords) {
        PreCon.notNull(coords);

        double deltaX = coords.getX() - getX();
        double deltaY = coords.getY() - _y;
        double deltaZ = coords.getZ() - getZ();

        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    /**
     * Create delta coordinates by subtracting other coordinates from
     * this coordinates.
     *
     * @param coords  The other coordinates.
     */
    public Coords3D getDelta(ICoords3D coords) {
        PreCon.notNull(coords);

        double deltaX = getX() - coords.getX();
        double deltaY = getY() - coords.getY();
        double deltaZ = getZ() - coords.getZ();

        return new Coords3D(deltaX, deltaY, deltaZ);
    }

    /**
     * Get a {@link org.bukkit.block.Block} from the specified {@link org.bukkit.World}
     * using this coordinates.
     *
     * @param world  The {@link org.bukkit.World} the block is in.
     */
    public Block getBlock(World world) {
        PreCon.notNull(world);

        return world.getBlockAt(getFloorX(), getFloorY(), getFloorZ());
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

        output.setX(getX());
        output.setY(_y);
        output.setZ(getZ());
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
        output.setX(getX());
        output.setY(_y);
        output.setZ(getZ());
        return output;
    }

    /**
     * Create a new {@link Coords3Di} using the floor coordinate values.
     */
    public Coords3Di to3Di() {
        return new Coords3Di(getFloorX(), getFloorY(), getFloorZ());
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
     * Copy values to an output {@link org.bukkit.Location}.
     *
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    @Override
    public Location copyTo(Location output) {
        PreCon.notNull(output);

        output.setY(_y);
        return super.copyTo(output);
    }

    /**
     * Copy values to an output {@link org.bukkit.Location}.
     *
     * @param world   The {@link org.bukkit.World} to put into the output {@link org.bukkit.Location}.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    @Override
    public Location copyTo(@Nullable World world, Location output) {
        PreCon.notNull(output);

        output.setY(_y);
        return super.copyTo(world, output);
    }

    @Override
    public void serialize(IDataNode dataNode) {
        super.serialize(dataNode);
        dataNode.set("y", _y);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        super.deserialize(dataNode);
        _y = dataNode.getDouble("y");
        _canSeal = true;
        seal();
    }

    @Override
    public void serialize(NucleusByteWriter writer) throws IOException {
        super.serialize(writer);
        writer.write(_y);
    }

    @Override
    public void deserialize(NucleusByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException {

        super.deserialize(reader);
        _y = reader.getDouble();
        _canSeal = true;
        seal();
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (int)_y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof Coords3D) {
            Coords3D other = (Coords3D)obj;

            return other.getX() == getX() &&
                    other._y == _y &&
                    other.getZ() == getZ();
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { x:" + getX() + ", y:" + _y + ", z:" + getZ() + '}';
    }

    /**
     * Set the Y coordinate.
     *
     * @param y  The Y coordinate.
     *
     * @throws java.lang.IllegalStateException if the object is immutable.
     */
    protected void setY(double y) {
        if (isImmutable())
            throw new IllegalStateException("Coords3D is immutable.");

        _y = y;
    }

    @Override
    protected void seal() {
        if (_canSeal)
            super.seal();
    }
}
