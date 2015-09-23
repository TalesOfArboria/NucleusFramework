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
import java.lang.reflect.InvocationTargetException;

/**
 * Static methods used at the beginning of methods to check method
 * arguments quickly.
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
     * @throws java.lang.IllegalStateException if condition is false.
     */
    public static void isValid(boolean condition) {
        isValid(condition, IllegalStateException.class, null);
    }

    /**
     * Ensure supplied condition is true.
     *
     * @param condition  The condition to test.
     * @param message    The exception message to use if the condition is false.
     * @param args       Optional message format arguments.
     *
     * @throws java.lang.IllegalStateException if condition is false.
     */
    public static void isValid(boolean condition, @Nullable String message, Object... args) {
        isValid(condition, IllegalStateException.class, message, args);
    }

    /**
     * Ensure supplied condition is true.
     *
     * @param condition  The condition to test.
     * @param message    The exception message to use if the condition is false.
     * @param args       Optional message format arguments.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if condition is false.
     */
    public static void isValid(boolean condition, Class<? extends RuntimeException> exception,
                               @Nullable String message, Object... args) {
        if (!condition) {
            throw exception(exception, message, args);
        }
    }

    /**
     * Ensure an operation is supported.
     *
     * @param isSupported  The condition to test.
     *
     * @throws java.lang.UnsupportedOperationException if isSupported is false.
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
     * @throws java.lang.UnsupportedOperationException if isSupported is false.
     */
    public static void supported(boolean isSupported, CharSequence message, Object... args) {
        if (!isSupported)
            throw new UnsupportedOperationException(TextUtils.format(message, args).toString());
    }

    /**
     * Ensure supplied object is not null.
     *
     * @param value  The object to check.
     *
     * @throws java.lang.IllegalArgumentException if value is null.
     */
    public static void notNull(@Nullable Object value) {
        notNull(value, "a checked argument");
    }

    /**
     * Ensure supplied object is not null.
     *
     * @param value      The object to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException if value is null.
     */
    public static void notNull(@Nullable Object value, String paramName) {
        notNull(value, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensure supplied object is not null.
     *
     * @param value      The object to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws RuntimeException if value is null.
     */
    public static void notNull(Object value, String paramName,
                               Class<? extends RuntimeException> exception) {
        if (value == null) {
            throw exception(exception, "The value of {0} cannot be null.", paramName);
        }
    }

    /**
     * Ensures supplied string is not null or empty
     *
     * @param value  The string to check.
     *
     * @throws java.lang.IllegalArgumentException  if value is empty or null.
     */
    public static void notNullOrEmpty(@Nullable CharSequence value) {
        notNullOrEmpty(value, "a checked argument");
    }

    /**
     * Ensures supplied string is not null or empty.
     *
     * @param value      The string to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if value is empty or null.
     */
    public static void notNullOrEmpty(@Nullable CharSequence value, String paramName) {
       notNullOrEmpty(value, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied string is not null or empty.
     *
     * @param value      The string to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if value is empty or null.
     */
    public static void notNullOrEmpty(@Nullable CharSequence value, String paramName,
                                      Class<? extends RuntimeException> exception) {
        PreCon.notNull(value, paramName, exception);

        if (value.length() == 0) {
            throw exception(exception, "The value of {0} cannot be empty.", paramName);
        }
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * <p>A valid node name is alphanumeric with no spaces and can only
     * contain the symbols: - _</p>
     *
     * @param nodeName  The name of the node.
     *
     * @throws java.lang.IllegalArgumentException  if nodeName is not a valid node name.
     */
    public static void validNodeName(@Nullable CharSequence nodeName) {
        validNodeName(nodeName, "a checked argument");
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * <p>A valid node name is alphanumeric with no spaces and can only
     * contain the symbols: - _</p>
     *
     * @param nodeName   The name of the node.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if nodeName is not a valid node name.
     */
    public static void validNodeName(@Nullable CharSequence nodeName, String paramName) {
        validNodeName(nodeName, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied string is a proper data node name.
     *
     * <p>A valid node name is alphanumeric with no spaces and can only
     * contain the symbols: - _</p>
     *
     * @param nodeName   The name of the node.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if nodeName is not a valid node name.
     */
    public static void validNodeName(CharSequence nodeName,
                                     String paramName, Class<? extends RuntimeException> exception) {
        PreCon.notNullOrEmpty(nodeName, paramName, exception);

        if (!TextUtils.PATTERN_NODE_NAMES.matcher(nodeName).matches()) {
            throw exception(exception,
                    "{0} is an invalid node name. Is '{1}'. Node names must be alphanumeric "
                            + "and can contain the following symbols: - _", paramName, nodeName);
        }
    }

    /**
     * Ensures supplied string is a proper data node path.
     *
     * <p>A valid node path is alphanumeric with no spaces and can only
     * contain the symbols: - . _</p>
     *
     * @param nodePath  The node path.
     *
     * @throws java.lang.IllegalArgumentException  if nodePath is not a valid node path.
     */
    public static void validNodePath(@Nullable String nodePath) {
        validNodePath(nodePath, "a checked argument");
    }

    /**
     * Ensures supplied string is a proper data node path.
     *
     * <p>A valid node path is alphanumeric with no spaces and can only
     * contain the symbols: - . _</p>
     *
     * @param nodePath   The node path.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if nodePath is not a valid node path.
     */
    public static void validNodePath(@Nullable String nodePath, String paramName) {
        validNodePath(nodePath, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied string is a proper data node path.
     *
     * <p>A valid node path is alphanumeric with no spaces and can only
     * contain the symbols: - . _</p>
     *
     * @param nodePath   The node path.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if nodePath is not a valid node path.
     */
    public static void validNodePath(@Nullable String nodePath,
                                     String paramName, Class<? extends RuntimeException> exception) {
        PreCon.notNull(nodePath, paramName, exception);

        if (!TextUtils.PATTERN_NODE_PATHS.matcher(nodePath).matches()) {
            throw exception(exception,
                    "The argument for {0} is an invalid node path. Is '{1}'. "
                            + "Node paths must be alphanumeric and can contain the "
                            + "following symbols: - . _", paramName, nodePath);
        }
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number  The number to check.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to 0.
     */
    public static void greaterThanZero(long number) {
        greaterThanZero(number, "a checked argument");
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to 0.
     */
    public static void greaterThanZero(long number, String paramName) {
        greaterThanZero(number, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is less than or equal to 0.
     */
    public static void greaterThanZero(long number, String paramName,
                                       Class<? extends RuntimeException> exception) {

        if (number <= 0) {
            throw exception(exception,
                    "Value of {0} must be greater than zero. Is {1}.",
                    paramName, number);
        }
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number  The number to check.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to 0.
     */
    public static void greaterThanZero(double number) {
        greaterThanZero(number, "a checked argument");
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to 0.
     */
    public static void greaterThanZero(double number, String paramName) {
        greaterThanZero(number, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is greater than zero.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is less than or equal to 0.
     */
    public static void greaterThanZero(double number, String paramName,
                                       Class<? extends RuntimeException> exception) {
        if (number <= 0.0D) {
            throw exception(exception,
                    "The value of {0} must be greater than zero. Is {1}.",
                    paramName, number);
        }
    }

    /**
     * Ensures supplied number is greater than limit.
     *
     * @param number  The number to check.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to limit.
     */
    public static void greaterThan(long number, long limit) {
        greaterThan(number, limit, "a checked argument");
    }

    /**
     * Ensures supplied number is greater than a specified limit.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to limit.
     */
    public static void greaterThan(long number, long limit, String paramName) {
        greaterThan(number, limit, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is greater than a specified limit.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is less than or equal to limit.
     */
    public static void greaterThan(long number, long limit,
                                   String paramName, Class<? extends RuntimeException> exception) {

        if (number <= limit) {
            throw exception(exception,
                    "Value of {0} must be greater than {1}. Is {2}.",
                    paramName, limit, number);
        }
    }

    /**
     * Ensures supplied number is greater than a specified limit.
     *
     * @param number  The number to check.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to limit.
     */
    public static void greaterThan(double number, double limit) {
        greaterThan(number, limit, "a checked argument");
    }

    /**
     * Ensures supplied number is greater than limit.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than or equal to limit.
     */
    public static void greaterThan(double number, double limit, String paramName) {
        greaterThan(number, limit, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is greater than limit.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is less than or equal to limit.
     */
    public static void greaterThan(double number, double limit,
                                   String paramName, Class<? extends RuntimeException> exception) {

        if (number <= limit) {
            throw exception(exception,
                    "The value of {0} must be greater than {1}. Is {2}.",
                    paramName, limit, number);
        }
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number  The number to check.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than 0.
     */
    public static void positiveNumber(long number) {
        positiveNumber(number, "a checked argument");
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than 0.
     */
    public static void positiveNumber(long number, String paramName) {
        positiveNumber(number, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is less than 0.
     */
    public static void positiveNumber(long number, String paramName,
                                      Class<? extends RuntimeException> exception) {

        if (number < 0) {
            throw exception(exception,
                    "The value of {0} must be a positive number (0 or greater). Is {1}.",
                    paramName, number);
        }
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number  The number to check.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than 0.
     */
    public static void positiveNumber(double number) {
        positiveNumber(number, "a checked argument");
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is less than 0.
     */
    public static void positiveNumber(double number, String paramName) {
        positiveNumber(number, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is a positive number.
     *
     * @param number     The number to check.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is less than 0.
     */
    public static void positiveNumber(double number, String paramName,
                                      Class<? extends RuntimeException> exception) {

        if (number < 0.0D) {
            throw exception(exception,
                    "The value of {0} must be a positive number (0 or greater). Is {1}.",
                    paramName, number);
        }
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than or equal to limit.
     */
    public static void lessThan(long number, long limit) {
        lessThan(number, limit, "a checked argument");
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than or equal to limit.
     */
    public static void lessThan(long number, long limit, String paramName) {
        lessThan(number, limit, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is greater than or equal to limit.
     */
    public static void lessThan(long number, long limit,
                                String paramName, Class<? extends RuntimeException> exception) {

        if (number >= limit) {
            throw exception(exception,
                    "The value of {0} must be less than {1}. Is {2}.",
                    paramName, limit, number);
        }
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than or equal to limit.
     */
    public static void lessThan (double number, double limit) {
        lessThan(number, limit, "a checked argument");
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than or equal to limit.
     */
    public static void lessThan (double number, double limit, String paramName) {
        lessThan(number, limit, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is less than limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is greater than or equal to limit.
     */
    public static void lessThan (double number, double limit,
                                 String paramName, Class<? extends RuntimeException> exception) {

        if (number >= limit) {
            throw exception(exception,
                    "The value of {0} must be less than {1}. Is {2}.",
                    paramName, limit, number);
        }
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than limit.
     */
    public static void lessThanEqual (long number, long limit) {
        lessThanEqual(number, limit, "a checked argument");
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than limit.
     */
    public static void lessThanEqual (long number, long limit, String paramName) {
        lessThanEqual(number, limit, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is greater than limit.
     */
    public static void lessThanEqual (long number, long limit, String paramName,
                                      Class<? extends RuntimeException> exception) {

        if (number > limit) {
            throw exception(exception,
                    "The value of {0} must be less than or equal to {1}. Is {2}.",
                    paramName, limit, number);
        }
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number  The number to check.
     * @param limit   The numbers limit.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than limit.
     */
    public static void lessThanEqual (double number, double limit) {
        lessThanEqual(number, limit, "a checked argument");
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     *
     * @throws java.lang.IllegalArgumentException  if number is greater than limit.
     */
    public static void lessThanEqual (double number, double limit, String paramName) {
        lessThanEqual(number, limit, paramName, IllegalArgumentException.class);
    }

    /**
     * Ensures supplied number is less than or equal to limit.
     *
     * @param number     The number to check.
     * @param limit      The numbers limit.
     * @param paramName  The name of the parameter being checked.
     * @param exception  The exception to throw if not valid.
     *
     * @throws RuntimeException  if number is greater than limit.
     */
    public static void lessThanEqual (double number, double limit,
                                      String paramName, Class<? extends RuntimeException> exception) {

        if (number > limit) {
            throw exception(exception,
                    "The value of {0} must be less than or equal to {1}. Is {2}.",
                    paramName, limit, number);
        }
    }

    private static RuntimeException exception(
            Class<? extends RuntimeException> clazz, @Nullable CharSequence message, Object... args) {

        if (message != null)
            message = TextUtils.format(message, args);

        if (clazz == IllegalArgumentException.class) {
            return message == null
                    ? new IllegalArgumentException()
                    : new IllegalArgumentException(message.toString());
        }
        else if (clazz == NullPointerException.class) {
            return message == null
                    ? new NullPointerException()
                    : new NullPointerException(message.toString());
        }
        else if (clazz == IllegalStateException.class) {
            return message == null
                    ? new IllegalStateException()
                    : new IllegalStateException(message.toString());
        }
        else {

            try {

                return message == null
                        ? clazz.getConstructor().newInstance()
                        : clazz.getConstructor(String.class).newInstance(message.toString());

            } catch (NoSuchMethodException | InvocationTargetException
                    | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return new RuntimeException(message != null ? message.toString() : null);
            }
        }
    }
}


