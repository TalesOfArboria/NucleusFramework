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


package com.jcwhatever.nucleus.internal.listeners;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.anvil.AnvilEnchantItemEvent;
import com.jcwhatever.nucleus.events.anvil.AnvilRenameItemEvent;
import com.jcwhatever.nucleus.events.anvil.AnvilRepairItemEvent;
import com.jcwhatever.nucleus.events.manager.EventManager;
import com.jcwhatever.nucleus.events.signs.SignInteractEvent;
import com.jcwhatever.nucleus.internal.regions.InternalRegionManager;
import com.jcwhatever.nucleus.internal.regions.RegionEventReason;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.sounds.playlist.PlayList;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils.DisplayNameOption;
import com.jcwhatever.nucleus.utils.materials.Materials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class JCGEventListener implements Listener {

    private final InternalRegionManager _regionManager;

    public JCGEventListener(InternalRegionManager regionManager) {
        _regionManager = regionManager;
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {

        // unregister all event handlers associated with the plugin
        EventManager.unregisterPlugin(event.getPlugin());

        if (Nucleus.getPlugin().isEnabled()) {
            Nucleus.getScriptApiRepo().unregisterPlugin(event.getPlugin());
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    private void onPlayerJoin(PlayerJoinEvent event) {

        final Player p = event.getPlayer();

        // tell player missed important messages
        Nucleus.getMessengerFactory().tellImportant(p, true);

        _regionManager.getPlayerWatcher()
                .updatePlayerLocation(p, RegionEventReason.JOIN_SERVER);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerMove(PlayerMoveEvent event) {

        _regionManager.getPlayerWatcher()
                .updatePlayerLocation(event.getPlayer(), event.getTo(), RegionEventReason.MOVE);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onPlayerDeath(PlayerDeathEvent event) {

        // check for "cancelled" event
        if (event.getEntity().getHealth() > 0)
            return;

        _regionManager.getPlayerWatcher()
                .updatePlayerLocation(event.getEntity(), LeaveRegionReason.DEAD);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        _regionManager.getPlayerWatcher()
                .updatePlayerLocation(event.getPlayer(), event.getRespawnLocation(), RegionEventReason.RESPAWN);
    }

    @EventHandler(priority=EventPriority.LOWEST) // first priority
    private void onPlayerQuit(PlayerQuitEvent event) {
        PlayList.clearQueue(event.getPlayer());

        _regionManager.getPlayerWatcher()
                .updatePlayerLocation(event.getPlayer(), LeaveRegionReason.QUIT_SERVER);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        if (event.getFrom() == null || event.getTo() == null)
            return;

        // player teleporting to a different world
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {

            PlayList.clearQueue(event.getPlayer());
        }

        if (event.getCause() != TeleportCause.UNKNOWN) {
            _regionManager.getPlayerWatcher()
                    .updatePlayerLocation(event.getPlayer(), event.getTo(), RegionEventReason.TELEPORT);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {

        if (!event.hasBlock()) {
            return;
        }

        Block clicked = event.getClickedBlock();

        // Signs
        if (clicked != null && (clicked.getType() == Material.WALL_SIGN
                || clicked.getType() == Material.SIGN_POST)) {

            Action action = event.getAction();
            BlockState signState = clicked.getState();

            if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK)
                return;

            if (!(signState instanceof Sign))
                return;

            Sign sign = (Sign)signState;
            SignInteractEvent signInteractEvent = new SignInteractEvent(event, sign);
            Nucleus.getEventManager().callBukkit(this, signInteractEvent);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent event) {

        HumanEntity entity = event.getWhoClicked();

        if (!(entity instanceof Player))
            return;

        final Player p = (Player)entity;

        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory))
            return;

        final AnvilInventory anvilInventory = (AnvilInventory)inventory;
        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();

        if (rawSlot != view.convertSlot(rawSlot) || rawSlot != 2)
            return;

        ItemStack resultItem = anvilInventory.getItem(2);
        if (resultItem == null)
            return;

        ItemStack slot1 = anvilInventory.getItem(0);
        ItemStack slot2 = anvilInventory.getItem(1);

        ItemStack slot1Clone = slot1 == null ? null : slot1.clone();
        ItemStack slot2Clone = slot2 == null ? null : slot2.clone();

        // check for rename
        String originalName = slot1 != null
                ? ItemStackUtils.getDisplayName(slot1, DisplayNameOption.OPTIONAL)
                : null;

        String newName = ItemStackUtils.getDisplayName(resultItem, DisplayNameOption.OPTIONAL);

        if (newName != null && !newName.equals(originalName)) {

            AnvilRenameItemEvent renameEvent = new AnvilRenameItemEvent(
                    p, anvilInventory, resultItem, newName, originalName);

            Nucleus.getEventManager().callBukkit(this, renameEvent);

            if (renameEvent.isCancelled()) {
                cancelAnvilEvent(p, anvilInventory, slot1Clone, slot2Clone);
                return;
            }

            ItemStackUtils.setDisplayName(resultItem, renameEvent.getNewName());
        }

        if (slot2 == null || slot2.getType() == Material.AIR)
            return;

        if (slot2.getType() == Material.ENCHANTED_BOOK) {

            AnvilEnchantItemEvent enchantEvent = new AnvilEnchantItemEvent(p, anvilInventory);
            Nucleus.getEventManager().callBukkit(this, enchantEvent);

            if (enchantEvent.isCancelled()) {
                cancelAnvilEvent(p, anvilInventory, slot1Clone, slot2Clone);
            }
        }
        else {

            AnvilRepairItemEvent repairEvent = new AnvilRepairItemEvent(p, anvilInventory, resultItem);
            Nucleus.getEventManager().callBukkit(this, repairEvent);

            if (repairEvent.isCancelled()) {
                cancelAnvilEvent(p, anvilInventory, slot1Clone, slot2Clone);
            }
        }
    }

    // detect cancelled thrown item and re-insert into inventory
    // so that it does not become invisible in the players inventory.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    private void onItemThrow(PlayerInteractEvent event) {

        if (!event.isCancelled())
            return;

        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
                event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack thrown = player.getItemInHand();
        Material material = thrown.getType();

        if (!Materials.isThrowable(material))
            return;

        // set same item in hand, causes packets to be sent to client
        // "reminding" it that there is still an item in the players hand.
        player.setItemInHand(thrown);
    }

    private void cancelAnvilEvent(final Player player, final Inventory inventory,
                                  final ItemStack original1, final ItemStack original2) {

        player.setItemOnCursor(null);

        Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {
            @Override
            public void run() {
                inventory.setItem(0, original1);
                inventory.setItem(1, original2);
                inventory.setItem(2, null);
                player.setItemOnCursor(null);
            }
        });
    }
}
