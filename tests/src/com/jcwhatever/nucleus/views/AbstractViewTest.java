package com.jcwhatever.nucleus.views;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Basic abstract test for a {@link View} implementation.
 */
public abstract class AbstractViewTest {

    protected Plugin plugin = BukkitTester.mockPlugin("dummy");
    protected Player player = BukkitTester.login("dummy");
    protected ViewSession _session;
    protected final IViewGenerator _generator;

    public interface IViewGenerator {
        View generate(Plugin plugin);
    }

    /**
     * Constructor.
     *
     * @param generator  An abject to generate new {@code View} instances for testing.
     */
    public AbstractViewTest(IViewGenerator generator) {
        _generator = generator;
    }

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

        // ensure a view session from a previous test is disposed.
        ViewSession s = ViewSession.getCurrent(player);
        if (s != null)
            s.dispose();

        // start a new view session
        _session = ViewSession.get(player, getSourceBlock());

        // test the test: we want to start with the players inventory closed.
        assertEquals(null, player.getOpenInventory());
    }

    /**
     * Make sure the plugin returned by the view is the plugin
     * specified during its construction.
     */
    @Test
    public void testGetPlugin() {

        Plugin plugin = BukkitTester.mockPlugin("dummy");

        View view = _generator.generate(plugin);

        assertEquals(plugin, view.getPlugin());
    }

    /**
     * Make sure the player returned by the view is the player
     * in the session the view has been assigned to.
     */
    @Test
    public void testGetPlayer() {

        View view = _generator.generate(plugin);

        _session.next(view);

        BukkitTester.pause(5);

        assertEquals(player, view.getPlayer());
    }

    /**
     * Make sure the view session returned by the view is the
     * view session it was assigned to.
     */
    @Test
    public void testGetViewSession() {

        View view = _generator.generate(plugin);

        _session.next(view);

        assertEquals(_session, view.getViewSession());
    }

    /**
     * Make sure there are no problems when showing the view to a player.
     */
    @Test
    public void testOpen() {

        View view = _generator.generate(plugin);

        _session.next(view);
        BukkitTester.pause(2);

        assertEquals(view.getInventoryView(), player.getOpenInventory());
    }

    /**
     * Invoked to get a source block when creating a new view session.
     */
    @Nullable
    protected abstract Block getSourceBlock();
}