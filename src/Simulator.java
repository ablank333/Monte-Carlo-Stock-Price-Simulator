public class Simulator {

    private static final double ONE_DAY = 1;
    private static final double TRADING_DAYS_PER_YEAR = 252;
    private final Stock stock; // stock being simulated
    private final int days; // number of days
    private final RandomGenerator randomGenerator; // random number

    // sets up simulator for given stock over given time period
    public Simulator(Stock stock, int days){
        this.stock = stock;
        this.days = days;
        randomGenerator = new RandomGenerator();
    }

    // simulates one possible price path using Geometric Brownian Motion
    // returns final price
    public double simulateOnePath(){
        double price = stock.getInitialPrice();
        double mu = stock.getExpectedReturn();
        double sigma = stock.getVolatility();

        double dt = ONE_DAY/TRADING_DAYS_PER_YEAR;

        for (int day = 1; day <= days; day++){
            double z = randomGenerator.nextStandardNormal();

            double drift = (mu - 0.5 * sigma * sigma) * dt;
            double shock = sigma * Math.sqrt(dt) * z;

            price = price * Math.exp(drift+shock);
        }

        return price;
    }

    // runs simulation many times and returns array of final prices
    public double[] runSimulations(int numSimulations){
        double[] finalPrices = new double[numSimulations];

        for (int i = 0; i < numSimulations; i++){
            finalPrices[i] = simulateOnePath();
        }

        return finalPrices;
    }
}
