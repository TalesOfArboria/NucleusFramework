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

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.Collection;
import javax.annotation.Nullable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Generics default {@code IScript} implementation
 */
public class GenericsScript implements IScript {

    private final GenericsScriptManager _manager;
    private final String _name;
    private final String _filename;
    private final String _type;
    private final String _script;

    /**
     * Constructor.
     *
     * <p>Is evaluated with the global engine manager.</p>
     *
     * @param name    The name of the script.
     * @param type    The script type.
     * @param script  The script source.
     */
    public GenericsScript(String name, @Nullable String filename, String type, String script) {
        this(null, name, filename, type, script);
    }


    /**
     * Constructor.
     *
     * @param manager   The scripts owning manager. Script engine from manager is used.
     * @param name      The name of the script.
     * @param filename  The name of the file the script is from.
     * @param type      The script type.
     * @param script    The script source.
     */
    public GenericsScript(@Nullable GenericsScriptManager manager,
                          String name, @Nullable String filename, String type, String script) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNullOrEmpty(type);
        PreCon.notNull(script);

        _manager = manager;
        _name = name;
        _filename = filename;
        _type = type;
        _script = script;
    }

    /**
     * Get the name of the script.
     */
    @Override
    public String getName() {
        return _name;
    }

    @Nullable
    @Override
    public String getFilename() {
        return _filename;
    }

    /**
     * Get the script source.
     */
    @Override
    public String getScript() {
        return _script;
    }

    /**
     * Get the script type. (file extension)
     */
    @Override
    public String getType() {
        return _type;
    }

    /**
     * Evaluate the script.
     *
     * @param apiCollection  The api to include.
     */
    @Override
    @Nullable
    public IEvaluatedScript evaluate(@Nullable Collection<? extends IScriptApi> apiCollection) {

        ScriptEngine engine = getScriptEngine();
        if (engine == null) {
            return null;
        }

        IEvaluatedScript script = instantiateEvaluatedScript(engine, apiCollection);

        if (!eval(engine, script.getContext())) {
            return null;
        }

        return script;
    }

    /**
     * Get a script engine for the script.
     *
     * @return  Null if an engine could not be found.
     */
    @Nullable
    protected ScriptEngine getScriptEngine() {
        ScriptEngineManager engineManager = _manager != null
                ? _manager.getEngineManager()
                : ScriptHelper.getGlobalEngineManager();

        return engineManager.getEngineByExtension(getType());
    }

    /**
     * Called to instantiate a new evaluated script.
     *
     * @param engine         The scripts engine.
     * @param apiCollection  Optional initial api collection.
     */
    protected IEvaluatedScript instantiateEvaluatedScript(ScriptEngine engine,
                                                          @Nullable Collection<? extends IScriptApi> apiCollection) {
        return new GenericsEvaluatedScript(this, engine, apiCollection);
    }

    /**
     * Called to evaluate the script into the specified engine.
     *
     * @param engine   The engine to evaluate the script into.
     * @param context  The script context.
     *
     * @return Null if evaluation failed.
     */
    protected boolean eval(ScriptEngine engine, ScriptContext context) {

        if (_filename != null)
            engine.put(ScriptEngine.FILENAME, _filename);

        try {
            // evaluate script
            engine.eval(getScript(), context);

            return true;

        } catch (ScriptException e) {
            e.printStackTrace();
            return false;
        }
    }

}
