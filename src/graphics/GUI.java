package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import shared.*;

public class GUI extends JFrame implements MouseListener {
    private Color foregroundColor;
    private Font menuFontPLAIN;
    private Font menuFontBOLD;
    private final Dimension defaultRes;
    private JPanel c;
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

    public GUI(String title, Color backgroundColor, JButton[] buttons, int width, int height) {
        super(title);
        uiGrid = new JPanel(); //button cluster
        menu = new JPanel(); //has uiGrid and ridePreviewPanel
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        game = new JPanel();
        defaultRes = new Dimension(width, height);
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

        center = new JPanel(); //everything but previewbar in game
        center.setPreferredSize(defaultRes);
        center.setBackground(backgroundColor);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setupPlayerMenu();
    }

    private void setupPlayerMenu() {
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
            //menuButtons
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
        //menuConstraints.anchor = GridBagConstraints.NORTH;
        menuConstraints.gridx = 0;
        menuConstraints.gridy = 11;
        //menuConstraints.weighty = 1;
        menuConstraints.gridwidth = 8;
        uiGrid.add(menuButtons[10], menuConstraints);

        rideWindow = new JPanel(); //green thing
        rideWindow.setBackground(Color.GREEN);
        rideWindow.setMinimumSize(new Dimension(1700 ,830));

        menu.add(uiGrid, BorderLayout.WEST);
        menu.add(rideWindow, BorderLayout.CENTER);
    }

    public void switchToPlayerMenu(){
        this.setContentPane(menu);
        this.pack();
        this.revalidate();
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

    public void toTrack(Node head, ArrayList <Car> cars) {
        track = new TrackPanel(head, cars);
        track.setPreferredSize(defaultRes);
        this.setContentPane(track);
        this.pack();
        this.revalidate();
        this.repaint();
        track.repaint();
    }

    /**
     * Shows the results of every race after it has been concluded and prompts the user to continue the game.
     * @param placedCars The list of Cars in order from 1st to last.
     * @param raceTime How much time the race took as an int.
     * @return A boolean indicated whether the user would like to continue the game.
     */
    public boolean showResults(ArrayList<Car> placedCars, int raceTime) {
        boolean userContinue;
        int playerPlacement = 0;
        Car lastCar = placedCars.get(placedCars.size()-1);
        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        JPanel statsHeaderPanel = new JPanel();
        JPanel headerPanel = new JPanel();
        JPanel placementPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 75, 0));
        JPanel imagePanel = new JPanel();
        JPanel loserPanel = new JPanel();
        JPanel promptPanel = new JPanel();
        JTextArea header = new JTextArea();
        JLabel loserLabel = new JLabel();
        JLabel promptLabel = new JLabel();
        Font messageFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        header.setTabSize(3);
        header.setEditable(false);
        header.setFont(messageFont);
        header.setBackground(headerPanel.getBackground());

        loserLabel.setFont(messageFont);
        promptLabel.setFont(messageFont);

        // Find the position that the player finished the race.
        for (int i = 0; i < placedCars.size(); i++) {
            if (placedCars.get(i).isPlayer()) playerPlacement = i + 1;
        }

        // If player beat the game show their stats and wrap up run.
        if (placedCars.size() == 2 && playerPlacement == 1) {
            Car player = placedCars.get(0);

            header.setText("\t\tYou win!\nThe final race lasted " + raceTime + " seconds.");
            JLabel statsHeader = new JLabel("Your winning car is:");
            statsHeader.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            JLabel stats = new JLabel("Spd-" + player.getTopSpeed() + " Acc-" + player.getAcceleration() + " Han-" + player.getHandling());
            stats.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            Sprite playerSprite = new Sprite("bikenana", 1);
            promptLabel.setText("Would you like to try another run?");

            statsHeaderPanel.add(statsHeader);
            placementPanel.add(stats);
            imagePanel.add(playerSprite);
        }
        else {
            String placementSuffix;
            // Find the proper suffix for player's placement.
            switch (playerPlacement) {
                case 1 :
                    placementSuffix = "st";
                    break;
                case 2 :
                    placementSuffix = "nd";
                    break;
                case 3 :
                    placementSuffix = "rd";
                    break;
                default :
                    placementSuffix = "th";
            }

            header.setText("\t You placed " + playerPlacement + placementSuffix + ".\nThe race lasted " + raceTime + " seconds.");
            // Change the message shown in the prompt label to reflect if the player has lost.
            promptLabel.setText((playerPlacement == placedCars.size()) ? "Try again?" : "Do you want to continue to the next round?");

            // Add every car's sprite and the corresponding placement in the prior race to panels.
            for (int i = 0; i < placedCars.size(); i++) {
                JLabel nextRanking = new JLabel(Integer.toString(i + 1));
                Sprite nextSprite = new Sprite("bikenana", 1);
                nextRanking.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (nextSprite.getPreferredSize().getWidth() * 3) / 4));

                placementPanel.add(nextRanking);
                imagePanel.add(nextSprite);
            }
        }
        // Change the message shown in the loser label to reflect if the player has lost.
        loserLabel.setText(((playerPlacement == placedCars.size()) ? "You lost. Your" : "Last place's") + " ending stats were: " +
                "Spd-" + lastCar.getTopSpeed() + ", Acc-" + lastCar.getAcceleration() + ", Han-" + lastCar.getHandling());

        headerPanel.add(header);
        loserPanel.add(loserLabel);
        promptPanel.add(promptLabel);

        results.add(headerPanel);
        // Adds an empty panel when the player did not win the tournament.
        results.add(statsHeaderPanel);
        results.add(placementPanel);
        results.add(imagePanel);
        results.add(loserPanel);
        results.add(promptPanel);

        // Show all the components in the form of a ConfirmDialog. Save the user response as a boolean (false if no).
        userContinue = JOptionPane.showConfirmDialog(this, results, "Race Results", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) != 1;
        return userContinue;
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
}