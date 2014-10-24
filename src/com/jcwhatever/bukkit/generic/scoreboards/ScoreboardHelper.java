package com.jcwhatever.bukkit.generic.scoreboards;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.plugin.Plugin;


public class ScoreboardHelper {
    
    
    public static <T extends IScoreboard> T instantiate(Plugin plugin, Class<T> scoreboardClass) {
        PreCon.notNull(scoreboardClass);
        
        IScoreboardInfo typeInfo = scoreboardClass.getAnnotation(IScoreboardInfo.class);
        if (typeInfo == null)
            throw new IllegalStateException("Scoreboard class is missing its ITypeInfo annotation.");
                
        T instance = null;

        try {
            instance = scoreboardClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (instance == null) {
            return null;
        }
                
        instance.init(plugin, typeInfo);

        return instance;

    }

}
