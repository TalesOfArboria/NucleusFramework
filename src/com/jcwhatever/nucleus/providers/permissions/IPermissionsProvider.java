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

package com.jcwhatever.nucleus.providers.permissions;

import com.jcwhatever.nucleus.providers.IProvider;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Interface for a permissions provider implementation.
 *
 * <p>Should be implemented by a type that extends {@link com.jcwhatever.nucleus.providers.Provider}.</p>
 */
public interface IPermissionsProvider extends IProvider {

    /**
     * Register a new permission.
     *
     * @param permissionName  The name of the permission.
     * @param value           The permissions default value.
     *
     * @return  The permission.
     */
    IPermission register(String permissionName, PermissionDefault value);

    /**
     * Unregister a permission.
     *
     * @param permissionName  The name of the permission to unregister.
     */
    void unregister(String permissionName);

    /**
     * Unregister a permission.
     *
     * @param permission  The permission to unregister.
     */
    void unregister(IPermission permission);

    /**
     * Add a parent permission to a permission.
     *
     * @param child   The child permission.
     * @param parent  The parent permission.
     * @param value   The permission value.
     */
    void addParent(IPermission child, IPermission parent, boolean value);

    /**
     * Run a batch operations of permissions. Many permissions being registered and or
     * having parents set should be done inside of a batch operation.
     *
     * <p>
     *     Improves performance if possible by attempting to prevent permission recalculations
     *     after each permission change.
     * </p>
     *
     * @param recalculate  True to recalculate permissions when the batch operation is finished.
     * @param operations   The runnable that runs the permissions operations.
     */
    void runBatchOperation(boolean recalculate, Runnable operations);

    /**
     * Get a permission by name.
     *
     * @param permissionName  The name of the permission.
     */
    @Nullable
    IPermission get(String permissionName);

    /**
     * Determine if the player has permission.
     *
     * @param sender               The player to check.
     * @param permissionName  The name of the permission.
     */
    boolean has(CommandSender sender, String permissionName);

    /**
     * Add a transient permission to a player.
     *
     * @param plugin          The plugin adding the transient permission.
     * @param sender               The player to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    boolean addTransient(Plugin plugin, CommandSender sender, String permissionName);

    /**
     * Remove a transient permission from a player.
     *
     * @param plugin          The plugin that added the transient permission.
     * @param sender               The player to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    boolean removeTransient(Plugin plugin, CommandSender sender, String permissionName);

    /**
     * Add a permission to a player.
     *
     * @param plugin          The plugin adding the permission.
     * @param sender               The player to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    boolean add(Plugin plugin, CommandSender sender, String permissionName);

    /**
     * Remove a players permission.
     *
     * @param plugin          The plugin removing the permission.
     * @param sender               The player to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    boolean remove(Plugin plugin, CommandSender sender, String permissionName);

    /**
     * Get the underlying permissions provider.
     */
    @Nullable
    Object getHandle();
}
