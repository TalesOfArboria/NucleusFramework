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


package com.jcwhatever.generic.permissions;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.providers.IPermissionsProvider;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Permissions API
 */
public final class Permissions {

    private Permissions() {}

    /**
     * Determine if the permissions implementation has group support.
     */
    public static boolean hasGroupSupport() {
        return getProvider().hasGroupSupport();
    }

    /**
     * Determine if the permissions implementation has permissions
     * by world support.
     */
    public static boolean hasWorldSupport() {
        return getProvider().hasWorldSupport();
    }

    /**
     * Register a permission.
     *
     * @param permissionName  The name of the permission.
     * @param value           The default permission value.
     *
     * @return  The permission.
     */
    public static IPermission register(String permissionName, PermissionDefault value) {
        return getProvider().register(permissionName, value);
    }

    /**
     * Unregister a permission.
     *
     * @param permissionName  The name of the permission.
     */
    public static void unregister(String permissionName) {
        getProvider().unregister(permissionName);
    }

    /**
     * Unregister a permission.
     *
     * @param permission  The permission to unregister.
     */
    public static void unregister(IPermission permission) {
        getProvider().unregister(permission);
    }

    /**
     * Add a parent permission to a child permission.
     *
     * @param child   The child permission.
     * @param parent  The parent permission.
     * @param value   The permission value.
     */
    public static void addParent(IPermission child, IPermission parent, boolean value) {
        getProvider().addParent(child, parent, value);
    }

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
    public static void runBatchOperation(boolean recalculate, Runnable operations) {
        getProvider().runBatchOperation(recalculate, operations);
    }

    /**
     * Get a permission by name.
     *
     * @param permissionName  The name of the permission.
     */
    @Nullable
    public static IPermission get(String permissionName) {
        return getProvider().get(permissionName);
    }

    /**
     * Determine if the player has permission.
     *
     * @param sender          The {@code CommandSender} to check.
     * @param permissionName  The name of the permission.
     */
    public static boolean has(CommandSender sender, String permissionName) {
        return getProvider().has(sender, permissionName);
    }

    /**
     * Determine if the player has permission.
     *
     * @param sender      The {@code CommandSender} to check.
     * @param permission  The permission.
     */
    public static boolean has(CommandSender sender, IPermission permission) {
        return getProvider().has(sender, permission.getName());
    }

    /**
     * Determine if the player has permission in the specified world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param sender          The {@code CommandSender} to check.
     * @param world           The world to check.
     * @param permissionName  The name of the permission.
     */
    public static boolean has(CommandSender sender, World world, String permissionName) {
        return getProvider().has(sender, world, permissionName);
    }

    /**
     * Add a transient permission to a player.
     *
     * @param plugin          The plugin adding the transient permission.
     * @param sender          The {@code CommandSender} to add the permission to..
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean addTransient(Plugin plugin, CommandSender sender, String permissionName) {
        return getProvider().addTransient(plugin, sender, permissionName);
    }

    /**
     * Remove a transient permission from a player.
     *
     * @param plugin          The plugin that added the transient permission.
     * @param sender          The {@code CommandSender} to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean removeTransient(Plugin plugin, CommandSender sender, String permissionName) {
        return getProvider().removeTransient(plugin, sender, permissionName);
    }

    /**
     * Add a permission to a player.
     *
     * @param plugin          The plugin adding the permission.
     * @param sender          The {@code CommandSender} to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean add(Plugin plugin, CommandSender sender, String permissionName) {
        return getProvider().add(plugin, sender, permissionName);
    }

    /**
     * Add a permission to a player.
     *
     * @param plugin      The plugin adding the permission.
     * @param sender      The {@code CommandSender} to add the permission to.
     * @param permission  The permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean add(Plugin plugin, CommandSender sender, IPermission permission) {
        return getProvider().add(plugin, sender, permission.getName());
    }

    /**
     * Add a permission to a player when in a specific world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param plugin          The plugin adding the permission.
     * @param sender          The {@code CommandSender} to add the permission to.
     * @param world           The world.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean add(Plugin plugin, CommandSender sender, World world, String permissionName) {
        return getProvider().add(plugin, sender, world, permissionName);
    }

    /**
     * Add a permission to a player when in a specific world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param plugin      The plugin adding the permission.
     * @param sender      The {@code CommandSender} to check.
     * @param world       The world.
     * @param permission  The permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean add(Plugin plugin, CommandSender sender, World world, IPermission permission) {
        return getProvider().add(plugin, sender, world, permission.getName());
    }

    /**
     * Remove a players permission.
     *
     * @param plugin          The plugin removing the permission.
     * @param sender          The {@code CommandSender} to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean remove(Plugin plugin, CommandSender sender, String permissionName) {
        return getProvider().remove(plugin, sender, permissionName);
    }

    /**
     * Remove a players permission.
     *
     * @param plugin      The plugin removing the permission.
     * @param sender      The {@code CommandSender} to remove the permission from.
     * @param permission  The permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean remove(Plugin plugin, Player sender, IPermission permission) {
        return getProvider().remove(plugin, sender, permission.getName());
    }

    /**
     * Remove a players permission in a world.
     * <p>
     *     Not all permission implementations will support permissions by world.
     * </p>
     *
     * @param plugin          The plugin removing the permission.
     * @param sender          The {@code CommandSender} to remove the permission from.
     * @param world           The world.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean remove(Plugin plugin, CommandSender sender, World world, String permissionName) {
        return getProvider().remove(plugin, sender, world, permissionName);
    }

    /**
     * Remove a players permission in a world.
     *
     * @param plugin      The plugin removing the permission.
     * @param sender      The {@code CommandSender} to remove the permission from.
     * @param world       The world.
     * @param permission  The permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean remove(Plugin plugin, CommandSender sender, World world, IPermission permission) {
        return getProvider().remove(plugin, sender, world, permission.getName());
    }

    /**
     * Add a player to a group permission.
     * <p>
     *     Not all implementations support group permissions.
     * </p>
     *
     * @param plugin     The plugin adding the player to the group.
     * @param sender     The {@code CommandSender} to add to the group.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was added.
     */
    public static boolean addGroup(Plugin plugin, CommandSender sender, String groupName) {
        return getProvider().addGroup(plugin, sender, groupName);
    }

