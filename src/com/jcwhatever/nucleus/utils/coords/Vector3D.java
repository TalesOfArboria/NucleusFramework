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
import com.jcwhatever.nucleus.utils.nms.INmsEntityHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Implementation of {@link IVector3D}.
 */
public class Vector3D extends Vector2D implements IVector3D {

    private static final Vector ENTITY_VELOCITY = new Vector();

    protected double _y;
    protected transient double _magnitude3D;
    protected transient boolean _hasMagnitude3D;
    private transient AsVector _asVector;

    /**
     * Constructor.
     *
     * <p>Initialize all coordinates to 0.</p>
     */
    public Vector3D() {}

    /**
     * Constructor.
     *
     * @param x  The initial X value.
     * @param y  The initial Y value.
     * @param z  The initial Z value.
     */
    public Vector3D(double x, double y, double z) {
        super(x, z);
        _y = y;
    }

    /**
     * Constructor.
     *
     * @param coords  Instance to copy coordinate values from.
     */
    public Vector3D(ICoords3D coords) {
        super(coords);
        _y = coords.getY();
    }

    /**
     * Constructor.
     *
     * @param coords  Instance to copy coordinate values from.
     */
    public Vector3D(ICoords2D coords) {
        super(coords);
    }

    /**
     * Constructor.
     *
     * @param vector  The Bukkit vector to copy coordinate values from.
     */
    public Vector3D(Vector vector) {
        super(vector);
        _y = vector.getY();
    }

    /**
     * Constructor.
     *
     * @param location  The Bukkit location to copy coordinate values from.
     */
    public Vector3D(Location location) {
        super(location.getX(), location.getZ());
        _y = location.getY();
    }

    /**
     * Constructor.
     *
     * @param entity  The entity to copy velocity values from.
     */
    public Vector3D(Entity entity) {
        copyFrom3D(entity);
    }

    @Override
    public double getY() {
        return _y;
    }

    @Override
    public int getFloorY() {
        return (int)Math.floor(_y);
    }

    @Override
    public Vector3D setY(double y) {
        _y = y;
        return this;
    }

    @Override
    public Vector3D copyFrom3D(Entity entity) {
        PreCon.notNull(entity);

        INmsEntityHandler handler = NmsUtils.getEntityHandler();
        if (handler != null) {
            handler.getVelocity(entity, this);
        }
        else {
            copyFrom3D(entity.getVelocity());
        }
        return this;
    }

    @Override
    public Vector3D copyFrom3D(ICoords2D coords) {
        PreCon.notNull(coords);

        _y = coords instanceof ICoords3D
                ? ((ICoords3D) coords).getY()
                : 0;
        super.copyFrom2D(coords);
        return this;
    }

    @Override
    public Vector3D copyFrom3D(Vector vector) {
        PreCon.notNull(vector);

        _y = vector.getY();
        super.copyFrom2D(vector);
        return this;
    }

    @Override
    public Vector3D copyFrom3D(Location location) {
        PreCon.notNull(location);

        _y = location.getY();
        super.copyFrom2D(location);
        return this;
    }

    @Override
    public Vector3D copyTo3D(Entity entity) {
        PreCon.notNull(entity);

        copyTo3D(ENTITY_VELOCITY);
        entity.setVelocity(ENTITY_VELOCITY);
        return this;
    }

    @Override
    public Vector3D copyTo3D(Vector vector) {
        PreCon.notNull(vector);

        vector.setX(_x);
        vector.setY(_y);
        vector.setZ(_z);
        return this;
    }

    @Override
    public Vector3D copyTo3D(Location location) {
        PreCon.notNull(location);

        location.setX(_x);
        location.setY(_y);
        location.setZ(_z);
        return this;
    }

    @Override
    public Vector3D copyFrom2D(ICoords2D coords) {
        super.copyFrom2D(coords);
        return this;
    }

    @Override
    public Vector3D copyFrom2D(Vector vector) {
        super.copyFrom2D(vector);
        return this;
    }

