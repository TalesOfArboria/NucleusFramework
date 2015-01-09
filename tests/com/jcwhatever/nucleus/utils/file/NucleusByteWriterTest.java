package com.jcwhatever.nucleus.utils.file;

import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class NucleusByteWriterTest {

    @Test
    public void testGetBytesWritten() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write((byte)1);

        Assert.assertEquals(1, writer.getBytesWritten());

        writer.write((short)1);

        Assert.assertEquals(3, writer.getBytesWritten());

        writer.write(1);

        Assert.assertEquals(7, writer.getBytesWritten());

        writer.write(1L);

        Assert.assertEquals(15, writer.getBytesWritten());

        writer.write(new byte[] { 1, 2, 3, 4, 5});

        Assert.assertEquals(24, writer.getBytesWritten());

        writer.close();
    }

    @Test
    public void testWriteBoolean() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(true);
        writer.write(false);
        writer.write(true);
        writer.write(true);
        writer.write(true);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(new byte[] { 29 }, bytes);

        writer.close();
    }

    @Test
    public void testWriteByte() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write((byte)5);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(new byte[] { 5 }, bytes);

        writer.close();
    }

    @Test
    public void testWriteBytes() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(new byte[] { 1, 2, 3, 4, 5});
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(new byte[] { 0, 0, 0, 5, 1, 2, 3, 4, 5}, bytes);

        writer.close();
    }

    @Test
    public void testWriteShort() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write((short)5);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(new byte[] { 0, 5 }, bytes);

        writer.close();
    }

    @Test
    public void testWriteInt() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(5);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(new byte[] { 0, 0, 0, 5 }, bytes);

        writer.close();
    }

    @Test
    public void testWriteLong() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(5L);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 5 }, bytes);

        writer.close();
    }

    @Test
    public void testWriteFloat() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(1.0F);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(getSmallStringBytes("1.0"), bytes);

        writer.close();
    }

    @Test
    public void testWriteDouble() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(1.0D);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(getSmallStringBytes("1.0"), bytes);

        writer.close();
    }

    @Test
    public void testWriteString() throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write("test");
        writer.flush();

        byte[] bytes = stream.toByteArray();

        byte[] stringBytes = "test".getBytes(StandardCharsets.UTF_16);

        byte[] expected = new byte[stringBytes.length + 2];

        ArrayUtils.copyFromEnd(stringBytes, expected);

        expected[1] = (byte)stringBytes.length; // string length

        Assert.assertArrayEquals(expected, bytes);

        writer.close();
    }

    @Test
    public void testWriteSmallString() throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.writeSmallString("test");
        writer.flush();

        byte[] bytes = stream.toByteArray();

        Assert.assertArrayEquals(getSmallStringBytes("test"), bytes);

        writer.close();
    }

    private byte[] getSmallStringBytes(String test) {
        byte[] stringBytes = test.getBytes(StandardCharsets.UTF_8);

        byte[] bytes = new byte[stringBytes.length + 1];

        ArrayUtils.copyFromEnd(stringBytes, bytes);

        bytes[0] = (byte)stringBytes.length; // string length

        return bytes;
    }
}