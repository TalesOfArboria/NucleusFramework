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
import com.jcwhatever.nucleus.utils.coords.Vector3D;

import java.math.BigDecimal;

/**
 * Nucleus implementation of {@link IRotationMatrix}.
 */
public class NucleusRotationMatrix implements IRotationMatrix {

    private static NucleusRotationMatrix[] MATRICES = new NucleusRotationMatrix[361];

    public static NucleusRotationMatrix get(float rotation) {

        while (rotation > 180f)
            rotation -= 360f;
        while (rotation < -180f)
            rotation += 360f;

        int index = (int)rotation + 180;

        NucleusRotationMatrix matrix = MATRICES[index];
        if (matrix == null) {
            matrix = new NucleusRotationMatrix((int)rotation, 4);
            MATRICES[index] = matrix;
        }
        return matrix;
    }

    private final float _rotation;
    private final int _scale;
    private float[][] _xMatrix;
    private float[][] _yMatrix;
    private float[][] _zMatrix;
    private float[][] _xReverseMatrix;
    private float[][] _yReverseMatrix;
    private float[][] _zReverseMatrix;
    private boolean _isCalculated;

    NucleusRotationMatrix(float rotation, int scale) {
        _rotation = rotation;
        _scale = scale;
    }

    NucleusRotationMatrix(float rotation) {
        this(rotation, 10);
    }

    @Override
    public float getRotation() {
        return _rotation;
    }

    @Override
    public IVector3D rotateX(IVector2D vector) {
        return rotateX(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateX(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        calculate();
        return rotate(vector, _xMatrix, output);
    }

    @Override
    public IVector3D rotateY(IVector2D vector) {
        return rotateY(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateY(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        calculate();
        return rotate(vector, _yMatrix, output);
    }

    @Override
    public IVector3D rotateZ(IVector2D vector) {
        return rotateZ(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateZ(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        calculate();
        return rotate(vector, _zMatrix, output);
    }

    @Override
    public IVector3D rotateReverseX(IVector2D vector) {
        return rotateReverseX(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateReverseX(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        calculate();
        return rotate(vector, _xReverseMatrix, output);
    }

    @Override
    public IVector3D rotateReverseY(IVector2D vector) {
        return rotateReverseY(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateReverseY(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        calculate();
        return rotate(vector, _yReverseMatrix, output);
    }

    @Override
    public IVector3D rotateReverseZ(IVector2D vector) {
        return rotateReverseZ(vector, new Vector3D());
    }

    @Override
    public <T extends IVector2D> T rotateReverseZ(IVector2D vector, T output) {
        PreCon.notNull(vector);
        PreCon.notNull(output);

        calculate();
        return rotate(vector, _zReverseMatrix, output);
    }

    private void calculate() {
        if (_isCalculated)
            return;

        _isCalculated = true;

        float cos = scale(FastMath.cos(Math.toRadians(_rotation)), _scale);
        float sin = scale(FastMath.sin(Math.toRadians(_rotation)), _scale);

        _xMatrix = new float[][] {
                { 1,    0,   0 },
                { 0,  cos, sin },
                { 0, -sin, cos }
        };

        _yMatrix = new float[][] {
                { cos, 0, -sin },
                {   0, 1,    0 },
                { sin, 0,  cos }
        };

        _zMatrix = new float[][] {
                {  cos, sin, 0 },
                { -sin, cos, 0 },
                {    0,   0, 1 },
        };

        _xReverseMatrix = new float[][] {
                { 1,    0,     0 },
                { 0,  cos,  -sin },
                { 0,  sin,   cos }
        };

        _yReverseMatrix = new float[][] {
                {  cos, 0, sin },
                {    0, 1,   0 },
                { -sin, 0, cos }
        };

        _zReverseMatrix = new float[][] {
                { cos, -sin, 0 },
                { sin,  cos, 0 },
                {   0,    0, 1 }
        };
    }

    private <T extends IVector2D> T rotate(IVector2D vector, float[][] matrix, T output) {

        if (output instanceof IVector3D) {
            ((IVector3D)output).copyFrom3D(vector);
        }
        else {
            output.copyFrom2D(vector);
        }

        applyMatrix(output, matrix);
        return output;
    }

    private static void applyMatrix(IVector2D vector, float[][] matrix) {

        if (vector instanceof IVector3D) {
            applyMatrix((IVector3D)vector, matrix);
            return;
        }

        double x = vector.getX();
        double z = vector.getZ();

        double newX = x * matrix[0][0] + z * matrix[2][0];
        double newZ = x * matrix[0][2] + z * matrix[2][2];

        vector.setX(newX);
        vector.setZ(newZ);
    }

    private static void applyMatrix(IVector3D vector, float[][] matrix) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        double newX = x * matrix[0][0] + y * matrix[1][0] + z * matrix[2][0];
        double newY = x * matrix[0][1] + y * matrix[1][1] + z * matrix[2][1];
        double newZ = x * matrix[0][2] + y * matrix[1][2] + z * matrix[2][2];

        vector.setX(newX);
        vector.setY(newY);
        vector.setZ(newZ);
    }

    private static float scale(float value, int scale) {
        return new BigDecimal(value)
                .setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
