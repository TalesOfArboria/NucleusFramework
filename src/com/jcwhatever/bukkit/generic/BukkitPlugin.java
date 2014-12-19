package com.jcwhatever.bukkit.generic;

import com.jcwhatever.bukkit.generic.internal.InternalEventManager;
import com.jcwhatever.bukkit.generic.internal.InternalRegionManager;
import com.jcwhatever.bukkit.generic.internal.InternalScriptApiRepo;
import com.jcwhatever.bukkit.generic.internal.InternalScriptManager;
import com.jcwhatever.bukkit.generic.internal.InternalTitleManager;
import com.jcwhatever.bukkit.generic.internal.PlayerTracker;
import com.jcwhatever.bukkit.generic.internal.commands.CommandHandler;
import com.jcwhatever.bukkit.generic.internal.jail.InternalJailManager;
import com.jcwhatever.bukkit.generic.internal.listeners.JCGEventListener;
import com.jcwhatever.bukkit.generic.internal.nms.InternalNmsManager;
import com.jcwhatever.bukkit.generic.internal.providers.InternalProviderManager;
import com.jcwhatever.bukkit.generic.internal.scripting.ScriptEngineLoader;
import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.generic.items.equipper.EntityEquipperManager;
import com.jcwhatever.bukkit.generic.messaging.MessengerFactory;
import com.jcwhatever.bukkit.generic.scheduler.BukkitTaskScheduler;
import com.jcwhatever.bukkit.generic.scheduler.ITaskScheduler;
import com.jcwhatever.bukkit.generic.scripting.GenericsScriptEngineManager;
import com.jcwhatever.bukkit.generic.titles.GenericsNamedTitleFactory;
import com.jcwhatever.bukkit.generic.utils.ScriptUtils;
import com.jcwhatever.bukkit.generic.utils.text.TextColor;

import java.io.File;
import javax.script.ScriptEngineManager;

/**
 * GenericsLib Bukkit Plugin
 */
public final class BukkitPlugin extends GenericsPlugin {

    InternalProviderManager _providerManager;
    InternalEventManager _eventManager;
    InternalTitleManager _titleManager;
    InternalRegionManager _regionManager;
    InternalScriptManager _scriptManager;
    InternalScriptApiRepo _scriptApiRepo;
    InternalNmsManager _nmsManager;

    InternalJailManager _jailManager;
    EntityEquipperManager _equipperManager;
    ITaskScheduler _scheduler;
    ScriptEngineManager _scriptEngineManager;
    KitManager _kitManager;
    CommandHandler _commandHandler;
    MessengerFactory _messengerFactory;

    ScriptEngineLoader _scriptEngineLoader;

    /**
     * Constructor.
     */
    public BukkitPlugin() {
        super();

        GenericsLib._plugin = this;
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
        return "[GenericsLib] ";
    }

    @Override
    protected void onPreEnable() {
        GenericsLib._hasEnabled = true;
        _providerManager = new InternalProviderManager();
    }

    @Override
    protected void onEnablePlugin() {

        _nmsManager = new InternalNmsManager();
        _commandHandler = new CommandHandler();
        _scheduler = new BukkitTaskScheduler();

        _eventManager = new InternalEventManager();

        _scriptApiRepo = new InternalScriptApiRepo();

        _scriptEngineManager = new GenericsScriptEngineManager();
        _scriptEngineLoader = new ScriptEngineLoader(_scriptEngineManager);
        _scriptEngineLoader.loadModules();

        _kitManager = new KitManager(this, getDataNode().getNode("kits"));
        _titleManager = new InternalTitleManager(this, getDataNode().getNode("titles"), new GenericsNamedTitleFactory());

        _regionManager = new InternalRegionManager(this);
        _equipperManager = new EntityEquipperManager();

        _jailManager = new InternalJailManager(getDataNode().getNode("jail"));
        _jailManager.loadSettings();

        registerEventListeners(new JCGEventListener(_regionManager));
        registerCommands(_commandHandler);

        loadScriptManager();

        // initialize player tracker
        PlayerTracker.get();
    }

    @Override
    protected void onDisablePlugin() {

        // make sure that evaluated scripts are disposed
        if (_scriptManager != null) {
            _scriptManager.clearScripts();
        }
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
