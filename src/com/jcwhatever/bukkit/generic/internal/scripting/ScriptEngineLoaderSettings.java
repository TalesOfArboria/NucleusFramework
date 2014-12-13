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

package com.jcwhatever.bukkit.generic.internal.scripting;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.modules.IModuleFactory;
import com.jcwhatever.bukkit.generic.modules.JarModuleLoaderSettings;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;
import javax.script.ScriptEngineFactory;

public class ScriptEngineLoaderSettings extends JarModuleLoaderSettings<ScriptEngineFactory> {

    public ScriptEngineLoaderSettings() {

        File scriptFolder = new File(GenericsLib.getPlugin().getDataFolder(), "scripts");
        File engineFolder = new File(scriptFolder, "engines");

        if (!engineFolder.exists() && !engineFolder.mkdirs()) {
            throw new RuntimeException("Failed to create script engine folder.");
        }

        setModuleFolder(engineFolder);
        setDirectoryTraversal(DirectoryTraversal.NONE);
        setModuleFactory(new IModuleFactory<ScriptEngineFactory>() {
            @Nullable
            @Override
            public ScriptEngineFactory create(Class<ScriptEngineFactory> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

                Constructor<ScriptEngineFactory> constructor = clazz.getDeclaredConstructor();
                return constructor.newInstance();
            }
        });
    }
}
