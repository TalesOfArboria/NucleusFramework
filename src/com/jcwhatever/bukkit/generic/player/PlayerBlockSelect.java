package com.jcwhatever.bukkit.generic.player;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Utility to get a block selected by a player
 */
public class PlayerBlockSelect implements Listener {

    private PlayerBlockSelect() {}

	private static Map<UUID, PlayerBlockSelectHandler> _actions = new PlayerMap<PlayerBlockSelectHandler>();
    private static PlayerBlockSelect _listener;

	public static void query(Player p, PlayerBlockSelectHandler action) {
        registerListener();

		_actions.put(p.getUniqueId(), action);
	}

	private static boolean doSelect(Player p, Block selectedBlock, Action clickAction) {
		if (clickAction != Action.LEFT_CLICK_BLOCK && clickAction != Action.RIGHT_CLICK_BLOCK)
			return false;
		
		PlayerBlockSelectHandler action = _actions.remove(p.getUniqueId());
		if (action == null)
			return false;
		
		if (!action.onBlockSelect(p, selectedBlock, clickAction)) {
			_actions.put(p.getUniqueId(), action);
			return false;
		}
		
		return true;
	}

    private static void registerListener() {

        if (_listener == null) {
            _listener = new PlayerBlockSelect();

            Bukkit.getPluginManager().registerEvents(_listener, GenericsLib.getPlugin());
        }

    }

    @EventHandler(priority= EventPriority.HIGHEST)
    private void onPlayerSelectBlock(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;

        if (doSelect(event.getPlayer(), event.getClickedBlock(), event.getAction()))
            event.setCancelled(true);
    }


    public static abstract class PlayerBlockSelectHandler {
		public abstract boolean onBlockSelect(Player p, Block selectedBlock, Action clickAction);
	}


	
}

