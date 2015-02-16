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

package com.jcwhatever.nucleus.scripting;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * Extends {@link ScriptEngineManager} and provides singleton {@link javax.script.ScriptEngine}
 * instances.
 */
public class NucleusScriptEngineManager extends ScriptEngineManager {

    private final Map<String, ScriptEngine> _namedEngines = new HashMap<>(5);
    private final Map<String, ScriptEngine> _extEngines = new HashMap<>(15);
    private final Map<String, ScriptEngine> _mimeEngines = new HashMap<>(15);

    @Override
    @Nullable
    public ScriptEngine getEngineByName(String shortName) {
        PreCon.notNullOrEmpty(shortName);

        ScriptEngine engine = _namedEngines.get(shortName);
        if (engine != null)
            return engine;

        engine = super.getEngineByName(shortName);
        if (engine != null) {
            storeEngine(engine);
        }

        return engine;
    }

    @Override
    @Nullable
    public ScriptEngine getEngineByExtension(String extension) {
        PreCon.notNullOrEmpty(extension);

        ScriptEngine engine = _extEngines.get(extension.toLowerCase());
        if (engine != null) {
            return engine;
        }

        engine = super.getEngineByExtension(extension);
        if (engine != null) {
            storeEngine(engine);
        }

        return engine;
    }

    @Override
    @Nullable
    public ScriptEngine getEngineByMimeType(String mimeType) {
        PreCon.notNullOrEmpty(mimeType);

        ScriptEngine engine = _extEngines.get(mimeType);
        if (engine != null) {
            return engine;
        }

        engine = super.getEngineByMimeType(mimeType);
        if (engine != null) {
            storeEngine(engine);
        }

        return engine;

    }

    @Override
    public void registerEngineName(String name, ScriptEngineFactory factory) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(factory);

        if (_namedEngines.containsKey(name)) {
            throw new IllegalArgumentException("An engine named '" + name + "' is already in " +
                    "use and cannot be replaced.");
        }

        super.registerEngineName(name, factory);
    }

    @Override
    public void registerEngineMimeType(String type, ScriptEngineFactory factory) {
        PreCon.notNullOrEmpty(type);
        PreCon.notNull(factory);

        if (_mimeEngines.containsKey(type)) {
            throw new IllegalArgumentException("An engine with mime type '" + type + "' is already in " +
                    "use and cannot be replaced.");
        }

        super.registerEngineMimeType(type, factory);
    }

    @Override
    public void registerEngineExtension(String extension, ScriptEngineFactory factory) {
        PreCon.notNullOrEmpty(extension);
        PreCon.notNull(factory);

        if (_extEngines.containsKey(extension)) {
            throw new IllegalArgumentException("An engine using extension '" + extension + "' is already in " +
                    "use and cannot be replaced.");
        }

        super.registerEngineExtension(extension, factory);
    }

    private void storeEngine(ScriptEngine engine) {
        String engineName = engine.getFactory().getEngineName();
        List<String> extensions = engine.getFactory().getExtensions();
        List<String> mimeTypes = engine.getFactory().getMimeTypes();

        _namedEngines.put(engineName, engine);

        for (String ext : extensions) {
            _extEngines.put(ext.toLowerCase(), engine);
        }

        for (String type : mimeTypes) {
            _mimeEngines.put(type, engine);
        }
    }
}
