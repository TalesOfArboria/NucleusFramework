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

package com.jcwhatever.bukkit.generic.internal.providers;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.modules.IModuleFactory;
import com.jcwhatever.bukkit.generic.modules.JarModuleLoaderSettings;
import com.jcwhatever.bukkit.generic.providers.IProvider;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;

/*
 * 
 */
public final class ProviderLoaderSettings extends JarModuleLoaderSettings<IProvider> {

    public ProviderLoaderSettings() {

        File folder = new File(GenericsLib.getPlugin().getDataFolder(), "providers");

        if (folder.exists()) {
            setModuleFolder(folder);

            setDirectoryTraversal(DirectoryTraversal.NONE);
            setModuleFactory(new IModuleFactory<IProvider>() {

                @Nullable
                @Override
                public IProvider create(Class<IProvider> clazz)
                        throws InstantiationException, IllegalAccessException,
                        NoSuchMethodException, InvocationTargetException {

                    Constructor<IProvider> constructor = clazz.getDeclaredConstructor();
                    return constructor.newInstance();
                }
            });
        }
    }
}
