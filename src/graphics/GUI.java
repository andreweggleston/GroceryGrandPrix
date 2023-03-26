package graphics;

import shared.Car;
import shared.Node;
import shared.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class GUI extends JFrame implements MouseListener {
    private int gameWidth, gameHeight, windowWidth, windowHeight, cardWidth, cardHeight;
    float previewScale;
    private final Dimension gameBounds;
    private final Dimension windowBounds;
    private String previewName;
    private Color backgroundColor, foregroundColor;
    private final Font menuFontPLAIN = new Font("Calibri", Font.PLAIN, 18);
    private final Font menuFontBOLD = new Font("Calibri", Font.BOLD, 22);
    private final Font menuFontMED = new Font("Calibri", Font.BOLD, 18);
    private final Font menuFontBEEG = new Font("Calibri", Font.BOLD, 36);
    private HashMap<String, BufferedImage> images;
    private JLabel[] thumbLabels;
    private ArrayList<JPanel> cards, thumbnails;
    private JPanel game, menu, uiGrid, uiBox, previewWindow, previewCards, center, track;
    private JSlider speedSlider, accelerationSlider, handlingSlider;
    private JButton startButton, hurryButton, pauseButton, restartButton, lastPreviewButton, nextPreviewButton;
    private JLabel selectVehicle, allocateStatsLabel, speedLabel, accelerationLabel, handlingLabel, budgetLabel, budgetValue, nameLabel, previewLabel;

    public GUI(String title, JComponent[] inputs, HashMap<String, BufferedImage> images, int width, int height) throws IOException {
        super(title);

        this.images = images;

        this.foregroundColor = new Color(222, 232, 243);
        this.backgroundColor = new Color(115, 122, 148);

        gameWidth = width;
        gameHeight = height;
        windowWidth = (int)(gameWidth*1.25);
        windowHeight = gameHeight;
        cardWidth = windowWidth-width;
        cardHeight = gameHeight/4;
        windowBounds = new Dimension(windowWidth, windowHeight);
        gameBounds = new Dimension(gameWidth, gameHeight);

        menu = new JPanel(); //has uiGrid and ridePreviewPanel
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        menu.setBackground(backgroundColor);

        game = new JPanel();
        game.setBackground(this.backgroundColor);
        game.setForeground(this.foregroundColor);
        game.setLayout(new BorderLayout());

        initializeMenuComponents(inputs);

        center = new JPanel(); //everything but previewCards in game
        center.setPreferredSize(gameBounds);
        center.setBackground(backgroundColor);

        //this.setResizable(false);
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
                    case "last":
                        lastPreviewButton = button;
                        lastPreviewButton.setText("<");
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
        //define preview scaling
        int maxWidth = 0;
        previewScale = 1.0f;
        for (BufferedImage img : images.values()) {
            if (img.getWidth() > maxWidth) {
                maxWidth = img.getWidth();
                previewScale = ((float) (gameWidth - 400)) / ((float) (maxWidth));
            }
        }
        previewLabel = new JLabel(new ImageIcon(), JLabel.LEFT);
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

    public void switchToPlayerMenu() {
        setContentPane(menu);
        this.pack();
        this.revalidate();
    }

    public void playerMenu(int budget) {
        GridBagConstraints menuConstraints = new GridBagConstraints();
        menu.setPreferredSize(windowBounds);
        menu.setMinimumSize(windowBounds);
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
        uiGrid.setLayout(new GridBagLayout());

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
        uiGrid.add(lastPreviewButton, menuConstraints);

        nameLabel = new JLabel("", JLabel.CENTER);
        nameLabel.setFont(menuFontBEEG);
        nameLabel.setForeground(foregroundColor);
        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.insets = vertical5_10;
        menuConstraints.weightx = 1;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 1;
        menuConstraints.gridwidth = 6;
        uiGrid.add(nameLabel, menuConstraints);

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

        menuConstraints.anchor = GridBagConstraints.NORTHEAST;
        menuConstraints.fill = GridBagConstraints.HORIZONTAL;
        menuConstraints.insets = vertical50_20;
        menuConstraints.gridx = 1;
        menuConstraints.gridy = 10;
        menuConstraints.weightx = 1;
        menuConstraints.weighty = 1;
        menuConstraints.gridwidth = 6;
        uiGrid.add(startButton, menuConstraints);

        changePreviewDisplay("Avocardo");

        previewWindow = new JPanel(new GridBagLayout());
        GridBagConstraints previewConstraints = new GridBagConstraints();
        previewConstraints.fill = GridBagConstraints.BOTH;
        previewConstraints.anchor = GridBagConstraints.SOUTHWEST;
        previewWindow.add(previewLabel, previewConstraints);
        previewWindow.setBackground(backgroundColor);
        previewWindow.setMinimumSize(new Dimension(1700 ,830));

        uiBox.add(uiGrid, BorderLayout.EAST);
        menu.add(uiBox, BorderLayout.WEST);
        menu.add(previewWindow, BorderLayout.CENTER);
    }

    public void updateStatLabels(int topSpeed, int acceleration, int handling, int budget, boolean updateSliders) {
        speedLabel.setText("Top Speed: " + topSpeed);
        accelerationLabel.setText("Acceleration: " + acceleration);
        handlingLabel.setText("Handling: " + handling);
        budgetValue.setText(String.valueOf(budget));

        if (updateSliders) {
            speedSlider.setValue(topSpeed);
            accelerationSlider.setValue(acceleration);
            handlingSlider.setValue(handling);
        }
    }

    public void createPreviewCards(ArrayList <Car> cars) {

        Color cardColor = new Color(190, 198, 211);
        Color[] statColors = new Color[]{new Color(255, 0, 255), new Color(255, 68, 0), new Color(251, 95, 28), new Color(224, 114, 40), new Color(201, 141, 51), new Color(206, 166, 51), new Color(167, 176, 62), new Color(133, 183, 80), new Color(104, 197, 98), new Color(28, 251, 135), new Color(0, 255, 183)};
        int unitWidth = (cardWidth-40)/10;
        cards = new ArrayList<JPanel>();
        thumbnails = new ArrayList<JPanel>();
        previewCards = new JPanel();
        previewCards.setLayout(new BoxLayout(previewCards, BoxLayout.Y_AXIS));
        previewCards.setBackground(cardColor);

        previewCards.setPreferredSize(new Dimension(cardWidth, windowHeight));
        previewCards.setMinimumSize(new Dimension(cardWidth, windowHeight));
        previewCards.setMaximumSize(new Dimension(cardWidth, windowHeight));

        thumbLabels = new JLabel[cars.size()];

        GridBagConstraints cardConstraints = new GridBagConstraints();
        for(Car car : cars){
            String img = car.getImageName();
            cards.add(cars.indexOf(car), new JPanel(new BorderLayout()));

            cards.get(cars.indexOf(car)).setPreferredSize(new Dimension(cardWidth, cardHeight));
            cards.get(cars.indexOf(car)).setMinimumSize(new Dimension(cardWidth, cardHeight));
            cards.get(cars.indexOf(car)).setMaximumSize(new Dimension(cardWidth, cardHeight));
            cards.get(cars.indexOf(car)).setBackground(cardColor);

            //Process thumbnail from image
            float thumbScale = ((float)(120))/((float)((images.get(img).getHeight())));
            thumbLabels[cars.indexOf(car)] = new JLabel(getScaledIcon(car.getImageName(), thumbScale));

            thumbnails.add(cars.indexOf(car), new JPanel());
            thumbnails.get(cars.indexOf(car)).setLayout(new GridBagLayout());
            thumbnails.get(cars.indexOf(car)).setBackground(foregroundColor);//leave as foreground
            cardConstraints.insets = new Insets(0,0,0,0);
            cardConstraints.anchor = GridBagConstraints.SOUTH;
            cardConstraints.weightx = 0;
            cardConstraints.weighty = 1.0;
            thumbnails.get(cars.indexOf(car)).add(thumbLabels[cars.indexOf(car)], cardConstraints);
            thumbnails.get(cars.indexOf(car)).setPreferredSize(new Dimension(300, 120));
            thumbnails.get(cars.indexOf(car)).setMaximumSize(new Dimension(300, 120));

            JPanel licensePlate = new JPanel();
            licensePlate.setBackground(cardColor);
            JLabel plateName = new JLabel(car.getImageName().toUpperCase());
            plateName.setFont(menuFontMED);
            licensePlate.add(plateName);

            JPanel speedBar = new JPanel();
            Dimension speedLength = new Dimension((unitWidth*car.getTopSpeed()), 3);
            Box.Filler speedFiller = new Box.Filler(speedLength, speedLength, speedLength);
            speedBar.add(speedFiller);
            speedBar.setMaximumSize(speedLength);
            speedBar.setBackground(statColors[car.getTopSpeed()]);
            speedBar.setToolTipText(car.getTopSpeed() + " TOP SPEED");

            Dimension speedSpacerLength = new Dimension((unitWidth*(10-car.getTopSpeed())), 3);
            Box.Filler speedSpacer = new Box.Filler(speedSpacerLength, speedSpacerLength, speedSpacerLength);
            JPanel speedBarSpacer = new JPanel();
            speedBarSpacer.add(speedSpacer);
            speedBarSpacer.setMaximumSize(speedSpacerLength);
            speedBarSpacer.setBackground(cardColor);

            JPanel accelerationBar = new JPanel();
            Dimension accelerationLength = new Dimension((unitWidth*car.getAcceleration()), 3);
            Box.Filler accelerationFiller = new Box.Filler(accelerationLength, accelerationLength, accelerationLength);
            accelerationBar.add(accelerationFiller);
            accelerationBar.setMaximumSize(accelerationLength);
            accelerationBar.setBackground(statColors[car.getAcceleration()]);
            accelerationBar.setToolTipText(car.getAcceleration() + " ACCELERATION");

            Dimension accelerationSpacerLength = new Dimension((unitWidth*(10-car.getAcceleration())), 3);
            Box.Filler accelerationSpacer = new Box.Filler(accelerationSpacerLength, accelerationSpacerLength, accelerationSpacerLength);
            JPanel accelerationBarSpacer = new JPanel();
            accelerationBarSpacer.add(accelerationSpacer);
            accelerationBarSpacer.setMaximumSize(accelerationSpacerLength);
            accelerationBarSpacer.setBackground(cardColor);

            JPanel handlingBar = new JPanel();
            Dimension handlingLength = new Dimension((unitWidth*car.getHandling()), 3);
            Box.Filler handlingFiller = new Box.Filler(handlingLength, handlingLength, handlingLength);
            handlingBar.add(handlingFiller);
            handlingBar.setMaximumSize(handlingLength);
            handlingBar.setBackground(statColors[car.getHandling()]);
            handlingBar.setToolTipText(car.getHandling() + " HANDLING");

            Dimension handlingSpacerLength = new Dimension ((unitWidth*(10-car.getHandling())), 3);
            Box.Filler handlingSpacer = new Box.Filler(handlingSpacerLength, handlingSpacerLength, handlingSpacerLength);
            JPanel handlingBarSpacer = new JPanel();
            handlingBarSpacer.add(handlingSpacer);
            handlingBarSpacer.setMaximumSize(handlingSpacerLength);
            handlingBarSpacer.setBackground(cardColor);

            JPanel statsDisplay = new JPanel();
            statsDisplay.setLayout(new GridBagLayout());
            statsDisplay.setBackground(cardColor);

            cardConstraints.insets = new Insets(0,0,3,0);
            cardConstraints.anchor = GridBagConstraints.WEST;
            cardConstraints.fill = GridBagConstraints.HORIZONTAL;
            //speed fill
            cardConstraints.gridx = 0;
            cardConstraints.gridy = 0;
            cardConstraints.gridwidth = car.getTopSpeed();
            cardConstraints.weightx = 1*(1/car.getTopSpeed());
            cardConstraints.weighty = 0;
            statsDisplay.add(speedBar, cardConstraints);
            //speed spacer
            cardConstraints.gridx = car.getTopSpeed();
            cardConstraints.gridy = 0;
            cardConstraints.gridwidth = 10-(car.getTopSpeed());
            cardConstraints.weightx = 10-(1*(1/car.getTopSpeed()));
            cardConstraints.weighty = 0;
            statsDisplay.add(speedBarSpacer, cardConstraints);
            //acceleration fill
            cardConstraints.gridx = 0;
            cardConstraints.gridy = 1;
            cardConstraints.gridwidth = car.getAcceleration();
            cardConstraints.weightx = 1*(1/car.getAcceleration());
            cardConstraints.weighty = 0;
            statsDisplay.add(accelerationBar, cardConstraints);
            //acceleration spacer
            cardConstraints.gridx = car.getAcceleration();
            cardConstraints.gridy = 1;
            cardConstraints.gridwidth = 10-(car.getAcceleration());
            cardConstraints.weightx = 10-(1*(1/car.getAcceleration()));
            cardConstraints.weighty = 0;
            statsDisplay.add(accelerationBarSpacer, cardConstraints);
            //handling fill
            cardConstraints.gridx = 0;
            cardConstraints.gridy = 2;
            cardConstraints.gridwidth = car.getHandling();
            cardConstraints.weightx = 1*(1/car.getHandling());
            cardConstraints.weighty = 0;
            statsDisplay.add(handlingBar, cardConstraints);
            //handling spacer
            cardConstraints.gridx = car.getHandling();
            cardConstraints.gridy = 2;
            cardConstraints.gridwidth = 10-(car.getHandling());
            cardConstraints.weightx = 10-(1*(1/car.getHandling()));
            cardConstraints.weighty = 0;
            statsDisplay.add(handlingBarSpacer, cardConstraints);

            JPanel previewCenter = new JPanel();
            previewCenter.setLayout(new BoxLayout(previewCenter, BoxLayout.Y_AXIS));
            previewCenter.add(thumbnails.get(cars.indexOf(car)));
            previewCenter.setBackground(cardColor);
            previewCenter.add(licensePlate);
            previewCenter.add(statsDisplay);

            cards.get(cars.indexOf(car)).add(new Box.Filler((new Dimension(cardWidth, 20)), (new Dimension(cardWidth, 20)), (new Dimension(cardWidth, 20))), BorderLayout.NORTH);
            cards.get(cars.indexOf(car)).add(previewCenter, BorderLayout.CENTER);
            cards.get(cars.indexOf(car)).add(new Box.Filler((new Dimension(20, cardHeight-40)), (new Dimension(20, cardHeight-40)), (new Dimension(20, cardHeight-40))), BorderLayout.WEST);
            cards.get(cars.indexOf(car)).add(new Box.Filler((new Dimension(20, cardHeight-40)), (new Dimension(20, cardHeight-40)), (new Dimension(20, cardHeight-40))), BorderLayout.EAST);
            cards.get(cars.indexOf(car)).add(new Box.Filler((new Dimension(cardWidth, 8)), (new Dimension(cardWidth, 8)), (new Dimension(cardWidth, 8))), BorderLayout.SOUTH);
            previewCards.add(cards.get(cars.indexOf(car)), car);
        }
    }

    public void changePreviewDisplay(String name){
        previewName = name;
        nameLabel.setText(previewName.toUpperCase());
        previewLabel.setIcon(getScaledIcon(previewName, previewScale));
        uiGrid.revalidate();
    }
    private ImageIcon getScaledIcon(String name, float scaleFactor){
        int iconX = (int) (images.get(name).getWidth() * scaleFactor);
        int iconY = (int) (images.get(name).getHeight() * scaleFactor);

        Image scaledPreviewImage = images.get(name).getScaledInstance(iconX, iconY, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(iconX, iconY, BufferedImage.TYPE_INT_ARGB);

        image.getGraphics().drawImage(scaledPreviewImage, 0, 0, null);

        return new ImageIcon(image);
    }

    public void toTrack(Node head, ArrayList <Car> cars) {
        track = new TrackPanel(head, cars);
        track.setBackground(backgroundColor);
        track.setPreferredSize(gameBounds);
        createPreviewCards(cars);
        game.removeAll();
        game.add(previewCards, BorderLayout.WEST);
        game.add(track, BorderLayout.CENTER);
        this.setContentPane(game);
        this.pack();
        this.revalidate();
        this.repaint();
        track.repaint();
    }

    /**
     * Shows the results of every race after it has been concluded and prompts the user to continue the game.
     * @param placedCars The list of Cars in order from 1st to last.
     * @param raceTime How much time the race took in seconds as a double.
     * @return A boolean indicated whether the user would like to continue the game.
     */
    public boolean showResults(ArrayList<Car> placedCars, double raceTime) {
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