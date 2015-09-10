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

import com.jcwhatever.nucleus.events.NucleusLoadedEvent;
import com.jcwhatever.nucleus.internal.InternalLeashTracker;
import com.jcwhatever.nucleus.internal.InternalPlayerTracker;
import com.jcwhatever.nucleus.internal.actionbar.InternalActionBarManager;
import com.jcwhatever.nucleus.internal.blockselect.InternalBlockSelector;
import com.jcwhatever.nucleus.internal.commands.economy.NEconomyCommand;
import com.jcwhatever.nucleus.internal.commands.friends.NFriendsCommand;
import com.jcwhatever.nucleus.internal.commands.jail.JailCommand;
import com.jcwhatever.nucleus.internal.commands.kits.KitsCommand;
import com.jcwhatever.nucleus.internal.commands.players.PlayersCommand;
import com.jcwhatever.nucleus.internal.commands.plugins.PluginsCommand;
import com.jcwhatever.nucleus.internal.commands.providers.ProvidersCommand;
import com.jcwhatever.nucleus.internal.commands.regions.RegionsCommand;
import com.jcwhatever.nucleus.internal.commands.respacks.ResPacksCommand;
import com.jcwhatever.nucleus.internal.commands.scripts.ScriptsCommand;
import com.jcwhatever.nucleus.internal.commands.signs.SignsCommand;
import com.jcwhatever.nucleus.internal.commands.storage.StorageCommand;
import com.jcwhatever.nucleus.internal.events.InternalEventManager;
import com.jcwhatever.nucleus.internal.listeners.JCGEventListener;
import com.jcwhatever.nucleus.internal.listeners.SpawnEggListener;
import com.jcwhatever.nucleus.internal.listeners.StartupListener;
import com.jcwhatever.nucleus.internal.managed.commands.InternalCommandManager;
import com.jcwhatever.nucleus.internal.managed.commands.response.InternalResponseRequestor;
import com.jcwhatever.nucleus.internal.managed.entity.InternalEntityTracker;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.InternalMobSerializer;
import com.jcwhatever.nucleus.internal.managed.items.equipper.InternalEquipperManager;
import com.jcwhatever.nucleus.internal.managed.items.floating.InternalFloatingItemManager;
import com.jcwhatever.nucleus.internal.managed.items.meta.InternalItemMetaHandlers;
import com.jcwhatever.nucleus.internal.managed.items.serializer.InternalItemSerializationManager;
import com.jcwhatever.nucleus.internal.managed.language.InternalLanguageManager;
import com.jcwhatever.nucleus.internal.managed.nms.InternalNmsManager;
import com.jcwhatever.nucleus.internal.managed.particles.InternalParticleEffectFactory;
import com.jcwhatever.nucleus.internal.managed.reflection.InternalReflectionManager;
import com.jcwhatever.nucleus.internal.managed.resourcepacks.InternalResourcePackManager;
import com.jcwhatever.nucleus.internal.managed.scheduler.InternalTaskScheduler;
import com.jcwhatever.nucleus.internal.managed.scoreboards.InternalScoreboardManager;
import com.jcwhatever.nucleus.internal.managed.scripting.InternalScriptApiRepo;
import com.jcwhatever.nucleus.internal.managed.scripting.InternalScriptEngineLoader;
import com.jcwhatever.nucleus.internal.managed.scripting.InternalScriptEngineManager;
import com.jcwhatever.nucleus.internal.managed.scripting.InternalScriptManager;
import com.jcwhatever.nucleus.internal.managed.signs.InternalSignManager;
import com.jcwhatever.nucleus.internal.managed.sounds.InternalSoundManager;
import com.jcwhatever.nucleus.internal.managed.teleport.InternalTeleportManager;
import com.jcwhatever.nucleus.internal.managed.titles.InternalTitleManager;
import com.jcwhatever.nucleus.internal.providers.InternalProviderLoader;
import com.jcwhatever.nucleus.internal.providers.InternalProviderManager;
import com.jcwhatever.nucleus.internal.regions.InternalRegionManager;
import com.jcwhatever.nucleus.managed.messaging.IMessengerFactory;
import com.jcwhatever.nucleus.managed.scheduler.ITaskScheduler;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.text.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import javax.script.ScriptEngineManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * NucleusFramework's Bukkit Plugin
 */
