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

import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Result;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IFriend}.
 */
public class NucleusFriend implements IFriend {

    private final UUID _friendOfId;
    private final UUID _friendId;
    private final IDataNode _dataNode;
    private final Date _befriendDate;
    private final NucleusFriendsProvider _provider;
    private final SetMultimap<String, String> _permissions =
            SetMultimapBuilder.hashKeys().hashSetValues().build();
    private final Object _sync = new Object();

    private Result<IFriend> _mutualFriend;
    private String _name;
    private volatile boolean _isValid = true;

    /**
     * Constructor.
     *
     * @param provider  The parent friend provider.
     * @param friendOf  The ID of the player the friend instance is for.
     * @param friendId  The ID of the player friend.
     * @param dataNode  The friend data node.
     */
    NucleusFriend(NucleusFriendsProvider provider, UUID friendOf, UUID friendId, IDataNode dataNode) {

        _provider = provider;
        _friendOfId = friendOf;
        _friendId = friendId;
        _dataNode = dataNode;

        long since = dataNode.getLong("since");
        if (since == 0)
            since = System.currentTimeMillis();

        _befriendDate = new Date(since);
    }

    @Override
    public UUID getFriendOfId() {
        return _friendOfId;
    }

    @Override
    public UUID getPlayerId() {
        return _friendId;
    }

    @Override
    public String getName() {
        if (_name == null)
            _name = PlayerUtils.getPlayerName(_friendId);

        return _name;
    }

    @Override
    public Date getBefriendDate() {
        return _befriendDate;
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Nullable
    @Override
    public IFriend getMutualFriend() {
        if (_mutualFriend == null) {

            synchronized (_sync) {

                if (_mutualFriend != null)
                    return _mutualFriend.getResult();

                IFriend friend = _provider.getFriend(_friendId, _friendOfId);
                _mutualFriend = new Result<IFriend>(true, friend);
            }
        }

        return _mutualFriend.getResult();
    }

    @Override
    public Set<String> getPermissions(Plugin plugin) {
        PreCon.notNull(plugin);

        loadPermissions(plugin);

        synchronized (_sync) {
            return Collections.unmodifiableSet(_permissions.get(plugin.getName()));
        }
    }

    @Override
    public boolean addPermission(Plugin plugin, String permission) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(permission);

        loadPermissions(plugin);

        Set<String> permissions;

        synchronized (_sync) {
            permissions = new HashSet<>(_permissions.get(plugin.getName()));
            if (!permissions.add(permission))
                return false;
        }

        IDataNode permNode = _dataNode.getNode("perms");

        permNode.set(plugin.getName(), permissions);
        permNode.save();

        synchronized (_sync) {
            _permissions.put(plugin.getName(), permission);
        }

        return true;
    }

    @Override
    public boolean removePermission(Plugin plugin, String permission) {
        PreCon.notNull(plugin);
        PreCon.notNull(permission);

        loadPermissions(plugin);

        Set<String> permissions;

        synchronized (_sync) {
            permissions = new HashSet<>(_permissions.get(plugin.getName()));
            if (!permissions.remove(permission))
                return false;
        }

        IDataNode permNode = _dataNode.getNode("perms");

        permNode.set(plugin.getName(), permissions);
        permNode.save();

        synchronized (_sync) {
            _permissions.remove(plugin.getName(), permission);
        }

        return true;
    }

    public void unFriend() {
        _dataNode.remove();
        _dataNode.save();
        _isValid = false;
    }

    private void loadPermissions(Plugin plugin) {

        if (!_isValid)
            return;

        synchronized (_sync) {
            if (_permissions.containsKey(plugin.getName()))
                return;
        }

        IDataNode permNode = _dataNode.getNode("perms");

        //noinspection unchecked
        List<String> permissions = permNode.getStringList(plugin.getName(),
                CollectionUtils.UNMODIFIABLE_EMPTY_LIST);

        assert permissions != null;

        synchronized (_sync) {
            _permissions.putAll(plugin.getName(), permissions);
        }
    }
}
