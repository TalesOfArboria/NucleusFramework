package com.jcwhatever.bukkit.generic.internal;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.language.LanguageManager;
import com.jcwhatever.bukkit.generic.language.Localized;
import org.bukkit.plugin.Plugin;

public class Lang {

    private Lang() {}

    private static LanguageManager _languageManager = new LanguageManager();


    @Localized
    public static String get(String text, Object... params) {
        return _languageManager.get(GenericsLib.getPlugin(), text, params);
    }

    public static String get(Plugin plugin, String text, Object... params) {
        return _languageManager.get(plugin, text, params);
    }
}
