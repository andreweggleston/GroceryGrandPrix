package graphics;

import shared.Car;
import shared.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TrackPanel is a JPanel responsible for painting the track and the Cars on the track.
 * It keeps a reference to all of the {@link Car}s in the game as an array, and creates its own List of {@link Line2D}s
 * which represent the sections of road between each node.
 * TrackPanel also has an array of each Car's images,
 * which are read from the filesystem using {@link Car#getImageName()}.
 */
public class TrackPanel extends JPanel {

    private List<Line2D.Double> trackSegments;
    private Car[] cars;
    private BufferedImage[] carImages;
    private BasicStroke roadStroke;
    private final int carOffset = 8;

    /**
     * Generates a new TrackPanel, reading each Car's imageName and reading the sprites with the corresponding name in
     * as a {@link BufferedImage}.
     * This constructor creates the trackSegments as well as loads the sprite images for each car.
     * @param head the start of the track.
     * @param cars list of cars to render in the panel
     */
    public TrackPanel(Node head, List<Car> cars) {
        super(new BorderLayout(), true);
        trackSegments = new ArrayList<>();
        this.setVisible(true);

        //Create Track segments
        Node temp = head;
        do {
            Point2D p1 = temp.getCoord();
            temp = temp.next();
            Point2D p2 = temp.getCoord();
            trackSegments.add(new Line2D.Double(p1, p2));
        } while (temp != head);

        //Load Car sprites
        this.cars = new Car[cars.size()];
        this.carImages = new BufferedImage[cars.size()];
        for (int i = 0; i < cars.size(); i++) {
            this.cars[i] = cars.get(i);
            try {
                BufferedImage image = ImageIO.read(new File("assets/sprites/sprite_" + this.cars[i].getImageName() + "_size_0.png"));
                int scaleX = (int)(image.getWidth()*.10);
                int scaleY = (int)(image.getHeight()*.10);
                Image img = image.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
                image = new BufferedImage(scaleX, scaleY, BufferedImage.TYPE_INT_ARGB);
                image.getGraphics().drawImage(img, 0, 0, null);
                this.carImages[i] = image;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        roadStroke = new BasicStroke(80, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    }

    /**
     * Calculates the Point2D at which Car is currently at.
     * Uses the location of the Car's last node as well as the Car's distance from that node.
     *
     * THIS IS STATIC BECAUSE IT DOES NOT USE ANY STATE IN TRACKPANEL. IT COULD BE A PUBLIC METHOD IN A UTIL CLASS,
     * BUT IT IS UNUSED ANYWHERE ELSE.
     * @param car car to calculate position of
     * @return Point2D representing the on-screen position of that car.
     */
    private static Point2D calculateCoord(Car car) {
        Node lastNode = car.getLastNode();
        double xOffset = car.getDistanceFromLast() * Math.cos(lastNode.getAngle());
        double yOffset = car.getDistanceFromLast() * Math.sin(lastNode.getAngle());
        return new Point2D.Double(lastNode.getCoord().x + xOffset, lastNode.getCoord().y + yOffset);
    }

    /**
     * Paint method
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints antiAliasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(antiAliasing);

        //Draw track segments
        g2.setStroke(roadStroke);
        for (Line2D.Double segment : trackSegments) {
            g2.draw(segment);
        }

        //draw cars, offset from the center of the track
        for (int i = cars.length - 1; i >= 0; i--) {
            Car car = cars[i];
            int carOffsetModifier = 0;
            //cars.length indicates the round of the game
            switch (cars.length) {
                case 4:
                    carOffsetModifier = (i >= 2) ? i - 1 : i - 2;
                    break;
                case 3:
                    carOffsetModifier = i - 1;
                    break;
                case 2:
                    carOffsetModifier = (i == 1) ? i + 1 : i - 2;
            }

            //get the 2d position on-screen of the car
            Point2D carCoord = calculateCoord(car);
            BufferedImage image = carImages[i];

            //rotate the car image
            final double radians = car.getLastNode().getAngle() + Math.PI/2;
            final double sine = Math.sin(radians);
            final double cosine = Math.cos(radians);
            final double width = Math.floor(image.getWidth() * Math.abs(cosine) + image.getHeight() * Math.abs(sine));
            final double height = Math.floor(image.getHeight() * Math.abs(cosine) + image.getWidth() * Math.abs(sine));
            final BufferedImage rotatedImage = new BufferedImage((int) width, (int) height, image.getType());
            final AffineTransform at = new AffineTransform();
            at.translate(width / 2, height / 2);
            at.rotate(radians, 0, 0);
            at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
            final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            rotateOp.filter(image, rotatedImage);

            //paint the rotated car, at the position on the track with the offset indicated
            g2.drawImage(rotatedImage, Math.round((float)((carCoord.getX()-(width/2))+((carOffsetModifier)*carOffset*cosine))),
                    Math.round((float) ((carCoord.getY()-(height/2))+((carOffsetModifier)*carOffset*sine))), null);
        }

    }
}
