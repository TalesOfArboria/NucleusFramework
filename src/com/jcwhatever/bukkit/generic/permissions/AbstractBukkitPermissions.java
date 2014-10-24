package com.jcwhatever.bukkit.generic.permissions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.utils.BatchTracker;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.jcwhatever.bukkit.generic.messaging.Messenger;

public abstract class AbstractBukkitPermissions implements IPermissions {

	private PluginManager _pm = Bukkit.getPluginManager();
	private static final BatchTracker _batch = new BatchTracker();
	private static Set<Permission> _toRecalculate = new HashSet<Permission>(1000);
	private static Set<Permission> _dontRecalculate = new HashSet<Permission>(1000);

	// reflection
	private Object _permissionsMap;
	private Method _putMethod;

	private boolean _canReflect = true;

	@Override
	public IPermission register(String permString, PermissionDefault value) {
		Permission perm = _pm.getPermission(permString);
		if (perm != null) 
			return new SuperPerm(perm);

		perm = new Permission(permString);
		perm.setDefault(value);

		if (!_batch.isRunning() || !addPermissionFast(perm))
			_pm.addPermission(perm);

		return new SuperPerm(perm);
	}
	
	@Override	
	public void unregister(Plugin plugin, String s) {
		plugin.getServer().getPluginManager().removePermission(s);
	}


	private boolean addPermissionFast(Permission permission) {

		if (!_canReflect)
			return false;

		if (_putMethod == null) {

			try {
				
				Field field = _pm.getClass().getDeclaredField("permissions");
				field.setAccessible(true);

				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

				_permissionsMap = field.get(_pm);

				_putMethod = _permissionsMap.getClass().getDeclaredMethod("put", Object.class, Object.class);
				
			} catch (Exception e) {

				e.printStackTrace();
				_canReflect = false;
				Messenger.warning(GenericsLib.getInstance(), "Failed to use reflection to add permission. Permissions will be added using Bukkit API which may take longer.");
				return false;
			}
		}

		try {
			
			_putMethod.invoke(_permissionsMap, permission.getName().toLowerCase(), permission);
			
		} catch (Exception e) {
			e.printStackTrace();
			_canReflect = false;
			Messenger.warning(GenericsLib.getInstance(), "Failed to use reflection to add permission. Permissions will be added using Bukkit API which may take longer.");
			return false;
		}
		
		if (!_dontRecalculate.contains(permission)) {
			_toRecalculate.add(permission);
		}
		
		return true;
	}


	@Override
	public void register(IPermission permission) {
		register(permission, true);
	}

	@Override
	public void register(IPermission permission, boolean check) {
		if (check) {
			Permission perm = _pm.getPermission(permission.getName());
			if (perm != null) 
				return;
		}
		
		if (!_batch.isRunning() || !addPermissionFast(permission.getSuperPermission()))
			_pm.addPermission(permission.getSuperPermission());
	}

	@Override
	public void addParent(IPermission child, IPermission parent, boolean value) {
		
		parent.getChildren().put(child.getName(), value);
		
		if (_batch.isRunning()) {
			
			if (!_dontRecalculate.contains(parent.getSuperPermission()))
				_toRecalculate.add(parent.getSuperPermission());
			
			_toRecalculate.remove(child.getSuperPermission());
			_dontRecalculate.add(child.getSuperPermission());
		}
		
		recalculatePermissibles(parent);
	}

	@Override
	public void runBatchOperation(boolean recalculate, Runnable operations) {

		_batch.start();
		operations.run();
        _batch.end();

		if (_batch.isRunning())
			return;

		if (recalculate) {

			for (Permission permission : _toRecalculate) {
				recalculatePermissionDefault(permission);
				recalculatePermissibles(permission);
			}
		}

		_toRecalculate.clear();
		_dontRecalculate.clear();
	}

	@Override
	public void recalculatePermissibles(IPermission permission) {
		recalculatePermissibles(permission.getSuperPermission());
	}

	private void recalculatePermissibles(Permission permission) {
		if (!_batch.isRunning())
			permission.recalculatePermissibles();
		else if (!_dontRecalculate.contains(permission)) {
			_toRecalculate.add(permission);
		}
	}


	public void recalculatePermissionDefault(IPermission permission) {
		recalculatePermissionDefault(permission.getSuperPermission());
	}

	private void recalculatePermissionDefault(Permission permission) {
		if (!_batch.isRunning())
			_pm.recalculatePermissionDefaults(permission);
		else if (!_dontRecalculate.contains(permission)) {
			_toRecalculate.add(permission);
		}
	}

	@Override
	public IPermission get(String permissionName) {
		return new SuperPerm(_pm.getPermission(permissionName));	
	}

}
