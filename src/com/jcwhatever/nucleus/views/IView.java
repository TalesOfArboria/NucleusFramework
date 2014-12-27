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

package com.jcwhatever.nucleus.views;

import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.views.data.ViewArguments;
import com.jcwhatever.nucleus.views.data.ViewCloseReason;
import com.jcwhatever.nucleus.views.data.ViewOpenReason;
import com.jcwhatever.nucleus.views.data.ViewResults;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Represents an instance of a view created for
 * a specific player.
 */
public interface IView extends IPluginOwned, IPlayerReference {

    /**
     * Get the views owning plugin.
     */
    @Override
    Plugin getPlugin();

    /**
     * Get the player this view is for.
     */
    @Override
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
     * Get the view instances Bukkit {@code Inventory}.
     *
     * @return Null if the inventory has been set yet or
     * the view does not have an inventory.
     */
    @Nullable
    Inventory getInventory();

    /**
     * Determine if the view is capable of generating an
     * {@code InventoryView} instance.
     *
     * <p>The result is not affected by whether or not the inventory
     * view has been shown yet.</p>
     */
    public boolean isInventoryViewable();

    /**
     * Get the arguments that were passed into the view
     * when it was created.
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
     * Open the view and show to the view session player.
     *
     * <p>Should not be called. Use {@code ViewSession}'s
     * {@code next} or {@code back} methods.</p>
     *
     * @param reason  The reason the view is being opened.
     *
     * @return  True if successful.
     */
    boolean open(ViewOpenReason reason);

    /**
     * Close the view.
     *
     * <p>Should not be called. Use {@code ViewSession}'s
     * {@code next} or {@code back} methods instead.</p>
     *
     * @param reason  The reason the view is being closed.
     *
     * @return  True if successful.
     */
    boolean close(ViewCloseReason reason);

    /**
     * Get the recent reason the view was closed.
     *
     * <p>Used by {@code ViewEventListener} to determine how
     * to handle the {@code InventoryCloseEvent}.</p>
     */
    ViewCloseReason getCloseReason();

    /**
     * Reset the close reason back to {@code ESCAPE}.
     *
     * <p>Used by {@code ViewEventListener} to reset
     * the views close reason after handling its
     * {@code InventoryCloseEvent}.</p>
     */
    void resetCloseReason();
}
