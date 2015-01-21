package com.jcwhatever.nucleus.views.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.nucleus.views.AbstractViewTest;
import com.jcwhatever.nucleus.views.View;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Test for {@link MenuView} using mock implementation {@link MockMenuView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class MenuViewTest extends AbstractViewTest {

    /**
     * Constructor.
     */
    public MenuViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new MockMenuView(plugin, null);
            }
        });
    }

    /**
     * Make sure {@code #onItemSelect} is invoked when a menu item is clicked
     * and that the correct {@code MenuItem} is passed into the invoked method.
     */
    @Test
    public void testOnItemSelected() {
        MockMenuView view = new MockMenuView(plugin, null);

        _session.next(view);
        BukkitTest.pause(2);

        MenuItem menuItem = new MenuItemBuilder(Material.WOOD).build(0);
        menuItem.setVisible(view, true);

        // select/click the menu item
        BukkitTest.viewClick(player,
                SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ALL);

        BukkitTest.pause(2);

        // check "onItemSelected" is invoked with correct menu item
        assertEquals(menuItem, view.selected);
    }

    /**
     * Make sure the "onShow" method is invoked when the
     * menu view is shown.
     */
    @Test
    public void testOnShow() {
        MockMenuView view = new MockMenuView(plugin, null);

        _session.next(view);
        BukkitTest.pause(2);

        // check "onShow" is invoked
        assertTrue(view.openReason != null);
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return null;
    }
}
