//Andrew Eggleston a_eggleston1
package shared;

/**
 * Car represents a car on the track
 * Car has 3 final fields: a String representing the name of its sprite/preview image, a {@link CarStats} object holding
 * its stats, and a boolean representing whether it's a player.
 * Car also has 4 mutable fields: momentum representing how fast the Car is traveling, a lastNode the last turn the Car
 * has passed, a distanceFromLast being the Car's distance from that last node, and a goalNode which also happens to be
 * the Car's starting node.
 */
public class Car {
    private final String imageName;

    private final CarStats stats;

    private final boolean isPlayer;

    private double momentum;
    private double distanceFromLast;

    private Node lastNode;
    private Node goalNode;

    /**
     * Constructor
     *
     * @param imageName name of the image Car will be painted with
     * @param stats     a {@link CarStats} to be the Car's stats
     * @param goalNode  the starting node of the Car.
     * @param isPlayer  whether the Car is the player Car or a Computer Car
     */
    public Car(String imageName, CarStats stats, Node goalNode, boolean isPlayer) {
        this.imageName = imageName;
        this.stats = stats;
        this.goalNode = goalNode;
        this.momentum = 0.0;
        this.distanceFromLast = 0.0;
        this.lastNode = goalNode;
        this.isPlayer = isPlayer;
    }

    /**
     * Default Constructor. Should be unused.
     */
    public Car() {
        this.imageName = "Uninitialized";
        this.stats = new CarStats(0, 0, 0);
        Node n = new Node(0, 0);
        this.goalNode = n;
        this.lastNode = n;
        this.momentum = 0.0;
        this.distanceFromLast = 0.0;
        this.isPlayer = false;
    }

    /**
     * Drive moves a car forward an amount based on a time passed 'ms'
     *
     * @param ms the amount of in-game time that has passed
     * @return whether the car has made a full loop and returned to its starting position
     */
    public boolean drive(double ms) {
        boolean aboutToFinish = lastNode.next().equals(goalNode);
        momentum = Math.min(stats.topSpeed(), momentum + stats.acceleration());
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
            double turnAngle = Math.toDegrees(Math.abs(lastNode.next().getAngle() - lastNode.getAngle())); //probably correct
            if (turnAngle > 180.0) turnAngle = 360.0 - turnAngle;
            double angleScale = turnAngle / 120.0;
            double spinout = stats.handling() * angleScale;

            if (Math.random() < spinout) { //spin out: set momentum to 0
                momentum = 0.0;
            } else { //turns decrease momentum more when high top speed and low handling
                momentum -= stats.handling() * momentum * Math.toRadians(turnAngle);
            }

            distanceFromLast -= lastNode.distanceToNext();
            lastNode = lastNode.next();
        }
        return false;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    /**
     * Sets the {@link Car}'s {@link CarStats} from an int[] that should have length 3.
     *
     * @param statsArray an array of 3 ints
     */
    public void setAllStats(int[] statsArray) {
        assert statsArray.length == 3;
        setAllStats(statsArray[0], statsArray[1], statsArray[2]);
    }

    /**
     * Sets the {@link Car}'s {@link CarStats} from 3 ints
     * @param topSpeed new top speed stat
     * @param acceleration new acceleration stat
     * @param handling new handling stat
     */
    public void setAllStats(int topSpeed, int acceleration, int handling) {
        stats.setTopSpeedStat(topSpeed);
        stats.setAccelerationStat(acceleration);
        stats.setHandlingStat(handling);
    }

    public int getTopSpeed() {
        return stats.getTopSpeedNumeral();
    }

    public int getAcceleration() {
        return stats.getAccelerationNumeral();
    }

    public int getHandling() {
        return stats.getHandlingNumeral();
    }

    public String getImageName() {
        return imageName;
    }

    public void setGoalNode(Node goalNode) {
        this.goalNode = goalNode;
        this.lastNode = goalNode;
    }

    //following accessors added by Naomi
    public double getDistanceFromLast() {
        return distanceFromLast;
    }

    public Node getLastNode() {
        return lastNode;
    }

    @Override
    public String toString() {
        return String.format("Car{\n" +
                "\timageName='%s'\n" +
                "\tstats=%s\n" +
                "\tisPlayer=%b\n" +
                "\tmomentum=%f\n" +
                "\tdistanceFromLast=%f\n" +
                "\tlastNode=%s\n" +
                "\tgoalNode=%s\n" +
                '}',
                imageName,  stats, isPlayer, momentum, distanceFromLast, lastNode, goalNode);
    }
}
