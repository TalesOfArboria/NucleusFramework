package com.jcwhatever.bukkit.generic.permissions;

import java.util.Map;
import java.util.Set;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class SuperPerm implements IPermission {
	
	private Permission _permission;
	
	public SuperPerm(Permission permission) {
		_permission = permission;
	}
	
	public String getName() {
		return _permission.getName();
		
	}
		
	public void addParent(IPermission permission, boolean value) {
		_permission.addParent(permission.getSuperPermission(), value);
	}
	
	public void addParent(String name, boolean value) {
		_permission.addParent(name, value);
	}
	
	public Map<String, Boolean> getChildren() {
		return _permission.getChildren();
	}
	
	public PermissionDefault getDefault() {
		return _permission.getDefault();
	}
	
	public String getDescription() {
		return _permission.getDescription();
	}
	
	public Set<Permissible> getPermissables() {
		return _permission.getPermissibles();
	}
	
	public void recalculatePermissibles() {
		_permission.getPermissibles();
	}
	
	public void setDefault(PermissionDefault value) {
		_permission.setDefault(value);
	}
	
	@Override
	public int hashCode() {
		return _permission.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return _permission.equals(o);
	}
		
	public boolean hasSuperPermission() {
		return true;
	}
	
	public Permission getSuperPermission() {
		return _permission;
	}

	@Override
	public void setDescription(String description) {
		_permission.setDescription(description);
	}
	
	

}
