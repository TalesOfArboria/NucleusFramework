/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Interface for a permissions handler implementation.
 */
public interface IPermissionsHandler {

    /**
     * Determine if the permissions implementation
     * has group support.
     */
    boolean hasGroupSupport();

    /**
     * Determine if the permissions implementation
     * has support for permissions by world.
     */
    boolean hasWorldSupport();

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
     * @param p               The player to check.
     * @param permissionName  The name of the permission.
     */
	boolean has(Player p, String permissionName);

    /**
     * Determine if the player has permission in the specified world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param p               The player to check.
     * @param world           The world to check.
     * @param permissionName  The name of the permission.
     */
	boolean has(Player p, World world, String permissionName);

    /**
     * Add a transient permission to a player.
     *
     * @param plugin          The plugin adding the transient permission.
     * @param p               The player to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    boolean addTransient(Plugin plugin, Player p, String permissionName);

    /**
     * Remove a transient permission from a player.
     *
     * @param plugin          The plugin that added the transient permission.
     * @param p               The player to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    boolean removeTransient(Plugin plugin, Player p, String permissionName);

    /**
     * Add a permission to a player.
     *
     * @param plugin          The plugin adding the permission.
     * @param p               The player to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    boolean add(Plugin plugin, Player p, String permissionName);

    /**
     * Add a permission to a player when in a specific world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param plugin          The plugin adding the permission.
     * @param p               The player to add the permission to.
     * @param world           The world.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    boolean add(Plugin plugin, Player p, World world, String permissionName);

    /**
     * Remove a players permission.
     *
     * @param plugin          The plugin removing the permission.
     * @param p               The player to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    boolean remove(Plugin plugin, Player p, String permissionName);

    /**
     * Remove a players permission in a world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param plugin          The plugin removing the permission.
     * @param p               The player to remove the permission from.
     * @param world           The world.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    boolean remove(Plugin plugin, Player p, World world, String permissionName);

    /**
     * Add a player to a group permission.
     * <p>
     *     Not all implementations support group permissions.
     * </p>
     *
     * @param plugin     The plugin adding the player to the group.
     * @param p          The player to add to the group.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was added.
     */
    boolean addGroup(Plugin plugin, Player p, String groupName);

    /**
     * Add a player to a group permission in the specified world.
     *
     * @param plugin     The plugin adding the player to the group.
     * @param p          The player to add to the group.
     * @param world      The world.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was added.
     */
    boolean addGroup(Plugin plugin, Player p, World world, String groupName);

    /**
     * Remove a player from a group permission.
     *
     * @param plugin     The plugin removing the player from the group.
     * @param p          The player to remove from the group.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was removed.
     */
    boolean removeGroup(Plugin plugin, Player p, String groupName);

    /**
     * Remove a player from a group permission.
     *
     * @param plugin     The plugin removing the player from the group.
     * @param p          The player to remove from the group.
     * @param world      The world.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was removed.
     */
    boolean removeGroup(Plugin plugin, Player p, World world, String groupName);

    /**
     * Get a string array of group permission names.
     */
    @Nullable
    String[] getGroups();

    /**
     * Get a string array of groups the specified player is in.
     *
     * @param p  The player to check.
     */
    @Nullable
    String[] getGroups(Player p);

    /**
     * Get a string array of groups the specified player is in while
     * in the specified world.
     *
     * @param p      The player to check.
     * @param world  The world.
     */
    @Nullable
    String[] getGroups(Player p, World world);
    
}
