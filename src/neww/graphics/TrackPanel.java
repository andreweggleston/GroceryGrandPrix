package neww.graphics;

import shared.Car;
import shared.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TrackPanel extends JPanel {

    List<Line2D.Double> trackSegments;
    List<Car> cars;
    BasicStroke roadStroke;

    public TrackPanel(Node head, List<Car> cars) {
        trackSegments = new ArrayList<>();
//        this.setPreferredSize(new Dimension(1000, 800));
        this.setVisible(true);
        Node temp = head;
        do {
            Point2D p1 = temp.getCoord();
            temp = temp.next();
            Point2D p2 = temp.getCoord();
            trackSegments.add(new Line2D.Double(p1, p2));
        } while (temp != head);
        this.cars = cars;
        roadStroke = new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    }

    private static Point2D calculateCoord(Car car) {
        Node lastNode = car.getLastNode();
        double xOffset = car.getDistanceFromLast() * Math.cos(lastNode.getAngle());
        double yOffset = car.getDistanceFromLast() * Math.sin(lastNode.getAngle());
        return new Point2D.Double(lastNode.getCoord().x + xOffset, lastNode.getCoord().y + yOffset);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints antiAliasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(antiAliasing);
        g2.setStroke(roadStroke);
        for (Line2D.Double segment : trackSegments) {
            g2.draw(segment);
        }
        for (Car car : cars) {
            Point2D carCoord = calculateCoord(car);
            BufferedImage image = car.getImage();
            final double radians = car.getLastNode().getAngle() + Math.PI/2;
            final double sine = Math.abs(Math.sin(radians));
            final double cosine = Math.abs(Math.cos(radians));
            final double width = Math.floor(image.getWidth() * cosine + image.getHeight() * sine);
            final double height = Math.floor(image.getHeight() * cosine + image.getWidth() * sine);
            final BufferedImage rotatedImage = new BufferedImage((int) width, (int) height, image.getType());
            final AffineTransform at = new AffineTransform();
            at.translate(width / 2, height / 2);
            at.rotate(radians, 0, 0);
            at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
            final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            rotateOp.filter(image, rotatedImage);
            g2.drawImage(rotatedImage, (int) (carCoord.getX()-(width/2)), (int) (carCoord.getY()-height/2), null);
        }

    }
}
