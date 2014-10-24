package com.jcwhatever.bukkit.generic.permissions;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

public class Permissions {
	
	private static IPermissions _permissionsInstance;
	
	static {
		
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            _permissionsInstance = new VaultPermissions();
        }
		else {
			_permissionsInstance = new BukkitPermissions();
		}
	}
    
	public static IPermission register(String permString, PermissionDefault value) {
		return _permissionsInstance.register(permString, value);
    }
	
	public static void register(IPermission permission) {
		_permissionsInstance.register(permission);
	}
	
	public static void register(IPermission permission, boolean check) {
		_permissionsInstance.register(permission, check);
	}
	
	public static void addParent(IPermission child, IPermission parent, boolean value) {
		_permissionsInstance.addParent(child, parent, value);
	}
	
	public static void runBatchOperation(boolean recalculate, Runnable operations) {
		_permissionsInstance.runBatchOperation(recalculate, operations);
	}
	
	public static void recalculatePermissibles(IPermission permission) {
		_permissionsInstance.recalculatePermissibles(permission);
	}
	
    public static void unregister(Plugin plugin, String s) {
    	_permissionsInstance.unregister(plugin, s);
    }
    
    public static IPermission get(String permissionName) {
    	return _permissionsInstance.get(permissionName);	
    }
	
    public static boolean has(Player p, String permission) {
		return _permissionsInstance.has(p, permission);
	}
    
    public static boolean has(Player p, Permission permission) {
		return p.hasPermission(permission);
	}
	
    public static boolean has(CommandSender sender, String permission) {
		return _permissionsInstance.has(sender, permission);
	}
    
    public static boolean has(CommandSender sender, Permission permission) {
        return !(sender instanceof Player) || has((Player) sender, permission);
    }

	
    public static boolean has(Player p, World world, String permission) {
		return _permissionsInstance.has(p, world, permission);
	}
    
	public static boolean has(CommandSender sender, World world, String permission) {
		return _permissionsInstance.has(sender, world, permission);
	}

	
	public static boolean addTransient(Plugin plugin, Player p, String permission) {
		return _permissionsInstance.addTransient(plugin, p, permission);
	}
	
	public static boolean addTransient(Plugin plugin, Player p, Permission permission) {
		return _permissionsInstance.addTransient(plugin, p, permission.getName());
	}

	
	public static boolean removeTransient(Plugin plugin, Player p, String permission) {
		return _permissionsInstance.removeTransient(plugin, p, permission);
	}
	
	public static boolean removeTransient(Plugin plugin, Player p, Permission permission) {
		return _permissionsInstance.removeTransient(plugin, p, permission.getName());
	}

	
	public static boolean add(Plugin plugin, Player p, String permission) {
		return _permissionsInstance.add(plugin, p, permission);
	}
	
	public static boolean add(Plugin plugin, Player p, Permission permission) {
		return _permissionsInstance.add(plugin, p, permission.getName());
	}

	
	public static boolean add(Plugin plugin, Player p, World world, String permission) {
		return _permissionsInstance.add(plugin, p, world, permission);
	}
	
	public static boolean add(Plugin plugin, Player p, World world, Permission permission) {
		return _permissionsInstance.add(plugin, p, world, permission.getName());
	}

	
	public static boolean remove(Plugin plugin, Player p, String permission) {
		return _permissionsInstance.remove(plugin, p, permission);
	}
	
	public static boolean remove(Plugin plugin, Player p, Permission permission) {
		return _permissionsInstance.remove(plugin, p, permission.getName());
	}

	public static boolean remove(Plugin plugin, Player p, World world, String permission) {
		return _permissionsInstance.remove(plugin, p, world, permission);
	}
	
	public static boolean remove(Plugin plugin, Player p, World world, Permission permission) {
		return _permissionsInstance.remove(plugin, p, world, permission.getName());
	}
	
	public static boolean addGroup(Plugin plugin, Player p, String groupName) {
		return _permissionsInstance.addGroup(plugin, p, groupName);
	}

	
	public static boolean addGroup(Plugin plugin, Player p, World world, String groupName) {
		return _permissionsInstance.addGroup(plugin, p, world, groupName);
	}

	
	public static boolean removeGroup(Plugin plugin, Player p, String groupName) {
		return _permissionsInstance.removeGroup(plugin, p, groupName);
	}

	
	public static boolean removeGroup(Plugin plugin, Player p, World world, String groupName) {
		return _permissionsInstance.removeGroup(plugin, p, world, groupName);
	}

	
	public static boolean hasGroupSupport() {
		return _permissionsInstance.hasGroupSupport();
	}

	public static boolean hasGroup(Player p, String groupName) {
		String[] groups = getGroups(p);

        for (String group : groups) {
            if (group.equals(groupName))
                return true;
        }
		
		return false;
	}
	
	public static String[] getGroups() {
		return _permissionsInstance.getGroups();
	}

	
	public static String[] getGroups(Player p) {
		return _permissionsInstance.getGroups(p);
	}

	
	public static String[] getGroups(Player p, World world) {
		return _permissionsInstance.getGroups(p, world);
	}
	
	public static IPermissions getImplementation() {
		return _permissionsInstance;
	}
	
	public static void fixPermissionGroups(Plugin plugin, Collection<IPermissionGroup> groups) {
		if (!_permissionsInstance.hasGroupSupport())
			return;
		
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			Permissions.fixPermissionGroups(plugin, player, groups);			
		}
	}

	
	public static void fixPermissionGroups(Plugin plugin, Player p, Collection<IPermissionGroup> groups) {
		if (!_permissionsInstance.hasGroupSupport())
			return;
		
		UUID playerId = p.getUniqueId();
		
		for (IPermissionGroup group : groups) {
			boolean canAssign = group.canAssignPermissionGroup(playerId);
			boolean hasGroup = hasGroup(p, group.getPermissionGroupName());
			
			if (!canAssign && hasGroup) {
				removeGroup(plugin, p, group.getPermissionGroupName());
			}
			else if (canAssign && !hasGroup) {
				addGroup(plugin, p, group.getPermissionGroupName());
			}
		}
		
	}
   
}
