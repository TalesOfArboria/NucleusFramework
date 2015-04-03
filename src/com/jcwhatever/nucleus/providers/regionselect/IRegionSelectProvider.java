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

package com.jcwhatever.nucleus.providers.regionselect;

import com.jcwhatever.nucleus.providers.IProvider;
import com.jcwhatever.nucleus.regions.selection.IRegionSelection;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Interface for a region selection provider.
 */
public interface IRegionSelectProvider extends IProvider {

    /**
     * Get the specified players region selection.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player doesn't have a region
     * selected or the selection is incomplete.
     */
    @Nullable
    IRegionSelection getSelection(Player player);

    /**
     * Set the specified players region selection.
     *
     * @param player     The player.
     * @param selection  The new selection to set.
     *
     * @return  True if the players region selection was set.
     */
    boolean setSelection(Player player, IRegionSelection selection);
}
