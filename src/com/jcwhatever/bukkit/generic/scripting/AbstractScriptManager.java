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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.ScriptUtils;
import com.jcwhatever.bukkit.generic.utils.ScriptUtils.ScriptConstructor;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * Manages scripts.
 */
public abstract class AbstractScriptManager<S extends IScript, E extends IEvaluatedScript> {

    private final Plugin _plugin;
    private final File _scriptFolder;
    private final DirectoryTraversal _directoryTraversal;

    // key is script name
    private final Map<String, S> _scripts = new HashMap<>(25);

    // key is script name
    private final Map<String, E> _evaluated = new HashMap<>(25);

    // default script apis included in all evaluated scripts
    private final Map<String, IScriptApi> _apiMap = new HashMap<>(30);

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public AbstractScriptManager(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _scriptFolder = null;
        _directoryTraversal = null;
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public AbstractScriptManager(Plugin plugin, File scriptFolder, DirectoryTraversal directoryTraversal) {
        PreCon.notNull(plugin);
        PreCon.notNull(scriptFolder);
        PreCon.notNull(directoryTraversal);
        PreCon.isValid(scriptFolder.isDirectory(), "Script folder must be a folder.");

        _plugin = plugin;
        _scriptFolder = scriptFolder;
        _directoryTraversal = directoryTraversal;
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the script folder.
     *
     * @return  Null if a script folder was not specified in the constructor.
     */
    @Nullable
    public File getScriptFolder() {
        return _scriptFolder;
    }

    /**
     * Get the directory traversal type used to
     * load scripts from the script folder.
     *
     * @return  Null if a script folder was not specified in the constructor.
     */
    @Nullable
    public DirectoryTraversal getDirectoryTraversal() {
        return _directoryTraversal;
    }

    /**
     * Get the script engine manager.
     */
    public ScriptEngineManager getEngineManager() {
        return GenericsLib.getScriptEngineManager();
    }

    /**
     * Load scripts from script folder.
     *
     * <p>Clears current scripts and evaluated scripts before loading.</p>
     *
     * <p>Loaded scripts are not automatically re-evaluated.</p>
     */
    public void loadScripts() {

        clearScripts();
        clearEvaluated();

        if (_scriptFolder == null || !_scriptFolder.exists())
            return;

        List<S> scripts = ScriptUtils.loadScripts(getPlugin(),
                getEngineManager(), _scriptFolder, _directoryTraversal, getScriptConstructor());

        for (S script : scripts) {
            addScript(script);
        }
    }

    /**
     * Evaluates all scripts.
     *
     * <p>If a script is already evaluated, it is disposed and re-evaluated.</p>
     */
    public void evaluate() {

        for (S script : _scripts.values()) {
            E current = _evaluated.remove(script.getName().toLowerCase());
            if (current != null) {
                current.dispose();
            }

            E evaluated = callEvaluate(script, _apiMap.values());
            if (evaluated == null)
                continue;

            _evaluated.put(script.getName().toLowerCase(), evaluated);
        }
    }

    /**
     * Add a script to the manager.
     *
     * @param script  The script to add.
     */
    public boolean addScript(S script) {
        PreCon.notNull(script);

        _scripts.put(script.getName().toLowerCase(), script);
        return true;
    }

    /**
     * Remove a script.
     *
     * <p>Also disposes and removes evaluated.</p>
     *
     * @param script  The script to remove.
     */
    public boolean removeScript(S script) {
        return removeScript(script.getName());
    }

    /**
     * Remove a script by name.
     *
     * <p>Also disposes and removes evaluated.</p>
     *
     * @param scriptName  The name of the script.
     */
    public boolean removeScript(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        if (_scripts.remove(scriptName.toLowerCase()) != null) {

            IEvaluatedScript evaluated = _evaluated.remove(scriptName.toLowerCase());
            if (evaluated != null) {
                evaluated.resetApi();
            }
            return true;
        }
        return false;
    }

    /**
     * Get a script by name.
     *
     * @param scriptName  The name of the script.
     */
    @Nullable
    public S getScript(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        return _scripts.get(scriptName.toLowerCase());
    }

    /**
     * Get an evaluated script by script name.
     *
     * @param scriptName  The name of the script.
     */
    @Nullable
    public E getEvaluated(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        return _evaluated.get(scriptName.toLowerCase());
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
    public List<S> getScripts() {
        return new ArrayList<>(_scripts.values());
    }

    /**
     * Get all evaluated scripts.
     */
    public List<E> getEvaluated() {
        return new ArrayList<>(_evaluated.values());
    }

    /**
     * Clear all scripts including evaluated.
     */
    public void clearScripts() {
        _scripts.clear();
        clearEvaluated();
    }

    /*
     * Add an api to be used in all evaluated scripts.
     */
    public void addScriptApi(IScriptApi api) {
        PreCon.notNull(api);

        _apiMap.put(api.getVariableName(), api);
    }

    /*
     * Add api to be used in all evaluated scripts.
     */
    public void addScriptApi(Collection<IScriptApi> apiCollection) {
        PreCon.notNull(apiCollection);

        for (IScriptApi api : apiCollection ) {
            addScriptApi(api);
        }
    }

    /*
     *  Get a script api by its variable name.
     */
    public IScriptApi getScriptApi(String apiVariableName) {
        PreCon.notNullOrEmpty(apiVariableName);

        return _apiMap.get(apiVariableName);
    }

    /*
     *  Get all script api.
     */
    public List<IScriptApi> getScriptApis() {

        return new ArrayList<>(_apiMap.values());
    }

    /**
     * Remove a script api. Does not remove the api from
     * currently evaluated scripts.
     *
     * @param scriptApi  The script api to remove.
     *
     * @return  True if found and removed.
     */
    public boolean removeScriptApi(IScriptApi scriptApi) {
        PreCon.notNull(scriptApi);

        return _apiMap.remove(scriptApi.getVariableName()) != null;
    }

    /**
     * Remove a script api. Does not remove the api from
     * currently evaluated scripts.
     *
     * @param apiVariableName  The script api variable name.
     *
     * @return  True if found and removed.
     */
    public boolean removeScriptApi(String apiVariableName) {
        PreCon.notNullOrEmpty(apiVariableName);

        return _apiMap.remove(apiVariableName) != null;
    }

    /**
     * Remove all script api. Does not remove the api's
     * from currently evaluated scripts.
     */
    public void clearScriptApi() {
        _apiMap.clear();
    }

    /*
     * Reload scripts and re-evaluate.
     */
    public void reload() {
        loadScripts();
        evaluate();
    }

    /*
     * Called to get the script constructor.
     */
    public abstract ScriptConstructor<S> getScriptConstructor();


    /**
     * Called to evaluate a script.
     *
     * @param script  The script to evaluate.
     * @param api     The api to include.
     */
    @Nullable
    protected E callEvaluate(S script, Collection<IScriptApi> api) {
        @SuppressWarnings("unchecked")
        E evaluated = (E)script.evaluate(api);
        return evaluated;
    }

    /*
     * Clear all evaluated scripts.
     */
    private void clearEvaluated() {
        for (IEvaluatedScript evaluated : _evaluated.values()) {
            evaluated.resetApi();
        }

        _evaluated.clear();
    }

}