public final class BukkitPlugin extends NucleusPlugin {

    InternalProviderManager _providerManager;
    InternalEventManager _eventManager;
    InternalRegionManager _regionManager;
    InternalScriptManager _scriptManager;
    InternalScriptApiRepo _scriptApiRepo;
    InternalSoundManager _soundManager;
    InternalMobSerializer _mobSerializer;
    InternalNmsManager _nmsManager;
    InternalSignManager _signManager;
    InternalEntityTracker _entityTracker;
    InternalFloatingItemManager _floatingItemManager;
    InternalItemSerializationManager _itemSerialization;
    InternalScoreboardManager _scoreboardTracker;
    InternalLanguageManager _languageManager;
    InternalEquipperManager _equipperManager;
    InternalLeashTracker _leashTracker;
    InternalBlockSelector _blockSelector;
    InternalResponseRequestor _responseRequestor;
    InternalItemMetaHandlers _itemMetaHandlers;
    InternalTeleportManager _teleportManager;
    InternalTitleManager _titleManager;
    InternalActionBarManager _actionBarManager;
    InternalReflectionManager _reflectionManager;
    InternalCommandManager _commandManager;
    InternalParticleEffectFactory _particleFactory;
    InternalResourcePackManager _resourcePacks;

    ITaskScheduler _scheduler;
    ScriptEngineManager _scriptEngineManager;
    IMessengerFactory _messengerFactory;

    InternalScriptEngineLoader _scriptEngineLoader;

    boolean _isModulesReady;

    /**
     * Constructor.
     */
    public BukkitPlugin() {
        super();

        Nucleus._plugin = this;

        // init rhino javascript engine if present
        try {
            initRhinoClassLoader();
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException ignore) {
            // rhino not found
        }
    }

    /**
     * Constructor for testing.
    */
    protected BukkitPlugin(JavaPluginLoader loader, PluginDescriptionFile description,
                           File dataFolder, File file) {
        super(loader, description, dataFolder, file);

        Nucleus._plugin = this;
    }

    @Override
    public String getChatPrefix() {
        return TextColor.BLUE + "[" + TextColor.WHITE + "Nucleus" + TextColor.BLUE + "] " + TextColor.WHITE;
    }

    @Override
    public String getConsolePrefix() {
        return "[NucleusFramework] ";
    }

    @Override
    public boolean isLoaded() {
        return isEnabled() && _isModulesReady;
    }

    @Override
    protected void onPreEnable() {

        new StartupListener().preventLogins();

        _reflectionManager = new InternalReflectionManager();
        _languageManager = new InternalLanguageManager();
        _itemMetaHandlers = new InternalItemMetaHandlers();
        _itemSerialization = new InternalItemSerializationManager();
        _equipperManager = new InternalEquipperManager();
        _scoreboardTracker = new InternalScoreboardManager();
        _scheduler = new InternalTaskScheduler();
        _teleportManager = new InternalTeleportManager();
        _leashTracker = new InternalLeashTracker();
        _mobSerializer = new InternalMobSerializer();

        _providerManager = new InternalProviderManager(isTesting());
        InternalProviderLoader providerLoader = new InternalProviderLoader(_providerManager);
        providerLoader.loadModules();

        _eventManager = new InternalEventManager(this);
        _scriptApiRepo = new InternalScriptApiRepo();
        _regionManager = new InternalRegionManager(this);
        _soundManager = new InternalSoundManager();
        _signManager = new InternalSignManager();
        _entityTracker = new InternalEntityTracker();
        _blockSelector = new InternalBlockSelector();
        _responseRequestor = new InternalResponseRequestor();
    }

