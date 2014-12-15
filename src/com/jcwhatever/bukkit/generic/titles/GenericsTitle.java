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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.GenericsLib.NmsHandlers;
import com.jcwhatever.bukkit.generic.nms.INmsTitleHandler;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Utils;
import com.jcwhatever.bukkit.generic.utils.text.TextComponent;
import com.jcwhatever.bukkit.generic.utils.text.TextComponents;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * GenericsLib implementation of {@link ITitle}
 */
public class GenericsTitle implements ITitle {

    private final String _title;
    private final String _subTitle;
    private final int _fadeInTime;
    private final int _stayTime;
    private final int _fadeOutTime;

    private TextComponents _titleComponents;
    private TextComponents _subTitleComponents;
    private String _formattedTitle;
    private String _formattedSubTitle;

    /**
     * Constructor.
     *
     * <p>Uses default times.</p>
     *
     * @param title     The title text.
     * @param subTitle  The sub title text.
     */
    public GenericsTitle(String title, @Nullable String subTitle) {
        this(title, subTitle, -1, -1, -1);
    }

    /**
     * Constructor.
     *
     * @param title        The title text components.
     * @param subTitle     The sub title text components.
     * @param fadeInTime   The time spent fading in.
     * @param stayTime     The time spent being displayed.
     * @param fadeOutTime  The time spent fading out.
     */
    public GenericsTitle(String title, @Nullable String subTitle,
                              int fadeInTime, int stayTime, int fadeOutTime) {
        PreCon.notNull(title);

        _title = title;
        _subTitle = subTitle;

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
    public String getTitle() {
        return _title;
    }

    /**
     * Get the sub-title components.
     */
    @Override
    @Nullable
    public String getSubTitle() {
        return _subTitle;
    }

    /**
     * Show the title to the specified player.
     *
     * @param p  The player to show the title to.
     */
    @Override
    public void showTo(Player p) {
        PreCon.notNull(p);

        INmsTitleHandler titleHandler = GenericsLib.getNmsManager().getNmsHandler(NmsHandlers.TITLES.name());
        if (titleHandler != null) {

            StringBuilder buffer = new StringBuilder(50);

            getJson(buffer, getTitleComponents());

            String title = buffer.toString();
            String subTitle = null;

            if (_subTitle != null) {
                buffer.setLength(0);
                getJson(buffer, getSubTitleComponents());
                subTitle = buffer.toString();
            }

            titleHandler.send(p, title, subTitle, _fadeInTime, _stayTime, _fadeOutTime);

            return;
        }

        // use commands as a fallback if there is no NMS title handler

        String titleCommand = getCommand(p, TitleCommandType.TITLE);
        String subTitleCommand = null;
        String timesCommand = null;

        if (_subTitleComponents != null) {
            subTitleCommand = getCommand(p, TitleCommandType.SUBTITLE);
        }

        // if one time is -1 then all times are -1
        if (getTime(_fadeInTime) != -1) {
            timesCommand = getCommand(p, TitleCommandType.TIMES);
        }

        if (timesCommand != null) {
            Utils.executeAsConsole(timesCommand);
        }

        if (subTitleCommand != null) {
            Utils.executeAsConsole(subTitleCommand);
        }

        Utils.executeAsConsole(titleCommand);
    }

    public TextComponents getTitleComponents() {
        if (_titleComponents == null) {
            _titleComponents = new TextComponents(_title);
        }

        return _titleComponents;
    }

    @Nullable
    public TextComponents getSubTitleComponents() {
        if (_subTitle != null && _subTitleComponents == null) {
            _subTitleComponents = new TextComponents(_subTitle);
        }

        return _subTitleComponents;
    }

    private String getCommand(Player p, TitleCommandType type) {
        StringBuilder buffer = new StringBuilder(100);

        buffer.append("title ");
        buffer.append(p.getName());
        buffer.append(' ');

        buffer.append(type.name());
        buffer.append(' ');

        switch (type) {
            case TITLE:
                getJson(buffer, getTitleComponents());
                break;
            case SUBTITLE:
                //noinspection ConstantConditions
                getJson(buffer, getSubTitleComponents());
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

    private int getTime(int time) {
        if (_fadeInTime < 0 || _stayTime < 0 || _fadeOutTime < 0)
            return -1;

        return time;
    }

    private void getJson(StringBuilder buffer, TextComponents segments) {

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

    protected enum TitleCommandType {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET
    }
}