    @Override
    public Vector3D copyFrom2D(Location location) {
        super.copyFrom2D(location);
        return this;
    }

    @Override
    public Vector3D copyTo2D(Vector vector) {
        super.copyTo2D(vector);
        return this;
    }

    @Override
    public Vector2D copyTo2D(Location location) {
        super.copyTo2D(location);
        return this;
    }

    @Override
    public Vector3D add3D(ICoords2D vector) {
        PreCon.notNull(vector);

        if (vector instanceof ICoords3D) {
            _y += ((ICoords3D) vector).getY();
        }
        add2D(vector);
        return this;
    }

    @Override
    public Vector3D add3D(Location vector) {
        PreCon.notNull(vector);

        _x += vector.getX();
        _y += vector.getY();
        _z += vector.getZ();
        onChange();
        return this;
    }

    @Override
    public Vector3D add3D(double scalar) {
        _y += scalar;
        super.add2D(scalar);
        return this;
    }

    @Override
    public Vector3D subtract3D(ICoords2D vector) {
        PreCon.notNull(vector);

        if (vector instanceof ICoords3D) {
            _y -= ((ICoords3D) vector).getY();
        }
        super.subtract2D(vector);
        return this;
    }

    @Override
    public Vector3D subtract3D(Location vector) {
        PreCon.notNull(vector);

        _x -= vector.getX();
        _y -= vector.getY();
        _z -= vector.getZ();
        onChange();
        return this;
    }

    @Override
    public Vector3D subtract3D(double scalar) {
        _y -= scalar;
        super.subtract2D(scalar);
        return this;
    }

    @Override
    public Vector3D multiply3D(ICoords2D vector) {
        PreCon.notNull(vector);

        if (vector instanceof ICoords3D) {
            _y *= ((ICoords3D) vector).getY();
        }
        else {
            _y = 0;
        }
        _x *= vector.getX();
        _z *= vector.getZ();
        onChange();
        return this;
    }

    @Override
    public Vector3D multiply3D(Location vector) {
        PreCon.notNull(vector);

        _x *= vector.getX();
        _y *= vector.getY();
        _z *= vector.getZ();
        onChange();
        return this;
    }

    @Override
    public Vector3D multiply3D(double scalar) {
        _y *= scalar;
        super.multiply2D(scalar);
        return this;
    }

    @Override
    public Vector3D average3D(ICoords2D vector) {
        PreCon.notNull(vector);

        double otherY = vector instanceof ICoords3D
                ? ((ICoords3D) vector).getY()
                : 0;

        _x = (_x + vector.getX()) * 0.5D;
        _y = (_y + otherY) * 0.5D;
        _z = (_z + vector.getZ()) * 0.5D;
        return null;
    }

    @Override
    public Vector3D reverse3D() {
        _x *= -1;
        _y *= -1;
        _z *= -1;
        return this;
    }

    @Override
    public Vector3D cross(ICoords3D vector) {
        PreCon.notNull(vector);

        _x = _y * vector.getZ() - vector.getY() * _z;
        _y =  _z * vector.getX() - vector.getZ() * _x;
        _z = _x * vector.getY() - vector.getX() * _y;
        return this;
    }

    @Override
    public Vector3D add2D(ICoords2D vector) {
        super.add2D(vector);
        return this;
    }

    @Override
    public Vector3D add2D(double value) {
        super.add2D(value);
        return this;
    }

    @Override
    public Vector3D subtract2D(ICoords2D vector) {
        super.subtract2D(vector);
        return this;
    }

    @Override
    public Vector3D subtract2D(double scalar) {
        super.subtract2D(scalar);
        return this;
    }

    @Override
    public Vector3D multiply2D(ICoords2D vector) {
        super.multiply2D(vector);
        return this;
    }

    @Override
    public Vector3D multiply2D(double scalar) {
        super.multiply2D(scalar);
        return this;
    }

    @Override
    public Vector3D average2D(ICoords2D vector) {
        super.average2D(vector);
        return this;
    }

