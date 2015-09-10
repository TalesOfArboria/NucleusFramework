package com.jcwhatever.nucleus.views.menu;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.views.ViewSession;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link MenuItem}.
 */
public class MenuItemTest {

    Player player = BukkitTester.login("dummy");
    Plugin plugin = BukkitTester.mockPlugin("dummy");

    boolean _isOnClickRun = false;

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Setup each test.
     */
    @Before
    public void before() {
        BukkitTester.pause(5);
    }

    /**
     * Test tear down.
     */
    @After
    public void after() {
        // ensure a view session from a previous test is disposed.
        ViewSession s = ViewSession.getCurrent(player);
        if (s != null)
            s.dispose();

        // test the test: we want to start with the players inventory closed.
        assertEquals(null, player.getOpenInventory());
    }

    /**
     * Make sure the correct slot is returned.
     */
    @Test
    public void testGetSlot() throws Exception {
        MenuItem menuItem = new MenuItemBuilder(Material.STONE).build(10);

        assertEquals(10, menuItem.getSlot());
    }

    /**
     * Make sure the correct title is returned.
     */
    @Test
    public void testGetTitle() throws Exception {
        MenuItem menuItem = new MenuItemBuilder(Material.STONE).title("test").build(0);

        assertEquals("test", menuItem.getTitle());
    }

    /**
     * Make sure the correct description is returned.
     */
    @Test
    public void testGetDescription() throws Exception {
        MenuItem menuItem = new MenuItemBuilder(Material.STONE).description("test").build(0);

        assertEquals("test", menuItem.getDescription());
    }

    /**
     * Make sure the title is set properly.
     */
    @Test
    public void testSetTitle() throws Exception {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).title("test").build(0);

        menuItem.setTitle("test2");

        assertEquals("test2", menuItem.getTitle());

    }

    /**
     * Make sure the description is set properly.
     */
    @Test
    public void testSetDescription() throws Exception {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).description("test").build(0);

        menuItem.setDescription("test2");

        assertEquals("test2", menuItem.getDescription());
    }

    /**
     * Make sure setting the visibility in an inventory view
     * works correctly.
     */
    @Test
    public void testSetVisible() throws Exception {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).title("test").build(0);

        MockMenuView view = new MockMenuView(plugin, null);

        ViewSession session = ViewSession.get(player, null);

        session.next(view);
        BukkitTester.pause(2);

        menuItem.setVisible(view, true);

        // item is added to slot 0 of inventory view.
        assertEquals(menuItem, view.getInventoryView().getItem(0));
        assertEquals(true, menuItem.isVisible(view));

        menuItem.setVisible(view, false);

        // item is removed from slot 0 of inventory view.
        assertEquals(null, view.getInventoryView().getItem(0));
        assertEquals(false, menuItem.isVisible(view));
    }

    /**
     * Make sure the on-click callbacks are run when the menu item
     * is clicked..
     */
    @Test
    public void testOnClick() throws Exception {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE)
                .title("test")
                .onClick(new Runnable() {
                    @Override
                    public void run() {
                        _isOnClickRun = true;
                    }
                })
                .build(0);

        MockMenuView view = new MockMenuView(plugin, null);
        ViewSession session = ViewSession.get(player, null);

        // open a menu view
        session.next(view);
        BukkitTester.pause(5);

        // set the item in the menu view
        menuItem.setVisible(view, true);
        BukkitTester.pause(5);

        // click the menu item
        BukkitTester.viewClick(player,
                SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        BukkitTester.pause(5);

        // make sure the items on click runnables ran
        assertEquals(true, _isOnClickRun);
    }
}