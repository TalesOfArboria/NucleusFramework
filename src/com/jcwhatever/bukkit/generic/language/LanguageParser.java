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

import com.jcwhatever.bukkit.generic.utils.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class LanguageParser {

    private final InputStream _stream;
    private final Set<String> _versions = new HashSet<>(10);
    private final LinkedList<LocalizedText> _localizedText = new LinkedList<>();


    public LanguageParser(InputStream stream) {
        _stream = stream;
    }

    public Set<String> getVersions() {
        return _versions;
    }

    public LinkedList<LocalizedText> getLocalizedText() {
        return _localizedText;
    }

    public void parseStream() throws InvalidLocalizedTextLineException {

        Scanner scanner = new Scanner(_stream, "UTF-16");

        int lineNumber = 1;

        while (scanner.hasNextLine()) {
            parseLine(lineNumber, scanner.nextLine());
            lineNumber++;
        }

        try {
            _stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(int lineNumber, String line) throws InvalidLocalizedTextLineException {

        // version>  empty lines   comments   language index

        if (line.startsWith("version> ")) {
            parseVersion(line);
        }
        else if (!line.startsWith("#") && !line.trim().isEmpty()) {

            LocalizedText text = parseLocalizedLine(line);
            if (text == null) {
                throw new InvalidLocalizedTextLineException("Invalid entry on line " + lineNumber + '.');
            }

            _localizedText.add(text);
        }
    }


    private void parseVersion(String versionLine) {

        String rawVersion = versionLine.substring(9, versionLine.length());

        String[] versions = TextUtils.PATTERN_COMMA.split(rawVersion);

        for (String version : versions) {
            _versions.add(version.trim());
        }
    }


    // return null if its not a localized line
    private LocalizedText parseLocalizedLine(String line) {

        int angleIndex = line.indexOf("> ");

        String rawNumber = line.substring(0, angleIndex);
        System.out.println("DEBUG RAW NUMBER: " + rawNumber);

        int index = -1;

        try {
            index = Integer.parseInt(rawNumber);
        }
        catch (NumberFormatException e) {
            return null;
        }

        String text = line.substring(angleIndex + 2, line.length());
        System.out.println("DEBUG TEXT:" + text);

        return new LocalizedText(index, text);
    }

}
