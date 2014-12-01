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
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 * Generics default {@code IEvaluatedScript} implementation.
 *
 * <p>An evaluated script</p>
 */
public class GenericsEvaluatedScript implements IEvaluatedScript {

    private final IScript _parentScript;
    private final ScriptEngine _engine;
    private ScriptContext _context;
    private final Map<String, IScriptApi> _scriptApis;
    private final Set<Class<? extends IScriptApi>> _included;
    private final List<IScriptApiObject> _apiObjects = new ArrayList<>(25);

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param parentScript  The script that was evaluated.
     * @param engine        The engine that evaluated the script.
     * @param scriptApis    The api that was included.
     */
    public GenericsEvaluatedScript (IScript parentScript, ScriptEngine engine,
                                    @Nullable Collection<? extends IScriptApi> scriptApis) {

        PreCon.notNull(parentScript);
        PreCon.notNull(engine);

        _parentScript = parentScript;
        _engine = engine;
        _scriptApis = new HashMap<>(scriptApis == null ? 10 : scriptApis.size() + 10);
        _included = new HashSet<>(scriptApis == null ? 10 : scriptApis.size() + 10);

        if (scriptApis != null) {
            for (IScriptApi api : scriptApis) {
                addScriptApi(api, api.getVariableName());
            }
        }
    }

    /**
     * Get the script that was evaluated.
     */
    @Override
    public IScript getParentScript() {
        return _parentScript;
    }

    /**
     * Get the script engine that evaluated the script.
     */
    @Override
    public ScriptEngine getScriptEngine() {
        return _engine;
    }

    /**
     * Get the script api included during evaluation.
     */
    @Override
    public List<IScriptApi> getScriptApi() {
        return new ArrayList<>(_scriptApis.values());
    }

    /**
     * Get the script context.
     */
    @Override
    public ScriptContext getContext() {
        if (_context == null) {
            _context = createContext();
        }
        return _context;
    }

    /**
     * Add a script api.
     */
    @Override
    public void addScriptApi(IScriptApi scriptApi, String variableName) {
        PreCon.notNull(scriptApi);
        PreCon.notNullOrEmpty(variableName);

        if (_included.contains(scriptApi.getClass()))
            return;

        if (_scriptApis.containsKey(variableName))
            return;

        _scriptApis.put(scriptApi.getVariableName(), scriptApi);
        _included.add(scriptApi.getClass());

        IScriptApiObject apiObject = scriptApi.getApiObject(this);
        getContext().setAttribute(variableName, apiObject, ScriptContext.ENGINE_SCOPE);

        _apiObjects.add(apiObject);
    }

    /**
     * Invoke a function in the evaluated script.
     *
     * @param functionName  The name of the function.
     * @param parameters    Function parameters.
     */
    @Override
    @Nullable
    public Object invokeFunction(String functionName, Object... parameters)
            throws NoSuchMethodException {

        Invocable inv = (Invocable)_engine;

        try {
            return inv.invokeFunction(functionName, parameters);
        }
        catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Evaluate another script into the scripts engine.
     *
     * @param script  The script to evaluated.
     */
    @Override
    @Nullable
    public Object evaluate(IScript script) {
        return evaluate(script, getContext());
    }

    /**
     * Evaluate another script into the scripts engine
     * using a custom context.
     *
     * @param script   The script to evaluated.
     * @param context  The context to use.
     */
    @Override
    @Nullable
    public Object evaluate(IScript script, ScriptContext context) {

        if (script.getFilename() != null) {
            _engine.put(ScriptEngine.FILENAME, script.getFilename());
        }

        try {
            return _engine.eval(script.getScript(), context);
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        resetApi();

        _isDisposed = true;
        _scriptApis.clear();
    }

    /**
     * Reset the included api.
     */
    @Override
    public void resetApi() {

        try {
            invokeFunction("onScriptReset");
        } catch (NoSuchMethodException ignore) {
            // do nothing
        }

        for (IScriptApiObject api : _apiObjects) {
            api.reset();
        }
    }

    /**
     * Called to get a context for the script.
     */
    protected ScriptContext createContext() {
        return new SimpleScriptContext();
    }

}
