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

import com.jcwhatever.nucleus.BukkitPlugin;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProvider;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.utils.DependencyRunner;
import com.jcwhatever.nucleus.utils.DependencyRunner.DependencyStatus;
import com.jcwhatever.nucleus.utils.DependencyRunner.IDependantRunnable;
import com.jcwhatever.nucleus.utils.DependencyRunner.IFinishHandler;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.modules.ClassLoadMethod;
import com.jcwhatever.nucleus.utils.modules.IModuleInfo;
import com.jcwhatever.nucleus.utils.modules.JarModuleLoader;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
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
    private final DependencyRunner<IDependantRunnable> _depend =
            new DependencyRunner<IDependantRunnable>(Nucleus.getPlugin());

    // keyed to module class name
    private final Map<String, ProviderModuleInfo> _moduleInfoMap = new HashMap<>(10);

    private boolean _isReady;

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

        if (!getModuleFolder().exists()) {
            Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    ((BukkitPlugin) Nucleus.getPlugin()).notifyProvidersReady();
                }
            });
            return;
        }

        super.loadModules();

        _depend.onFinish(new IFinishHandler<IDependantRunnable>() {
            @Override
            public void onFinish(List<IDependantRunnable> notRun) {

                List<IProvider> providers = getModules();

                _manager._isProvidersLoading = true;

                for (IProvider provider : providers) {

                    if (provider instanceof IStorageProvider) {

                        _manager.registerStorageProvider((IStorageProvider) provider);
                    }
                    else if (provider instanceof IPermissionsProvider) {

                        _manager.setPermissionsProvider((IPermissionsProvider) provider);
                    }
                    else if (provider instanceof IRegionSelectProvider) {

                        _manager.setRegionSelectionProvider((IRegionSelectProvider) provider);
                    }
                    else if (provider instanceof IEconomyProvider) {

                        _manager.setEconomyProvider((IEconomyProvider) provider);
                    }
                    else if (provider instanceof IBankItemsProvider) {

                        _manager.setBankItemsProvider((IBankItemsProvider) provider);
                    }
                    else if (provider instanceof INpcProvider) {

                        _manager.setNpcProvider((INpcProvider)provider);
                    }
                    else {
                        removeModule(provider.getName());
                    }
                }

                _manager.getStorageProvider().onEnable();
                _manager.getPermissionsProvider().onEnable();
                _manager.getRegionSelectionProvider().onEnable();
                _manager.getEconomyProvider().onEnable();
                _manager.getBankItemsProvider().onEnable();

                _manager._isProvidersLoading = false;

                ((BukkitPlugin)Nucleus.getPlugin()).notifyProvidersReady();
            }
        });

        _depend.start();
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

        final IProvider instance;
        final ProviderModuleInfo info = _moduleInfoMap.get(clazz.getCanonicalName());
        if (info == null)
            return null;

        try {
            Constructor<IProvider> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        _depend.add(new IDependantRunnable() {

            @Override
            public DependencyStatus getDependencyStatus() {

                boolean dependsReady = info.isDependsReady();
                boolean softReady = info.isSoftDependsReady();

                if (dependsReady && softReady) {
                    return DependencyStatus.READY;
                }
                else {
                    return dependsReady
                            ? DependencyStatus.REQUIRED_READY
                            : DependencyStatus.NOT_READY;
                }
            }

            @Override
            public void run() {
                instance.onRegister();
            }
        });

        return instance;
    }
}
