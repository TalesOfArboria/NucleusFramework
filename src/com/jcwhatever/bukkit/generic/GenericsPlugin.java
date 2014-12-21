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

import com.jcwhatever.bukkit.generic.commands.CommandDispatcher;
import com.jcwhatever.bukkit.generic.commands.TabCompleteDispatcher;
import com.jcwhatever.bukkit.generic.language.LanguageManager;
import com.jcwhatever.bukkit.generic.messaging.IChatPrefixed;
import com.jcwhatever.bukkit.generic.messaging.IMessenger;
import com.jcwhatever.bukkit.generic.messaging.MessengerFactory;
import com.jcwhatever.bukkit.generic.storage.DataPath;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.IDataNode;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;

/**
 * An abstract implementation of a Bukkit plugin with
 * GenericsLib specific features.
 */
public abstract class GenericsPlugin extends JavaPlugin implements IChatPrefixed {

    private LanguageManager _languageManager;
    private IDataNode _dataNode;
    private boolean _isDebugging;
    private IMessenger _messenger;
    private IMessenger _anonMessenger;

    /**
     * Constructor.
     */
    public GenericsPlugin() {
        super();
        onInit();
    }

    /**
     * Determine if the plugin is in debug mode.
     */
    public final boolean isDebugging() {
        return _isDebugging;
    }

    /**
     * Set the plugins debug mode.
     *
     * @param isDebugging  True to turn debug on.
     */
    public final void setDebugging(boolean isDebugging) {
        _isDebugging = isDebugging;
    }

    /**
     * Determine if the plugin is finished loading.
     */
    public boolean isLoaded() {
        return isEnabled();
    }

    /**
     * Get the plugins chat message prefix.
     */
    @Override
    public abstract String getChatPrefix();

    /**
     * Get the plugins console message prefix.
     */
    @Override
    public abstract String getConsolePrefix();

    /**
     * Get the plugins data node.
     */
    public IDataNode getDataNode() {

        return _dataNode;
    }

    /**
     * Get the plugins language manager.
     */
    public LanguageManager getLanguageManager() {
        return _languageManager;
    }

    /**
     * Get the plugins chat and console messenger.
     */
    public IMessenger getMessenger() {
        return _messenger;
    }

    /**
     * Get the plugins anonymous chat messenger.
     *
     * <p>A messenger that has no chat prefix.</p>
     */
    public IMessenger getAnonMessenger() {
        return _anonMessenger;
    }

    @Override
    public final void onEnable() {
        onPreEnable();

        loadDataNode();

        _messenger = MessengerFactory.get(this);
        _anonMessenger = MessengerFactory.getAnon(this);

        _languageManager = new LanguageManager(this);
        GenericsLib.registerPlugin(this);

        onEnablePlugin();
    }

    @Override
    public final void onDisable() {

        GenericsLib.unregisterPlugin(this);
        onDisablePlugin();
    }

    /**
     * Called when the plugin is instantiated.
     */
    protected void onInit() {
        // do nothing
    }

    /**
     * Called before the plugin config is loaded.
     */
    protected void onPreEnable() {
        // do nothing
    }

    /**
     * Called when the plugin is enabled.
     */
    protected abstract void onEnablePlugin();

    /**
     * Called when the plugin is disabled.
     */
    protected abstract void onDisablePlugin();

    /**
     * Register all commands defined in the plugin.yml
     * file to the specified dispatcher.
     *
     * @param dispatcher  The dispatcher to register.
     */
    protected void registerCommands(CommandDispatcher dispatcher) {
        Set<String> commands = getDescription().getCommands().keySet();
        for (String cmd : commands) {
            PluginCommand command = getCommand(cmd);
            command.setExecutor(dispatcher);
            command.setTabCompleter(new TabCompleteDispatcher());
        }
    }

    /**
     * Register event listeners.
     *
     * @param listeners  The listeners to register.
     */
    protected void registerEventListeners(Listener...listeners) {
        PluginManager pm = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    /*
     * Load the plugins data node.
     */
    private void loadDataNode() {
        File dir = getDataFolder();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create data folders.");
        }

        _dataNode = DataStorage.getStorage(this, new DataPath("config"));
        if (!_dataNode.load()) {
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException("The plugins data node (config) could not be loaded!");
        }

        _isDebugging = _dataNode.getBoolean("debug");
    }
}
