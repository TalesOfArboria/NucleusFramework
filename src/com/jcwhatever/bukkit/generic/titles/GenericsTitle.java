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

package com.jcwhatever.bukkit.generic.titles;

import com.jcwhatever.bukkit.generic.utils.Utils;
import com.jcwhatever.bukkit.generic.utils.text.TextComponent;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

/**
 * GenericsLib implementation of {@link ITitle}
 */
public class GenericsTitle implements ITitle {

    private final List<TextComponent> _titleComponents;
    private final List<TextComponent> _subTitleComponents;
    private final int _fadeInTime;
    private final int _stayTime;
    private final int _fadeOutTime;

    public GenericsTitle(List<TextComponent> titleComponents,
                         @Nullable List<TextComponent> subTitleComponents) {
        this(titleComponents, subTitleComponents, -1, -1, -1);
    }

    public GenericsTitle(List<TextComponent> titleComponents,
                         @Nullable List<TextComponent> subTitleComponents,
                         int fadeInTime, int stayTime, int fadeOutTime) {

        _titleComponents = Collections.unmodifiableList(titleComponents);

        _subTitleComponents = subTitleComponents != null
                ? Collections.unmodifiableList(subTitleComponents)
                : null;

        _fadeInTime = fadeInTime;
        _stayTime = stayTime;
        _fadeOutTime = fadeOutTime;

    }

    /**
     * Get the time spent fading in.
     *
     * @return -1 if the default is used.
     */
    @Override
    public int getFadeInTime() {
        return getTime(_fadeInTime);
    }

    /**
     * Get the time spent being displayed.
     *
     * @return -1 if the default is used.
     */
    @Override
    public int getStayTime() {
        return getTime(_stayTime);
    }

    /**
     * Get the time spent fading out.
     *
     * @return  -1 if the default is used.
     */
    @Override
    public int getFadeOutTime() {
        return getTime(_fadeOutTime);
    }

    /**
     * Get the title components.
     */
    @Override
    public List<TextComponent> getTitleComponents() {
        return _titleComponents;
    }

    /**
     * Get the sub-title components.
     */
    @Override
    public List<TextComponent> getSubTitleComponents() {
        if (_subTitleComponents == null)
            return new ArrayList<>(0);

        return _subTitleComponents;
    }

    /**
     * Show the title to the specified player.
     *
     * @param p  The player to show the title to.
     */
    @Override
    public void showTo(Player p) {
        String titleCommand = getCommand(p, TitleType.TITLE);
        String subTitleCommand = null;
        String timesCommand = null;

        if (_subTitleComponents != null) {
            subTitleCommand = getCommand(p, TitleType.SUBTITLE);
        }

        // if one time is -1 then all times are -1
        if (getTime(_fadeInTime) != -1) {
            timesCommand = getCommand(p, TitleType.TIMES);
        }

        if (timesCommand != null) {
            Utils.executeAsConsole(timesCommand);
        }

        if (subTitleCommand != null) {
            Utils.executeAsConsole(subTitleCommand);
        }

        Utils.executeAsConsole(titleCommand);
    }

    private String getCommand(Player p, TitleType type) {
        StringBuilder buffer = new StringBuilder(100);

        buffer.append("title ");
        buffer.append(p.getName());
        buffer.append(' ');

        buffer.append(type.name());
        buffer.append(' ');

        switch (type) {
            case TITLE:
                getJson(buffer, _titleComponents);
                break;
            case SUBTITLE:
                getJson(buffer, _subTitleComponents);
                break;
            case TIMES:
                buffer.append(_fadeInTime);
                buffer.append(' ');
                buffer.append(_stayTime);
                buffer.append(' ');
                buffer.append(_fadeOutTime);
                break;
            case CLEAR:
                break;
            case RESET:
                break;
            default:
                throw new AssertionError();
        }

        return buffer.toString();
    }

    private enum TitleType {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET
    }

    private int getTime(int time) {
        if (_fadeInTime < 0 || _stayTime < 0 || _fadeOutTime < 0)
            return -1;

        return time;
    }

    private void getJson(StringBuilder buffer, List<TextComponent> segments) {

        buffer.append('{');
        getJsonSegment(buffer, segments.get(0));

        if (segments.size() > 1) {
            buffer.append(",extra:[");

            for (int i=1; i < segments.size(); i++) {
                buffer.append('{');
                getJsonSegment(buffer, segments.get(i));
                buffer.append('}');

                if (i < segments.size() - 1) {
                    buffer.append(',');
                }
            }

            buffer.append(']');
        }

        buffer.append('}');
    }

    private void getJsonSegment(StringBuilder buffer, TextComponent segment) {
        buffer.append("text:\"");
        buffer.append(segment.getText());
        buffer.append('"');

        if (segment.getTextColor() != null) {

            buffer.append(",color:");
            buffer.append(segment.getTextColor().name().toLowerCase());
        }
    }

}
