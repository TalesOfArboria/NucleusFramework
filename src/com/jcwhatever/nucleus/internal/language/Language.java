/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus.internal.language;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Parsed language localization data.
 */
class Language {

    private final Set<String> _versions;
    private final Set<LocalizedText> _localizedText;
    private final Map<Integer, LocalizedText> _localizedTextMap;

    /**
     * Constructor.
     *
     * @param languageStream  The language file stream.
     */
    public Language(InputStream languageStream) {

        LanguageParser parser = new LanguageParser(languageStream);

        try {
            parser.parseStream();

        } catch (InvalidLocalizedTextException e) {
            e.printStackTrace();
        }
        finally {
            _versions = parser.getVersions();

            int total = parser.getLocalizedText().size();

            _localizedText = new HashSet<>(total);
            _localizedTextMap = new HashMap<>(total);

            while (!parser.getLocalizedText().isEmpty()) {

                LocalizedText text = parser.getLocalizedText().removeFirst();

                _localizedText.add(text);
                _localizedTextMap.put(text.getIndex(), text);
            }
        }
    }

    /**
     * Get the number of localized strings in the language.
     */
    public int size() {
        return _localizedText.size();
    }

    /**
     * Determine if the language is of the specified version.
     *
     * @param version  The version to check.
     */
    public boolean isValidVersion(String version) {
        return _versions.contains(version);
    }

    /**
     * Get all versions specified by the language.
     */
    public List<String> getVersions() {
        return new ArrayList<>(_versions);
    }

    /**
     * Get the localized text from the language corresponding
     * to the specified key index.
     *
     * @param index  The key index.
     *
     * @return  Null if there is no entry.
     */
    @Nullable
    public String getText(int index) {

        LocalizedText text = _localizedTextMap.get(index);
        if (text == null)
            return null;

        return text.getText();
    }

    /**
     * Get all localized text in the language.
     */
    public List<LocalizedText> getLocalizedText() {
        return new ArrayList<>(_localizedText);
    }

}
