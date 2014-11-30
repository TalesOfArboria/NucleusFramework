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


package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.scripting.ScriptHelper.ScriptConstructor;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * Manages scripts.
 */
public class GenericsScriptManager {

    // key is cachname in format: baseCachName.filename (no extension)
    // baseCacheName is relative path to file with slashes replaced with dots.
    // i.e. dir1.dir2.scriptName
    private final Map<String, IScript> _scripts = new HashMap<>(25);
    private final Plugin _plugin;
    private final ScriptEngineManager _engineManager;
    private ScriptConstructor<IScript> _scriptConstructor;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public GenericsScriptManager(Plugin plugin, ScriptEngineManager engineManager) {
        _plugin = plugin;
        _engineManager = engineManager;
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the script engine manager.
     */
    public ScriptEngineManager getEngineManager() {
        return _engineManager;
    }

    /**
     * Load scripts from a folder.
     *
     * @param scriptFolder  The folder to load from
     */
    public List<IScript> loadScripts(File scriptFolder, DirectoryTraversal traversal) {
        PreCon.notNull(scriptFolder);
        PreCon.isValid(scriptFolder.isDirectory());

        if (!scriptFolder.exists())
            return new ArrayList<>(0);

        List<IScript> scripts = ScriptHelper.loadScripts(getPlugin(),
                _engineManager, scriptFolder, traversal, getScriptConstructor());

        for (IScript script : scripts) {
            addScript(script);
        }

        return scripts;
    }

    /**
     * Add a script to the manager.
     *
     * @param script  The script to add.
     */
    public void addScript(IScript script) {
        PreCon.notNull(script);

        _scripts.put(script.getName().toLowerCase(), script);
    }

    /**
     * Remove a script by name.
     *
     * @param scriptName  The name of the script.
     */
    public void removeScript(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        _scripts.remove(scriptName.toLowerCase());
    }

    /**
     * Get a script by name.
     *
     * @param scriptName  The name of the script. (the relative path using dots instead of dashes)
     */
    @Nullable
    public IScript getScript(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        return _scripts.get(scriptName.toLowerCase());
    }

    /**
     * Get the names of all scripts.
     */
    public List<String> getScriptNames() {
        return new ArrayList<String>(_scripts.keySet());
    }

    /**
     * Get all scripts.
     */
    public List<IScript> getScripts() {
        return new ArrayList<>(_scripts.values());
    }

    /**
     * Clear all scripts.
     */
    public void clearScripts() {
        _scripts.clear();
    }

    /*
     * Called to get the script constructor.
     */
    public ScriptConstructor<IScript> getScriptConstructor() {

        if (_scriptConstructor == null) {
            final GenericsScriptManager manager = this;
            _scriptConstructor = new ScriptConstructor<IScript>() {
                @Override
                public IScript construct(String name, @Nullable String filename, String type, String script) {
                    return new GenericsScript(manager, name, filename, type, script);
                }
            };
        }

        return _scriptConstructor;
    }

}
