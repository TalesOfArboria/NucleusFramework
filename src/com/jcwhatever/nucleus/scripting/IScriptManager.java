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

import java.io.File;
import java.util.List;
import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;

/**
 * Interface for a script manager.
 *
 * @see com.jcwhatever.nucleus.Nucleus#getScriptManager
 */
public interface IScriptManager {

    /**
     * Get the script folder.
     *
     * @return  Null if a script folder was not specified in the constructor.
     */
    File getScriptFolder();

    /**
     * Get the folder where include scripts are kept.
     *
     * <p>Include scripts are not automatically evaluated by the script manager.</p>
     */
    File getIncludeFolder();

    /**
     * Get the script engine manager.
     */
    public ScriptEngineManager getEngineManager();

    /*
     * Reload scripts and re-evaluate.
     */
    void reload();

    /**
     * Add a script to the manager.
     *
     * @param script  The script to add.
     */
    boolean addScript(IScript script);

    /**
     * Remove a script.
     *
     * <p>Also disposes and removes evaluated.</p>
     *
     * @param script  The script to remove.
     */
    boolean removeScript(IScript script);

    /**
     * Remove a script by name.
     *
     * <p>Also disposes and removes evaluated.</p>
     *
     * @param scriptName  The name of the script.
     */
    boolean removeScript(String scriptName);

    /**
     * Get a script by name.
     *
     * @param scriptName  The name of the script.
     */
    @Nullable
    IScript getScript(String scriptName);

    /**
     * Get an evaluated script by script name.
     *
     * @param scriptName  The name of the script.
     */
    @Nullable
    IEvaluatedScript getEvaluated(String scriptName);

    /**
     * Get the names of all scripts.
     */
    List<String> getScriptNames();

    /**
     * Get all scripts.
     */
    List<IScript> getScripts();

    /**
     * Get all evaluated scripts.
     */
    List<IEvaluatedScript> getEvaluated();

    /**
     * Clear all scripts including evaluated.
     */
    void clearScripts();

    /*
     * Invoked to get the script factory.
     */
    IScriptFactory getScriptFactory();
}
