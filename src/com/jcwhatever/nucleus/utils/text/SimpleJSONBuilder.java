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

package com.jcwhatever.nucleus.utils.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A simple Minecraft based JSON builder.
 */
public class SimpleJSONBuilder {

    private int _estimatedSize = 2;
    private Map<String, String> _textMap = new HashMap<>(3);
    private Map<String, List<String>> _multiTextMap = new HashMap<>(3);
    private Map<String, Boolean> _boolMap = new HashMap<>(3);

    /**
     * Insert a boolean property.
     *
     * @param propertyName  The property name.
     * @param value         The property value.
     *
     * @return  Self for chaining.
     */
    public SimpleJSONBuilder bool(String propertyName, boolean value) {

        _boolMap.put(propertyName, value);

        _estimatedSize += propertyName.length() + 20;
        return this;
    }

    /**
     * Insert a text based property.
     *
     * @param propertyName  The property name.
     * @param value         The text value.
     * @param args          Optional format arguments.
     *
     * @return  Self for chaining.
     */
    public SimpleJSONBuilder text(String propertyName, String value, Object... args) {

        String text = TextUtils.format(value, args);

        _textMap.put(propertyName, text);

        _estimatedSize += propertyName.length() + text.length() + 20;

        return this;
    }

    /**
     * Insert a list of text.
     *
     * <p>List is converted to a JSON array of text.</p>
     *
     * @param propertyName  The property name.
     * @param values        The text list.
     *
     * @return  Self for chaining.
     */
    public SimpleJSONBuilder text(String propertyName, List<String> values) {

        _multiTextMap.put(propertyName, values);

        _estimatedSize += propertyName.length() + (values.size() * 15) + 20;
        return this;
    }

    /**
     * Build the JSON text.
     *
     * @return  The JSON text.
     */
    public String build() {

        StringBuilder buffer = new StringBuilder(_estimatedSize);
        boolean hasProperties = false;

        buffer.append('{');

        // boolean entries
        for (Entry<String, Boolean> boolEntry : _boolMap.entrySet()) {

            if (hasProperties)
                buffer.append(',');

            hasProperties = true;

            String propertyName = boolEntry.getKey();
            Boolean value = boolEntry.getValue();

            buffer.append(propertyName);
            buffer.append(':');
            buffer.append(value ? "true" : "false");
        }

        // text entries
        for (Entry<String, String> textEntry : _textMap.entrySet()) {

            if (hasProperties)
                buffer.append(',');

            hasProperties = true;

            String propertyName = textEntry.getKey();
            String text = textEntry.getValue();

            TextComponents components = new TextComponents(text);

            buffer.append(propertyName);
            buffer.append(":\"");
            getJsonText(buffer, components, true);
            buffer.append('\"');
        }

        // multi text entries
        for (Entry<String, List<String>> textEntry : _multiTextMap.entrySet()) {

            if (hasProperties)
                buffer.append(',');

            hasProperties = true;

            String propertyName = textEntry.getKey();
            List<String> textList = textEntry.getValue();

            buffer.append(propertyName);
            buffer.append(":[");



            for (int i=0; i < textList.size(); i++) {
                String rawText = textList.get(i);
                String text = TextUtils.format(rawText);
                TextComponents components = new TextComponents(text);

                if (i != 0)
                    buffer.append(',');

                buffer.append('"');
                getJsonText(buffer, components, true);
                buffer.append('"');
            }

            buffer.append(']');
        }

        buffer.append('}');

        return buffer.toString();
    }

    /**
     * Create a standalone text JSON string.
     *
     * @param textTemplate  The text or text template.
     * @param args          Optional format arguments.
     *
     * @return  The text JSON string.
     */
    public static String text(Object textTemplate, Object... args) {

        StringBuilder buffer = new StringBuilder(textTemplate.toString().length() + 30);

        text(buffer, textTemplate, args);

        return buffer.toString();
    }

    /**
     * Create a standalone text JSON string and append to the
     * supplied {@code StringBuilder}.
     *
     * @param buffer        The {@code StringBuilder} to append the output to.
     * @param textTemplate  The text or text template.
     * @param args          Optional format arguments.
     */
    public static void text(StringBuilder buffer, Object textTemplate, Object... args) {

        String rawText = TextUtils.format(textTemplate, args);

        TextComponents components = new TextComponents(rawText);

        getJsonText(buffer, components, false);
    }

    /**
     * Create a standalone text JSON string and append to the
     * supplied {@code StringBuilder}.
     *
     * @param buffer      The {@code StringBuilder} to append the output to.
     * @param components  The text components.
     */
    public static void text(StringBuilder buffer, TextComponents components) {
        getJsonText(buffer, components, false);
    }

    // generate standalone JSON text.
    private static void getJsonText(StringBuilder buffer, TextComponents segments, boolean escape) {

        buffer.append('{');
        getJsonTextSegment(buffer, segments.get(0), escape);

        if (segments.size() > 1) {
            buffer.append(",extra:[");

            for (int i=1; i < segments.size(); i++) {
                buffer.append('{');
                getJsonTextSegment(buffer, segments.get(i), escape);
                buffer.append('}');

                if (i < segments.size() - 1) {
                    buffer.append(',');
                }
            }

            buffer.append(']');
        }

        buffer.append('}');
    }

    // generate a text property segment.
    private static void getJsonTextSegment(StringBuilder buffer, TextComponent segment, boolean escape) {
        buffer.append("text:");

        if (escape)
            buffer.append("\\\"");
        else
            buffer.append('"');

        buffer.append(segment.getText());
        if (escape)
            buffer.append("\\\"");
        else
            buffer.append('"');

        if (segment.getTextColor() != null) {

            buffer.append(",color:");
            buffer.append(segment.getTextColor().name().toLowerCase());
        }
    }

}
