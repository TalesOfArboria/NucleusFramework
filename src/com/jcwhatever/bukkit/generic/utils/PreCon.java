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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import javax.annotation.Nullable;

/**
 * Static methods used at the beginning of methods
 * to check method arguments quickly.
 *
 * All methods throw an exception if conditions are not met.
 */
public final class PreCon {

    private PreCon() {}

    /**
     * Ensure supplied condition is true.
     *
     * @param condition  The condition to test
     *
     * @throws java.lang.IllegalStateException
     */
    public static void isValid(boolean condition) {
        isValid(condition, null);
    }

    /**
     * Ensure supplied condition is true.
     *
     * @param condition  The condition to test
     * @param message    The exception message to use if the condition is false
     *
     * @throws java.lang.IllegalStateException
     */
    public static void isValid(boolean condition, @Nullable String message) {
        if (!condition) {
            if (message != null) {
                throw new IllegalStateException(message);
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Ensure supplied object is not null.
     *
     * @param value  The object to check
     *
     * @throws java.lang.NullPointerException
     */
    public static void notNull(@Nullable Object value) {
        if (value == null)
            throw new NullPointerException();
    }

    /**
     * Ensure supplied object is not null.
     *
     * @param value    The object to check
     * @param message  The exception message to use if the object is false
     *
     * @throws java.lang.NullPointerException
     */
    public static void notNull(@Nullable Object value, String message) {
        if (value == null)
            throw new NullPointerException(message);
    }

    /**
     * Ensures supplied string is not null or empty
     *
     * @param value  The string to check
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void notNullOrEmpty(@Nullable String value) {
        if (value == null)
            throw new NullPointerException();

        if (value.isEmpty())
            throw new IllegalArgumentException("String argument cannot be empty.");
    }

    /**
     * Ensures supplied string is not null or empty.
     *
     * @param value         The string to check.
     * @param nullMessage   The exception message to use if the string is null.
     * @param emptyMessage  The exception message to use if the string is empty.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void notNullOrEmpty(@Nullable String value, String nullMessage, String emptyMessage) {
        if (value == null)
            throw new NullPointerException(nullMessage);

        if (value.isEmpty())
            throw new IllegalArgumentException(emptyMessage);
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * @param nodeName  The name of the node.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodeName(@Nullable String nodeName) {
        PreCon.notNullOrEmpty(nodeName);

        if (!TextUtils.PATTERN_NODE_NAMES.matcher(nodeName).matches())
            throw new IllegalArgumentException("Node names must be alphanumeric and " +
                    "can contain the following symbols: - _");
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * @param nodeName        The name of the node.
     * @param invalidMessage  The exception message to use.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodeName(@Nullable String nodeName, String invalidMessage) {
        PreCon.notNullOrEmpty(nodeName, invalidMessage, invalidMessage);

        if (!TextUtils.PATTERN_NODE_NAMES.matcher(nodeName).matches())
            throw new IllegalArgumentException(invalidMessage);
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * @param nodeName        The name of the node.
     * @param invalidMessage  The exception message to use if the name contains illegal characters.
     * @param nullMessage     The exception message to use if the name is null.
     * @param emptyMessage    The exception message to use if the name is empty.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodeName(@Nullable String nodeName,
                                     String invalidMessage,
                                     String nullMessage,
                                     String emptyMessage) {
        PreCon.notNullOrEmpty(nodeName, nullMessage, emptyMessage);

        if (!TextUtils.PATTERN_NODE_NAMES.matcher(nodeName).matches())
            throw new IllegalArgumentException(invalidMessage);
    }

    /**
     * Ensures supplied string is a proper data node path.
     *
     * @param nodePath  The node path.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodePath(@Nullable String nodePath) {
        PreCon.notNullOrEmpty(nodePath);

        if (!TextUtils.PATTERN_NODE_PATHS.matcher(nodePath).matches())
            throw new IllegalArgumentException("Node paths must be alphanumeric and " +
                    "can contain the following symbols: - . _");
    }

    /**
     * Ensures supplied string is a proper data node path.
     *
     * @param nodePath        The node path.
     * @param invalidMessage  The exception message to use.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodePath(@Nullable String nodePath, String invalidMessage) {
        PreCon.notNullOrEmpty(nodePath, invalidMessage, invalidMessage);

        if (!TextUtils.PATTERN_NODE_PATHS.matcher(nodePath).matches())
            throw new IllegalArgumentException(invalidMessage);
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * @param nodePath        The node path.
     * @param invalidMessage  The exception message to use if the path contains illegal characters.
     * @param nullMessage     The exception message to use if the path is null.
     * @param emptyMessage    The exception message to use if the path is empty.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodePath(@Nullable String nodePath,
                                     String invalidMessage,
                                     String nullMessage,
                                     String emptyMessage) {
        PreCon.notNullOrEmpty(nodePath, nullMessage, emptyMessage);

        if (!TextUtils.PATTERN_NODE_PATHS.matcher(nodePath).matches())
            throw new IllegalArgumentException(invalidMessage);
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number  The number to check
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void greaterThanZero(long number) {
        if (number <= 0)
            throw new IllegalArgumentException("Argument must be greater than zero. Is " + number + '.');
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number   The number to check
     * @param message  The exception message to use if the number is less than or equal to zero
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void greaterThanZero(long number, String message) {
        if (number <= 0)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number  The number to check
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void greaterThanZero(double number) {
        if (number <= 0.0D)
            throw new IllegalArgumentException("Argument must be greater than zero. Is " + number + '.');
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number   The number to check
     * @param message  The exception message to use if the number is less than or equal to zero
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void greaterThanZero(double number, String message) {
        if (number <= 0.0D)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number  The number to check
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void positiveNumber (long number) {
        if (number < 0)
            throw new IllegalArgumentException("Argument must be a positive number. Is " + number + '.');
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number   The number to check
     * @param message  The exception message to use if the number is less than zero
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void positiveNumber (long number, String message) {
        if (number < 0)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number  The number to check
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void positiveNumber (double number) {
        if (number < 0.0D)
            throw new IllegalArgumentException("Argument must be a positive number. Is " + number + '.');
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number   The number to check
     * @param message  The exception message to use if the number is less than zero
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void positiveNumber (double number, String message) {
        if (number < 0.0D)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number  The number to check
     * @param limit   The numbers limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThan (long number, long limit) {
        if (number >= limit)
            throw new IllegalArgumentException("Argument must be less than " + limit + ". Is " + number + '.');
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number   The number to check
     * @param limit    The numbers limit
     * @param message  The exception message to use if the number is greater than or equal to limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThan (long number, long limit, String message) {
        if (number >= limit)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number  The number to check
     * @param limit   The numbers limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThan (double number, double limit) {
        if (number >= limit)
            throw new IllegalArgumentException("Argument must be less than " + limit + ". Is " + number + '.');
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number   The number to check
     * @param limit    The numbers limit
     * @param message  The exception message to use if the number is greater than or equal to limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThan (double number, double limit, String message) {
        if (number >= limit)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number  The number to check
     * @param limit   The numbers limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThanEqual (long number, long limit) {
        if (number > limit)
            throw new IllegalArgumentException("Argument must be less than or equal to " + limit + ". Is " + number + '.');
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number   The number to check
     * @param limit    The numbers limit
     * @param message  The exception message to use if the number is greater than limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThanEqual (long number, long limit, String message) {
        if (number > limit)
            throw new IllegalArgumentException(message);
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number  The number to check
     * @param limit   The numbers limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThanEqual (double number, double limit) {
        if (number > limit)
            throw new IllegalArgumentException("Argument must be less than or equal to " + limit + ". Is " + number + '.');
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number   The number to check
     * @param limit    The numbers limit
     * @param message  The exception message to use if the number is greater than limit
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThanEqual (double number, double limit, String message) {
        if (number > limit)
            throw new IllegalArgumentException(message);
    }

}


