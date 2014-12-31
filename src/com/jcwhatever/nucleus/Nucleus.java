/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus;

import com.jcwhatever.nucleus.events.manager.NucleusEventManager;
import com.jcwhatever.nucleus.internal.InternalMessengerFactory;
import com.jcwhatever.nucleus.internal.commands.NucleusCommandDispatcher;
import com.jcwhatever.nucleus.kits.KitManager;
import com.jcwhatever.nucleus.utils.items.equipper.EntityEquipperManager;
import com.jcwhatever.nucleus.utils.items.equipper.IEntityEquipper;
import com.jcwhatever.nucleus.jail.IJailManager;
import com.jcwhatever.nucleus.jail.Jail;
import com.jcwhatever.nucleus.messaging.MessengerFactory;
import com.jcwhatever.nucleus.nms.NmsManager;
import com.jcwhatever.nucleus.providers.IProviderManager;
import com.jcwhatever.nucleus.regions.IGlobalRegionManager;
import com.jcwhatever.nucleus.scheduler.ITaskScheduler;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.IScript;
import com.jcwhatever.nucleus.scripting.ScriptApiRepo;
import com.jcwhatever.nucleus.scripting.ScriptManager;
import com.jcwhatever.nucleus.titles.INamedTitle;
import com.jcwhatever.nucleus.titles.TitleManager;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * NucleusFramework Services
 */
public final class Nucleus {

    private Nucleus() {}

    private static final String ERROR_NOT_ENABLED = "NucleusFramework is not enabled yet.";

    private static Map<String, NucleusPlugin> _pluginNameMap = new HashMap<>(25);
    private static Map<Class<? extends NucleusPlugin>, NucleusPlugin> _pluginClassMap = new HashMap<>(25);

    static BukkitPlugin _plugin;

    // flag to prevent dependent plugins from using NucleusFramework before
    // it is ready.
    static boolean _hasEnabled;

    /**
     * Get the {@code NucleusFramework} plugin instance.
     */
    public static NucleusPlugin getPlugin() {
        return _plugin;
    }

    /**
     * Determine if NucleusFramework is enabled.
     */
    public static boolean isEnabled() {
        return _plugin != null && _plugin.isEnabled();
    }

    /**
     * Get a Bukkit plugin that implements {@code NucleusPlugin} by name.
     *
     * @param name  The name of the plugin.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static NucleusPlugin getNucleusPlugin(String name) {
        PreCon.notNullOrEmpty(name);

        return _pluginNameMap.get(name.toLowerCase());
    }

    /**
     * Get a Bukkit plugin that implements {@code NucleusPlugin}.
     *
     * @param pluginClass  The plugin class.
     *
     * @param <T>  The plugin type.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static <T extends NucleusPlugin> T getNucleusPlugin(Class<T> pluginClass) {
        PreCon.notNull(pluginClass);

        NucleusPlugin plugin = _pluginClassMap.get(pluginClass);
        if (plugin == null)
            return null;

        return pluginClass.cast(plugin);
    }

    /**
     * Get all Bukkit plugins that extend {@code NucleusPlugin}.
     */
    public static List<NucleusPlugin> getNucleusPlugins() {
        return new ArrayList<>(_pluginNameMap.values());
    }

    /**
     * Get the global event manager.
     */
    public static NucleusEventManager getEventManager() {
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
     * Get the default Jail Manager.
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

        return _plugin._jailManager.getJail(Nucleus.getPlugin(), "default");
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
     * <p>Returns an instance of {@code NucleusScriptEngineManager}.</p>
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
     * Get the default messenger factory instance.
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
     * Get NucleusFramework's internal command handler.
     */
    public static NucleusCommandDispatcher getCommandHandler() {
        PreCon.isValid(_hasEnabled, ERROR_NOT_ENABLED);

        return _plugin._commandHandler;
    }

    /**
     * Get NucleusFramework's internal NMS manager.
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
     * Register NucleusPlugin instance.
     */
    static void registerPlugin(NucleusPlugin plugin) {

        _pluginNameMap.put(plugin.getName().toLowerCase(), plugin);
        _pluginClassMap.put(plugin.getClass(), plugin);
    }

    /*
     * Unregister NucleusPlugin instance.
     */
    static void unregisterPlugin(NucleusPlugin plugin) {

        _pluginNameMap.remove(plugin.getName().toLowerCase());
        _pluginClassMap.remove(plugin.getClass());
    }

    public enum NmsHandlers {
        ACTION_BAR,
        TITLES
    }
}
