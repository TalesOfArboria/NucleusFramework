package com.jcwhatever.bukkit.generic.messaging;

import com.jcwhatever.bukkit.generic.GenericsPlugin;
import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.TimedSet;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Messenger {
    
    private Messenger() {}
    
	private static final Logger _log = Logger.getLogger("Minecraft");
	
    private static final int _maxLineLen = 60;
    private static IDataNode _importantData;
    private static Pattern returnPattern = Pattern.compile("\r");
    private static Map<UUID, TimedSet<String>> _noSpamCache = new PlayerMap<TimedSet<String>>();
    
    public enum LineWrapping {
        ENABLED,
        DISABLED
    }
    
    public static boolean tellNoSpam(CommandSender sender, Object message, Object...params) {
        
        return tellNoSpam(null, sender, false, 140, message, params);
    }
    
    public static boolean tellNoSpam(@Nullable Plugin plugin, CommandSender sender, Object message, Object...params) {
    
    	return tellNoSpam(plugin, sender, true, 140, message, params);
    }
    
    public static boolean tellNoSpam(@Nullable Plugin plugin, CommandSender sender, int ticks, Object message, Object...params) {
                
    	return tellNoSpam(plugin, sender, true, ticks, message, params);
    }
    
    public static boolean tellNoSpam(CommandSender sender, int ticks, Object message, Object...params) {
        
        return tellNoSpam(null, sender, false, ticks, message, params);
    }

    public static boolean tellNoSpam(@Nullable Plugin plugin, CommandSender sender, boolean cutLines, int ticks, Object message, Object...params) {
        PreCon.notNull(sender);
        PreCon.positiveNumber(ticks);
        PreCon.notNull(message);
        PreCon.notNull(params);
    	
    	if (!(sender instanceof Player)) {
    		return tell(plugin, sender, message, params);
    	}
    	
    	Player p = (Player)sender;
    	
    	String msg = TextUtils.format(message, params);
    	
    	TimedSet<String> recent = _noSpamCache.get(p.getUniqueId());
    	if (recent == null) {
    		recent = new TimedSet<String>(20, 140);
    		_noSpamCache.put(p.getUniqueId(), recent);
    	}
    	
    	if (recent.contains(msg, 140))
    		return false;
    	
    	recent.add(msg, ticks);
    	
    	return tell(cutLines, plugin, p, msg);
    }
    
    public static boolean tell(LineWrapping lineWrapping, Plugin plugin, CommandSender sender, Object message, Object...params) {
        PreCon.notNull(lineWrapping);
        PreCon.notNull(plugin);
        
        return tell(lineWrapping == LineWrapping.ENABLED, plugin, sender, TextUtils.format(message, params));
    }
    
    public static boolean tell(@Nullable Plugin plugin, CommandSender sender, Object message, Object...params) {
        
        return tell(true, plugin, sender, TextUtils.format(message, params));
    }
    
    public static boolean tell(CommandSender sender, Object message, Object...params) {
        
        return tell(false, null, sender, TextUtils.format(message, params));
    }
    
    private static boolean tell(boolean cutLines, @Nullable Plugin plugin, CommandSender sender, String message) {
        PreCon.notNull(sender);
        PreCon.notNull(message);

    	String chatPrefix = getChatPrefix(plugin);

    	cutLines = cutLines && plugin != null;

    	// if lines don't need to be cut, simply send the raw message
        if (!cutLines) {
        	sender.sendMessage(chatPrefix + message);
        	return true;
        }

        String[] lines = returnPattern.split(message);

        for (String line : lines) {

            line = chatPrefix + line;
            String testLine = ChatColor.stripColor(line);

            if (testLine.length() > _maxLineLen) {
                List<String> moreLines = TextUtils.paginateString(line, chatPrefix, _maxLineLen, true);
                for (String mLine : moreLines) {
                    sender.sendMessage(mLine);
                }
            } else {
                sender.sendMessage(line);
            }
        }
        return true;
    }
    
    public static void tellImportant(UUID playerId, String context, Object message, Object...params) {
        
        tellImportant(null, playerId, context, message, params);
    }
    
    public static void tellImportant(@Nullable Plugin plugin, UUID playerId, String context, Object message, Object...params) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(context);
        PreCon.notNull(message);
        PreCon.notNull(params);
        
        if (!TextUtils.isValidName(context, 64))
            throw new IllegalArgumentException("illegal characters in context argument or argument is too long.");
                
		Player p = PlayerHelper.getPlayer(playerId);
		if (p != null && p.isOnline()) {
			tell(plugin, p, message, params);
			return;
		}
		
		IDataNode data = getImportantData();
		
		data.set(playerId.toString() + '.' + context + ".message", TextUtils.format(message, params));
		data.set(playerId.toString() + '.' + context + ".plugin", plugin != null ? plugin.getName() : null);
		data.saveAsync(null);
	}
	
	public static void tellImportant(Player p) {
	    PreCon.notNull(p);
	    
		IDataNode data = getImportantData();
		
		IDataNode playerData = data.getNode(p.getUniqueId().toString());
		
		Set<String> contexts = playerData.getSubNodeNames();
		if (contexts == null)
			return;
		
		boolean save = false;
		
		for (String context : contexts) {
			IDataNode contextData = playerData.getNode(context);
			
			String pluginName = contextData.getString("plugin");
			if (pluginName == null)
				continue;
			
			Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
			if (plugin == null)
				continue;
			
			String message = contextData.getString("message", "");
			
			tell(plugin, p, message);
			
			contextData.clear();
			save = true;
		}
		
		if (save)
			data.saveAsync(null);
	}
    
    
    public static void broadcast(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);
                
        String formatted = TextUtils.format(message, params);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            tell(plugin, p, formatted);
        }
    }

    public static void broadcast(@Nullable Plugin plugin, Object message, Collection<Player> exclude, Object...params) {
        PreCon.notNull(message);
        PreCon.notNull(exclude);
        PreCon.notNull(params);
        
        String formatted = TextUtils.format(message, params);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            
            if (exclude.contains(p)) 
            	continue;
            
            tell(plugin, p, formatted);
        }
    }
    
    public static void info(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);
        
        _log.info(getConsolePrefix(plugin) + TextUtils.format(message, params));
    }

    public static void debug(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);

        if (plugin instanceof GenericsPlugin && !((GenericsPlugin) plugin).isDebugging())
            return;

    	ConsoleCommandSender e = Bukkit.getConsoleSender();

    	if (e != null) {
    		tell(false, plugin, e, ChatColor.GOLD + "[debug] " + TextUtils.format(message, params));
    	}
    	else {
    		info(plugin, "[debug] " + TextUtils.format(message, params));
    	}
    }

    public static void warning(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);
    	
        _log.warning(getConsolePrefix(plugin) + TextUtils.format(message, params));
    }

    public static void severe(@Nullable Plugin plugin, Object message, Object... params) {
        PreCon.notNull(message);
        PreCon.notNull(params);
        
        _log.severe(getConsolePrefix(plugin) + TextUtils.format(message, params));
    }
    
    
    private static String getChatPrefix(@Nullable Plugin plugin) {
    	if (plugin instanceof GenericsPlugin) {
    		return ((GenericsPlugin) plugin).getChatPrefix();
    	}
    	
    	if (plugin != null) {
    		return '[' + plugin.getName() + ']';
    	}
    	
    	return "";
    }
    
    private static String getConsolePrefix(@Nullable Plugin plugin) {
    	if (plugin instanceof GenericsPlugin) {
    		return ((GenericsPlugin) plugin).getConsolePrefix();
    	}
    	
    	if (plugin != null) {
    		return '[' + plugin.getName() + ']';
    	}
    	
    	return "";
    	
    }
    
    private static IDataNode getImportantData() {
    	if (_importantData == null) {

    		_importantData = DataStorage.getStorage(GenericsLib.getInstance(), new DataPath("important-messages"));
    		_importantData.loadAsync();
    	}
    	
    	return _importantData;
    }
}

