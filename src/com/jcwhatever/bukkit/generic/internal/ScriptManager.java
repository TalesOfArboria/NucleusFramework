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

package com.jcwhatever.bukkit.generic.internal;

import com.jcwhatever.bukkit.generic.scripting.AbstractScriptManager;
import com.jcwhatever.bukkit.generic.scripting.GenericsScript;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScript;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.ScriptUtils.IScriptFactory;

import org.bukkit.plugin.Plugin;

import java.io.File;
import javax.annotation.Nullable;

/**
 * GenericsLib default ScriptManager.
 */
public class ScriptManager extends AbstractScriptManager<IScript, IEvaluatedScript> {

    private static IScriptFactory<IScript> _scriptFactory = new IScriptFactory<IScript>() {
        @Override
        public IScript construct(String name, @Nullable File file, String type, String script) {
            return new GenericsScript(name, file, type, script);
        }
    };

    public ScriptManager(Plugin plugin, File scriptFolder) {
        super(plugin, scriptFolder, DirectoryTraversal.RECURSIVE);
    }

    @Override
    public IScriptFactory<IScript> getScriptConstructor() {
        return _scriptFactory;
    }
}
