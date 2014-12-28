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


package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.utils.text.TextUtils;

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
     * @param condition  The condition to test.
     *
     * @throws java.lang.IllegalStateException
     */
    public static void isValid(boolean condition) {
        isValid(condition, null);
    }

    /**
     * Ensure supplied condition is true.
     *
     * @param condition  The condition to test.
     * @param message    The exception message to use if the condition is false.
     * @param args       Optional message format arguments.
     *
     * @throws java.lang.IllegalStateException
     */
    public static void isValid(boolean condition, @Nullable String message, Object... args) {
        if (!condition) {
            if (message != null) {
                throw new IllegalStateException(TextUtils.format(message, args));
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Ensure an operation is supported.
     *
     * @param isSupported  The condition to test.
     *
     * @throws java.lang.UnsupportedOperationException
     */
    public static void supported(boolean isSupported) {
        if (!isSupported)
            throw new UnsupportedOperationException();
    }

    /**
     * Ensure an operation is supported.
     *
     * @param isSupported  The condition to test.
     * @param message      The exception message to use if the condition is false.
     * @param args         Optional message format arguments.
     *
     * @throws java.lang.UnsupportedOperationException
     */
    public static void supported(boolean isSupported, String message, Object... args) {
        if (!isSupported)
            throw new UnsupportedOperationException(TextUtils.format(message, args));
    }

    /**
     * Ensure supplied object is not null.
     *
     * @param value  The object to check.
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
     * @param value    The object to check.
     * @param message  The exception message to use if the object is false.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.NullPointerException
     */
    public static void notNull(@Nullable Object value, String message, Object... args) {
        if (value == null)
            throw new NullPointerException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied string is not null or empty
     *
     * @param value  The string to check.
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
     *  @param args         Optional message format arguments for both messages.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void notNullOrEmpty(@Nullable String value,
                                      String nullMessage, String emptyMessage, Object... args) {
        if (value == null)
            throw new NullPointerException(TextUtils.format(nullMessage, args));

        if (value.isEmpty())
            throw new IllegalArgumentException(TextUtils.format(emptyMessage, args));
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
     * @param args            Optional message format arguments.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodeName(@Nullable String nodeName, String invalidMessage, Object... args) {
        PreCon.notNullOrEmpty(nodeName, invalidMessage, invalidMessage, args);

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
     * @param args            Optional message format arguments.
     *
     * @throws java.lang.NullPointerException
     * @throws java.lang.IllegalArgumentException
     */
    public static void validNodePath(@Nullable String nodePath, String invalidMessage, Object... args) {
        PreCon.notNullOrEmpty(nodePath, invalidMessage, invalidMessage);

        if (!TextUtils.PATTERN_NODE_PATHS.matcher(nodePath).matches())
            throw new IllegalArgumentException(TextUtils.format(invalidMessage, args));
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
     * @param number  The number to check.
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
     * @param number   The number to check.
     * @param message  The exception message to use if the number is less than or equal to zero.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void greaterThanZero(long number, String message, Object... args) {
        if (number <= 0)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number  The number to check.
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
     * @param number   The number to check.
     * @param message  The exception message to use if the number is less than or equal to zero.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void greaterThanZero(double number, String message, Object... args) {
        if (number <= 0.0D)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number  The number to check.
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
     * @param number   The number to check.
     * @param message  The exception message to use if the number is less than zero.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void positiveNumber (long number, String message, Object... args) {
        if (number < 0)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number  The number to check.
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
     * @param number   The number to check.
     * @param message  The exception message to use if the number is less than zero.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void positiveNumber (double number, String message, Object... args) {
        if (number < 0.0D)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
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
     * @param number   The number to check.
     * @param limit    The numbers limit.
     * @param message  The exception message to use if the number is greater than or equal to limit.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThan (long number, long limit, String message, Object... args) {
        if (number >= limit)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
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
     * @param number   The number to check.
     * @param limit    The numbers limit.
     * @param message  The exception message to use if the number is greater than or equal to limit.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThan (double number, double limit, String message, Object... args) {
        if (number >= limit)
            throw new IllegalArgumentException(String.format(message, args));
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
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
     * @param number   The number to check.
     * @param limit    The numbers limit.
     * @param message  The exception message to use if the number is greater than limit.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThanEqual (long number, long limit, String message, Object... args) {
        if (number > limit)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
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
     * @param number   The number to check.
     * @param limit    The numbers limit.
     * @param message  The exception message to use if the number is greater than limit.
     * @param args     Optional message format arguments.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void lessThanEqual (double number, double limit, String message, Object... args) {
        if (number > limit)
            throw new IllegalArgumentException(TextUtils.format(message, args));
    }

}