    @Override
    public Vector3D reverse2D() {
        super.reverse2D();
        return this;
    }

    @Override
    public Vector3D normalize() {
        double magnitude = getMagnitude3D();
        if (magnitude < 0.0D || magnitude > 0.0D) {
            _x /= magnitude;
            _z /= magnitude;
            _y /= magnitude;
            onChange();
            _magnitude3D = 1;
            _hasMagnitude3D = true;
            _magnitude2D = 1;
            _hasMagnitude2D = true;
        }
        return this;
    }

    @Override
    public Vector3D reset() {
        _x = _y = _z = 0;
        resetCaches();
        _magnitude3D = 0;
        _hasMagnitude3D = true;
        _magnitude2D = 0;
        _hasMagnitude2D = true;
        return this;
    }

    @Override
    public Vector3D abs() {
        _y = Math.abs(_y);
        super.abs();
        return this;
    }

    @Override
    public double getMagnitude3D() {
        if (_hasMagnitude3D)
            return _magnitude3D;

        _hasMagnitude3D = true;
        return _magnitude3D = Math.sqrt(_x * _x + _y * _y + _z * _z);
    }

    @Override
    public double getMagnitudeSquared3D() {
        return _x * _x + _y * _y + _z * _z;
    }

    @Override
    public double getDot3D(ICoords3D vector) {
        PreCon.notNull(vector);

        return _x * vector.getX() + _y * vector.getY() + _z + vector.getZ();
    }

    @Override
    public double getDistance3D(ICoords2D vector) {
        PreCon.notNull(vector);

        return FastMath.sqrt(getDistanceSquared3D(vector));
    }

    @Override
    public double getDistance3D(Location vector) {
        PreCon.notNull(vector);

        return FastMath.sqrt(getDistanceSquared3D(vector));
    }

