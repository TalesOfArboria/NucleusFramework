package com.jcwhatever.nucleus.utils.signs;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.v1_8_R3.MockWorld;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.managed.signs.ISignManager;
import com.jcwhatever.nucleus.managed.signs.SignHandler;

import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.Plugin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link SignHandler}.
 */
public class SignHandlerTest {

    static Plugin _plugin;
    static MockSignHandler _signHandler;
    static ISignManager _signManager;

    MockWorld _world = BukkitTester.world("signHandlerWorld");
    Player _player = BukkitTester.login("dummy");

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
        BukkitTester.pause(10);
        _plugin = BukkitTester.mockPlugin("Plugin");
        _signHandler = new MockSignHandler(_plugin);
        _signManager = Nucleus.getSignManager();
        _signManager.registerHandler(_signHandler);
    }

    @AfterClass
    public static void deInit() {
        _signManager.unregisterHandler(_signHandler.getName());
    }

    /**
     * Make sure {@link #getTitle} returns the correct value.
     */
    @Test
    public void testGetDisplayName() throws Exception {

        assertEquals("Sign Name", _signHandler.getDisplayName());
    }

    /**
     * Make sure sign change event is received by the handler.
     */
    @Test
    public void testOnSignChange() throws Exception {

        // test sign change not handled for unmanaged sign.
        _world.placeWallSign(_player, 2, 2, 2, "line1", "line2", "line3", "line4");
        assertEquals(0, _signHandler.signChangeCount);

        // test sign change handled for managed sign.
        _world.placeWallSign(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");
        assertEquals(1, _signHandler.signChangeCount);
        assertEquals(_player, _signHandler.lastChangePlayer);
        assertEquals(_signHandler.getDisplayName(), _signHandler.lastChangeContainer.getLine(0));
        assertEquals("line2", _signHandler.lastChangeContainer.getLine(1));
        assertEquals("line3", _signHandler.lastChangeContainer.getLine(2));
        assertEquals("line4", _signHandler.lastChangeContainer.getLine(3));
    }

    /**
     * Make sure sign click event is received by the handler.
     */
    @Test
    public void testOnSignClick() throws Exception {

        // test sign click not handled for unmanaged sign.
        _world.placeWallSign(_player, 2, 2, 2, "line1", "line2", "line3", "line4");

        BukkitTester.blockClick(_player, Action.RIGHT_CLICK_BLOCK, _world.getBlockAt(2, 2, 2), BlockFace.SOUTH);
        assertEquals(0, _signHandler.signClickCount);

        // test sign click handled for managed sign.
        _world.placeWallSign(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");

        BukkitTester.blockClick(_player, Action.RIGHT_CLICK_BLOCK, _world.getBlockAt(0, 0, 0), BlockFace.SOUTH);
        assertEquals(1, _signHandler.signClickCount);
        assertEquals(_player, _signHandler.lastClickPlayer);
        assertEquals(_signHandler.getDisplayName(), _signHandler.lastClickContainer.getLine(0));
        assertEquals("line2", _signHandler.lastClickContainer.getLine(1));
        assertEquals("line3", _signHandler.lastClickContainer.getLine(2));
        assertEquals("line4", _signHandler.lastClickContainer.getLine(3));
    }

    /**
     * Make sure the sign break event is received by the handler.
     */
    @Test
    public void testOnSignBreak() throws Exception {

        _player.setGameMode(GameMode.CREATIVE);

        // test sign break not handled for unmanaged sign.
        _world.placeWallSign(_player, 2, 2, 2, "line1", "line2", "line3", "line4");

        _world.breakBlock(_player, 2, 2, 2);
        assertEquals(0, _signHandler.signBreakCount);

        // test sign change handled for managed sign.
        _world.placeWallSign(_player, 0, 0, 0, _signHandler.getName(), "line2", "line3", "line4");

        _world.breakBlock(_player, 0, 0, 0);
        assertEquals(1, _signHandler.signBreakCount);
        assertEquals(_player, _signHandler.lastBreakPlayer);
        assertEquals(_signHandler.getDisplayName(), _signHandler.lastBreakContainer.getLine(0));
        assertEquals("line2", _signHandler.lastBreakContainer.getLine(1));
        assertEquals("line3", _signHandler.lastBreakContainer.getLine(2));
        assertEquals("line4", _signHandler.lastBreakContainer.getLine(3));
    }
}