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

package com.jcwhatever.nucleus.managed.resourcepacks;

import com.jcwhatever.nucleus.managed.resourcepacks.sounds.IResourcePackSounds;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import org.bukkit.entity.Player;

/**
 * Resource pack interface object.
 */
public interface IResourcePack extends INamedInsensitive {

    /**
     * Get the url address of the resource pack.
     */
    String getUrl();

    /**
     * Apply the resource pack to the specified player.
     *
     * <p>Use this method instead of directly setting the resource pack on the player.</p>
     *
     * @param player  The player.
     *
     * @return  True if applied, false if already applied.
     */
    boolean apply(Player player);

    /**
     * Remove the resource pack from the player.
     *
     * <p>Removes the resource pack from the players view and removes the last
     * occurrence from the tracked stack of packs the player is viewing.
     * If the removed pack is the one the player is currently viewing and if
     * there is a previous pack in the stack, the previous pack will be
     * applied to the player automatically.</p>
     *
     * <p>Use this method instead of directly setting the resource pack on the player.</p>
     *
     * @param player  The player.
     *
     * @return  True if found and removed, otherwise false.
     */
    boolean remove(Player player);

    /**
     * Get the resource pack sounds.
     */
    IResourcePackSounds getSounds();
}
