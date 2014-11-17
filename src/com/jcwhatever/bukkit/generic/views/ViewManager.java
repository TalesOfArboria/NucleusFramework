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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Manage player views.
 *
 * <p>
 *     A View represents a type of view that a player can see.
 * </p>
 * <p>
 *     A View Instance represents a specific View as seen by a specific player.
 * </p>
 *
 */
public class ViewManager {

    // player to ViewInstance map. View instance is the first view instance in a string of instances.
    private static final Map<UUID, ViewInstance> _playerInstanceMap = new PlayerMap<ViewInstance>(GenericsLib.getLib());

    private final Plugin _plugin;
    private final IDataNode _viewNode;
    private final Map<String, IView> _viewMap = new HashMap<String, IView>(10);
    private final Map<String, Class<? extends IView>> _registeredTypes = new HashMap<String, Class<? extends IView>>(10);

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The manager data storage node.
     */
    public ViewManager(Plugin plugin, @Nullable IDataNode dataNode) {
        PreCon.notNull(plugin);

        _viewNode = dataNode;
        _plugin = plugin;

        registerType(AnvilView.class);
        registerType(WorkbenchView.class);
        registerType(MenuView.class);
        registerType(ChestView.class);

        loadSettings();
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
     * @param viewClass  The view type class.
     */
    public void registerType(Class<? extends IView> viewClass) {
        _registeredTypes.put(viewClass.getSimpleName(), viewClass);
    }

    /**
     * Show a view to a player.
     *
     * @param p             The player to show the view to.
     * @param viewName      The name of the view.
     * @param sourceBlock   Optional source block. The block the player clicked.
     * @param instanceMeta  Optional instance meta.
     *
     * @return  The view instance for the player or null if an instance could not be created.
     */
    @Nullable
    public ViewInstance show(Player p, String viewName, @Nullable Block sourceBlock, @Nullable ViewMeta instanceMeta) {
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(viewName);

        viewName = viewName.toLowerCase();

        IView view = _viewMap.get(viewName);
        if (view == null)
            return null;

        return show(p, view, sourceBlock, instanceMeta);
    }

    /**
     * Show a view to a player.
     *
     * @param p             The player to show the view to.
     * @param view          The view to show to the player.
     * @param sourceBlock   Optional source block. The block the player clicked.
     * @param instanceMeta  Optional instance meta.
     *
     * @return  The view instance for the player or null if an instance could not be created.
     */
    @Nullable
    public ViewInstance show(Player p, IView view, @Nullable Block sourceBlock, @Nullable ViewMeta instanceMeta) {
        PreCon.notNull(p);
        PreCon.notNull(view);

        ViewInstance current = getCurrent(p);

        ViewMeta sessionMeta = current != null
                ? current.getSessionMeta()
                : new ViewMeta();

        ViewInstance instance = view.createInstance(p, current, sessionMeta, instanceMeta);
        if (instance == null)
            return null;

        if (current == null)
            _playerInstanceMap.put(p.getUniqueId(), instance);

        instance.show(sourceBlock, instanceMeta);

        return instance;
    }

    /**
     * Get the players current view.
     *
     * @param p  The player.
     *
     * @return  Null if the player is not looking at a view.
     */
    @Nullable
    public static ViewInstance getCurrent(Player p) {
        PreCon.notNull(p);

        ViewInstance instance = _playerInstanceMap.get(p.getUniqueId());
        if (instance == null)
            return null;

        return instance.getLast();
    }

    /**
     * Get a view by name.
     *
     * @param name  The name of the view
     *
     * @return  Null if the view is not found.
     */
    @Nullable
    public IView getView(String name) {
        PreCon.notNullOrEmpty(name);

        name = name.toLowerCase();

        IView view = _viewMap.get(name);
        if (view == null)
            return null;

        return view;
    }

    /**
     * Get a views class type.
     *
     * @param name  The name of the view.
     *
     * @return  null if the view was not found.
     */
    @Nullable
    public Class<? extends IView> getViewClass(String name) {
        PreCon.notNullOrEmpty(name);

        name = name.toLowerCase();

        return _registeredTypes.get(name);
    }

    /**
     * Get a view by name and type.
     *
     * @param name       The name of the view.
     * @param viewClass  The view class.
     *
     * @param <T>  The view type.
     *
     * @return  null if the view is not found.
     */
    @Nullable
    public <T extends IView> T getView(String name, Class<T> viewClass) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(viewClass);

        name = name.toLowerCase();

        IView view = _viewMap.get(name);
        if (view == null)
            return null;

        if (!viewClass.isInstance(view))
            return null;

        return viewClass.cast(view);
    }

