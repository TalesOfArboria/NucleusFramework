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
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.utils.DependencyRunner;
import com.jcwhatever.nucleus.utils.DependencyRunner.DependencyStatus;
import com.jcwhatever.nucleus.utils.DependencyRunner.IDependantRunnable;
import com.jcwhatever.nucleus.utils.DependencyRunner.IFinishHandler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.FileUtils.DirectoryTraversal;
import com.jcwhatever.nucleus.utils.modules.ClassLoadMethod;
import com.jcwhatever.nucleus.utils.modules.IModuleInfo;
import com.jcwhatever.nucleus.utils.modules.JarModuleLoader;
import com.jcwhatever.nucleus.utils.observer.future.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.IFuture.FutureStatus;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Loads provider modules from /plugins/NucleusFramework/providers folder
 */
public final class InternalProviderLoader extends JarModuleLoader<Provider> {

    private final InternalProviderManager _manager;
    private final File _folder;
    private final DependencyRunner<IDependantRunnable> _depend =
            new DependencyRunner<IDependantRunnable>(Nucleus.getPlugin());

    // keyed to module class name
    private final Map<String, InternalProviderModuleInfo> _moduleInfoMap = new HashMap<>(10);

    /**
     * Constructor.
     */
    public InternalProviderLoader(InternalProviderManager providerManager) {
        super(Nucleus.getPlugin(), Provider.class);

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

                _manager.setLoading(false);
                _manager.enableProviders()
                        .onSuccess(new FutureSubscriber() {
                            @Override
                            public void on(FutureStatus status, @Nullable String message) {
                                ((BukkitPlugin)Nucleus.getPlugin()).notifyProvidersReady();
                            }
                        });
            }
        });

        _manager.setLoading(true);
        _depend.start();
    }

    @Override
    protected ClassLoadMethod getLoadMethod(File file) {
        return ClassLoadMethod.DIRECT;
    }

    @Override
    protected String getModuleClassName(JarFile jarFile) {

        InternalProviderModuleInfo info = new InternalProviderModuleInfo(jarFile);
        if (!info.isValid())
            return null;

        _moduleInfoMap.put(info.getModuleClassName(), info);


        return info.getModuleClassName();
    }

    @Nullable
    @Override
    protected IModuleInfo createModuleInfo(Provider moduleInstance) {
        return _moduleInfoMap.get(moduleInstance.getClass().getCanonicalName());
    }

    @Nullable
    @Override
    protected Provider instantiateModule(Class<Provider> clazz) {

        final Provider instance;
        final InternalProviderModuleInfo info = _moduleInfoMap.get(clazz.getCanonicalName());
        if (info == null)
            return null;

        try {
            Constructor<Provider> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        instance.setInfo(info);
        _depend.add(new DependantRunnable(instance, info));

        return instance;
    }

    private class DependantRunnable implements IDependantRunnable {

        final Provider provider;
        final InternalProviderModuleInfo info;

        DependantRunnable(Provider provider, InternalProviderModuleInfo info) {
            this.provider = provider;
            this.info = info;
        }

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
            if (!_manager.addProvider(provider)) {
                NucMsg.debug("Failed to add service provider: '{0}'", provider.getInfo().getName());
                removeModule(provider.getInfo().getName());
            }
        }
    }
}
