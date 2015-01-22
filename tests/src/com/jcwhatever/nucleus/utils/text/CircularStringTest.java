package com.jcwhatever.nucleus.utils.text;

import org.junit.Assert;
import org.junit.Test;

public class CircularStringTest {

    private String string = "12345";


    @Test
    public void testSetString() throws Exception {

        CircularString circ = new CircularString();

        circ.setString(string);

        Assert.assertEquals(string, circ.toString());
    }

    @Test
    public void testGetChars() throws Exception {

        CircularString circ = new CircularString(string);

        char[] chars = circ.getChars();

        Assert.assertEquals(string, String.copyValueOf(chars));
    }

    @Test
    public void testReset() throws Exception {
        CircularString circ = new CircularString(string);

        circ.rotateLeft(1);
        circ.reset();

        char[] chars = circ.getChars();

        Assert.assertEquals(string, String.copyValueOf(chars));

        Assert.assertEquals(string, circ.toString());
    }

    @Test
    public void testGetRotation() throws Exception {

        CircularString circ = new CircularString(string);

        circ.rotateLeft(1);

        Assert.assertEquals(1, circ.getRotation());

        circ.rotateRight(2);

        Assert.assertEquals(string.length()-1, circ.getRotation());
    }

    @Test
    public void testRotateLeft() throws Exception {

        CircularString circ = new CircularString(string);

        circ.rotateLeft(1);

        Assert.assertEquals(1, circ.getRotation());
        Assert.assertEquals("23451", circ.toString());

        circ.rotateLeft(2);

        Assert.assertEquals(3, circ.getRotation());
        Assert.assertEquals("45123", circ.toString());

        circ.rotateLeft(-3);
        Assert.assertEquals(0, circ.getRotation());
        Assert.assertEquals("12345", circ.toString());

        circ.rotateLeft(string.length() * 100 + 1);
        Assert.assertEquals(1, circ.getRotation());
        Assert.assertEquals("23451", circ.toString());
    }

    @Test
    public void testRotateRight() throws Exception {
        CircularString circ = new CircularString(string);

        circ.rotateRight(1);

        Assert.assertEquals(string.length() - 1, circ.getRotation());
        Assert.assertEquals("51234", circ.toString());

        circ.rotateRight(2);

        Assert.assertEquals(string.length() - 3, circ.getRotation());
        Assert.assertEquals("34512", circ.toString());

        circ.rotateRight(-3);
        Assert.assertEquals(0, circ.getRotation());
        Assert.assertEquals("12345", circ.toString());

        circ.rotateRight(string.length() * 100 + 1);
        Assert.assertEquals(string.length() - 1, circ.getRotation());
        Assert.assertEquals("51234", circ.toString());
    }

    @Test
    public void testSetChar() throws Exception {
        CircularString circ = new CircularString(string);

        circ.setChar(0, 'A');
        Assert.assertEquals("A2345", circ.toString());

        circ.rotateLeft(1);

        circ.setChar(0, 'A');
        Assert.assertEquals("A345A", circ.toString());
    }

    @Test
    public void testLength() throws Exception {

        CircularString circ = new CircularString(string);

        Assert.assertEquals(string.length(), circ.length());

        circ.rotateLeft(1);

        Assert.assertEquals(string.length(), circ.length());

        circ.rotateRight(3);

        Assert.assertEquals(string.length(), circ.length());
    }

    @Test
    public void testCharAt() throws Exception {

        CircularString circ = new CircularString(string);

        Assert.assertEquals('1', circ.charAt(0));

        circ.rotateLeft(1);

        Assert.assertEquals('2', circ.charAt(0));
    }

    @Test
    public void testSubSequence() throws Exception {

        CircularString circ = new CircularString(string);

        CircularString circ1 = circ.subSequence(0, 1);

        Assert.assertEquals("1", circ1.toString());

        circ.rotateLeft(1);

        Assert.assertEquals("1", circ1.toString());

        CircularString circ2 = circ.subSequence(0, 1);

        Assert.assertEquals("2", circ2.toString());
    }
}