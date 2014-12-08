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

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/*
 * 
 */
public class ViewManager {

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    private final Map<String, IViewFactory> _registeredTypes = new HashMap<>(10);

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The manager data storage node.
     */
    public ViewManager(Plugin plugin, @Nullable IDataNode dataNode) {
        PreCon.notNull(plugin);

        _dataNode = dataNode;
        _plugin = plugin;
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public ViewManager(Plugin plugin) {
        this(plugin, null);
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Register a view type.
     *
     * @param factory  The view type factory.
     */
    public void registerType(IViewFactory factory) {
        _registeredTypes.put(factory.getSearchName(), factory);
    }

    /**
     * Get a view factory by name.
     *
     * @param name  The name of the factory.
     *
     * @return  Null if not found.
     */
    @Nullable
    public IViewFactory getFactory(String name) {
        PreCon.notNullOrEmpty(name);

        return _registeredTypes.get(name.toLowerCase());
    }

    /**
     * Remove a view.
     *
     * @param name  The name of the view.
     *
     * @return  The removed view instance or null.
     */
    @Nullable
    public boolean removeFactory(String name) {
        PreCon.notNullOrEmpty(name);

        return _registeredTypes.remove(name.toLowerCase()) != null;
    }

    /**
     * Get all registered view factories.
     */
    public List<IViewFactory> getFactories() {
        return new ArrayList<>(_registeredTypes.values());
    }

    /**
     * Start a new view session.
     *
     * @param p            The player to show the view to.
     * @param viewName     The name of the view factory.
     * @param sourceBlock  The source block.
     *
     * @return  True if successful.
     */
    public boolean showView(Player p, String viewName, @Nullable Block sourceBlock) {

        IViewFactory factory = getFactory(viewName);
        if (factory == null) {
            Messenger.debug(_plugin, "Failed to find view factory named '{0}'.", viewName);
            return false;
        }

        ViewSession session = ViewSession.get(p, sourceBlock);

        return session.next(factory, new ViewArguments()) != null;
    }

    // initial of load settings
    private void loadSettings() {

        if (_dataNode == null) {
            return;
        }
    }

}
