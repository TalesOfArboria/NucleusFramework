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
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Generics default {@code IScript} implementation
 */
public class GenericsScript implements IScript {

    private final GenericsScriptManager _manager;
    private final String _name;
    private final String _type;
    private final String _script;

    /**
     * Constructor.
     *
     * @param name    The name of the script.
     * @param type    The script type.
     * @param script  The script source.
     */
    public GenericsScript(GenericsScriptManager manager, String name, String type, String script) {
        PreCon.notNull(manager);
        PreCon.notNullOrEmpty(name);
        PreCon.notNullOrEmpty(type);
        PreCon.notNull(script);

        _manager = manager;
        _name = name;
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

        ScriptEngine engine = _manager.getEngineManager().getEngineByExtension(getType());
        if (engine == null)
            return null;

        GenericsEvaluatedScript script = new GenericsEvaluatedScript(this, engine, apiCollection);

        try {
            // evaluate script
            engine.eval(getScript());

            return script;

        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

}
