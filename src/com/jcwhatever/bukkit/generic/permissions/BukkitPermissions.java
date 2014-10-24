package com.jcwhatever.bukkit.generic.permissions;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitPermissions extends AbstractBukkitPermissions {
	
	private static Map<UUID, PermissionAttachment> _transient = new PlayerMap<PermissionAttachment>();
	private IDataNode _permissions;

	BukkitPermissions() {
		
		_permissions = DataStorage.getStorage(GenericsLib.getInstance(), new DataPath("permissions"));
		_permissions.load();
		
	}
	
	public IDataNode getPermissions() {
		return _permissions;
	}
	
	@Override
	public boolean has(Player p, String permission) {
		return p.hasPermission(permission);
	}

	@Override
	public boolean has(CommandSender sender, String permission) {
        return !(sender instanceof Player) || has((Player) sender, permission);
    }

	@Override
	public boolean has(Player p, World world, String permission) {
		return p.hasPermission(permission);
	}

	@Override
	public boolean has(CommandSender sender, World world, String permission) {
        return !(sender instanceof Player) || has((Player) sender, permission);
    }

	@Override
	public boolean addTransient(Plugin plugin, Player p, String permission) {
		PreCon.notNull(plugin);
    	PreCon.notNull(p);
    	PreCon.notNullOrEmpty(permission);
    	
    	IPermission perm = Permissions.get(permission);
        if (perm == null) 
        	return false;
            	
    	PermissionAttachment attachment = _transient.get(p.getUniqueId());
    	if (attachment == null) {
    		attachment = p.addAttachment(plugin);
    		_transient.put(p.getUniqueId(), attachment);
    	}
    		
        attachment.setPermission(perm.getSuperPermission(), true);

        return true;
	}

	@Override
	public boolean removeTransient(Plugin plugin, Player p, String permission) {
		PreCon.notNull(p);
    	PreCon.notNullOrEmpty(permission);
    	
    	IPermission perm = Permissions.get(permission);
        if (perm == null) 
        	return false;
            	
        PermissionAttachment attachment = _transient.get(p.getUniqueId());
    	if (attachment == null) {
    		return false;
    	}
    	
        attachment.setPermission(perm.getSuperPermission(), false);

        return true;
	}

	@Override
	public boolean add(Plugin plugin, Player p, String permission) {
		String pid = p.getUniqueId().toString() + '.' + plugin.getName();
		
		List<String> permissions = _permissions.getStringList(pid, null);
		if (permissions == null)
			permissions = new ArrayList<String>(30);
		
		permissions.add(permission);
		
		_permissions.set(pid, permissions);
		_permissions.saveAsync(null);
		
		return addTransient(plugin, p, permission);
	}

	@Override
	public boolean add(Plugin plugin, Player p, World world, String permission) {
		return add(plugin, p, permission);
	}

	@Override
	public boolean remove(Plugin plugin, Player p, String permission) {
		String pid = p.getUniqueId().toString() + '.' + plugin.getName();
		
		List<String> permissions = _permissions.getStringList(pid, null);
		if (permissions == null)
			permissions = new ArrayList<String>(30);
		
		permissions.remove(permission);
		
		_permissions.set(pid, permissions);
		
		return removeTransient(plugin, p, permission);
	}

	@Override
	public boolean remove(Plugin plugin, Player p, World world,	String permission) {
		return remove(plugin, p, permission);
	}

	@Override
	public boolean addGroup(Plugin plugin, Player p, String groupName) {
		return false;
	}

	@Override
	public boolean addGroup(Plugin plugin, Player p, World world, String groupName) {
		return false;
	}

	@Override
	public boolean removeGroup(Plugin plugin, Player p, String groupName) {
		return false;
	}

	@Override
	public boolean removeGroup(Plugin plugin, Player p, World world, String groupName) {
		return false;
	}

	@Override
	public boolean hasGroupSupport() {
		return false;
	}

	@Override
	public String[] getGroups() {
		return null;
	}

	@Override
	public String[] getGroups(Player p) {
		return null;
	}

	@Override
	public String[] getGroups(Player p, World world) {
		return null;
	}


}
