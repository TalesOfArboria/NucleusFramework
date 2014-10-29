/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.internal.events;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.response.CommandRequests;
import com.jcwhatever.bukkit.generic.events.bukkit.AnvilItemRenameEvent;
import com.jcwhatever.bukkit.generic.events.bukkit.AnvilItemRepairEvent;
import com.jcwhatever.bukkit.generic.events.bukkit.SignInteractEvent;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper.DisplayNameResult;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.sounds.PlayList;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class JCGEventListener implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
	    
        if (CommandRequests.onResponse(event.getPlayer(), event.getMessage())) {
            Messenger.debug(null, "Response Processed");
            event.setCancelled(true);
        }
	}

	@EventHandler(priority=EventPriority.LOW)
	private void onPlayerJoin(PlayerJoinEvent event) {

		Player p = event.getPlayer();

		// update player name in id lookup
		PlayerHelper.setPlayerName(p.getUniqueId(), p.getName());

		// tell player missed important messages
		Messenger.tellImportant(p);
	}

	@EventHandler(priority=EventPriority.LOW)
	private void onPlayerMove(PlayerMoveEvent event) {
		GenericsLib.getRegionManager().updatePlayerLocation(event.getPlayer(), event.getTo());
	}

	@EventHandler(priority=EventPriority.NORMAL)
	private void onPlayerQuit(PlayerQuitEvent event) {
        PlayList.clearQueue(event.getPlayer());
	}

	@EventHandler(priority=EventPriority.NORMAL)
	private void onPlayerTeleport(PlayerTeleportEvent event) {

        // player teleporting to a different world
		if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {

            PlayList.clearQueue(event.getPlayer());
		}
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
			SignInteractEvent.callEvent(event, sign);
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


		if (inventory instanceof AnvilInventory) {
			AnvilInventory anvilInventory = (AnvilInventory)inventory;
			InventoryView view = event.getView();
			int rawSlot = event.getRawSlot();

			if (rawSlot == view.convertSlot(rawSlot) && rawSlot == 2) {

				ItemStack resultItem = anvilInventory.getItem(2);

				if (resultItem != null) {

					ItemStack slot1 = anvilInventory.getItem(0);
					
					// check for rename
					String originalName = slot1 != null
                            ? ItemStackHelper.getDisplayName(slot1, DisplayNameResult.OPTIONAL)
                            : null;

					String newName = ItemStackHelper.getDisplayName(resultItem, DisplayNameResult.OPTIONAL);
					
					if (newName != null && !newName.equals(originalName)) {
						
						AnvilItemRenameEvent renameEvent = AnvilItemRenameEvent.callEvent(
                                p, anvilInventory, resultItem, newName, originalName);
						
						if (renameEvent.isCancelled()) {
							event.setCancelled(true);
							return;
						}
					}
					
					
					// check for repair
					short startDurability = slot1 != null ? slot1.getDurability() : Short.MAX_VALUE;
					short resultDurability = resultItem.getDurability();
					
					if (resultDurability > startDurability) {
						AnvilItemRepairEvent repairEvent = AnvilItemRepairEvent.callEvent(p, anvilInventory, resultItem);
						if (repairEvent.isCancelled()) {
							event.setCancelled(true);
						}
					}
				}
				
			}

		}

	}

}
