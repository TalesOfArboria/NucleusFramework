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

import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Interface for a players resource pack view.
 */
public interface IPlayerResourcePacks extends IPlayerReference {

    /**
     * Get the resource pack the player is currently viewing.
     *
     * <p>If there is no default pack and the player is not viewing any pack, the
     * most recent pack is returned to show client behaviour.</p>
     */
    @Nullable
    IResourcePack getCurrent();

    /**
     * Use the previous resource pack.
     *
     * @return  True if there is a previous resource pack, otherwise false.
     */
    boolean prev();

    /**
     * Use the specified resource pack.
     *
     * @param resourcePack  The resource pack.
     *
     * @return  True if the resource pack is set, False if a pending operation on the previous
     * resource pack is preventing the resource pack from being set.
     */
    boolean next(IResourcePack resourcePack);

    /**
     * Use the specified resource pack.
     *
     * @param resourcePack  The resource pack.
     * @param remove        Resource packs to remove from the players stack before setting
     *                      new pack.
     *
     * @return  True if the resource pack is set, False if a pending operation on the previous
     * resource pack is preventing the resource pack from being set.
     */
    boolean next(IResourcePack resourcePack, IResourcePack... remove);

    /**
     * Remove a resource pack from the players stack.
     *
     * <p>If the player is currently viewing the specified resource pack, this method is
     * effectively the same as invoking {@link #prev()}</p>
     *
     * @param resourcePack  The resource pack to remove.
     *
     * @return  True if found and removed, otherwise false.
     */
    boolean remove(IResourcePack resourcePack);

    /**
     * Get the status of the current resource pack.
     */
    ResourcePackStatus getStatus();

    /**
     * Get the next change in status when it occurs.
     */
    IFutureResult<IPlayerResourcePacks> getNextStatus();

    /**
     * Get the final status when it occurs.
     *
     * <p>If the current status is already a final status, the future is immediately called.</p>
     */
    IFutureResult<IPlayerResourcePacks> getFinalStatus();

    /**
     * Get the players stack of resource packs.
     *
     * <p>The current resource pack is at the end of the list.</p>
     */
    List<IResourcePack> getStack();

    /**
     * Copy the players stack of resource packs to the specified
     * output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<IResourcePack>> T getStack(T output);
}
