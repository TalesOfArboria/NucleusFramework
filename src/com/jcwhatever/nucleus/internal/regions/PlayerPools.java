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

package com.jcwhatever.nucleus.internal.regions;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.LinkedList;
import java.util.UUID;

/**
 * Pools player location pools to prevent too many discarded {@link PlayerLocationCache}'s
 * from existing in the tenure memory space on servers that experience large numbers
 * of players coming and going.
 */
class PlayerPools {

    private LinkedList<PlayerLocationCache> _pools = new LinkedList<>();

    public PlayerLocationCache createLocationPool(UUID playerId) {
        PreCon.notNull(playerId);

        PlayerLocationCache pool;

        pool = _pools.isEmpty() ? new PlayerLocationCache(playerId) : _pools.remove();

        pool.setOwner(playerId);
        return pool;
    }

    public void repool(PlayerLocationCache pool) {
        PreCon.notNull(pool);
        _pools.add(pool);
    }
}
