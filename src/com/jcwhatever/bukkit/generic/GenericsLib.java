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


package com.jcwhatever.bukkit.generic;

import com.jcwhatever.bukkit.generic.internal.commands.CommandHandler;
import com.jcwhatever.bukkit.generic.internal.listeners.JCGEventListener;
import com.jcwhatever.bukkit.generic.items.equipper.IEntityEquipper;
import com.jcwhatever.bukkit.generic.items.equipper.EntityEquipperManager;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.regions.RegionManager;
import com.jcwhatever.bukkit.generic.scheduler.BukkitTaskScheduler;
import com.jcwhatever.bukkit.generic.scheduler.ITaskScheduler;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * GenericsLib Bukkit plugin.
 */
public class GenericsLib extends GenericsPlugin {

    private static GenericsLib _instance;
    private static Map<String, GenericsPlugin> _pluginNameMap = new HashMap<>(25);
    private static Map<Class<? extends GenericsPlugin>, GenericsPlugin> _pluginClassMap = new HashMap<>(25);

    private JailManager _jailManager;
    private RegionManager _regionManager;
    private EntityEquipperManager _equipperManager;
    private ITaskScheduler _scheduler;

    /**
     * Get the {@code GenericsLib} plugin instance.
     */
    public static GenericsLib getLib() {
        return _instance;
    }

    /**
     * Get a Bukkit plugin that implements {@code GenericsPlugin} by name.
     *
     * @param name  The name of the plugin.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static GenericsPlugin getGenericsPlugin(String name) {
        PreCon.notNullOrEmpty(name);

        return _pluginNameMap.get(name.toLowerCase());
    }

    /**
     * Get a Bukkit plugin that implements {@code GenericsPlugin}.
     *
     * @param pluginClass  The plugin class.
     *
     * @param <T>  The plugin type.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static <T extends GenericsPlugin> T getGenericsPlugin(Class<T> pluginClass) {
        PreCon.notNull(pluginClass);

        GenericsPlugin plugin = _pluginClassMap.get(pluginClass);
        if (plugin == null)
            return null;

        return pluginClass.cast(plugin);
    }

    /**
     * Get all Bukkit plugins that implement {@code GenericsPlugin}.
     */
    public static List<GenericsPlugin> getGenericsPlugins() {
        return new ArrayList<>(_pluginNameMap.values());
    }

    /**
     * Get the default task scheduler.
     */
    public static ITaskScheduler getScheduler() {
        return _instance._scheduler;
    }

    /**
     * Get the global {@code RegionManager}.
     */
    public static RegionManager getRegionManager() {
        return _instance._regionManager;
    }

    /**
     * Get the default Jail Manager.
     */
    public static JailManager getJailManager() {
        return _instance._jailManager;
    }

    /**
     * Get the default entity equipper manager.
     */
    public static EntityEquipperManager getEquipperManager() {
        return _instance._equipperManager;
    }

    /**
     * Get an entity equipper from the default entity equipper manager
     * for the specified entity type.
     *
     * @param entityType  The entity type
     */
    public static IEntityEquipper getEquipper(EntityType entityType) {
        return _instance._equipperManager.getEquipper(entityType);
    }

    /**
     * Constructor.
     */
    public GenericsLib() {
        super();

        _instance = this;
    }

    /**
     * Get the chat prefix.
     */
    @Override
    public String getChatPrefix() {
        return "[GenericsLib] ";
    }

    /**
     * Get the console prefix.
     */
    @Override
    public String getConsolePrefix() {
        return getChatPrefix();
    }

    @Override
    protected void onEnablePlugin() {

        _scheduler = new BukkitTaskScheduler();
        _regionManager = new RegionManager(this);
        _jailManager = new JailManager(this, "default", getDataNode().getNode("jail"));
        _equipperManager = new EntityEquipperManager();

        registerEventListeners(new JCGEventListener());
        registerCommands(new CommandHandler());
    }

    @Override
    protected void onDisablePlugin() {
        // do nothing
        // Note: Disabling GenericsLib can break plugins.
    }

    /*
     * Register GenericsPlugin instance.
     */
    void registerPlugin(GenericsPlugin plugin) {

        _pluginNameMap.put(plugin.getName().toLowerCase(), plugin);
        _pluginClassMap.put(plugin.getClass(), plugin);
    }

    /*
     * Unregister GenericsPlugin instance.
     */
    void unregisterPlugin(GenericsPlugin plugin) {

        _pluginNameMap.remove(plugin.getName().toLowerCase());
        _pluginClassMap.remove(plugin.getClass());
    }
}
