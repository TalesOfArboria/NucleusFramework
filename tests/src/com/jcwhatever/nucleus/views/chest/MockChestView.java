package com.jcwhatever.nucleus.views.chest;

import com.jcwhatever.v1_8_R3.MockInventory;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Mock implementation of a {@link ChestView}.
 */
public class MockChestView extends ChestView {

    ChestEventInfo itemsPlaced;
    ChestEventInfo itemsPickup;
    ChestEventInfo itemsDropped;
    ViewOpenReason openReason;

    protected MockChestView(Plugin plugin, @Nullable ItemStackMatcher comparer) {
        super(plugin, comparer);
    }

    @Override
    public String getTitle() {
        return "title";
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        openReason = reason;
    }

    @Override
    protected Inventory createInventory() {
        return new MockInventory(getPlayer(), InventoryType.CHEST, 9);
    }

    @Override
    protected ChestEventAction onItemsPlaced(ChestEventInfo eventInfo) {
        itemsPlaced = eventInfo;
        return ChestEventAction.ALLOW;
    }

    @Override
    protected ChestEventAction onItemsPickup(ChestEventInfo eventInfo) {
        itemsPickup = eventInfo;
        return ChestEventAction.ALLOW;
    }

    @Override
    protected ChestEventAction onItemsDropped(ChestEventInfo eventInfo) {
        itemsDropped = eventInfo;
        return ChestEventAction.ALLOW;
    }
}
