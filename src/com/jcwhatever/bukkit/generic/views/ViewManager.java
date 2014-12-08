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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/*
 * 
 */
public class ViewManager {

    private final Plugin _plugin;
    private final IViewSessionFactory _sessionFactory;
    private Map<String, IViewFactory> _viewFactories = new HashMap<>(20);
    private Map<Player, IViewSession> _viewSessions = new WeakHashMap<>(20);


    public ViewManager(Plugin plugin, IViewSessionFactory sessionFactory) {
        PreCon.notNull(plugin);
        PreCon.notNull(sessionFactory);

        _plugin = plugin;
        _sessionFactory = sessionFactory;
    }

    public Plugin getPlugin() {
        return _plugin;
    }

    public IViewSessionFactory getSessionFactory() {
        return _sessionFactory;
    }

    public boolean register(IViewFactory factory) {
        PreCon.notNull(factory);

        if (_viewFactories.containsKey(factory.getSearchName()))
            return false;

        _viewFactories.put(factory.getSearchName(), factory);

        return true;
    }

    public boolean unregister(String name) {
        PreCon.notNullOrEmpty(name);

        return _viewFactories.remove(name.toLowerCase()) != null;
    }

    @Nullable
    public IViewFactory getFactory(String name) {
        PreCon.notNullOrEmpty(name);

        return _viewFactories.get(name.toLowerCase());
    }

    @Nullable
    public IViewSession getSession(Player p) {
        return _viewSessions.get(p);
    }

    @Nullable
    public IViewSession show(Player p, String initialViewFactoryName, @Nullable Block sourceBlock) {
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(initialViewFactoryName);

        IViewFactory view = getFactory(initialViewFactoryName);
        if (view == null)
            return null;

        return show(p, view, sourceBlock);
    }

    public IViewSession show(Player p, IViewFactory view, @Nullable Block sourceBlock) {
        PreCon.notNull(p);
        PreCon.notNull(view);

        IViewSession session = _viewSessions.remove(p);
        if (session != null) {
            session.dispose();
        }

        session = _sessionFactory.create(p, sourceBlock);

        _viewSessions.put(p, session);

        session.next(view, new ViewArguments());

        return session;
    }

}
