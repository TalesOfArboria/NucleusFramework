package com.jcwhatever.nucleus.regions.selection;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.bukkit.v1_8_R1.MockChunk;
import com.jcwhatever.bukkit.v1_8_R1.MockWorld;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.regions.data.CuboidPoint;
import com.jcwhatever.nucleus.regions.data.RegionShape;
import com.jcwhatever.nucleus.regions.data.SyncLocation;

import org.bukkit.Location;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests implementations of {@link IRegionSelection}.
 */
public abstract class IRegionSelectionTest {


    MockWorld _world = BukkitTester.world("world");

    protected abstract IRegionSelection getUndefinedSelection();

    protected abstract IRegionSelection getSelection(Location p1, Location p2);


    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Make sure {@link #isDefined} returns the correct value.
     */
    @Test
    public void testIsDefined() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(false, selection.isDefined());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(true, selection.isDefined());
    }

    /**
     * Make sure {@link #getWorld} returns the correct value.
     */
    @Test
    public void testGetWorld() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getWorld());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(_world, selection.getWorld());
    }

    /**
     * Make sure {@link #getWorldName} returns the correct value.
     */
    @Test
    public void testGetWorldName() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getWorldName());


        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals("world", selection.getWorldName());


        p1 = new SyncLocation("unloadedWorld", 1, 1, 1);
        p2 = new SyncLocation("unloadedWorld", -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals("unloadedWorld", selection.getWorldName());
    }

    /**
     * Make sure {@link #isWorldLoaded} returns the correct value.
     */
    @Test
    public void testIsWorldLoaded() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(false, selection.isWorldLoaded());


        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(true, selection.isWorldLoaded());


        p1 = new SyncLocation("unloadedWorld", 1, 1, 1);
        p2 = new SyncLocation("unloadedWorld", -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(false, selection.isWorldLoaded());
    }

    /**
     * Make sure {@link #getP1} and {@link #getP2} return the correct value.
     */
    @Test
    public void testGetP1_P2() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getP1());
        assertEquals(null, selection.getP2());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(p1, selection.getP1());
        assertEquals(p2, selection.getP2());
    }

    /**
     * Make sure {@link #getLowerPoint} returns the correct value.
     */
    @Test
    public void testGetLowerPoint() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getLowerPoint());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(p2, selection.getLowerPoint());

        selection = getSelection(p2, p1);
        assertEquals(p2, selection.getLowerPoint());
    }

    /**
     * Make sure {@link #getUpperPoint} returns the correct value.
     */
    @Test
    public void testGetUpperPoint() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getUpperPoint());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(p1, selection.getUpperPoint());

        selection = getSelection(p2, p1);
        assertEquals(p1, selection.getUpperPoint());
    }

    /**
     * Make sure {@link #getXStart} returns the correct value.
     */
    @Test
    public void testGetXStart() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getXStart());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(-4, selection.getXStart());
    }

    /**
     * Make sure {@link #getYStart} returns the correct value.
     */
    @Test
    public void testGetYStart() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getYStart());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(-2, selection.getYStart());
    }

    /**
     * Make sure {@link #getZStart} returns the correct value.
     */
    @Test
    public void testGetZStart() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getZStart());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(-6, selection.getZStart());
    }

    /**
     * Make sure {@link #getXEnd} returns the correct value.
     */
    @Test
    public void testGetXEnd() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getXEnd());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(1, selection.getXEnd());
    }

    /**
     * Make sure {@link #getYEnd} returns the correct value.
     */
    @Test
    public void testGetYEnd() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getYEnd());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(5, selection.getYEnd());
    }

    /**
     * Make sure {@link #getZEnd} returns the correct value.
     */
    @Test
    public void testGetZEnd() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getZEnd());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(3, selection.getZEnd());
    }

    /**
     * Make sure {@link #getXWidth} returns the correct value.
     */
    @Test
    public void testGetXWidth() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getXWidth());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(5, selection.getXWidth());
    }

    /**
     * Make sure {@link #getZWidth} returns the correct value.
     */
    @Test
    public void testGetZWidth() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getZWidth());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(9, selection.getZWidth());
    }

    /**
     * Make sure {@link #getYHeight} returns the correct value.
     */
    @Test
    public void testGetYHeight() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getYHeight());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(7, selection.getYHeight());
    }

    /**
     * Make sure {@link #getXBlockWidth} returns the correct value.
     */
    @Test
    public void testGetXBlockWidth() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getXBlockWidth());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(6, selection.getXBlockWidth());
    }

    /**
     * Make sure {@link #getZBlockWidth} returns the correct value.
     */
    @Test
    public void testGetZBlockWidth() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getZBlockWidth());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(10, selection.getZBlockWidth());
    }

    /**
     * Make sure {@link #getYBlockHeight} returns the correct value.
     */
    @Test
    public void testGetYBlockHeight() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getYBlockHeight());

        Location p1 = new Location(_world, 1, -2, 3);
        Location p2 = new Location(_world, -4, 5, -6);

        selection = getSelection(p1, p2);
        assertEquals(8, selection.getYBlockHeight());
    }

    /**
     * Make sure {@link #getVolume} returns the correct value.
     */
    @Test
    public void testGetVolume() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getVolume());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, 5, 5, 5);

        selection = getSelection(p1, p2);
        assertEquals(64, selection.getVolume());
    }

    /**
     * Make sure {@link #getCenter} returns the correct value.
     */
    @Test
    public void testGetCenter() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getCenter());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(new Location(_world, 0.5, 0.5, 0.5), selection.getCenter());
    }

    /**
     * Make sure {@link #getChunkX} returns the correct value.
     */
    @Test
    public void testGetChunkX() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(null, selection.getCenter());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, -1, -1, -1);

        selection = getSelection(p1, p2);
        assertEquals(new Location(_world, 0.5, 0.5, 0.5), selection.getCenter());
    }

    /**
     * Make sure {@link #getChunkZ} returns the correct value.
     */
    @Test
    public void testGetChunkZ() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getChunkZ());

        Location p1 = new Location(_world, 17, 17, 17);
        Location p2 = new Location(_world, 18, 18, 18);

        selection = getSelection(p1, p2);
        assertEquals(1, selection.getChunkZ());

        p1 = new Location(_world, 17, 17, 17);
        p2 = new Location(_world, 100, 100, 100);

        selection = getSelection(p1, p2);
        assertEquals(1, selection.getChunkZ());

        p1 = new Location(_world, -17, -17, -17);
        p2 = new Location(_world, 100, 100, 100);

        selection = getSelection(p1, p2);
        assertEquals(-2, selection.getChunkZ());
    }

    /**
     * Make sure {@link #getChunkXWidth} returns the correct value.
     */
    @Test
    public void testGetChunkXWidth() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getChunkXWidth());

        Location p1 = new Location(_world, 17, 17, 17);
        Location p2 = new Location(_world, 18, 18, 18);

        selection = getSelection(p1, p2);
        assertEquals(1, selection.getChunkXWidth());

        p1 = new Location(_world, 17, 17, 17);
        p2 = new Location(_world, 100, 100, 100);

        selection = getSelection(p1, p2);
        assertEquals(6, selection.getChunkXWidth());
    }

    /**
     * Make sure {@link #getChunkZWidth} returns the correct value.
     */
    @Test
    public void testGetChunkZWidth() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getChunkZWidth());

        Location p1 = new Location(_world, 17, 17, 17);
        Location p2 = new Location(_world, 18, 18, 18);

        selection = getSelection(p1, p2);
        assertEquals(1, selection.getChunkZWidth());

        p1 = new Location(_world, 17, 17, 17);
        p2 = new Location(_world, 100, 100, 100);

        selection = getSelection(p1, p2);
        assertEquals(6, selection.getChunkZWidth());
    }

    /**
     * Make sure {@link #getChunks} returns the correct values.
     */
    @Test
    public void testGetChunks() throws Exception {
        IRegionSelection selection = getUndefinedSelection();
        assertEquals(0, selection.getChunks().size());

        Location p1 = new Location(_world, 17, 17, 17);
        Location p2 = new Location(_world, 18, 18, 18);

        selection = getSelection(p1, p2);
        assertEquals(1, selection.getChunks().size());

        p1 = new Location(_world, 17, 17, 17);
        p2 = new Location(_world, 100, 100, 100);

        selection = getSelection(p1, p2);
        assertEquals(36, selection.getChunks().size());
    }

    /**
     * Make sure {@link #getShape} returns the correct value.
     */
    @Test
    public void testGetShape() throws Exception {

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(RegionShape.CUBOID, selection.getShape());

        Location p1 = new Location(_world, 1, 1, 1);
        Location p2 = new Location(_world, 1, 1, 1);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.POINT, selection.getShape());

        p1 = new Location(_world, 10, 1, 10);
        p2 = new Location(_world, -10, 1, -10);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.FLAT_HORIZONTAL, selection.getShape());

        p1 = new Location(_world, 10, 10, 1);
        p2 = new Location(_world, -10, -10, 1);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.FLAT_NORTH_SOUTH, selection.getShape());

        p1 = new Location(_world, 1, 10, 10);
        p2 = new Location(_world, 1, -10, -10);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.FLAT_WEST_EAST, selection.getShape());

        p1 = new Location(_world, 1, 10, 1);
        p2 = new Location(_world, 1, -10, 1);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.VERTICAL_LINE, selection.getShape());

        p1 = new Location(_world, 1, 1, 10);
        p2 = new Location(_world, 1, 1, -10);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.NORTH_SOUTH_LINE, selection.getShape());

        p1 = new Location(_world, 10, 1, 1);
        p2 = new Location(_world, -10, 1, 1);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.WEST_EAST_LINE, selection.getShape());

        p1 = new Location(_world, 10, 11, 12);
        p2 = new Location(_world, -13, -14, -15);

        selection = getSelection(p1, p2);
        assertEquals(RegionShape.CUBOID, selection.getShape());
    }

    /**
     * Make sure {@link #contains(Location)} returns the correct value.
     */
    @Test
    public void testContains() throws Exception {

        Location p1 = new Location(_world, 10, 10, 10);
        Location p2 = new Location(_world, -10, -10, -10);

        Location inside = new Location(_world, 5, 5, 5);
        Location outside = new Location(_world, 11, 11, 11);

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(false, selection.contains(p1));
        assertEquals(false, selection.contains(p2));
        assertEquals(false, selection.contains(inside));
        assertEquals(false, selection.contains(outside));

        selection = getSelection(p1, p2);
        assertEquals(true, selection.contains(p1));
        assertEquals(true, selection.contains(p2));
        assertEquals(true, selection.contains(inside));
        assertEquals(false, selection.contains(outside));
    }

    /**
     * Make sure {@link #contains(int, int, int)} returns the correct value.
     */
    @Test
    public void testContains1() throws Exception {

        Location p1 = new Location(_world, 10, 10, 10);
        Location p2 = new Location(_world, -10, -10, -10);

        IRegionSelection selection = getUndefinedSelection();
        assertEquals(false, selection.contains(10, 10, 10)); //p1
        assertEquals(false, selection.contains(-10, -10, -10)); //p2
        assertEquals(false, selection.contains(5, 5, 5)); //inside
        assertEquals(false, selection.contains(11, 11, 11)); // outside

        selection = getSelection(p1, p2);
        assertEquals(true, selection.contains(10, 10, 10)); //p1
        assertEquals(true, selection.contains(-10, -10, -10)); //p2
        assertEquals(true, selection.contains(5, 5, 5)); //inside
        assertEquals(false, selection.contains(11, 11, 11)); // outside
    }

    /**
     * Make sure {@link #contains(Location, boolean, boolean, boolean)} returns
     * the correct value.
     */
    @Test
    public void testContains2() throws Exception {
        Location p1 = new Location(_world, 10, 10, 10);
        Location p2 = new Location(_world, -10, -10, -10);

        Location inside = new Location(_world, 5, 5, 5);

        Location outside1 = new Location(_world, 11, 5, 11);
        Location outside2 = new Location(_world, 5, 11, 11);
        Location outside3 = new Location(_world, 11, 11, 5);
        Location outside4 = new Location(_world, 5, 11, 5);

        IRegionSelection selection = getSelection(p1, p2);
        assertEquals(true, selection.contains(inside, true, true, true));
        assertEquals(true, selection.contains(inside, true, true, false));
        assertEquals(true, selection.contains(inside, false, false, false));

        assertEquals(false, selection.contains(outside1, true, true, true));
        assertEquals(false, selection.contains(outside1, true, true, false));
        assertEquals(false, selection.contains(outside1, true, false, true));
        assertEquals(false, selection.contains(outside1, false, true, true));
        assertEquals(false, selection.contains(outside1, true, false, false));
        assertEquals(true, selection.contains(outside1, false, true, false));

        assertEquals(false, selection.contains(outside2, true, true, true));
        assertEquals(false, selection.contains(outside2, true, true, false));
        assertEquals(false, selection.contains(outside2, true, false, true));
        assertEquals(false, selection.contains(outside2, false, true, true));
        assertEquals(true, selection.contains(outside2, true, false, false));
        assertEquals(false, selection.contains(outside2, false, true, false));

        assertEquals(false, selection.contains(outside3, true, true, true));
        assertEquals(false, selection.contains(outside3, true, true, false));
        assertEquals(false, selection.contains(outside3, true, false, true));
        assertEquals(false, selection.contains(outside3, false, true, true));
        assertEquals(false, selection.contains(outside3, true, false, false));
        assertEquals(true, selection.contains(outside3, false, false, true));

        assertEquals(false, selection.contains(outside4, true, true, true));
        assertEquals(false, selection.contains(outside4, true, true, false));
        assertEquals(true, selection.contains(outside4, true, false, true));
        assertEquals(false, selection.contains(outside4, false, true, true));
        assertEquals(true, selection.contains(outside4, true, false, false));
        assertEquals(false, selection.contains(outside4, false, true, false));
    }

    /**
     * Make sure {@link #intersects(int, int)} returns the correct value.
     */
    @Test
    public void testIntersects() throws Exception {

        IRegionSelection selection = getUndefinedSelection();

        assertEquals(false, selection.intersects(1, 1));
        assertEquals(false, selection.intersects(-1, -1));
        assertEquals(false, selection.intersects(0, 0));

        Location p1 = new Location(_world, 10, 10, 10);
        Location p2 = new Location(_world, -10, -10, -10);

        selection = getSelection(p1, p2);

        assertEquals(false, selection.intersects(1, 1));
        assertEquals(true, selection.intersects(-1, -1));
        assertEquals(true, selection.intersects(0, 0));
    }

    /**
     * Make sure {@link #intersects(Chunk)} returns the correct value.
     */
    @Test
    public void testIntersects1() throws Exception {

        MockChunk chunk1 = new MockChunk(_world, 1, 1);
        MockChunk chunk2 = new MockChunk(_world, -1, -1);
        MockChunk chunk3 = new MockChunk(_world, 0, 0);

        IRegionSelection selection = getUndefinedSelection();

        assertEquals(false, selection.intersects(chunk1));
        assertEquals(false, selection.intersects(chunk2));
        assertEquals(false, selection.intersects(chunk3));

        Location p1 = new Location(_world, 10, 10, 10);
        Location p2 = new Location(_world, -10, -10, -10);

        selection = getSelection(p1, p2);

        assertEquals(false, selection.intersects(chunk1));
        assertEquals(true, selection.intersects(chunk2));
        assertEquals(true, selection.intersects(chunk3));
    }

    /**
     * Make sure {@link #getPoint} returns the correct value.
     */
    @Test
    public void testGetPoint() throws Exception {

        Location p1 = new Location(_world, 20, 20, 20);
        Location p2 = new Location(_world, -10, -10, -10);

        IRegionSelection selection = getSelection(p1, p2);

        assertEquals(new Location(_world, -10, -10, -10),
                selection.getPoint(CuboidPoint.X_MIN_Y_MIN_Z_MIN));

        assertEquals(new Location(_world, -10, 20, -10),
                selection.getPoint(CuboidPoint.X_MIN_Y_MAX_Z_MIN));

        assertEquals(new Location(_world, -10, 20, 20),
                selection.getPoint(CuboidPoint.X_MIN_Y_MAX_Z_MAX));

        assertEquals(new Location(_world, -10, -10, 20),
                selection.getPoint(CuboidPoint.X_MIN_Y_MIN_Z_MAX));

        assertEquals(new Location(_world, 20, -10, -10),
                selection.getPoint(CuboidPoint.X_MAX_Y_MIN_Z_MIN));

        assertEquals(new Location(_world, 20, 20, -10),
                selection.getPoint(CuboidPoint.X_MAX_Y_MAX_Z_MIN));

        assertEquals(new Location(_world, 20, 20, 20),
                selection.getPoint(CuboidPoint.X_MAX_Y_MAX_Z_MAX));

        assertEquals(new Location(_world, 20, -10, 20),
                selection.getPoint(CuboidPoint.X_MAX_Y_MIN_Z_MAX));
    }

}