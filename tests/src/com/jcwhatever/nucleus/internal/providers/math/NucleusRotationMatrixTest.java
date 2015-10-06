package com.jcwhatever.nucleus.internal.providers.math;

import com.jcwhatever.nucleus.utils.coords.Vector3D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test Nucleus default fast math rotation matrix implementation.
 */
public class NucleusRotationMatrixTest {

    @Test
    public void testMatrixLookup() {

        NucleusRotationMatrix matrix = NucleusRotationMatrix.get(-180);
        assertEquals(-180f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(180);
        assertEquals(180f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(-90);
        assertEquals(-90f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(90);
        assertEquals(90f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(0);
        assertEquals(0f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(-45);
        assertEquals(-45f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(45);
        assertEquals(45f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(360);
        assertEquals(0f, matrix.getRotation(), 0.0f);

        matrix = NucleusRotationMatrix.get(-360);
        assertEquals(0f, matrix.getRotation(), 0.0f);
    }

    @Test
    public void testRotate3dX() throws Exception {
        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 1, 1);
        matrix.rotateX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateX(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateX(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateX(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);
    }

    @Test
    public void testRotateReverse3dX() throws Exception {
        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseX(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);
    }

    @Test
    public void testRotate3dY() throws Exception {
        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 0, 0);
        matrix.rotateY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateY(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateY(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);
    }

    @Test
    public void testRotateReverse3dY() throws Exception {

        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 0, 0);
        matrix.rotateReverseY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateReverseY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateReverseY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateReverseY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateReverseY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseY(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);
    }

    @Test
    public void testRotate3dZ() throws Exception {
        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 1, 1);
        matrix.rotateZ(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateZ(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateZ(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 1, 0);
        matrix.rotateZ(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(0, 1, 0);
        matrix.rotateZ(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(0, 1, 0);
        matrix.rotateZ(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateZ(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);
    }

    @Test
    public void testRotateReverse3dZ() throws Exception {
        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 1, 0);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(0, 1, 0);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(0, 1, 0);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(180);
        vector = new Vector3D(1, 1, 1);
        matrix.rotateReverseZ(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(-1, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);
    }
}