package com.jcwhatever.bukkit.generic.percentbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcwhatever.bukkit.generic.GenericsLib;
import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.jcwhatever.bukkit.generic.player.collections.PlayerSet;

public class Bar {
	
	private Set<Player> _players = new PlayerSet();
	private String _message;
	private float _percent;
	
	Bar (String message, float initialPercent) {
		_message = message;
		_percent = initialPercent;
	}
	
	Bar (Player p, String message, float initialPercent) {
		_message = message;
		_percent = initialPercent;
		add(p);
	}

	Bar (Collection<Player> players, String message, float initialPercent) {
		_message = message;
		_percent = initialPercent;
		add(players);
	}
	
	public List<Player> getPlayers() {
		return new ArrayList<Player>(_players);
	}

	public String getMessage() {
		return _message;
	}

	public float getPercent() {
		return _percent;
	}

	public void setPercent(float percent) {
		
		if (percent == _percent)
			return;
		
		_percent = percent;
		
		if (!BarManager.hasBarAPI())
			return;
		
		for (Player p : _players) {
			BarAPI.setHealth(p, percent);
		}
	}
	
	public void updatePlayers(List<Player> players) {
		Set<Player> toRemove = new HashSet<Player>();
		
		// get players to remove
		for (Player p : _players) {
			if (!players.contains(p))
				toRemove.add(p);
		}
		
		// remove players not in update
		for (Player p : toRemove) {
			remove(p);
		}

		// add new players
		for (Player p : players) {
			if (!_players.contains(p))
				add(p);
		}
	}

	public void add(final Player p) {
		if (_players.add(p)) {
			Bar current = BarManager._bars.get(p.getUniqueId());
			if (current != null) {
				current.remove(p);
			}
			
			BarManager._bars.put(p.getUniqueId(), this);
			
			if (!BarManager.hasBarAPI())
				return;
			
			BarAPI.removeBar(p);
			
			Bukkit.getScheduler().runTaskLater(GenericsLib.getInstance(), new Runnable() {

				@Override
				public void run() {
					BarAPI.setMessage(p, _message, _percent);
				}
				
			}, 5);
		}
	}
	
	public void add(Collection<Player> players) {
		for (Player p : players)
			add(p);
	}
	
	public void remove() {
		remove(getPlayers());
	}
	
	public void remove(Player p) {
		
		_players.remove(p);
		BarManager._bars.remove(p.getUniqueId());
		
		if (!BarManager.hasBarAPI())
			return;

		BarAPI.removeBar(p);
		
	}
	
	public void remove(Collection<Player> players) {
		for (Player p : players)
			remove(p);
	}

	
}
