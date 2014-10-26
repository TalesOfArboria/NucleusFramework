package com.jcwhatever.bukkit.generic.player.collections;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.UUID;

final class PlayerCollectionListener implements Listener {

    // keyed to player id, a map of collections a player is contained in
    private static MultiValueMap<UUID, IPlayerCollection> _collectionMap = new MultiValueMap<>(100, 25);

    // singleton collection listener
	private static PlayerCollectionListener _listener;

    // synchronization object
	private static final Object _sync = new Object();

    /**
     * Get the singleton instance of the player collection listener
     * @return
     */
	static PlayerCollectionListener get() {
        if (_listener == null) {
            synchronized (_sync) {
                if (_listener == null) {
                    _listener = new PlayerCollectionListener();

                    PluginManager pm = GenericsLib.getPlugin().getServer().getPluginManager();
                    pm.registerEvents(_listener, GenericsLib.getPlugin());
                }
            }
        }

		return _listener;
	}

	private PlayerCollectionListener() {}

    /**
     * Register a collection as containing the specified player.
     *
     * @param p           The player.
     * @param collection  The collection.
     */
    public void addCollection(Player p, IPlayerCollection collection) {
        addCollection(p.getUniqueId(), collection);
    }

    /**
     * Register a collection as containing the specified player.
     *
     * @param playerId    The player Id.
     * @param collection  The collection.
     */
    public void addCollection(UUID playerId, IPlayerCollection collection) {
        synchronized (_sync) {
            _collectionMap.put(playerId, collection);
        }
    }

    /**
     * Unregister a collection as containing the specified player.
     *
     * @param p           The player.
     * @param collection  The collection.
     */
    public void removeCollection(Player p, IPlayerCollection collection) {
        removePlayer(p.getUniqueId(), collection);
    }

    /**
     * Unregister a collection as containing the specified player.
     *
     * @param playerId    The id of the player.
     * @param collection  The collection.
     */
    public void removePlayer(UUID playerId, IPlayerCollection collection) {
        synchronized (_sync) {
            _collectionMap.removeValue(playerId, collection);
        }
    }

    // remove a player from all collections
	private void removePlayer(Player p) {
        List<IPlayerCollection> collections = _collectionMap.getValues(p.getUniqueId());
        if (collections == null || collections.isEmpty())
            return;

        Scheduler.runTaskLaterAsync(GenericsLib.getPlugin(), 1, new RemovePlayer(p, collections));
	}

    // event handler, Remove player from all collections when logged out
    @EventHandler(priority=EventPriority.NORMAL)
    private void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    // event handler, Remove player from all collections when kicked
    @EventHandler(priority=EventPriority.NORMAL)
    private void onPlayerQuit(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }

    // Asynchronous removal of player from collections
	static class RemovePlayer implements Runnable {
		private Player p;
        private List<IPlayerCollection> collections;

		public RemovePlayer(Player p, List<IPlayerCollection> collections) {
			this.p = p;
            this.collections = collections;
		}

		@Override
		public void run() {
			synchronized (_sync) {
				for (IPlayerCollection collection : collections) {
					collection.removePlayer(p);
				}				
			}
		}

	}



}
