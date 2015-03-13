package com.jcwhatever.nucleus.providers.friends;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;
import java.util.UUID;

public abstract class IFriendTest {

    Player _player = BukkitTester.login("player");
    Player _friend = BukkitTester.login("friend");
    Plugin _plugin = BukkitTester.mockPlugin("friendsPlugin");

    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    protected abstract IFriend createFriend(UUID playerId, UUID friendId);

    @Test
    public void testGetFriendOfId() throws Exception {

        IFriend friend = createFriend(_player.getUniqueId(), _friend.getUniqueId());

        assertEquals(_player.getUniqueId(), friend.getFriendOfId());
    }

    @Test
    public void testGetPlayerId() throws Exception {

        IFriend friend = createFriend(_player.getUniqueId(), _friend.getUniqueId());

        assertEquals(_friend.getUniqueId(), friend.getPlayerId());
    }

    @Test
    public void testPermissions() throws Exception {

        IFriend friend = createFriend(_player.getUniqueId(), _friend.getUniqueId());

        Set<String> permissions = friend.getFlags(_plugin);
        assertEquals(0, permissions.size());

        friend.addFlag(_plugin, "permission1");

        permissions = friend.getFlags(_plugin);
        assertEquals(1, permissions.size());
        assertEquals(true, permissions.contains("permission1"));

        friend.removeFlag(_plugin, "permission1");
        assertEquals(0, permissions.size());
    }
}