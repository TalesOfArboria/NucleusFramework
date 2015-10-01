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
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Implementation of {@link IVector2D}.
 */
public class Vector2D implements IVector2D {

    protected double _x;
    protected double _z;

    protected transient double _magnitude;
    protected transient double _direction;
    protected transient float _yaw;
    protected transient boolean _hasMagnitude;
    protected transient boolean _hasDirection;
    protected transient boolean _hasYaw;

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

        if (coords2D instanceof Vector2D)
            copyCaches((Vector2D)coords2D);
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
        onChange();
        return this;
    }

    @Override
    public Vector2D setZ(double z) {
        _z = z;
        onChange();
        return this;
    }

    @Override
    public Vector2D copyFrom2D(ICoords2D coords) {
        PreCon.notNull(coords);

        _x = coords.getX();
        _z = coords.getZ();

        if (coords instanceof Vector2D) {
            copyCaches((Vector2D)coords);
        }

        onChange();
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
        onChange();
        return this;
    }

    @Override
    public Vector2D add2D(double value) {
        _x += value;
        _z += value;
        onChange();
        return this;
    }

    @Override
    public Vector2D subtract2D(ICoords2D vector) {
        PreCon.notNull(vector);

        _x -= vector.getX();
        _z -= vector.getZ();
        onChange();
        return this;
    }

    @Override
    public Vector2D subtract2D(double scalar) {
        _x -= scalar;
        _z -= scalar;
        onChange();
        return this;
    }

    @Override
    public Vector2D multiply2D(ICoords2D vector) {
        PreCon.notNull(vector);

        _x *= vector.getX();
        _z *= vector.getZ();
        onChange();
        return this;
    }

    @Override
    public Vector2D multiply2D(double scalar) {
        _x *= scalar;
        _z *= scalar;
        onChange();
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
    public Vector2D reverse2D() {
        _x *= -1;
        _z *= -1;
        return this;
    }

    @Override
    public Vector2D normalize() {
        double magnitude = getMagnitude();
        if (magnitude < 0.0D || magnitude > 0.0D) {
            _x /= magnitude;
            _z /= magnitude;
            onChange();
            _magnitude = 1;
            _hasMagnitude = true;
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
        onChange();
        return this;
    }

    @Override
    public float getAngle(ICoords2D vector) {
        PreCon.notNull(vector);

        // If vector, use vectors magnitude. Otherwise calculate
        double oMag = vector instanceof IVector2D
                ? ((IVector2D) vector).getMagnitude()
                : calculateMagnitude(vector);

        double dot = this instanceof IVector3D && vector instanceof ICoords3D
                ? ((IVector3D) this).getDot3D((ICoords3D) vector)
                : getDot2D(vector);

        return FastMath.acos(dot / (getMagnitude() * oMag));
    }

    @Override
    public float getYawDelta(ICoords2D vector) {
        PreCon.notNull(vector);

        if (_hasYaw && vector instanceof Vector2D && ((Vector2D) vector)._hasYaw) {
            return ((Vector2D) vector)._yaw - _yaw;
        }

        float localAngle = getYaw();
        float otherAngle = vector instanceof IVector2D
                ? ((IVector2D) vector).getYaw()
                : calculateYaw(vector);

        return otherAngle - localAngle;
    }

    @Override
    public float getYaw() {
        if (_hasYaw)
            return _yaw;

        _hasYaw = true;
        return _yaw = calculateYaw(this);
    }

    @Override
    public double getDot2D(ICoords2D vector) {
        PreCon.notNull(vector);

        return _x * vector.getX() + _z * vector.getZ();
    }

    @Override
    public double getMagnitude() {
        if (_hasMagnitude)
            return _magnitude;

        _hasMagnitude = true;
        return _magnitude = calculateMagnitude(this);
    }

    @Override
    public double getMagnitudeSquared() {
        return _x * _x + _z * _z;
    }

    @Override
    public double getDirection() {
        if (_hasDirection)
            return _direction;

        _hasDirection = true;
        double angle = FastMath.atan2(_z, _x);
        float degrees = (float)Math.toDegrees(angle);
        return _direction = Float.compare(degrees, 0.0f) == 0 ? degrees : -degrees;
    }

    @Override
    public double getDistance2D(ICoords2D vector) {
        PreCon.notNull(vector);

        return Math.sqrt(getDistanceSquared2D(vector));
    }

    @Override
    public double getDistanceSquared2D(ICoords2D vector) {
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

    protected void onChange() {
        resetCaches();
    }

    protected void copyCaches(Vector2D vector) {
        _magnitude = vector._magnitude;
        _direction = vector._direction;
        _yaw = vector._yaw;
        _hasMagnitude = vector._hasMagnitude;
        _hasDirection = vector._hasDirection;
        _hasYaw = vector._hasYaw;
    }

    private void resetCaches() {
        _hasDirection = false;
        _hasMagnitude = false;
        _hasYaw = false;
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

    private static float calculateYaw(ICoords2D vector) {
        float yaw = (float)Math.toDegrees(FastMath.atan2(vector.getX(), vector.getZ()));
        return Float.compare(yaw, 0.0f) == 0 ? yaw : -yaw;
    }
}
