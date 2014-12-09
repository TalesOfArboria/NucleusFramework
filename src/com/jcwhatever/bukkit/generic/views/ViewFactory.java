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

import org.bukkit.plugin.Plugin;

/**
 * Abstract implementation of a view factory that
 * implements common view factory methods.
 */
public abstract class ViewFactory implements IViewFactory {

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param plugin  The factories owning plugin.
     * @param name    The name of the factory.
     */
    protected ViewFactory(Plugin plugin, String name) {
        // PreCon.notNull(plugin) - do not null check plugin yet,
        // allow null so getPlugin() can be overridden.
        PreCon.notNull(name);

        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        if (_plugin == null)
            throw new NullPointerException("Plugin cannot be null.");

        return _plugin;
    }

    /**
     * Get the name of the view factory.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Get the name of the view factory in lower case.
     */
    @Override
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Determine if the view factory has been disposed.
     */
    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    /**
     * Dispose the view factory. Should only be called
     * if the view factory is no longer needed.
     */
    @Override
    public void dispose() {
        if (_isDisposed)
            return;

        onDispose();
        _isDisposed = true;
    }

    /**
     * Called when the factory is disposed.
     */
    protected abstract void onDispose();

    protected void checkDisposed() {
        if (_isDisposed)
            throw new RuntimeException("A ViewFactory cannot be used after it has been disposed.");
    }
}
