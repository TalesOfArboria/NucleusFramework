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


package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import org.bukkit.plugin.Plugin;

/**
 * Abstract implementation of an api variable within a script.
 */
public abstract class GenericsScriptApi implements IScriptApi {

    private final Plugin _plugin;
    private final ScriptApiInfo _info;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin
     */
    public GenericsScriptApi(Plugin plugin) {
        _plugin = plugin;

        ScriptApiInfo info = getClass().getAnnotation(ScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Script api missing its IScriptApiInfo annotation.");

        _info = info;

    }

    /**
     * Get the owning plugin.
     */
    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public final String getVariableName() {
        return _info.variableName();
    }
}
