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

import com.jcwhatever.nucleus.managed.commands.ICommandManager;
import com.jcwhatever.nucleus.managed.commands.response.IResponseRequestor;
import com.jcwhatever.nucleus.events.manager.EventManager;
import com.jcwhatever.nucleus.internal.managed.messenger.InternalMessengerFactory;
import com.jcwhatever.nucleus.managed.actionbar.IActionBarManager;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelector;
import com.jcwhatever.nucleus.managed.entity.IEntityTracker;
import com.jcwhatever.nucleus.managed.items.equipper.IEquipperManager;
import com.jcwhatever.nucleus.managed.items.floating.IFloatingItemManager;
import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandlers;
import com.jcwhatever.nucleus.managed.items.serializer.IItemStackSerialization;
import com.jcwhatever.nucleus.managed.language.ILanguageManager;
import com.jcwhatever.nucleus.managed.leash.ILeashTracker;
import com.jcwhatever.nucleus.managed.messaging.IMessengerFactory;
import com.jcwhatever.nucleus.managed.particles.IParticleEffectFactory;
import com.jcwhatever.nucleus.managed.reflection.IReflectionManager;
import com.jcwhatever.nucleus.managed.scheduler.ITaskScheduler;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboardManager;
import com.jcwhatever.nucleus.managed.scripting.IScriptApiRepo;
import com.jcwhatever.nucleus.managed.scripting.IScriptManager;
import com.jcwhatever.nucleus.managed.signs.ISignManager;
import com.jcwhatever.nucleus.managed.sounds.ISoundManager;
import com.jcwhatever.nucleus.managed.titles.ITitleManager;
import com.jcwhatever.nucleus.providers.IProviderManager;
import com.jcwhatever.nucleus.regions.IGlobalRegionManager;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.nms.NmsManager;

