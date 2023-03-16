import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite extends JLabel{
    //constructor for animated top-down sprites
    public Sprite(Car car) {
        super();
        try {
            BufferedImage image = ImageIO.read(new File("assets/preview_" + "_size_" + ".png"));
            final double radians = Math.toRadians(car.getLastNode().getAngle());
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
            this.setPreferredSize(new Dimension(rotatedImage.getWidth(), rotatedImage.getHeight()));
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setVerticalAlignment(JLabel.CENTER);
            this.setIcon(new ImageIcon(rotatedImage));
        } catch (IOException e) {}
    }
    //constructor for preview images
    public Sprite(String type, int size) {
        super();
        try {
            BufferedImage image = ImageIO.read(new File("assets//previews/preview_" + type + "_size_" + size + ".png"));
            this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            this.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
            System.out.println("Max: " + this.getMaximumSize() + "\nPref: " + this.getPreferredSize() + "\nW: " + image.getWidth() + "\nH: " + image.getHeight());
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setVerticalAlignment(JLabel.CENTER);
            this.setIcon(new ImageIcon(image));
        } catch (IOException e) {
        }
    }
}

