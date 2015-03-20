package com.jcwhatever.nucleus.jail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.storage.MemoryDataNode;
import com.jcwhatever.nucleus.utils.NamedLocation;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests {@link Jail}.
 */
public class JailTest {

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

        _jail = new Jail(_plugin, "testJail", new MemoryDataNode(_plugin));
        _jail.getJailBounds().setCoords(new Location(_world, 0, 0, 0), new Location(_world, 100, 100, 100));
    }

    /**
     * Tear down test.
     */
    @After
    public void after() {
        _jail.dispose();
    }

    /**
     * Make sure the correct plugin is returned by the jail.
     */
    @Test
    public void testGetPlugin() {

        assertEquals(_plugin, _jail.getPlugin());
    }

    /**
     * Make sure the correct name is returned by the jail.
     */
    @Test
    public void testGetName() {

        assertEquals("testJail", _jail.getName());
    }

    /**
     * Make sure the {@link Jail#imprison} method works properly, and that the
     * prisoner is released after the specified duration.
     */
    @Test
    public void testImprison() {

        JailSession session = _jail.imprison(_player, 10, TimeScale.TICKS);
        assertTrue(session != null);

        // make sure player is prisoner
        assertEquals(true, _jail.isPrisoner(_player));

        BukkitTester.pause(35); // warden resolution is approx. 20 ticks

        // make sure player is released
        assertEquals(false, _jail.isPrisoner(_player));

    }

    /**
     * Make sure {@link Jail#addTeleport} works properly.
     */
    @Test
    public void testAddTeleport() {

        Location location = new Location(_world, 0, 0, 0);

        assertEquals(true, _jail.addTeleport("addTeleportTest", location));

        NamedLocation namedLocation = _jail.getTeleport("addTeleportTest");

        assertTrue(namedLocation != null);
        assertEquals("addTeleportTest", namedLocation.getName());
        assertEquals(location, namedLocation);
    }

    /**
     * Make sure {@link Jail#removeTeleport} works properly.
     */
    @Test
    public void testRemoveTeleport() {

        Location location = new Location(_world, 0, 0, 0);

        assertEquals(true, _jail.addTeleport("remTeleportTest", location));

        NamedLocation namedLocation = _jail.getTeleport("remTeleportTest");

        // baseline test : make sure the location is added
        assertTrue(namedLocation != null);
        assertEquals("remTeleportTest", namedLocation.getName());
        assertEquals(location, namedLocation);

        // test remove
        assertEquals(true, _jail.removeTeleport("remTeleportTest"));

        namedLocation = _jail.getTeleport("remTeleportTest");

        assertTrue(namedLocation == null);
    }

    /**
     * Make sure {@link Jail#getRandomTeleport} method works properly.
     */
    @Test
    public void testGetRandomTeleport() {

        Set<Location> results = new HashSet<>(10);

        Location location1 = new Location(_world, 4, 0, 1);
        Location location2 = new Location(_world, 3, 0, 2);
        Location location3 = new Location(_world, 2, 0, 3);
        Location location4 = new Location(_world, 1, 0, 4);

        _jail.addTeleport("randomTest1", location1);
        _jail.addTeleport("randomTest2", location2);
        _jail.addTeleport("randomTest3", location3);
        _jail.addTeleport("randomTest4", location4);

        for (int i=0; i < 1000; i++) {

            Location randomLocation = _jail.getRandomTeleport();

            assertTrue(randomLocation != null);

            results.add(randomLocation);
        }

        assertEquals(4, results.size());
    }

    /**
     * Make sure {@link Jail#getTeleports} returns the correct results.
     */
    @Test
    public void testGetTeleports() {

        Location location1 = new Location(_world, 4, 0, 1);
        Location location2 = new Location(_world, 3, 0, 2);

        _jail.addTeleport("randomTest1", location1);
        _jail.addTeleport("randomTest2", location2);

        List<NamedLocation> teleports = _jail.getTeleports();

        assertEquals(2, teleports.size());
        assertEquals("randomTest1", teleports.get(0).getName());
        assertEquals("randomTest2", teleports.get(1).getName());
    }

    /**
     * Make sure {@link Jail#getReleaseLocation} and {@link Jail#setReleaseLocation} work properly
     * and test that player is sent to release location after being released.
     */
    @Test
    public void testReleaseLocation() throws Exception {
        Location location = new Location(_world, 15, 10, 22);

        _jail.setReleaseLocation(location);

        assertEquals(location, _jail.getReleaseLocation());

        _jail.imprison(_player, 10, TimeScale.TICKS);

        BukkitTester.pause(35);

        assertEquals(location, _player.getLocation());
    }
}