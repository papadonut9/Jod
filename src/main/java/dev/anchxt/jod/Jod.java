package dev.anchxt.jod;

import dev.anchxt.jod.schema.*;

/**
 * Main entry point for the Jod validation library.
 * Provides static factory methods for creating schema builders.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>{@code
 * var nameSchema = Jod.string().min(2).max(50).trim();
 * var ageSchema = Jod.intType().min(0).max(120);
 * }</pre>
 */
public final class Jod {

    private Jod() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a new StringSchema for validating strings.
     */
    public static StringSchema string() {
        return new StringSchema();
    }

    /**
     * Creates a new IntSchema for validating integers.
     */
    public static IntSchema intType() {
        return new IntSchema();
    }

    /**
     * Creates a new LongSchema for validating long values.
     */
    public static LongSchema longType() {
        return new LongSchema();
    }

    /**
     * Creates a new DoubleSchema for validating double values.
     */
    public static DoubleSchema doubleType() {
        return new DoubleSchema();
    }

    /**
     * Creates a new BooleanSchema for validating booleans.
     */
    public static BooleanSchema bool() {
        return new BooleanSchema();
    }
}
