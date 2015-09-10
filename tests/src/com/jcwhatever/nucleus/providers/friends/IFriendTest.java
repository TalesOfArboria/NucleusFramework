package com.jcwhatever.nucleus.providers.friends;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

public abstract class IFriendTest {

    Player _player = BukkitTester.login("player");
    Player _friend = BukkitTester.login("friend");

    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    protected abstract IFriend createFriend(UUID playerId, UUID friendId);

    @Test
    public void testGetSourceId() throws Exception {

        IFriend friend = createFriend(_player.getUniqueId(), _friend.getUniqueId());

        assertEquals(_player.getUniqueId(), friend.getSourceId());
    }

    @Test
    public void testGetPlayerId() throws Exception {

        IFriend friend = createFriend(_player.getUniqueId(), _friend.getUniqueId());

        assertEquals(_friend.getUniqueId(), friend.getFriendId());
    }
}