//Lee Snyder

import java.awt.geom.Point2D;

/**
 * Node represents one turn on the track. It is effectively one node in a "doubly linked list" in which each element of
 * the list has a reference to a "previous" node and a "next" node. <p>
 * Node, since it is a turn, has an angle which points to the next node, and a distance to that node.
 * These values are used by a {@link Car} when it is driving
 */
public class Node {
    private Point2D.Double coord = new Point2D.Double();
    private double angle;
    private double distance;
    private Node next;
    private Node prev;

    public Node(double x, double y){
        this.coord.x = x;
        this.coord.y = y;
        setNext(this);
        prev = this;
    }
    public Node(double x, double y, Node n, Node l){
        this.coord.x = x;
        this.coord.y = y;
        setNext(n);
        prev = l;
    }
    public Node next(){
        return next;
    }
    public int getQuad(int width, int height){ // 1 = top left, 2 = top right, 3 = bottom right, 4 = bottom left
        int quad;
        if (this.getCoord().getX() >= width/2) {
            if (this.getCoord().getY() >= height/2) {
                quad = 3;
            } else {
                quad = 2;
            }
        } else {
            if (this.getCoord().getY() >= height/2) {
                quad = 4;
            } else {
                quad = 1;
            }
        }
        return quad;
    }
    public void setPrev(Node p){
        this.prev = p;
    }
    public void setNext(Node n){
        n.setPrev(this);
        next = n;
        distance = coord.distance(n.getCoord());
        angle = Math.atan2(n.getCoord().getY() - this.getCoord().getY(), n.getCoord().getX() - this.getCoord().getX());

    }
    public double getAngle(){
        return angle;
    }
    public double turn(){
        return Math.abs(prev.getAngle() - this.getAngle());
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
