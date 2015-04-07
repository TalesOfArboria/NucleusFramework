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

package com.jcwhatever.nucleus.internal.providers.permissions.bukkit;

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.utils.BatchTracker;
import com.jcwhatever.nucleus.managed.reflection.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An extended implementation of {@link BatchTracker} that aids in modifying Bukkit permissions
 * without triggering costly recalculations.
 *
 * <p>Normally modifies permissions using the Bukkit API. Use {@link BatchTracker#start} to start
 * a batch operation which prevents recalculations until all batch operations are completed.</p>
 */
public final class FastPermissions extends BatchTracker {

    private Set<Permission> _toRecalculate;
    private Set<Permission> _dontRecalculate;
    private Map<String, Permission> _permissions;

    /**
     * Constructor.
     */
    public FastPermissions() {

        try {
            loadPermissionsMap();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            NucMsg.warning("Failed to access Bukkits permissions map for fast permission insertion. " +
                    "Normal insertion will be used instead.");
        }
    }

    /**
     *  Add a permission without causing a permissions recalculation.
     *
     * <p>If there is at least 1 batch operation running, the permission will be added
     * without causing a permissions recalculation.</p>
     *
     * <p>If there was a problem loading Bukkits plugin manager's permission map via
     * reflection, the normal method via the Bukkit API is always used.</p>
     *
     *  @param permission  The permission to add.
     */
    public void addPermission(Permission permission) {

        // make sure a batch operation is in progress.
        if (_permissions == null || !isRunning()) {
            Bukkit.getPluginManager().addPermission(permission);
            return;
        }

        loadSets();

        _permissions.put(permission.getName(), permission);

        // add permission to the collection of permissions that need to be recalculated
        if (!_dontRecalculate.contains(permission))
            _toRecalculate.add(permission);
    }

    /**
     * Add a parent permission to a child permission.
     *
     * <p>If there is at least 1 batch operation running, the permission will be modified
     * without causing a permissions recalculation.</p>
     *
     * <p>If there was a problem loading Bukkits plugin manager's permission map via
     * reflection, the normal method via the Bukkit API is always used.</p>
     *
     * @param child   The child permission to add the parent to.
     * @param parent  The parent permission.
     * @param value   The permission value.
     */
    public void addParent(Permission child, Permission parent, boolean value) {

        if (_permissions == null || !isRunning()) {
            child.addParent(parent, value);
            return;
        }

        loadSets();

        parent.getChildren().put(child.getName(), value);

        if (!_dontRecalculate.contains(parent))
            _toRecalculate.add(parent);

        _toRecalculate.remove(child);
        _dontRecalculate.add(child);
    }

    @Override
    public int end() {

        int count = super.end();
        if (count == 0)
            recalculate();

        return count;
    }

    /*
     * Recalculate all permissions added or modified by this since the
     * last time recalculate was invoked.
     */
    private void recalculate() {

        if (_toRecalculate == null)
            return;

        for (Permission permission : _toRecalculate) {
            Bukkit.getPluginManager().recalculatePermissionDefaults(permission);
            permission.recalculatePermissibles();
        }

        _toRecalculate = null;
        _dontRecalculate = null;
    }

    private void loadSets() {
        if (_toRecalculate != null)
            return;

        _toRecalculate = new HashSet<>(200);
        _dontRecalculate = new HashSet<>(200);
    }

    private boolean loadPermissionsMap() throws NoSuchFieldException, IllegalAccessException {

        PluginManager pm = Bukkit.getPluginManager();


        Field field = pm.getClass().getDeclaredField("permissions");
        if (!Reflection.removeFinal(field))
            return false;

        @SuppressWarnings("unchecked")
        Map<String, Permission> permissions = (Map<String, Permission>) field.get(pm);

        _permissions = permissions;

        return true;
    }
}
