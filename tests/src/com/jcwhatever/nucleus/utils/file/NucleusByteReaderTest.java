package com.jcwhatever.nucleus.utils.file;

import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NucleusByteReaderTest {

    private enum TestEnum {
        CONSTANT,
        DEFAULT
    }

    @Test
    public void testGetBytesRead() throws Exception {

        NucleusByteReader reader = getReader(new byte[] { 0, 0, 0});

        reader.getByte();
        reader.getByte();
        reader.getByte();

        Assert.assertEquals(3, reader.getBytesRead());

        reader.close();
    }

    @Test
    public void testRead() throws Exception {

        NucleusByteReader reader = getReader(new byte[] { 0, 1, 2});

        Assert.assertEquals(0, reader.getByte());
        Assert.assertEquals(1, reader.getByte());
        Assert.assertEquals(2, reader.getByte());

        reader.close();
    }

    @Test
    public void testSkip() throws Exception {
        NucleusByteReader reader = getReader(new byte[] { 0, 1, 2});

        Assert.assertEquals(0, reader.getByte());
        reader.skip(1);
        Assert.assertEquals(2, reader.getByte());

        reader.close();
    }

    @Test
    public void testGetBoolean() throws Exception {

        NucleusByteReader reader = getReader(new byte[] { 0xF, 0, 0xF });

        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(true, reader.getBoolean());

        Assert.assertEquals(0, reader.getByte());

        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(true, reader.getBoolean());
        Assert.assertEquals(false, reader.getBoolean());
        Assert.assertEquals(false, reader.getBoolean());
        Assert.assertEquals(false, reader.getBoolean());

        reader.close();
    }

    @Test
    public void testGetByte() throws Exception {
        NucleusByteReader reader = getReader(new byte[] { 0, 1, 2 });

        Assert.assertEquals(0, reader.getByte());
        Assert.assertEquals(1, reader.getByte());
        Assert.assertEquals(2, reader.getByte());

        reader.close();
    }

    @Test
    public void testGetBytes() throws Exception {
        NucleusByteReader reader = getReader(new byte[] { 0, 0, 0, 1, 5 });

        Assert.assertArrayEquals(new byte[] { 5 }, reader.getBytes());

        reader.close();
    }

    @Test
    public void testGetShort() throws Exception {
        NucleusByteReader reader = getReader(new byte[] { 0, 5 });

        Assert.assertEquals(5, reader.getShort());

        reader.close();
    }

    @Test
    public void testGetInteger() throws Exception {
        NucleusByteReader reader = getReader(new byte[] { 0, 0, 0, 5 });

        Assert.assertEquals(5, reader.getInteger());

        reader.close();
    }

    @Test
    public void testGetLong() throws Exception {
        NucleusByteReader reader = getReader(new byte[] { 0, 0, 0, 0, 0, 0, 0, 5 });

        Assert.assertEquals(5, reader.getLong());

        reader.close();
    }

    @Test
    public void testGetString() throws Exception {

        String test = "test";

        byte[] stringBytes = test.getBytes(StandardCharsets.UTF_16);

        byte[] bytes = new byte[stringBytes.length + 2];

        ArrayUtils.copyFromEnd(stringBytes, bytes);

        bytes[1] = (byte)stringBytes.length; // string length

        NucleusByteReader reader = getReader(bytes);

        Assert.assertEquals("test", reader.getString());

        reader.close();

    }

    @Test
    public void testGetSmallString() throws Exception {

        byte[] bytes  = getSmallStringBytes("test");

        NucleusByteReader reader = getReader(bytes);

        Assert.assertEquals("test", reader.getSmallString());

        reader.close();
    }

    @Test
    public void testGetFloat() throws Exception {

        byte[] bytes  = getSmallStringBytes("0.1");

        NucleusByteReader reader = getReader(bytes);

        Assert.assertEquals(0.1F, reader.getFloat(), 0.0F);

        reader.close();
    }

    @Test
    public void testGetDouble() throws Exception {

        byte[] bytes  = getSmallStringBytes("0.1");

        NucleusByteReader reader = getReader(bytes);

        Assert.assertEquals(0.1D, reader.getDouble(), 0.0D);

        reader.close();
    }

    @Test
    public void testGetEnum() throws Exception {
        byte[] bytes  = getSmallStringBytes(TestEnum.CONSTANT.name());

        NucleusByteReader reader = getReader(bytes);

        Assert.assertEquals(TestEnum.CONSTANT, reader.getEnum(TestEnum.class));

        reader.close();
    }

    @Test
    public void testGetUUID() throws Exception {

        UUID uuid = UUID.randomUUID();

        byte[] most = getLongBytes(uuid.getMostSignificantBits());
        byte[] least = getLongBytes(uuid.getLeastSignificantBits());

        byte[] bytes = new byte[16];

        ArrayUtils.copyFromStart(most, bytes);
        ArrayUtils.copyFromEnd(least, bytes);

        NucleusByteReader reader = getReader(bytes);

        Assert.assertEquals(uuid, reader.getUUID());

        reader.close();
    }

    private NucleusByteReader getReader(byte[] array) {

        InputStream stream = new ByteArrayInputStream(array);

        return new NucleusByteReader(stream);
    }

    private byte[] getSmallStringBytes(String test) {
        byte[] stringBytes = test.getBytes(StandardCharsets.UTF_8);

        byte[] bytes = new byte[stringBytes.length + 1];

        ArrayUtils.copyFromEnd(stringBytes, bytes);

        bytes[0] = (byte)stringBytes.length; // string length

        return bytes;
    }

    public byte[] getLongBytes(long longValue) throws IOException {

        return new byte[]{

                (byte) (longValue >> 56 & 0xFF),
                (byte) (longValue >> 48 & 0xFF),
                (byte) (longValue >> 40 & 0xFF),
                (byte) (longValue >> 32 & 0xFF),
                (byte) (longValue >> 24 & 0xFF),
                (byte) (longValue >> 16 & 0xFF),
                (byte) (longValue >> 8 & 0xFF),
                (byte) (longValue & 0xFF)
        };
    }
}