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
    private int previewCount;
    private int previewIndex;
    private String[] previewNames;
    private Color backgroundColor;
    private Color foregroundColor;
    private final Font menuFontPLAIN = new Font("Calibri", Font.PLAIN, 18);
    private final Font menuFontBOLD = new Font("Calibri", Font.BOLD, 22);
    private final Dimension defaultRes;
    private JSlider speedSlider;
    private JSlider accelerationSlider;
    private JSlider handlingSlider;
    private JPanel c;
    private JPanel game;
    private JPanel menu;
    private JPanel uiGrid;
    private JPanel uiBox;
    private JPanel previewWindow;
    private JButton speedHelpButton;
    private JButton accelerationHelpButton;
    private JButton handlingHelpButton;
    private JButton budgetHelpButton;
    private JButton startButton;
    private JButton hurryButton;
    private JButton pauseButton;
    private JButton restartButton;
    private JButton previousPreviewButton;
    private JButton nextPreviewButton;
    private ArrayList<JPanel> previewCards;
    private ArrayList<JPanel> previewSprites;
    private JLabel selectVehicle;
    private JLabel allocateStatsLabel;
    private JLabel speedLabel;
    private JLabel accelerationLabel;
    private JLabel handlingLabel;
    private JLabel budgetLabel;
    private JLabel budgetValue;
    private JLabel previewLabel;
    private JPanel previewBar;
    private JPanel center;
    private JPanel track;
    private JPanel southButtons;
    private ArrayList<Line2D.Double> trackSegments;
    private ArrayList<Ellipse2D.Double> trackJoints;
    boolean draw;

    public GUI(String title, Color foregroundColor, Color backgroundColor, JComponent[] inputs, int width, int height) {
        super(title);
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        initializeMenuComponents(inputs);
        menu = new JPanel(); //has uiGrid and ridePreviewPanel
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        menu.setBackground(backgroundColor);
        game = new JPanel();
        defaultRes = new Dimension(width, height);


        game.setBackground(this.backgroundColor);
        game.setForeground(this.foregroundColor);
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

    }
    private void initializeMenuComponents(JComponent[] inputs){
        for (JComponent input : inputs) {
            if (input instanceof JButton) {
                JButton button = (JButton) input;
                button.setFont(menuFontBOLD);
                switch (button.getActionCommand()) {
                    case "race":
                        startButton = button;
                        startButton.setText("START RACE");
                        break;
                    case "fast":
                        hurryButton = button;
                        hurryButton.setText("Hurry");
                        break;
                    case "stop":
                        pauseButton = button;
                        pauseButton.setText("Pause");
                        break;
                    case "redo":
                        restartButton = button;
                        restartButton.setText("Restart?");
                        break;
                    case "last":
                        previousPreviewButton = button;
                        previousPreviewButton.setText("<");
                        break;
                    case "next":
                        nextPreviewButton = button;
                        nextPreviewButton.setText(">");
                        break;
                }
            }
            else if (input instanceof JSlider) {
                JSlider slider = (JSlider) input;
                switch (slider.getName()) {
                    case "spd" :
                        speedSlider = slider;
                        speedSlider.setBackground(backgroundColor);
                    break;
                    case "acc" :
                        accelerationSlider = slider;
                        accelerationSlider.setBackground(backgroundColor);
                    break;
                    case "han":
                        handlingSlider = slider;
                        handlingSlider.setBackground(backgroundColor);
                    break;
                }
            }
        }

        //JLabels
        selectVehicle = new JLabel("SELECT VEHICLE:", JLabel.CENTER);
        selectVehicle.setFont(menuFontBOLD);
        selectVehicle.setForeground(foregroundColor);
        allocateStatsLabel = new JLabel("ALLOCATE STAT POINTS:", JLabel.CENTER);
        allocateStatsLabel.setFont(menuFontBOLD);
        allocateStatsLabel.setForeground(foregroundColor);
        speedLabel = new JLabel("Top Speed", JLabel.CENTER);
        speedLabel.setFont(menuFontBOLD);
        speedLabel.setForeground(foregroundColor);

        accelerationLabel = new JLabel("Acceleration", JLabel.CENTER);
        accelerationLabel.setFont(menuFontBOLD);
        accelerationLabel.setForeground(foregroundColor);

        handlingLabel = new JLabel("Handling", JLabel.CENTER);
        handlingLabel.setFont(menuFontBOLD);
        handlingLabel.setForeground(foregroundColor);

        budgetLabel = new JLabel ("POINTS AVAILABLE: ", JLabel.RIGHT);
        budgetLabel.setFont(menuFontBOLD);
        budgetLabel.setForeground(foregroundColor);

        budgetValue = new JLabel ("00", JLabel.LEFT);
        budgetValue.setFont(menuFontBOLD);
        budgetValue.setForeground(Color.GREEN);
        //JToolTips
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(8000);
        UIManager.put("ToolTip.font", menuFontPLAIN);
        speedLabel.setToolTipText("Sets vehicle's top speed");
        accelerationLabel.setToolTipText("<html>"
                                       + "Sets rate at which top speed is "
                                       + "<br>"
                                       + "reached from rest"
                                       + "</html>");
        handlingLabel.setToolTipText("<html>"
                + "Sets vehicle's chance to perform "
                + "<br>"
                + "very sharp turns without losing "
                + "<br>"
                + "control (stopping briefly)"
                + "</html>");
        budgetLabel.setToolTipText("<html>"
                                 + "Number of points to be allocated "
                                 + "<br>"
                                 + "to your vehicle's stats. You can't "
                                 + "<br>"
                                 + "start a race until there are zero "
                                 + "<br>"
                                 + "points available"
                                 + "</html>");
    }
    public void playerMenu(int round){}

    public void switchToPlayerMenu() {
        setContentPane(menu);
        this.pack();
        this.revalidate();
    }

    public void playerMenu(String[] carNames, int budget) {
        previewNames = carNames;
        previewCount = previewNames.length;
        previewIndex = (int)(Math.random() * (previewCount) + 0);



        GridBagLayout menuLayout;
        GridBagConstraints menuConstraints = new GridBagConstraints();
        //(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady)
        menu.setPreferredSize(defaultRes);
        menu.setMinimumSize(defaultRes);
        int menuWidth = 320;
        if(System.getProperty("os.name").contains("Mac")){
            menuWidth = 400;
            System.out.println(System.getProperty("os.name"));
        }
        int menuHeight = 1080;
        Dimension boxBounds = new Dimension(menuWidth+40, menuHeight);
        Dimension gridBounds = new Dimension(menuWidth, menuHeight);


        uiBox = new JPanel(new BorderLayout());//bounding box
        uiBox.setPreferredSize(boxBounds);
        uiBox.setMaximumSize(boxBounds);
        uiBox.setBackground(backgroundColor);


        uiGrid = new JPanel(); //button cluster
        uiGrid.setBackground(backgroundColor);
        uiGrid.setPreferredSize(gridBounds);
        uiGrid.setMaximumSize(gridBounds);
        uiGrid.setLayout(menuLayout = new GridBagLayout());
        menuLayout.setConstraints(uiGrid, menuConstraints);

        Insets zero = new Insets(0,0,0,0);
        Insets vertical5_10 = new Insets(5,0,10,0);
        Insets vertical50_20 = new Insets(50,0,20,0);

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.insets = vertical50_20;
        menuConstraints.weighty = 0;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 0;
        menuConstraints.gridwidth = 6;
        uiGrid.add(selectVehicle, menuConstraints);

        menuConstraints.fill = GridBagConstraints.NONE;
        menuConstraints.anchor = GridBagConstraints.NORTH;
        menuConstraints.insets = zero;
        menuConstraints.weightx = 0;
        menuConstraints.gridx = 0;
        menuConstraints.gridy = 1;
        menuConstraints.gridwidth = 1;
        uiGrid.add(previousPreviewButton, menuConstraints);

        previewLabel = new JLabel(previewNames[previewIndex], JLabel.CENTER);
        previewLabel.setFont(menuFontBOLD);
        previewLabel.setForeground(foregroundColor);
        //System.out.println(previewIndex);
        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.insets = vertical5_10;
        menuConstraints.weightx = 1;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 1;
        menuConstraints.gridwidth = 6;
        uiGrid.add(previewLabel, menuConstraints);

        menuConstraints.fill = GridBagConstraints.NONE;
        menuConstraints.insets = zero;
        menuConstraints.weightx = 0;
        menuConstraints.gridx = 7;
        menuConstraints.gridy = 1;
        menuConstraints.gridwidth = 1;
        uiGrid.add(nextPreviewButton, menuConstraints);

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.insets = vertical50_20;
        menuConstraints.weightx = 0.5;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 2;
        menuConstraints.gridwidth = 6;
        uiGrid.add(allocateStatsLabel, menuConstraints);

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.anchor = GridBagConstraints.SOUTH;
        menuConstraints.insets = vertical5_10;
        menuConstraints.weighty = 0.03;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 3; // (0,3)
        menuConstraints.gridwidth = 6;
        uiGrid.add(speedLabel, menuConstraints);

        /*menuConstraints.anchor = GridBagConstraints.SOUTHEAST;
        menuConstraints.fill = GridBagConstraints.NONE;
        menuConstraints.gridx = 7; // (7,3)
        menuConstraints.gridwidth = 1;
        uiGrid.add(speedHelpButton, menuConstraints);*/

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.anchor = GridBagConstraints.NORTH;
        menuConstraints.insets = zero;
        menuConstraints.weightx = 1;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 4; //(0,4)
        menuConstraints.gridwidth = 6;
        uiGrid.add(speedSlider, menuConstraints);

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.anchor = GridBagConstraints.SOUTH;
        menuConstraints.insets = vertical5_10;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 5;// (0,5)
        menuConstraints.gridwidth = 6;
        uiGrid.add(accelerationLabel, menuConstraints);

        /*menuConstraints.anchor = GridBagConstraints.SOUTHEAST;
        menuConstraints.fill = GridBagConstraints.NONE;
        menuConstraints.weightx = 0;
        menuConstraints.gridx = 7;  // (7,5)
        menuConstraints.gridwidth = 1;
        uiGrid.add(accelerationHelpButton, menuConstraints);*/

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.anchor = GridBagConstraints.NORTH;
        menuConstraints.insets = zero;
        menuConstraints.weightx = 1;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 6; //(0,6)
        menuConstraints.gridwidth = 6;
        uiGrid.add(accelerationSlider, menuConstraints);

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.anchor = GridBagConstraints.SOUTH;
        menuConstraints.insets = vertical5_10;
        menuConstraints.weightx = 0.5;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 7;
        menuConstraints.gridwidth = 6;
        uiGrid.add(handlingLabel, menuConstraints);

        /*menuConstraints.anchor = GridBagConstraints.SOUTHEAST;
        menuConstraints.fill = GridBagConstraints.NONE;
        menuConstraints.weightx = 0;
        menuConstraints.gridx = 7;  // (7,7)
        menuConstraints.gridwidth = 1;
        uiGrid.add(handlingHelpButton, menuConstraints);*/

        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.anchor = GridBagConstraints.NORTH;
        menuConstraints.insets = zero;
        menuConstraints.weightx = 1;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 8;
        menuConstraints.gridwidth = 6;
        uiGrid.add(handlingSlider, menuConstraints);



        menuConstraints.fill = GridBagConstraints.NONE;
        menuConstraints.anchor = GridBagConstraints.CENTER;
        menuConstraints.weightx = 1;
        menuConstraints.weighty = 0.3;
        menuConstraints.gridx = 3;
        menuConstraints.gridy = 9;
        menuConstraints.gridwidth = 2;
        uiGrid.add(budgetLabel, menuConstraints);

        budgetValue.setText(String.valueOf(budget));
        menuConstraints.weightx = 0.2;
        menuConstraints.anchor = GridBagConstraints.WEST;
        menuConstraints.gridx = 5;
        menuConstraints.gridy = 9;
        uiGrid.add(budgetValue, menuConstraints);

        /*menuConstraints.anchor = GridBagConstraints.EAST;
        menuConstraints.weightx = 0;
        menuConstraints.gridx = 7;  // (7,7)
        menuConstraints.gridwidth = 1;
        uiGrid.add(budgetHelpButton, menuConstraints);*/

        menuConstraints.anchor = GridBagConstraints.NORTHEAST;
        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.insets = vertical50_20;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 10;
        menuConstraints.weightx = 1;
        menuConstraints.weighty = 1;
        menuConstraints.gridwidth = 6;
        uiGrid.add(startButton, menuConstraints);

        previewWindow = new JPanel();
        previewWindow.setBackground(backgroundColor);
        previewWindow.setMinimumSize(new Dimension(1700 ,830));

        uiBox.add(uiGrid, BorderLayout.EAST);
        menu.add(uiBox, BorderLayout.WEST);
        menu.add(previewWindow, BorderLayout.CENTER);
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

    public int getPreviewIndex() {
        return previewIndex;
    }

    public void setPreviewIndex(int newIndex) {
        if(newIndex >= previewCount){
            this.previewIndex = 0;
        }else if(newIndex < 0){
            previewIndex = previewCount-1;
        }else{
            this.previewIndex = newIndex;
        }
        previewLabel.setText(previewNames[previewIndex]);
        uiGrid.revalidate();
    }

    public void toTrack(Node head, ArrayList <Car> cars) {
        track = new TrackPanel(head, cars);
        track.setBackground(backgroundColor);
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