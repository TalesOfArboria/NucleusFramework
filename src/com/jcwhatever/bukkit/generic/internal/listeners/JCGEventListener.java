/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.internal.listeners;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.response.CommandRequests;
import com.jcwhatever.bukkit.generic.events.anvil.AnvilItemRenameEvent;
import com.jcwhatever.bukkit.generic.events.anvil.AnvilItemRepairEvent;
import com.jcwhatever.bukkit.generic.events.manager.GenericsEventManager;
import com.jcwhatever.bukkit.generic.events.signs.SignInteractEvent;
import com.jcwhatever.bukkit.generic.internal.InternalRegionManager;
import com.jcwhatever.bukkit.generic.regions.Region.LeaveRegionReason;
import com.jcwhatever.bukkit.generic.regions.Region.RegionReason;
import com.jcwhatever.bukkit.generic.sounds.PlayList;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils.DisplayNameResult;
import com.jcwhatever.bukkit.generic.utils.Scheduler;

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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
		GenericsEventManager.unregisterPlugin(event.getPlugin());

		if (GenericsLib.getPlugin().isEnabled()) {
			GenericsLib.getScriptApiRepo().unregisterPlugin(event.getPlugin());
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		if (CommandRequests.onResponse(event.getPlayer(), event.getMessage())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	private void onPlayerJoin(PlayerJoinEvent event) {

		final Player p = event.getPlayer();

		// tell player missed important messages
		GenericsLib.getMessengerFactory().tellImportant(p);

		Scheduler.runTaskLater(GenericsLib.getPlugin(), 5, new Runnable() {
			@Override
			public void run() {
				_regionManager
						.updatePlayerLocation(p, p.getLocation(), RegionReason.JOIN_SERVER);
			}
		});
	}

	@EventHandler(priority=EventPriority.LOW)
	private void onPlayerMove(PlayerMoveEvent event) {
		_regionManager
				.updatePlayerLocation(event.getPlayer(), event.getTo(), RegionReason.MOVE);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerDeath(PlayerDeathEvent event) {

		if (event.getEntity().getHealth() > 0)
			return;

		_regionManager
				.updatePlayerLocation(event.getEntity(), LeaveRegionReason.DEAD);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerRespawn(PlayerRespawnEvent event) {
		_regionManager
				.updatePlayerLocation(event.getPlayer(), event.getRespawnLocation(), RegionReason.RESPAWN);
	}

	@EventHandler(priority=EventPriority.LOWEST) // first priority
	private void onPlayerQuit(PlayerQuitEvent event) {
		PlayList.clearQueue(event.getPlayer());

		_regionManager
				.updatePlayerLocation(event.getPlayer(), LeaveRegionReason.QUIT_SERVER);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerTeleport(PlayerTeleportEvent event) {

		// player teleporting to a different world
		if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {

			PlayList.clearQueue(event.getPlayer());
		}

		_regionManager
				.updatePlayerLocation(event.getPlayer(), event.getTo(), RegionReason.TELEPORT);
	}

	@EventHandler(priority=EventPriority.NORMAL)
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
			GenericsLib.getEventManager().callBukkit(signInteractEvent);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	private void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled())
			return;

		HumanEntity entity = event.getWhoClicked();

		if (!(entity instanceof Player))
			return;

		Player p = (Player)entity;

		Inventory inventory = event.getInventory();
		if (!(inventory instanceof AnvilInventory))
			return;

		AnvilInventory anvilInventory = (AnvilInventory)inventory;
		InventoryView view = event.getView();
		int rawSlot = event.getRawSlot();

		if (rawSlot != view.convertSlot(rawSlot) || rawSlot != 2)
			return;

		ItemStack resultItem = anvilInventory.getItem(2);
		if (resultItem == null)
			return;

		ItemStack slot1 = anvilInventory.getItem(0);

		// check for rename
		String originalName = slot1 != null
				? ItemStackUtils.getDisplayName(slot1, DisplayNameResult.OPTIONAL)
				: null;

		String newName = ItemStackUtils.getDisplayName(resultItem, DisplayNameResult.OPTIONAL);

		if (newName != null && !newName.equals(originalName)) {

			AnvilItemRenameEvent renameEvent = new AnvilItemRenameEvent(
					p, anvilInventory, resultItem, newName, originalName);

			GenericsLib.getEventManager().callBukkit(renameEvent);

			if (renameEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			}

			ItemStackUtils.setDisplayName(resultItem, renameEvent.getNewName());
		}

		// check for repair
		short startDurability = slot1 != null ? slot1.getDurability() : Short.MAX_VALUE;
		short resultDurability = resultItem.getDurability();

		if (resultDurability > startDurability) {
			AnvilItemRepairEvent repairEvent = new AnvilItemRepairEvent(p, anvilInventory, resultItem);

			GenericsLib.getEventManager().callBukkit(repairEvent);

			if (repairEvent.isCancelled()) {
				event.setCancelled(true);
			}
		}
	}
}