    /**
     * Add a player to a group permission in the specified world.
     *
     * @param plugin     The plugin adding the player to the group.
     * @param sender     The {@code CommandSender} to add to the group.
     * @param world      The world.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was added.
     */
    public static boolean addGroup(Plugin plugin, CommandSender sender, World world, String groupName) {
        return getProvider().addGroup(plugin, sender, world, groupName);
    }

    /**
     * Remove a player from a group permission.
     *
     * @param plugin     The plugin removing the player from the group.
     * @param sender     The {@code CommandSender} to remove from the group..
     * @param groupName  The name of the group.
     *
     * @return  True if the player was removed.
     */
    public static boolean removeGroup(Plugin plugin, CommandSender sender, String groupName) {
        return getProvider().removeGroup(plugin, sender, groupName);
    }

    /**
     * Remove a player from a group permission.
     *
     * @param plugin     The plugin removing the player from the group.
     * @param sender     The {@code CommandSender} to remove from the group.
     * @param world      The world.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was removed.
     */
    public static boolean removeGroup(Plugin plugin, CommandSender sender, World world, String groupName) {
        return getProvider().removeGroup(plugin, sender, world, groupName);
    }

    /**
     * Determine if a player has group permission.
     *
     * @param sender     The {@code CommandSender} to check.
     * @param groupName  The name of the group.
     */
    public static boolean hasGroup(CommandSender sender, String groupName) {
        String[] groups = getGroups(sender);
        if (groups == null)
            return false;

        for (String group : groups) {
            if (group.equals(groupName))
                return true;
        }

        return false;
    }

    /**
     * Get a string array of group permission names.
     */
    @Nullable
    public static String[] getGroups() {
        return getProvider().getGroups();
    }

    /**
     * Get a string array of groups the specified player is in.
     *
     * @param sender  The {@code CommandSender} to check.
     */
    @Nullable
    public static String[] getGroups(CommandSender sender) {
        return getProvider().getGroups(sender);
    }

    /**
     * Get a string array of groups the specified player is in while
     * in the specified world.
     *
     * @param sender  The {@code CommandSender} to check.
     * @param world   The world.
     */
    @Nullable
    public static String[] getGroups(CommandSender sender, World world) {
        return getProvider().getGroups(sender, world);
    }

    /**
     * Fix permission groups of all players on the server. Ensures
     * players have the permission groups specified if they are able to
     * have them as specified by the permission group instances provided.
     *
     * @param plugin  The plugin fixing permission groups.
     * @param groups  The groups to fix.
     */
    public static void fixPermissionGroups(Plugin plugin, Collection<IPermissionGroup> groups) {
        PreCon.notNull(plugin);
        PreCon.notNull(groups);

        if (!hasGroupSupport())
            return;

        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            Permissions.fixPermissionGroups(plugin, player, groups);
        }
    }

    /**
     * Fix a specific players groups. Ensures player has the permission groups specified
     * if they are able to have them as specified by the permission group instances provided.
     *
     * @param plugin  The plugin fixing permission groups.
     * @param sender  The {@code CommandSender} whose group permissions need to be checked.
     * @param groups  The groups to fix.
     */
    public static void fixPermissionGroups(Plugin plugin, CommandSender sender, Collection<IPermissionGroup> groups) {
        PreCon.notNull(plugin);
        PreCon.notNull(sender);
        PreCon.notNull(groups);

        if (!hasGroupSupport())
            return;

        if (!(sender instanceof Player))
            return;

        UUID playerId = ((Player)sender).getUniqueId();

        for (IPermissionGroup group : groups) {
            boolean canAssign = group.canAssignPermissionGroup(playerId);
            boolean hasGroup = hasGroup(sender, group.getPermissionGroupName());

            if (!canAssign && hasGroup) {
                removeGroup(plugin, sender, group.getPermissionGroupName());
            }
            else if (canAssign && !hasGroup) {
                addGroup(plugin, sender, group.getPermissionGroupName());
            }
        }
    }

    private static IPermissionsProvider getProvider() {
        return GenericsLib.getProviderManager().getPermissionsProvider();
    }
}
