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


package com.jcwhatever.nucleus.internal.providers.permissions;

import com.jcwhatever.nucleus.internal.providers.permissions.bukkit.FastPermissions;
import com.jcwhatever.nucleus.internal.providers.permissions.bukkit.SuperPermission;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.permissions.IPermission;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;

/**
 * Abstract implementation of a permissions handler
 */
public abstract class AbstractPermissionsProvider extends Provider implements IPermissionsProvider {

    private final FastPermissions _fastPerms = new FastPermissions();

    @Nullable
    @Override
    public IPermission get(String permissionName) {

        Permission permission = manager().getPermission(permissionName);
        if (permission == null)
            return null;

        return new SuperPermission(this, permission, _fastPerms);
    }

    @Override
    public IPermission register(String permissionName, PermissionDefault value) {
        Permission perm = manager().getPermission(permissionName);
        if (perm != null)
            return new SuperPermission(this, perm, _fastPerms);

        perm = new Permission(permissionName);
        perm.setDefault(value);

        _fastPerms.addPermission(perm);

        return new SuperPermission(this, perm, _fastPerms);
    }

    @Override
    public void unregister(String permissionName) {
        Bukkit.getPluginManager().removePermission(permissionName);
    }

    @Override
    public void unregister(IPermission permission) {
        Bukkit.getPluginManager().removePermission((Permission) permission.getHandle());
    }

    @Override
    public void runBatchOperation(Runnable operations) {
        PreCon.notNull(operations);

        _fastPerms.start();
        operations.run();
        _fastPerms.end();
    }

    private PluginManager manager() {
        return Bukkit.getPluginManager();
    }
}
