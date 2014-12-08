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

import com.jcwhatever.bukkit.generic.utils.MetaKey;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

/*
 * 
 */
public class ViewSession implements IViewSession {

    private final Plugin _plugin;
    private final Player _player;
    private final Map<Object, Object> _meta = new HashMap<>(10);
    private final SessionRegistration _session;
    private final Block _sessionBlock;

    protected ViewContainer _first;
    protected ViewContainer _current;
    private boolean _isDisposed;

    public ViewSession(Plugin plugin, Player player, @Nullable Block sessionBlock) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);

        _plugin = plugin;
        _player = player;
        _sessionBlock = sessionBlock;
        _session = new SessionRegistration(this);
    }

    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public final Player getPlayer() {
        return _player;
    }

    @Override
    public final SessionRegistration getRegistration() {
        return _session;
    }

    @Nullable
    @Override
    public Block getSessionBlock() {
        return _sessionBlock;
    }

    @Nullable
    @Override
    public IView getCurrentView() {
        if (_current == null)
            return null;

        return _current.view;
    }

    @Nullable
    @Override
    public IView getPrevView() {
        if (_current == null || _current.prev == null)
            return null;

        return _current.prev.view;
    }

    @Nullable
    @Override
    public IView getNextView() {
        if (_current == null || _current.next == null)
            return null;

        return _current.next.view;
    }

    @Override
    @Nullable
    public IView getFirstView() {

        if (_current == null)
            return null;

        ViewContainer current = _current;

        while (current.prev != null) {
            current = current.prev;
        }

        return current.view;
    }

    @Override
    @Nullable
    public IView getLastView() {

        if (_current == null)
            return null;

        ViewContainer current = _current;

        while (current.next != null) {
            current = current.next;
        }

        return current.view;
    }

    @Override
    @Nullable
    public IView back() {
        if (_current == null)
            return null;

        _current = _current.prev;
        if (_current == null)
            return null;

        return _current.view;
    }

    @Override
    public IView next(IViewFactory factory, ViewArguments arguments) {
        return next(null, factory, arguments);
    }

    @Override
    public IView next(@Nullable String title, IViewFactory factory, ViewArguments arguments) {

        if (_current == null) {

            IView view = factory.create(title, this, arguments);

            _first = new ViewContainer(view, null, null);
            _current = _first;
        }
        else {

            ViewContainer prev = _current;
            IView prevView = _current.view;

            IView currentView = factory.create(title, this, arguments);
            ViewContainer current = new ViewContainer(currentView, prev, null);

            _current = current;

            prev.next = current;
            prev.view.close(ViewCloseReason.NEXT);
        }

        factory.open(ViewOpenReason.NEXT, _current.view);
        return _current.view;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        getRegistration().unregister();

        _isDisposed = true;
    }

    @Nullable
    @Override
    public <T> T getMeta(MetaKey<T> key) {
        PreCon.notNull(key);

        @SuppressWarnings("unchecked")
        T item = (T)_meta.get(key);

        return item;
    }

    @Nullable
    @Override
    public Object getMetaObject(Object key) {
        PreCon.notNull(key);

        return _meta.get(key);
    }

    @Override
    public <T> void setMeta(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        if (value == null) {
            _meta.remove(key);
        }
        else {
            _meta.put(key, value);
        }
    }

    @Override
    public Iterator<IView> iterator() {
        return new Iterator<IView>() {

            ViewContainer _current = _first;

            @Override
            public boolean hasNext() {
                return _current.next != null;
            }

            @Override
            public IView next() {
                _current = _current.next;
                return _current.view;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected static class ViewContainer {
        final IView view;
        final ViewContainer prev;
        ViewContainer next;

        protected ViewContainer(IView view, @Nullable ViewContainer prev,
                                @Nullable ViewContainer next) {
            this.view = view;
            this.prev = prev;
            this.next = next;
        }
    }
}
