package com.jcwhatever.bukkit.generic.permissions;

import java.util.UUID;

public interface IPermissionGroup extends Comparable<IPermissionGroup> {

	String getPermissionGroupName();
	
	boolean canAssignPermissionGroup(UUID playerId);
}
