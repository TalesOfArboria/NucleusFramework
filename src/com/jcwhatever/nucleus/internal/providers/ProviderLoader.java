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

package com.jcwhatever.nucleus.internal.providers;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.modules.ClassLoadMethod;
import com.jcwhatever.nucleus.modules.IModuleInfo;
import com.jcwhatever.nucleus.modules.JarModuleLoader;
import com.jcwhatever.nucleus.providers.IPermissionsProvider;
import com.jcwhatever.nucleus.providers.IProvider;
import com.jcwhatever.nucleus.providers.IRegionSelectProvider;
import com.jcwhatever.nucleus.providers.IStorageProvider;
import com.jcwhatever.nucleus.utils.file.FileUtils.DirectoryTraversal;
import com.jcwhatever.nucleus.utils.PreCon;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import javax.annotation.Nullable;

/**
 * Loads provider modules from /plugins/NucleusFramework/providers folder
 */
public final class ProviderLoader extends JarModuleLoader<IProvider> {

    private final InternalProviderManager _manager;
    private final File _folder;

    // keyed to module class name
    private final Map<String, ProviderModuleInfo> _moduleInfoMap = new HashMap<>(10);

    /**
     * Constructor.
     */
    public ProviderLoader(InternalProviderManager providerManager) {
        super(Nucleus.getPlugin(), IProvider.class);

        PreCon.notNull(providerManager);

        _manager = providerManager;
        _folder = new File(Nucleus.getPlugin().getDataFolder(), "providers");
    }

    @Override
    public File getModuleFolder() {
        return _folder;
    }

    @Override
    public DirectoryTraversal getDirectoryTraversal() {
        return DirectoryTraversal.NONE;
    }

    @Override
    public void loadModules() {

        if (!getModuleFolder().exists())
            return;

        super.loadModules();

        List<IProvider> providers = getModules();

        _manager._isProvidersLoading = true;

        for (IProvider provider : providers) {

            if (provider instanceof IStorageProvider) {

                _manager.registerStorageProvider((IStorageProvider)provider);
            }
            else if (provider instanceof IPermissionsProvider) {

                _manager.setPermissionsProvider((IPermissionsProvider) provider);
            }
            else if (provider instanceof IRegionSelectProvider) {

                _manager.setRegionSelectionProvider((IRegionSelectProvider)provider);
            }
            else {
                removeModule(provider.getName());
            }
        }

        _manager._isProvidersLoading = false;
    }

    @Override
    protected ClassLoadMethod getLoadMethod(File file) {
        return ClassLoadMethod.DIRECT;
    }

    @Override
    protected String getModuleClassName(JarFile jarFile) {

        ProviderModuleInfo info = new ProviderModuleInfo(jarFile);
        if (!info.isValid())
            return null;

        _moduleInfoMap.put(info.getModuleClassName(), info);

        return info.getModuleClassName();
    }

    @Nullable
    @Override
    protected IModuleInfo createModuleInfo(final IProvider moduleInstance) {
        return _moduleInfoMap.get(moduleInstance.getClass().getCanonicalName());
    }

    @Nullable
    @Override
    protected IProvider instantiateModule(Class<IProvider> clazz) {

        try {
            Constructor<IProvider> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
