package com.jcwhatever.bukkit.generic.permissions;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

public interface IPermissions {
	
	IPermission register(String permString, PermissionDefault value);
	
	void register(IPermission permission);
	
	void register(IPermission permission, boolean check);
	
	void addParent(IPermission child, IPermission parent, boolean value);
	
	void runBatchOperation(boolean recalculate, Runnable operations);
	
	void recalculatePermissibles(IPermission permission);
	
    void unregister(Plugin plugin, String s);
    
    IPermission get(String permissionName);
	
	
	boolean has(Player p, String permission);
	
	boolean has(CommandSender sender, String permission);
	
	boolean has(Player p, World world, String permission);
	
	boolean has(CommandSender sender, World world, String permission);
    
    boolean addTransient(Plugin plugin, Player p, String permission);
    
    boolean removeTransient(Plugin plugin, Player p, String permission);
    
    boolean add(Plugin plugin, Player p, String permission);
    
    boolean add(Plugin plugin, Player p, World world, String permission);
    
    boolean remove(Plugin plugin, Player p, String permission);
    
    boolean remove(Plugin plugin, Player p, World world, String permission);
    
    
    boolean addGroup(Plugin plugin, Player p, String groupName);
    
    boolean addGroup(Plugin plugin, Player p, World world, String groupName);
    
    boolean removeGroup(Plugin plugin, Player p, String groupName);
    
    boolean removeGroup(Plugin plugin, Player p, World world, String groupName);
    
    
    boolean hasGroupSupport();
    
    String[] getGroups();
    
    String[] getGroups(Player p);
    
    String[] getGroups(Player p, World world);
    
}
