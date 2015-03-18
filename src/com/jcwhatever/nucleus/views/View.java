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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Abstract implementation of a view.
 */
public abstract class View implements IPluginOwned, IPlayerReference {

    private final Plugin _plugin;
    private ViewSession _session;

    private ViewCloseReason _recentCloseReason = ViewCloseReason.ESCAPE;

    /**
     * Constructor.
     */
    protected View(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
    }

    /**
     * Get the views owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the player the view is for.
     */
    @Override
    public Player getPlayer() {
        return _session.getPlayer();
    }

    /**
     * Get the view inventory type.
     */
    public abstract InventoryType getInventoryType();

    /**
     * Get the views {@link ViewSession}.
     */
    public ViewSession getViewSession() {
        return _session;
    }

    /**
     * Get the view instances Bukkit {@link org.bukkit.inventory.InventoryView}.
     *
     * @return Null if the view has not been shown yet or
     * does not have an {@link org.bukkit.inventory.InventoryView}.
     */
    @Nullable
    public abstract InventoryView getInventoryView();

    /**
     * Get the view instances Bukkit {@link org.bukkit.inventory.Inventory}.
     *
     * @return Null if the inventory has been set yet or
     * the view does not have an inventory.
     */
    @Nullable
    public abstract Inventory getInventory();

    /**
     * Determine if the view is capable of generating an
     * {@link org.bukkit.inventory.InventoryView} instance.
     *
     * <p>The result is not affected by whether or not the inventory
     * view has been shown yet.</p>
     */
    public abstract boolean isInventoryViewable();

    /**
     * Get the most reason the view was closed.
     *
     * <p>Used by {@link ViewEventListener} to determine how
     * to handle the {@link org.bukkit.event.inventory.InventoryCloseEvent}.</p>
     */
    public ViewCloseReason getCloseReason() {
        return _recentCloseReason;
    }

    /**
     * Reset the close reason back to {@link ViewCloseReason#ESCAPE}.
     *
     * <p>Used by {@link ViewEventListener} to reset
     * the views close reason after handling its
     * {@link org.bukkit.event.inventory.InventoryCloseEvent}.</p>
     */
    public void resetCloseReason() {
        _recentCloseReason = ViewCloseReason.ESCAPE;
    }

    /**
     * Open the view and show to the view session player.
     *
     * <p>Should not be called. Use {@link ViewSession}'s
     * {@link ViewSession#next} or {@link ViewSession#previous} methods.</p>
     *
     * @param reason  The reason the view is being opened.
     *
     * @return  True if successful.
     */
    boolean open(final ViewOpenReason reason) {
        PreCon.notNull(reason);

        Scheduler.runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                openView(reason);
            }
        });

        return true;
    }

    /**
     * Close the view.
     *
     * <p>Should not be called. Use {@link ViewSession}'s
     * {@link ViewSession#next} or {@link ViewSession#previous} methods instead.</p>
     *
     * @param reason  The reason the view is being closed.
     *
     * @return  True if successful.
     */
    boolean close(ViewCloseReason reason) {
        PreCon.notNull(reason);

        if (_session.getCurrent() != this)
            return false;

        _recentCloseReason = reason;

        if (reason != ViewCloseReason.ESCAPE)
            getPlayer().closeInventory();

        onClose(reason);

        return true;
    }

    /**
     * Set the view session.
     */
    void setViewSession(ViewSession session) {
        PreCon.notNull(session);

        if (_session != null && _session != session)
            throw new RuntimeException("A view instance can only be used for a single view session.");

        _session = session;

        onViewSessionSet(session);
    }

    /**
     * Called when the view needs to be opened.
     *
     * <p>This method should handle creating the inventory and
     * inventory view and showing them to the player.</p>
     *
     * @param reason  The reason the view is being opened.
     */
    protected abstract boolean openView(ViewOpenReason reason);

    /**
     * Invoked after the view is closed.
     *
     * @param reason  The reason the view was closed.
     */
    protected void onClose(ViewCloseReason reason) {}

    /**
     * Invoked after the view session is set.
     *
     * @param session  The view session that is set.
     */
    protected void onViewSessionSet(ViewSession session) {}

    /**
     * Invoked when the {@link ViewSession} is disposed.
     */
    protected void onDispose() {}
}
