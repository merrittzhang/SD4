// Auctioneer.java: Testing code for auctions assignment
// COS 445 SD5, Spring 2019
// Created by Cyril Zhang
// Modified by Andrew Wonnacott

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Auctioneer extends Tournament<Bidder, AuctionConfig> {
  Auctioneer(List<String> bidderNames) {
    super(Bidder.class, bidderNames);
  }

  public static double ER(double maxValue) {
    return 1.0 / Math.max(1.0 / maxValue, rand.nextDouble());
  }

  public double[] runTrial(List<Class<? extends Bidder>> strategies, AuctionConfig config) {
    final int NUM_DAYS = config.getDays();
    final double[] RATES = config.getRates();
    final double INITIAL_BUDGET = config.getBudget();
    final double MAX_VALUE = config.getMaxValue();

    // Uncomment this to suppress output.
    // PrintStream stdout = System.out;
    // System.setOut(new PrintStream(OutputStream.nullOutputStream()));

    if (strategies.size() <= RATES.length) {
      throw new RuntimeException("Too few bidders: " + Integer.toString(strategies.size()) + " (need "
          + Integer.toString(RATES.length + 1) + ")");
    }
    List<Bidder> bidders = new ArrayList<Bidder>();
    for (Class<? extends Bidder> bidderClass : strategies) {
      try {
        bidders.add(bidderClass.getDeclaredConstructor().newInstance());
      } catch (ReflectiveOperationException roe) {
        throw new RuntimeException(roe);
      }
    }

    double[] budget = new double[bidders.size()];
    double[] utility = new double[bidders.size()];
    // Set up budgets and current payoffs
    for (int i = 0; i < bidders.size(); ++i) {
      budget[i] = INITIAL_BUDGET;
      utility[i] = INITIAL_BUDGET;
    }

    for (int t = 0; t < NUM_DAYS; ++t) {
      // draw each player's value; get their bid
      double[] values = new double[bidders.size()];
      double[] bids = new double[bidders.size()];

      for (int i = 0; i < bidders.size(); i++) {
        values[i] = ER(MAX_VALUE);
        bids[i] = bidders.get(i).getBid(values[i]);
        assert !Double.isNaN(bids[i]) : bidders.get(i).getClass().getSimpleName() + ": NaN bid";
        assert bids[i] >= 0 : bidders.get(i).getClass().getSimpleName() + ": negative bid: " + bids[i];
        if (Double.isNaN(bids[i]) || bids[i] < 0) {
          // When running without assertions, keep going in a sane way
          bids[i] = 0;
        }
        if (bids[i] * RATES[0] > budget[i]) {
          if (Auctioneer.class.desiredAssertionStatus()) {
            System.err.println(bidders.get(i).getClass().getSimpleName() + ": bid (" + bids[i]
                + ") potentially above budget (" + budget[i] + ") at maximum rate (" + RATES[0] + ")");
          }
          bids[i] = budget[i] / RATES[0];
        }
      }

      // argsort bids
      Integer[] idx = new Integer[bidders.size()];
      for (int i = 0; i < bidders.size(); i++) {
        idx[i] = i;
      }
      Collections.shuffle(Arrays.asList(idx));
      Arrays.sort(idx, new Comparator<Integer>() {
        @Override
        public int compare(final Integer o1, final Integer o2) {
          return Double.compare(bids[o2], bids[o1]);
        }
      });

      // determine winners
      List<Double> winning_bids = Collections.unmodifiableList(
          IntStream.range(0, RATES.length).boxed().map(i -> bids[idx[i]]).collect(Collectors.toList()));

      // give feedback
      for (int i = 0; i < bidders.size(); i++) {
        try {
          if (i < RATES.length) {
            double paid = RATES[i] * bids[idx[i + 1]];

            bidders.get(idx[i]).addResults(winning_bids, i, paid);

            utility[idx[i]] += RATES[i] * values[idx[i]] - paid;
            budget[idx[i]] -= paid;
          } else {
            bidders.get(idx[i]).addResults(winning_bids, -1, Double.NaN);
          }
        } catch (Exception e) {
          // welp, you just don't get updated I guess
          System.err.println(bidders.get(idx[i]).toString() + "\t" + e);
        }
      }
    }

    // Uncomment this if you are suppressing output.
    // System.setOut(stdout);
    return utility;
  }

  public static final AuctionConfig defaultConfig;

  static {
    defaultConfig = new AuctionConfig(new double[] { .050, .035, .030, .015, .015, .015, .015, .015, .015, .015 }, 500,
        10000, 200);
  }

  public static void main(String[] args) throws java.io.FileNotFoundException {
    if (args.length < 1) {
      throw new RuntimeException("Expected filename of strategies as first argument");
    }
    final BufferedReader namesFile = new BufferedReader(new FileReader(args[0]));
    final List<String> strategyNames = namesFile.lines().map(s -> String.format("Bidder_%s", s))
        .collect(Collectors.toList());
    final Auctioneer withStrategies = new Auctioneer(strategyNames);
    final int N = strategyNames.size();

    double[] res = withStrategies.oneEachTrials(100, defaultConfig);
    System.out.println("netID,score");
    for (int i = 0; i < N; ++i) {
      System.out.println(strategyNames.get(i).substring(7) + "," + Double.toString(res[i]));
    }
  }
}
