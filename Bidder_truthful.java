// Sample code for PS4 problem 4
// COS 445 SD4, Spring 2019
// Created by Andrew Wonnacott

import java.util.List;

public class Bidder_truthful implements Bidder {
  private double budget = Auctioneer.defaultConfig.getBudget();
  private static final double factor = 1;

  // given your value for the day, determine an action
  public double getBid(double v) {
    return Math.min(v * factor, budget);
  }

  // callback function with results
  public void addResults(List<Double> bids, int myBid, double myPayment) {
    // record my utility and budget
    if (myBid >= 0) {
      budget -= myPayment;
    }
  }
}
