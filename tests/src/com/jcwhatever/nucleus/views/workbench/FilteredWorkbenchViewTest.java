package com.jcwhatever.nucleus.views.workbench;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.storage.MemoryDataNode;
import com.jcwhatever.nucleus.utils.items.ItemFilterManager;
import com.jcwhatever.nucleus.views.AbstractViewTest;
import com.jcwhatever.nucleus.views.View;

import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Tests {@link FilteredWorkbenchView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class FilteredWorkbenchViewTest extends AbstractViewTest {

    static Plugin plugin = BukkitTester.mockPlugin("dummy");
    static ItemFilterManager manager = new ItemFilterManager(plugin, new MemoryDataNode(plugin));

    public FilteredWorkbenchViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new FilteredWorkbenchView(plugin, manager);
            }
        });
    }

    /**
     * Make sure the correct inventory type is returned.
     */
    @Test
    public void testGetInventoryType() throws Exception {

        FilteredWorkbenchView view = new FilteredWorkbenchView(plugin, manager);

        assertEquals(InventoryType.CRAFTING, view.getInventoryType());
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return null;
    }
}