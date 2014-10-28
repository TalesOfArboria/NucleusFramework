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


package com.jcwhatever.bukkit.generic.language;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John on 10/13/2014.
 */
public class LanguageManager {

    /**
     * TODO
     *
     * ICommandInfo description should also be parsed and localized
     *
     *
     * Resource file named lang.txt
     *
     *
     *
     */

    private final Plugin _plugin;
    private Map<String, String> _localizationMap;
    private LanguageKeys _keys;

    public LanguageManager(Plugin plugin) {
        _plugin = plugin;
    }

    public void clear() {
        _localizationMap.clear();
    }

    public void reload() {
        _localizationMap.clear();

        loadInternalLanguage();
    }

    /**
     * Add a language file.
     *
     * @param file  The language file to include
     */
    public void addFile(File file) throws FileNotFoundException {
        PreCon.notNull(file);
        PreCon.isValid(file.isFile());

        FileInputStream stream = new FileInputStream(file);

        Language language = new Language(stream);

        mergeLanguage(language);
    }


    @Localized
    public String get(String text, Object... params) {
        PreCon.notNull(text);

        if (text.isEmpty())
            return text;

        if (_keys == null) {
            return format(text, params);
        }

        String localizedText = _localizationMap.get(text);
        if (localizedText == null) {
            return format(text, params);
        }

        return format(localizedText);
    }

    private String format(String text, Object... params) {

        text = TextUtils.format(text, params);

        return TextUtils.formatPluginInfo(_plugin, text);
    }


    private void loadInternalLanguage() {

        InputStream langStream = getClass().getResourceAsStream("/res/lang.txt");
        if (langStream == null)
            return;

        Language language = new Language(langStream);

        mergeLanguage(language);

    }

    @Nullable
    private LanguageParser parseLanguage(InputStream stream) {

        LanguageParser langParser = new LanguageParser(stream);

        try {
            langParser.parseStream();

            return langParser;
        }
        catch (InvalidLocalizedTextLineException e) {
            e.printStackTrace();
            return null;
        }
    }


    private LanguageKeys getLanguageKeys() {

        if (_keys != null)
            return _keys;

        InputStream langStream = getClass().getResourceAsStream("/res/lang.keys.txt");
        if (langStream == null)
            return null;

        return _keys = new LanguageKeys(langStream);
    }


    private void mergeLanguage(Language language) {

        LanguageKeys keys = getLanguageKeys();
        if (keys == null)
            return;

        _localizationMap = new HashMap<>(keys.size());

        List<LocalizedText> localized = language.getLocalizedText();

        for (LocalizedText text : localized) {

            String key = keys.getText(text.getIndex());
            if (key == null) {
                Messenger.warning(_plugin, "Failed to find localization key indexed {0}.", text.getIndex());
                continue;
            }

            _localizationMap.put(key, text.getText());
        }
    }

}
