/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.internal.providers.friends;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.timed.TimedHashMap;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.providers.friends.FriendLevel;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IFriendsProvider}.
 */
public class NucleusFriendsProvider implements IFriendsProvider {

    private final Map<UUID, FriendInfo> _friends = new TimedHashMap<>(
            Nucleus.getPlugin(), 15, 30, TimeScale.MINUTES);

    private final Object _sync = new Object();

    @Override
    public String getName() {
        return "NucleusFriendProvider";
    }

    @Override
    public String getVersion() {
        return Nucleus.getPlugin().getDescription().getVersion();
    }

    @Override
    public int getLogicalVersion() {
        return 0;
    }

    @Override
    public void onRegister() {
        // do nothing
    }

    @Override
    public void onEnable() {
        // do nothing
    }

    @Override
    public void onDisable() {
        // do nothing
    }

    @Override
    public Collection<IFriend> getFriends(UUID playerId) {
        PreCon.notNull(playerId);

        loadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = _friends.get(playerId);

            assert friendInfo != null;

            return Collections.unmodifiableCollection(friendInfo.friends.values());
        }
    }

    @Nullable
    @Override
    public IFriend getFriend(UUID playerId, UUID friendId) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);

        loadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = _friends.get(playerId);

            return friendInfo.friends.get(friendId);
        }
    }

    @Override
    public boolean isFriend(UUID playerId, UUID friendId) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);

        loadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = _friends.get(playerId);

            return friendInfo.friends.containsKey(friendId);
        }
    }

    @Override
    public IFriend addFriend(UUID playerId, UUID friendId, FriendLevel level) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);
        PreCon.notNull(level);

        loadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = _friends.get(playerId);

            IDataNode dataNode = friendInfo.dataNode.getNode(friendId.toString());
            dataNode.set("since", System.currentTimeMillis());
            dataNode.set("level", level);

            NucleusFriend friend = new NucleusFriend(this, playerId, friendId, dataNode);
            dataNode.save();

            friendInfo.friends.put(friendId, friend);

            return friend;
        }
    }

    @Override
    public boolean removeFriend(UUID playerId, UUID friendId) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);

        loadFriends(playerId);

        synchronized (_sync) {

            FriendInfo friendInfo = _friends.get(playerId);

            IFriend friend = friendInfo.friends.remove(friendId);
            if (friend == null)
                return false;

            ((NucleusFriend) friend).unFriend();

            return true;
        }
    }

    private void loadFriends(UUID playerId) {

        synchronized (_sync) {
            if (_friends.containsKey(playerId))
                return;
        }

        IDataNode dataNode = DataStorage.get(Nucleus.getPlugin(), new DataPath("friends." + playerId.toString()));
        dataNode.load();

        synchronized (_sync) {
            Map<UUID, IFriend> friends = new HashMap<>(dataNode.size());

            for (IDataNode friendNode : dataNode) {
                UUID friendId = TextUtils.parseUUID(friendNode.getName());
                if (friendId == null) {
                    NucMsg.debug("Failed to load friend node because it could not be parsed " +
                            "to a UUID: {0}", friendNode.getName());
                    continue;
                }

                NucleusFriend friend = new NucleusFriend(this, playerId, friendId, friendNode);
                friends.put(friendId, friend);
            }

            _friends.put(playerId, new FriendInfo(playerId, dataNode, friends));
        }
    }

    private static class FriendInfo {

        final UUID playerId;
        final IDataNode dataNode;
        final Map<UUID, IFriend> friends;

        FriendInfo(UUID playerId, IDataNode dataNode, Map<UUID, IFriend> friends) {
            this.playerId = playerId;
            this.dataNode = dataNode;
            this.friends = friends;
        }
    }
}
