package com.jcwhatever.nucleus.jail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.storage.MemoryDataNode;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * Tests {@link JailSession}.
 */
public class JailSessionTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");
    private Player _player = BukkitTester.login("dummy");
    private World _world = BukkitTester.world("world");
    private Jail _jail;

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
        BukkitTester.pause(2);
        Nucleus.getJailManager().release(_player.getUniqueId());
        BukkitTester.pause(2);

        _jail = new Jail(_plugin, "testJail", new MemoryDataNode(_plugin));
        _jail.getJailBounds().setCoords(new Location(_world, 0, 0, 0), new Location(_world, 100, 100, 100));
    }

    @After
    public void after() {
        Nucleus.getJailManager().release(_player.getUniqueId());
        BukkitTester.pause(2);
        _jail.dispose();
    }

    /**
     * Make sure the {@link JailSession} returns the correct {@link Jail}.
     */
    @Test
    public void testGetJail() {

        JailSession session = _jail.imprison(_player, 10, TimeScale.TICKS);

        assertTrue(session != null);

        assertEquals(_jail, session.getJail());
    }

    /**
     * Make sure the jail session returns the correct player Id.
     */
    @Test
    public void testGetPlayerId() {

        JailSession session = _jail.imprison(_player, 10, TimeScale.TICKS);

        assertTrue(session != null);

        assertEquals(_player.getUniqueId(), session.getPlayerId());
    }

    /**
     * Make sure the jail session returns the correct expiration.
     */
    @Test
    public void testGetExpiration() throws Exception {

        Date expires = new Date(System.currentTimeMillis() + 1000);

        JailSession session = _jail.imprison(_player, expires);

        assertTrue(session != null);

        assertEquals(expires, session.getExpiration());
    }

    /**
     * Make sure the {@link #isReleased} method returns the correct value.
     */
    @Test
    public void testIsReleased() throws Exception {

        JailSession session = _jail.imprison(_player, 10, TimeScale.TICKS);

        assertTrue(session != null);

        assertEquals(false, session.isReleased());
        assertEquals(true, Nucleus.getJailManager().isPrisoner(_player.getUniqueId()));

        BukkitTester.pause(35);

        assertEquals(true, session.isReleased());
        assertEquals(false, Nucleus.getJailManager().isPrisoner(_player.getUniqueId()));
    }

    /**
     * Make sure the {@link #isExpired} method returns the correct value.
     */
    @Test
    public void testIsExpired() throws Exception {

        JailSession session = _jail.imprison(_player, 10, TimeScale.TICKS);

        assertTrue(session != null);

        assertEquals(false, session.isExpired());
        assertEquals(true, Nucleus.getJailManager().isPrisoner(_player.getUniqueId()));

        BukkitTester.pause(35);

        assertEquals(true, session.isExpired());
        assertEquals(false, Nucleus.getJailManager().isPrisoner(_player.getUniqueId()));
    }

    /**
     * Make sure the {@link release} method works properly.
     */
    @Test
    public void testRelease() throws Exception {
        JailSession session = _jail.imprison(_player, 10, TimeScale.MINUTES);

        assertTrue(session != null);

        assertEquals(false, session.isReleased());
        assertEquals(true, Nucleus.getJailManager().isPrisoner(_player.getUniqueId()));

        BukkitTester.pause(10);

        session.release();

        assertEquals(true, session.isReleased());
        assertEquals(false, Nucleus.getJailManager().isPrisoner(_player.getUniqueId()));
    }
}