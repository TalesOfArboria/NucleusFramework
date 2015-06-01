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
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.providers.friends.IFriendsContext;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nucleus implementation of {@link IFriendsContext}.
 */
class NucleusNamedFriendsContext extends NucleusFriendsContext implements INamedInsensitive {

    private final String _name;
    private final String _searchName;
    private Map<UUID, FriendInfo> _friendInfo;

    public NucleusNamedFriendsContext(NucleusFriendsProvider provider, String name) {
        super(provider);

        _name = name;
        _searchName = name.toLowerCase();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    protected Map<UUID, FriendInfo> getFriendsMap() {
        if (_friendInfo == null) {
            _friendInfo = new TimedHashMap<>(
                    Nucleus.getPlugin(), 15, 30, TimeScale.MINUTES);
        }
        return _friendInfo;
    }

    @Override
    protected void lazyLoadFriends(UUID playerId) {

        synchronized (_sync) {
            if (getFriendsMap().containsKey(playerId))
                return;
        }

        IDataNode dataNode = DataStorage.get(Nucleus.getPlugin(),
                getProvider().getDataPath(
                                    getName() + '.'
                                    + (Bukkit.getOnlineMode() ? "" : "-offline.")
                                    + playerId.toString()));
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

            getFriendsMap().put(playerId, new FriendInfo(playerId, dataNode, friends));
        }
    }
}
