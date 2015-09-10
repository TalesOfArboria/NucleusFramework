package com.jcwhatever.nucleus.utils.signs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.v1_8_R3.MockWorld;
import com.jcwhatever.v1_8_R3.blocks.MockBlock;
import com.jcwhatever.v1_8_R3.blocks.MockSign;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.managed.signs.ISignManager;
import com.jcwhatever.nucleus.managed.signs.SignHandler;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

/**
 * Tests Nucleus {@link ISignManager} implementation.
 */
public class SignManagerTest {

    Plugin _plugin = BukkitTester.mockPlugin("Plugin");
    MockWorld _world = BukkitTester.world("signManagerWorld");
    Player _player = BukkitTester.login("signManagerPlayer");
    MockSignHandler _signHandler = new MockSignHandler(_plugin);

    ISignManager _signManager;

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    @Before
    public void before() {
        _signManager = Nucleus.getSignManager();
        _signManager.registerHandler(_signHandler);
        _signHandler.reset();
    }

    @After
    public void after() {
        _signManager.unregisterHandler(_signHandler.getName());
    }

    /**
     * Make sure registering and unregistering a sign type works
     * correctly.
     */
    @Test
    public void testRegisterSignType() throws Exception {

        Collection<SignHandler> signHandlers = _signManager.getSignHandlers();
        assertEquals(1, signHandlers.size());
    }

    /**
     * Make sure {@link ISignManager#getSignHandler} returns the correct value.
     */
    @Test
    public void testGetSignHandler() throws Exception {

        SignHandler signHandler = _signManager.getSignHandler(_signHandler.getName());

        assertEquals(_signHandler, signHandler);
    }

