package com.jcwhatever.bukkit.generic.worlds;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages a collection of worlds used to blacklist or
 * whitelist worlds for various purposes.
 */
public class WorldValidator {

	private IDataNode _worldNode;
	private WorldValidationMode _worldMode = WorldValidationMode.BLACKLIST;
	private Set<String> _worlds = new HashSet<String>(10);

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
	public WorldValidator(IDataNode dataNode) {
        PreCon.notNull(dataNode);

		_worldNode = dataNode;

		loadSettings();
	}

    /**
     * Determine if a world is considered valid.
     *
     * @param world  The world to check.
     */
	public boolean isValidWorld(World world) {
        PreCon.notNull(world);

		boolean result = false;
		
		if (_worldMode == WorldValidationMode.BLACKLIST)
			result = !_worlds.contains(world.getName());

		else if (_worldMode == WorldValidationMode.WHITELIST)
			result = _worlds.contains(world.getName());

		return result;
	}

    /**
     * Get the names of the worlds in the collection.
     */
	public List<String> getWorlds() {
		return new ArrayList<String>(_worlds);
	}

    /**
     * Get the mode used to determine how worlds in the collection
     * are used for validation.
     */
	public WorldValidationMode getMode() {
		return _worldMode;
	}

    /**
     * Set the mode used to determine how worlds in the collection
     * are used for validation.
     *
     * @param validationMode  The world validation mode.
     */
	public void setMode(WorldValidationMode validationMode) {
        PreCon.notNull(validationMode);

		_worldMode = validationMode;
		_worldNode.set("mode", validationMode);
		_worldNode.saveAsync(null);
	}

    /**
     * Add a world to the collection.
     *
     * @param world  The world to add.
     */
	public boolean addWorld(World world) {
        PreCon.notNull(world);

		if (_worlds.add(world.getName())) {
			_worldNode.set("worlds", new ArrayList<String>(_worlds));
			_worldNode.saveAsync(null);

			return true;
		}
		return false;
	}

    /**
     * Remove a world from the collection.
     *
     * @param world  The world to removed.
     */
	public boolean removeWorld(World world) {
        PreCon.notNull(world);

		return removeWorld(world.getName());
	}

    /**
     * Remove a world from the collection.
     *
     * @param worldName  The name of the world.
     */
	public boolean removeWorld(String worldName) {
        PreCon.notNullOrEmpty(worldName);

		if (!_worlds.contains(worldName))
			return false;
		
		if (_worlds.remove(worldName)) {
			_worldNode.set("worlds", new ArrayList<String>(_worlds));
			_worldNode.saveAsync(null);

			return true;
		}
		
		return false;
	}

    // initial load of settings
	private void loadSettings() {
		_worldMode = _worldNode.getEnum("mode", _worldMode, WorldValidationMode.class);

		_worlds.clear();
		List<String> worldNames = _worldNode.getStringList("worlds", null);
		if (worldNames != null && !worldNames.isEmpty()) {
			_worlds.addAll(worldNames);
		}
	}

}
