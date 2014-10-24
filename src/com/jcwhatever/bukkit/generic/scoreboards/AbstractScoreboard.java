package com.jcwhatever.bukkit.generic.scoreboards;

import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public abstract class AbstractScoreboard implements IScoreboard {

    private static Map<UUID, Stack<IScoreboard>> _stackMap = new PlayerMap<Stack<IScoreboard>>();
    
	private boolean _isInitialized = false;
		
	protected Plugin _plugin;
	protected IScoreboardInfo _typeInfo;
	protected Scoreboard _scoreboard;
	protected Map<String, ScoreItem> _scoreItems = new HashMap<String, ScoreItem>();
	
	@Override
	public void init(Plugin plugin, IScoreboardInfo typeInfo) {
	    PreCon.notNull(plugin);
		PreCon.notNull(typeInfo);
				
		if (_isInitialized) {
			throw new IllegalStateException("Cannot initialize scoreboard because it's already initialized.");
		}

		_isInitialized = true;
		_plugin = plugin;
		_typeInfo = typeInfo;
		_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		onInit();
	}
	
	@Override
	public final String getType() {
		return _typeInfo.name();
	}
	
	@Override
	public final Scoreboard getScoreboard() {
		return _scoreboard;
	}
	
	@Override
	public final void apply(Player p) {
	    
	    Stack<IScoreboard> stack = _stackMap.get(p.getUniqueId());
	    if (stack == null) {
	        stack = new Stack<IScoreboard>();
	        _stackMap.put(p.getUniqueId(), stack);
	    }
	    
	    p.setScoreboard(_scoreboard);
	    
	    if (!stack.isEmpty()) {
	        IScoreboard current = stack.peek();
	        if (current == this) {
	            return;
	        }
	    }
	    
	    stack.push(this);
	    
	    onApply(p);
	}
	
	@Override
	public final void cease(Player p) {
	    Stack<IScoreboard> stack = _stackMap.get(p.getUniqueId());
	    IScoreboard next = null;
	    
	    if (stack != null && !stack.isEmpty()) {
	        if (stack.peek() == this) {
	            stack.pop();
	        }
	        else {     
	            stack.remove(this);
	        }
	    }
	    
	    if (stack != null && !stack.isEmpty()) {
	        next = stack.peek();
	    }
	    
	    if (next != null) {
	        p.setScoreboard(next.getScoreboard());
	    }
	    else {
	        p.setScoreboard(null);
	    }
	    	    
	    onCease(p);
	}
	
	@Override
	public void dispose() {
	    Set<Objective> objectives = _scoreboard.getObjectives();
	    for (Objective objective : objectives) {
	        objective.unregister();
	    }
	}

	protected final void addScoreItem(ScoreItem scoreItem) {
		_scoreItems.put(scoreItem.getKey(), scoreItem);
		initScore(scoreItem);
	}

	protected final ScoreItem getScoreItem(String key) {
		return _scoreItems.get(key);
	}
	
	protected abstract void onInit();
	protected abstract String getDisplayHeader();
	protected abstract void onInitScore(ScoreItem scoreItem, Score score);

	protected abstract void onApply(Player p);
    protected abstract void onCease(Player p);
	
	protected final class ScoreItem {

		private OfflinePlayer _scoreItem;
		private String _key;
		private int _initScore;
		private String _objectiveName;
		
		public ScoreItem(String key, String objectiveName, Player p, int initVal) {
			_scoreItem = p;
			_initScore = initVal;
			_objectiveName = objectiveName;
			_key = key;
		}

		public ScoreItem(String key, String objectiveName, String text, int initVal) {
			_scoreItem = Bukkit.getOfflinePlayer(text);
			_initScore = initVal;
			_objectiveName = objectiveName;
			_key = key;
		}

		public String getKey() {
			return _key;
		}
		
		public String getObjectiveName() {
		    return _objectiveName;
		}

		public OfflinePlayer getOfflinePlayer() {
			return _scoreItem;
		}

		public int getInitScore() {
			return _initScore;
		}

		public int getScore() {
		    Objective objective = _scoreboard.getObjective(_objectiveName);
		    if (objective == null)
		        return 0;
		    
			Score score = objective.getScore(_scoreItem);
			return score.getScore();
		}

		public void setScore(int value) {
		    Objective objective = _scoreboard.getObjective(_objectiveName);
		    if (objective == null)
		        return;
		    
			Score score = objective.getScore(_scoreItem);
			score.setScore(value);
		}

		public int incrementScore() {
			return incrementScore(1);
		}

		public int incrementScore(int amount) {
		    Objective objective = _scoreboard.getObjective(_objectiveName);
		    if (objective == null)
		        return 0;
		    
			Score score = objective.getScore(_scoreItem);
			int scoreCount = score.getScore() + amount;
			score.setScore(scoreCount);
			return scoreCount;
		}

	}


	private void initScore(final ScoreItem scoreItem) {
	    Objective objective = _scoreboard.getObjective(scoreItem.getObjectiveName());
	    if (objective == null) {
	        objective = _scoreboard.registerNewObjective(scoreItem.getObjectiveName(), "dummy");
	    }
	    
		final Score score = objective.getScore(scoreItem.getOfflinePlayer());
		
		Bukkit.getScheduler().runTaskLater(_plugin, new Runnable() {

            @Override
            public void run () {
                score.setScore(1);
                onInitScore(scoreItem, score);
            }
		    
		}, 1);
	}
	
	
	
	

}
