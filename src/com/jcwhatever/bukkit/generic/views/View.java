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

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Abstract implementation of an {@link IView}.
 */
public abstract class View implements IView {

    private final ViewSession _session;
    private final IViewFactory _factory;
    private final ViewArguments _meta;

    private String _title;
    private ViewCloseReason _recentCloseReason = ViewCloseReason.ESCAPE;

    /**
     * Constructor.
     *
     * @param title      The optional title to use. Not all views can have a title set.
     * @param session    The view session the view is part of.
     * @param factory    The view factory that instantiated the view.
     * @param arguments  Meta arguments for the view.
     */
    protected View(@Nullable String title, ViewSession session,
                   IViewFactory factory, ViewArguments arguments) {
        PreCon.notNull(session);
        PreCon.notNull(factory);
        PreCon.notNull(arguments);

        _title = title;
        _session = session;
        _factory = factory;
        _meta = arguments;
    }

    /**
     * Get the views owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _factory.getPlugin();
    }

    /**
     * Get the player the view is for.
     */
    @Override
    public Player getPlayer() {
        return _session.getPlayer();
    }

    /**
     * Get the title of the view.
     *
     * @return  Null if not set.
     */
    @Nullable
    @Override
    public String getTitle() {
        return _title;
    }

    /**
     * Get the views {@code ViewSession}.
     */
    @Override
    public ViewSession getViewSession() {
        return _session;
    }

    /**
     * Get the factory that instantiated the view.
     */
    @Override
    public IViewFactory getFactory() {
        return _factory;
    }

    /**
     * Get the arguments that were passed into the view.
     */
    @Override
    public ViewArguments getArguments() {
        return _meta;
    }

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
    @Override
    public boolean open(final ViewOpenReason reason) {
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
     * <p>Should not be called. Use {@code ViewSession}'s
     * {@code next} or {@code back} methods instead.</p>
     *
     * @param reason  The reason the view is being closed.
     *
     * @return  True if successful.
     */
    @Override
    public boolean close(ViewCloseReason reason) {
        PreCon.notNull(reason);

        if (_session.getCurrentView() != this)
            return false;

        _recentCloseReason = reason;

        if (reason != ViewCloseReason.ESCAPE)
            getPlayer().closeInventory();

        onClose(reason);

        return true;
    }

    /**
     * Get the most reason the view was closed.
     *
     * <p>Used by {@code ViewEventListener} to determine how
     * to handle the {@code InventoryCloseEvent}.</p>
     */
    @Override
    public ViewCloseReason getCloseReason() {
        return _recentCloseReason;
    }

    /**
     * Reset the close reason back to {@code ESCAPE}.
     *
     * <p>Used by {@code ViewEventListener} to reset
     * the views close reason after handling its
     * {@code InventoryCloseEvent}.</p>
     */
    @Override
    public void resetCloseReason() {
        _recentCloseReason = ViewCloseReason.ESCAPE;
    }

    /**
     * Sets the views title.
     *
     * @param title  The title text.
     */
    protected void setTitle(@Nullable String title) {
        _title = title;
    }

    /**
     * Called when the view needs to be opened.
     *
     * @param reason  The reason the view is being opened.
     */
    protected abstract boolean openView(ViewOpenReason reason);

    /**
     * Called when the view is closed.
     *
     * @param reason  The reason the view is being closed.
     */
    protected abstract void onClose(ViewCloseReason reason);
}
