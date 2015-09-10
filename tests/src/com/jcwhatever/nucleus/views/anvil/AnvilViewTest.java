package com.jcwhatever.nucleus.views.anvil;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.v1_8_R3.blocks.MockBlock;
import com.jcwhatever.nucleus.views.AbstractViewTest;
import com.jcwhatever.nucleus.views.View;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Test for {@link AnvilView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class AnvilViewTest extends AbstractViewTest {

    /**
     * Constructor.
     */
    public AnvilViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new AnvilView(plugin);
            }
        });
    }

    /**
     * Make sure the correct inventory type is returned.
     */
    @Test
    public void testGetInventoryType() throws Exception {

        AnvilView view = new AnvilView(BukkitTester.mockPlugin("dummy"));

        assertEquals(InventoryType.ANVIL, view.getInventoryType());
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return new MockBlock(BukkitTester.world("world"), Material.ANVIL, 0, 0, 0);
    }
}