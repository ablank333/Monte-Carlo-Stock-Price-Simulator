import io.javalin.Javalin;

public class Server {
    public static void main(String[] args) {

        // creates and starts a web server listening on port 8080
        Javalin app = Javalin.create(config ->
        {config.staticFiles.add("/public");}).start(8080);

        app.get("/simulate", ctx -> {
          String ticker = ctx.queryParam("ticker");

          if (ticker == null || ticker.isBlank()){
            ctx.status(400);
            ctx.json(new ErrorResponse("Please enter a stock ticker."));
            return;
          }
          try {
            HistoricalDataLoader loader = new HistoricalDataLoader(ticker);
            String json = loader.fetchJson();
            double[] prices = loader.extractClosingPrices(json);

            double initialPrice = prices[prices.length - 1];
            double expectedReturn = loader.getAnnualizedReturn(prices);
            double volatility = loader.getAnnualizedVolatility(prices);

            Stock stock = new Stock(initialPrice, expectedReturn, volatility);
            Simulator simulator = new Simulator(stock, 252);
            double[] results = simulator.runSimulations(10000);
            Statistics stats = new Statistics(results, initialPrice);

            SimulationResponse response = new SimulationResponse();
            response.ticker = ticker;
            response.initialPrice = initialPrice;
            response.expectedReturn = expectedReturn;
            response.volatility = volatility;
            response.meanPrice = stats.getMean();
            response.medianPrice = stats.getMedian();
            response.fifthPercentile = stats.getPercentile(0.05);
            response.ninetyFifthPercentile = stats.getPercentile(0.95);
            response.probabilityOfGain = stats.getProbabilityOfGain();
            response.valueAtRisk = stats.getValueAtRisk();
            response.allResults = results;

            ctx.json(response);
          } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorResponse(e.getMessage()));
          }
        });
    }
}
