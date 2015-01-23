package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.nucleus.collections.java.MapRunnable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

public class PlayerMapTest extends AbstractPlayerMapTest<String> {

    protected Plugin _plugin = BukkitTest.mockPlugin("dummy");
    protected Player _player1 = BukkitTest.login("player1");
    protected Player _player2 = BukkitTest.login("player2");
    protected Player _player3 = BukkitTest.login("player3");

    @Test
    public void testMapInterface() throws Exception {

        PlayerMap<String> map = new PlayerMap<String>(_plugin);

        MapRunnable<UUID, String> interfaceTest = new MapRunnable<>(map,
                _player1.getUniqueId(), _player2.getUniqueId(), _player3.getUniqueId(), "a", "b", "c");
        interfaceTest.run();
    }

    @Override
    protected Map<UUID, String> getMap() {
        return new PlayerMap<String>(_plugin);
    }
}