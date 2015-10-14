/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.internal.providers.selection;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.permissions.Permissions;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelectProvider;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.regions.SimpleRegionSelection;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * NucleusFramework's default selection provider when no other
 * provider is available.
 */
public final class NucleusSelectionProvider extends Provider implements IRegionSelectProvider {

    public static final String NAME = "NucleusRegionSelector";
    public static final String PERMISSION = "nucleusframework.providers.nucleusselection.wand";

    @Localizable static final String _P1_SELECTED =
            "{DARK_PURPLE}P1 region point selected:\n{DARK_PURPLE}{0: location}";

    @Localizable static final String _P2_SELECTED =
            "{DARK_PURPLE}P2 region point selected:\n{DARK_PURPLE}{0: location}";

    private final Map<UUID, Location> _p1Selections = new PlayerMap<>(Nucleus.getPlugin());
    private final Map<UUID, Location> _p2Selections = new PlayerMap<>(Nucleus.getPlugin());

    private final Object _sync = new Object();

    public NucleusSelectionProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default region selection provider."));
    }

    @Override
    public void onEnable() {

        Permissions.register(PERMISSION, PermissionDefault.OP, true);

        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), Nucleus.getPlugin());
    }

    @Nullable
    @Override
    public IRegionSelection getSelection(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {

            Location p1 = _p1Selections.get(player.getUniqueId());
            if (p1 == null)
                return null;

            Location p2 = _p2Selections.get(player.getUniqueId());
            if (p2 == null)
                return null;

            return new SimpleRegionSelection(p1.clone(), p2.clone());
        }
    }

    @Override
    public boolean setSelection(Player player, IRegionSelection selection) {
        PreCon.notNull(player);
        PreCon.notNull(selection);

        if (!selection.isDefined())
            return false;

        assert selection.getP1() != null;
        assert selection.getP2() != null;

        synchronized (_sync) {
            _p1Selections.put(player.getUniqueId(), selection.getP1().clone());
            _p2Selections.put(player.getUniqueId(), selection.getP2().clone());
        }

        return true;
    }

    private class BukkitEventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
        private void onPlayerInteract(PlayerInteractEvent event) {

            if (!event.hasBlock())
                return;

            Player player = event.getPlayer();

            if (!hasPermission(player))
                return;

            ItemStack item = player.getInventory().getItemInHand();
            if (item == null)
                return;

            if (item.getType() != Material.WOOD_AXE)
                return;

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Location p1 = event.getClickedBlock().getLocation();
                setP1(player, p1);
                event.setCancelled(true);
            }
            else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location p2 = event.getClickedBlock().getLocation();
                setP2(player, p2);
                event.setCancelled(true);
            }
        }

        @EventHandler
        private void onCommand(PlayerCommandPreprocessEvent event) {

            Player player = event.getPlayer();

            if (!hasPermission(player))
                return;

            String message = event.getMessage();

            switch (message) {
                case "//wand":
                    player.getInventory().addItem(new ItemStack(Material.WOOD_AXE));
                    event.setCancelled(true);
                    break;
                case "//pos1":
                    setP1(player, player.getLocation());
                    event.setCancelled(true);
                    break;
                case "//pos2":
                    setP2(player, player.getLocation());
                    event.setCancelled(true);
                    break;
            }
        }
    }

    private void setP1(Player player, Location location) {
        Location previous;

        synchronized (_sync) {
            previous = _p1Selections.put(player.getUniqueId(), location);
        }

        if (!location.equals(previous)) {
            NucMsg.tell(player, NucLang.get(_P1_SELECTED, LocationUtils.serialize(location, 2)));
        }
    }

    private void setP2(Player player, Location location) {
        Location previous;

        synchronized (_sync) {
            previous = _p2Selections.put(player.getUniqueId(), location);
        }

        if (!location.equals(previous)) {
            NucMsg.tell(player, NucLang.get(_P2_SELECTED, LocationUtils.serialize(location, 2)));
        }
    }

    private boolean hasPermission(Player player) {
        return player.isOp() ||
                Permissions.has(player, PERMISSION);
    }
}
