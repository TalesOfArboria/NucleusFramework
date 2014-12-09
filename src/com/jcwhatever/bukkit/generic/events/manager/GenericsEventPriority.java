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


package com.jcwhatever.bukkit.generic.events.manager;

/**
 * Defines the order an event handler
 * executed.
 */
public enum GenericsEventPriority {

    /**
     * Watcher. Only watches to see if the event
     * is called but does not effect the outcome
     * of the event. Is always called even if the
     * event is cancelled.
     */
    WATCHER (5),

    /**
     * The last handler to be called.
     */
    LAST    (4),

    /**
     * Low priority. Called second to last.
     */
    LOW     (3),

    /**
     * Normal priority.
     */
    NORMAL  (2),

    /**
     * High priority. Called second.
     */
    HIGH    (1),

    /**
     * Highest priority. Called first.
     */
    FIRST   (0);

    private final int _order;

    GenericsEventPriority(int order) {
        _order = order;
    }

    /**
     * Get a sort order index number.
     */
    public int getSortOrder() {
        return _order;
    }
}
