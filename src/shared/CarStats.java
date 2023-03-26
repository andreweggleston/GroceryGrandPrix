//Andrew Eggleston a_eggleston1

package shared;

/**
 * CarStats class used by Car and GroceryGrandPrix that encapsulates the translation from integer stats (1 through 10)
 * to double values used by car to calculate things like momentum and handling.
 * CarStats encapsulates all code related to balancing the game -- it makes it easy to, as a developer, tune the game
 * to behave as we want it to.
 */
public class CarStats {

    private static final double TOP_SPEED_SCALE_FACTOR = 4.5;
    private static final double ACCELERATION_SCALE_FACTOR = .025;
    private static final double HANDLING_SCALE_FACTOR = 0.53;
    private static final double MIN_HANDLING_FACTOR = 0.02;
    private int topSpeed;
    private int acceleration;
    private int handling;

    public CarStats(int topSpeed, int acceleration, int handling) {
        assert (topSpeed > 0 && topSpeed <= 10) && (acceleration > 0 && acceleration <= 10) && (handling > 0 && handling <= 10);
        this.topSpeed = topSpeed;
        this.acceleration = acceleration;
        this.handling = handling;
    }

    public CarStats(int[] stats){
        this(stats[0], stats[1], stats[2]);
        if (stats.length != 3) throw new RuntimeException();
    }

    @Override
    public String toString() {
        return String.format(
                "Top Speed: %d, Acceleration %d, Handling %d",
                topSpeed,
                acceleration,
                handling
        );
    }

    /**
     * Calculates double value for top speed used by Car
     * log_10(stat*4.5)
     * Should be between ~.65 and 1.65
     * @return top speed double
     */
    public double topSpeed() {
        return Math.log10(topSpeed * TOP_SPEED_SCALE_FACTOR);
    }

    /**
     * Calculates double value for acceleration used by Car
     * Should be between .015 and .15
     * @return acceleration double
     */
    public double acceleration() {
        return Math.pow(acceleration * ACCELERATION_SCALE_FACTOR, 2);
    }

    /**
     * Calculates double value for handling used by Car
     * Should be between ~0.02 and ~0.5
     * @return handling double
     */
    public double handling() {
        return (10 - handling) / 10.0 * HANDLING_SCALE_FACTOR + MIN_HANDLING_FACTOR;
    }

    public int getTopSpeedNumeral(){
        return topSpeed;
    }

    public int getAccelerationNumeral() {
        return acceleration;
    }

    public int getHandlingNumeral() {
        return handling;
    }

    public void setTopSpeedStat(int topSpeedStat) {
        this.topSpeed = topSpeedStat;
    }

    public void setAccelerationStat(int accelerationStat) {
        this.acceleration = accelerationStat;
    }

    public void setHandlingStat(int handlingStat) {
        this.handling = handlingStat;
    }
}