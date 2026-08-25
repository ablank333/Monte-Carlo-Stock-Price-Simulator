import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.TreeMap;

public class HistoricalDataLoader {

    private static final int TRADING_DAYS_PER_YEAR = 252;

    // API key read from environment variable
    private static final String API_KEY = System.getenv("ALPHA_VANTAGE_API_KEY");

    private final String ticker; // stores stock symbol

    // stores the ticker that the data loader will fetch data for
    public HistoricalDataLoader(String ticker){
        this.ticker = ticker;
    }

    // makes HTTP GET request to Alpha Vantage - returns raw JSON response
    // data consists of last 100 trading days of data
    public String fetchJson() throws Exception{
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY"
                + "&symbol=" + ticker
                + "&apikey=" + API_KEY;

        // creates tool used to send HTTP requests
        HttpClient client = HttpClient.newHttpClient();

        // packages URL into a request object that is ready to send
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        // sends request and waits for server's response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // returns the raw JSON text
        return response.body();
    }

    // parses raw JSON - returns closing prices ordered from oldest to newest
    public double[] extractClosingPrices(String json){
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(json, JsonObject.class);

        JsonObject timeSeries = root.getAsJsonObject("Time Series (Daily)");

        if (timeSeries == null) {
            System.out.println("Alpha Vantage raw response: " + json);
            throw new RuntimeException("No data found for this ticker. It may be invalid, or the API rate limit may have been reached.");
        }
        // TreeMap keeps entries sorted by key (date) automatically
        TreeMap<String, Double> sortedPrices = new TreeMap<>();

        for (String date : timeSeries.keySet()){
            JsonObject dayData = timeSeries.getAsJsonObject(date);
            double close = dayData.get("4. close").getAsDouble();
            sortedPrices.put(date, close);
        }

        // convert sorted values into a double[] array
        double[] prices = new double[sortedPrices.size()];
        int i = 0;
        for (double price : sortedPrices.values()){
            prices[i] = price;
            i++;
        }
        return prices;
    }

    // converts daily closing prices into daily log returns
    public double[] calculateLogReturns(double[] prices){
        double[] returns = new double[prices.length - 1];

        for (int i = 1; i < prices.length;i++){
            returns[i-1] = Math.log(prices[i] / prices[i-1]);
        }
        return returns;
    }

    // average of daily log returns
    private double getMeanReturn(double[] returns){
        double sum = 0;
        for (double r : returns){
            sum += r;
        }
        return sum / returns.length;
    }

    // standard deviation of daily log returns
    private double getStdDevReturn(double[] returns) {
        double mean = getMeanReturn(returns);
        double sumSquaredDiffs = 0;

        for (double r : returns) {
            sumSquaredDiffs += Math.pow(r - mean, 2);
        }

        double variance = sumSquaredDiffs / returns.length;
        return Math.sqrt(variance);
    }

    // annualized expected return - calculated from historical prices
    public double getAnnualizedReturn(double[] prices){
        double[] returns = calculateLogReturns(prices);
        double dailyMu = getMeanReturn(returns);
        return dailyMu * TRADING_DAYS_PER_YEAR;
    }

    // annualized volatility - calculated from historical prices
    public double getAnnualizedVolatility(double[] prices){
        double[] returns = calculateLogReturns(prices);
        double dailySigma = getStdDevReturn(returns);
        return dailySigma * Math.sqrt(TRADING_DAYS_PER_YEAR);
    }

}
