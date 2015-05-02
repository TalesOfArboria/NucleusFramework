package com.jcwhatever.nucleus.storage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.jcwhatever.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.storage.IDataNode.AutoSaveMode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

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
        node.set("location", new Location(BukkitTester.world("world"), 0, 0, 0));
        node.set("items", new ItemStackBuilder(Material.WOOD).build());
    }

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    @Test
    public void testRoot() {

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
    public void testSizeRoot() {

        IDataNode dataNode = _generator.generateRoot();

        testSize(dataNode);
    }

    /**
     * Test size from a sub node.
     */
    @Test
    public void testSizeSub() {

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
     * Make sure {@link IDataNode#isDirty} method returns the correct value.
     */
    @Test
    public void testDirty() {

        IDataNode dataNode = _generator.generateRoot();

        assertEquals(false, dataNode.isDirty());

        IDataNode node1 = dataNode.getNode("node1");
        IDataNode node2 = dataNode.getNode("node2");

        assertEquals(false, dataNode.isDirty());

        node1.set("test", "value");

        assertEquals(true, dataNode.isDirty());
        assertEquals(true, node1.isDirty());
        assertEquals(false, node2.isDirty());

        node1.saveSync();

        assertEquals(false, dataNode.isDirty());
        assertEquals(false, node1.isDirty());
        assertEquals(false, node2.isDirty());
    }

    /**
     * test hasNode on root node.
     */
    @Test
    public void testHasNode() {

        IDataNode dataNode = _generator.generateRoot();

        testHasNode(dataNode);
    }

    /**
     * test hasNode on sub node.
     */
    @Test
    public void testHasNode1() {

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
    public void testGetNodeRoot() {

        IDataNode dataNode = _generator.generateRoot();

        testGetNode(dataNode);
    }

    /**
     * test getNode from sub node.
     */
    @Test
    public void testGetNodeSub() {

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
    public void testGetSubNodeNamesRoot() {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        testGetSubNodeNames(dataNode);
    }

    /**
     * test getSubNodeNames from subNode
     */
    @Test
    public void testGetSubNodeNamesSub() {

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
    public void testClearRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testClear(dataNode);
    }

    /**
     * test clear from sub node.
     */
    @Test
    public void testClearSub() {

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
    public void testRemove() {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        dataNode.set("testRemoval", "node");
        dataNode.set("testRemoval2", "node");
        dataNode.set("testRemoval3", "node");

        assertEquals(true, dataNode.hasNode("testRemoval2"));

        dataNode.remove("testRemoval2");

        assertEquals(false, dataNode.hasNode("testRemoval2"));

        try {
            // test remove root node
            dataNode.remove();
            throw new AssertionError("UnsupportedOperationException expected.");
        }
        catch (UnsupportedOperationException ignore) {}
    }

    /**
     * test remove from sub node
     */
    @Test
    public void testRemove1() {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        dataNode.set("testRemoval", "node");
        dataNode.set("testRemoval2", "node");
        dataNode.set("testRemoval3", "node");

        assertEquals(true, dataNode.hasNode("testRemoval2"));

        IDataNode subNode = dataNode.getNode("testRemoval2");
        subNode.remove();
        assertEquals(false, dataNode.hasNode("testRemoval2"));

        // remove non-explicitly set node
        subNode = dataNode.getNode("testRemoval4");
        subNode.remove();
        assertEquals(false, dataNode.hasNode("testRemoval4"));
    }

    /**
     * test set from root node.
     */
    @Test
    public void testSetRoot() {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        testSet(dataNode);
    }

    /**
     * test set from sub node.
     */
    @Test
    public void testSetSub() {

        IDataNode dataNode = _generator.generateRoot();

        testSet(dataNode.getNode("newNode"));
    }

    private void testSet(IDataNode dataNode) {

        boolean isSet = dataNode.set("testSet.test", "node");

        assertEquals(true, isSet);
        assertEquals(true, dataNode.hasNode("testSet"));
        assertEquals(true, dataNode.hasNode("testSet.test"));

        IDataNode node = dataNode.getNode("testSet");

        assertEquals(true, node.hasNode("test"));

        String value = node.getString("test");

        assertEquals("node", value);

        // test nulling value
        isSet = dataNode.set("testSet.test", null);
        assertEquals(true, isSet);

        // test nulling value on non-existing node key
        isSet = dataNode.set("testSet.test2", null);
        assertEquals(true, isSet);
    }

    /**
     * test getAllValues from root node.
     */
    @Test
    public void testGetAllValuesRoot() {

        IDataNode dataNode = _generator.generateRoot();

        testGetAllValues(dataNode);
    }

    /**
     * test getAllValues from root node.
     */
    @Test
    public void testGetAllValuesSub() {

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
    public void testGetRoot() {

        IDataNode dataNode = _generator.generateRoot();
        initDataNode(dataNode);

        testGet(dataNode);
    }

    /**
     * test get from sub node.
     */
    @Test
    public void testGetSub() {

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
    public void testGetIntegerRoot() {

        testGetInteger(_generator.generateRoot());
    }

    /**
     * test getInteger on sub node.
     */
    @Test
    public void testGetIntegerSub() {

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
    public void testGetLongRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetLong(dataNode);
    }

    /**
     * test getLong on sub node.
     */
    @Test
    public void testGetLongSub() {

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
    public void testGetDoubleRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetDouble(dataNode);
    }

    /**
     * test getDouble on sub node.
     */
    @Test
    public void testGetDoubleSub() {

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
    public void testGetBooleanRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetBoolean(dataNode);
    }

    /**
     * test getBoolean on sub node.
     */
    @Test
    public void testGetBooleanSub() {

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
    public void testGetStringRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetString(dataNode);
    }

    /**
     * test getString on sub node.
     */
    @Test
    public void testGetStringSub() {

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
    public void testGetUUIDRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetUUID(dataNode);
    }

    /**
     * test getUUID on sub node.
     */
    @Test
    public void testGetUUIDSub() {

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
    public void testGetLocationRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetLocation(dataNode);
    }

    /**
     * test getLocation on sub node.
     */
    @Test
    public void testGetLocationSub() {

        IDataNode dataNode = _generator.generateRoot();
        testGetLocation(dataNode.getNode("newNode"));
    }

    private void testGetLocation(IDataNode dataNode) {
        initDataNode(dataNode);

        World world = BukkitTester.world("dummy");
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
    public void testGetItemStacksRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetItemStack(dataNode);
    }

    /**
     * test getItemStacks on sub node.
     */
    @Test
    public void testGetItemStacksSub() {

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
    public void testGetEnumRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetEnum(dataNode);
    }

    /**
     * test getEnum on sub node.
     */
    @Test
    public void testGetEnumSub() {

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
    public void testGetEnumGenericRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetEnumGeneric(dataNode);
    }

    /**
     * test getEnumGeneric on sub node.
     */
    @Test
    public void testGetEnumGenericSub() {

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
    public void testGetStringListRoot() {

        IDataNode dataNode = _generator.generateRoot();
        testGetStringList(dataNode);
    }

    /**
     * test getStringList on sub node.
     */
    @Test
    public void testGetStringListSub() {

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
     * Make sure node iteration works correctly on root node.
     */
    @Test
    public void testIterableRoot() {
        testIterable(_generator.generateRoot());
    }

    /**
     * Make sure node iteration works correctly on root node.
     */
    @Test
    public void testIterableSub() {
        IDataNode dataNode = _generator.generateRoot();
        testIterable(dataNode.getNode("newNode"));
    }

    private void testIterable(IDataNode dataNode) {

        dataNode.set("key1", "value");
        dataNode.set("key2", "value");
        dataNode.set("node1.key1", "value");
        dataNode.set("node2.key1", "value");
        dataNode.set("node3.key1", "value");
        dataNode.set("node4.key1", "value");

        Set<String> nodesIterated = new HashSet<>(6);

        for (IDataNode node : dataNode) {

            nodesIterated.add(node.getNodePath());

            // the parent node should not be included in the iterations.
            assertNotEquals(dataNode.getNodePath(), node.getNodePath());
        }

        assertEquals(6, nodesIterated.size());
    }


    /**
     * Make sure that non key nodes do not return a key value.
     */
    @Test
    public void testNonKeyNodesNull() {

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
                dataNode.loadAsync().onStatus(new FutureSubscriber() {
                    @Override
                    public void on(FutureStatus status, @Nullable String message) {

                        _testLoadRunCount++;

                    }
                });
            }
        });

        thread.run();

        long timeout = System.currentTimeMillis() + 5000;

        while(_testLoadRunCount == 0 && System.currentTimeMillis() < timeout) {

            BukkitTester.heartBeat();

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

                dataNode.save().onStatus(new FutureSubscriber() {
                    @Override
                    public void on(FutureStatus status, @Nullable String message) {

                        _testSaveRunCount++;

                    }
                });
            }
        });

        thread.run();

        long timeout = System.currentTimeMillis() + 5000;

        while(_testSaveRunCount == 0 && System.currentTimeMillis() < timeout) {

            BukkitTester.heartBeat();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
        }

        assertEquals(1, _testSaveRunCount);
    }

    /**
     * Make sure auto save works.
     */
    @Test
    public void testAutoSave() {

        IDataNode dataNode = _generator.generateRoot();

        assertEquals(AutoSaveMode.DEFAULT, dataNode.getAutoSaveMode());

        dataNode.setAutoSaveMode(AutoSaveMode.ENABLED);
        assertEquals(AutoSaveMode.ENABLED, dataNode.getAutoSaveMode());

        assertEquals(false, dataNode.isDirty());


        dataNode.set("test", "value");

        assertEquals(true, dataNode.isDirty());

        BukkitTester.pause(50);

        assertEquals(false, dataNode.isDirty());
    }

    /**
     * Make sure {@link IDataNode#getDefaultAutoSaveMode} does not return {@link AutoSaveMode#DEFAULT}.
     */
    @Test
    public void testDefaultAutoSave() {
        IDataNode dataNode = _generator.generateRoot();

        assertNotEquals(AutoSaveMode.DEFAULT, dataNode.getDefaultAutoSaveMode());
    }

    /**
     * Make sure saving an {@link IDataNodeSerializable} instance works.
     */
    @Test
    public void testIDataNodeSerializable() {

        IDataNode dataNode = _generator.generateRoot();

        SerializeTest1 test1 = new SerializeTest1();
        dataNode.set("serialize", test1);
        assertEquals("test", dataNode.getString("serialize.test1"));
        assertNotNull(dataNode.getSerializable("serialize", SerializeTest1.class));

        // check nodes created from test 1 are cleared
        SerializeTest2 test2 = new SerializeTest2();
        dataNode.set("serialize", test2);
        assertEquals("test", dataNode.getString("serialize.test2"));
        assertEquals(null, dataNode.getString("serialize.test1"));
        assertNotNull(dataNode.getSerializable("serialize", SerializeTest2.class));
    }

    @Test
    public void testAllowEmptyKeyPathInSerializable() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        SerializeTest1 test1 = new SerializeTest1();
        dataNode.set("", test1);
        assertEquals("test", dataNode.getString("test1"));
        assertNotNull(dataNode.getSerializable("", SerializeTest1.class));
    }

    @Test
    public void testAllowEmptyKeyPathInGetNode() throws Exception {

        IDataNode dataNode = _generator.generateRoot();

        assertEquals(dataNode, dataNode.getNode(""));
    }

    public enum TestEnum {
        CONSTANT
    }

    private static class SerializeTest1 implements IDataNodeSerializable {

        private SerializeTest1() {}

        @Override
        public void serialize(IDataNode dataNode) {
            dataNode.set("test1", "test");
        }

        @Override
        public void deserialize(IDataNode dataNode) throws DeserializeException {
            assertEquals("test", dataNode.getString("test1"));
        }
    }

    private static class SerializeTest2 implements IDataNodeSerializable {

        private SerializeTest2() {}

        @Override
        public void serialize(IDataNode dataNode) {
            dataNode.set("test2", "test");
        }

        @Override
        public void deserialize(IDataNode dataNode) throws DeserializeException {
            assertEquals("test", dataNode.getString("test2"));
        }
    }

}
