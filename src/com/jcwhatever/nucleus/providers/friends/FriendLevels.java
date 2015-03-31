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

package com.jcwhatever.nucleus.providers.friends;

/**
 * Global default friend levels.
 */
public class FriendLevels {

    public static final IFriendLevel CASUAL = new IFriendLevel() {

        @Override
        public String getName() {
            return "Casual";
        }

        @Override
        public String getSearchName() {
            return "casual";
        }

        @Override
        public int getRawLevel() {
            return 25;
        }
    };

    public static final IFriendLevel CLOSE = new IFriendLevel() {

        @Override
        public String getName() {
            return "Close";
        }

        @Override
        public String getSearchName() {
            return "close";
        }

        @Override
        public int getRawLevel() {
            return 50;
        }
    };

    public static final IFriendLevel BEST = new IFriendLevel() {

        @Override
        public String getName() {
            return "Best";
        }

        @Override
        public String getSearchName() {
            return "best";
        }

        @Override
        public int getRawLevel() {
            return 75;
        }
    };
}
