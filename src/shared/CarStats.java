package shared;

/**
 * CarStats class used by Car and GroceryGrandPrix that encapsulates the translation from integer stats (1 through 10)
 * to double values used by car to calculate things like momentum and handling.
 */
public class CarStats {

    private static final double TOP_SPEED_SCALE_FACTOR = 2.5;
    private static final double ACCELERATION_SCALE_FACTOR = 1.0;
    private static final double HANDLING_SCALE_FACTOR = 0.53;
    private static final double MIN_HANDLING_FACTOR = 0.02;
    Stat topSpeed;
    Stat acceleration;
    Stat handling;

    public CarStats(int topSpeed, int acceleration, int handling) {
        assert (topSpeed > 0 && topSpeed < 10) && (acceleration > 0 && acceleration < 10) && (handling > 0 && handling < 10);
        this.topSpeed = Stat.fromInt(topSpeed);
        this.acceleration = Stat.fromInt(acceleration);
        this.handling = Stat.fromInt(handling);
    }

    @Override
    public String toString() {
        return String.format(
                "Top Speed: %d, Acceleration %d, Handling %d",
                topSpeed.statNumeral,
                acceleration.statNumeral,
                handling.statNumeral
        );
    }

    public void incrementTopSpeed() {
        topSpeed = topSpeed.increment();
    }

    public void decrementTopSpeed() {
        topSpeed = topSpeed.decrement();
    }

    public void incrementAcceleration() {
        acceleration = acceleration.increment();
    }

    public void decrementAcceleration() {
        acceleration = acceleration.decrement();
    }

    public void incrementHandling() {
        handling = handling.increment();
    }

    public void decrementHandling() {
        handling = handling.decrement();
    }

    /**
     * Calculates double value for top speed used by Car
     *
     * @return top speed double
     */
    public double topSpeed() {
        return topSpeed.statNumeral * TOP_SPEED_SCALE_FACTOR;
    }

    /**
     * Calculates double value for acceleration used by Car
     *
     * @return acceleration double
     */
    public double acceleration() {
        return acceleration.statNumeral * ACCELERATION_SCALE_FACTOR;
    }

    /**
     * Calculates double value for handling used by Car
     *
     * @return handling double
     */
    public double handling() {
        return (10 - handling.statNumeral) / 10.0 * HANDLING_SCALE_FACTOR + MIN_HANDLING_FACTOR;
    }

    /**
     * Stat enumerates the integer values [1,10]. We use an enumerator because we want the rigorous type
     * definition provided by an enum. This guarantees that a Stat won't exist with values outside the range
     * provided by the enum.
     * We use an enum--instead of a class, even if we want methods--because a class with an integer field would not
     * guarantee the int was between 1 and 10.  With an enum those integer values are set once when the program is
     * compiled and then cannot be touched during runtime.
     */
    enum Stat {
        ONE(1) {
            @Override
            public Stat decrement() {
                return ONE;
            }
        }, TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10) {
            @Override
            public Stat increment() {
                return TEN;
            }
        };

        private final int statNumeral;

        Stat(int i) {
            statNumeral = i;
        }

        public static Stat fromInt(int i) {
            return Stat.values()[i];
        }

        public int getStatNumeral() {
            return statNumeral;
        }

        public Stat increment() {
            return values()[ordinal() + 1];
        }

        public Stat decrement() {
            return values()[ordinal() - 1];
        }
    }

}