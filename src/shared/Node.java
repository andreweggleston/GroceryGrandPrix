package shared;

import java.awt.geom.Point2D;

public class Node {
    private Point2D.Double coord = new Point2D.Double();
    private double angle;
    private double distance;
    private Node next;
    private int trackX;
    private int trackY;

    public Node(double x, double y, int trackX, int trackY){
        this.coord.x = x;
        this.coord.y = y;
        setNext(this);
    }
    public Node(double x, double y, Node n, int trackX, int trackY){
        this.coord.x = x;
        this.coord.y = y;
        setNext(n);
    }
    public Node next(){
        return next;
    }
    public int getQuad(){ // 1 = top left, 2 = top right, 3 = bottom right, 4 = bottom left
        int quad;
        if (this.getCoord().getX() >= 600) {
            if (this.getCoord().getY() >= 450) {
                quad = 3;
            } else {
                quad = 2;
            }
        } else {
            if (this.getCoord().getY() >= 450) {
                quad = 4;
            } else {
                quad = 1;
            }
        }
        return quad;
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
        return "X: " + this.coord.getX() + " Y: " + this.coord.getY() + " Distance: " + distance + " Angle: " + angle + " Quad: " + this.getQuad();
    }

    @Override
    public int hashCode() {
        return coord.hashCode();
    }
}
