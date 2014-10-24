package com.jcwhatever.bukkit.generic.signs;

import org.bukkit.Location;

class SignInfo {

    private final String[] _lines;
	private final Location _location;

	SignInfo(Location location, String...lines) {
		_location = location;
		_lines = lines;
	}

    public String[] getLines() {
        return _lines;
    }

    public Location getLocation() {
        return _location;
    }
}
