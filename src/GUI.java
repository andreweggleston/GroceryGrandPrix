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
    private Font menuFontPLAIN;
    private Font menuFontBOLD;
    private int centerWidth;
    private int centerHeight;
    private final Dimension defaultRes = new Dimension(1920, 1080);
    private JFrame frame;
    private Container pane;
    private CardLayout cards;
    private JPanel content;
    private JPanel game;
    private JPanel menu;
    private JPanel uiGrid;
    private JPanel uiBox;
    private JPanel rideWindow;
    private Container winlose;
    private Container loading;
    private ArrayList<JPanel> previewCards;
    private ArrayList<JPanel> previewSprites;
    private JButton[] menuButtons;
    private JLabel[] statLabels;
    private JPanel previewBar;
    private JPanel center;
    private JPanel track;
    private JPanel southButtons;
    private ArrayList<Line2D.Double> trackSegments;
    private ArrayList<Ellipse2D.Double> trackJoints;
    boolean draw;
    public enum gamestate {
        inMenu, inGame, endRace;
    }
    private gamestate state;

    public GUI(Color backgroundColor, JButton[] buttons, int width, int height) {
        state = gamestate.inMenu;
        frame = new JFrame("GroceryGrandPrix");
        pane = new Container();
        pane = frame.getContentPane();
        pane.setPreferredSize(defaultRes);
        pane.setMinimumSize(defaultRes);



        uiGrid = new JPanel();
        menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        game = new JPanel();
        draw = true;
        centerWidth = width;
        centerHeight = height;
        this.backgroundColor = backgroundColor;
        foregroundColor = Color.lightGray;
        menuFontPLAIN = new Font("Courier", Font.PLAIN, 16);
        menuFontBOLD = new Font("Courier", Font.BOLD, 16);
        menuButtons = buttons;
        statLabels = new JLabel[8];
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

        cards = new CardLayout();
        cards.addLayoutComponent(menu,"menu");
        cards.addLayoutComponent(game, "game");
        //cards.addLayoutComponenet(end, "end");
        content = new JPanel();
        content.setLayout(cards);
        pane.add(content);


        playerMenu(0, 17);
        
        game.add(previewBar, BorderLayout.NORTH);
        game.add(center, BorderLayout.CENTER);



        frame.setContentPane(pane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);

    }

    public void playerMenu(int round, int budget) {
        state = gamestate.inMenu;
        GridBagLayout menuLayout;
        GridBagConstraints menuConstraints = new GridBagConstraints();//12x12
        //(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady)
        int labelWidth = 5;
        int labelHeight = 2;
        menu.setPreferredSize(defaultRes);
        menu.setMinimumSize(defaultRes);
        Dimension menuDimension = new Dimension(256, 700);
        uiGrid.setPreferredSize(menuDimension);
        //uiGrid.setMinimumSize(menuDimension);
        uiGrid.setMaximumSize(menuDimension);
        //menu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        uiGrid.setLayout(menuLayout = new GridBagLayout());
        menuConstraints.fill = GridBagConstraints.BOTH;//to make it resizable

        menuLayout.setConstraints(uiGrid, menuConstraints);

        menuConstraints.fill = GridBagConstraints.NONE;//most other menu items should not be resizeable
        //menuConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
        menuConstraints.gridx = 0;
        menuConstraints.gridy = 0;
        menuConstraints.gridwidth = 8;
        menuConstraints.gridheight = 2;
        uiGrid.add(new JLabel("Choose Your Ride"), menuConstraints);

        menuConstraints.gridx = 0; //redundant
        menuConstraints.gridy = 2;
        menuConstraints.gridwidth = 1;
        menuConstraints.gridheight = 2; //redundant
        uiGrid.add(new JLabel("<"), menuConstraints);

        String rideChoice = "Bikenana";//placeholder
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 2; //redundant
        menuConstraints.gridwidth = 6;
        menuConstraints.gridheight = 2; //redundant
        uiGrid.add(new JLabel(rideChoice), menuConstraints);

        menuConstraints.gridx = 7;
        menuConstraints.gridy = 2; //redundant
        menuConstraints.gridwidth = 1;
        menuConstraints.gridheight = 2; //redundant
        uiGrid.add(new JLabel(">"), menuConstraints);

        statLabels[0] = new JLabel("Top Speed");
        menuConstraints.gridx = 0;
        menuConstraints.gridy = 4;
        menuConstraints.gridwidth = 5;
        menuConstraints.gridheight = 2; //redundant
        uiGrid.add(statLabels[0], menuConstraints);

        statLabels[1] = new JLabel("Acceleration");
        menuConstraints.gridx = 0; //redundant
        menuConstraints.gridy = 6;
        menuConstraints.gridwidth = 5; //redundant
        menuConstraints.gridheight = 2; //redundant
        uiGrid.add(statLabels[1], menuConstraints);

        statLabels[2] = new JLabel("Handling");
        //menuConstraints.gridx = 0; //redundant
        menuConstraints.gridy = 8;
        //menuConstraints.gridwidth = 5; //redundant
        //menuConstraints.gridheight = 2; //redundant
        uiGrid.add(statLabels[2], menuConstraints);

        Dimension plusminusSize = new Dimension(18,14);
        Font plusFont = new Font("Arial Black", Font.PLAIN, 18);
        Font minusFont = new Font("Arial", Font.BOLD, 29);
        Font helpFont = new Font("Arial Black", Font.PLAIN, 12);

        for(int i = 0; i < 3; i++){
            //statLabels[i].setBackground(Color.lightGray);
            //statLabels[i].setBorder(null);
            menuConstraints.gridx = 6;
            menuConstraints.gridy = 4+(i*2);
            menuConstraints.gridwidth = 1;
            menuConstraints.gridheight = 1;
            menuButtons[0+i].setText("+");//plus button
            //menuButtons[0+i].setBackground(Color.lightGray);
            menuButtons[0+i].setMaximumSize(plusminusSize);
            menuButtons[0+i].setPreferredSize(plusminusSize);
            menuButtons[0+i].setFont(plusFont);
            menuButtons[0+i].setMargin(new Insets(-1,0,-2,0));
            uiGrid.add(menuButtons[0+i], menuConstraints);
            menuButtons[6+i].setText("?");
            menuButtons[6+i].setFont(helpFont);
            menuButtons[6+i].setMargin(new Insets(-1,4,-1,4));
            menuConstraints.gridx = 7;
            menuConstraints.gridy = 2+(i*2);
            menuConstraints.gridheight = 2;

            uiGrid.add(menuButtons[6+i], menuConstraints);

            menuConstraints.gridx = 6;
            menuConstraints.gridy = 5+(i*2);
            menuConstraints.gridheight = 1;
            menuButtons[3+i].setText("-");//minus button
            menuButtons[3+i].setPreferredSize(plusminusSize);
            menuButtons[3+i].setMaximumSize(plusminusSize);
            menuButtons[3+i].setFont(minusFont);
            menuButtons[3+i].setMargin(new Insets(-4,-2,-1,-1));

            uiGrid.add(menuButtons[3+i], menuConstraints);

        }
        statLabels[3] = new JLabel ("Budget: ");
        statLabels[3].setFont(menuFontPLAIN);
        menuConstraints.gridx = 0;
        menuConstraints.gridy = 10;
        uiGrid.add(statLabels[3], menuConstraints);

        menuButtons[10].setText("START RACE");
        menuConstraints.gridx = 0;
        menuConstraints.gridy = 11;
        menuConstraints.gridwidth = 8;
        uiGrid.add(menuButtons[10], menuConstraints);

        rideWindow = new JPanel();
        rideWindow.setBackground(Color.GREEN);
        rideWindow.setMinimumSize(new Dimension(1700 ,830));

        createPreviewCard(0);

        menu.add(uiGrid, BorderLayout.WEST);
        menu.add(rideWindow, BorderLayout.CENTER);
        cards.show(content, "menu");
    }

    public void createPreviewCard(int car) {
        previewCards.add(car, new JPanel(new BorderLayout()));
        previewCards.get(car).setPreferredSize(new Dimension(150, 150));
        previewCards.get(car).setMinimumSize(new Dimension(150, 150));
        previewCards.get(car).setMaximumSize(new Dimension(150, 150));
        previewCards.get(car).setBackground(foregroundColor);

        previewSprites.add(car, new JPanel());
        previewSprites.get(car).setBackground(Color.WHITE);
        previewSprites.get(car).add(new Sprite("bikenana", 1));
        System.out.println("Max: " + previewSprites.get(car).getMaximumSize() + "\nPref: " + previewSprites.get(car).getPreferredSize()/* + "\nW: " + image.getWidth() + "\nH: " + image.getHeight()*/);
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
        if(state.equals(gamestate.inMenu)){
            cards.show(content, "game");
            state = gamestate.inGame;
        }
        if(!track.isVisible()) track.setVisible(true);

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