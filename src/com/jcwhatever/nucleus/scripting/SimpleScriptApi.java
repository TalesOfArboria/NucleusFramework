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

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

/**
 * Simple implementation of a script API.
 */
public class SimpleScriptApi implements IScriptApi {

    private final Plugin _plugin;
    private final String _name;
    private final IApiObjectCreator _creator;

    /**
     * Constructor.
     *
     * @param plugin   The owning plugin
     * @param name     The name of the API. Also used as the default script variable name.
     * @param creator  Use to create new instances of the disposable API object.
     */
    public SimpleScriptApi(Plugin plugin, String name, IApiObjectCreator creator) {
        PreCon.notNull(plugin);
        PreCon.notNull(creator);

        _plugin = plugin;
        _name = name;
        _creator = creator;
    }

    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public IDisposable createApi(Plugin plugin, IEvaluatedScript script) {
        return _creator.create(plugin, script);
    }

    public interface IApiObjectCreator {
        IDisposable create(Plugin plugin, IEvaluatedScript script);
    }
}
