package com.jcwhatever.bukkit.generic.utils;

import javax.annotation.Nullable;

/**
 * Static methods used at the beginning of methods
 * to check method arguments quickly.
 *
 * All methods throw an exception if conditions are not met.
 */
public class PreCon {

    private PreCon() {}

    /**
     * Ensure supplied condition is true.
     *
     * @param condition  The condition to test
     *
     * @throws java.lang.IllegalArgumentException
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
     * @throws java.lang.IllegalArgumentException
     */
    public static void isValid(boolean condition, String message) {
        if (!condition) {
            if (message != null) {
                throw new IllegalArgumentException(message);
            }
            else {
                throw new IllegalArgumentException();
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
     * Ensures supplied string is not null or empty
     *
     * @param value         The string to check
     * @param nullMessage   The exception message to use if the string is null
     * @param emptyMessage  The exception message to use if the string is empty
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


