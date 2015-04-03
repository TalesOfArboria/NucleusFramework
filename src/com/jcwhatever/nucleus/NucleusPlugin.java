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

import com.jcwhatever.nucleus.commands.CommandDispatcher;
import com.jcwhatever.nucleus.managed.language.ILanguageContext;
import com.jcwhatever.nucleus.managed.messaging.IChatPrefixed;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.MemoryDataNode;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An abstract implementation of a Bukkit plugin with
 * NucleusFramework specific features.
 */
public abstract class NucleusPlugin extends JavaPlugin implements IChatPrefixed {

    static List<NucleusPlugin> _enabled = new ArrayList<>(10);

    private ILanguageContext _languageContext;
    private IDataNode _dataNode;
    private boolean _isDebugging;
    private IMessenger _messenger;
    private IMessenger _anonMessenger;
    private boolean _isTesting;

    boolean _isEnabled;

    /**
     * Constructor.
     */
    public NucleusPlugin() {
        super();
        onInit();
    }

    /**
     * Constructor for testing.
     */
    protected NucleusPlugin(JavaPluginLoader loader, PluginDescriptionFile description,
                            File dataFolder, File file) {
        super(loader, description, dataFolder, file);

        _isTesting = true;

        onInit();
    }

    /**
     * Determine if the plugin is in debug mode.
     */
    public final boolean isDebugging() {
        return _isDebugging;
    }

    /**
     * Determine if the plugin is being tested.
     */
    public final boolean isTesting() {
        return _isTesting;
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
        return isEnabled() && _isEnabled;
    }

    @Override
    public abstract String getChatPrefix();

    @Override
    public abstract String getConsolePrefix();

    /**
     * Get the plugins data node.
     */
    public IDataNode getDataNode() {

        return _dataNode;
    }

    /**
     * Get the plugins language context.
     */
    public ILanguageContext getLanguageContext() {
        return _languageContext;
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

        _messenger = Nucleus.getMessengerFactory().get(this);
        _anonMessenger = Nucleus.getMessengerFactory().getAnon(this);

        loadDataNode();

        _languageContext = Nucleus.getLanguageManager().createContext(this);
        Nucleus.registerPlugin(this);

        if (!(this instanceof BukkitPlugin))
            _enabled.add(this);

        onPostPreEnable();
    }

    @Override
    public final void onDisable() {

        Nucleus.unregisterPlugin(this);

        try {
            onDisablePlugin();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked when the plugin is instantiated.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onInit() {
        // do nothing
    }

    /**
     * Invoked before the plugin config is loaded.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onPreEnable() {
        // do nothing
    }

    /**
     * Invoked after the plugin data node is loaded but before the plugin is enabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onPostPreEnable() {
        // do nothing
    }

    /**
     * Invoked when the plugin is enabled.
     */
    protected abstract void onEnablePlugin();

    /**
     * Invoked when the plugin is disabled.
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
            command.setTabCompleter(dispatcher);
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
        if (!_isTesting && !dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create data folders.");
        }

        if (_isTesting) {
            _dataNode = new MemoryDataNode(this);
        }
        else {
            _dataNode = DataStorage.get(this, new DataPath("config"));
            if (!_dataNode.load()) {
                getServer().getPluginManager().disablePlugin(this);
                throw new RuntimeException("The plugins data node (config) could not be loaded!");
            }
        }

        _isDebugging = _dataNode.getBoolean("debug");
    }
}
