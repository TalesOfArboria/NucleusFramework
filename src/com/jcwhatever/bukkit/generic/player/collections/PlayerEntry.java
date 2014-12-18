/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.player.collections;

import org.bukkit.entity.Player;

import java.util.UUID;


/**
 * Encapsulates a player object and provides hashCode and
 * equals based on player UUID.
 *
 * <p>Used to compensate for CraftBukkit player objects dynamic hashCode.</p>
 */
public final class PlayerEntry {

    private Player _player;

    /**
     * Constructor.
     *
     * @param player  The {@code Player} object to encapsulate.
     */
    public PlayerEntry(Player player) {
        _player = player;
    }

    /**
     * Get the encapsulated player object.
     */
    public Player getPlayer() {
        return _player;
    }

    /**
     * Get the encapsulated players unique id.
     */
    public UUID getUniqueId() {
        return _player.getUniqueId();
    }

    @Override
    public int hashCode() {
        return _player.getUniqueId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Player) {
            return ((Player) obj).getUniqueId().equals(_player.getUniqueId());
        }
        else if (obj instanceof PlayerEntry) {
            return ((PlayerEntry) obj)._player.getUniqueId().equals(_player.getUniqueId());
        }

        return false;
    }
}
