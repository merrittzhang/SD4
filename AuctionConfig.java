// COS 445 SD4, Spring 2019
// Created by Andrew Wonnacott

import java.util.Arrays;

public class AuctionConfig {
  private final double[] _rates;
  private final double _budget;
  private final int _days;
  private final double _max;

  public AuctionConfig(double[] rates, double budget, int days, double max) {
    for (int i = 0; i < rates.length - 1; ++i) {
      if (rates[i] < rates[i + 1]) {
        throw new RuntimeException(
            "Tried to create auction config with increasing rates: " + Arrays.toString(rates));
      }
    }
    _rates = rates;
    _budget = budget;
    _days = days;
    _max = max;
  }

  public double[] getRates() {
    return _rates;
  }

  public double getBudget() {
    return _budget;
  }

  public int getDays() {
    return _days;
  }

  public double getMaxValue() {
    return _max;
  }
}
