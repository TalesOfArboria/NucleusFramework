package com.jcwhatever.bukkit.generic.worlds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.World;

import com.jcwhatever.bukkit.generic.performance.SingleCache;
import com.jcwhatever.bukkit.generic.storage.IDataNode;

public class WorldManager {

	private IDataNode _worldNode;

	private WorldMode _worldMode = WorldMode.BLACKLIST;
	private Set<String> _worlds = new HashSet<String>();
	
	private SingleCache<World, Boolean> _validWorldCache = new SingleCache<World, Boolean>();
	
	public WorldManager(IDataNode worldNode) {
		_worldNode = worldNode;

		loadSettings();
	}

	public boolean isValidWorld(World world) {
		
		if (_validWorldCache.keyEquals(world)) {
			return _validWorldCache.getValue();
		}
		
		boolean result = false;
		
		if (_worldMode == WorldMode.BLACKLIST)
			result = !_worlds.contains(world.getName());

		else if (_worldMode == WorldMode.WHITELIST)
			result = _worlds.contains(world.getName());
		
		
		_validWorldCache.set(world, result);
		
		return result;
	}

	public List<String> getWorlds() {
		return new ArrayList<String>(_worlds);
	}

	public WorldMode getMode() {
		return _worldMode;
	}
	
	public void setMode(WorldMode worldMode) {
		_worldMode = worldMode;
		_worldNode.set("mode", worldMode);
		_worldNode.saveAsync(null);
		
		_validWorldCache.reset();
	}

	public boolean addWorld(World world) {
		if (_worlds.add(world.getName())) {
			_worldNode.set("worlds", new ArrayList<String>(_worlds));
			_worldNode.saveAsync(null);
			
			_validWorldCache.reset();
			
			return true;
		}
		return false;
	}
	
	public boolean removeWorld(World world) {
		return removeWorld(world.getName());
	}
	
	public boolean removeWorld(String worldName) {
		if (!_worlds.contains(worldName))
			return false;
		
		if (_worlds.remove(worldName)) {
			_worldNode.set("worlds", new ArrayList<String>(_worlds));
			_worldNode.saveAsync(null);
			
			_validWorldCache.reset();
			
			return true;
		}
		
		return false;
	}

	private void loadSettings() {
		_worldMode = _worldNode.getEnum("mode", _worldMode, WorldMode.class);

		_worlds.clear();
		List<String> worldNames = _worldNode.getStringList("worlds", null);
		if (worldNames != null && !worldNames.isEmpty()) {
			_worlds.addAll(worldNames);
		}
		
		_validWorldCache.reset();
	}

}
