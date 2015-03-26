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


package com.jcwhatever.nucleus.scripting.api;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;

/**
 * Provide scripts with API for setting flags on players.
 */
public class SAPI_PlayerFlags implements IDisposable {

    private final IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param dataNode  The data node to store and retrieve flags from.
     */
    public SAPI_PlayerFlags(IDataNode dataNode) {
        _dataNode = dataNode;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void dispose() {
        // do nothing
    }

    /**
     * Determine if a player has a flag set.
     *
     * @param player    The player to check
     * @param flagName  The name of the flag
     *
     * @return  True if the flag is set.
     */
    public boolean has(Object player, String flagName) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(flagName);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        return _dataNode.getBoolean(p.getUniqueId().toString() + '.' + flagName, false);
    }

    /**
     * Set a flag on a player.
     *
     * @param player    The player.
     * @param flagName  The name of the flag.
     */
    public void set(Object player, String flagName) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(flagName);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        _dataNode.set(p.getUniqueId().toString() + '.' + flagName, true);
        _dataNode.save();
    }

    /**
     * Clear a flag on a player.
     *
     * @param player    The player.
     * @param flagName  The name of the flag.
     */
    public void clear(Object player, String flagName) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(flagName);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        _dataNode.remove(p.getUniqueId().toString() + '.' + flagName);
        _dataNode.save();
    }
}

