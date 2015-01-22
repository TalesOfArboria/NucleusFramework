package com.jcwhatever.nucleus.providers.bankitems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Abstract test for {@link IBankItem} implementations.
 */
public abstract class IBankItemTest {

    protected abstract IBankItem getBankItem(UUID id, ItemStack itemStack, int amount);

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Make sure the {@code BankItem} returns the correct ID.
     */
    @Test
    public void testGetId() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 4);

        assertEquals(id, bankItem.getId());
    }

    /**
     * Make sure {@code #isRootItem} returns the correct value.
     */
    @Test
    public void testIsRootItem() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 4);

        assertEquals(true, bankItem.isRootItem());

        List<IBankItem> items = bankItem.getPage(1);
        for (IBankItem subItem : items) {
            assertTrue(subItem != bankItem);
            assertEquals(false, subItem.isRootItem());
        }
    }

    /**
     * Make sure {@code #getRootItem} returns the correct value.
     */
    @Test
    public void testGetRootItem() {
        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 4);

        assertEquals(bankItem, bankItem.getRootItem());

        List<IBankItem> items = bankItem.getPage(1);
        for (IBankItem subItem : items) {
            assertTrue(subItem != bankItem);
            assertEquals(bankItem, subItem.getRootItem());
        }
    }

    /**
     * Make sure {@code #getType} returns the correct value.
     */
    @Test
    public void testGetType() {
        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 4);

        assertEquals(Material.WOOD, bankItem.getType());
    }

    /**
     * Make sure {@code #getAmount} returns the correct amount.
     */
    @Test
    public void testGetAmount() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        assertEquals(128, bankItem.getAmount());

        List<IBankItem> items = bankItem.getPage(1);
        for (IBankItem subItem : items) {
            assertEquals(64, subItem.getAmount());
        }
    }

    /**
     * Make sure {@code #getRootAmount} returns the correct value.
     */
    @Test
    public void testGetRootAmount() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        assertEquals(128, bankItem.getRootAmount());

        List<IBankItem> items = bankItem.getPage(1);
        for (IBankItem subItem : items) {
            assertEquals(128, subItem.getRootAmount());
        }
    }

    /**
     * Make sure {@code #getMaxStackSize} returns the correct value.
     */
    @Test
    public void testGetMaxStackSize() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        assertEquals(Material.WOOD.getMaxStackSize(), bankItem.getMaxStackSize());

        stack = new ItemStack(Material.IRON_SWORD);
        bankItem = getBankItem(id, stack, 128);

        assertEquals(Material.IRON_SWORD.getMaxStackSize(), bankItem.getMaxStackSize());
    }

    /**
     * Make sure {@code #getTotalStacks} returns the correct amount.
     */
    @Test
    public void testGetTotalStacks() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 130);

        assertEquals(3, bankItem.getTotalStacks());

        stack = new ItemStack(Material.IRON_SWORD);
        bankItem = getBankItem(id, stack, 128);

        assertEquals(128, bankItem.getTotalStacks());
    }

    /**
     * Make sure {@code #toItemStack} works correctly.
     */
    @Test
    public void testToItemStack() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 130);

        ItemStack itemStack = bankItem.toItemStack(10);

        assertEquals(Material.WOOD, itemStack.getType());
        assertEquals(10, itemStack.getAmount());
    }

    /**
     * Make sure {@code #getTotalPages} works correctly.
     */
    @Test
    public void testGetTotalPages() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 130);

        bankItem.setItemsPerPage(1);
        assertEquals(3, bankItem.getTotalPages());

        bankItem.setItemsPerPage(2);
        assertEquals(2, bankItem.getTotalPages());

        bankItem.setItemsPerPage(3);
        assertEquals(1, bankItem.getTotalPages());
    }

    /**
     * Make sure {@code getPage} works correctly.
     */
    @Test
    public void testGetPage() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        bankItem.setItemsPerPage(2);
        List<IBankItem> items = bankItem.getPage(1);
        assertEquals(2, items.size());


        bankItem.setItemsPerPage(1);
        items = bankItem.getPage(1);
        assertEquals(1, items.size());
    }

    /**
     * Make sure {@code #getItems} works correctly.
     */
    @Test
    public void testGetItems() {
        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        List<IBankItem> items = bankItem.getItems();

        assertEquals(2, items.size());
    }

    /**
     * Make sure {@code #iterator} works correctly.
     */
    @Test
    public void testIterator() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        Iterator<IBankItem> iterator = bankItem.iterator();

        int count = 0;

        while (iterator.hasNext()) {
            IBankItem item = iterator.next();

            assertEquals(64, item.getAmount());
            count++;
        }

        // should be 2 items iterated. (128total / 64maxStackSize = 2 stacks)
        assertEquals(2, count);

        iterator = bankItem.iterator();

        try {
            while (iterator.hasNext()) {
                iterator.next();

                iterator.remove();
            }
            throw new AssertionError("UnsupportedOperationException expected.");
        }
        catch (UnsupportedOperationException ignore) {}
    }

    /**
     * Make sure {@code #iterator(int)} works correctly.
     */
    @Test
    public void testIterator1() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);

        IBankItem bankItem = getBankItem(id, stack, 128);

        // set only one item in a page
        bankItem.setItemsPerPage(1);

        // get iterator for page 1.
        Iterator<IBankItem> iterator = bankItem.iterator(1);

        int count = 0;

        while (iterator.hasNext()) {
            IBankItem item = iterator.next();

            assertEquals(64, item.getAmount());
            count++;
        }

        // make sure there was only one item in iterator
        assertEquals(1, count);

        iterator = bankItem.iterator(1);

        try {
            while (iterator.hasNext()) {
                iterator.next();

                iterator.remove();
            }
            throw new AssertionError("UnsupportedOperationException expected.");
        }
        catch (UnsupportedOperationException ignore) {}
    }

}