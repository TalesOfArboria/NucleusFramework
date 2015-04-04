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

import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.providers.friends.IFriendLevel;
import com.jcwhatever.nucleus.providers.friends.IFriendsContext;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/*
 * 
 */
class InternalFriendsContext implements IFriendsContext {

    private final NucleusFriendsProvider _provider;
    private Map<UUID, FriendInfo> _friendInfo;

    protected final Object _sync = new Object();

    public InternalFriendsContext(NucleusFriendsProvider provider) {
        PreCon.notNull(provider);

        _provider = provider;
    }

    public NucleusFriendsProvider getProvider() {
        return _provider;
    }

    @Override
    public Collection<IFriend> getAll(UUID playerId) {
        PreCon.notNull(playerId);

        lazyLoadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = getFriendsMap().get(playerId);

            assert friendInfo != null;

            return Collections.unmodifiableCollection(friendInfo.friends.values());
        }
    }

    @Nullable
    @Override
    public IFriend get(UUID playerId, UUID friendId) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);

        lazyLoadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = getFriendsMap().get(playerId);

            return friendInfo.friends.get(friendId);
        }
    }

    @Override
    public boolean isFriend(UUID playerId, UUID friendId) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);

        lazyLoadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = getFriendsMap().get(playerId);

            return friendInfo.friends.containsKey(friendId);
        }
    }

    @Override
    public IFriend add(UUID playerId, UUID friendId, IFriendLevel level) {
        PreCon.notNull(level);

        return add(playerId, friendId, level.getRawLevel());
    }

    @Override
    public IFriend add(UUID playerId, UUID friendId, int rawLevel) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);


        lazyLoadFriends(playerId);

        synchronized (_sync) {
            FriendInfo friendInfo = getFriendsMap().get(playerId);

            IDataNode dataNode = friendInfo.dataNode.getNode(friendId.toString());
            dataNode.set("since", System.currentTimeMillis());
            dataNode.set("level", rawLevel);

            NucleusFriend friend = new NucleusFriend(this, playerId, friendId, dataNode);
            dataNode.save();

            friendInfo.friends.put(friendId, friend);

            return friend;
        }
    }

    @Override
    public boolean remove(UUID playerId, UUID friendId) {
        PreCon.notNull(playerId);
        PreCon.notNull(friendId);

        lazyLoadFriends(playerId);

        synchronized (_sync) {

            FriendInfo friendInfo = getFriendsMap().get(playerId);

            IFriend friend = friendInfo.friends.remove(friendId);
            if (friend == null)
                return false;

            ((NucleusFriend) friend).unFriend();

            return true;
        }
    }

    protected Map<UUID, FriendInfo> getFriendsMap() {
        if (_friendInfo == null) {
            _friendInfo = new HashMap<>(15);
        }
        return _friendInfo;
    }

    protected void lazyLoadFriends(UUID playerId) {
        // do nothing
    }

    public static class FriendInfo {

        protected final UUID playerId;
        protected final IDataNode dataNode;
        protected final Map<UUID, IFriend> friends;

        FriendInfo(UUID playerId, IDataNode dataNode, Map<UUID, IFriend> friends) {
            this.playerId = playerId;
            this.dataNode = dataNode;
            this.friends = friends;
        }
    }
}
