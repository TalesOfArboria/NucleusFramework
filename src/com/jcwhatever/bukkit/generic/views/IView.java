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

import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.views.triggers.IViewTrigger;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import javax.annotation.Nullable;

/**
 * Represents a type of player view
 */
public interface IView extends IDisposable {

    /**
     * Called only once. Used internally after instantiating view.
     *
     * @param name         The name of the view.
     * @param viewManager  The view manager responsible for the view.
     * @param dataNode     The data node to save settings to.
     */
    void init(String name, ViewManager viewManager, @Nullable IDataNode dataNode);

    /**
     * Get the name of the view
     */
    String getName();

    /**
     * Get the default title used for a view if one is not set in the instance.
     */
    String getDefaultTitle();

    /**
     * Get the views View Manager.
     */
    ViewManager getViewManager();

    /**
     * Get the Bukkit inventory type of the inventory view.
     */
    InventoryType getInventoryType();

    /**
     * Get the view type
     */
    ViewType getViewType();

    /**
     * Get the optional trigger used to open the view.
     */
    IViewTrigger getViewTrigger();

    /**
     * Set the optional view trigger
     *
     * @param triggerClass  The trigger class.
     *
     * @return True if the trigger was successfully changed.
     */
    boolean setViewTrigger(@Nullable Class<? extends IViewTrigger> triggerClass);

    /**
     * Create a new instance of the view to display to a player.
     *
     * @param p            The player the instance is for.
     * @param previous     The previous instance the player was viewing.
     * @param sessionMeta  The meta used for the session.
     */
    ViewInstance createInstance(Player p, ViewInstance previous, ViewMeta sessionMeta);

    /**
     * Create a new instance of the view to display to a player.
     *
     * @param p             The player the instance is for.
     * @param previous      The previous instance the player was viewing.
     * @param sessionMeta   The meta used for the session.
     * @param instanceMeta  The meta that applies to the new instance.
     */
    ViewInstance createInstance(
            Player p, @Nullable ViewInstance previous, ViewMeta sessionMeta, @Nullable ViewMeta instanceMeta);

    /**
     * Called internally when the View is removed.
     */
    @Override
    void dispose();


}
