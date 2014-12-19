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

import com.jcwhatever.bukkit.generic.modules.JarModuleLoader;
import com.jcwhatever.bukkit.generic.providers.IPermissionsProvider;
import com.jcwhatever.bukkit.generic.providers.IProvider;
import com.jcwhatever.bukkit.generic.providers.IStorageProvider;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.List;

/**
 * Loads provider modules from /plugins/GenericsLib/providers folder
 */
public final class ProviderLoader extends JarModuleLoader<IProvider> {

    private final InternalProviderManager _manager;

    /**
     * Constructor.
     */
    public ProviderLoader(InternalProviderManager providerManager, ProviderLoaderSettings settings) {
        super(IProvider.class, settings);

        PreCon.notNull(providerManager);

        _manager = providerManager;
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
                _manager.setPermissionsProvider((IPermissionsProvider)provider);
            }
            else {
                removeModule(provider.getName());
            }
        }

        _manager._isProvidersLoading = false;
    }
}
