package com.jcwhatever.nucleus.internal.providers.math;

import com.jcwhatever.nucleus.utils.coords.Vector3D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NucleusRotationMatrixTest {

    @Test
    public void testRotate3dY() throws Exception {
        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 0, 0);
        matrix.rotateY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateY(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);
    }

    @Test
    public void testRotateReverse3dY() throws Exception {

        NucleusRotationMatrix matrix = new NucleusRotationMatrix(90);
        Vector3D vector = new Vector3D(1, 0, 0);
        matrix.rotateReverseY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(-90);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateReverseY(vector, vector);

        assertEquals(0, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(-1, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(0);
        vector = new Vector3D(1, 0, 0);
        matrix.rotateReverseY(vector, vector);

        assertEquals(1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);

        matrix = new NucleusRotationMatrix(90);
        vector = new Vector3D(0, 0, 1);
        matrix.rotateReverseY(vector, vector);

        assertEquals(-1, vector.getX(), 0.0001D);
        assertEquals(0, vector.getY(), 0.0001D);
        assertEquals(0, vector.getZ(), 0.0001D);
    }
}