package com.jcwhatever.nucleus.utils.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Tests {@link BasicByteReader}.
 */
public class BasicByteReaderTest {

    private enum TestEnum {
        CONSTANT,
        DEFAULT
    }

    /**
     * Make sure {@link BasicByteReader#getBytesRead} returns the correct value.
     */
    @Test
    public void testGetBytesRead() throws Exception {

        BasicByteReader reader = getReader(new byte[] { 0, 0, 0});

        reader.getByte();
        reader.getByte();
        reader.getByte();

        assertEquals(3, reader.getBytesRead());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#read} returns the correct value.
     */
    @Test
    public void testRead() throws Exception {

        BasicByteReader reader = getReader(new byte[] { 0, 1, 2});

        assertEquals(0, reader.getByte());
        assertEquals(1, reader.getByte());
        assertEquals(2, reader.getByte());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#skip} works correctly.
     */
    @Test
    public void testSkip() throws Exception {
        BasicByteReader reader = getReader(new byte[] { 0, 1, 2});

        assertEquals(0, reader.getByte());
        reader.skip(1);
        assertEquals(2, reader.getByte());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getBoolean} returns the correct value.
     */
    @Test
    public void testGetBoolean() throws Exception {

        BasicByteReader reader = getReader(new byte[] { 0xF, 0, 0xF });

        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());

        assertEquals(0, reader.getByte());

        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(false, reader.getBoolean());
        assertEquals(false, reader.getBoolean());
        assertEquals(false, reader.getBoolean());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getByte} returns the correct value.
     */
    @Test
    public void testGetByte() throws Exception {
        BasicByteReader reader = getReader(new byte[] { 0, 1, 2 });

        assertEquals(0, reader.getByte());
        assertEquals(1, reader.getByte());
        assertEquals(2, reader.getByte());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getBytes} returns the correct value.
     */
    @Test
    public void testGetBytes() throws Exception {
        BasicByteReader reader = getReader(new byte[] { 0, 0, 0, 1, 5 });

        assertArrayEquals(new byte[]{5}, reader.getBytes());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getShort} returns the correct value.
     */
    @Test
    public void testGetShort() throws Exception {
        BasicByteReader reader = getReader(new byte[] { 0, 5 });

        assertEquals(5, reader.getShort());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getInteger} returns the correct value.
     */
    @Test
    public void testGetInteger() throws Exception {
        BasicByteReader reader = getReader(new byte[] { 0, 0, 0, 5 });

        assertEquals(5, reader.getInteger());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getLong} returns the correct value.
     */
    @Test
    public void testGetLong() throws Exception {
        BasicByteReader reader = getReader(new byte[] { 0, 0, 0, 0, 0, 0, 0, 5 });

        assertEquals(5, reader.getLong());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getString} returns the correct value.
     */
    @Test
    public void testGetString() throws Exception {

        String test = "test";

        byte[] stringBytes = test.getBytes(StandardCharsets.UTF_16);

        byte[] bytes = new byte[stringBytes.length + 2];

        ArrayUtils.copyFromEnd(stringBytes, bytes);

        bytes[1] = (byte)stringBytes.length; // string length

        BasicByteReader reader = getReader(bytes);

        assertEquals("test", reader.getString());

        reader.close();

    }

    /**
     * Make sure {@link BasicByteReader#getSmallString} returns the correct value.
     */
    @Test
    public void testGetSmallString() throws Exception {

        byte[] bytes  = getSmallStringBytes("test");

        BasicByteReader reader = getReader(bytes);

        assertEquals("test", reader.getSmallString());

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getFloat} returns the correct value.
     */
    @Test
    public void testGetFloat() throws Exception {

        byte[] bytes  = getSmallStringBytes("0.1");

        BasicByteReader reader = getReader(bytes);

        assertEquals(0.1F, reader.getFloat(), 0.0F);

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getDouble} returns the correct value.
     */
    @Test
    public void testGetDouble() throws Exception {

        byte[] bytes  = getSmallStringBytes("0.1");

        BasicByteReader reader = getReader(bytes);

        assertEquals(0.1D, reader.getDouble(), 0.0D);

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getEnum} returns the correct value.
     */
    @Test
    public void testGetEnum() throws Exception {
        byte[] bytes  = getSmallStringBytes(TestEnum.CONSTANT.name());

        BasicByteReader reader = getReader(bytes);

        assertEquals(TestEnum.CONSTANT, reader.getEnum(TestEnum.class));

        reader.close();
    }

    /**
     * Make sure {@link BasicByteReader#getUUID} returns the correct value.
     */
    @Test
    public void testGetUUID() throws Exception {

        UUID uuid = UUID.randomUUID();

        byte[] most = getLongBytes(uuid.getMostSignificantBits());
        byte[] least = getLongBytes(uuid.getLeastSignificantBits());

        byte[] temp = new byte[16];

        ArrayUtils.copyFromStart(most, temp);
        ArrayUtils.copyFromEnd(least, temp);

        byte[] bytes = new byte[17];
        bytes[0] = 1;

        System.arraycopy(temp, 0, bytes, 1, bytes.length - 1);

        BasicByteReader reader = getReader(bytes);

        assertEquals(uuid, reader.getUUID());

        reader.close();
    }

    private BasicByteReader getReader(byte[] array) {

        InputStream stream = new ByteArrayInputStream(array);

        return new BasicByteReader(stream);
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