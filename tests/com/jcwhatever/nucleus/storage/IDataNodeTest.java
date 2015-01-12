package com.jcwhatever.nucleus.storage;

import com.jcwhatever.dummy.DummyServer;
import com.jcwhatever.dummy.DummyWorld;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
 * 
 */
public abstract class IDataNodeTest {

    protected IDataNode _dataNode;

    protected void initNode(IDataNode node) {
        node.set("boolean", true);
        node.set("integer", Integer.MAX_VALUE);
        node.set("long", Long.MAX_VALUE);
        node.set("double", Double.MAX_VALUE);
        node.set("string", "String");
        node.set("uuid", UUID.randomUUID());
        node.set("enum", TestEnum.CONSTANT);
        node.set("location", new Location(new DummyWorld("world"), 0, 0, 0));
        node.set("items", new ItemStackBuilder(Material.WOOD).build());
    }

    @BeforeClass
    public static void testStartup() {
        try {
            Bukkit.setServer(new DummyServer());
        }
        catch (UnsupportedOperationException ignore) {}
    }


    @Test
    public void testRoot() throws Exception {

        IDataNode node1 = _dataNode.getNode("node1");
        IDataNode node2 = node1.getNode("node2");

        Assert.assertTrue(node1.getRoot() == _dataNode);
        Assert.assertTrue(node2.getRoot() == _dataNode);
    }

    @Test
    public void testSize() throws Exception {

        int initialSize = _dataNode.size();

        _dataNode.set("testSize", "value");

        Assert.assertEquals(initialSize + 1, _dataNode.size());
    }

    @Test
    public void testHasNode() throws Exception {

        _dataNode.set("testHasNode", "value");

        Assert.assertTrue(_dataNode.hasNode("testHasNode"));

        _dataNode.set("testHasNode1.val", "value");

        Assert.assertTrue(_dataNode.hasNode("testHasNode1.val"));
    }

    @Test
    public void testGetNode() throws Exception {

        _dataNode.set("testGetNode", "node");

        IDataNode dataNode = _dataNode.getNode("testGetNode");

        Assert.assertTrue(dataNode != null);

        dataNode = _dataNode.getNode("newNode");

        Assert.assertTrue(dataNode != null);
    }

    @Test
    public void testGetSubNodeNames() throws Exception {

        _dataNode.set("testGetSubNodeNames", "node");

        IDataNode dataNode = _dataNode.getNode("testGetNode");

        Assert.assertTrue(dataNode != null);

        dataNode = _dataNode.getNode("newNode");

        Assert.assertTrue(dataNode != null);
    }

    @Test
    public void testClear() throws Exception {

        int initialSize = _dataNode.size();

        _dataNode.set("testClear", "node");
        _dataNode.set("testClear2", "node");
        _dataNode.set("testClear3", "node");

        Assert.assertTrue(_dataNode.size() == initialSize + 3);

        _dataNode.clear();

        Assert.assertTrue(_dataNode.size() == 0);
    }

    @Test
    public void testRemove() throws Exception {
        _dataNode.set("testRemoval", "node");
        _dataNode.set("testRemoval2", "node");
        _dataNode.set("testRemoval3", "node");

        Assert.assertTrue(_dataNode.hasNode("testRemoval2"));

        _dataNode.remove("testRemoval2");

        Assert.assertFalse(_dataNode.hasNode("testRemoval2"));
    }

    @Test
    public void testRemove1() throws Exception {
        _dataNode.set("testRemoval", "node");
        _dataNode.set("testRemoval2", "node");
        _dataNode.set("testRemoval3", "node");

        Assert.assertTrue(_dataNode.hasNode("testRemoval2"));

        IDataNode node = _dataNode.getNode("testRemoval2");

        node.remove();

        Assert.assertFalse(_dataNode.hasNode("testRemoval2"));
    }

    @Test
    public void testSet() throws Exception {
        //_dataNode.set("testSet", "node");
        _dataNode.set("testSet.test", "node");

        Assert.assertTrue(_dataNode.hasNode("testSet"));
        Assert.assertTrue(_dataNode.hasNode("testSet.test"));

        IDataNode node = _dataNode.getNode("testSet");

        Assert.assertTrue(node.hasNode("test"));

        String value = node.getString("test");

        Assert.assertEquals("node", value);
    }

