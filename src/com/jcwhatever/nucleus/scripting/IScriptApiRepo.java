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

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Repository of script API's that can be instantiated and retrieved.
 *
 * @see com.jcwhatever.nucleus.Nucleus#getScriptApiRepo
 */
public interface IScriptApiRepo {

    /**
     * Register a script API with the repository so other plugins can use it.
     *
     * @param api  The api to register.
     */
    boolean registerApi(IScriptApi api);

    /**
     * Unregister a script API from the repository.
     *
     * <p>Plugins are expected to unregister their API types
     * when they are disabled.</p>
     *
     * @param api  The API to remove.
     */
    boolean unregisterApi(IScriptApi api);

    /**
     * Get an API from the repository by the name of its owning plugin
     * and variable name and instantiate for the specified plugin.
     *
     * @param pluginName    The name of the API's owning plugin.
     * @param variableName  Case sensitive variable name.
     *
     * @return  An {@link IScriptApi} instance or null if not found.
     */
    @Nullable
    IScriptApi get(String pluginName, String variableName);

    /**
     * Get an API from the repository by its owning plugin and variable name and
     * instantiate it for the owning plugin.
     *
     * @param plugin        The API's owning plugin.
     * @param variableName  Case sensitive variable name.
     *
     * @return  The {@link IScriptApi} instance or null if not found.
     */
    @Nullable
    IScriptApi get(Plugin plugin, String variableName);

    /**
     * Unregister all script API's registered by the specified plugin.
     *
     * @param plugin  The plugin.
     */
    void unregisterPlugin(Plugin plugin);
}
