package com.jcwhatever.nucleus.collections.players;

import com.google.common.collect.Multimap;
import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.collections.java.MultimapRunnable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.UUID;

public class PlayerSetMultimapTest extends AbstractPlayerMultimapTest<String> {

    protected Plugin _plugin = BukkitTester.mockPlugin("dummy");
    protected Player _player1 = BukkitTester.login("player1");
    protected Player _player2 = BukkitTester.login("player2");
    protected Player _player3 = BukkitTester.login("player3");

    @Test
    public void testMapInterface() throws Exception {

        PlayerSetMultimap<String> map = new PlayerSetMultimap<>(_plugin);

        MultimapRunnable<UUID, String> interfaceTest = new MultimapRunnable<>(map,
                _player1.getUniqueId(), _player2.getUniqueId(), _player3.getUniqueId(), "a", "b", "c");
        interfaceTest.run();
    }

    @Override
    protected Multimap<UUID, String> getMap() {
        return new PlayerSetMultimap<String>(_plugin);
    }

}