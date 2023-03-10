import javax.swing.Icon;

public class Car {
    private Icon icon;

    private CarStats stats;

    private double momentum;
    private double distanceFromLast;

    private Node lastNode;
    private Node goalNode;

    public Car(Icon icon, int topSpeed, int acceleration, int handling, Node goalNode) {
        this.icon = icon;
        this.stats = new CarStats(topSpeed, acceleration, handling);
        this.goalNode = goalNode;
        this.momentum = 0.0;
        this.distanceFromLast = 0.0;
        this.lastNode = goalNode;
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
        return true;
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
    public void setMomentum(double momentum) {
        this.momentum = momentum;
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
    public void setLastNode(Node lastNode) {
        this.lastNode = lastNode;
    }
    public Node getGoalNode() {
        return goalNode;
    }
    public void setGoalNode(Node goalNode) {
        this.goalNode = goalNode;
    }

    //TODO explain reasoning for using a private static inner class
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

        public double topSpeed() {
            return topSpeed.statNumeral * TOP_SPEED_SCALE_FACTOR;
        }

        public double acceleration() {
            return acceleration.statNumeral * ACCELERATION_SCALE_FACTOR;
        }

        public double handling() {
            return (10 - handling.statNumeral) / 10.0 * HANDLING_SCALE_FACTOR + MIN_HANDLING_FACTOR;
        }

        /**
         * Stat enumerates the integer values [1,10]. We use an enumerator because we want the rigorous type
         * definition provided by an enum. This guarantees that a Stat won't exist with values outside the range
         * provided by the enum.
         * We use an enum--instead of a class, even if we want methods--because a class would not allow for this
         * behavior without constant checking of the actual integer value of the stat. With an enum those integer values
         * are set once when the program is compiled and then cannot be touched during runtime.
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
