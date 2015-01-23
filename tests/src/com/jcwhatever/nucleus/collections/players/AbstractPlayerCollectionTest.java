package com.jcwhatever.nucleus.collections.players;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

/*
 * 
 */
public abstract class AbstractPlayerCollectionTest {

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
    protected abstract Collection<Player> getCollection();

    /**
     * Make sure player is removed when they log out.
     */
    @Test
    public void testPlayerRemoved1() {

        Collection<Player> collection = getCollection();
        assertEquals(0, collection.size());

        Player player1 = BukkitTest.login("playerCollectionTest1");
        Player player2 = BukkitTest.login("playerCollectionTest2");

        collection.add(player1);
        collection.add(player2);

        assertEquals(true, collection.contains(player1));
        assertEquals(true, collection.contains(player2));
        assertEquals(2, collection.size());

        BukkitTest.pause(20);

        // make sure players are still in collection after waiting 20 ticks
        assertEquals(true, collection.contains(player1));
        assertEquals(true, collection.contains(player2));
        assertEquals(2, collection.size());

        // logout player 1
        BukkitTest.logout("playerCollectionTest1");
        BukkitTest.pause(3);

        // make sure player1 was removed
        assertEquals(false, collection.contains(player1));
        assertEquals(true, collection.contains(player2));
        assertEquals(1, collection.size());

        // kick player 2
        BukkitTest.kick("playerCollectionTest2");
        BukkitTest.pause(3);

        // make sure player2 was removed
        assertEquals(false, collection.contains(player1));
        assertEquals(false, collection.contains(player2));
        assertEquals(0, collection.size());
    }
}
