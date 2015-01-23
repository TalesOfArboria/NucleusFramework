package com.jcwhatever.nucleus.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.bukkit.v1_8_R1.MockEntity;
import com.jcwhatever.bukkit.v1_8_R1.MockWorld;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.easetech.easytest.annotation.Repeat;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class LocationUtilsTest {

    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    @Test
    public void testGetCenteredLocation() throws Exception {

        Location location = new Location(null, 10, 10, 10);

        Location centered = LocationUtils.getCenteredLocation(location);

        assertTrue(centered != location); // must be a different instance
        assertEquals(10.5D, centered.getX(), 0.0D);
        assertEquals(10.0D, centered.getY(), 0.0D);
        assertEquals(10.5D, centered.getZ(), 0.0D);
    }

    @Test
    public void testTeleportCentered() throws Exception {

        MockEntity entity = new MockEntity(0, EntityType.BAT);

        Location location = new Location(BukkitTester.world("dummy"), 10, 10, 10);

        LocationUtils.teleportCentered(entity, location);

        Location centered = entity.getLocation();

        assertEquals(10.5D, centered.getX(), 0.0D);
        assertEquals(10.0D, centered.getY(), 0.0D);
        assertEquals(10.5D, centered.getZ(), 0.0D);
    }

    @Test
    public void testGetBlockLocation() throws Exception {

        Location location = new Location(BukkitTester.world("dummy"),
                10.37D, 10.29D, 10.953D, 3.35f, 8.36f);

        Location blockLoc = LocationUtils.getBlockLocation(location);

        assertTrue(blockLoc != location); // must be a different instance
        assertEquals(10D, blockLoc.getX(), 0.0D);
        assertEquals(10D, blockLoc.getY(), 0.0D);
        assertEquals(10D, blockLoc.getZ(), 0.0D);
        assertEquals(0f, blockLoc.getYaw(), 0.0f);
        assertEquals(0f, blockLoc.getPitch(), 0.0f);
    }

    @Test
    public void testAdd() throws Exception {

        Location location = new Location(BukkitTester.world("dummy"), 10, 10, 10);

        Location added = LocationUtils.add(location, 1.1D, 1.2D, 1.3D);

        assertTrue(added != location); // must be a different instance
        assertEquals(11.1D, added.getX(), 0.0D);
        assertEquals(11.2D, added.getY(), 0.0D);
        assertEquals(11.3D, added.getZ(), 0.0D);
        assertEquals(0f, added.getYaw(), 0.0f);
        assertEquals(0f, added.getPitch(), 0.0f);
    }

    @Test
    @Repeat(times=500)
    public void testAddNoise() throws Exception {

        Location location = new Location(BukkitTester.world("dummy"), 10, 10, 10);

        Location noise = LocationUtils.addNoise(location, 5, 5, 5);

        assertTrue(noise != location); // must be a different instance
        assertTrue(noise.getX() <= 15D);
        assertTrue(noise.getX() >= 5D);
        assertTrue(noise.getY() <= 15D);
        assertTrue(noise.getY() >= 5D);
        assertTrue(noise.getZ() <= 15D);
        assertTrue(noise.getZ() >= 5D);

        assertEquals(0f, noise.getYaw(), 0.0f);
        assertEquals(0f, noise.getPitch(), 0.0f);
    }

    @Test
    public void testIsLocationMatch() throws Exception {

        Location location1 = new Location(BukkitTester.world("dummy"), 10, 10, 10);

        Location location2 = new Location(BukkitTester.world("dummy"), 11, 11, 11);

        assertEquals(true, LocationUtils.isLocationMatch(location1, location2, 1D));

        assertEquals(false, LocationUtils.isLocationMatch(location1, location2, 0.5D));
    }

    @Test
    public void testParseSimpleLocation() throws Exception {

        String text = "10,10,10.0";

        Location location = LocationUtils.parseSimpleLocation(BukkitTester.world("world"), text);

        assertTrue(location != null);

        assertEquals(10.0D, location.getX(), 0.0D);
        assertEquals(10.0D, location.getY(), 0.0D);
        assertEquals(10.0D, location.getZ(), 0.0D);
    }

    @Test
    public void testParseLocation() throws Exception {

        String text = "10,10,10.0,0.5,0.6,dummy";

        MockWorld world = BukkitTester.world("dummy");

        Location location = LocationUtils.parseLocation(text);

        assertTrue(location != null);

        assertEquals(10.0D, location.getX(), 0.0D);
        assertEquals(10.0D, location.getY(), 0.0D);
        assertEquals(10.0D, location.getZ(), 0.0D);
        assertEquals(0.5f, location.getYaw(), 0.0f);
        assertEquals(0.6f, location.getPitch(), 0.0f);
        assertEquals(world, location.getWorld());
    }

    @Test
    public void testParseLocationWorldName() throws Exception {

        String text = "10,10,10.0,0.5,0.6,dummy";

        String worldName = LocationUtils.parseLocationWorldName(text);

        assertEquals("dummy", worldName);
    }

    @Test
    public void testGetBlockFacingYaw() throws Exception {

        Location location = new Location(null, 0, 0, 0, 0, 0);

        location.setYaw(-22f);
        assertEquals(BlockFace.SOUTH_SOUTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(0);
        assertEquals(BlockFace.SOUTH, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(22f);
        assertEquals(BlockFace.SOUTH_SOUTH_WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(45f);
        assertEquals(BlockFace.SOUTH_WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(68f);
        assertEquals(BlockFace.WEST_SOUTH_WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(90f);
        assertEquals(BlockFace.WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(112f);
        assertEquals(BlockFace.WEST_NORTH_WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(134f);
        assertEquals(BlockFace.NORTH_WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(158f);
        assertEquals(BlockFace.NORTH_NORTH_WEST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(180f);
        assertEquals(BlockFace.NORTH, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(202f);
        assertEquals(BlockFace.NORTH_NORTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(224f);
        assertEquals(BlockFace.NORTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(248f);
        assertEquals(BlockFace.EAST_NORTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(270f);
        assertEquals(BlockFace.EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(292f);
        assertEquals(BlockFace.EAST_SOUTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(314f);
        assertEquals(BlockFace.SOUTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(336f);
        assertEquals(BlockFace.SOUTH_SOUTH_EAST, LocationUtils.getBlockFacingYaw(location));

        location.setYaw(358f);
        assertEquals(BlockFace.SOUTH, LocationUtils.getBlockFacingYaw(location));
    }

    @Test
    public void testFindSurfaceBelow() throws Exception {

        Location location = new Location(BukkitTester.world("dummy"), 0, 100, 0, 0, 0);

        Location result = LocationUtils.findSurfaceBelow(location);

        assertTrue(result != null);

        assertEquals(10, result.getY(), 0.0D); // dummy world surface is at y:10
    }

    @Test
    public void testGetClosestLocation() throws Exception {

        World world = BukkitTester.world("dummy");

        Location source = new Location(world, 0, 0, 0);
        Location closest = new Location(world, 10, 10, 10);


        List<Location> locations = ArrayUtils.asList(
                new Location(world, 100, 100, 100),
                new Location(world, 100, 100, 10),
                new Location(world, 100, 10, 100),
                new Location(world, 10, 100, 100),
                closest
        );

        Location result = LocationUtils.getClosestLocation(source, locations);

        assertEquals(closest, result);
    }

    @Test
    public void testGetClosestLocation1() throws Exception {
        World world = BukkitTester.world("dummy");

        Location source = new Location(world, 0, 0, 0);
        final Location closest = new Location(world, 10, 10, 10);
        Location secondClosest = new Location(world, 15, 15, 15);


        List<Location> locations = ArrayUtils.asList(
                new Location(world, 100, 100, 100),
                new Location(world, 100, 100, 10),
                new Location(world, 100, 10, 100),
                new Location(world, 10, 100, 100),
                secondClosest,
                closest
        );

        Location result = LocationUtils.getClosestLocation(source, locations, new IValidator<Location>() {
            @Override
            public boolean isValid(Location element) {
                return element != closest;
            }
        });

        assertEquals(secondClosest, result);
    }

    @Test
    public void testRotate() throws Exception {

        World world = BukkitTester.world("dummy");

        Location axis = new Location(world, 0, 0, 0);
        Location location = new Location(world, 10, 0, 0);

        Location result = LocationUtils.rotate(axis, location, 0, 0, 180);

        assertTrue(result != location); // must not be same instance

        assertEquals(-10, result.getX(), 1.0D);
        assertEquals(0, result.getY(), 1.0D);
        assertEquals(0, result.getZ(), 0.0D);
        assertEquals(0, result.getYaw(), 0.0D);
        assertEquals(0, result.getPitch(), 0.0D);
    }
}