import org.bukkit.inventory.ItemStack;

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

    private static final String ERROR_NOT_READY = "NucleusFramework is not ready yet.";

    private static Map<String, NucleusPlugin> _pluginNameMap = new HashMap<>(25);
    private static Map<Class<? extends NucleusPlugin>, NucleusPlugin> _pluginClassMap = new HashMap<>(25);

    static BukkitPlugin _plugin;

    /**
     * Get the NucleusFramework plugin instance.
     */
    public static NucleusPlugin getPlugin() {
        return _plugin;
    }

    /**
     * Determine if NucleusFramework is enabled and fully loaded.
     */
    public static boolean isLoaded() {
        return _plugin != null && _plugin.isLoaded();
    }

    /**
     * Get a Bukkit plugin that implements {@link NucleusPlugin} by name.
     *
     * @param name  The name of the plugin.
     *
     * @return  The plugin or null if not found.
     */
    @Nullable
    public static NucleusPlugin getNucleusPlugin(String name) {
        PreCon.notNullOrEmpty(name);

        return _pluginNameMap.get(name.toLowerCase());
    }

    /**
     * Get a Bukkit plugin that implements {@link NucleusPlugin}.
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
     * Get all Bukkit plugins that extend {@link NucleusPlugin}.
     */
    public static List<NucleusPlugin> getNucleusPlugins() {
        return new ArrayList<>(_pluginNameMap.values());
    }

    /**
     * Get the global action bar manager.
     */
    public static IActionBarManager getActionBarManager() {
        PreCon.isValid(_plugin._actionBarManager != null, ERROR_NOT_READY);

        return _plugin._actionBarManager;
    }

    /**
     * Get the global player block selector.
     */
    public static IBlockSelector getBlockSelector() {
        PreCon.isValid(_plugin._blockSelector != null, ERROR_NOT_READY);

        return _plugin._blockSelector;
    }

    /**
     * Get the global command manager.
     */
    public static ICommandManager getCommandManager() {
        PreCon.isValid(_plugin._commandManager != null, ERROR_NOT_READY);

        return _plugin._commandManager;
    }

    /**
     * Get the global entity tracker.
     */
    public static IEntityTracker getEntityTracker() {
        PreCon.isValid(_plugin._entityTracker != null, ERROR_NOT_READY);

        return _plugin._entityTracker;
    }

    /**
     * Get the default entity equipper manager.
     */
    public static IEquipperManager getEquipperManager() {
        PreCon.isValid(_plugin._equipperManager != null, ERROR_NOT_READY);

        return _plugin._equipperManager;
    }

    /**
     * Get the global event manager.
     */
    public static EventManager getEventManager() {
        PreCon.isValid(_plugin._eventManager != null, ERROR_NOT_READY);

        return _plugin._eventManager;
    }

    /**
     * Get the global floating item manager.
     */
    public static IFloatingItemManager getFloatingItems() {
        PreCon.isValid(_plugin._floatingItemManager != null, ERROR_NOT_READY);

        return _plugin._floatingItemManager;
    }

    /**
     * Get the global {@link ItemStack} meta handlers used by the
     * internal {@link ItemStack} serializer.
     */
    public static IItemMetaHandlers getItemMetaHandlers() {
        PreCon.isValid(_plugin._itemSerialization != null, ERROR_NOT_READY);

        return _plugin._itemMetaHandlers;
    }

    /**
     * Get the global {@link ItemStack} serialization manager.
     */
    public static IItemStackSerialization getItemSerialization() {
        PreCon.isValid(_plugin._itemSerialization != null, ERROR_NOT_READY);

        return _plugin._itemSerialization;
    }

    /**
     * Get the global language manager.
     */
    public static ILanguageManager getLanguageManager() {
        PreCon.isValid(_plugin._itemSerialization != null, ERROR_NOT_READY);

        return _plugin._languageManager;
    }

    /**
     * Get the global leash tracker.
     */
    public static ILeashTracker getLeashTracker() {
        PreCon.isValid(_plugin._leashTracker != null, ERROR_NOT_READY);

        return _plugin._leashTracker;
    }

    /**
     * Get the global messenger factory.
     */
    public static IMessengerFactory getMessengerFactory() {

        if (_plugin._messengerFactory == null)
            _plugin._messengerFactory = new InternalMessengerFactory();

        return _plugin._messengerFactory;
    }

    /**
     * Get NucleusFramework's internal NMS manager.
     */
    public static NmsManager getNmsManager() {
        PreCon.isValid(_plugin._nmsManager != null, ERROR_NOT_READY);

        return _plugin._nmsManager;
    }

    /**
     * Get NucleusFramework's particle effect factory.
     */
    public static IParticleEffectFactory getParticleEffects() {
        PreCon.isValid(_plugin._particleFactory != null, ERROR_NOT_READY);

        return _plugin._particleFactory;
    }

    /**
     * Get the global provider manager.
     */
    public static IProviderManager getProviders() {
        PreCon.isValid(_plugin._providerManager != null, ERROR_NOT_READY);

        return _plugin._providerManager;
    }

    /**
     * Get the global reflection manager.
     */
    public static IReflectionManager getReflectionManager() {
        PreCon.isValid(_plugin._reflectionManager != null, ERROR_NOT_READY);

        return _plugin._reflectionManager;
    }

    /**
     * Get the global Region Manager.
     */
    public static IGlobalRegionManager getRegionManager() {
        PreCon.isValid(_plugin._regionManager != null, ERROR_NOT_READY);

        return _plugin._regionManager;
    }

    /**
     * Get the global command response requestor.
     */
    public static IResponseRequestor getResponseRequestor() {
        PreCon.isValid(_plugin._responseRequestor != null, ERROR_NOT_READY);

        return _plugin._responseRequestor;
    }

    /**
     * Get the global task scheduler.
     */
    public static ITaskScheduler getScheduler() {
        PreCon.isValid(_plugin._scheduler != null, ERROR_NOT_READY);

        return _plugin._scheduler;
    }

    /**
     * Get the global scoreboard tracker.
     */
    public static IScoreboardManager getScoreboardTracker() {
        PreCon.isValid(_plugin._scoreboardTracker != null, ERROR_NOT_READY);

        return _plugin._scoreboardTracker;
    }

    /**
     * Get the global Script API Repository.
     */
    public static IScriptApiRepo getScriptApiRepo() {
        PreCon.isValid(_plugin._scriptApiRepo != null, ERROR_NOT_READY);

        return _plugin._scriptApiRepo;
    }

    /**
     * Get the global script engine manager.
     *
     * <p>Engines returned from the script engine manager are singleton
     * instances that are used globally.</p>
     */
    public static ScriptEngineManager getScriptEngineManager() {
        PreCon.isValid(_plugin._scriptEngineManager != null, ERROR_NOT_READY);

        return _plugin._scriptEngineManager;
    }

    /**
     * Get the global script manager.
     */
    public static IScriptManager getScriptManager() {
        PreCon.isValid(_plugin._scriptManager != null, ERROR_NOT_READY);

        return _plugin._scriptManager;
    }

    /**
     * Get the global sign manager.
     */
    public static ISignManager getSignManager() {
        PreCon.isValid(_plugin._signManager != null, ERROR_NOT_READY);

        return _plugin._signManager;
    }

    /**
     * Get the global resource sound manager.
     */
    public static ISoundManager getSoundManager() {
        PreCon.isValid(_plugin._soundManager != null, ERROR_NOT_READY);

        return _plugin._soundManager;
    }

    /**
     * Get the global Minecraft title manager.
     */
    public static ITitleManager getTitleManager() {
        PreCon.isValid(_plugin._titleManager != null, ERROR_NOT_READY);

        return _plugin._titleManager;
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
}
