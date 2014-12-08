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

package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;
import com.jcwhatever.bukkit.generic.views.data.ViewResults;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Represents an instance of a View created for a specific player
 */
public interface IView {

    /**
     * Get the views owning plugin.
     */
    Plugin getPlugin();

    /**
     * Get the player this view is for.
     */
    Player getPlayer ();

    /**
     * Get the view title.
     *
     * <p>Not all views can have the title set.</p>
     *
     * @return Null if a title was not set.
     */
    @Nullable
    String getTitle ();

    /**
     * Get the view chest type.
     */
    InventoryType getInventoryType();

    /**
     * Get the view session.
     */
    ViewSession getViewSession();

    /**
     * Get the factory that created the view instance.
     */
    IViewFactory getFactory();

    /**
     * Get the view instances Bukkit {@code InventoryView}.
     *
     * @return Null if the view has not been shown yet or
     * does not have an {@code InventoryView}.
     */
    @Nullable
    InventoryView getInventoryView();

    /**
     * Determine if the view is capable of generating an
     * {@code InventoryView} instance.
     *
     * <p>The result is not affected by whether or not the inventory
     * view has been shown yet.</p>
     */
    public boolean isInventoryViewable();

    /**
     * Get the meta data associated with this specific instance.
     */
    ViewArguments getArguments();

    /**
     * Get the view results after closing.
     *
     * @return Null if the view does not provide results or has
     * not provided them yet.
     */
    @Nullable
    ViewResults getResults();

    /**
     * Close the view.
     *
     * @param reason  The reason the view is being closed.
     *
     * @return  True if the view was closed.
     */
    boolean close(ViewCloseReason reason);

    /**
     * Get the reason the view was last closed.
     */
    ViewCloseReason getCloseReason();

    /**
     * Called by the view event listener after
     * handling the inventory close event.
     */
    void resetCloseReason();
}
