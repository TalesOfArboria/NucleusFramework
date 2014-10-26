/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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
