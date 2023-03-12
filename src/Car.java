import javax.swing.Icon;

public class Car {
    private Icon icon;

    private int topSpeed;
    private int acceleration;
    private int handling;

    private double momentum;
    private double distanceFromLast;

    private Node lastNode;
    private Node goalNode;

    public Car(Icon icon, int topSpeed, int acceleration, int handling, Node goalNode) {
        this.icon = icon;
        this.topSpeed = topSpeed;
        this.acceleration = acceleration;
        this.handling = handling;
        this.goalNode = goalNode;
        this.momentum = 0.0;
        this.distanceFromLast = 0.0;
        this.lastNode = goalNode;
    }

    public boolean drive(int ms) {
        boolean aboutToFinish = lastNode.next().equals(goalNode);
        momentum += Math.min(topSpeed*2.5, acceleration);
        distanceFromLast += momentum*(ms/10.0);

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
            double randomChance = spinoutChance(this.handling, this.lastNode);

            if (Math.random() < randomChance) {
                momentum = 0.0;
            }

            lastNode = lastNode.next();
            distanceFromLast -= lastNode.distanceToNext();
        }
        return false;
    }

    private static double spinoutChance(int handling, Node node) {
        double turnAngle = Math.abs(node.next().getAngle() - node.getAngle()); //TODO probably wrong
        double angleScale = turnAngle/120.0;

        double handlingScale = (0.53)*((10-handling)/10.0);
        assert handlingScale > 0.0; //dont have more than 10 handling
        double spinout = (0.02 + handlingScale)*angleScale;

        return spinout;
    }
}
