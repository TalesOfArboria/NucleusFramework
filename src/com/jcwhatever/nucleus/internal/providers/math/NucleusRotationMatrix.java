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

package com.jcwhatever.nucleus.internal.providers.math;

import com.jcwhatever.nucleus.providers.math.FastMath;
import com.jcwhatever.nucleus.providers.math.IRotationMatrix;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.IVector2D;
import com.jcwhatever.nucleus.utils.coords.IVector3D;
import com.jcwhatever.nucleus.utils.coords.Vector2D;
import com.jcwhatever.nucleus.utils.coords.Vector3D;

import java.math.BigDecimal;

/**
 * Nucleus implementation of {@link IRotationMatrix}.
 */
public class NucleusRotationMatrix implements IRotationMatrix {

    private static final int ANGLE_PRECISION = 64;
    private static final int ANGLE_MODULUS = 361 * ANGLE_PRECISION;
    private static NucleusRotationMatrix[] MATRICES = new NucleusRotationMatrix[ANGLE_MODULUS];

    static {
        for (int i=0; i < MATRICES.length; i++) {
            MATRICES[i] = new NucleusRotationMatrix((float)(i / ANGLE_PRECISION) - 180f);
        }
    }

    public static NucleusRotationMatrix get(float rotation) {

        while (rotation > 180f)
            rotation -= 360f;
        while (rotation < -180f)
            rotation += 360f;

        int index = (int)((rotation + 180) * ANGLE_PRECISION + 0.5f);
        return MATRICES[index];
    }

    private final float _rotation;
    private final float _cos;
    private final float _sin;

    NucleusRotationMatrix(float rotation) {
        _rotation = rotation;
        _cos = FastMath.cos(_rotation);
        _sin = FastMath.sin(_rotation);
        /*
        newX = x * matrix[0][0] + y * matrix[1][0] + z * matrix[2][0];
        newY = x * matrix[0][1] + y * matrix[1][1] + z * matrix[2][1];
        newZ = x * matrix[0][2] + y * matrix[1][2] + z * matrix[2][2];
        */
    }

    @Override
    public float getRotation() {
        return _rotation;
    }

    @Override
    public IVector2D rotateX(IVector2D vector) {
        return rotateX(vector, new Vector2D());
    }

    @Override
    public IVector3D rotateX(IVector3D vector) {
        return rotateX(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateX(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double z = vector.getZ();

        output.setX(x);
        output.setZ(z * _cos);

        return output;
    }

    @Override
    public <T extends IVector3D> T rotateX(IVector3D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        output.setX(x);
        output.setY(y * _cos + z * -_sin);
        output.setZ(y * _sin + z * _cos);

        return output;
    }

    @Override
    public IVector2D rotateY(IVector2D vector) {
        return rotateY(vector, new Vector2D());
    }

    @Override
    public IVector3D rotateY(IVector3D vector) {
        return rotateY(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateY(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double z = vector.getZ();

        // using reverse Matrix instead of forward Matrix due to Minecraft's inverted X axis coordinates
        // relative to Yaw 0.
        output.setX(x * _cos + z * -_sin);
        output.setZ(x * _sin + z * _cos);

        return output;
    }

    @Override
    public <T extends IVector3D> T rotateY(IVector3D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        // using reverse Matrix instead of forward Matrix due to Minecraft's inverted X axis coordinates
        // relative to Yaw 0.
        output.setX(x * _cos + z * -_sin);
        output.setY(y);
        output.setZ(x * _sin + z * _cos);

        return output;
    }

    @Override
    public IVector2D rotateZ(IVector2D vector) {
        return rotateZ(vector, new Vector2D());
    }

    @Override
    public IVector3D rotateZ(IVector3D vector) {
        return rotateZ(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateZ(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double z = vector.getZ();

        output.setX(x * _cos);
        output.setZ(z);

        return output;
    }

    @Override
    public <T extends IVector3D> T rotateZ(IVector3D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        output.setX(x * _cos + y * -_sin);
        output.setY(x * _sin + y * _cos);
        output.setZ(z);

        return output;
    }

    @Override
    public IVector2D rotateReverseX(IVector2D vector) {
        return rotateReverseX(vector, new Vector2D());
    }

    @Override
    public IVector3D rotateReverseX(IVector3D vector) {
        return rotateReverseX(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateReverseX(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double z = vector.getZ();

        output.setX(x);
        output.setZ(z * _cos);

        return output;
    }

    @Override
    public <T extends IVector3D> T rotateReverseX(IVector3D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        output.setX(x);
        output.setY(y * _cos + z * _sin);
        output.setZ(y * -_sin + z * _cos);

        return output;
    }

    @Override
    public IVector2D rotateReverseY(IVector2D vector) {
        return rotateReverseY(vector, new Vector2D());
    }

    @Override
    public IVector3D rotateReverseY(IVector3D vector) {
        return rotateReverseY(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateReverseY(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double z = vector.getZ();

        // using forward Matrix instead of Reverse Matrix due to Minecraft's inverted X axis coordinates
        // relative to Yaw 0.
        output.setX(x * _cos + z * _sin);
        output.setZ(x * -_sin + z * _cos);

        return output;
    }

    @Override
    public <T extends IVector3D> T rotateReverseY(IVector3D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        // using forward Matrix instead of Reverse Matrix due to Minecraft's inverted X axis coordinates
        // relative to Yaw 0.
        output.setX(x * _cos + z * _sin);
        output.setY(y);
        output.setZ(x * -_sin + z * _cos);

        return output;
    }

    @Override
    public IVector2D rotateReverseZ(IVector2D vector) {
        return rotateReverseZ(vector, new Vector2D());
    }

    @Override
    public IVector3D rotateReverseZ(IVector3D vector) {
        return rotateReverseZ(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateReverseZ(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double z = vector.getZ();

        output.setX(x * _cos);
        output.setZ(z);

        return output;
    }

    @Override
    public <T extends IVector3D> T rotateReverseZ(IVector3D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        output.setX(x * _cos + y * _sin);
        output.setY(x * -_sin + y * _cos);
        output.setZ(z);

        return output;
    }

    private static float scale(float value, int scale) {
        return new BigDecimal(value)
                .setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
