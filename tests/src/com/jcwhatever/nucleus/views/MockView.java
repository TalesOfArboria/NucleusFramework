package com.jcwhatever.nucleus.views;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Mock implementation of a {@link View}.
 */
public class MockView extends View {

    private InventoryView _view;
    private Inventory _inventory;

    protected ViewCloseReason onCloseReason;
    protected ViewOpenReason onOpenReason;

    /**
     * Constructor.
     *
     * @param plugin
     */
    protected MockView(Plugin plugin) {
        super(plugin);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Nullable
    @Override
    public InventoryView getInventoryView() {
        return _view;
    }

    @Nullable
    @Override
    public Inventory getInventory() {
        return _inventory;
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {

        onOpenReason = reason;

        _inventory = Bukkit.createInventory(getViewSession().getPlayer(), getInventoryType(), "test");

        _view = getViewSession().getPlayer().openInventory(_inventory);
        return true;
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        onCloseReason = reason;
    }
}
