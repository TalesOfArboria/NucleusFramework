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

package com.jcwhatever.nucleus.utils.text.dynamic;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An {@code IDynamicText} implementation that can be used to
 * queue text to be displayed for specified amounts
 * of time in the order that they are queued.
 *
 * <p>Can also add {@code Runnable}'s to the queue as well as
 * insert pauses between queued text and {@code Runnable}'s.</p>
 */
public class QueuedText implements IDynamicText {

    private final LinkedList<Pause> _pauses = new LinkedList<>();
    private final LinkedList<Runnable> _onEmpty = new LinkedList<>();
    private volatile long _nextUpdate;
    private volatile IDynamicText _currentText;
    private final Object _sync = new Object();

    /**
     * Constructor.
     */
    public QueuedText() {}

    /**
     * Determine if there are text events queued.
     */
    public boolean isEmpty() {
        synchronized (_sync) {
            return _pauses.isEmpty();
        }
    }

    /**
     * Get the number of queued text events.
     */
    public int size() {
        synchronized (_sync) {
            return _pauses.size();
        }
    }

    /**
     * Get a builder used to add queued events to
     * the {@code QueuedText} instance.
     */
    public QueueTextBuilder getBuilder() {
        return new QueueTextBuilder();
    }

    @Override
    public String nextText() {

        if (_nextUpdate != 0 && _nextUpdate >= System.currentTimeMillis()) {
            return _currentText != null ? _currentText.nextText() : null;
        }

        Action runAction = null;

        synchronized (_sync) {

            if (isEmpty()) {

                while (!_onEmpty.isEmpty()) {
                    Runnable onEmpty = _onEmpty.removeFirst();

                    onEmpty.run();
                }
                return null;
            }

            Pause pause = _pauses.removeFirst();

            _nextUpdate = System.currentTimeMillis() + pause.duration;

            if (pause instanceof Text) {
                Text text = (Text) pause;
                return (_currentText = text.text).nextText();
            }

            if (pause instanceof Action) {
                runAction = (Action) pause;
            }
        }

        if (runAction != null) {
            runAction.runnable.run();
        }

        return null;
    }

    @Override
    public int getRefreshRate() {
        return isEmpty() ? 0 : 1;
    }

    @Override
    public String toString() {
        return nextText();
    }

    public class QueueTextBuilder {

        List<Pause> pauses = new ArrayList<>(10);
        List<Runnable> onEmpty = new ArrayList<>(3);

        /**
         * Add text to the queue.
         *
         * @param duration  The duration the text is displayed for.
         * @param text      The text to display.
         *
         * @return  Self for chaining.
         */
        public QueueTextBuilder text(int duration, CharSequence text) {
            PreCon.positiveNumber(duration);
            PreCon.notNull(text);

            pauses.add(new Text(duration, new DynamicTextBuilder().append(text).build()));

            return this;
        }

        /**
         * Add dynamic text to the queue.
         *
         * @param duration  The duration the text is displayed for.
         * @param text      The text to display.
         *
         * @return  Self for chaining.
         */
        public QueueTextBuilder text(int duration, IDynamicText text) {
            PreCon.positiveNumber(duration);
            PreCon.notNull(text);

            pauses.add(new Text(duration, text));

            return this;
        }

        /**
         * Add a {@code Runnable} instance to run.
         *
         * @param duration  The duration to pause before moving to the next queue item.
         * @param runnable  The runnable to run.
         *
         * @return  Self for chaining.
         */
        public QueueTextBuilder run(int duration, Runnable runnable) {
            PreCon.positiveNumber(duration);
            PreCon.notNull(runnable);

            pauses.add(new Action(duration, runnable));

            return this;
        }

        /**
         * Add a pause duration to the queue.
         *
         * @param duration  The duration of the pause.
         *
         * @return  Self for chaining.
         */
        public QueueTextBuilder pause(int duration) {
            PreCon.positiveNumber(duration);

            pauses.add(new Pause(duration));

            return this;
        }

        /**
         * Add a one time {@code Runnable} instance to run
         * when the queue is empty.
         *
         * @param runnable  The runnable.
         *
         * @return  Self for chaining.
         */
        public QueueTextBuilder onEmpty(Runnable runnable) {
            PreCon.notNull(runnable);

            onEmpty.add(runnable);

            return this;
        }

        /**
         * Build and append to the parent {@code QueueText} queue.
         */
        public void build() {

            synchronized (_sync) {
                _pauses.addAll(pauses);
                pauses.clear();

                _onEmpty.addAll(onEmpty);
                onEmpty.clear();
            }
        }
    }

    private static class Text extends Pause {
        final IDynamicText text;

        Text(int duration, IDynamicText text) {
            super(duration);
            this.text = text;
        }
    }

    private static class Action extends Pause {
        final Runnable runnable;

        Action(int duration, Runnable runnable) {
            super(duration);
            this.runnable = runnable;
        }
    }

    private static class Pause {
        final int duration; // milliseconds

        Pause (int duration) {
            this.duration = duration * TimeScale.TICKS.getTimeFactor();
        }
    }
}
