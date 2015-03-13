package com.jcwhatever.nucleus.collections.players;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Multimap;
import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

/*
 * 
 */
public abstract class AbstractPlayerMultimapTest<V> {

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Invoked to get a new collection instance for testing.
     */
    protected abstract Multimap<UUID, V> getMap();

    /**
     * Make sure player is removed when they log out.
     */
    @Test
    public void testPlayerRemoved1() {

        Multimap<UUID, V> map = getMap();
        assertEquals(0, map.size());

        Player player1 = BukkitTester.login("playerCollectionTest1");
        Player player2 = BukkitTester.login("playerCollectionTest2");

        map.put(player1.getUniqueId(), null);
        map.put(player2.getUniqueId(), null);

        assertEquals(true, map.containsKey(player1.getUniqueId()));
        assertEquals(true, map.containsKey(player2.getUniqueId()));
        assertEquals(2, map.size());

        BukkitTester.pause(20);

        // make sure players are still in map after waiting 20 ticks
        assertEquals(true, map.containsKey(player1.getUniqueId()));
        assertEquals(true, map.containsKey(player2.getUniqueId()));
        assertEquals(2, map.size());

        // logout player 1
        BukkitTester.logout("playerCollectionTest1");
        BukkitTester.pause(3);

        // make sure player1 was removed
        assertEquals(false, map.containsKey(player1.getUniqueId()));
        assertEquals(true, map.containsKey(player2.getUniqueId()));
        assertEquals(1, map.size());

        // kick player 2
        BukkitTester.kick("playerCollectionTest2");
        BukkitTester.pause(3);

        // make sure player2 was removed
        assertEquals(false, map.containsKey(player1.getUniqueId()));
        assertEquals(false, map.containsKey(player2.getUniqueId()));
        assertEquals(0, map.size());
    }

}
