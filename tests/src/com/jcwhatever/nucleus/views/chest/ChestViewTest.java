package com.jcwhatever.nucleus.views.chest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.AbstractViewTest;

import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Test for {@link ChestView} using mock implementation {@link MockChestView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class ChestViewTest  extends AbstractViewTest {

    public ChestViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new MockChestView(plugin, null);
            }
        });
    }

    /**
     * Make sure the "onShow" method is invoked.
     */
    @Test
    public void testOnShow() {

        MockChestView view = new MockChestView(plugin, null);

        _session.next(view);
        BukkitTester.pause(2);

        // check that "onShow" was invoked.
        assertEquals(ViewOpenReason.FIRST, view.openReason);
    }

    /**
     * Make sure the "onItemsPickup" method is invoked when
     * items are picked up in the inventory view.
     */
    @Test
    public void testOnItemsPickup() {

        MockChestView view = new MockChestView(plugin, null);

        _session.next(view);
        BukkitTester.pause(2);

        BukkitTester.viewClick(player,
                SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ALL);

        BukkitTester.pause(2);

        // check "onItemsPickup" is invoked
        assertTrue(view.itemsPickup != null);
    }

    /**
     * Make sure the "onItemsPlaced" method is invoked when
     * items are placed in the inventory view.
     */
    @Test
    public void testOnItemsPlaced() {
        MockChestView view = new MockChestView(plugin, null);

        _session.next(view);
        BukkitTester.pause(2);

        BukkitTester.viewClick(player,
                SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PLACE_ALL);

        BukkitTester.pause(2);

        // check "onItemsPlaced" is invoked
        assertTrue(view.itemsPlaced != null);
    }

    /**
     * Make sure the "onItemsDropped" method is invoked when
     * items are dropped from the inventory view.
     */
    @Test
    public void testOnItemsDropped() {

        MockChestView view = new MockChestView(plugin, null);

        _session.next(view);
        BukkitTester.pause(2);

        BukkitTester.viewClick(player,
                SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.DROP_ALL_CURSOR);

        BukkitTester.pause(2);

        // check "onItemsDropped" is invoked
        assertTrue(view.itemsDropped != null);
    }

    /**
     * Make sure the correct inventory type is returned.
     */
    @Test
    public void testGetInventoryType() throws Exception {

        MockChestView view = new MockChestView(BukkitTester.mockPlugin("dummy"), null);

        assertEquals(InventoryType.CHEST, view.getInventoryType());
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return null;
    }
}