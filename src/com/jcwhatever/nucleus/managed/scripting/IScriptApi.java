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


package com.jcwhatever.nucleus.managed.scripting;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.IPluginOwned;

import org.bukkit.plugin.Plugin;

/**
 * Represents a script API object factory.
 *
 * <p>Used to retrieve an API object which is inserted into a script using
 * a specified variable name.</p>
 *
 * @see SimpleScriptApi
 * @see IScriptApiRepo
 */
public interface IScriptApi extends IPluginOwned, INamed {

    /**
     * Create a new instance of the API object for a specific script and plugin.
     *
     * @param plugin  The plugin the API is being instantiated for.
     * @param script  The script the API is being instantiated for.
     */
    public IDisposable createApi(Plugin plugin, IEvaluatedScript script);
}
