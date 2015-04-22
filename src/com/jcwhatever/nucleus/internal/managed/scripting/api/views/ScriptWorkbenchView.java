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

package com.jcwhatever.nucleus.internal.managed.scripting.api.views;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemFilter;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.workbench.FilteredWorkbenchView;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A {@link FilteredWorkbenchView} implementation for scripts.
 */
public class ScriptWorkbenchView extends FilteredWorkbenchView implements IDisposable {

    private final Player _player;

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param plugin      The views owning plugin.
     * @param player      The player the view is for.
     * @param itemFilter  The filter manager used to allow or deny specific items.
     */
    public ScriptWorkbenchView(Plugin plugin, Player player, ItemFilter itemFilter) {
        super(plugin, itemFilter);

        PreCon.notNull(player);

        _player = player;
    }

    /**
     * Open the view to the player.
     *
     * @return  Self for chaining.
     */
    public ScriptWorkbenchView open() {

        ViewSession viewSession = ViewSession.get(_player, null);

        viewSession.next(this);

        return this;
    }

    /**
     * Close the view to the player.
     *
     * @return  Self for chaining.
     */
    public ScriptWorkbenchView close() {

        ViewSession viewSession = ViewSession.get(_player, null);

        if (!viewSession.contains(this))
            return this;

        View view = viewSession.getCurrent();

        // close all views opened after this view
        while (view != null && !view.equals(this)) {
            viewSession.previous();
            view = viewSession.getCurrent();
        }

        if (view != null && view.equals(this))
            viewSession.previous();

        return this;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }
}
