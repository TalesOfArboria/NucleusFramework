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


package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.scripting.IScript;
import com.jcwhatever.nucleus.scripting.IScriptFactory;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.file.FileUtils.DirectoryTraversal;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Script utilities.
 *
 * @see com.jcwhatever.nucleus.Nucleus#getScriptManager
 */
public final class ScriptUtils {

    private ScriptUtils() {}

    private static final Pattern PATTERN_LEADING_DOT = Pattern.compile("^\\.");

    /**
     * Load scripts from a script folder.
     *
     * @param plugin         The scripts owning plugin.
     * @param engineManager  The engine manager used to determine if a file type is a script.
     * @param scriptFolder   The folder to find scripts in.
     * @param traversal      The type of directory traversal used to find script files.
     * @param scriptFactory  A script constructor to create new script instances.
     */
    public static List<IScript> loadScripts(Plugin plugin,
                                            ScriptEngineManager engineManager,
                                            File scriptFolder,
                                            DirectoryTraversal traversal,
                                            IScriptFactory scriptFactory) {

        return loadScripts(plugin, engineManager, scriptFolder, null, traversal, scriptFactory);
    }

    /**
     * Load scripts from a script folder.
     *
     * @param plugin         The scripts owning plugin.
     * @param engineManager  The engine manager used to determine if a file type is a script.
     * @param scriptFolder   The folder to find scripts in.
     * @param exclude        Optional file or folder to exclude.
     * @param traversal      The type of directory traversal used to find script files.
     * @param scriptFactory  A script constructor to create new script instances.
     *
     */
    public static List<IScript> loadScripts(Plugin plugin,
                                            ScriptEngineManager engineManager,
                                            File scriptFolder,
                                            @Nullable File exclude,
                                            DirectoryTraversal traversal,
                                            IScriptFactory scriptFactory) {
        PreCon.notNull(plugin);
        PreCon.notNull(scriptFolder);
        PreCon.isValid(scriptFolder.isDirectory());

        if (!scriptFolder.exists())
            return new ArrayList<>(0);

        List<File> scriptFiles = FileUtils.getFiles(scriptFolder, traversal);
        List<IScript> result = new ArrayList<>(scriptFiles.size());

        for (File file : scriptFiles) {

            if (exclude != null && file.getAbsolutePath().startsWith(exclude.getAbsolutePath())) {
                continue;
            }

            String type = getScriptType(file);
            if (type == null || type.isEmpty())
                continue;

            if (engineManager.getEngineByExtension(type) == null)
                continue;

            String name = getScriptName(scriptFolder, file);

            try {

                BufferedReader reader = new BufferedReader(new FileReader(file));

                StringBuilder buffer = new StringBuilder(2000);
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }

                reader.close();

                IScript script = scriptFactory.construct(name, file, type, buffer.toString());
                if (script != null)
                    result.add(script);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Load a single script into a new script instance.
     *
     * @param plugin             The scripts owning plugin.
     * @param scriptFolder       The folder to find scripts in.
     * @param scriptFile         The script file.
     * @param scriptFactory  A script constructor used to create new script instances.
     *
     * @return Null if the file is not found or there is an error opening or reading from it.
     */
    @Nullable
    public static IScript loadScript(Plugin plugin,
                                     File scriptFolder,
                                     File scriptFile,
                                     IScriptFactory scriptFactory) {
        PreCon.notNull(plugin);
        PreCon.notNull(scriptFolder);
        PreCon.notNull(scriptFile);

        if (!scriptFile.exists())
            return null;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(scriptFile));

            StringBuilder buffer = new StringBuilder(2000);
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }

            reader.close();

            String scriptName = getScriptName(scriptFolder, scriptFile);

            String scriptType = getScriptType(scriptFile);

            return scriptFactory.construct(scriptName, scriptFile, scriptType, buffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a name for a script based on the scriptFolder and file name.
     *
     * @param scriptFolder  The base folder where scripts are kept.
     * @param file          The script file.
     */
    public static String getScriptName(File scriptFolder, File file) {
        PreCon.notNull(scriptFolder);
        PreCon.isValid(scriptFolder.isDirectory());
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory());

        String baseCacheName = FileUtils.getRelative(scriptFolder, file.getParentFile());

        Matcher pathMatcher = TextUtils.PATTERN_FILEPATH_SLASH.matcher(baseCacheName);
        baseCacheName = pathMatcher.replaceAll(".");

        String result = baseCacheName + '.' + FileUtils.getNameWithoutExtension(file);

        Matcher leadingDotMatcher = PATTERN_LEADING_DOT.matcher(result);
        return leadingDotMatcher.replaceAll("");
    }

    /**
     * Get the script type from a file.
     *
     * <p>Returns the file extension.</p>
     *
     * @param file  The script file.
     */
    public static String getScriptType(File file) {
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory());

        return FileUtils.getFileExtension(file);
    }

    /**
     * Evaluate a script into a script engine.
     *
     * @param engine   The script engine.
     * @param context  The script context.
     * @param script   The script to evaluate.
     *
     * @return The results returned from the script, if any.
     */
    public static Result<Object> eval(ScriptEngine engine, ScriptContext context, IScript script) {

        File file = script.getFile();

        String filename = file != null ? file.getName() : "<unknown>";
        String engineName = engine.getFactory().getEngineName();
        boolean isNashorn = engineName.contains("Nashorn");

        engine.put(ScriptEngine.FILENAME, filename);
        context.setAttribute(ScriptEngine.FILENAME, filename, ScriptContext.ENGINE_SCOPE);

        Object result;

        try {
            if (isNashorn) {

                context.setAttribute("script", script.getScript(), ScriptContext.ENGINE_SCOPE);
                context.setAttribute("scriptName", filename, ScriptContext.ENGINE_SCOPE);

                // evaluate script into Nashorn
                result = engine.eval("load({ script: script, name: scriptName})", context);
            }
            else {
                // evaluate script
                result = engine.eval(script.getScript(), context);
            }

            return new Result<Object>(true, result);

        } catch (ScriptException e) {
            e.printStackTrace();
            return new Result<Object>(false);
        }
    }
}
