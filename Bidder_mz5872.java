// Sample code for PS4 problem 4
// COS 445 SD4, Spring 2019
// Created by Andrew Wonnacott

import java.util.List;

public class Bidder_mz5872 implements Bidder {
    private double budget = 500; // initial budget
    private int totalRounds = 10000;
    private int currentRound = 0;
    private double[] slotClickThroughRates = {0.05, 0.035, 0.03, //... and so on for all 10 slots
                                              0.015}; // assuming the last 7 slots have the same CTR

    @Override
    public double getBid(double dailyValue) {
        currentRound++; // Increment the current round

        // Adjust your bid based on your strategy
        // For example, bid a fraction of the daily value, taking into account the CTR of the best slot
        double maxCTR = slotClickThroughRates[0]; // Best slot CTR
        double bid = dailyValue * maxCTR;

        // Make sure we don't bid more than our budget allows
        double maxBid = budget / (totalRounds - currentRound + 1);
        bid = Math.min(bid, maxBid);

        return bid;
    }

    @Override
    public void addResults(List<Double> bids, int myBid, double myPayment) {
        // Subtract the payment from the budget
        budget -= myPayment;

        // Here you can also analyze the bids to adjust your strategy
        // For example, you can calculate the average winning bid and adjust your future bids accordingly
        // This is left as an exercise
    }
}
