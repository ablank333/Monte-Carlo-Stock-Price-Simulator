public class Stock {

    // core stock params - set once and never modified
    private final double initialPrice;
    private final double expectedReturn;
    private final double volatility;

    // creates stock with a given price, return, and volatility
    public Stock(double initialPrice, double expectedReturn, double volatility){
        this.initialPrice = initialPrice;
        this.expectedReturn = expectedReturn;
        this.volatility = volatility;
    }

    // returns the starting stock price
    public double getInitialPrice(){
        return initialPrice;
    }

    // returns the annualized expected return
    public double getExpectedReturn(){
        return expectedReturn;
    }

    // returns the annualized volatility
    public double getVolatility(){
        return volatility;
    }

    // formats stock's data as readable string
    @Override
    public String toString(){
        return String.format("Stock[price=$%.2f, expectedReturn=%.2f%%, volatility=%.2f%%]",
                initialPrice, expectedReturn * 100, volatility * 100);
    }
}