    @Override
    public double getDistanceSquared3D(ICoords2D vector) {
        PreCon.notNull(vector);

        double deltaY = _y - (vector instanceof ICoords3D ? ((ICoords3D) vector).getY() : 0);

        double deltaX = _x - vector.getX();
        double deltaZ = _z - vector.getZ();
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    @Override
    public double getDistanceSquared3D(Location vector) {
        PreCon.notNull(vector);

        double deltaX = _x - vector.getX();
        double deltaY = _y - vector.getY();
        double deltaZ = _z - vector.getZ();
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    @Override
    public Vector asBukkitVector() {
        if (_asVector == null)
            _asVector = new AsVector(_x, _y, _z);

        return _asVector;
    }

    @Override
    public Vector getBukkitVector() {
        Vector result = super.getBukkitVector();
        result.setY(_y);
        return result;
    }

    @Override
    public Vector getBukkitVector(Vector output) {
        PreCon.notNull(output);

        super.getBukkitVector(output);
        output.setY(_y);
        return output;
    }

    @Override
    protected void onChange() {
        super.onChange();

        if (_asVector != null) {
            _asVector.update();
        }
    }

    @Override
    protected void copyCaches(Vector2D vector) {
        super.copyCaches(vector);

        if (vector instanceof Vector3D) {
            _magnitude3D = ((Vector3D) vector)._magnitude3D;
            _hasMagnitude3D = ((Vector3D) vector)._hasMagnitude3D;
        }
    }

    @Override
    protected void resetCaches() {
        super.resetCaches();
        _hasMagnitude3D = false;
    }

    class AsVector extends Vector {

        AsVector(double x, double y, double z) {
            super(x, y, z);
        }

        @Override
        public Vector add(Vector vec) {
            this.x = _x += vec.getX();
            this.y = _y += vec.getY();
            this.z = _z += vec.getZ();
            resetCaches();
            return this;
        }

        @Override
        public Vector subtract(Vector vec) {
            this.x = _x -= vec.getX();
            this.y = _y -= vec.getY();
            this.z = _z -= vec.getZ();
            resetCaches();
            return this;
        }

        @Override
        public Vector multiply(Vector vec) {
            this.x = _x *= vec.getX();
            this.y = _y *= vec.getY();
            this.z = _z *= vec.getZ();
            resetCaches();
            return this;
        }

        @Override
        public Vector divide(Vector vec) {
            this.x = _x /= vec.getX();
            this.y = _y /= vec.getY();
            this.z = _z /= vec.getZ();
            resetCaches();
            return this;
        }

        @Override
        public Vector copy(Vector vec) {
            this.x = _x = vec.getX();
            this.y = _y = vec.getY();
            this.z = _z = vec.getZ();
            resetCaches();
            return this;
        }

        @Override
        public double length() {
            return getMagnitude3D();
        }

        @Override
        public double distance(Vector o) {
            return FastMath.sqrt(distanceSquared(o));
        }

        @Override
        public double distanceSquared(Vector o) {
            double deltaX = _x - o.getX();
            double deltaY = _y - o.getY();
            double deltaZ = _z - o.getZ();
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        }

        @Override
        public Vector midpoint(Vector other) {
            this.x = _x = (_x + other.getX()) * 0.5D;
            this.y = _y = (_y + other.getY()) * 0.5D;
            this.z = _z = (_z + other.getZ()) * 0.5D;
            resetCaches();
            return this;
        }

        @Override
        public Vector multiply(int m) {
            Vector3D.this.multiply3D(m);
            resetCaches();
            return this;
        }

        @Override
        public Vector multiply(double m) {
            Vector3D.this.multiply3D(m);
            resetCaches();
            return this;
        }

        @Override
        public Vector multiply(float m) {
            Vector3D.this.multiply3D(m);
            return this;
        }

        @Override
        public double dot(Vector other) {
            return _x * other.getX() + _y * other.getY() + _z * other.getZ();
        }

        @Override
        public Vector crossProduct(Vector o) {
            this.x = _x = _y * o.getZ() - o.getY() * _z;
            this.x = _y = _z * o.getX() - o.getZ() * _x;
            this.x = _z = _x * o.getY() - o.getX() * _y;
            resetCaches();
            return this;
        }

        @Override
        public Vector normalize() {
            Vector3D.this.normalize();

            return this;
        }

        @Override
        public Vector zero() {
            this.x = _x = 0.0D;
            this.y = _y = 0.0D;
            this.z = _z = 0.0D;
            resetCaches();
            return this;
        }

        @Override
        public double getX() {
            return _x;
        }

        @Override
        public int getBlockX() {
            return Vector3D.this.getFloorX();
        }

        @Override
        public double getY() {
            return _y;
        }

        @Override
        public int getBlockY() {
            return Vector3D.this.getFloorY();
        }

        @Override
        public double getZ() {
            return _z;
        }

        @Override
        public int getBlockZ() {
            return Vector3D.this.getFloorZ();
        }

        @Override
        public Vector setX(int x) {
            this.x = _x = x;
            resetCaches();
            return this;
        }

        @Override
        public Vector setX(double x) {
            this.x = _x = x;
            resetCaches();
            return this;
        }

        @Override
        public Vector setX(float x) {
            this.x = _x = x;
            resetCaches();
            return this;
        }

        @Override
        public Vector setY(int y) {
            this.y = _y = y;
            resetCaches();
            return this;
        }

        @Override
        public Vector setY(double y) {
            this.y = _y = y;
            resetCaches();
            return this;
        }

        @Override
        public Vector setY(float y) {
            this.y = _y = y;
            resetCaches();
            return this;
        }

        @Override
        public Vector setZ(int z) {
            this.z = _z = z;
            resetCaches();
            return this;
        }

        @Override
        public Vector setZ(double z) {
            this.z = _z = z;
            resetCaches();
            return this;
        }

        @Override
        public Vector setZ(float z) {
            this.z = _z = z;
            resetCaches();
            return this;
        }

        @Override
        public Vector clone() {
            return new Vector(_x, _y, _z);
        }

        void update() {
            this.x = _x;
            this.y = _y;
            this.z = _z;
        }
    }
}
