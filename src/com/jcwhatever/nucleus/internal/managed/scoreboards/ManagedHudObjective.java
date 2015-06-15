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

package com.jcwhatever.nucleus.internal.managed.scoreboards;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.scoreboards.IHudObjective;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboard;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.dynamic.DynamicTextBuilder;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * Managed HUD objective implementation.
 */
public class ManagedHudObjective extends AbstractObjective implements IHudObjective {

    private static final Map<ManagedHudObjective, Void> _instances = new WeakHashMap<>(10);
    private static final IDynamicText BLANK_TEXT = new DynamicTextBuilder().append("").build();
    private static Updater _updater;

    private Objective _objective;
    private List<Line> _lines = new ArrayList<>(16);

    StringBuilder _textBuffer = new StringBuilder(36);
    TextColor[] _prefixes = new TextColor[] {
            TextColor.LIGHT_PURPLE,
            TextColor.DARK_PURPLE,
            TextColor.AQUA,
            TextColor.BLACK,
            TextColor.BLUE,
            TextColor.DARK_AQUA,
            TextColor.DARK_BLUE,
            TextColor.DARK_GRAY,
            TextColor.DARK_GREEN,
            TextColor.DARK_RED,
            TextColor.GOLD,
            TextColor.GRAY,
            TextColor.GREEN,
            TextColor.RED,
            TextColor.WHITE,
            TextColor.YELLOW
    };

    ManagedHudObjective(IScoreboard scoreboard, Objective objective) {
        super(scoreboard, objective);

        _objective = objective;

        if (_updater == null) {
            _updater = new Updater();
            Scheduler.runTaskRepeat(Nucleus.getPlugin(), 3, 3, _updater);
        }

        _instances.put(this, null);
    }

    @Override
    public int size() {
        return _lines.size();
    }

    @Override
    public ManagedHudObjective set(int lineIndex, String text, Object... args) {
        return set(lineIndex, text.isEmpty()
                ? BLANK_TEXT
                : new DynamicTextBuilder().append(text, args).build());
    }

    // included for script compatibility
    public ManagedHudObjective set(int lineIndex, String text) {
        return set(lineIndex, text.isEmpty()
                ? BLANK_TEXT
                : new DynamicTextBuilder().append(text).build());
    }

    @Override
    public ManagedHudObjective set(int lineIndex, IDynamicText text) {

        fillLinesTo(lineIndex);

        Line line = _lines.get(lineIndex);
        line.text = text;

        line.update();

        return this;
    }

    @Nullable
    @Override
    public IDynamicText get(int lineIndex) {

        if (lineIndex >= _lines.size())
            return null;

        Line line = _lines.get(lineIndex);
        return line.text;
    }

    @Override
    public ManagedHudObjective clear(int lineIndex) {

        Line line = _lines.get(lineIndex);
        line.nextUpdate = 0;
        line.text = BLANK_TEXT;

        return this;
    }

    @Override
    public ManagedHudObjective remove(int lineIndex) {

        Line line = _lines.remove(lineIndex);
        line.nextUpdate = 0;

        line.score.getScoreboard().resetScores(line.score.getEntry());

        for (int i = lineIndex; i < _lines.size(); i++) {
            line = _lines.get(i);
            line.nextUpdate = 0;
            line.previousText = null;
        }

        return this;
    }

    @Override
    public ManagedHudObjective clearAll() {

        for (int i=0; i < _lines.size(); i++) {
            clear(i);
        }

        return this;
    }

    @Override
    public ManagedHudObjective removeAll() {

        for (int i = 0; i < _lines.size(); i++) {
            Line line = _lines.remove(i);
            line.score.getScoreboard().resetScores(line.score.getEntry());
        }

        return this;
    }

    public void updateLines() {

        for (Line line : _lines) {
            line.update();
        }
    }

    private void fillLinesTo(int index) {

        boolean isUpdated = false;

        for (int i=_lines.size(); i <= index; i++) {

            Score score = _objective.getScore(getInvisiblePrefix(i));
            score.setScore(0);

            _lines.add(new Line(BLANK_TEXT, score));

            isUpdated = true;
        }

        if (!isUpdated)
            return;

        for (int i=0; i <= index; i++) {
            Line line = _lines.get(i);
            line.score.setScore(reverseIndex(i));
        }
    }

    private String getInvisiblePrefix(int index) {

        _textBuffer.setLength(0);

        _textBuffer.append(_prefixes[index]);

        if (index < _prefixes.length) {
            _textBuffer.append(TextColor.WHITE);
            return _textBuffer.toString();
        }

        for (int i=0; i < index - _prefixes.length; i++) {
            _textBuffer.append(_prefixes[i]);
        }

        _textBuffer.append(TextColor.WHITE);
        return _textBuffer.toString();
    }

    private int reverseIndex(int index) {
        return _lines.size() - index - 1;
    }

    private class Line {

        int refreshRate;
        String previousText = null;
        IDynamicText text;

        Score score;

        long nextUpdate;

        Line(IDynamicText text, Score score) {
            this.text = text;
            this.score = score;
        }

        void update() {

            int currentRefreshRate = text.getRefreshRate();

            if (currentRefreshRate == refreshRate && nextUpdate > System.currentTimeMillis())
                return;

            refreshRate = currentRefreshRate;

            int lineIndex = score.getScore();

            String t = getInvisiblePrefix(reverseIndex(lineIndex)) + text.nextText();
            if (previousText != null && previousText.equals(t))
                return;

            previousText = t;

            if (t.length() > 40)
                t = TextUtils.truncate(t, 40);

            score.getScoreboard().resetScores(score.getEntry());

            score = _objective.getScore(t);
            score.setScore(lineIndex);

            nextUpdate = System.currentTimeMillis() + (refreshRate > 0
                    ? refreshRate * 50
                    : 1000);
        }
    }

    private static class Updater implements Runnable {

        @Override
        public void run() {

            Iterator<Entry<ManagedHudObjective, Void>> iterator = _instances.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ManagedHudObjective, Void> entry = iterator.next();

                ManagedHudObjective objective = entry.getKey();

                if (objective.getScoreboard().isDisposed()) {
                    iterator.remove();
                }
                else {
                    objective.updateLines();
                }
            }
        }
    }
}
