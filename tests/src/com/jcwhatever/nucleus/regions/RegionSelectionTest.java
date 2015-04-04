package com.jcwhatever.nucleus.regions;

import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;

import org.bukkit.Location;

/**
 * Tests {@link SimpleRegionSelection}.
 */
public class RegionSelectionTest extends IRegionSelectionTest {

    @Override
    protected IRegionSelection getUndefinedSelection() {
        return new SimpleRegionSelection();
    }

    @Override
    protected IRegionSelection getSelection(Location p1, Location p2) {
        return new SimpleRegionSelection(p1, p2);
    }
}