    @Test
    public void testGetAllValues() throws Exception {

        _dataNode.clear();
        _dataNode.set("testGetAllValues.test1", "node");
        _dataNode.set("testGetAllValues.test2", "node");

        Map<String, Object> values = _dataNode.getAllValues();

        Assert.assertEquals("node", values.get("testGetAllValues.test1"));
        Assert.assertEquals("node", values.get("testGetAllValues.test2"));
        Assert.assertEquals(2, values.size());
    }

    @Test
    public void testGet() throws Exception {

        _dataNode.set("testGet", "node");
        _dataNode.set("testGet.test", "node"); // should erase test get

        Assert.assertFalse(_dataNode.get("testGet") instanceof String);
        Assert.assertTrue(_dataNode.get("testGet.test") instanceof String);


        IDataNode node = _dataNode.getNode("testGet");

        // the second setter should have converted "testGet" from a value
        // to a node.
        Assert.assertEquals(null, node.getString(""));

        _dataNode.set("testGet", "node");

        Assert.assertEquals("node", node.getString(""));
        //children destroyed when testGet converted to key value
        Assert.assertFalse(_dataNode.get("testGet.test") instanceof String);

        node.set("", "node2");
        Assert.assertEquals("node2", node.getString(""));
    }

    @Test
    public void testGetInteger() throws Exception {

        _dataNode.set("testGetInteger", 1);

        Assert.assertEquals(1, _dataNode.getInteger("testGetInteger"));
        Assert.assertEquals(0, _dataNode.getInteger("testGetInteger1"));

        Assert.assertEquals(Integer.MAX_VALUE, _dataNode.getInteger("integer"));
    }

    @Test
    public void testGetLong() throws Exception {

        _dataNode.set("testGetLong", 1);

        Assert.assertEquals(1, _dataNode.getLong("testGetLong"));
        Assert.assertEquals(0, _dataNode.getLong("testGetLong1"));

        Assert.assertEquals(Long.MAX_VALUE, _dataNode.getLong("long"));
    }

    @Test
    public void testGetDouble() throws Exception {
        _dataNode.set("testGetDouble", 1.0D);

        Assert.assertEquals(1.0D, _dataNode.getDouble("testGetDouble"), 0.0D);
        Assert.assertEquals(0.0D, _dataNode.getDouble("testGetDouble1"), 0.0D);

        Assert.assertEquals(Double.MAX_VALUE, _dataNode.getDouble("double"), 0.0D);
    }

    @Test
    public void testGetBoolean() throws Exception {
        _dataNode.set("testGetBoolean", true);

        Assert.assertEquals(true, _dataNode.getBoolean("testGetBoolean"));
        Assert.assertEquals(false, _dataNode.getBoolean("testGetBoolean1"));

        Assert.assertEquals(true, _dataNode.getBoolean("boolean"));
    }

    @Test
    public void testGetString() throws Exception {
        _dataNode.set("testGetString", "string");

        Assert.assertEquals("string", _dataNode.getString("testGetString"));
        Assert.assertEquals(null, _dataNode.getString("testGetString1"));

        Assert.assertEquals("String", _dataNode.getString("string"));
    }

    @Test
    public void testGetUUID() throws Exception {

        UUID id = UUID.randomUUID();

        _dataNode.set("testGetUUID", id);

        Assert.assertEquals(id, _dataNode.getUUID("testGetUUID"));
        Assert.assertEquals(null, _dataNode.getUUID("testGetUUID1"));

        Assert.assertTrue(_dataNode.getUUID("uuid") != null);
    }

    @Test
    public void testGetLocation() throws Exception {

        World world = new DummyWorld("dummy");
        Location location = new Location(world, 0, 0, 0);

        _dataNode.set("testGetLocation", location);

        Assert.assertTrue(_dataNode.getLocation("testGetLocation") != null);
        Assert.assertEquals(null, _dataNode.getLocation("testGetLocation1"));

        Assert.assertTrue(_dataNode.getLocation("location") != null);
    }

