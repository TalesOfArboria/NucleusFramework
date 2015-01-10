package com.jcwhatever.nucleus.utils.inventory;

import com.jcwhatever.dummy.DummyInventory;
import com.jcwhatever.dummy.DummyServer;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InventorySnapshotTest {

    private ItemStack[] getInventoryStack() {
        return new ItemStack[] {
                new ItemStackBuilder(Material.WOOD).amount(5).build(),
                null,
                new ItemStackBuilder(Material.GLASS).amount(64).build(),
                new ItemStackBuilder(Material.STONE).amount(5).build(),
                new ItemStackBuilder(Material.GRASS).amount(32).build(),
                null,
                null,
                null,
                null
        };
    }

    private Inventory getInventory() {

        DummyInventory inventory = new DummyInventory(null, InventoryType.CHEST, 9);
        inventory.setContents(getInventoryStack());
        return inventory;
    }

    @BeforeClass
    public static void testStartup() {
        try {
            Bukkit.setServer(new DummyServer());
        }
        catch (UnsupportedOperationException ignore) {}
    }

    @Test
    public void testGetSlot() throws Exception {

        ItemStack[] contents = getInventoryStack();

        InventorySnapshot snapshot = new InventorySnapshot(getInventory());

        Assert.assertEquals(contents[0], snapshot.getSlot(0));
        Assert.assertEquals(contents[1], snapshot.getSlot(1));
        Assert.assertEquals(contents[2], snapshot.getSlot(2));
        Assert.assertEquals(contents[3], snapshot.getSlot(3));
        Assert.assertEquals(contents[4], snapshot.getSlot(4));
        Assert.assertEquals(contents[5], snapshot.getSlot(5));
        Assert.assertEquals(contents[6], snapshot.getSlot(6));
        Assert.assertEquals(contents[7], snapshot.getSlot(7));
        Assert.assertEquals(contents[8], snapshot.getSlot(8));
    }

    @Test
    public void testGetAmount() throws Exception {
        ItemStack[] contents = getInventoryStack();

        InventorySnapshot snapshot = new InventorySnapshot(getInventory());

        Assert.assertEquals(5, snapshot.getAmount(contents[0]));
        Assert.assertEquals(64, snapshot.getAmount(contents[2]));
        Assert.assertEquals(5, snapshot.getAmount(contents[3]));
        Assert.assertEquals(32, snapshot.getAmount(contents[4]));
    }


    @Test
    public void testGetItemStacks() throws Exception {

        InventorySnapshot snapshot = new InventorySnapshot(getInventory());

        ItemStack[] expected = ArrayUtils.removeNull(getInventoryStack());

        Assert.assertArrayEquals(expected, snapshot.getItemStacks());
    }

    @Test
    public void testGetSnapshot() throws Exception {

        InventorySnapshot snapshot = new InventorySnapshot(getInventory());

        ItemStack[] expected = getInventoryStack();

        Assert.assertArrayEquals(expected, snapshot.getSnapshot());
    }
}