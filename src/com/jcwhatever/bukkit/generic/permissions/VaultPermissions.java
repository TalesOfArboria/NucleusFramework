package com.jcwhatever.bukkit.generic.permissions;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermissions extends AbstractBukkitPermissions {
	
	private Permission _perms = null;
	
	VaultPermissions () {
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            _perms = permissionProvider.getProvider();
        }
        
	}

	@Override
	public boolean has(Player p, String permission) {
		return _perms.has(p, permission);
	}

	@Override
	public boolean has(CommandSender sender, String permission) {
		return _perms.has(sender, permission);
	}

	@Override
	public boolean has(Player p, World world, String permission) {
		return _perms.has(p.getWorld(), p.getName(), permission);
	}

	@Override
	public boolean has(CommandSender sender, World world, String permission) {
		if (sender instanceof Player) {
			return _perms.has(((Player)sender).getWorld(), ((Player)sender).getName(), permission);
		}
		
		return true;
	}

	@Override
	public boolean addTransient(Plugin plugin, Player p, String permission) {
		return _perms.playerAddTransient(p, permission);
	}

	@Override
	public boolean removeTransient(Plugin plugin, Player p, String permission) {
		return _perms.playerRemoveTransient(p, permission);
	}

	@Override
	public boolean add(Plugin plugin, Player p, String permission) {
		return _perms.playerAdd(p, permission);
	}

	@Override
	public boolean add(Plugin plugin, Player p, World world, String permission) {
		return _perms.playerAdd(world, p.getName(), permission);
	}

	@Override
	public boolean remove(Plugin plugin, Player p, String permission) {
		return _perms.playerRemove(p, permission);
	}

	@Override
	public boolean remove(Plugin plugin, Player p, World world,	String permission) {
		return _perms.playerRemove(world, p.getName(), permission);
	}

	@Override
	public boolean addGroup(Plugin plugin, Player p, String groupName) {
		return _perms.playerAddGroup(p, groupName);
	}

	@Override
	public boolean addGroup(Plugin plugin, Player p, World world, String groupName) {
		return _perms.playerAddGroup(world, p.getName(), groupName);
	}

	@Override
	public boolean removeGroup(Plugin plugin, Player p, String groupName) {
		return _perms.playerRemoveGroup(p, groupName);
	}

	@Override
	public boolean removeGroup(Plugin plugin, Player p, World world, String groupName) {
		return _perms.playerRemoveGroup(world, p.getName(), groupName);
	}

	@Override
	public boolean hasGroupSupport() {
		return _perms.hasGroupSupport();
	}

	@Override
	public String[] getGroups() {
		return _perms.getGroups();
	}

	@Override
	public String[] getGroups(Player p) {
		return _perms.getPlayerGroups(p);
	}

	@Override
	public String[] getGroups(Player p, World world) {
		return _perms.getPlayerGroups(world, p.getName());
	}
	
}
