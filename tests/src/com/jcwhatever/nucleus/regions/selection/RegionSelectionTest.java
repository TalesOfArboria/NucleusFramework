package com.jcwhatever.nucleus.regions.selection;

import org.bukkit.Location;

/**
 * Tests {@link RegionSelection}.
 */
public class RegionSelectionTest extends IRegionSelectionTest {

    @Override
    protected IRegionSelection getUndefinedSelection() {
        return new RegionSelection();
    }

    @Override
    protected IRegionSelection getSelection(Location p1, Location p2) {
        return new RegionSelection(p1, p2);
    }
}