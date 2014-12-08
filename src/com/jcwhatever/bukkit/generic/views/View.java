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
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/*
 * 
 */
public abstract class View implements IView {

    private final IViewSession _session;
    private final IViewFactory _factory;
    private final ViewArguments _meta;
    private String _title;

    private ViewCloseReason _recentCloseReason = ViewCloseReason.NONE;

    protected View(@Nullable String title, IViewSession session,
                   IViewFactory factory, ViewArguments arguments) {
        PreCon.notNull(session);
        PreCon.notNull(factory);
        PreCon.notNull(arguments);

        _title = title;
        _session = session;
        _factory = factory;
        _meta = arguments;
    }

    @Override
    public Plugin getPlugin() {
        return _session.getPlugin();
    }

    @Override
    public Player getPlayer() {
        return _session.getPlayer();
    }

    @Nullable
    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public IViewSession getViewSession() {
        return _session;
    }

    @Override
    public IViewFactory getFactory() {
        return _factory;
    }

    @Override
    public ViewArguments getArguments() {
        return _meta;
    }

    @Override
    public boolean close(ViewCloseReason reason) {
        PreCon.notNull(reason);

        if (_session.getCurrentView() != this)
            return false;

        _recentCloseReason = reason;
        getPlayer().closeInventory();
        return true;
    }

    @Override
    public ViewCloseReason getCloseReason() {
        return _recentCloseReason;
    }

    @Override
    public void resetCloseReason() {
        _recentCloseReason = ViewCloseReason.NONE;
    }

    protected void setTitle(@Nullable String title) {
        _title = title;
    }

    protected abstract void onClose(ViewCloseReason reason);
}
