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

package com.jcwhatever.generic.internal.scripting;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.internal.Msg;
import com.jcwhatever.generic.modules.ClassLoadMethod;
import com.jcwhatever.generic.modules.IModuleInfo;
import com.jcwhatever.generic.modules.JarModuleLoader;
import com.jcwhatever.generic.utils.file.FileUtils.DirectoryTraversal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.jar.JarFile;
import javax.annotation.Nullable;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public final class ScriptEngineLoader extends JarModuleLoader<ScriptEngineFactory> {

    private final ScriptEngineManager _engineManager;
    private final File _engineFolder;

    /**
     * Constructor.
     */
    public ScriptEngineLoader(ScriptEngineManager engineManager) {
        super(GenericsLib.getPlugin(), ScriptEngineFactory.class);

        _engineManager = engineManager;

        File scriptFolder = new File(GenericsLib.getPlugin().getDataFolder(), "scripts");
        _engineFolder = new File(scriptFolder, "engines");

        if (!_engineFolder.exists() && !_engineFolder.mkdirs()) {
            throw new RuntimeException("Failed to create script engine folder.");
        }
    }

    @Override
    public File getModuleFolder() {
        return _engineFolder;
    }

    @Override
    public DirectoryTraversal getDirectoryTraversal() {
        return DirectoryTraversal.NONE;
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

    @Override
    protected ClassLoadMethod getLoadMethod(File file) {
        return ClassLoadMethod.SEARCH;
    }

    @Override
    protected String getModuleClassName(JarFile jarFile) {
        return null;
    }

    @Nullable
    @Override
    protected IModuleInfo createModuleInfo(final ScriptEngineFactory moduleInstance) {
        return new IModuleInfo() {

            String name = moduleInstance.getEngineName();
            String searchName = moduleInstance.getEngineName().toLowerCase();

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getSearchName() {
                return searchName;
            }
        };
    }

    @Nullable
    @Override
    protected ScriptEngineFactory instantiateModule(Class<ScriptEngineFactory> clazz) {

        try {
            Constructor<ScriptEngineFactory> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
