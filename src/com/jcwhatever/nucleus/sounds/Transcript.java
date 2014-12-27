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


package com.jcwhatever.nucleus.sounds;

import com.jcwhatever.nucleus.messaging.IMessenger;
import com.jcwhatever.nucleus.messaging.IMessenger.LineWrapping;
import com.jcwhatever.nucleus.messaging.MessengerFactory;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Represents a voice transcript for a resource sound
 * which can be played to players.
 *
 * <p>
 *     Transcript format: <br />
 *     Paragraph1 text{p:seconds}Paragraph2 text
 * </p>
 * <p>
 *     The transcript is divided into paragraphs. Each paragraph is displayed to
 *     a user after a number of seconds has elapsed after the time the transcript is called
 *     to be displayed. Paragraphs are divided by using the letter "p" followed by a colon
 *     and the integer number of seconds, all wrapped in curly braces.
 * </p>
 * <p>
 *     The initial paragraph by default displays immediately, however, a paragraph tag can
 *     be added at the beginning to delay the first paragraph.
 * </p>
 * <p>
 *     Paragraph tag example: {p:10}
 * </p>
 */
public class Transcript {

    private static Pattern _splitPattern = Pattern.compile("\\{p:[0-9]+}");

    private List<Paragraph> _paragraphs = new ArrayList<Paragraph>(10);

    private String _rawTranscript;

    /**
     * Constructor.
     *
     * @param text  The transcript text.
     */
    public Transcript (String text) {
        PreCon.notNull(text);

        _rawTranscript = text;

        if (text.trim().isEmpty())
            return;

        Matcher matcher = _splitPattern.matcher(text);

        int lastEnd = 0;
        String lastGroup = null;

        while (matcher.find()) {
            String parText = text.substring(lastEnd, matcher.start());

            if (!parText.isEmpty())
                _paragraphs.add(new Paragraph(parText, lastGroup));

            lastGroup = matcher.group();
            lastEnd = matcher.end();
        }

        if (lastGroup == null) {
            _paragraphs.add(new Paragraph(text, null));
        }
        else {
            String parText = text.substring(lastEnd, text.length());
            if (!parText.isEmpty())
                _paragraphs.add(new Paragraph(parText, lastGroup));
        }
    }

    /**
     * Get the raw transcript.
     */
    public String getRawTranscript() {
        return _rawTranscript;
    }

    /**
     * Get the parsed paragraphs of the transcript.
     */
    public List<Paragraph> getParagraphs() {
        return new ArrayList<Paragraph>(_paragraphs);
    }

    /**
     * Tell a collection of players the transcript text.
     *
     * @param plugin   The plugin the text is from.
     * @param players  The players to display to.
     */
    public void tell(final Plugin plugin, Collection<Player> players) {
        tell(plugin, players, -1);
    }

    /**
     * Tell a collection of players the transcript text.
     * @param plugin          The plugin the text is from.
     * @param players         The players to display to.
     * @param maxTimeSeconds  The maximum time in seconds the transcript can run.
     */
    public void tell(final Plugin plugin, Collection<Player> players, int maxTimeSeconds) {

        final PriorityQueue<Paragraph> paragraphs = new PriorityQueue<Paragraph>(_paragraphs);
        final IMessenger msg = MessengerFactory.get(plugin);

        while (!paragraphs.isEmpty()) {
            final Paragraph paragraph = paragraphs.remove();

            for (final Player p : players) {

                if (maxTimeSeconds > -1 && paragraph.getStartTimeSeconds() > maxTimeSeconds)
                    continue;

                if (paragraph.getStartTimeSeconds() == 0) {

                    msg.tell(p, LineWrapping.DISABLED, paragraph.getText());
                } else {

                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

                        @Override
                        public void run() {
                            msg.tell(p, LineWrapping.DISABLED, paragraph.getText());
                        }

                    }, paragraph.getStartTimeSeconds() * 20);
                }
            }
        }
    }

    /**
     * Represents a timed paragraph of text parsed from a transcript.
     */
    public static class Paragraph implements Comparable<Paragraph> {
        private int _startTimeSeconds;
        private String _text;

        /**
         * Constructor.
         *
         * @param text       The paragraph text.
         * @param timeStamp  The raw timestamp for the paragraph.
         */
        Paragraph (String text, @Nullable String timeStamp) {
            _text = text.trim();

            if (timeStamp != null) {
                String numbers = timeStamp.replace("{p:", "").replace("}", "");

                try {
                    _startTimeSeconds = Integer.parseInt(numbers);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Get the time in seconds waited before the
         * paragraph is displayed to players.
         */
        public int getStartTimeSeconds() {
            return _startTimeSeconds;
        }

        /**
         * Get the text of the paragraph.
         */
        public String getText() {
            return _text;
        }

        /**
         * Allow sorting paragraphs by start time.
         *
         * @param paragraph  The paragraph to compare with.
         */
        @Override
        public int compareTo(Paragraph paragraph) {
            return Integer.compare(paragraph._startTimeSeconds, _startTimeSeconds);
        }
    }
}
