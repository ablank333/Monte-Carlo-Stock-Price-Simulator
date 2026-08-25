import java.util.Scanner;

public class Main {

    private static final int TRADING_DAYS_PER_YEAR = 252;

    public static void main(String[] args) throws Exception {

        // scans user input of stock ticker
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter stock ticker: ");
        String ticker = scanner.nextLine();

        // fetch and analyze historical data of past 100 trading days
        HistoricalDataLoader loader = new HistoricalDataLoader(ticker);
        String json = loader.fetchJson();
        double[] prices = loader.extractClosingPrices(json);

        // calculates starting price, expected return, volatility
        double initialPrice = prices[prices.length-1];
        double expectedReturn = loader.getAnnualizedReturn(prices);
        double volatility = loader.getAnnualizedVolatility(prices);

        int days = 252;
        int numSimulations = 10000;

        // create stock using above values
        Stock stock = new Stock(initialPrice, expectedReturn, volatility);
        System.out.println();
        System.out.println(stock);

        // create simulator using stock and days
        Simulator simulator = new Simulator(stock, days);

        // run many simulations
        double[] results = simulator.runSimulations(numSimulations);

        // analyze results with statistics
        Statistics stats = new Statistics(results, initialPrice);

        System.out.println();
        System.out.printf("RESULTS (%d trading days / ~%.1f years from today)%n",
        days, days / (double) TRADING_DAYS_PER_YEAR);
        System.out.printf("Expected Price:      $%.2f%n",
                stats.getMean());
        System.out.printf("Median Price:        $%.2f%n",
                stats.getMedian());
        System.out.printf("5th Percentile:      $%.2f%n",
                stats.getPercentile(0.05));
        System.out.printf("95th Percentile:     $%.2f%n",
                stats.getPercentile(0.95));
        System.out.printf("Probability of Gain: %.1f%%%n",
                stats.getProbabilityOfGain()*100);
        System.out.printf("95%% VaR:             $%.2f%n",
                stats.getValueAtRisk());
    }
}
