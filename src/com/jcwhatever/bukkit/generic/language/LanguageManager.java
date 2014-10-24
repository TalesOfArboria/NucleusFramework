package com.jcwhatever.bukkit.generic.language;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.plugin.Plugin;

/**
 * Created by John on 10/13/2014.
 */
public class LanguageManager {

    /**
     * TODO
     *
     * Create jar parser to get all classes and use reflection to find
     * all Localizable annotated fields.
     *
     * After all literals are obtained, a file can be created and included with the jar.
     * The file acts as an index key that maps literals to an index location.
     *
     * External language files must implement literals in the same order as the key
     * file in the jar
     *
     * Text from the key file is used as a key in a hashmap and mapped to the
     * corresponding translation from another externally loaded file.
     *
     * Needs versioning info: Add localization version using plugin version to
     * top of key file and top of external language file. Key file can have multiple versions set.
     *
     *
     * ICommandInfo description should also be parsed and localized
     *
     */

    private static Language _defaultLang = Language.EN_US;

    @Localized
    public String get(Plugin plugin, String text, Object... params) {
        PreCon.notNull(plugin);
        PreCon.notNull(text);

        if (text.isEmpty())
            return text;

        text = get(_defaultLang, text, params);

        return TextUtils.formatPluginInfo(plugin, text);
    }

    @Localized
    public String get(Language displayLanguage, String text, Object... params) {
        return TextUtils.format(text, params);
    }

    public void mergeFrom(LanguageManager languageManager) {
        // TODO: implement, merge language indexes from child language manager
    }
}
