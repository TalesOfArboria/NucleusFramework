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

import com.jcwhatever.nucleus.providers.math.FastMath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.IByteReader;
import com.jcwhatever.nucleus.utils.file.IByteSerializable;
import com.jcwhatever.nucleus.utils.file.IByteWriter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.IOException;

/**
 * Implementation of {@link IVector2D}.
 */
public class Vector2D implements IVector2D, IDataNodeSerializable, IByteSerializable {

    protected double _x;
    protected double _z;

    /**
     * Constructor.
     *
     * <p>Initialize all coordinates to 0.</p>
     */
    public Vector2D() {}

    /**
     * Constructor.
     *
     * @param x  The X value.
     * @param z  The Z value.
     */
    public Vector2D(double x, double z) {
        _x = x;
        _z = z;
    }

    /**
     * Constructor.
     *
     * @param coords2D  The coordinates to copy from.
     */
    public Vector2D(ICoords2D coords2D) {
        this(coords2D.getX(), coords2D.getZ());
    }

    /**
     * Constructor.
     *
     * @param coords2D  The coordinates to copy from.
     */
    public Vector2D(ICoords2Di coords2D) {
        this(coords2D.getX(), coords2D.getZ());
    }

    /**
     * Constructor.
     *
     * @param vector  The Bukkit vector to copy from.
     */
    public Vector2D(Vector vector) {
        this(vector.getX(), vector.getZ());
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
        return (int)Math.floor(_x);
    }

    @Override
    public int getFloorZ() {
        return (int)Math.floor(_z);
    }

    @Override
    public Vector2D setX(double x) {
        _x = x;
        return this;
    }

    @Override
    public Vector2D setZ(double z) {
        _z = z;
        return this;
    }

    @Override
    public Vector2D set2D(double x, double z) {
        _x = x;
        _z = z;
        return this;
    }

    @Override
    public Vector2D copyFrom2D(ICoords2D coords) {
        PreCon.notNull(coords);

        _x = coords.getX();
        _z = coords.getZ();
        return this;
    }

    @Override
    public Vector2D copyFrom2D(ICoords2Di coords) {
        PreCon.notNull(coords);

        _x = coords.getX();
        _z = coords.getZ();
        return this;
    }

    @Override
    public Vector2D copyFrom2D(Vector vector) {
        PreCon.notNull(vector);

        _x = vector.getX();
        _z = vector.getZ();
        return this;
    }

    @Override
    public Vector2D copyFrom2D(Location location) {
        PreCon.notNull(location);

        _x = location.getX();
        _z = location.getZ();
        return this;
    }

    @Override
    public Vector2D copyTo2D(Vector vector) {
        PreCon.notNull(vector);

        vector.setX(_x);
        vector.setZ(_z);
        return this;
    }

    @Override
    public Vector2D copyTo2D(Location location) {
        PreCon.notNull(location);

        location.setX(_x);
        location.setZ(_z);
        return this;
    }

    @Override
    public Vector2D add2D(ICoords2D vector) {
        PreCon.notNull(vector);

        _x += vector.getX();
        _z += vector.getZ();
        return this;
    }

