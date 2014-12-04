/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import javax.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

/*
 * Parse GenericsLib language file from stream.
 */
public class LanguageParser {

    private final InputStream _stream;
    private final Set<String> _versions = new HashSet<>(10);
    private final LinkedList<LocalizedText> _localizedText = new LinkedList<>();

    /**
     * Constructor.
     *
     * @param stream  The stream to parse.
     */
    public LanguageParser(InputStream stream) {
        _stream = stream;
    }

    /**
     * Get valid language versions parsed from stream.
     */
    public Set<String> getVersions() {
        return _versions;
    }

    /**
     * Get all parsed localized text.
     */
    public LinkedList<LocalizedText> getLocalizedText() {
        return _localizedText;
    }

    /**
     * Begin parsing the stream provided in the constructor.
     *
     * @throws InvalidLocalizedTextException  If the stream contains invalid lines.
     */
    public void parseStream() throws InvalidLocalizedTextException {

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

    /*
     *  Parse a single line from the stream.
     */
    private void parseLine(int lineNumber, String line) throws InvalidLocalizedTextException {

        // check for version
        if (line.startsWith("version> ")) {
            parseVersion(line);
        }
        // excluding comments and empty lines, parse localized text.
        else if (!line.startsWith("#") && !line.trim().isEmpty()) {

            LocalizedText text = parseLocalizedLine(line);
            if (text == null) {
                throw new InvalidLocalizedTextException("Invalid entry on line " + lineNumber + '.');
            }

            _localizedText.add(text);
        }
    }

    /*
     * Parse the language version from a line.
     */
    private void parseVersion(String versionLine) {

        String rawVersion = versionLine.substring(9, versionLine.length());

        String[] versions = TextUtils.PATTERN_COMMA.split(rawVersion);

        for (String version : versions) {
            _versions.add(version.trim());
        }
    }

    /*
     * Parse a localized line. Returns null if the line is invalid.
     */
    @Nullable
    private LocalizedText parseLocalizedLine(String line) {

        int angleIndex = line.indexOf("> ");

        String rawNumber = line.substring(0, angleIndex);

        int index;

        try {
            index = Integer.parseInt(rawNumber);
        }
        catch (NumberFormatException e) {
            return null;
        }

        String text = line.substring(angleIndex + 2, line.length());
        return new LocalizedText(index, text);
    }

}
