import java.util.Arrays;

public class Statistics {

    private final double[] results;  // raw simulation results
    private final double initialPrice; // stock's starting price

    public Statistics(double[] results, double initialPrice){
        this.results = results;
        this.initialPrice = initialPrice;
    }

    // return average of all simulated final prices
    public double getMean(){
        double sum = 0;
        for (double price : results){
            sum += price;
        }
        return sum/results.length;
    }

    // return median of all simulated final prices
    public double getMedian(){
        double[] sorted = results.clone();
        Arrays.sort(sorted);

        int middle = sorted.length/2;
        if (sorted.length % 2 == 0){
            return (sorted[middle-1] + sorted[middle])/2.0;
        }
        else {
            return sorted[middle];
        }
    }

    // return value at given percentile (e.g. 0.05 for 5th percentile)
    public double getPercentile(double percentile){
        double[] sorted = results.clone();
        Arrays.sort(sorted);

        int index = (int) (percentile * sorted.length);
        return sorted[index];
    }

    // return fraction of simulations that ended above the initial price
    public double getProbabilityOfGain(){
        int count = 0;
        for (double price : results){
            if (price > initialPrice){
                count++;
            }
        }
        return (double) count / results.length;
    }

    // return 95% Value at Risk - potential loss in bad scenario
    public double getValueAtRisk(){
        double fifthPercentile = getPercentile(0.05);
        return initialPrice - fifthPercentile;
    }
}
