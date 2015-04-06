package com.jcwhatever.nucleus.storage.serialize;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.storage.DeserializeException;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.MemoryDataNode;
import com.jcwhatever.nucleus.utils.coords.SyncLocation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

/**
 * Tests {@link DataFieldSerializer}.
 */
public class DataFieldSerializerTest {

    private TestClass getTestClass() {
        TestClass test = new TestClass();

        test.str1 = null;
        test.str2 = "str";

        test.b = false;
        test.b2 = 1;
        test.s = 2;
        test.i = 3;
        test.l = 4L;
        test.f = 5f;
        test.d = 6D;

        test.location = new Location(null, 1, 2, 3);
        test.itemStack = new ItemStack(Material.GRASS);
        test.itemStackArray = new ItemStack[] {
                new ItemStack(Material.HARD_CLAY),
                new ItemStack(Material.DEAD_BUSH)
        };

        test.serializable.s = "modified";
        test.testEnum = TestEnum.CONSTANT2;

        return test;
    }

    @Test
    public void testSerialize() throws Exception {

        TestClass test = getTestClass();

        MemoryDataNode node = new MemoryDataNode(BukkitTester.mockPlugin("test"));

        DataFieldSerializer.serialize(test, node);

        assertEquals(null, node.getString("str1"));
        assertEquals("str", node.getString("str2"));
        assertEquals(false, node.getBoolean("b"));
        assertEquals(1, node.getInteger("b2"));
        assertEquals(2, node.getInteger("s"));
        assertEquals(3, node.getInteger("i"));
        assertEquals(4L, node.getInteger("l"));
        assertEquals(5f, node.getDouble("f"), 0.0f);
        assertEquals(6D, node.getDouble("d"), 0.0D);

        assertEquals(new SyncLocation((World) null, 1, 2, 3), node.getLocation("location"));
        assertArrayEquals(new ItemStack[]{new ItemStack(Material.GRASS)},
                node.getItemStacks("itemStack"));

        assertArrayEquals(new ItemStack[]{
                        new ItemStack(Material.HARD_CLAY),
                        new ItemStack(Material.DEAD_BUSH)},
                node.getItemStacks("itemStackArray"));

        assertEquals("modified", node.getString("serializable.s"));

        assertEquals(TestEnum.CONSTANT2, node.getEnum("testEnum", TestEnum.class));

        assertEquals(false, node.hasNode("nonData1"));
        assertEquals(false, node.hasNode("nonData2"));
        assertEquals(false, node.hasNode("nonData3"));
        assertEquals(false, node.hasNode("nonData4"));
    }

    @Test
    public void testDeserializeInto() throws Exception {


        MemoryDataNode node = new MemoryDataNode(BukkitTester.mockPlugin("test"));
        DataFieldSerializer.serialize(getTestClass(), node);

        TestClass test = new TestClass();

        DataFieldSerializer.deserializeInto(test, node);

        assertEquals("", test.str1);
        assertEquals("str", test.str2);
        assertEquals(false, test.b);
        assertEquals(1, test.b2);
        assertEquals(2, test.s);
        assertEquals(3, test.i);
        assertEquals(4L, test.l);
        assertEquals(5f, test.f, 0.0f);
        assertEquals(6D, test.d, 0.0D);

        assertEquals(new SyncLocation((World) null, 1, 2, 3), test.location);
        assertEquals(new ItemStack(Material.GRASS), test.itemStack);

        assertArrayEquals(new ItemStack[]{
                        new ItemStack(Material.HARD_CLAY),
                        new ItemStack(Material.DEAD_BUSH)},
                test.itemStackArray);

        assertEquals("modified", test.serializable.s);

        assertEquals(TestEnum.CONSTANT2, test.testEnum);

        assertEquals(true, test.nonData1);
        assertEquals(13, test.nonData2);
        assertEquals(14.0f, test.nonData3, 0.0f);
        assertEquals(new Location(null, 5, 4, 3), test.nonData4);

    }

    private static class TestClass {

        private boolean nonData1 = true;
        private long nonData2 = 13;
        private Float nonData3 = 14.0f;
        private Location nonData4 = new Location(null, 5, 4, 3);

        @DataField private String str1 = "";
        @DataField private String str2 = null;
        @DataField private boolean b = true;
        @DataField private byte b2 = 10;
        @DataField private short s = 11;
        @DataField private int i = 12;
        @DataField private long l = 13;
        @DataField private float f = 14.0f;
        @DataField private double d = 15.0D;

        @DataField private Location location = new Location(null, 5, 4, 3);
        @DataField private ItemStack itemStack = new ItemStack(Material.WOOD);
        @DataField private ItemStack[] itemStackArray = new ItemStack[] {
                new ItemStack(Material.PAPER)
        };

        @DataField private SerializableClass serializable = new SerializableClass();

        @DataField private TestEnum testEnum = TestEnum.CONSTANT1;
    }

    private static class SerializableClass implements IDataNodeSerializable {

        private String s = "orig";

        @Override
        public void serialize(IDataNode dataNode) {
            dataNode.set("s", s);
        }

        @Override
        public void deserialize(IDataNode dataNode) throws DeserializeException {
            s = dataNode.getString("s");
        }
    }

    private enum TestEnum {
        CONSTANT1,
        CONSTANT2
    }
}