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

import com.jcwhatever.bukkit.generic.events.manager.GenericsEventManager;
import com.jcwhatever.bukkit.generic.internal.InternalMessengerFactory;
import com.jcwhatever.bukkit.generic.internal.commands.CommandHandler;
import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.generic.items.equipper.EntityEquipperManager;
import com.jcwhatever.bukkit.generic.items.equipper.IEntityEquipper;
import com.jcwhatever.bukkit.generic.jail.IJailManager;
import com.jcwhatever.bukkit.generic.jail.Jail;
import com.jcwhatever.bukkit.generic.messaging.MessengerFactory;
import com.jcwhatever.bukkit.generic.nms.NmsManager;
import com.jcwhatever.bukkit.generic.providers.IProviderManager;
import com.jcwhatever.bukkit.generic.regions.IGlobalRegionManager;
import com.jcwhatever.bukkit.generic.scheduler.ITaskScheduler;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiRepo;
import com.jcwhatever.bukkit.generic.scripting.ScriptManager;
import com.jcwhatever.bukkit.generic.titles.INamedTitle;
import com.jcwhatever.bukkit.generic.titles.TitleManager;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * GenericsLib Services
 */
public final class GenericsLib {

    private GenericsLib() {}

    private static final String ERROR_NOT_ENABLED = "GenericsLib is not enabled yet.";

    private static Map<String, GenericsPlugin> _pluginNameMap = new HashMap<>(25);
    private static Map<Class<? extends GenericsPlugin>, GenericsPlugin> _pluginClassMap = new HashMap<>(25);

    static BukkitPlugin _plugin;

    // flag to prevent dependent plugins from using GenericsLib before
    // it is ready.
    static boolean _hasEnabled;

    /**
     * Get the {@code GenericsLib} plugin instance.
     */
    public static GenericsPlugin getPlugin() {
        return _plugin;
    }

    /**
     * Determine if GenericsLib is enabled.
     */
    public static boolean isEnabled() {
        return _plugin != null && _plugin.isEnabled();
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
     * Get all Bukkit plugins that extend {@code GenericsPlugin}.
     */
    public static List<GenericsPlugin> getGenericsPlugins() {
        return new ArrayList<>(_pluginNameMap.values());
    }

    /**
     * Get the global event manager.
     */
    public static GenericsEventManager getEventManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._eventManager;
    }

    /**
     * Get the default task scheduler.
     */
    public static ITaskScheduler getScheduler() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._scheduler;
    }

    /**
     * Get the global {@code RegionManager}.
     */
    public static IGlobalRegionManager getRegionManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._regionManager;
    }

    /**
     * Get GenericsLib's internal Jail Manager.
     */
    public static IJailManager getJailManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._jailManager;
    }

    /**
     * Get the default jail.
     */
    public static Jail getDefaultJail() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._jailManager.getJail(GenericsLib.getPlugin(), "default");
    }

    /**
     * Get the default entity equipper manager.
     */
    public static EntityEquipperManager getEquipperManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._equipperManager;
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
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._scriptEngineManager;
    }

    /**
     * Get the default script manager.
     */
    public static ScriptManager<IScript, IEvaluatedScript> getScriptManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._scriptManager;
    }

    /**
     * Get an entity equipper from the default entity equipper manager
     * for the specified entity type.
     *
     * @param entityType  The entity type
     */
    public static IEntityEquipper getEquipper(EntityType entityType) {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._equipperManager.getEquipper(entityType);
    }

    /**
     * Get the default kit manager.
     */
    public static KitManager getKitManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._kitManager;
    }

    /**
     * Get the default title manager.
     */
    public static TitleManager<INamedTitle> getTitleManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._titleManager;
    }

    /**
     * Get GenericsLibs messenger factory instance.
     */
    public static MessengerFactory getMessengerFactory() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        if (_plugin._messengerFactory == null)
            _plugin._messengerFactory = new InternalMessengerFactory(_plugin);

        return _plugin._messengerFactory;
    }

    /**
     * Get the Script API Repository.
     */
    public static ScriptApiRepo getScriptApiRepo() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._scriptApiRepo;
    }

    /**
     * Get GenericsLib's internal command handler.
     */
    public static CommandHandler getCommandHandler() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._commandHandler;
    }

    /**
     * Get GenericsLib's internal NMS manager.
     */
    public static NmsManager getNmsManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._nmsManager;
    }

    /**
     * Get the provider manager.
     */
    public static IProviderManager getProviderManager() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._providerManager;
    }

    /*
     * Register GenericsPlugin instance.
     */
    static void registerPlugin(GenericsPlugin plugin) {

        _pluginNameMap.put(plugin.getName().toLowerCase(), plugin);
        _pluginClassMap.put(plugin.getClass(), plugin);
    }

    /*
     * Unregister GenericsPlugin instance.
     */
    static void unregisterPlugin(GenericsPlugin plugin) {

        _pluginNameMap.remove(plugin.getName().toLowerCase());
        _pluginClassMap.remove(plugin.getClass());
    }

    public enum NmsHandlers {
        TITLES
    }
}
