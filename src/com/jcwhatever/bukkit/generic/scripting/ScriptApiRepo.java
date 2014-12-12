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

import com.jcwhatever.bukkit.generic.internal.InternalScriptApiRepo;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Global repository of script API's that can be used
 * by any plugins script manager.
 */
public class ScriptApiRepo {

    private ScriptApiRepo() {}

    /**
     * Register a script api with the repository so
     * other plugins can use it.
     *
     * @param plugin    The api owning plugin.
     * @param apiClass  The api type to register.
     */
    public static boolean registerApiType(Plugin plugin, Class<? extends IScriptApi> apiClass) {
        return InternalScriptApiRepo.get().registerApiType(plugin, apiClass);
    }

    /**
     * Unregister a script api from the repository.
     *
     * @param plugin    The api owning plugin.
     * @param apiClass  The api type to remove.
     */
    public static boolean unregisterApiType(Plugin plugin, Class<? extends IScriptApi> apiClass) {
        return InternalScriptApiRepo.get().unregisterApiType(plugin, apiClass);
    }

    /**
     * Get an api from the repository by the name of its owning plugin
     * and variable name and instantiate for the specified plugin.
     *
     * @param plugin            The plugin to instantiate the api for.
     * @param owningPluginName  The name of the api owning plugin.
     * @param variableName      Case sensitive variable name.
     */
    @Nullable
    public static IScriptApi getApi(Plugin plugin, String owningPluginName, String variableName) {
        return InternalScriptApiRepo.get().getApi(plugin, owningPluginName, variableName);
    }

    /**
     * Get an api from the repository by its
     * owning plugin and variable name and instantiate it
     * for the owning plugin.
     *
     * @param plugin        The api owning plugin and plugin to instantiate for.
     * @param variableName  Case sensitive variable name.
     */
    @Nullable
    public static IScriptApi getApi(Plugin plugin, String variableName) {
        return InternalScriptApiRepo.get().getApi(plugin, variableName);
    }
}