    /**
     * Add a view type.
     *
     * @param name       The name of the view.
     * @param title      The title the view displays.
     * @param viewClass  The view class.
     *
     * @param <T>  The view type.
     *
     * @return The view instance or null if failed to add the view.
     */
    @Nullable
    public <T extends IView> T addView(String name, String title, Class<T> viewClass) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(title);
        PreCon.notNull(viewClass);

        name = name.toLowerCase();

        IView view = _viewMap.get(name);
        if (view != null)
            return null;

        IDataNode node = null;

        if (_viewNode != null) {
            node = _viewNode.getNode(viewClass.getSimpleName() + '.' + name);
            node.set("title", title);
            node.saveAsync(null);
        }

        try {
            view = viewClass.newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }
        catch (IllegalAccessException e){
            e.printStackTrace();
            return null;
        }

        view.init(name, this, node);

        _viewMap.put(name, view);

        return viewClass.cast(view);
    }

    /**
     * Remove a view.
     *
     * @param name  The name of the view.
     *
     * @return  The removed view instance or null.
     */
    @Nullable
    public IView removeView(String name) {
        PreCon.notNullOrEmpty(name);

        name = name.toLowerCase();

        IView view = _viewMap.remove(name);

        if (view != null) {
            view.dispose();

            if (_viewNode != null) {
                _viewNode.remove(view.getClass().getSimpleName() + '.' + name);
                _viewNode.saveAsync(null);
            }
        }

        return view;
    }

    /**
     * Remove a view.
     *
     * @param name       The name of the view.
     * @param viewClass  The view class.
     *
     * @param <T>  The view type.
     *
     * @return  The removed view instance or null.
     */
    @Nullable
    public <T extends IView> T removeView(String name, Class<T> viewClass) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(viewClass);

        name = name.toLowerCase();

        IView view = _viewMap.remove(name);
        if (view == null)
            return null;

        if (!viewClass.isInstance(view))
            return null;


        view.dispose();

        if (_viewNode != null) {
            _viewNode.remove(view.getClass().getSimpleName() + '.' + name);
            _viewNode.saveAsync(null);
        }

        return viewClass.cast(view);
    }

    /**
     * Get all registered views.
     */
    public List<IView> getViews() {
        return new ArrayList<IView>(_viewMap.values());
    }

    // initial of load settings
    private void loadSettings() {

        if (_viewNode == null)
            return;

        Set<String> typeNames = _viewNode.getSubNodeNames();
        if (typeNames != null && !typeNames.isEmpty()) {

            for (String typeName : typeNames) {

                Class<? extends IView> viewClass = _registeredTypes.get(typeName);
                if (viewClass == null)
                    continue;

                Set<String> viewNames = _viewNode.getSubNodeNames(typeName);

                if (viewNames != null && !viewNames.isEmpty()) {
                    for (String viewName : viewNames) {

                        IDataNode node = _viewNode.getNode(typeName + '.' + viewName);

                        IView view;

                        try {
                            view = viewClass.newInstance();
                        }
                        catch (InstantiationException e) {
                            e.printStackTrace();
                            continue;
                        }
                        catch (IllegalAccessException e) {
                            e.printStackTrace();
                            continue;
                        }

                        view.init(viewName.toLowerCase(), this, node);

                        _viewMap.put(viewName.toLowerCase(), view);

                    }
                }


            }

        }
    }

    /**
     * Clear the players view instances from the internal map.
     *
     * @param p  The player to remove.
     */
    static void clearCurrent(Player p) {
        _playerInstanceMap.remove(p.getUniqueId());
    }


}
