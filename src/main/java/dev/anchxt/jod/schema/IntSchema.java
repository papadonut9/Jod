package dev.anchxt.jod.schema;

/**
 * Schema for validating Integer values.
 */
public class IntSchema extends NumberSchema<Integer, IntSchema> {

    @Override
    protected boolean isPositive(Integer value) {
        return value > 0;
    }

    @Override
    protected boolean isNegative(Integer value) {
        return value < 0;
    }

    @Override
    protected boolean isMultipleOf(Integer value, Integer divisor) {
        return divisor != 0 && value % divisor == 0;
    }
}