    @Override
    public Vector2D add2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        _x += vector.getX();
        _z += vector.getZ();
        return this;
    }

    @Override
    public Vector2D add2D(double value) {
        _x += value;
        _z += value;
        return this;
    }

    @Override
    public Vector2D add2DMax(double scalar, double value) {
        _x = Math.max(_x * scalar, value);
        _z = Math.max(_z * scalar, value);
        return this;
    }

    @Override
    public Vector2D add2DMin(double scalar, double value) {
        _x = Math.min(_x * scalar, value);
        _z = Math.min(_z * scalar, value);
        return this;
    }

    @Override
    public Vector2D addX(double value) {
        _x += value;
        return this;
    }

    @Override
    public Vector2D addZ(double value) {
        _z += value;
        return this;
    }

    @Override
    public Vector2D subtract2D(ICoords2D vector) {
        PreCon.notNull(vector);

        _x -= vector.getX();
        _z -= vector.getZ();
        return this;
    }

    @Override
    public Vector2D subtract2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        _x -= vector.getX();
        _z -= vector.getZ();
        return this;
    }

    @Override
    public Vector2D subtract2D(double scalar) {
        _x -= scalar;
        _z -= scalar;
        return this;
    }

    @Override
    public Vector2D subtract2DMax(double scalar, double value) {
        _x = Math.max(_x - scalar, value);
        _z = Math.max(_z - scalar, value);
        return this;
    }

    @Override
    public Vector2D subtract2DMin(double scalar, double value) {
        _x = Math.min(_x - scalar, value);
        _z = Math.min(_z - scalar, value);
        return this;
    }

    @Override
    public Vector2D subtractX(double value) {
        _x -= value;
        return this;
    }

    @Override
    public Vector2D subtractZ(double value) {
        _z -= value;
        return this;
    }

    @Override
    public Vector2D multiply2D(ICoords2D vector) {
        PreCon.notNull(vector);

        _x *= vector.getX();
        _z *= vector.getZ();
        return this;
    }

    @Override
    public Vector2D multiply2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        _x *= vector.getX();
        _z *= vector.getZ();
        return this;
    }

    @Override
    public Vector2D multiply2D(double scalar) {
        _x *= scalar;
        _z *= scalar;
        return this;
    }

    @Override
    public Vector2D multiply2DMax(double scalar, double value) {
        _x = Math.max(_x * scalar, value);
        _z = Math.max(_z * scalar, value);
        return this;
    }

    @Override
    public Vector2D multiply2DMin(double scalar, double value) {
        _x = Math.min(_x * scalar, value);
        _z = Math.min(_z * scalar, value);
        return this;
    }

    @Override
    public Vector2D multiplyX(double value) {
        _x *= value;
        return this;
    }

    @Override
    public Vector2D multiplyZ(double value) {
        _z *= value;
        return this;
    }

    @Override
    public Vector2D average2D(ICoords2D vector) {
        PreCon.notNull(vector);

        _x = (_x + vector.getX()) * 0.5D;
        _z = (_z + vector.getZ()) * 0.5D;
        return this;
    }

    @Override
    public Vector2D average2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        _x = (_x + vector.getX()) * 0.5D;
        _z = (_z + vector.getZ()) * 0.5D;
        return this;
    }

    @Override
    public Vector2D reverse2D() {
        _x *= -1;
        _z *= -1;
        return this;
    }

    @Override
    public Vector2D normalize() {
        double magnitude = getMagnitude2D();
        if (magnitude < 0.0D || magnitude > 0.0D) {
            _x /= magnitude;
            _z /= magnitude;
        }
        return this;
    }

    @Override
    public Vector2D reset() {
        _x = _z = 0;
        return this;
    }

    @Override
    public Vector2D abs() {
        _x = Math.abs(_x);
        _z = Math.abs(_z);
        return this;
    }

    @Override
    public float getAngle(ICoords2D vector) {
        PreCon.notNull(vector);

        // If vector, use vectors magnitude. Otherwise calculate
        double oMag = vector instanceof IVector2D
                ? ((IVector2D) vector).getMagnitude2D()
                : calculateMagnitude(vector);

        double dot = this instanceof IVector3D && vector instanceof ICoords3D
                ? ((IVector3D) this).getDot3D((ICoords3D) vector)
                : getDot2D(vector);

        return FastMath.acos(dot / (getMagnitude2D() * oMag));
    }

    @Override
    public float getAngle(ICoords2Di vector) {
        PreCon.notNull(vector);

        // If vector, use vectors magnitude. Otherwise calculate
        double oMag = calculateMagnitude(vector);
        double dot = getDot2D(vector);

        return FastMath.acos(dot / (getMagnitude2D() * oMag));
    }

    @Override
    public float getYawDelta(ICoords2D vector) {
        PreCon.notNull(vector);

        float localAngle = getYaw();
        float otherAngle = vector instanceof IVector2D
                ? ((IVector2D) vector).getYaw()
                : calculateYaw(vector);

        return otherAngle - localAngle;
    }

    @Override
    public float getYawDelta(ICoords2Di vector) {
        PreCon.notNull(vector);

        float localAngle = getYaw();
        float otherAngle = calculateYaw(vector);

        return otherAngle - localAngle;
    }

    @Override
    public float getYaw() {
        return calculateYaw(this);
    }

    @Override
    public double getDot2D(ICoords2D vector) {
        PreCon.notNull(vector);

        return _x * vector.getX() + _z * vector.getZ();
    }

    @Override
    public double getDot2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        return _x * vector.getX() + _z * vector.getZ();
    }

    @Override
    public double getMagnitude2D() {
        return FastMath.sqrt(_x * _x + _z * _z);
    }

    @Override
    public double getMagnitudeSquared2D() {
        return _x * _x + _z * _z;
    }

    @Override
    public double getDirection() {
        double angle = FastMath.atan2(_z, _x);
        float degrees = (float)Math.toDegrees(angle);
        return Float.compare(degrees, 0.0f) == 0 ? degrees : -degrees;
    }

    @Override
    public double getDistance2D(ICoords2D vector) {
        PreCon.notNull(vector);

        double deltaX = _x - vector.getX();
        double deltaZ = _z - vector.getZ();
        return FastMath.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    @Override
    public double getDistance2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        double deltaX = _x - vector.getX();
        double deltaZ = _z - vector.getZ();
        return FastMath.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    @Override
    public double getDistanceSquared2D(ICoords2D vector) {
        PreCon.notNull(vector);

        double deltaX = _x - vector.getX();
        double deltaZ = _z - vector.getZ();
        return deltaX * deltaX + deltaZ * deltaZ;
    }

    @Override
    public double getDistanceSquared2D(ICoords2Di vector) {
        PreCon.notNull(vector);

        double deltaX = _x - vector.getX();
        double deltaZ = _z - vector.getZ();
        return deltaX * deltaX + deltaZ * deltaZ;
    }

    @Override
    public Vector getBukkitVector() {
        return new Vector(_x, 0, _z);
    }

    @Override
    public Vector getBukkitVector(Vector output) {
        PreCon.notNull(output);

        output.setX(_x);
        output.setY(0);
        output.setZ(_z);
        return output;
    }

    @Override
    public void serialize(IByteWriter writer) throws IOException {
        writer.write(_x);
        writer.write(_z);
    }

    @Override
    public void deserialize(IByteReader reader) throws IOException {
        _x = reader.getDouble();
        _z = reader.getDouble();
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
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { x:" + getX() + ", z:" + getZ() + '}';
    }

    @Override
    public int hashCode() {
        return (int)(_x * 100) ^ (int)(_z * 100);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof Vector2D) {
            Vector2D other = (Vector2D)obj;

            return Double.compare(other.getX(), getX()) == 0
                    && Double.compare(other.getZ(), getZ()) == 0;
        }

        return false;
    }

    private static double calculateMagnitude(ICoords2D coords) {
        if (coords instanceof ICoords3D) {
            ICoords3D coords3D = (ICoords3D)coords;
            return FastMath.sqrt(coords.getX() * coords.getX()
                    + coords3D.getY() * coords3D.getY()
                    + coords.getZ() * coords.getZ());
        }
        else {
            return FastMath.sqrt(coords.getX() * coords.getX() + coords.getZ() * coords.getZ());
        }
    }

    private static double calculateMagnitude(ICoords2Di coords) {
        if (coords instanceof ICoords3Di) {
            ICoords3Di coords3D = (ICoords3Di)coords;
            return FastMath.sqrt(coords.getX() * coords.getX()
                    + coords3D.getY() * coords3D.getY()
                    + coords.getZ() * coords.getZ());
        }
        else {
            return FastMath.sqrt(coords.getX() * coords.getX() + coords.getZ() * coords.getZ());
        }
    }

    private static float calculateYaw(ICoords2D vector) {
        float yaw = (float)Math.toDegrees(FastMath.atan2(vector.getX(), vector.getZ()));
        return Float.compare(yaw, 0.0f) == 0 ? yaw : -yaw;
    }

    private static float calculateYaw(ICoords2Di vector) {
        float yaw = (float)Math.toDegrees(FastMath.atan2(vector.getX(), vector.getZ()));
        return Float.compare(yaw, 0.0f) == 0 ? yaw : -yaw;
    }
}
