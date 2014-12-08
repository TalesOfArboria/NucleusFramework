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
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;

import org.bukkit.plugin.Plugin;

/*
 * 
 */
public abstract class ViewFactory<T extends IView> implements IViewFactory {

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private final Class<T> _viewClass;

    private boolean _isDisposed;

    protected ViewFactory(Plugin plugin, String name, Class<T> viewClass) {
        // PreCon.notNull(plugin) - do not null check plugin yet, allow null so getPlugin() can be overridden.
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(viewClass);

        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
        _viewClass = viewClass;
    }

    @Override
    public Plugin getPlugin() {
        if (_plugin == null)
            throw new NullPointerException("Plugin cannot be null.");

        return _plugin;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public boolean open(ViewOpenReason reason, IView view) {
        PreCon.notNull(reason);
        PreCon.notNull(view);

        if (_viewClass.isAssignableFrom(view.getClass())) {

            onOpen(reason, _viewClass.cast(view));

            return true;
        }

        return false;
    }

    protected abstract boolean onOpen(ViewOpenReason reason, T view);

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        onDispose();
        _isDisposed = true;
    }

    protected abstract void onDispose();


}
