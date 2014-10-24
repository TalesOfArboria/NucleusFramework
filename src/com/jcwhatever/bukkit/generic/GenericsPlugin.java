package com.jcwhatever.bukkit.generic;

import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;

public abstract class GenericsPlugin extends JavaPlugin {

	private IDataNode _settings;
    private boolean _isDebugging;


	public GenericsPlugin() {
		init();
	}

    public final boolean isDebugging() {
        return _isDebugging;
    }

    public final void setDebugging(boolean isDebugging) {
        _isDebugging = isDebugging;
    }

    public abstract String getChatPrefix();

    public abstract String getConsolePrefix();

    @Override
    public final void onEnable() {
        onPreEnable();

        loadConfigFile();

        onEnablePlugin();
    }

    @Override
    public final void onDisable() {
        onDisablePlugin();
    }

	protected void init() {
		// do nothing
	}

    protected void onPreEnable() {
        // do nothing
    }

    protected abstract void onEnablePlugin();

    protected abstract void onDisablePlugin();

	public IDataNode getSettings() {
	
		return _settings;
	}

	private void loadConfigFile() {
		File dir = getDataFolder();
		if (!dir.exists() && !dir.mkdirs()) {
			throw new RuntimeException("Failed to crate data folders.");
		}

		_settings = DataStorage.getStorage(this, new DataPath("config"));
		if (!_settings.load()) {
			getServer().getPluginManager().disablePlugin(this);
			throw new RuntimeException("The config-file could not be loaded!");
		}

        _isDebugging = _settings.getBoolean("debug");
	}

	protected void registerCommands(CommandExecutor handler) {
		Set<String> commands = getDescription().getCommands().keySet();
		for (String cmd : commands) {
			getCommand(cmd).setExecutor(handler);
		}
	}
	
	protected void registerEventListeners(Listener...listeners) {
        PluginManager pm = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
	}

}
