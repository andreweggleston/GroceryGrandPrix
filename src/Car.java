import javax.swing.Icon;

/**
 * Car represents a car in the game, both on and off the track.
 */
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
        stats.incrementTopSpeed();
    }

    public void decrementTopSpeed() {
        stats.decrementTopSpeed();
    }

    public void incrementAcceleration() {
       stats.incrementAcceleration();
    }

    public void decrementAcceleration() {
        stats.decrementAcceleration();
    }

    public void incrementHandling() {
        stats.incrementHandling();
    }

    public void decrementHandling() {
        stats.decrementHandling();
    }

    public int getTopSpeed() {
        return stats.topSpeed.getStatNumeral();
    }

    public int getAcceleration() {
        return stats.acceleration.getStatNumeral();
    }

    public int getHandling() {
        return stats.handling.getStatNumeral();
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

}
