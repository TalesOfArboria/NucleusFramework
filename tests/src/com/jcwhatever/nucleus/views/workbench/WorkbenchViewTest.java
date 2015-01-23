package com.jcwhatever.nucleus.views.workbench;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.views.AbstractViewTest;
import com.jcwhatever.nucleus.views.View;

import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Tests {@link WorkbenchView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class WorkbenchViewTest extends AbstractViewTest {

    public WorkbenchViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new WorkbenchView(plugin);
            }
        });
    }

    /**
     * Make sure correct inventory type is returned.
     */
    @Test
    public void testGetInventoryType() throws Exception {

        WorkbenchView view = new WorkbenchView(BukkitTester.mockPlugin("dummy"));

        assertEquals(InventoryType.CRAFTING, view.getInventoryType());
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return null;
    }
}