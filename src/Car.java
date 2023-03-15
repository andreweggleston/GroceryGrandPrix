import javax.swing.Icon;

public class Car {
    private Icon icon;

    private CarStats stats;

    private boolean isPlayer;

    private double momentum;
    private double distanceFromLast;

    private Node lastNode;
    private Node goalNode;

    public Car(Icon icon, int topSpeed, int acceleration, int handling, Node goalNode, boolean isPlayer) {
        this.icon = icon;
        this.stats = new CarStats(topSpeed, acceleration, handling);
        this.goalNode = goalNode;
        this.momentum = 0.0;
        this.distanceFromLast = 0.0;
        this.lastNode = goalNode;
        this.isPlayer = isPlayer;
    }

    public boolean drive(int ms) {
        boolean aboutToFinish = lastNode.next().equals(goalNode);
        momentum += Math.min(stats.topSpeed(), stats.acceleration());
        distanceFromLast += momentum * (ms / 10.0);

        //if the car has passed a turn
        if (distanceFromLast > lastNode.distanceToNext()) {

            //if the turn the car took was their starting node
            if (aboutToFinish) {
                lastNode = lastNode.next();
                distanceFromLast = 0.0;
                return true;
            }

            //do the handling "spin out" logic
            //1 handling is 50% spin out, 10 handling is 2% spin out
            double turnAngle = Math.abs(lastNode.next().getAngle() - lastNode.getAngle()); //TODO probably wrong
            double angleScale = turnAngle / 120.0; //TODO MAGIC NUMBER
            double spinout = stats.handling() * angleScale;

            if (Math.random() < spinout) {
                momentum = 0.0;
            }

            lastNode = lastNode.next();
            distanceFromLast -= lastNode.distanceToNext();
        }
        return false;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void incrementTopSpeed() {
        this.stats.topSpeed = stats.topSpeed.increment();
    }

    public void decrementTopSpeed() {
        this.stats.topSpeed = stats.topSpeed.decrement();
    }

    public void incrementAcceleration() {
        this.stats.acceleration = stats.acceleration.increment();
    }

    public void decrementAcceleration() {
        this.stats.acceleration = stats.acceleration.decrement();
    }

    public void incrementHandling() {
        this.stats.handling = stats.handling.increment();
    }

    public void decrementHandling() {
        this.stats.handling = stats.handling.decrement();
    }

    public int getTopSpeed() {
        return stats.topSpeed.statNumeral;
    }

    public int getAcceleration() {
        return stats.acceleration.statNumeral;
    }

    public int getHandling() {
        return stats.handling.statNumeral;
    }

    //following accessors added by Naomi
    public Icon getIcon() {
        return icon;
    }
    public double getMomentum() {
        return momentum;
    }
    public double getDistanceFromLast() {
        return distanceFromLast;
    }
    public void setDistanceFromLast(double distanceFromLast) {
        this.distanceFromLast = distanceFromLast;
    }
    public Node getLastNode() {
        return lastNode;
    }
    public Node getGoalNode() {
        return goalNode;
    }

    //TODO explain reasoning for using a private static inner class

    /**
     * CarStats is a private inner class of Car that encapsulates the translation from integer stats (1 through 10) to
     * double values used by car to calculate things like momentum and handling.
     * The purpose for it being a private inner class is to prevent git conflicts when two people are working on Car
     * code at the same time, as well as to keep all static final "factors" as a member of CarStats, not car.
     */
    private static class CarStats {


        Stat topSpeed;
        Stat acceleration;
        Stat handling;

        private static final double TOP_SPEED_SCALE_FACTOR = 2.5;
        private static final double ACCELERATION_SCALE_FACTOR = 1.0;
        private static final double HANDLING_SCALE_FACTOR = 0.53;
        private static final double MIN_HANDLING_FACTOR = 0.02;

        public CarStats(int topSpeed, int acceleration, int handling) {
            assert (topSpeed > 0 && topSpeed < 10) && (acceleration > 0 && acceleration < 10) && (handling > 0 && handling < 10);
            this.topSpeed = Stat.fromInt(topSpeed);
            this.acceleration = Stat.fromInt(acceleration);
            this.handling = Stat.fromInt(handling);
        }

        /**
         * Calculates double value for top speed used by Car
         * @return top speed double
         */
        public double topSpeed() {
            return topSpeed.statNumeral * TOP_SPEED_SCALE_FACTOR;
        }

        /**
         * Calculates double value for acceleration used by Car
         * @return acceleration double
         */
        public double acceleration() {
            return acceleration.statNumeral * ACCELERATION_SCALE_FACTOR;
        }

        /**
         * Calculates double value for handling used by Car
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
            },
            TWO(2),
            THREE(3),
            FOUR(4),
            FIVE(5),
            SIX(6),
            SEVEN(7),
            EIGHT(8),
            NINE(9),
            TEN(10) {
                @Override
                public Stat increment() {
                    return TEN;
                }
            };

            private final int statNumeral;

            Stat(int i) {
                statNumeral = i;
            }

            public Stat increment() {
                return values()[ordinal() + 1];
            }

            public Stat decrement() {
                return values()[ordinal() - 1];
            }

            public static Stat fromInt(int i) {
                return Stat.values()[i];
            }
        }

    }

}