    @Test
    public void testGetItemStacks() throws Exception {

        ItemStack[] items = new ItemStack[] {
                new ItemStackBuilder(Material.STONE).build(),
                new ItemStackBuilder(Material.WOOD).build(),
                new ItemStackBuilder(Material.ANVIL).build()
        };

        ItemStack item = new ItemStackBuilder(Material.STONE).build();

        _dataNode.set("testGetItemStacks", items);
        _dataNode.set("testGetItemStack_Single", item);

        Assert.assertArrayEquals(items, _dataNode.getItemStacks("testGetItemStacks"));
        Assert.assertArrayEquals(new ItemStack[]{item}, _dataNode.getItemStacks("testGetItemStack_Single"));
        Assert.assertArrayEquals(null, _dataNode.getItemStacks("testGetItemStacks1"));

        Assert.assertTrue(_dataNode.getItemStacks("items") != null);
        Assert.assertTrue(_dataNode.getItemStacks("items").length == 1);
    }

    @Test
    public void testGetEnum() throws Exception {

        _dataNode.set("testGetEnum", TestEnum.CONSTANT);

        Assert.assertEquals(TestEnum.CONSTANT, _dataNode.getEnum("testGetEnum", TestEnum.class));
        Assert.assertEquals(null, _dataNode.getEnum("testGetEnum1", TestEnum.class));

        Assert.assertTrue(_dataNode.getEnum("enum", TestEnum.class) == TestEnum.CONSTANT);
    }

    @Test
    public void testGetEnumGeneric() throws Exception {

        _dataNode.set("testGetEnumGeneric", TestEnum.CONSTANT);

        Assert.assertEquals(TestEnum.CONSTANT, _dataNode.getEnum("testGetEnumGeneric", TestEnum.class));
        Assert.assertEquals(null, _dataNode.getEnum("testGetEnumGeneric1", TestEnum.class));
    }

    @Test
    public void testGetStringList() throws Exception {

        String[] array = new String[] {
                "line1",
                "line2"
        };

        _dataNode.set("testGetStringList", array);

        List<String> result = _dataNode.getStringList("testGetStringList", null);

        Assert.assertEquals(true, result != null);
        Assert.assertEquals(2, result.size());
    }

    /**
     * Make sure that non key nodes do not return a key value.
     */
    @Test
    public void testNonKeyNodesNull() throws Exception {

        _dataNode.set("node", "string");
        _dataNode.set("node.key", "string");
        Assert.assertEquals(null, _dataNode.getString("node"));
        Assert.assertEquals("default", _dataNode.getString("node", "default"));

        _dataNode.set("node", 10);
        _dataNode.set("node.key", 10);
        Assert.assertEquals(0, _dataNode.getInteger("node"));
        Assert.assertEquals(20, _dataNode.getInteger("node", 20));

        _dataNode.set("node", 10L);
        _dataNode.set("node.key", 10L);
        Assert.assertEquals(0, _dataNode.getLong("node"));
        Assert.assertEquals(20L, _dataNode.getLong("node"), 20L);

        _dataNode.set("node", true);
        _dataNode.set("node.key", true);
        Assert.assertEquals(false, _dataNode.getBoolean("node"));
        Assert.assertEquals(true, _dataNode.getBoolean("node", true));

        _dataNode.set("node", TestEnum.CONSTANT);
        _dataNode.set("node.key", TestEnum.CONSTANT);
        Assert.assertEquals(null, _dataNode.getEnum("node", TestEnum.class));
        Assert.assertEquals(TestEnum.CONSTANT, _dataNode.getEnum("node", TestEnum.CONSTANT, TestEnum.class));

        _dataNode.set("node", 10.0D);
        _dataNode.set("node.key", 10.0D);
        Assert.assertEquals(0, _dataNode.getDouble("node"), 0.0D);
        Assert.assertEquals(20.0D, _dataNode.getDouble("node", 20.0D), 0.0D);

        _dataNode.set("node", new ItemStackBuilder(Material.WOOD).build());
        _dataNode.set("node.key", new ItemStackBuilder(Material.WOOD).build());
        Assert.assertArrayEquals(null, _dataNode.getItemStacks("node"));
        Assert.assertArrayEquals(new ItemStack[] { new ItemStackBuilder(Material.GRASS).build() },
                _dataNode.getItemStacks("node", new ItemStackBuilder(Material.GRASS).build()));

    }

    public enum TestEnum {
        CONSTANT
    }

}
