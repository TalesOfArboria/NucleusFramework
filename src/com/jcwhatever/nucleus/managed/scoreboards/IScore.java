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

/*
 * 
 */
public interface IScore {

    /**
     * Get the name of the entry the score is for.
     */
    String getEntry();

    /**
     * Get the objective the score is from.
     */
    IObjective getObjective();

    /**
     * Get the score.
     */
    int getScore();

    /**
     * Set the score.
     *
     * @param score  The score value.
     */
    void setScore(int score);

    /**
     * Add the specified amount to the score.
     *
     * @param amount  The amount to add (negative numbers to subtract).
     *
     * @return  The new score.
     */
    int add(int amount);

    /**
     * Determine if the score is set.
     */
    boolean isScoreSet();

    /**
     * Get the owning scoreboard.
     */
    IManagedScoreboard getScoreboard();
}
