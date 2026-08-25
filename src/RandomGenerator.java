import java.util.Random;

public class RandomGenerator {

    private final Random random; // random number

    // initialize the random number generator
    public RandomGenerator(){
        random = new Random();
    }

    // Box-Muller transform - returns one standard normal random number
    public double nextStandardNormal(){
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();

        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);

        return z;
    }
}
