package com.jcwhatever.bukkit.generic.permissions;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Map;
import java.util.Set;

public interface IPermission {
	
	boolean hasSuperPermission();
	
	Permission getSuperPermission();
	
	
	String getName();
		
	void addParent(IPermission permission, boolean value);
	
	void addParent(String name, boolean value);
	
	Map<String, Boolean> getChildren();
	
	PermissionDefault getDefault();
	
	String getDescription();
	
	Set<Permissible> getPermissables();
	
	void recalculatePermissibles();
	
	void setDefault(PermissionDefault value);

	void setDescription(String description);

}
