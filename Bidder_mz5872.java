/* import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Bidder_mz5872 implements Bidder {
  private double budget;
  private int roundsCompleted = 0;
  private List<Double> normalizedWinningBids = new ArrayList<>();
  private double aggressiveness = 1.0;
  private static final int TOTAL_ROUNDS = 10000; // As specified in the problem statement
  private static final double[] RATES = {0.05, 0.035, 0.03, 0.015, 0.015, 0.015, 0.015, 0.015, 0.015, 0.015}; // Assuming these are the CTRs for the 10 slots

  public Bidder_mz5872() {
    this.budget = Auctioneer.defaultConfig.getBudget();
  }

  @Override
  public double getBid(double v) {
    double bid = v * aggressiveness;

    // Determine the maximum bid based on remaining budget and rounds to ensure budget lasts all game
    double maxBidBasedOnBudget = budget / (TOTAL_ROUNDS - roundsCompleted);
    bid = Math.min(bid, maxBidBasedOnBudget);

    // Adjust bid to ensure it's a sensible proportion of the budget
    double sensibleBid = budget * RATES[0]; // Use the CTR of the top slot for calculation
    return Math.min(bid, sensibleBid);
  }

  @Override
  public void addResults(List<Double> bids, int myBid, double myPayment) {
    roundsCompleted++;

    // Normalize winning bids by the CTR and record them
    if (!bids.isEmpty()) {
      for (int i = 0; i < Math.min(bids.size(), RATES.length); i++) {
        double normalizedBid = bids.get(i) / RATES[i];
        normalizedWinningBids.add(normalizedBid);
      }
    }

    Collections.sort(normalizedWinningBids);
    double medianBid = !normalizedWinningBids.isEmpty() ? 
        normalizedWinningBids.get(normalizedWinningBids.size() / 2) : 0;

    // Adjust aggressiveness based on the median normalized bid
    if (medianBid > 0) {
      aggressiveness = budget / (medianBid * (TOTAL_ROUNDS - roundsCompleted));
    }

    // Update the budget
    if (myBid >= 0) {
      budget -= myPayment;
    }

    // Ensure aggressiveness is within reasonable bounds
    aggressiveness = Math.min(aggressiveness, 2.0); // Upper bound
    aggressiveness = Math.max(aggressiveness, 0.5); // Lower bound
  }
} */
// Sample code for PS4 problem 4
// COS 445 SD4, Spring 2019
// Created by Andrew Wonnacott

import java.util.List;

public class Bidder_mz5872 implements Bidder {
  private double budget = Auctioneer.defaultConfig.getBudget();
  private static final double factor = 0.5;

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