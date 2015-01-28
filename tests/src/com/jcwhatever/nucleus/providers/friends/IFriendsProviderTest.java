package com.jcwhatever.nucleus.providers.friends;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

public abstract class IFriendsProviderTest {

    Player _player = BukkitTester.login("player");
    Player _friend1 = BukkitTester.login("friend1");

    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    protected abstract IFriendsProvider createProvider();

    @Test
    public void test() throws Exception {

        IFriendsProvider provider = createProvider();

        Collection<IFriend> friends = provider.getFriends(_player.getUniqueId());
        assertEquals(0, friends.size());

        provider.addFriend(_player.getUniqueId(), _friend1.getUniqueId(), FriendLevel.CASUAL);

        friends = provider.getFriends(_player.getUniqueId());
        assertEquals(1, friends.size());

        provider.removeFriend(_player.getUniqueId(), _friend1.getUniqueId());

        friends = provider.getFriends(_player.getUniqueId());
        assertEquals(0, friends.size());
    }
}