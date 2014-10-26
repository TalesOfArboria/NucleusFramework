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
