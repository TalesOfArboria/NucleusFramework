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

import com.jcwhatever.bukkit.generic.events.GenericsEventManager;
import com.jcwhatever.bukkit.generic.internal.InternalEventManager;
import com.jcwhatever.bukkit.generic.internal.InternalScriptManager;
import com.jcwhatever.bukkit.generic.internal.InternalTitleManager;
import com.jcwhatever.bukkit.generic.internal.commands.CommandHandler;
import com.jcwhatever.bukkit.generic.internal.listeners.JCGEventListener;
import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.generic.items.equipper.EntityEquipperManager;
import com.jcwhatever.bukkit.generic.items.equipper.IEntityEquipper;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.regions.RegionManager;
import com.jcwhatever.bukkit.generic.scheduler.BukkitTaskScheduler;
import com.jcwhatever.bukkit.generic.scheduler.ITaskScheduler;
import com.jcwhatever.bukkit.generic.scripting.GenericsScriptEngineManager;
import com.jcwhatever.bukkit.generic.titles.GenericsNamedTitleFactory;
import com.jcwhatever.bukkit.generic.titles.INamedTitle;
import com.jcwhatever.bukkit.generic.titles.TitleManager;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.ScriptUtils;
import com.jcwhatever.bukkit.generic.utils.text.TextColor;

import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * GenericsLib Bukkit plugin.
 */
public class GenericsLib extends GenericsPlugin {

    private static GenericsLib _instance;
    private static Map<String, GenericsPlugin> _pluginNameMap = new HashMap<>(25);
    private static Map<Class<? extends GenericsPlugin>, GenericsPlugin> _pluginClassMap = new HashMap<>(25);

    private InternalEventManager _eventManager;
    private InternalTitleManager _titleManager;
    private JailManager _jailManager;
    private RegionManager _regionManager;
    private EntityEquipperManager _equipperManager;
    private ITaskScheduler _scheduler;
    private ScriptEngineManager _scriptEngineManager;
    private InternalScriptManager _scriptManager;
    private KitManager _kitManager;
    private CommandHandler _commandHandler;

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
     * Get the global event manager.
     */
    public static GenericsEventManager getEventManager() {
        return _instance._eventManager;
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
     * Get the default script engine manager.
     *
     * <p>Returns an instance of {@code GenericsScriptEngineManager}.</p>
     *
     * <p>Engines returned from the script engine manager are singleton
     * instances that are used globally.</p>
     */
    public static ScriptEngineManager getScriptEngineManager() {
        return _instance._scriptEngineManager;
    }

    /**
     * Get the default script manager.
     */
    public static InternalScriptManager getScriptManager() {
        return _instance._scriptManager;
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
     * Get the default kit manager.
     */
    public static KitManager getKitManager() {
        return _instance._kitManager;
    }

    /**
     * Get the default title manager.
     */
    public static TitleManager<INamedTitle> getTitleManager() {
        return _instance._titleManager;
    }

    /**
     * Get GenericsLib's internal command handler.
     */
    public CommandHandler getCommandHandler() {
        return _commandHandler;
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
        return TextColor.BLUE + "[" + TextColor.WHITE + "Generics" + TextColor.BLUE + "] " + TextColor.WHITE;
    }

    /**
     * Get the console prefix.
     */
    @Override
    public String getConsolePrefix() {
        return "[GenericsLib]";
    }

    @Override
    protected void onEnablePlugin() {

        _commandHandler = new CommandHandler();
        _scheduler = new BukkitTaskScheduler();

        _eventManager = new InternalEventManager();
        _scriptEngineManager = new GenericsScriptEngineManager();
        _kitManager = new KitManager(this, getDataNode().getNode("kits"));
        _titleManager = new InternalTitleManager(this, getDataNode().getNode("titles"), new GenericsNamedTitleFactory());

        _regionManager = new RegionManager(this);
        _jailManager = new JailManager(this, "default", getDataNode().getNode("jail"));
        _equipperManager = new EntityEquipperManager();

        registerEventListeners(new JCGEventListener());
        registerCommands(_commandHandler);

        loadScriptManager();
    }

    @Override
    protected void onDisablePlugin() {

        // make sure that evaluated scripts are disposed
        if (_scriptManager != null) {
            _scriptManager.clearScripts();
        }
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

    private void loadScriptManager() {

        File scriptFolder = new File(getDataFolder(), "scripts");
        if (!scriptFolder.exists() && !scriptFolder.mkdirs()) {
            throw new RuntimeException("Failed to create script folder.");
        }

        _scriptManager = new InternalScriptManager(this, scriptFolder);
        _scriptManager.addScriptApi(ScriptUtils.getDefaultApi(this, _scriptManager));
        _scriptManager.reload();
    }
}
