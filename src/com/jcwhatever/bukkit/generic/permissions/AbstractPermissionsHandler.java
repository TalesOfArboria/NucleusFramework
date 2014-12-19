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


package com.jcwhatever.bukkit.generic.permissions;

import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.providers.IPermissionsProvider;
import com.jcwhatever.bukkit.generic.utils.BatchTracker;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of a permissions handler
 */
public abstract class AbstractPermissionsHandler implements IPermissionsProvider {

    private static final BatchTracker _batch = new BatchTracker();
    private static Set<Permission> _toRecalculate = new HashSet<>(1000);
    private static Set<Permission> _dontRecalculate = new HashSet<>(1000);

    private PluginManager _pm = Bukkit.getPluginManager();

    // reflection
    private Object _permissionsMap;
    private Method _putMethod;
    private boolean _canReflect = true;

    @Override
    public IPermission get(String permissionName) {
        return new SuperPermission(_pm.getPermission(permissionName));
    }

    @Override
    public IPermission register(String permissionName, PermissionDefault value) {
        Permission perm = _pm.getPermission(permissionName);
        if (perm != null)
            return new SuperPermission(perm);

        perm = new Permission(permissionName);
        perm.setDefault(value);

        if (!_batch.isRunning() || !addPermissionFast(perm))
            _pm.addPermission(perm);

        return new SuperPermission(perm);
    }

    @Override
    public void unregister(String permissionName) {
        Bukkit.getPluginManager().removePermission(permissionName);
    }

    @Override
    public void unregister(IPermission permission) {
        Bukkit.getPluginManager().removePermission((Permission)permission.getHandle());
    }

    @Override
    public void addParent(IPermission child, IPermission parent, boolean value) {

        parent.getChildren().put(child.getName(), value);

        Permission parentPerm = (Permission)parent.getHandle();
        Permission childPerm = (Permission)child.getHandle();

        if (_batch.isRunning()) {

            if (!_dontRecalculate.contains(parentPerm))
                _toRecalculate.add(parentPerm);

            _toRecalculate.remove(childPerm);
            _dontRecalculate.add(childPerm);
        }

        recalculatePermissibles(parentPerm);
    }

    @Override
    public void runBatchOperation(boolean recalculate, Runnable operations) {

        _batch.start();
        operations.run();
        _batch.end();

        if (_batch.isRunning())
            return;

        if (recalculate) {

            for (Permission permission : _toRecalculate) {
                recalculatePermissionDefault(permission);
                recalculatePermissibles(permission);
            }
        }

        _toRecalculate.clear();
        _dontRecalculate.clear();
    }

    /*
     * recalculate Bukkit permission
     */
    private void recalculatePermissibles(Permission permission) {
        if (!_batch.isRunning())
            permission.recalculatePermissibles();
        else if (!_dontRecalculate.contains(permission)) {
            _toRecalculate.add(permission);
        }
    }

    /*
     * recalculate Bukkit permission defaults
     */
    private void recalculatePermissionDefault(Permission permission) {
        if (!_batch.isRunning())
            _pm.recalculatePermissionDefaults(permission);
        else if (!_dontRecalculate.contains(permission)) {
            _toRecalculate.add(permission);
        }
    }

    /*
     *  Use reflection to add a permission without causing a
      * permissions recalculation.
     */
    private boolean addPermissionFast(Permission permission) {

        if (!_canReflect)
            return false;

        // see if reflected map put method is already cached
        if (_putMethod == null) {

            try {

                Field field = _pm.getClass().getDeclaredField("permissions");
                field.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                _permissionsMap = field.get(_pm);

                _putMethod = _permissionsMap.getClass().getDeclaredMethod("put", Object.class, Object.class);

            } catch (Exception e) {

                e.printStackTrace();
                _canReflect = false;
                Msg.warning("Failed to use reflection to add permission. " +
                        "Permissions will be added using Bukkit API which may take longer.");
                return false;
            }
        }

        // use reflected map put method handle to put the permission in the private permissions map of the
        // Bukkit plugin manager.
        try {

            _putMethod.invoke(_permissionsMap, permission.getName().toLowerCase(), permission);
        }
        catch (Exception e) {
            e.printStackTrace();

            _canReflect = false;
            Msg.warning("Failed to use reflection to add permission. " +
                    "Permissions will be added using Bukkit API which may take longer.");
            return false;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();

            _canReflect = false;
            Msg.warning("Failed to use reflection to add permission. " +
                    "Permissions will be added using Bukkit API which may take longer.");
            return false;
        }

        // add permission to the collection of permissions that need to be recalculated
        if (!_dontRecalculate.contains(permission)) {
            _toRecalculate.add(permission);
        }

        return true;
    }

}
