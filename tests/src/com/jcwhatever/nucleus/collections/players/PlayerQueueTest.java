package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.collections.java.QueueRunnable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.Collection;

public class PlayerQueueTest extends AbstractPlayerCollectionTest {

    protected Plugin _plugin = BukkitTester.mockPlugin("dummy");
    protected Player _player1 = BukkitTester.login("player1");
    protected Player _player2 = BukkitTester.login("player2");
    protected Player _player3 = BukkitTester.login("player3");

    @Test
    public void testQueueInterface() throws Exception {

        PlayerQueue playerSet = new PlayerQueue(_plugin);

        QueueRunnable<Player> interfaceTest = new QueueRunnable<>(playerSet, _player1, _player2, _player3);
        interfaceTest.run();
    }

    @Override
    protected Collection<Player> getCollection() {
        return new PlayerQueue(_plugin);
    }
}