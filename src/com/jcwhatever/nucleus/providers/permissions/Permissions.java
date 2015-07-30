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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Static convenience methods for accessing the permissions provider.
 */
public final class Permissions {

    private Permissions() {}

    /**
     * Determine if the permissions implementation has group support.
     */
    public static boolean hasGroupSupport() {
        return getProvider() instanceof IGroupPermissionsProvider;
    }

    /**
     * Determine if the permissions implementation has world
     * permissions support.
     */
    public static boolean hasWorldSupport() {
        return getProvider() instanceof IWorldPermissionsProvider;
    }

    /**
     * Determine if the permissions implementation has
     * world group support.
     */
    public static boolean hasWorldGroupSupport() {
        return getProvider() instanceof IWorldGroupPermissionsProvider;
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
        return register(permissionName, value, false);
    }

    /**
     * Register a permission.
     *
     * <p>Can automatically register all parent permissions detected in the
     * name with wildcard suffix and child permissions.</p>
     *
     * @param permissionName  The name of the permission.
     * @param value           The default permission value.
     * @param registerParents  True to register parent permissions with wild card suffix.
     *
     * @return  The permission.
     */
    public static IPermission register(final String permissionName,
                                       final PermissionDefault value, boolean registerParents) {
        PreCon.notNullOrEmpty(permissionName);
        PreCon.notNull(value);

        if (permissionName.endsWith(".*") || !registerParents)
            return getProvider().register(permissionName, value);

        final IPermission result = getProvider().register(permissionName, value);

        runBatchOperation(new Runnable() {
            @Override
            public void run() {

                String[] components = TextUtils.PATTERN_DOT.split(permissionName);

                StringBuilder buffer = new StringBuilder(permissionName.length() + 2);
                List<IPermission> permParents = new ArrayList<>(components.length);

                for (int i=0; i < components.length; i++) {

                    buffer.append(components[i]);

                    if (i < components.length - 1) {
                        String name = buffer.toString();
                        IPermission permission = get(name + ".*");
                        if (permission == null) {
                            permission = register(name + ".*", PermissionDefault.FALSE, false);
                        }

                        for (IPermission permParent : permParents) {
                            permission.addParent(permParent, true);
                        }
                        permParents.add(permission);

                        buffer.append('.');
                    }
                }

                for (IPermission permParent : permParents) {
                    result.addParent(permParent, true);
                }


                IPermission allPermission = Permissions.get(permissionName + ".*");
                if (allPermission == null) {
                    allPermission = Permissions.register(permissionName + ".*", PermissionDefault.FALSE);
                }
                result.addParent(allPermission, true);
            }
        });

        return result;
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
     * Run a batch operations of permissions.
     *
     * <p>Many permissions being registered and or having parents set should be done
     * inside of a batch operation to potentially improve performance.</p>
     *
     * @param operations   The runnable that runs the permissions operations.
     */
    public static void runBatchOperation(Runnable operations) {
        getProvider().runBatchOperation(operations);
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
     * @param player          The player to check.
     * @param permissionName  The name of the permission.
     */
    public static boolean has(OfflinePlayer player, String permissionName) {
        return getProvider().has(player, permissionName);
    }

    /**
     * Determine if the player has permission.
     *
     * @param player      The player to check.
     * @param permission  The permission.
     */
    public static boolean has(OfflinePlayer player, IPermission permission) {
        return getProvider().has(player, permission.getName());
    }

    /**
     * Determine if the player has permission in the specified world.
     *
     * @param player          The player to check.
     * @param world           The world to check.
     * @param permissionName  The name of the permission.
     *
     * @throws java.lang.UnsupportedOperationException if the provider does not support permission by world.
     */
    public static boolean has(OfflinePlayer player, World world, String permissionName) {
        return getWorldProvider().has(player, world, permissionName);
    }

    /**
     * Add a transient permission to a player.
     *
     * @param plugin          The plugin adding the transient permission.
     * @param player          The player to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean addTransient(Plugin plugin, Player player, String permissionName) {
        return getProvider().addTransient(plugin, player, permissionName);
    }

    /**
     * Remove a transient permission from a player.
     *
     * @param plugin          The plugin that added the transient permission.
     * @param player          The player to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean removeTransient(Plugin plugin, Player player, String permissionName) {
        return getProvider().removeTransient(plugin, player, permissionName);
    }

    /**
     * Add a permission to a player.
     *
     * @param plugin          The plugin adding the permission.
     * @param player          The player to add the permission to.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean add(Plugin plugin, OfflinePlayer player, String permissionName) {
        return getProvider().add(plugin, player, permissionName);
    }

    /**
     * Add a permission to a player.
     *
     * @param plugin      The plugin adding the permission.
     * @param player      The player to add the permission to.
     * @param permission  The permission.
     *
     * @return  True if the permission was added.
     */
    public static boolean add(Plugin plugin, OfflinePlayer player, IPermission permission) {
        return getProvider().add(plugin, player, permission.getName());
    }

    /**
     * Add a permission to a player when in a specific world.
     *
     * @param plugin          The plugin adding the permission.
     * @param player          The player to add the permission to.
     * @param world           The world.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was added.
     *
     * @throws java.lang.UnsupportedOperationException if the provider does not support permissions by world.
     */
    public static boolean add(Plugin plugin, OfflinePlayer player, World world, String permissionName) {
        return getWorldProvider().add(plugin, player, world, permissionName);
    }

    /**
     * Add a permission to a player when in a specific world.
     *
     * @param plugin      The plugin adding the permission.
     * @param player      The player to check.
     * @param world       The world.
     * @param permission  The permission.
     *
     * @return  True if the permission was added.
     *
     * @throws java.lang.UnsupportedOperationException if the provider does not support permissions by world.
     */
    public static boolean add(Plugin plugin, OfflinePlayer player, World world, IPermission permission) {
        return getWorldProvider().add(plugin, player, world, permission.getName());
    }

    /**
     * Remove a players permission.
     *
     * @param plugin          The plugin removing the permission.
     * @param player          The player to remove the permission from.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean remove(Plugin plugin, OfflinePlayer player, String permissionName) {
        return getProvider().remove(plugin, player, permissionName);
    }

    /**
     * Remove a players permission.
     *
     * @param plugin      The plugin removing the permission.
     * @param player      The player to remove the permission from.
     * @param permission  The permission.
     *
     * @return  True if the permission was removed.
     */
    public static boolean remove(Plugin plugin, OfflinePlayer player, IPermission permission) {
        return getProvider().remove(plugin, player, permission.getName());
    }

    /**
     * Remove a players permission in a world.
     *
     * @param plugin          The plugin removing the permission.
     * @param player          The player to remove the permission from.
     * @param world           The world.
     * @param permissionName  The name of the permission.
     *
     * @return  True if the permission was removed.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have world group support.
     */
    public static boolean remove(Plugin plugin, OfflinePlayer player, World world, String permissionName) {
        return getWorldProvider().remove(plugin, player, world, permissionName);
    }

    /**
     * Remove a players permission in a world.
     *
     * @param plugin      The plugin removing the permission.
     * @param player      The player to remove the permission from.
     * @param world       The world.
     * @param permission  The permission.
     *
     * @return  True if the permission was removed.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have world group support.
     */
    public static boolean remove(Plugin plugin, OfflinePlayer player, World world, IPermission permission) {
        return getWorldProvider().remove(plugin, player, world, permission.getName());
    }

    /**
     * Add a player to a group permission.
     *
     * @param plugin     The plugin adding the player to the group.
     * @param player     The player to add to the group.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was added.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    public static boolean addGroup(Plugin plugin, OfflinePlayer player, String groupName) {
        return getGroupProvider().addGroup(plugin, player, groupName);
    }

    /**
     * Add a player to a group permission in the specified world.
     *
     * @param plugin     The plugin adding the player to the group.
     * @param player     The player to add to the group.
     * @param world      The world.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was added.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have world group support.
     */
    public static boolean addGroup(Plugin plugin, OfflinePlayer player, World world, String groupName) {
        return getWorldGroupProvider().addGroup(plugin, player, world, groupName);
    }

    /**
     * Remove a player from a group permission.
     *
     * @param plugin     The plugin removing the player from the group.
     * @param player     The player to remove from the group.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was removed.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    public static boolean removeGroup(Plugin plugin, OfflinePlayer player, String groupName) {
        return getGroupProvider().removeGroup(plugin, player, groupName);
    }

    /**
     * Remove a player from a group permission.
     *
     * @param plugin     The plugin removing the player from the group.
     * @param player     The player to remove from the group.
     * @param world      The world.
     * @param groupName  The name of the group.
     *
     * @return  True if the player was removed.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have world group support.
     */
    public static boolean removeGroup(Plugin plugin, OfflinePlayer player, World world, String groupName) {
        return getWorldGroupProvider().removeGroup(plugin, player, world, groupName);
    }

    /**
     * Determine if a player has group permission.
     *
     * @param player     The player to check.
     * @param groupName  The name of the group.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    public static boolean hasGroup(OfflinePlayer player, String groupName) {
        Collection<IPermissionGroup> groups = getGroups(player);
        if (groups.isEmpty())
            return false;

        for (IPermissionGroup group : groups) {
            if (group.getName().equals(groupName))
                return true;
        }

        return false;
    }

    /**
     * Get a string array of group permission names.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    @Nullable
    public static Collection<IPermissionGroup> getGroups() {
        return getGroupProvider().getGroups();
    }

    /**
     * Get a string array of groups the specified player is in.
     *
     * @param player  The player to check.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    public static Collection<IPermissionGroup> getGroups(OfflinePlayer player) {
        return getGroupProvider().getGroups(player);
    }

    /**
     * Get a string array of groups the specified player is in while
     * in the specified world.
     *
     * @param player  The player to check.
     * @param world   The world.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have world group support.
     */
    public static Collection<IPermissionGroup> getGroups(OfflinePlayer player, World world) {
        return getWorldGroupProvider().getGroups(player, world);
    }

    /**
     * Fix permission groups of all players on the server. Ensures
     * players have the permission groups specified if they are able to
     * have them as specified by the permission group instances provided.
     *
     * @param plugin  The plugin fixing permission groups.
     * @param groups  The groups to fix.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
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
     * @param player  The player whose group permissions need to be checked.
     * @param groups  The groups to fix.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    public static void fixPermissionGroups(Plugin plugin, OfflinePlayer player,
                                           Collection<IPermissionGroup> groups) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(groups);

        if (!hasGroupSupport())
            return;

        if (!(player instanceof Player))
            return;

        for (IPermissionGroup group : groups) {
            boolean canAssign = group.canAssign(player);

            if (canAssign) {
                addGroup(plugin, player, group.getName());
            } else {
                removeGroup(plugin, player, group.getName());
            }
        }
    }

    /**
     * Get the permissions provider.
     */
    public static IPermissionsProvider getProvider() {
        return Nucleus.getProviders().getPermissions();
    }

    /**
     * Get the world permissions provider.
     *
     * @throws java.lang.UnsupportedOperationException
     */
    public static IWorldPermissionsProvider getWorldProvider() {
        IWorldPermissionsProvider provider =
                (IWorldPermissionsProvider)Nucleus.getProviders().getPermissions();

        if (provider == null)
            throw new UnsupportedOperationException();

        return provider;
    }

    /**
     * Get the group permissions provider.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have group support.
     */
    public static IGroupPermissionsProvider getGroupProvider() {
        IGroupPermissionsProvider provider =
                (IGroupPermissionsProvider)Nucleus.getProviders().getPermissions();

        if (provider == null)
            throw new UnsupportedOperationException();

        return provider;
    }

    /**
     * Get the world group permissions provider.
     *
     * @throws java.lang.UnsupportedOperationException if permissions provider does not have world group support.
     */
    public static IWorldGroupPermissionsProvider getWorldGroupProvider() {
        IWorldGroupPermissionsProvider provider =
                (IWorldGroupPermissionsProvider)Nucleus.getProviders().getPermissions();

        if (provider == null)
            throw new UnsupportedOperationException();

        return provider;
    }
}
