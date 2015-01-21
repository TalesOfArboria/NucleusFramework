package com.jcwhatever.nucleus.views.anvil;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.bukkit.v1_8_R1.MockBlock;
import com.jcwhatever.nucleus.storage.MemoryDataNode;
import com.jcwhatever.nucleus.utils.items.ItemFilterManager;
import com.jcwhatever.nucleus.views.AbstractViewTest;
import com.jcwhatever.nucleus.views.View;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Test for {@link FilteredAnvilView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class FilteredAnvilViewTest extends AbstractViewTest {

    static Plugin plugin = BukkitTest.mockPlugin("dummy");
    static ItemFilterManager manager = new ItemFilterManager(plugin, new MemoryDataNode(plugin));

    public FilteredAnvilViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new FilteredAnvilView(plugin, manager);
            }
        });
    }

    /**
     * Make sure correct inventory type is returned.
     */
    @Test
    public void testGetInventoryType() throws Exception {

        FilteredAnvilView view = new FilteredAnvilView(plugin, manager);

        assertEquals(InventoryType.ANVIL, view.getInventoryType());
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return new MockBlock(BukkitTest.world("world"), Material.ANVIL, 0, 0, 0);
    }

}