package com.jcwhatever.nucleus.views.menu;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.utils.MetaKey;

import org.bukkit.Material;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Tests {@link MenuItemBuilder}.
 */
public class MenuItemBuilderTest {

    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Make sure the correct title is set.
     */
    @Test
    public void testTitle() {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).title("test").build(0);
        assertEquals("test", menuItem.getTitle());
    }

    /**
     * Make sure the correct description is set.
     */
    @Test
    public void testDescription() {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).description("test").build(0);
        assertEquals("test", menuItem.getDescription());
    }

    /**
     * Make sure the correct amount is set.
     */
    @Test
    public void testAmount() {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).amount(10).build(0);
        assertEquals(10, menuItem.getAmount());
    }

    /**
     * Make sure the correct meta is set.
     */
    @Test
    public void testMeta() throws Exception {

        MetaKey<String> metaKey = new MetaKey<String>(String.class);

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).meta(metaKey, "val").build(0);
        assertEquals("val", menuItem.getMeta(metaKey));
    }

    /**
     * Make sure the correct on click runnable is set.
     */
    @Test
    public void testOnClick() throws Exception {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).onClick(runnable).build(0);
        List<Runnable> onClicks = menuItem.getOnClick();

        assertEquals(1, onClicks.size());
        assertEquals(runnable, onClicks.get(0));
    }

    /**
     * Make sure build sets the correct slot.
     */
    @Test
    public void testBuild() throws Exception {

        MenuItem menuItem = new MenuItemBuilder(Material.STONE).build(2);
        assertEquals(2, menuItem.getSlot());
    }

}