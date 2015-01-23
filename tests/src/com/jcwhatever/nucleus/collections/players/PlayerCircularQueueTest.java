package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.nucleus.collections.java.QueueRunnable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.Collection;

public class PlayerCircularQueueTest extends AbstractPlayerCollectionTest {

    protected Plugin _plugin = BukkitTest.mockPlugin("dummy");
    protected Player _player1 = BukkitTest.login("player1");
    protected Player _player2 = BukkitTest.login("player2");
    protected Player _player3 = BukkitTest.login("player3");

    @Test
    public void testQueueInterface() throws Exception {

        PlayerCircularQueue playerSet = new PlayerCircularQueue(_plugin);

        QueueRunnable<Player> interfaceTest = new QueueRunnable<>(playerSet, _player1, _player2, _player3);
        interfaceTest.run();
    }

    @Override
    protected Collection<Player> getCollection() {
        return new PlayerCircularQueue(_plugin);
    }

}