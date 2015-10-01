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

package com.jcwhatever.nucleus.providers.math;

import com.jcwhatever.nucleus.utils.coords.IVector2D;
import com.jcwhatever.nucleus.utils.coords.IVector3D;

/**
 * A rotation matrix used to rotate vectors.
 */
public interface IRotationMatrix {

    /**
     * Get the angle of rotation.
     */
    float getRotation();

    /**
     * Rotate the vector on the X axis by the rotation angle.
     *
     * @param vector  The input vector.
     *
     * @return  A new rotated vector.
     */
    IVector3D rotateX(IVector2D vector);

    /**
     * Copy and rotate the vector on the X axis by the rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     * @param output  The rotated output vector.
     *
     * @return  The rotated output vector.
     */
    <T extends IVector2D> T rotateX(IVector2D vector, T output);

    /**
     * Rotate the vector on the Y axis by the rotation angle.
     *
     * @param vector  The input vector.
     *
     * @return  A new rotated vector.
     */
    IVector3D rotateY(IVector2D vector);

    /**
     * Copy and rotate the vector on the Y axis by the rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     * @param output  The rotated output vector.
     *
     * @return  The rotated output vector.
     */
    <T extends IVector2D> T rotateY(IVector2D vector, T output);

    /**
     * Rotate the vector on the Z axis by the rotation angle.
     *
     * @param vector  The input vector.
     *
     * @return  A new rotated vector.
     */
    IVector3D rotateZ(IVector2D vector);

    /**
     * Copy and rotate the vector on the Z axis by the matrix angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     * @param output  The rotated output vector.
     *
     * @return  The rotated output vector.
     */
    <T extends IVector2D> T rotateZ(IVector2D vector, T output);

    /**
     * Rotate the vector on the Y axis by the negated rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     *
     * @return  A new rotated vector.
     */
    IVector3D rotateReverseX(IVector2D vector);

    /**
     * Copy and rotate the vector on the X axis by the negated rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     * @param output  The rotated output vector.
     *
     * @return  The rotated output vector.
     */
    <T extends IVector2D> T rotateReverseX(IVector2D vector, T output);

    /**
     * Rotate the vector on the Y axis by the negated rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     *
     * @return  A new rotated vector.
     */
    IVector3D rotateReverseY(IVector2D vector);

    /**
     * Copy and rotate the vector on the Y axis by the negated rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     * @param output  The rotated output vector.
     *
     * @return  The rotated output vector.
     */
    <T extends IVector2D> T rotateReverseY(IVector2D vector, T output);

    /**
     * Rotate the vector on the Z axis by the negated rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     *
     * @return  A new rotated vector.
     */
    IVector3D rotateReverseZ(IVector2D vector);

    /**
     * Copy and rotate the vector on the Z axis by the negated rotation angle.
     *
     * @param vector  The input vector.
     *                If the vector is 2D, 0 is substituted for the Y coords.
     * @param output  The rotated output vector.
     *
     * @return  The rotated output vector.
     */
    <T extends IVector2D> T rotateReverseZ(IVector2D vector, T output);
}
