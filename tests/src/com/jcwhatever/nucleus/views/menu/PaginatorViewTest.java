package com.jcwhatever.nucleus.views.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.collections.ArrayListPaginator;
import com.jcwhatever.nucleus.mixins.IPaginator;
import com.jcwhatever.nucleus.mixins.IPaginator.PageStartIndex;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.views.View;
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
 * Tests {@link PaginatorView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class PaginatorViewTest extends AbstractViewTest {

    public PaginatorViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {

                IPaginator paginator = new ArrayListPaginator(PageStartIndex.ONE, 9);

                return new PaginatorView(plugin, paginator, null);
            }
        });
    }

    /**
     * Make sure paginator inventory type is {@code InventoryType.CHEST}.
     */
    @Test
    public void testGetInventoryType() throws Exception {

        IPaginator paginator = new ArrayListPaginator(PageStartIndex.ONE, 9);

        PaginatorView view = new PaginatorView(plugin, paginator, null);

        assertEquals(InventoryType.CHEST, view.getInventoryType());
    }

    /**
     * Make sure selecting a page works properly.
     */
    @Test
    public void testSelectPage() {

        ArrayListPaginator<String> paginator = new ArrayListPaginator<String>(PageStartIndex.ONE, 9);

        // add enough to produce 2 pages
        paginator.addAll(ArrayUtils.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"));

        PaginatorView view = new PaginatorView(plugin, paginator, null);

        // show the paginator to the player
        _session.next(view);
        BukkitTester.pause(5);

        // ensure that page 2 items are shown in the view
        assertTrue(view.getInventoryView().getItem(0) != null);
        assertTrue(view.getInventoryView().getItem(1) != null);
        assertTrue(view.getInventoryView().getItem(2) == null);

        // click on inventory slot index 1 (page 2)
        BukkitTester.viewClick(player,
                SlotType.CONTAINER, 1, ClickType.LEFT, InventoryAction.PICKUP_ALL);

        BukkitTester.pause(5);

        // make sure the paginator reports that page 2 was selected.
        assertEquals(2, view.getSelectedPage());

        // make sure paginator closes itself when page is selected
        assertEquals(null, _session.getCurrentView());
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return null;
    }
}