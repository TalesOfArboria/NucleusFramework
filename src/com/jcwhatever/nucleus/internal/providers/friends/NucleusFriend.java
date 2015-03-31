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
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Result;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IFriend}.
 */
public class NucleusFriend implements IFriend {

    private final UUID _sourceId;
    private final UUID _friendId;
    private final IDataNode _dataNode;
    private final Date _befriendDate;
    private final InternalFriendsContext _context;
    private final Object _sync = new Object();

    private String _name;
    private Map<Class<? extends IFriendLevel>, IFriendLevel> _classLevelMap;
    private IFriendLevel _level;
    private int _rawLevel;
    private Result<IFriend> _mutualFriend;

    private volatile boolean _isValid = true;

    /**
     * Constructor.
     *
     * @param context  The parent friend provider.
     * @param sourceId  The ID of the player the friend instance is for.
     * @param friendId  The ID of the player friend.
     * @param dataNode  The friend data node.
     */
    NucleusFriend(InternalFriendsContext context, UUID sourceId, UUID friendId, IDataNode dataNode) {

        _context = context;
        _sourceId = sourceId;
        _friendId = friendId;
        _dataNode = dataNode;

        long since = dataNode.getLong("since");
        if (since == 0)
            since = System.currentTimeMillis();

        _befriendDate = new Date(since);
        _rawLevel = dataNode.getInteger("level", 0);
    }

    @Override
    public UUID getSourceId() {
        return _sourceId;
    }

    @Override
    public UUID getFriendId() {
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

                IFriend friend = _context.get(_friendId, _sourceId);
                _mutualFriend = new Result<IFriend>(true, friend);
            }
        }

        return _mutualFriend.getResult();
    }

    @Nullable
    @Override
    public <T extends IFriendLevel> T getLevel(Class<T> levelClass) {

        if (_classLevelMap != null) {

            IFriendLevel level = _classLevelMap.get(levelClass);
            if (level != null) {
                @SuppressWarnings("unchecked")
                T result = (T) level;
                return result;
            }
        }

        T level = _context.getProvider().getClosestBelowLevel(levelClass, _rawLevel);
        if (level == null)
            return null;

        if (_classLevelMap == null)
            _classLevelMap = new HashMap<>(7);

        _classLevelMap.put(levelClass, level);

        return level;
    }

    @Nullable
    @Override
    public IFriendLevel getLevel() {
        if (_level != null)
            return _level;

        _level = _context.getProvider().getClosestBelowLevel(_rawLevel);
        return _level;
    }

    @Override
    public int getRawLevel() {
        return _rawLevel;
    }

    @Override
    public void setRawLevel(int level) {
        PreCon.notNull(level);

        _rawLevel = level;

        _dataNode.set("level", level);
        _dataNode.save();

        if (_classLevelMap != null)
            _classLevelMap.clear();

        _level = null;
    }

    public void unFriend() {
        _dataNode.remove();
        _dataNode.save();
        _isValid = false;
    }
}
