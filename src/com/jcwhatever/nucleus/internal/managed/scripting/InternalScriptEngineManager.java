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
public final class InternalScriptEngineManager extends ScriptEngineManager {

    @Override
    @Nullable
    public ScriptEngine getEngineByName(String shortName) {
        PreCon.notNullOrEmpty(shortName);

        return super.getEngineByName(shortName);
    }

    @Override
    @Nullable
    public ScriptEngine getEngineByExtension(String extension) {
        PreCon.notNullOrEmpty(extension);

        return super.getEngineByExtension(extension);
    }

    @Override
    @Nullable
    public ScriptEngine getEngineByMimeType(String mimeType) {
        PreCon.notNullOrEmpty(mimeType);

        return super.getEngineByMimeType(mimeType);
    }

    @Override
    public void registerEngineName(String name, ScriptEngineFactory factory) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(factory);

        super.registerEngineName(name, factory);
    }

    @Override
    public void registerEngineMimeType(String type, ScriptEngineFactory factory) {
        PreCon.notNullOrEmpty(type);
        PreCon.notNull(factory);

        super.registerEngineMimeType(type, factory);
    }

    @Override
    public void registerEngineExtension(String extension, ScriptEngineFactory factory) {
        PreCon.notNullOrEmpty(extension);
        PreCon.notNull(factory);

        super.registerEngineExtension(extension, factory);
    }

    void reload() {
        // do nothing
    }
}
