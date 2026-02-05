package dev.anchxt.jod.schema;

/**
 * Schema for validating Double values.
 */
public class DoubleSchema extends NumberSchema<Double, DoubleSchema> {

    private static final double EPSILON = 1e-10;

    @Override
    protected boolean isPositive(Double value) {
        return value > 0.0;
    }

    @Override
    protected boolean isNegative(Double value) {
        return value < 0.0;
    }

    @Override
    protected boolean isMultipleOf(Double value, Double divisor) {
        if (divisor == 0.0) {
            return false;
        }
        double remainder = value % divisor;
        // Handle floating point precision issues
        return Math.abs(remainder) < EPSILON || Math.abs(remainder - divisor) < EPSILON;
    }
}
