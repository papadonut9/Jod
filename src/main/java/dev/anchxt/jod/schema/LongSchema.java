package dev.anchxt.jod.schema;

/** Schema for validating Long values. */
public class LongSchema extends NumberSchema<Long, LongSchema> {

  @Override
  protected boolean isPositive(Long value) {
    return value > 0L;
  }

  @Override
  protected boolean isNegative(Long value) {
    return value < 0L;
  }

  @Override
  protected boolean isMultipleOf(Long value, Long divisor) {
    return divisor != 0L && value % divisor == 0L;
  }
}
