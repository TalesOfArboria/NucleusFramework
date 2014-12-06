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
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        _scriptApis.put(scriptApi.getVariableName(), scriptApi);

        IScriptApiObject apiObject = scriptApi.getApiObject(this);
        //_engine.put(variableName, apiObject);
        getContext().setAttribute(variableName, apiObject, ScriptContext.ENGINE_SCOPE);

        _apiObjects.add(apiObject);
    }

    /**
     * Determine if the script engine allows script functions
     * to be invoked via {@code javax.script.Invocable} interface.
     */
    @Override
    public boolean canInvoke() {
        return _engine instanceof Invocable;
    }

    /**
     * Invoke a function in the evaluated script using the
     * {@code javax.script.Invocable} interface.
     *
     * @param functionName  The name of the function.
     * @param parameters    Function parameters.
     */
    @Override
    @Nullable
    public Object invokeFunction(String functionName, Object... parameters)
            throws NoSuchMethodException {

        if (!(_engine instanceof Invocable))
            return null;

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
        if (script.getFile() != null) {
            _engine.put(ScriptEngine.FILENAME, script.getFile().getName());
        }

        try {
            return _engine.eval(script.getScript(), getContext());
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        try {
            invokeFunction("onScriptDispose");
        } catch (NoSuchMethodException ignore) {
            // do nothing
        }

        for (IScriptApiObject api : _apiObjects) {
            api.dispose();
        }

        _isDisposed = true;
        _scriptApis.clear();
    }

    /**
     * Called to get a context for the script.
     */
    protected ScriptContext createContext() {

        Class<?> contextClazz = getScriptEngine().getContext().getClass();
        try {

            // some engines require their own script context implementation,
            // try instantiating a new script context using the type from the engine.
            Constructor<?> constructor = contextClazz.getDeclaredConstructor();
            return (ScriptContext)constructor.newInstance();

        } catch (NoSuchMethodException | InvocationTargetException |
                InstantiationException | IllegalAccessException e) {
            Messenger.debug(GenericsLib.getLib(), "Failed to create new script context using current context type." +
                    "Using SimpleScriptContext instead.");

            // if failed, use a SimpleScriptContext
            return new SimpleScriptContext();
        }
    }

}
