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


package com.jcwhatever.nucleus.managed.scripting.api;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Provide scripts with meta data storage for general and player specific use.
 */
public class SAPI_Meta implements IDisposable {

    private final IDataNode _dataNode;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param dataNode  The data node to store and retrieve meta from.
     */
    public SAPI_Meta(IDataNode dataNode) {
        _dataNode = dataNode;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Get meta data set on a player.
     *
     * @param player  The player.
     * @param key     The meta data key.
     *
     * @return  The stored object or null.
     */
    @Nullable
    public Object getPlayerMeta(Object player, String key) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(key);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        return _dataNode.get(p.getUniqueId().toString() + '.' + key);
    }

    /**
     * Set meta data value on a player.
     *
     * @param player  The player.
     * @param key     The meta data key.
     * @param value   The meta data value.
     */
    public void setPlayerMeta(Object player, String key, @Nullable Object value) {
        PreCon.notNull(player);
        PreCon.notNull(key);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        _dataNode.set(p.getUniqueId().toString() + '.' + key, value);
        _dataNode.save();
    }

    /**
     * Get global meta data value.
     *
     * @param key  The meta data key.
     *
     * @return  The stored object or null.
     */
    public Object getMeta(String key) {
        PreCon.notNullOrEmpty(key);

        return _dataNode.get("global." + key);
    }

    /**
     * Set global meta data value.
     *
     * @param key    The meta data key.
     * @param value  The meta data value.
     */
    public void setMeta(String key, @Nullable Object value) {
        PreCon.notNullOrEmpty(key);

        _dataNode.set("global." + key, value);
        _dataNode.save();
    }
}
