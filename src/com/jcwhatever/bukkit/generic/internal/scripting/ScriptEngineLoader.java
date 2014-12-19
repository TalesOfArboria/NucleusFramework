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

package com.jcwhatever.bukkit.generic.internal.scripting;

import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.modules.JarModuleLoader;

import java.util.List;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public final class ScriptEngineLoader extends JarModuleLoader<ScriptEngineFactory> {

    private final ScriptEngineManager _engineManager;

    /**
     * Constructor.
     */
    public ScriptEngineLoader(ScriptEngineManager engineManager) {
        super(ScriptEngineFactory.class, new ScriptEngineLoaderSettings());

        _engineManager = engineManager;
    }

    @Override
    public void loadModules() {

        super.loadModules();

        List<ScriptEngineFactory> factories = getModules();

        for (ScriptEngineFactory factory : factories) {
            _engineManager.registerEngineName(factory.getEngineName(), factory);

            List<String> extensions = factory.getExtensions();
            for (String ext : extensions) {
                _engineManager.registerEngineExtension(ext, factory);
            }

            List<String> mimes = factory.getMimeTypes();
            for (String mime : mimes) {
                _engineManager.registerEngineExtension(mime, factory);
            }

            Msg.info("Loaded script engine: {0}", factory.getEngineName());
        }
    }
}
