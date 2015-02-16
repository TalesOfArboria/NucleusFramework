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

package com.jcwhatever.nucleus.providers.npc.ai.actions;

import com.jcwhatever.nucleus.providers.npc.INpc;

import javax.annotation.Nullable;

/**
 * Interface for an action selector object.
 *
 * <p>Used to run actions and set action result.</p>
 */
public interface INpcActionSelector {

    /**
     * Get the NPC the selector is for.
     */
    INpc getNpc();

    /**
     * Determine if the {@link INpcAction} the selector is for
     * is finished.
     */
    boolean isFinished();

    /**
     * Ends the running action.
     */
    void finish();

    /**
     * Ends the running action.
     *
     * <p>Optionally add another action to run an action in response to the
     * result of running the current action.</p>
     *
     * @param action  Optional next action to run.
     */
    void finish(@Nullable INpcAction action);

    /**
     * Ends the running action and all child actions. Actions that were set to
     * run when the action is finished are also cancelled.
     */
    void cancel();

    /**
     * Ends the running action and all child actions. Actions that were set to
     * run when the action is finished are also cancelled.
     *
     * @param action  Optional action to run.
     */
    void cancel(@Nullable INpcAction action);

    /**
     * Run another action. If the action continues to run, it will run concurrently with the
     * current action and continue even when the current action ends so long as it specifies
     * that it needs to continue running.
     *
     * @param action  The action to run.
     *
     * @return  The argument actions {@link INpcActionSelector}.
     */
    INpcActionSelector run(INpcAction action);

    /**
     * Run another action after the current action is finished.
     *
     * @param action  The action to run.
     *
     * @return  The argument actions {@link INpcActionSelector}.
     */
    INpcActionSelector next(INpcAction action);
}