    @Override
    protected void onPostPreEnable() {

        _nmsManager = new InternalNmsManager();
        _titleManager = new InternalTitleManager();
        _actionBarManager = new InternalActionBarManager();
        _particleFactory = new InternalParticleEffectFactory();
        _commandManager = new InternalCommandManager();

        _scriptEngineManager = new InternalScriptEngineManager();
        _scriptEngineLoader = new InternalScriptEngineLoader(_scriptEngineManager);
        _scriptEngineLoader.loadModules();

        _floatingItemManager = new InternalFloatingItemManager();

        IDataNode resPackNode = DataStorage.get(Nucleus.getPlugin(), new DataPath("resource-packs"));
        resPackNode.load();
        _resourcePacks = new InternalResourcePackManager(resPackNode);
    }

    @Override
    protected void onEnablePlugin() {

        registerEventListeners(
                new JCGEventListener(_regionManager),
                new SpawnEggListener());

        registerCommand(NEconomyCommand.class);
        registerCommand(NFriendsCommand.class);
        registerCommand(JailCommand.class);
        registerCommand(KitsCommand.class);
        registerCommand(PlayersCommand.class);
        registerCommand(PluginsCommand.class);
        registerCommand(ProvidersCommand.class);
        registerCommand(RegionsCommand.class);
        registerCommand(ResPacksCommand.class);
        registerCommand(ScriptsCommand.class);
        registerCommand(SignsCommand.class);
        registerCommand(StorageCommand.class);

        loadScriptManager();

        // initialize player tracker
        InternalPlayerTracker.get();
    }

    @Override
    protected void onDisablePlugin() {

        // make sure that evaluated scripts are disposed
        if (_scriptManager != null) {
            _scriptManager.clearScripts();
        }
    }

    private void loadScriptManager() {

        // allow script engines to find nucleus classes
        Thread.currentThread().setContextClassLoader(getClassLoader());

        File scriptFolder = new File(getDataFolder(), "scripts");
        if (!scriptFolder.exists() && !scriptFolder.mkdirs()) {
            throw new RuntimeException("Failed to create script folder.");
        }

        _scriptManager = new InternalScriptManager(scriptFolder);
        _scriptManager.reload();
    }

    /**
     * Invoked by the provider loader when all providers are ready.
     */
    public void notifyProvidersReady() {

        if (_isModulesReady)
            return;

        _isModulesReady = true;

        onEnablePlugin();

        // enable Nucleus plugins
        for (NucleusPlugin plugin : NucleusPlugin._enabled) {
            if (plugin instanceof BukkitPlugin)
                continue;

            // skip plugins that were disabled before this point.
            if (!plugin.isEnabled())
                continue;

            try {
                plugin._isEnabled = true;
                plugin.onEnablePlugin();
            }
            catch (Throwable e) {
                e.printStackTrace();
                plugin._isEnabled = false;
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }

        NucleusLoadedEvent event = new NucleusLoadedEvent();
        Bukkit.getPluginManager().callEvent(event);
    }

    /*
     * Init class loader into global factory.
     * Reflection used to prevent compile issues with "internal" package.
     *
     * Equivalent code:
     *
     * ContextFactory factory = ContextFactory.getGlobal();
     * ClassLoader loader = Nucleus.class.getClassLoader();
     *
     * if (loader == null) {
     *      factory.initApplicationClassLoader(loader);
     * }
     */
    private void initRhinoClassLoader() throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> contextFactoryClass = Class.forName("sun.org.mozilla.javascript.internal.ContextFactory");

        Method getGlobal = contextFactoryClass.getDeclaredMethod("getGlobal");
        getGlobal.setAccessible(true);

        ClassLoader classLoader = Nucleus.class.getClassLoader();

        Object contextFactory = getGlobal.invoke(null);

        Method getApplicationClassLoader = contextFactoryClass.getDeclaredMethod(
                "getApplicationClassLoader");
        getApplicationClassLoader.setAccessible(true);

        ClassLoader current = (ClassLoader) getApplicationClassLoader.invoke(contextFactory);

        if (current == null) {

            Method initApplicationClassLoader = contextFactoryClass.getDeclaredMethod(
                    "initApplicationClassLoader", ClassLoader.class);
            initApplicationClassLoader.setAccessible(true);

            initApplicationClassLoader.invoke(contextFactory, classLoader);
        }
    }
}
