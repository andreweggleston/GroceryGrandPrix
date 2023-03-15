import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GUI extends JComponent implements MouseListener {
    private Color backgroundColor;
    private Color foregroundColor;
    private ArrayList<JPanel> previews;
    private JPanel previewBar;
    private JPanel center;
    private JPanel track;
    private JPanel southButtons;
    private ArrayList<Line2D.Double> trackSegments;
    private ArrayList<Ellipse2D.Double> trackJoints;
    boolean draw;

    public GUI(Color backgroundColor, JButton[] buttons) {
        super();
        JFrame frame = new JFrame("GroceryGrandPrix");
        draw = true;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = Color.lightGray;
        frame.setBackground(backgroundColor);
        frame.setForeground(foregroundColor);
        frame.getContentPane();

        setLayout(new BorderLayout());

        previews = new ArrayList<JPanel>();
        previewBar = new JPanel();
        previewBar.setLayout(new BoxLayout(previewBar, BoxLayout.X_AXIS));
        previewBar.setBackground(backgroundColor);

        center = new JPanel();
        //center.setLayout(null);
        center.setPreferredSize(new Dimension(900, 540));
        center.setBackground(backgroundColor);
        this.playerMenu(0, 20);
        frame.add(this);
        frame.add(previewBar, BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void playerMenu(int round, int budget /* , Car playerCar*/) {
        JPanel menu = new JPanel();
        createPreview(0);
        //center.add(menu);
    }

    public void createPreview(int car) {
        previews.add(car, new JPanel(new BorderLayout()));
        previews.get(car).setPreferredSize(new Dimension(150, 150));
        previews.get(car).setMinimumSize(new Dimension(150, 150));
        previews.get(car).setMaximumSize(new Dimension(150, 150));
        previews.get(car).setBackground(foregroundColor);

        JPanel placeholderForJIcon = new JPanel();
        placeholderForJIcon.setBackground(Color.WHITE);
        placeholderForJIcon.setPreferredSize(new Dimension(120, 80));
        placeholderForJIcon.setMaximumSize(new Dimension(120, 80));

        JPanel speedBar = new JPanel();
        speedBar.setBackground(Color.MAGENTA);

        JPanel accelerationBar = new JPanel();
        accelerationBar.setBackground(Color.CYAN);

        JPanel handlingBar = new JPanel();
        handlingBar.setBackground(Color.GREEN);

        JPanel statsDisplay = new JPanel();
        statsDisplay.setLayout(new GridLayout(3, 1, 10, 10));
        statsDisplay.setBackground(Color.lightGray);
        statsDisplay.setPreferredSize(new Dimension(120, 34));
        statsDisplay.setMinimumSize(new Dimension(120, 34));
        statsDisplay.setMaximumSize(new Dimension(120, 34));
        statsDisplay.add(speedBar);
        statsDisplay.add(accelerationBar);
        statsDisplay.add(handlingBar);


        JPanel previewCenter = new JPanel();
        previewCenter.setBackground(Color.lightGray);
        previewCenter.setLayout(new BoxLayout(previewCenter, BoxLayout.Y_AXIS));
        previewCenter.add(placeholderForJIcon);
        previewCenter.add(new Box.Filler((new Dimension(150, 15)), (new Dimension(150, 15)), (new Dimension(150, 15))));
        previewCenter.add(statsDisplay);

        previews.get(car).add(new Box.Filler((new Dimension(120, 15)), (new Dimension(120, 15)), (new Dimension(120, 15))), BorderLayout.NORTH);
        previews.get(car).add(previewCenter, BorderLayout.CENTER);
        previews.get(car).add(new Box.Filler((new Dimension(15, 130)), (new Dimension(15, 130)), (new Dimension(15, 130))), BorderLayout.WEST);
        previews.get(car).add(new Box.Filler((new Dimension(15, 130)), (new Dimension(15, 130)), (new Dimension(15, 130))), BorderLayout.EAST);
        previews.get(car).add(new Box.Filler((new Dimension(150, 15)), (new Dimension(150, 15)), (new Dimension(150, 15))), BorderLayout.SOUTH);
        previewBar.add(previews.get(car), car);
    }

    public void buildSegments(Node head) {

        trackSegments = new ArrayList<Line2D.Double>();
        Node temp = head;
        do {
            Point2D p1 = temp.getCoord();
            temp = temp.next();
            Point2D p2 = temp.getCoord();
            trackSegments.add(new Line2D.Double(p1, p2));
        } while (temp != head);
        int i = 0;
        for (Line2D segment : trackSegments) {
            i++;
            System.out.println(i + ":\n" + segment.toString() + "\n" + segment.getP1() + "\n " + segment.getP2() + "\n");
        }
        track = new Track(trackSegments);
    }

    public void drawTrack() {
        center.add(track);
        //track.repaint();
    }

    public void showWin() {
    }

    public void showLose() {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        switch (((JButton) e.getSource()).getActionCommand()) {
            case "Speed Help":
                break;
            case "Acceleration Help":
                break;
            case "Handling Help":
                break;
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private class Track extends JPanel {
        ArrayList<Line2D.Double> segments;
        boolean paintedOnce;

        public Track(ArrayList<Line2D.Double> trackSegments) {
            super(true);
            paintedOnce = false;
            setPreferredSize(new Dimension(900, 540));
            this.segments = trackSegments;
            setForeground(Color.darkGray);
        }

        @Override
        public void paintComponent(Graphics g) {
            System.out.println("not dead yet!");
            Graphics2D g2 = (Graphics2D) g;
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHints(rh);

            for (Line2D.Double segment : segments) {
                g2.setStroke(new BasicStroke(10));
                g2.draw(segment);
                System.out.println("Line2D : " + segment.toString());
            }
        }
    }
}