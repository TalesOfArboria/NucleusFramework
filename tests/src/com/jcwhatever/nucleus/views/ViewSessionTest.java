package com.jcwhatever.nucleus.views;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.bukkit.v1_8_R1.blocks.MockBlock;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.views.workbench.WorkbenchView;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link ViewSession}.
 */
public class ViewSessionTest {


    Plugin plugin = BukkitTester.mockPlugin("dummy");
    Player player = BukkitTester.login("dummy");
    ViewSession _session;

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
        _session = ViewSession.get(player, null);

        // test the test: we want to start with the players inventory closed.
        assertEquals(null, player.getOpenInventory());
    }

    @Test
    public void testPreviousWithNoView() {

        assertEquals(null, _session.getCurrentView());

        try {
            _session.previous();
            throw new AssertionError("IllegalStateException expected.");
        }
        catch (IllegalStateException ignore) {}
    }

    /**
     * Make sure the {@link ViewSession#next} method functions properly.
     */
    @Test
    public void testNext() {

        WorkbenchView view1 = new WorkbenchView(plugin);
        WorkbenchView view2 = new WorkbenchView(plugin);

        // baseline test: No views have been opened
        assertEquals(null, _session.getCurrentView());
        assertEquals(null, _session.getPrevView());
        assertEquals(null, _session.getNextView());

        // show view1 to player
        _session.next(view1);
        BukkitTester.pause(2);

        // make sure player sees view1
        assertEquals(view1.getInventoryView(), player.getOpenInventory());

        assertEquals(view1, _session.getCurrentView()); // make sure view1 is the current reported view
        assertEquals(null, _session.getPrevView());
        assertEquals(null, _session.getNextView());
        assertEquals(view1, _session.getLastView()); // make sure view1 is the reported last/final view

        // show view 2 to player
        _session.next(view2);
        BukkitTester.pause(2);

        assertEquals(view2, _session.getCurrentView()); // make sure view2 is the current reported view
        assertEquals(view1, _session.getPrevView()); // make sure view1 is the reported previous view
        assertEquals(null, _session.getNextView());
        assertEquals(view2, _session.getLastView()); // make sure view2 is the reported last/final view
    }

    /**
     * Make sure the {@link ViewSession#previous} method functions properly.
     */
    @Test
    public void testPrevious() {

        WorkbenchView view1 = new WorkbenchView(plugin);
        WorkbenchView view2 = new WorkbenchView(plugin);


        // open to view2
        _session.next(view1);
        _session.next(view2);
        BukkitTester.pause(4);

        // baseline test: Reported views should be correct
        assertEquals(view2, _session.getCurrentView());
        assertEquals(view1, _session.getPrevView());
        assertEquals(null, _session.getNextView());
        assertEquals(view2, _session.getLastView());

        // close view2 and show view1 to the player
        _session.previous();
        BukkitTester.pause(2);

        assertEquals(view1, _session.getCurrentView()); // make sure view1 is the current reported view
        assertEquals(null, _session.getPrevView());
        assertEquals(view2, _session.getNextView()); // make sure view2 is the reported next view
        assertEquals(view2, _session.getLastView()); // make sure view2 is the reported last/final view

        // close view1 (ends/disposes session)
        _session.previous();
        BukkitTester.pause(2);

        // session should be disposed from closing first view,
        // all values cleared.
        assertEquals(true, _session.isDisposed());
        assertEquals(null, _session.getCurrentView());
        assertEquals(null, _session.getPrevView());
        assertEquals(null, _session.getNextView());
    }

    /**
     * Make sure the {@link ViewSession#refresh} method functions properly.
     */
    @Test
    public void refresh() {

        WorkbenchView view1 = new WorkbenchView(plugin);

        // open first view
        _session.next(view1);
        BukkitTester.pause(2);

        // baseline test : Reported views should be correct
        assertEquals(view1, _session.getCurrentView());
        assertEquals(null, _session.getPrevView());
        assertEquals(null, _session.getNextView());
        assertEquals(view1, _session.getLastView());

        // refresh the view (Close and re-open)
        _session.refresh();
        BukkitTester.pause(2);

        // reported views should be the same as before
        assertEquals(view1, _session.getCurrentView());
        assertEquals(null, _session.getPrevView());
        assertEquals(null, _session.getNextView());
        assertEquals(view1, _session.getLastView());
    }

    /**
     * Make sure the source block returned by the session is the source block
     * provided when the session is first initialized.
     */
    @Test
    public void testSourceBlock() {

        // dispose the session generated by #before
        _session.dispose();

        Block block = new MockBlock(player.getWorld(), Material.ANVIL, 0, 0, 0);

        // start a new session using the anvil block.
        _session = ViewSession.get(player, block);

        assertEquals(block, _session.getSessionBlock());
    }
}