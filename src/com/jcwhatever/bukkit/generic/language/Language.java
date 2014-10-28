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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Language {

    private final Set<String> _versions;
    private final Set<LocalizedText> _localizedText;
    private final Map<Integer, LocalizedText> _localizedTextMap;

    public Language() {
        _versions = new HashSet<>(5);
        _localizedText = new HashSet<LocalizedText>(50);
        _localizedTextMap = new HashMap<>(50);
    }

    public Language(InputStream keyStream) {

        LanguageParser parser = new LanguageParser(keyStream);

        try {
            parser.parseStream();

        } catch (InvalidLocalizedTextLineException e) {
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

    public int size() {
        return _localizedText.size();
    }

    public boolean isValidVersion(String version) {
        return _versions.contains(version);
    }

    public List<String> getVersions() {
        return new ArrayList<>(_versions);
    }

    public String getText(int index) {

        LocalizedText text = _localizedTextMap.get(index);
        if (text == null)
            return null;

        return text.getText();
    }

    public List<LocalizedText> getLocalizedText() {
        return new ArrayList<>(_localizedText);
    }

}