    /**
     * Make sure {@link ISignManager#getSigns} returns the correct value.
     */
    @Test
    public void testGetSigns() throws Exception {

        // baseline test: should be no signs
        Collection<ISignContainer> signs = _signManager.getSigns(_signHandler.getName());
        assertEquals(0, signs.size());

        // place a new sign
        _world.placeSignPost(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");

        // should be 1 sign
        signs = _signManager.getSigns(_signHandler.getName());
        assertEquals(1, signs.size());
    }

    /**
     * Make sure {@link ISignManager#getSavedLines} returns the correct value.
     */
    @Test
    public void testGetSavedLines() throws Exception {

        // place a new sign
        _world.placeSignPost(_player, 10, 10, 10, _signHandler.getName(), "line2", "line3", "line4");

        MockBlock signBlock = _world.getBlockAt(10, 10, 10);
        String[] lines = _signManager.getSavedLines((Sign)signBlock.getState());

        // validate result
        assertTrue(lines != null);
        assertEquals(_signHandler.getDisplayName(), lines[0]);
        assertEquals("line2", lines[1]);
        assertEquals("line3", lines[2]);
        assertEquals("line4", lines[3]);
    }

    /**
     * Make sure {@link ISignManager#restoreSign} works correctly.
     */
    @Test
    public void testRestoreSign() throws Exception {

        // place a new sign
        _world.placeSignPost(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");

        MockBlock signBlock = _world.getBlockAt(0, 0, 0);

        // delete new sign
        signBlock.setType(Material.AIR);
        assertEquals(Material.AIR, signBlock.getType());

        // restore sign
        _signManager.restoreSign(_signHandler.getName(), new Location(_world, 0, 0, 0));
        BukkitTester.pause(2);

        // validate sign restored
        assertEquals(Material.SIGN_POST, signBlock.getType());
        MockSign state1 = (MockSign)signBlock.getState();

        assertEquals(_signHandler.getDisplayName(), state1.getLines()[0]);
        assertEquals("line2", state1.getLines()[1]);
        assertEquals("line3", state1.getLines()[2]);
        assertEquals("line4", state1.getLines()[3]);
    }

    /**
     * Make sure {@link ISignManager#restoreSigns} works correctly.
     */
    @Test
    public void testRestoreSigns() throws Exception {

        // place new signs
        _world.placeSignPost(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");
        _world.placeSignPost(_player, 1, 1, 1, _signHandler.getName(), "2line2", "2line3", "2line4");

        MockBlock signBlock1 = _world.getBlockAt(0, 0, 0);
        MockBlock signBlock2 = _world.getBlockAt(1, 1, 1);

        // delete new signs
        signBlock1.setType(Material.AIR);
        signBlock2.setType(Material.AIR);
        assertEquals(Material.AIR, signBlock1.getType());
        assertEquals(Material.AIR, signBlock2.getType());

        // restore all signs
        _signManager.restoreSigns(_signHandler.getName());
        BukkitTester.pause(2);

        // check sign1
        assertEquals(Material.SIGN_POST, signBlock1.getType());
        MockSign state1 = (MockSign)signBlock1.getState();

        assertEquals(_signHandler.getDisplayName(), state1.getLines()[0]);
        assertEquals("line2", state1.getLines()[1]);
        assertEquals("line3", state1.getLines()[2]);
        assertEquals("line4", state1.getLines()[3]);


        // check sign2
        assertEquals(Material.SIGN_POST, signBlock2.getType());
        MockSign state2 = (MockSign)signBlock2.getState();

        assertEquals(_signHandler.getDisplayName(), state2.getLines()[0]);
        assertEquals("2line2", state2.getLines()[1]);
        assertEquals("2line3", state2.getLines()[2]);
        assertEquals("2line4", state2.getLines()[3]);
    }

    /**
     * Make sure change sign event is handled properly when
     * player is in {@link GameMode#SURVIVAL}.
     */
    @Test
    public void testSignChangeSurvival() throws Exception {
        _player.setGameMode(GameMode.SURVIVAL);
        testSignBreak();
    }

    /**
     * Make sure change sign event is handled properly when
     * player is in {@link GameMode#CREATIVE}.
     */
    @Test
    public void testSignChangeCreative() throws Exception {
        _player.setGameMode(GameMode.CREATIVE);
        testSignChange();
    }

    private void testSignChange() {

        // place a new NON-handled sign
        _world.placeSignPost(_player, 4, 4, 4, "line1", "line2", "line3", "line4");
        // make sure the sign was not handled
        assertEquals(0, _signHandler.signChangeCount);

        // place a new handled sign
        _world.placeSignPost(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");
        // make sure the sign change was detected and handled.
        assertEquals(1, _signHandler.signChangeCount);
    }

    /**
     * Make sure sign click event is handled properly when player
     * is in {@link GameMode#SURVIVAL}.
     */
    @Test
    public void testSignClickSurvival() throws Exception {
        _player.setGameMode(GameMode.SURVIVAL);
        testSignClick();
    }

    /**
     * Make sure sign click event is handled properly when player
     * is in {@link GameMode#CREATIVE}.
     */
    @Test
    public void testSignClickCreative() throws Exception {
        _player.setGameMode(GameMode.CREATIVE);
        testSignClick();
    }

    private void testSignClick() throws Exception {

        // place a new handled sign
        _world.placeSignPost(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");
        // place a new NON-handled sign
        _world.placeSignPost(_player, 4, 4, 4, "line1", "line2", "line3", "line4");

        // click a non-sign block
        BukkitTester.blockClick(_player, Action.RIGHT_CLICK_BLOCK, _world.getBlockAt(2, 2, 2), BlockFace.SOUTH);
        // make sure the block break on a non sign was NOT handled.
        assertEquals(0, _signHandler.signClickCount);

        // click a non-handled sign
        BukkitTester.blockClick(_player, Action.RIGHT_CLICK_BLOCK, _world.getBlockAt(4, 4, 4), BlockFace.SOUTH);
        // make sure the sign click was NOT handled.
        assertEquals(0, _signHandler.signClickCount);

        // click sign
        BukkitTester.blockClick(_player, Action.RIGHT_CLICK_BLOCK, _world.getBlockAt(0, 0, 0), BlockFace.SOUTH);
        // make sure the sign click was detected and handled.
        assertEquals(1, _signHandler.signClickCount);
    }

    /**
     * Make sure sign break event is handled properly when
     * player is in {@link GameMode#SURVIVAL}.
     */
    @Test
    public void testSignBreakSurvival() throws Exception {
        _player.setGameMode(GameMode.SURVIVAL);
        testSignBreak();

        // make sure the sign break was not handled (cancelled).
        assertEquals(0, _signHandler.signBreakCount);
    }

    /**
     * Make sure sign break event is handled properly when
     * player is in {@link GameMode#CREATIVE}.
     */
    @Test
    public void testSignBreakCreative() throws Exception {
        _player.setGameMode(GameMode.CREATIVE);
        testSignBreak();

        // make sure the sign break was detected and handled.
        assertEquals(1, _signHandler.signBreakCount);
    }

    private void testSignBreak() {

        // place a new handled sign
        _world.placeSignPost(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");
        // place a new NON-handled sign
        _world.placeSignPost(_player, 4, 4, 4, "line1", "line2", "line3", "line4");

        // break a non-sign block
        _world.breakBlock(_player, 2, 2, 2);
        // make sure the block break was NOT handled.
        assertEquals(0, _signHandler.signBreakCount);

        // break a non-handled sign
        _world.breakBlock(_player, 4, 4, 4);
        // make sure the block break was NOT handled.
        assertEquals(0, _signHandler.signBreakCount);

        // break the handled sign
        _world.breakBlock(_player, 0, 0, 0);
    }
}