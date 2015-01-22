package com.jcwhatever.nucleus.providers.bankitems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

/**
 * Abstract test for {@link IBankItemsAccount} implementations.
 */
public abstract class IBankItemsAccountTest {

    protected abstract IBankItemsAccount getAccount(UUID ownerId);

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Make sure {@code #getOwnerId} returns the correct value.
     */
    @Test
    public void testGetOwnerId() {

        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        assertEquals(ownerId, account.getOwnerId());
    }

    /**
     * Make sure {@code #getBank} returns the correct value.
     */
    @Test
    public void testGetBank() {

        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        assertEquals(null, account.getBank());
    }

    /**
     * Make sure {@code #getBalance(ItemStack)} works correctly.
     */
    @Test
    public void testGetBalance() {

        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(itemStack, 20);

        assertEquals(20, account.getBalance(itemStack));
    }

    /**
     * Make sure {@code #getBalance(Material)} works correctly.
     */
    @Test
    public void testGetBalance1() throws Exception {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(itemStack, 20);

        assertEquals(20, account.getBalance(Material.WOOD));
    }

    /**
     * Make sure {@code #getBalance(MaterialData)} works correctly.
     */
    @Test
    public void testGetBalance2() throws Exception {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(itemStack, 20);

        assertEquals(20, account.getBalance(new MaterialData(Material.WOOD)));
    }

    /**
     * Make sure {@code #getBalance()} works correctly.
     */
    @Test
    public void testGetBalance3() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(itemStack, 20);

        assertEquals(20, account.getBalance());
    }

    /**
     * Make sure {@code #deposit(ItemStack)} works correctly.
     */
    @Test
    public void testDeposit() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(itemStack);

        assertEquals(10, account.getBalance(itemStack));
    }

    /**
     * Make sure {@code #deposit(ItemStack, int)} works correctly.
     */
    @Test
    public void testDeposit1() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(itemStack, 30);

        assertEquals(30, account.getBalance(itemStack));
    }

    /**
     * Make sure {@code #deposit(Material, int)} works correctly.
     */
    @Test
    public void testDeposit2() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(Material.WOOD, 30);

        assertEquals(30, account.getBalance(itemStack));
    }

    /**
     * Make sure {@code #deposit(MaterialData, int)} works correctly.
     */
    @Test
    public void testDeposit3() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(10).build();

        account.deposit(new MaterialData(Material.WOOD), 30);

        assertEquals(30, account.getBalance(itemStack));
    }

    /**
     * Make sure {@code #withdraw()} works correctly.
     */
    @Test
    public void testWithdraw() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        List<ItemStack> withdrawn = account.withdraw();

        assertEquals(1, withdrawn.size());
        assertEquals(30, withdrawn.get(0).getAmount());

        withdrawn = account.withdraw();

        assertEquals(0, withdrawn.size());
    }

    /**
     * Make sure {@code #withdraw(Material)} works correctly.
     */
    @Test
    public void testWithdraw1() throws InsufficientItemsException {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        List<ItemStack> withdrawn = account.withdraw(Material.WOOD);

        assertEquals(1, withdrawn.size());
        assertEquals(30, withdrawn.get(0).getAmount());

        withdrawn = account.withdraw(Material.WOOD);

        assertEquals(0, withdrawn.size());
    }

    /**
     * Make sure {@code #withdraw(MaterialData)} works correctly.
     */
    @Test
    public void testWithdraw2() throws InsufficientItemsException {

        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        List<ItemStack> withdrawn = account.withdraw(new MaterialData(Material.WOOD));

        assertEquals(1, withdrawn.size());
        assertEquals(30, withdrawn.get(0).getAmount());

        withdrawn = account.withdraw(new MaterialData(Material.WOOD));

        assertEquals(0, withdrawn.size());

    }

    /**
     * Make sure {@code #withdraw(Material, int)} works correctly.
     */
    @Test
    public void testWithdraw3() throws InsufficientItemsException {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        List<ItemStack> withdrawn = account.withdraw(Material.WOOD, 15);

        assertEquals(1, withdrawn.size());
        assertEquals(15, withdrawn.get(0).getAmount());

        try {
            account.withdraw(Material.WOOD, 100);
            throw new AssertionError("InsufficientItemsException expected.");
        }
        catch(InsufficientItemsException ignore) {}
    }

    /**
     * Make sure {@code #withdraw(MaterialData, int)} works properly.
     */
    @Test
    public void testWithdraw4() throws InsufficientItemsException {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        List<ItemStack> withdrawn = account.withdraw(new MaterialData(Material.WOOD), 15);

        assertEquals(1, withdrawn.size());
        assertEquals(15, withdrawn.get(0).getAmount());

        try {
            account.withdraw(new MaterialData(Material.WOOD), 100);
            throw new AssertionError("InsufficientItemsException expected.");
        }
        catch(InsufficientItemsException ignore) {}
    }

    /**
     * Make sure {@code withdraw(ItemStack, int)} works properly.
     */
    @Test
    public void testWithdraw5() throws InsufficientItemsException {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        ItemStack itemStack = new ItemStack(Material.WOOD);

        List<ItemStack> withdrawn = account.withdraw(itemStack, 15);

        assertEquals(1, withdrawn.size());
        assertEquals(15, withdrawn.get(0).getAmount());

        try {
            account.withdraw(itemStack, 100);
            throw new AssertionError("InsufficientItemsException expected.");
        }
        catch(InsufficientItemsException ignore) {}
    }

    /**
     * Make sure {@code #withdraw(ItemStack)} works correctly.
     */
    @Test
    public void testWithdraw6() throws Exception {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 30);

        ItemStack itemStack = new ItemStackBuilder(Material.WOOD).amount(15).build();

        List<ItemStack> withdrawn = account.withdraw(itemStack);

        assertEquals(1, withdrawn.size());
        assertEquals(15, withdrawn.get(0).getAmount());

        try {
            account.withdraw(itemStack);
            account.withdraw(itemStack);
            throw new AssertionError("InsufficientItemsException expected.");
        }
        catch(InsufficientItemsException ignore) {}
    }

    /**
     * Make sure {@code #getItem} works correctly.
     */
    @Test
    public void testGetItem() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 128);

        ItemStack wood = new ItemStack(Material.WOOD);

        IBankItem item = account.getItem(wood);
        assertTrue(item != null);
        assertEquals(Material.WOOD, item.getType());
        assertEquals(128, item.getAmount());


        ItemStack glass = new ItemStack(Material.GLASS);

        item = account.getItem(glass);
        assertTrue(item == null);
    }

    /**
     * Make sure {@code #getItems} works properly.
     */
    @Test
    public void testGetItems() {
        UUID ownerId = UUID.randomUUID();
        IBankItemsAccount account = getAccount(ownerId);

        account.deposit(Material.WOOD, 128);

        List<IBankItem> items = account.getItems();

        assertEquals(2, items.size());
    }
}