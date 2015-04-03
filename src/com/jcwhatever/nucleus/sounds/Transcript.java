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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateAgent;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.UpdateAgent;
import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
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
 *     <pre>Paragraph1 text{p:seconds}Paragraph2 text</pre>
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
 *     Paragraph tag example: <pre>{p:10}</pre>
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
     * Run the transcript and receive each paragraph at the appropriate time
     * via an {@link IUpdateSubscriber}.
     *
     * <p>After all paragraphs have been sent to the subscriber, one final update
     * is sent with a null value to indicate there are no more paragraphs.</p>
     *
     * @param plugin      The calling plugin.
     * @param subscriber  The subscriber.
     */
    public void run(Plugin plugin, final IUpdateSubscriber<String> subscriber) {

        if (_paragraphs.isEmpty())
            return;

        final PriorityQueue<Paragraph> paragraphs = new PriorityQueue<Paragraph>(_paragraphs);
        final IUpdateAgent<String> agent = new UpdateAgent<>(1);
        agent.register(subscriber);

        Paragraph first = paragraphs.peek();
        if (first.getStartTimeSeconds() == 0) {
            agent.update(first.getText());
            paragraphs.remove();
        }

        if (paragraphs.size() == 0) {
            agent.dispose();
            return;
        }

        final Paragraph firstScheduled = paragraphs.remove();
        final long firstUpdate = System.currentTimeMillis() + (firstScheduled.getStartTimeSeconds() * 1000);

        Scheduler.runTaskRepeat(plugin, 1, 1, new TaskHandler() {

            Paragraph next = firstScheduled;
            long nextUpdate = firstUpdate;
            int lastStartTime = firstScheduled.getStartTimeSeconds();

            @Override
            public void run() {

                if (subscriber.isDisposed() || agent.isDisposed()) {
                    cancelTask();
                    return;
                }

                if (nextUpdate > System.currentTimeMillis())
                    return;

                agent.update(next.getText());

                if (paragraphs.isEmpty()) {
                    cancelTask();
                    return;
                }

                next = paragraphs.remove();
                nextUpdate =  System.currentTimeMillis() + ((next.getStartTimeSeconds() - lastStartTime) * 1000);
                lastStartTime = next.getStartTimeSeconds();
            }

            @Override
            protected void onCancel() {

                if (!agent.isDisposed()) {
                    agent.update(null);
                    agent.dispose();
                }
            }
        });
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
            return Integer.compare(_startTimeSeconds, paragraph._startTimeSeconds);
        }
    }
}
