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


package com.jcwhatever.generic.scripting;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.scripting.api.IScriptApi;
import com.jcwhatever.generic.utils.PreCon;

import java.io.File;
import java.util.Collection;
import javax.annotation.Nullable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Generics default {@code IScript} implementation
 */
public class GenericsScript implements IScript {

    private final String _name;
    private final File _file;
    private final String _type;
    private final String _script;

    /**
     * Constructor.
     *
     * @param name      The name of the script.
     * @param file      The name of the file the script is from.
     * @param type      The script type.
     * @param script    The script source.
     */
    public GenericsScript(String name, @Nullable File file, String type, String script) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNullOrEmpty(type);
        PreCon.notNull(script);

        _name = name;
        _file = file;
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

    /**
     * Get the file the script is from.
     *
     * @return Null if the script did not come from a file.
     */
    @Nullable
    @Override
    public File getFile() {
        return _file;
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
        return GenericsLib.getScriptEngineManager().getEngineByExtension(getType());
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

        if (_file != null)
            engine.put(ScriptEngine.FILENAME, _file.getName() + " (" + _name + ')');

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
