import java.awt.geom.Point2D;

public class Node {
    Point2D.Double coord = new Point2D.Double();
    double angle;
    double distance;
    Node next;

    public Node(int x, int y){
        this.coord.x = x;
        this.coord.y = y;
        setNext(this);
    }
    public Node(int x, int y, Node n){
        this.coord.x = x;
        this.coord.y = y;
        setNext(n);
    }
    public Node next(){
        return next;
    }
    public void setNext(Node n){
        next = n;
        distance = coord.distance(n.getCoord());
        angle = Math.atan2(n.getCoord().getY() - this.getCoord().getY(), n.getCoord().getX() - this.getCoord().getX());
    }
    public double getAngle(){
        return angle;
    }
    public double distanceToNext(){
        return distance;
    }
    public Point2D.Double getCoord(){
        return coord;
    }

}
