import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GUI implements MouseListener {
    private Color backgroundColor;
    private Color foregroundColor;
    JFrame frame;
    Container game;
    Container menu;
    Container results;
    Container loading;
    private ArrayList<JPanel> previewCards;
    private ArrayList<JPanel> previewSprites;
    private JButton[] menuButtons;
    private JLabel[] menuLabels;
    private JPanel previewBar;
    private JPanel center;
    private JPanel east;
    private JPanel track;
    private JPanel southButtons;
    private ArrayList<Line2D.Double> trackSegments;
    private ArrayList<Ellipse2D.Double> trackJoints;
    private int centerWidth;
    private int centerHeight;
    boolean draw;

    public GUI(Color backgroundColor, JButton[] buttons, int width, int height) {
        frame = new JFrame("GroceryGrandPrix");
        menu = new Container();
        game = new Container();
        results = new Container();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        draw = true;
        centerWidth = width;
        centerHeight = height;
        this.backgroundColor = backgroundColor;
        foregroundColor = Color.lightGray;
        menuButtons = buttons;
        menuLabels = new JLabel[4];
        game.setBackground(backgroundColor);
        game.setForeground(foregroundColor);
        game.setLayout(new BorderLayout());

        previewCards = new ArrayList<JPanel>();
        previewSprites = new ArrayList<JPanel>();
        previewBar = new JPanel();
        previewBar.setLayout(new BoxLayout(previewBar, BoxLayout.X_AXIS));
        previewBar.setBackground(backgroundColor);

        center = new JPanel();
        center.setPreferredSize(new Dimension(centerWidth, centerHeight));
        center.setBackground(backgroundColor);
        east = new JPanel(true);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        playerMenu(0, 20);
        
        game.add(previewBar, BorderLayout.NORTH);
        game.add(center, BorderLayout.CENTER);

        frame.setContentPane(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void playerMenu(int round, int budget) {
        menu.setLayout(new GridLayout(4,1));
        JPanel typeDisplay = new JPanel();
        JPanel typeSelector = new JPanel();
        JPanel statSelectors = new JPanel(new GridLayout(3,3));

        statSelectors.add(menuButtons[3]);
        statSelectors.add(menuLabels[1] = new JLabel(""));
        statSelectors.add(menuButtons[0]);
        statSelectors.add(menuButtons[4]);
        statSelectors.add(menuLabels[2] = new JLabel(""));
        statSelectors.add(menuButtons[1]);
        statSelectors.add(menuButtons[5]);
        statSelectors.add(menuLabels[3] = new JLabel(""));
        statSelectors.add(menuButtons[2]);

        createPreviewCard(0);
        //menu.
        //east.add(menu);
    }

    public void createPreviewCard(int car) {
        previewCards.add(car, new JPanel(new BorderLayout()));
        previewCards.get(car).setPreferredSize(new Dimension(150, 150));
        previewCards.get(car).setMinimumSize(new Dimension(150, 150));
        previewCards.get(car).setMaximumSize(new Dimension(150, 150));
        previewCards.get(car).setBackground(foregroundColor);

        previewSprites.add(car, new JPanel());
        previewSprites.get(car).setBackground(Color.WHITE);
        //previewSprites.get(car).add(new Sprite("bikenana", 1));
        //System.out.println("Max: " + previewSprites.get(car).getMaximumSize() + "\nPref: " + previewSprites.get(car).getPreferredSize()/* + "\nW: " + image.getWidth() + "\nH: " + image.getHeight()*/);
        previewSprites.get(car).setPreferredSize(new Dimension(120, 80));
        previewSprites.get(car).setMaximumSize(new Dimension(120, 80));

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
        previewCenter.add(previewSprites.get(car));
        previewCenter.add(new Box.Filler((new Dimension(150, 15)), (new Dimension(150, 15)), (new Dimension(150, 15))));
        previewCenter.add(statsDisplay);

        previewCards.get(car).add(new Box.Filler((new Dimension(120, 15)), (new Dimension(120, 15)), (new Dimension(120, 15))), BorderLayout.NORTH);
        previewCards.get(car).add(previewCenter, BorderLayout.CENTER);
        previewCards.get(car).add(new Box.Filler((new Dimension(15, 130)), (new Dimension(15, 130)), (new Dimension(15, 130))), BorderLayout.WEST);
        previewCards.get(car).add(new Box.Filler((new Dimension(15, 130)), (new Dimension(15, 130)), (new Dimension(15, 130))), BorderLayout.EAST);
        previewCards.get(car).add(new Box.Filler((new Dimension(150, 15)), (new Dimension(150, 15)), (new Dimension(150, 15))), BorderLayout.SOUTH);
        previewBar.add(previewCards.get(car), car);
    }

    public void buildTrack(Node head, ArrayList <Car> cars) {
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
        track = new Track(trackSegments, centerWidth, centerHeight);
        track.setVisible(false);
        center.add(track);
    }
    public void drawTrack() {
        if(!track.isVisible()) track.setVisible(true);

    }

    public void showResults(ArrayList<Car> cars) {
        Font messageFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
        JPanel headerPanel = new JPanel();
        JPanel rankingPanel = new JPanel();
        rankingPanel.setLayout(new BoxLayout(rankingPanel, BoxLayout.Y_AXIS));
        JPanel placementPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 75, 0));
        JPanel imagePanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        JLabel header = new JLabel("Placeholder text");
        header.setFont(messageFont);
        JLabel bottom = new JLabel("Placeholder text");
        bottom.setFont(messageFont);
        headerPanel.add(header);
        for (int i = 0; i < cars.size(); i++) {
            JLabel nextRanking = new JLabel(Integer.toString(i+1));
            Sprite nextSprite = new Sprite("bikenana", 1);
            nextRanking.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(nextSprite.getPreferredSize().getWidth()*3)/4));
            placementPanel.add(nextRanking);
            imagePanel.add(nextSprite);
        }
        rankingPanel.add(placementPanel);
        rankingPanel.add(imagePanel);

        bottomPanel.add(bottom);

        results.add(headerPanel);
        results.add(rankingPanel);
        results.add(bottomPanel);
        //frame.setContentPane(results);
        //frame.pack();
        //frame.revalidate();

        JDialog resultsDialog = new JDialog(frame, "Results", true);
        resultsDialog.setResizable(false);
        resultsDialog.setContentPane(results);
        resultsDialog.pack();
        resultsDialog.revalidate();
        resultsDialog.setVisible(true);
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
        BasicStroke road;

        public Track(ArrayList<Line2D.Double> trackSegments, int width, int height) {
            super(true);
            setPreferredSize(new Dimension(width, height));
            this.segments = trackSegments;
            setForeground(Color.lightGray);
        }
        @Override
        public void paintComponent(Graphics g) {
            System.out.println("not dead yet!");
            Graphics2D g2 = (Graphics2D) g;
            RenderingHints antiAliasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            road = new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
            g2.setRenderingHints(antiAliasing);
            g2.setStroke(road);
            for (Line2D.Double segment : segments) {
                g2.draw(segment);
                System.out.println("Line2D : " + segment.toString());
            }
        }
    }
}