package com.jcwhatever.nucleus.storage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.bukkit.v1_8_R1.MockServer;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;
import com.jcwhatever.nucleus.utils.observer.result.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.result.Result;

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

    protected IDataNodeGenerator _generator;
    private volatile int _testLoadRunCount;
    private volatile int _testSaveRunCount;

    public interface IDataNodeGenerator {
        IDataNode generateRoot();
    }

    protected void setNodeGenerator(IDataNodeGenerator generator) {
        _generator = generator;
    }

    protected void initDataNode(IDataNode node) {
        node.set("boolean", true);
        node.set("integer", Integer.MAX_VALUE);
        node.set("long", Long.MAX_VALUE);
        node.set("double", Double.MAX_VALUE);
        node.set("string", "String");
        node.set("uuid", UUID.randomUUID());
        node.set("enum", TestEnum.CONSTANT);
        node.set("location", new Location(BukkitTest.world("world"), 0, 0, 0));
        node.set("items", new ItemStackBuilder(Material.WOOD).build());
    }

    @BeforeClass
    public static void testStartup() {
        try {
            Bukkit.setServer(new MockServer());
        }
        catch (UnsupportedOperationException ignore) {}
    }


    @Test
    public void testRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        IDataNode node1 = dataNode.getNode("node1");
        IDataNode node2 = node1.getNode("node2");

        assertEquals(dataNode, node1.getRoot());
        assertEquals(dataNode, node2.getRoot());
    }

    /**
     * Test size from a root node.
     */
    @Test
    public void testSizeRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testSize(dataNode);
    }

    /**
     * Test size from a sub node.
     */
    @Test
    public void testSizeSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testSize(dataNode.getNode("newNode"));
    }

    private void testSize(IDataNode dataNode) {

        assertEquals(0, dataNode.size());

        // add 9 key values
        initDataNode(dataNode);

        assertEquals(9, dataNode.size());

        dataNode.set("testSize", "value");

        assertEquals(10, dataNode.size());

        dataNode.clear();

        assertEquals(0, dataNode.size());
    }

    /**
     * test hasNode on root node.
     */
    @Test
    public void testHasNode() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testHasNode(dataNode);
    }

    /**
     * test hasNode on sub node.
     */
    @Test
    public void testHasNode1() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testHasNode(dataNode.getNode("newNode"));
    }

    private void testHasNode(IDataNode dataNode) {

        dataNode.set("testHasNode", "value");

        assertEquals(true, dataNode.hasNode("testHasNode"));

        dataNode.set("testHasNode1.val", "value");

        assertEquals(true, dataNode.hasNode("testHasNode1.val"));
    }

    /**
     * test getNode from root node.
     */
    @Test
    public void testGetNodeRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testGetNode(dataNode);
    }

    /**
     * test getNode from sub node.
     */
    @Test
    public void testGetNodeSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testGetNode(dataNode.getNode("newNode"));
    }

    private void testGetNode(IDataNode dataNode) {

        dataNode.set("testGetNode", "node");

        IDataNode node = dataNode.getNode("testGetNode");

        assertNotNull(node);

        node = dataNode.getNode("newNode");

        assertNotNull(node);
    }

    /**
     * test getSubNodeNames from root node.
     */
    @Test
    public void testGetSubNodeNamesRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        testGetSubNodeNames(dataNode);
    }

    /**
     * test getSubNodeNames from subNode
     */
    @Test
    public void testGetSubNodeNamesSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testGetSubNodeNames(dataNode.getNode("newNode"));
    }

    private void testGetSubNodeNames(IDataNode dataNode) {
        dataNode.set("testGetSubNodeNames", "node");

        IDataNode node = dataNode.getNode("testGetNode");

        assertNotNull(node);

        node = dataNode.getNode("newNode");

        assertNotNull(node);
    }

    /**
     * test clear from root node.
     */
    @Test
    public void testClearRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testClear(dataNode);
    }

    /**
     * test clear from sub node.
     */
    @Test
    public void testClearSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testClear(dataNode.getNode("newNode"));
    }

    private void testClear(IDataNode dataNode) {

        dataNode.set("testClear", "node");
        dataNode.set("testClear2", "node");
        dataNode.set("testClear3", "node");

        assertEquals(3, dataNode.size());

        dataNode.clear();

        assertEquals(0, dataNode.size());
    }

    /**
     * test remove from root node.
     */
    @Test
    public void testRemove() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        dataNode.set("testRemoval", "node");
        dataNode.set("testRemoval2", "node");
        dataNode.set("testRemoval3", "node");

        assertEquals(true, dataNode.hasNode("testRemoval2"));

        dataNode.remove("testRemoval2");

        assertEquals(false, dataNode.hasNode("testRemoval2"));
    }

    /**
     * test remove from sub node
     */
    @Test
    public void testRemove1() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        dataNode.set("testRemoval", "node");
        dataNode.set("testRemoval2", "node");
        dataNode.set("testRemoval3", "node");

        assertEquals(true, dataNode.hasNode("testRemoval2"));

        IDataNode subNode = dataNode.getNode("testRemoval2");

        subNode.remove();

        assertEquals(false, dataNode.hasNode("testRemoval2"));
    }

    /**
     * test set from root node.
     */
    @Test
    public void testSetRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        testSet(dataNode);
    }

    /**
     * test set from sub node.
     */
    @Test
    public void testSetSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testSet(dataNode.getNode("newNode"));
    }

    private void testSet(IDataNode dataNode) {

        dataNode.set("testSet.test", "node");

        assertEquals(true, dataNode.hasNode("testSet"));
        assertEquals(true, dataNode.hasNode("testSet.test"));

        IDataNode node = dataNode.getNode("testSet");

        assertEquals(true, node.hasNode("test"));

        String value = node.getString("test");

        assertEquals("node", value);
    }

    /**
     * test getAllValues from root node.
     */
    @Test
    public void testGetAllValuesRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testGetAllValues(dataNode);
    }

    /**
     * test getAllValues from root node.
     */
    @Test
    public void testGetAllValuesSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testGetAllValues(dataNode.getNode("newNode"));
    }

    private void testGetAllValues(IDataNode dataNode) {

        dataNode.set("testGetAllValues.test1", "node");
        dataNode.set("testGetAllValues.test2", "node");

        Map<String, Object> values = dataNode.getAllValues();

        assertEquals("node", values.get("testGetAllValues.test1"));
        assertEquals("node", values.get("testGetAllValues.test2"));
        assertEquals(2, values.size());

    }

    /**
     * test get from root node.
     */
    @Test
    public void testGetRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        testGet(dataNode);
    }

    /**
     * test get from sub node.
     */
    @Test
    public void testGetSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        testGet(dataNode.getNode("newNode"));
    }

    private void testGet(IDataNode dataNode) {

        dataNode.set("testGet", "node");
        dataNode.set("testGet.test", "node"); // should erase test get

        assertEquals(false, dataNode.get("testGet") instanceof String);
        assertEquals(true, dataNode.get("testGet.test") instanceof String);


        IDataNode node = dataNode.getNode("testGet");

        // the second setter should have converted "testGet" from a value
        // to a node.
        assertEquals(null, node.getString(""));

        dataNode.set("testGet", "node");

        assertEquals("node", node.getString(""));
        //children destroyed when testGet converted to key value
        assertEquals(false, dataNode.get("testGet.test") instanceof String);

        node.set("", "node2");
        assertEquals("node2", node.getString(""));
    }

    /**
     * test getInteger on root node.
     */
    @Test
    public void testGetIntegerRoot() throws Exception {

        testGetInteger(_generator.generateRoot());
    }

    /**
     * test getInteger on sub node.
     */
    @Test
    public void testGetIntegerSub() throws Exception {

        testGetInteger(_generator.generateRoot().getNode("newNode"));
    }

    private void testGetInteger(IDataNode dataNode) {

        initDataNode(dataNode);

        dataNode.set("testGetInteger", 1);

        assertEquals(1, dataNode.getInteger("testGetInteger"));
        assertEquals(0, dataNode.getInteger("testGetInteger1"));

        assertEquals(Integer.MAX_VALUE, dataNode.getInteger("integer"));
    }

    /**
     * test getLong on root node.
     */
    @Test
    public void testGetLongRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetLong(dataNode);
    }

    /**
     * test getLong on sub node.
     */
    @Test
    public void testGetLongSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetLong(dataNode.getNode("newNode"));
    }

    private void testGetLong(IDataNode dataNode) {
        initDataNode(dataNode);

        dataNode.set("testGetLong", 1);

        assertEquals(1, dataNode.getLong("testGetLong"));
        assertEquals(0, dataNode.getLong("testGetLong1"));

        assertEquals(Long.MAX_VALUE, dataNode.getLong("long"));
    }

    /**
     * test getDouble on root node.
     */
    @Test
    public void testGetDoubleRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetDouble(dataNode);
    }

    /**
     * test getDouble on sub node.
     */
    @Test
    public void testGetDoubleSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetDouble(dataNode.getNode("newNode"));
    }

    private void testGetDouble(IDataNode dataNode) {
        initDataNode(dataNode);

        dataNode.set("testGetDouble", 1.0D);

        assertEquals(1.0D, dataNode.getDouble("testGetDouble"), 0.0D);
        assertEquals(0.0D, dataNode.getDouble("testGetDouble1"), 0.0D);

        assertEquals(Double.MAX_VALUE, dataNode.getDouble("double"), 0.0D);
    }

    /**
     * test getBoolean on root node.
     */
    @Test
    public void testGetBooleanRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetBoolean(dataNode);
    }

    /**
     * test getBoolean on sub node.
     */
    @Test
    public void testGetBooleanSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetBoolean(dataNode.getNode("newNode"));
    }

    private void testGetBoolean(IDataNode dataNode) {
        initDataNode(dataNode);

        dataNode.set("testGetBoolean", true);

        assertEquals(true, dataNode.getBoolean("testGetBoolean"));
        assertEquals(false, dataNode.getBoolean("testGetBoolean1"));

        assertEquals(true, dataNode.getBoolean("boolean"));
    }

    /**
     * test getString on root node.
     */
    @Test
    public void testGetStringRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetString(dataNode);
    }

    /**
     * test getString on sub node.
     */
    @Test
    public void testGetStringSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetString(dataNode.getNode("newNode"));
    }

    private void testGetString(IDataNode dataNode) {
        initDataNode(dataNode);

        dataNode.set("testGetString", "string");

        assertEquals("string", dataNode.getString("testGetString"));
        assertEquals(null, dataNode.getString("testGetString1"));

        assertEquals("String", dataNode.getString("string"));
    }

    /**
     * test getUUID on root node.
     */
    @Test
    public void testGetUUIDRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetUUID(dataNode);
    }

    /**
     * test getUUID on sub node.
     */
    @Test
    public void testGetUUIDSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetUUID(dataNode.getNode("newNode"));
    }

    private void testGetUUID(IDataNode dataNode) {
        initDataNode(dataNode);

        UUID id = UUID.randomUUID();

        dataNode.set("testGetUUID", id);

        assertEquals(id, dataNode.getUUID("testGetUUID"));
        assertEquals(null, dataNode.getUUID("testGetUUID1"));

        Assert.assertEquals(true, dataNode.getUUID("uuid") != null);
    }

    /**
     * test getLocation on root node.
     */
    @Test
    public void testGetLocationRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetLocation(dataNode);
    }

    /**
     * test getLocation on sub node.
     */
    @Test
    public void testGetLocationSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetLocation(dataNode.getNode("newNode"));
    }

    private void testGetLocation(IDataNode dataNode) {
        initDataNode(dataNode);

        World world = BukkitTest.world("dummy");
        Location location = new Location(world, 0, 0, 0);

        dataNode.set("testGetLocation", location);

        Assert.assertEquals(true, dataNode.getLocation("testGetLocation") != null);
        assertEquals(null, dataNode.getLocation("testGetLocation1"));

        Assert.assertEquals(true, dataNode.getLocation("location") != null);
    }

    /**
     * test getItemStacks on root node.
     */
    @Test
    public void testGetItemStacksRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetItemStack(dataNode);
    }

    /**
     * test getItemStacks on sub node.
     */
    @Test
    public void testGetItemStacksSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetItemStack(dataNode.getNode("newNode"));
    }

    private void testGetItemStack(IDataNode dataNode) {
        initDataNode(dataNode);

        ItemStack[] items = new ItemStack[] {
                new ItemStackBuilder(Material.STONE).build(),
                new ItemStackBuilder(Material.WOOD).build(),
                new ItemStackBuilder(Material.ANVIL).build()
        };

        ItemStack item = new ItemStackBuilder(Material.STONE).build();

        dataNode.set("testGetItemStacks", items);
        dataNode.set("testGetItemStack_Single", item);

        assertArrayEquals(items, dataNode.getItemStacks("testGetItemStacks"));
        assertArrayEquals(new ItemStack[]{item}, dataNode.getItemStacks("testGetItemStack_Single"));
        assertArrayEquals(null, dataNode.getItemStacks("testGetItemStacks1"));

        assertNotNull(dataNode.getItemStacks("items"));
        assertEquals(1, dataNode.getItemStacks("items").length);
    }

    /**
     * test getEnum on root node.
     */
    @Test
    public void testGetEnumRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetEnum(dataNode);
    }

    /**
     * test getEnum on sub node.
     */
    @Test
    public void testGetEnumSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetEnum(dataNode.getNode("newNode"));
    }

    private void testGetEnum(IDataNode dataNode) {
        initDataNode(dataNode);

        dataNode.set("testGetEnum", TestEnum.CONSTANT);

        assertEquals(TestEnum.CONSTANT, dataNode.getEnum("testGetEnum", TestEnum.class));
        assertEquals(null, dataNode.getEnum("testGetEnum1", TestEnum.class));

        assertEquals(TestEnum.CONSTANT, dataNode.getEnum("enum", TestEnum.class));
    }

    /**
     * test getEnumGeneric on root node.
     */
    @Test
    public void testGetEnumGenericRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetEnumGeneric(dataNode);
    }

    /**
     * test getEnumGeneric on sub node.
     */
    @Test
    public void testGetEnumGenericSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetEnumGeneric(dataNode.getNode("newNode"));
    }

    private void testGetEnumGeneric(IDataNode dataNode) {
        initDataNode(dataNode);

        dataNode.set("testGetEnumGeneric", TestEnum.CONSTANT);

        assertEquals(TestEnum.CONSTANT, dataNode.getEnum("testGetEnumGeneric", TestEnum.class));
        assertEquals(null, dataNode.getEnum("testGetEnumGeneric1", TestEnum.class));
    }

    /**
     * test getStringList on root node.
     */
    @Test
    public void testGetStringListRoot() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetStringList(dataNode);
    }

    /**
     * test getStringList on sub node.
     */
    @Test
    public void testGetStringListSub() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        testGetStringList(dataNode.getNode("newNode"));
    }

    private void testGetStringList(IDataNode dataNode) {
        initDataNode(dataNode);

        String[] array = new String[] {
                "line1",
                "line2"
        };

        dataNode.set("testGetStringList", array);

        List<String> result = dataNode.getStringList("testGetStringList", null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Make sure that non key nodes do not return a key value.
     */
    @Test
    public void testNonKeyNodesNull() throws Exception {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        dataNode.set("node", "string");
        dataNode.set("node.key", "string");
        assertEquals(null, dataNode.getString("node"));
        assertEquals("default", dataNode.getString("node", "default"));

        dataNode.set("node", 10);
        dataNode.set("node.key", 10);
        assertEquals(0, dataNode.getInteger("node"));
        assertEquals(20, dataNode.getInteger("node", 20));

        dataNode.set("node", 10L);
        dataNode.set("node.key", 10L);
        assertEquals(0, dataNode.getLong("node"));
        assertEquals(20L, dataNode.getLong("node"), 20L);

        dataNode.set("node", true);
        dataNode.set("node.key", true);
        assertEquals(false, dataNode.getBoolean("node"));
        assertEquals(true, dataNode.getBoolean("node", true));

        dataNode.set("node", TestEnum.CONSTANT);
        dataNode.set("node.key", TestEnum.CONSTANT);
        assertEquals(null, dataNode.getEnum("node", TestEnum.class));
        assertEquals(TestEnum.CONSTANT, dataNode.getEnum("node", TestEnum.CONSTANT, TestEnum.class));

        dataNode.set("node", 10.0D);
        dataNode.set("node.key", 10.0D);
        assertEquals(0, dataNode.getDouble("node"), 0.0D);
        assertEquals(20.0D, dataNode.getDouble("node", 20.0D), 0.0D);

        dataNode.set("node", new ItemStackBuilder(Material.WOOD).build());
        dataNode.set("node.key", new ItemStackBuilder(Material.WOOD).build());
        assertArrayEquals(null, dataNode.getItemStacks("node"));
        assertArrayEquals(new ItemStack[]{new ItemStackBuilder(Material.GRASS).build()},
                dataNode.getItemStacks("node", new ItemStackBuilder(Material.GRASS).build()));

    }

    @Test
    public void testLoad() {

        NucleusTest.init();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                IDataNode dataNode = _generator.generateRoot();
                dataNode.loadAsync().onResult(new FutureSubscriber<IDataNode>() {
                    @Override
                    public void on(Result<IDataNode> result) {

                        _testLoadRunCount++;

                    }
                });
            }
        });

        thread.run();

        long timeout = System.currentTimeMillis() + 5000;

        while(_testLoadRunCount == 0 && System.currentTimeMillis() < timeout) {

            BukkitTest.heartBeat();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
        }

        assertEquals(1, _testLoadRunCount);

    }

    @Test
    public void testSave() {

        NucleusTest.init();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IDataNode dataNode = _generator.generateRoot();
                assertEquals(true, dataNode.load());

                dataNode = _generator.generateRoot();

                dataNode.save().onResult(new FutureSubscriber<IDataNode>() {
                    @Override
                    public void on(Result<IDataNode> result) {

                        _testSaveRunCount++;

                    }
                });
            }
        });

        thread.run();

        long timeout = System.currentTimeMillis() + 5000;

        while(_testSaveRunCount == 0 && System.currentTimeMillis() < timeout) {

            BukkitTest.heartBeat();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
        }

        assertEquals(1, _testSaveRunCount);
    }

    public enum TestEnum {
        CONSTANT
    }

}
