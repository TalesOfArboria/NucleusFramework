package com.jcwhatever.nucleus.utils.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.regions.data.SyncLocation;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Tests {@link NucleusByteReader} and {@link NucleusByteWriter} inter-compatibility.
 */
public class NucleusByteReaderWriterTest {


    /**
     * Make sure the reader understands the writers booleans.
     */
    @Test
    public void testBoolean() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(true);
        writer.write(false);
        writer.write(true);
        writer.write(true);
        writer.write(true);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(true, reader.getBoolean());
        assertEquals(false, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());
        assertEquals(true, reader.getBoolean());

        input.close();
    }

    /**
     * Make sure the reader understands the writers byte.
     */
    @Test
    public void testByte() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write((byte)5);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(5, reader.getByte());

        input.close();
    }

    /**
     * Make sure the reader understands the writers bytes.
     */
    @Test
    public void testBytes() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(new byte[] { 1, 2, 3, 4, 5});
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, reader.getBytes());

        input.close();
    }

    /**
     * Make sure the reader understands the writers shorts.
     */
    @Test
    public void testShort() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write((short)5);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(5, reader.getShort());

        input.close();
    }

    /**
     * Make sure the reader understands the writers integers.
     */
    @Test
    public void testInteger() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(5);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(5, reader.getInteger());

        input.close();
    }

    /**
     * Make sure the reader understands the writers longs.
     */
    @Test
    public void testLong() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(5L);
        writer.flush();

        byte[] bytes = stream.toByteArray();

        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(5, reader.getLong());

        input.close();

    }

    /**
     * Make sure the reader understands the writers floats.
     */
    @Test
    public void testFloat() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(1.0F);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(1.0f, reader.getFloat(), 1.0f);

        input.close();

    }

    /**
     * Make sure the reader understands the writers doubles.
     */
    @Test
    public void testWriteDouble() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write(1.0D);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(1.0D, reader.getDouble(), 1.0D);

        input.close();
    }

    /**
     * Make sure the reader understands the writers strings.
     */
    @Test
    public void testString() throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.write("test");
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals("test", reader.getString());

        input.close();
    }

    /**
     * Make sure the reader understands the writers small strings.
     */
    @Test
    public void testSmallString() throws Exception {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        writer.writeSmallString("test");
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals("test", reader.getSmallString());

        input.close();
    }

    /**
     * Make sure the reader understands the writers locations (null world)
     */
    @Test
    public void testLocation() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        // null world location
        Location location = new SyncLocation((String)null, 10.0D, 10.0D, 10.0D, 7f, 8f);

        writer.write(location);
        writer.write(location);
        writer.write(location);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(location, reader.getLocation());
        assertEquals(location, reader.getLocation());
        assertEquals(location, reader.getLocation());

        input.close();
    }

    /**
     * Make sure the reader understands the writers locations.
     */
    @Test
    public void testLocation1() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        BukkitTester.init();
        World world = BukkitTester.world("world");

        Location location = new SyncLocation(world, 10, 10, 10);

        writer.write(location);
        writer.write(location);
        writer.write(location);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(location, reader.getLocation());
        assertEquals(location, reader.getLocation());
        assertEquals(location, reader.getLocation());

        input.close();
    }

    @Test
    public void testLocation2() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        NucleusByteWriter writer =  new NucleusByteWriter(stream);

        BukkitTester.init();
        World world = BukkitTester.world("world");

        Location location1 = new Location(world, 10, 10, 10);
        Location location2 = new Location(null, 10.0D, 10.0D, 10.0D, 7.0001f, 8.2384f);

        writer.write(location1);
        writer.write(location2);
        writer.write(location1);
        writer.write(location2);
        writer.flush();

        byte[] bytes = stream.toByteArray();
        writer.close();

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        NucleusByteReader reader = new NucleusByteReader(input);

        assertEquals(new SyncLocation(location1), reader.getLocation());
        assertEquals(new SyncLocation(location2), reader.getLocation());
        assertEquals(new SyncLocation(location1), reader.getLocation());
        assertEquals(new SyncLocation(location2), reader.getLocation());

        input.close();
    }
}
