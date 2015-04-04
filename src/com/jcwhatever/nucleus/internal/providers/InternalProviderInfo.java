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
import com.jcwhatever.nucleus.providers.IProviderInfo;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.List;

/**
 * Internal implementation of {@link IProviderInfo}.
 */
public final class InternalProviderInfo implements IProviderInfo {

    private final Class<?> _module;
    private final String _name;
    private final String _searchName;
    private final String _description;

    public InternalProviderInfo(Class<?> module, String name, String description) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(description);

        _module = module;
        _name = name;
        _searchName = name.toLowerCase();
        _description = description;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public String getVersion() {
        return Nucleus.getPlugin().getDescription().getVersion();
    }

    @Override
    public int getLogicalVersion() {
        return 0;
    }

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public String getModuleClassName() {
        return _module.getName();
    }

    @Override
    public List<String> getBukkitDepends() {
        return CollectionUtils.unmodifiableList();
    }

    @Override
    public List<String> getBukkitSoftDepends() {
        return CollectionUtils.unmodifiableList();
    }
}
