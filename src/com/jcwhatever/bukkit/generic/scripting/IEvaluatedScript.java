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

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;

import java.util.List;
import javax.script.ScriptEngine;

/**
 * A script that has been evaluated by a script engine.
 */
public interface IEvaluatedScript {

    /**
     * Get the script that was evaluated.
     */
    IScript getParentScript();

    /**
     * Get the scripting engine used to evaluate.
     */
    ScriptEngine getScriptEngine();

    /**
     * Get included script api.
     */
    List<IScriptApi> getScriptApi();

    /**
     * Add a script api.
     */
    void addScriptApi(IScriptApi scriptApi, String variableName);

    /**
     * Invoke a function in the script.
     *
     * @param functionName  The name of the function.
     * @param parameters    Function parameters.
     *
     * @return  Object returned by the function.
     */
    Object invokeFunction(String functionName, Object... parameters) throws NoSuchMethodException;

    /**
     * Evaluate a script into the evaluated script.
     *
     * @param script  The script to evaluated.
     */
    Object evaluate(IScript script);

    /**
     * Reset all API's.
     */
    void resetApi();
}
