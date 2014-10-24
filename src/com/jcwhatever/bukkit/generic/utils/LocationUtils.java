package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LocationUtils {

    private LocationUtils () {}

	static BlockFace[] _yawFaces = new BlockFace[] {
		BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST,
		BlockFace.WEST,  BlockFace.WEST_NORTH_WEST,  BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST,
		BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST,
		BlockFace.EAST,  BlockFace.EAST_SOUTH_EAST,  BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST,
		BlockFace.SOUTH
	};

    public static Location getCenteredLocation(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getY(), loc.getBlockZ() + 0.5, loc.getYaw(), loc.getPitch());
    }

    public static boolean teleportCentered(Entity p, Location loc) {
        Location adjusted = getCenteredLocation(loc);
        return p.teleport(adjusted, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public static Location getBlockLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
	
	public static Location addNoise(Location location, int radiusX, int radiusY, int radiusZ) {
		location = location.clone();
		
		int noiseX = 0, noiseY = 0, noiseZ = 0;
		
		if (radiusX > 0) {
			 noiseX = Rand.getInt(radiusX * 2) - radiusX;
		}
		
		if (radiusY > 0) {
			noiseY = Rand.getInt(radiusY * 2) - radiusY;
		}
		
		if (radiusZ > 0) {
			noiseZ = Rand.getInt(radiusZ * 2) - radiusZ;
		}
		
		return location.add(noiseX, noiseY, noiseZ);
	}


	public static boolean isLocationMatch(Location loc1, Location loc2, double precision) {
		
		if (loc1 == null || loc2 == null)
			return false;

		double xDelta = Math.abs(loc1.getX() - loc2.getX());
		double zDelta = Math.abs(loc1.getZ() - loc2.getZ());
		double yDelta = Math.abs(loc1.getY() - loc2.getY());

		return xDelta < precision && zDelta < precision && yDelta < precision;
	}


	public static Location parseSimpleLocation(World world, String coords) {
		String[] parts = coords.split(",");
		if (parts.length != 3) {
			throw new IllegalArgumentException("Input string must contain only x, y, and z");
		}
		Integer x = parseInteger(parts[0]);
		Integer y = parseInteger(parts[1]);
		Integer z = parseInteger(parts[2]);
		if (x != null && y != null && z != null) 
			return new Location(world, (double)x.intValue(), (double)y.intValue(), (double)z.intValue());

		throw new NullPointerException("Some of the parsed values are null!");
	}

	public static Location parseLocation(String coords) {
		return parseLocation(null, coords);
	}

	public static Location parseLocation(World world, String coords) {
		String[] parts = coords.split(",");
		if (parts.length != 5 && parts.length != 6) {
			throw new IllegalArgumentException("String must contain x, y, z, yaw and pitch");
		}

		if (world == null && parts.length != 6) {
			throw new IllegalArgumentException("String must contain x, y, z, yaw, pitch and world");
		}

		Integer x = parseInteger(parts[0]);
		Integer y = parseInteger(parts[1]);
		Integer z = parseInteger(parts[2]);
		Float yaw = parseFloat(parts[3]);
		Float pitch = parseFloat(parts[4]);

		if (parts.length == 6) {
			world = Bukkit.getWorld((String)parts[5]);
		}
		if (x != null && y != null && z != null && yaw != null && pitch != null && world != null) 
			return new Location(world, (double)x.intValue(), (double)y.intValue(), (double)z.intValue(), yaw.floatValue(), pitch.floatValue());

		return null;
	}

	public static String locationToString(Location loc) {
		StringBuffer result = new StringBuffer();
		result.append(String.valueOf(loc.getBlockX()) + ",");
		result.append(String.valueOf(loc.getBlockY()) + ",");
		result.append(String.valueOf(loc.getBlockZ()) + ",");
		result.append(String.valueOf(loc.getYaw()) + ",");
		result.append(String.valueOf(loc.getPitch()) + ",");
		result.append(loc.getWorld().getName());
		return result.toString();
	}

	public static BlockFace getBlockFacingYaw(Location loc) {
		float yaw = (loc.getYaw() + (loc.getYaw() < 0 ? 360 : 0)) % 360;

		int i = (int)(yaw / 22.5);

		if (i > _yawFaces.length - 1 || i < 0) {
			i = _yawFaces.length - 1;
		}

		return _yawFaces[i];
	}


	private static Integer parseInteger(String s) {
		try {
			return Integer.parseInt(s.trim());
		}
		catch (Exception e) {
			return null;
		}
	}

	private static Float parseFloat(String s) {
		try {
			return Float.valueOf(Float.parseFloat(s.trim()));
		}
		catch (Exception e) {
			return null;
		}
	}



}
