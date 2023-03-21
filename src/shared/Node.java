package shared;

import java.awt.geom.Point2D;

public class Node {
    private Point2D.Double coord = new Point2D.Double();
    private double angle;
    private double distance;
    private Node next;

    public Node(double x, double y){
        this.coord.x = x;
        this.coord.y = y;
        setNext(this);
    }
    public Node(double x, double y, Node n){
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

    public String toString(){
        return "X: " + this.coord.getX() + " Y: " + this.coord.getY() + " Distance: " + distance + " Angle: " + angle;
    }

    @Override
    public int hashCode() {
        return coord.hashCode();
    }
}
