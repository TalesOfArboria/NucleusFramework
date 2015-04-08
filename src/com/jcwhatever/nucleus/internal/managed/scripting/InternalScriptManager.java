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

package com.jcwhatever.nucleus.internal.managed.scripting;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_ActionBar;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Depends;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Economy;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Events;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Include;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Inventory;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_ItemBank;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Jails;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Msg;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_NpcProvider;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Permissions;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Rand;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Regions;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Scheduler;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Sounds;
import com.jcwhatever.nucleus.internal.managed.scripting.api.SAPI_Titles;
import com.jcwhatever.nucleus.internal.managed.scripting.api.views.SAPI_MenuView;
import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.managed.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.managed.scripting.IScript;
import com.jcwhatever.nucleus.managed.scripting.IScriptApi;
import com.jcwhatever.nucleus.managed.scripting.IScriptFactory;
import com.jcwhatever.nucleus.managed.scripting.IScriptManager;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi.IApiObjectCreator;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.ScriptUtils;
import com.jcwhatever.nucleus.utils.file.FileUtils.DirectoryTraversal;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * NucleusFramework's default ScriptManager.
 */
public final class InternalScriptManager implements IScriptManager {

    private static IScriptFactory _scriptFactory = new IScriptFactory() {
        @Override
        public IScript construct(String name, @Nullable File file, String type, String script) {
            return new Script(name, file, type, script);
        }
    };

    private final File _scriptFolder;
    private final File _includeFolder;

    // key is script name
    private final Map<String, IScript> _scripts = new HashMap<>(25);

    // key is script name
    private final Map<String, IEvaluatedScript> _evaluated = new HashMap<>(25);

    // default script apis included in all evaluated scripts
    private final List<IScriptApi> _api = new ArrayList<>(15);

    public InternalScriptManager(File scriptFolder) {
        PreCon.notNull(scriptFolder);

        _scriptFolder = scriptFolder;
        _includeFolder = new File(scriptFolder, "includes");

        if (!_includeFolder.exists() && !_includeFolder.mkdirs()) {
            throw new RuntimeException("Failed to create script includes folder.");
        }

        loadDefaultApi();
    }

    @Override
    public File getScriptFolder() {
        return _scriptFolder;
    }

    @Override
    public File getIncludeFolder() {
        return _includeFolder;
    }

    @Override
    public ScriptEngineManager getEngineManager() {
        return Nucleus.getScriptEngineManager();
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

        if (!_scriptFolder.exists())
            return;

        List<IScript> scripts = ScriptUtils.loadScripts(Nucleus.getPlugin(),
                getEngineManager(), _scriptFolder, _includeFolder,
                DirectoryTraversal.RECURSIVE,
                getScriptFactory());

        for (IScript script : scripts) {
            addScript(script);
        }
    }

    /**
     * Evaluates all scripts.
     *
     * <p>If a script is already evaluated, it is disposed and re-evaluated.</p>
     */
    public void evaluate() {

        for (IScript script : _scripts.values()) {
            evaluate(script);
        }
    }

    /**
     * Evaluates a script.
     *
     * <p>Ensures the <em>evaluated</em> script is stored by the manager.</p>
     */
    public boolean evaluate(IScript script) {

        IEvaluatedScript current = _evaluated.remove(script.getName().toLowerCase());
        if (current != null)
            current.dispose();

        IEvaluatedScript evaluated = script.evaluate(_api);
        if (evaluated == null)
            return false;

        _evaluated.put(script.getName().toLowerCase(), evaluated);

        return true;
    }

    public boolean reload(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        IScript script = getScript(scriptName);
        if (script == null)
            return false;

        if (script.getFile() != null) {
            script = ScriptUtils.loadScript(Nucleus.getPlugin(),
                    _scriptFolder, script.getFile(), getScriptFactory());
            if (script == null)
                return false;

            _scripts.put(script.getName().toLowerCase(), script);
        }

        evaluate(script);
        return true;
    }

    @Override
    public void reload() {
        loadScripts();
        evaluate();

        Scheduler.runTaskLater(Nucleus.getPlugin(), 20, new Runnable() {
            @Override
            public void run() {
                // remove long lived objects
                System.gc();
            }
        });
    }

    @Override
    public boolean addScript(IScript script) {
        PreCon.notNull(script);

        _scripts.put(script.getName().toLowerCase(), script);
        return true;
    }

    @Override
    public boolean removeScript(IScript script) {
        return removeScript(script.getName());
    }

    @Override
    public boolean removeScript(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        if (_scripts.remove(scriptName.toLowerCase()) != null) {

            IEvaluatedScript evaluated = _evaluated.remove(scriptName.toLowerCase());
            if (evaluated != null) {
                evaluated.dispose();
            }
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public IScript getScript(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        return _scripts.get(scriptName.toLowerCase());
    }

    @Override
    @Nullable
    public IEvaluatedScript getEvaluated(String scriptName) {
        PreCon.notNullOrEmpty(scriptName);

        return _evaluated.get(scriptName.toLowerCase());
    }

    @Override
    public List<String> getScriptNames() {
        return new ArrayList<String>(_scripts.keySet());
    }

    @Override
    public List<IScript> getScripts() {
        return new ArrayList<>(_scripts.values());
    }

    @Override
    public List<IEvaluatedScript> getEvaluated() {
        return new ArrayList<>(_evaluated.values());
    }

    @Override
    public void clearScripts() {
        _scripts.clear();
        clearEvaluated();
    }

    @Override
    public IScriptFactory getScriptFactory() {
        return _scriptFactory;
    }

    /*
     * Clear all evaluated scripts.
     */
    private void clearEvaluated() {
        for (IEvaluatedScript evaluated : _evaluated.values()) {
            try {
                evaluated.dispose();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }

        _evaluated.clear();
    }

    private void loadDefaultApi() {

        Plugin plugin = Nucleus.getPlugin();

        _api.add(new SimpleScriptApi(plugin, "econ", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Economy();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "events", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Events(plugin);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "inventory", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Inventory();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "itemBank", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_ItemBank();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "jails", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Jails();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "msg", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {

                IMessenger messenger = Nucleus.getMessengerFactory().create(plugin);
                return new SAPI_Msg(messenger);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "permissions", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Permissions();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "sounds", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Sounds(plugin);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "depends", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Depends();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "rand", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Rand();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "scheduler", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Scheduler(plugin);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "include", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Include(plugin, script, InternalScriptManager.this);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "titles", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Titles();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "actionBars", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_ActionBar();
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "npcProvider", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_NpcProvider(plugin);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "menuViews", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_MenuView(plugin);
            }
        }));
        _api.add(new SimpleScriptApi(plugin, "regions", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new SAPI_Regions();
            }
        }));
    }
}
