package com.jcwhatever.nucleus.views.menu;

import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.views.ViewOpenReason;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Mock implementation of {@link MenuView}.
 */
public class MockMenuView extends MenuView {

    ViewOpenReason openReason;
    MenuItem selected;
    MenuItem menuItem;

    protected MockMenuView(Plugin plugin, @Nullable ItemStackMatcher comparer) {
        super(plugin, comparer);
    }

    @Override
    protected List<MenuItem> createMenuItems() {

        return ArrayUtils.asList(
                menuItem = new MenuItem(0, new ItemStack(Material.WOOD)));
    }

    @Override
    protected void onItemSelect(MenuItem menuItem) {
        selected = menuItem;
    }

    @Override
    public String getTitle() {
        return "mock";
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        openReason = reason;
    }
}
