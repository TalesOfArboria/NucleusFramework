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

package com.jcwhatever.nucleus.managed.scoreboards;

import org.bukkit.scoreboard.DisplaySlot;

/**
 * Interface for a managed scoreboard objective.
 */
public interface IObjective {

    /**
     * Get the objective name.
     */
    String getName();

    /**
     * Get the objective display name.
     */
    String getDisplayName();

    /**
     * Set the objective display name.
     *
     * @param displayName  The display name.
     * @param args         Optional format arguments.
     */
    void setDisplayName(CharSequence displayName, Object... args);

    /**
     * Get the scoreboard criteria.
     */
    String getCriteria();

    /**
     * Determine if the objective can be modified.
     */
    boolean isModifiable();

    /**
     * Get the owning scoreboard.
     */
    IScoreboard getScoreboard();

    /**
     * Unregister and remove the objective from its owning scoreboard.
     */
    void unregister();

    /**
     * Set the objectives display slot.
     *
     * @param displaySlot  The objectives display slot.
     */
    void setDisplaySlot(DisplaySlot displaySlot);

    /**
     * Get the objectives display slot.
     */
    DisplaySlot getDisplaySlot();
}
