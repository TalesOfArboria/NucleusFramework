package com.jcwhatever.bukkit.generic;

import com.jcwhatever.bukkit.generic.internal.commands.CommandHandler;
import com.jcwhatever.bukkit.generic.internal.events.JCGEventListener;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.regions.RegionManager;

/**
 * GenericsLib Bukkit plugin.
 */
public class GenericsLib extends GenericsPlugin {

    private static GenericsLib _instance;


    private JailManager _jailManager;
    private RegionManager _regionManager;

    /**
     * Get the {@code GenericsLib} plugin instance.
     */
	public static GenericsLib getPlugin() {
		return _instance;
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
        _regionManager = new RegionManager();
        _jailManager = new JailManager(this, "default", getDataNode().getNode("jail"));

        registerEventListeners(new JCGEventListener());
        registerCommands(new CommandHandler());
    }

    @Override
    protected void onDisablePlugin() {

    }
}
