import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends JFrame{
    private int gameWidth, gameHeight, frameWidth, frameHeight, cardWidth, cardHeight;
    private float previewScale;
    private final Dimension gameBounds;
    private final Dimension windowBounds;
    private final Dimension controlBounds;
    private String previewName;
    private Color backgroundColor, foregroundColor;
    private final Font plain = new Font("Calibri", Font.PLAIN, 18);
    private final Font bold = new Font("Calibri", Font.BOLD, 22);
    private final Font boldSmall = new Font("Calibri", Font.BOLD, 18);
    private final Font boldLarge = new Font("Calibri", Font.BOLD, 36);
    private HashMap<String, BufferedImage> images;
    private JLabel[] thumbLabels;
    private ArrayList<JPanel> cards, thumbnails;
    private JPanel menu, uiGrid, uiBox, previewWindow, game, previewCards, race, track, controls;
    private JSlider speedSlider, accelerationSlider, handlingSlider;
    private JButton startButton, hurryButton, pauseButton, lastPreviewButton, nextPreviewButton;
    private JLabel selectVehicle, allocateStatsLabel, speedLabel, accelerationLabel, handlingLabel, budgetLabel, budgetValue, nameLabel, previewLabel;

    //default constructor
    public GUI(){
        this("Untitled Racing Game", new JComponent[]{}, new HashMap<String, BufferedImage>(), 800, 600);
    }

    /**
     * GUI constructor called by Grand Prix
     * @param title Title of the window
     * @param inputs array of interactive components (buttons and sliders)
     * @param images map from car names to loaded bufferedimages
     * @param width width of the frame to create
     * @param height height of the frame to create
     */
    public GUI(String title, JComponent[] inputs, HashMap<String, BufferedImage> images, int width, int height){
        super(title);

        this.images = images;

        this.foregroundColor = new Color(222, 232, 243);
        this.backgroundColor = new Color(115, 122, 148);

        gameWidth = width;
        gameHeight = height;
        frameWidth = (int) (gameWidth * 1.25);//sets up frame to have 20% more horizontal width than game window for preview cards during races
        frameHeight = (int) (frameWidth * 0.5625);//forces frame to 16:9 aspect ratio
        if (gameHeight > frameHeight) frameHeight = (int) (gameHeight * 1.15);//failsafe for unexpected game dimensions
        cardWidth = frameWidth - gameWidth;
        cardHeight = frameHeight / 4;
        gameBounds = new Dimension(gameWidth, gameHeight);
        windowBounds = new Dimension(frameWidth, frameHeight);
        controlBounds = new Dimension(gameWidth, (frameHeight - gameHeight));

        menu = new JPanel(); //has uiGrid and ridePreviewPanel
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        menu.setBackground(backgroundColor);

        game = new JPanel(new BorderLayout());
        game.setBackground(this.backgroundColor);
        game.setForeground(this.foregroundColor);

        controls = new JPanel(new GridBagLayout());
        controls.setPreferredSize(controlBounds);
        controls.setMaximumSize(controlBounds);
        controls.setMinimumSize(controlBounds);
        controls.setBackground(this.backgroundColor);

        initializeMenuComponents(inputs);

        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * sets up buttons and sliders to look correct and to have proper names
     * also sets up tooltips on sliders and point budget
     * @param inputs interactive components to set attributes on
     */
    private void initializeMenuComponents(JComponent[] inputs) {
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.insets = new Insets(0,20,0,20);
        for (JComponent input : inputs) {
            if (input instanceof JButton) {
                JButton button = (JButton) input;
                button.setFont(bold);
                switch (button.getActionCommand()) {
                    case "race":
                        startButton = button;
                        startButton.setText("START RACE");
                        break;
                    case "fast":
                        hurryButton = button;
                        hurryButton.setText("Hurry");
                        buttonConstraints.gridx = 1;
                        buttonConstraints.weightx = 0.5;
                        buttonConstraints.anchor = GridBagConstraints.WEST;
                        controls.add(hurryButton, buttonConstraints);

                        break;
                    case "stop":
                        pauseButton = button;
                        pauseButton.setText("Pause");
                        buttonConstraints.gridx = 0;
                        buttonConstraints.weightx = 0.5;
                        buttonConstraints.anchor = GridBagConstraints.EAST;
                        controls.add(pauseButton, buttonConstraints);
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
            } else if (input instanceof JSlider) {
                JSlider slider = (JSlider) input;
                switch (slider.getName()) {
                    case "spd":
                        speedSlider = slider;
                        speedSlider.setPaintTicks(true);
                        speedSlider.setMajorTickSpacing(1);
                        speedSlider.setBackground(backgroundColor);
                        break;
                    case "acc":
                        accelerationSlider = slider;
                        accelerationSlider.setPaintTicks(true);
                        accelerationSlider.setMajorTickSpacing(1);
                        accelerationSlider.setBackground(backgroundColor);
                        break;
                    case "han":
                        handlingSlider = slider;
                        handlingSlider.setPaintTicks(true);
                        handlingSlider.setMajorTickSpacing(1);
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
        selectVehicle.setFont(bold);
        selectVehicle.setForeground(foregroundColor);
        allocateStatsLabel = new JLabel("ALLOCATE STAT POINTS:", JLabel.CENTER);
        allocateStatsLabel.setFont(bold);
        allocateStatsLabel.setForeground(foregroundColor);
        speedLabel = new JLabel("Top Speed", JLabel.CENTER);
        speedLabel.setFont(bold);
        speedLabel.setForeground(foregroundColor);

        accelerationLabel = new JLabel("Acceleration", JLabel.CENTER);
        accelerationLabel.setFont(bold);
        accelerationLabel.setForeground(foregroundColor);

        handlingLabel = new JLabel("Handling", JLabel.CENTER);
        handlingLabel.setFont(bold);
        handlingLabel.setForeground(foregroundColor);

        budgetLabel = new JLabel("POINTS AVAILABLE: ", JLabel.RIGHT);
        budgetLabel.setFont(bold);
        budgetLabel.setForeground(foregroundColor);

        budgetValue = new JLabel("00", JLabel.LEFT);
        budgetValue.setFont(bold);
        budgetValue.setForeground(Color.GREEN);

        //JToolTips
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(8000);
        UIManager.put("ToolTip.font", plain);
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

    /**
     * Swaps out the current content pane with the stat picking menu JPanel
     */
    public void switchToPlayerMenu() {
        setContentPane(menu);
        this.pack();
        this.revalidate();
    }

    /**
     * Initializes the player stat picking menu JPanel
     * @param budget amount of points to start with
     */
    public void playerMenu(int budget) {
        GridBagConstraints menuConstraints = new GridBagConstraints();
        menu.setPreferredSize(windowBounds);
        menu.setMinimumSize(windowBounds);
        int menuWidth = 320;
        if (System.getProperty("os.name").contains("Mac")) {
            menuWidth = 400;
        }
        int menuHeight = 1080;
        Dimension boxBounds = new Dimension(menuWidth + 40, menuHeight);
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

        Insets zero = new Insets(0, 0, 0, 0);
        Insets vertical5_10 = new Insets(5, 0, 10, 0);
        Insets vertical50_20 = new Insets(50, 0, 20, 0);

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
        nameLabel.setFont(boldLarge);
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
        previewWindow.setMinimumSize(new Dimension(1700, 830));

        uiBox.add(uiGrid, BorderLayout.EAST);
        menu.add(uiBox, BorderLayout.WEST);
        menu.add(previewWindow, BorderLayout.CENTER);
    }

    /**
     * updates the tooltips for the stat sliders
     * @param topSpeed new topspeed settings
     * @param acceleration new acceleration setting
     * @param handling new handling setting
     * @param budget new budget
     * @param updateSliders whether to update the sliders
     */
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

    /**
     * initializes the preview cars (on the left side of the track screen)
     * @param cars list of cars to initialize preview cards with
     */
    public void createPreviewCards(ArrayList<Car> cars) {

        Color cardColor = new Color(190, 198, 211);
        Color[] statColors = new Color[]{new Color(255, 0, 255), new Color(255, 0, 0), new Color(255, 100, 0), new Color(255, 145, 0), new Color(255, 199, 0), new Color(255, 251, 0), new Color(221, 255, 0), new Color(191, 255, 0), new Color(140, 255, 0), new Color(0, 255, 140), new Color(0, 255, 196)};
        int unitWidth = (cardWidth - 40) / 10;
        cards = new ArrayList<JPanel>();
        thumbnails = new ArrayList<JPanel>();
        previewCards = new JPanel();
        previewCards.setLayout(new BoxLayout(previewCards, BoxLayout.Y_AXIS));
        previewCards.setBackground(cardColor);

        previewCards.setPreferredSize(new Dimension(cardWidth, frameHeight));
        previewCards.setMinimumSize(new Dimension(cardWidth, frameHeight));
        previewCards.setMaximumSize(new Dimension(cardWidth, frameHeight));

        thumbLabels = new JLabel[cars.size()];

        GridBagConstraints cardConstraints = new GridBagConstraints();
        for (Car car : cars) {
            String img = car.getImageName();
            cards.add(cars.indexOf(car), new JPanel(new BorderLayout()));

            cards.get(cars.indexOf(car)).setPreferredSize(new Dimension(cardWidth, cardHeight));
            cards.get(cars.indexOf(car)).setMinimumSize(new Dimension(cardWidth, cardHeight));
            cards.get(cars.indexOf(car)).setMaximumSize(new Dimension(cardWidth, cardHeight));
            cards.get(cars.indexOf(car)).setBackground(cardColor);

            //Process thumbnail from image
            float thumbScale = ((float) (((cardWidth-40)*0.4))) / ((float) ((images.get(img).getHeight())));
            thumbLabels[cars.indexOf(car)] = new JLabel(getScaledIcon(car.getImageName(), thumbScale));

            thumbnails.add(cars.indexOf(car), new JPanel());
            thumbnails.get(cars.indexOf(car)).setLayout(new GridBagLayout());
            thumbnails.get(cars.indexOf(car)).setBackground(foregroundColor);//leave as foreground
            cardConstraints.insets = new Insets(0, 0, 0, 0);
            cardConstraints.anchor = GridBagConstraints.SOUTH;
            cardConstraints.weightx = 0;
            cardConstraints.weighty = 1.0;
            thumbnails.get(cars.indexOf(car)).add(thumbLabels[cars.indexOf(car)], cardConstraints);
            thumbnails.get(cars.indexOf(car)).setPreferredSize(new Dimension(cardWidth-40, (int)((cardWidth-40)*0.4)));
            thumbnails.get(cars.indexOf(car)).setMaximumSize(new Dimension(cardWidth-40, 120));

            JPanel licensePlate = new JPanel();
            licensePlate.setBackground(cardColor);
            JLabel plateName = new JLabel(car.getImageName().toUpperCase());
            plateName.setFont(boldSmall);
            licensePlate.add(plateName);

            JPanel speedBar = new JPanel();
            Dimension speedLength = new Dimension((unitWidth * car.getTopSpeed()), 3);
            Component speedFiller = Box.createHorizontalStrut(unitWidth * car.getTopSpeed());
            speedBar.add(speedFiller);
            speedBar.setMaximumSize(speedLength);
            speedBar.setBackground(statColors[car.getTopSpeed()]);
            speedBar.setToolTipText(car.getTopSpeed() + " TOP SPEED");

            Dimension speedSpacerLength = new Dimension((unitWidth * (10 - car.getTopSpeed())), 3);
            Component speedSpacer = Box.createHorizontalStrut(unitWidth * (10 - car.getTopSpeed()));
            JPanel speedBarSpacer = new JPanel();
            speedBarSpacer.add(speedSpacer);
            speedBarSpacer.setMaximumSize(speedSpacerLength);
            speedBarSpacer.setBackground(cardColor);

            JPanel accelerationBar = new JPanel();
            Dimension accelerationLength = new Dimension((unitWidth * car.getAcceleration()), 3);
            Component accelerationFiller = Box.createHorizontalStrut(unitWidth * car.getAcceleration());
            accelerationBar.add(accelerationFiller);
            accelerationBar.setMaximumSize(accelerationLength);
            accelerationBar.setBackground(statColors[car.getAcceleration()]);
            accelerationBar.setToolTipText(car.getAcceleration() + " ACCELERATION");

            Dimension accelerationSpacerLength = new Dimension((unitWidth * (10 - car.getAcceleration())), 3);
            Component accelerationSpacer = Box.createHorizontalStrut(unitWidth * (10 - car.getAcceleration()));
            JPanel accelerationBarSpacer = new JPanel();
            accelerationBarSpacer.add(accelerationSpacer);
            accelerationBarSpacer.setMaximumSize(accelerationSpacerLength);
            accelerationBarSpacer.setBackground(cardColor);

            JPanel handlingBar = new JPanel();
            Dimension handlingLength = new Dimension((unitWidth * car.getHandling()), 3);
            Component handlingFiller = Box.createHorizontalStrut(unitWidth * car.getHandling());
            handlingBar.add(handlingFiller);
            handlingBar.setMaximumSize(handlingLength);
            handlingBar.setBackground(statColors[car.getHandling()]);
            handlingBar.setToolTipText(car.getHandling() + " HANDLING");

            Dimension handlingSpacerLength = new Dimension((unitWidth * (10 - car.getHandling())), 3);
            Component handlingSpacer = Box.createHorizontalStrut(unitWidth * (10 - car.getHandling()));
            JPanel handlingBarSpacer = new JPanel();
            handlingBarSpacer.add(handlingSpacer);
            handlingBarSpacer.setMaximumSize(handlingSpacerLength);
            handlingBarSpacer.setBackground(cardColor);

            JPanel statsDisplay = new JPanel();
            statsDisplay.setLayout(new GridBagLayout());
            statsDisplay.setBackground(cardColor);

            //speed fill
            cardConstraints.anchor = GridBagConstraints.WEST;
            cardConstraints.insets = new Insets(0, 0, 3, 0);
            cardConstraints.gridx = 1;
            cardConstraints.gridy = 0;
            cardConstraints.gridwidth = car.getTopSpeed();
            cardConstraints.gridheight = 1;
            cardConstraints.weighty = 0;
            statsDisplay.add(speedBar, cardConstraints);


            //acceleration fill
            cardConstraints.gridx = 1;
            cardConstraints.gridy = 1;
            cardConstraints.gridwidth = car.getAcceleration();
            cardConstraints.weighty = 0;
            statsDisplay.add(accelerationBar, cardConstraints);

            //handling fill
            cardConstraints.gridx = 1;
            cardConstraints.gridy = 2;
            cardConstraints.gridwidth = car.getHandling();
            cardConstraints.weighty = 0;
            statsDisplay.add(handlingBar, cardConstraints);

            //speed spacer
            cardConstraints.gridx = car.getTopSpeed();
            cardConstraints.gridy = 0;
            cardConstraints.gridwidth = 10 - car.getTopSpeed();
            cardConstraints.weighty = 0;
            statsDisplay.add(speedBarSpacer, cardConstraints);

            //acceleration spacer
            cardConstraints.gridx = car.getAcceleration();
            cardConstraints.gridy = 1;
            cardConstraints.gridwidth = 10 - car.getAcceleration();
            cardConstraints.weighty = 0;
            statsDisplay.add(accelerationBarSpacer, cardConstraints);

            //handling spacer
            cardConstraints.gridx = car.getHandling();
            cardConstraints.gridy = 2;
            cardConstraints.gridwidth = 10 - car.getHandling();
            cardConstraints.weighty = 0;
            statsDisplay.add(handlingBarSpacer, cardConstraints);

            JPanel previewCenter = new JPanel();
            previewCenter.setLayout(new BoxLayout(previewCenter, BoxLayout.Y_AXIS));
            previewCenter.add(thumbnails.get(cars.indexOf(car)));
            previewCenter.setBackground(cardColor);
            previewCenter.add(licensePlate);
            previewCenter.add(statsDisplay);

            cards.get(cars.indexOf(car)).add(Box.createVerticalStrut(20), BorderLayout.NORTH);
            cards.get(cars.indexOf(car)).add(previewCenter, BorderLayout.CENTER);
            cards.get(cars.indexOf(car)).add(Box.createHorizontalStrut(20), BorderLayout.WEST);
            cards.get(cars.indexOf(car)).add(Box.createHorizontalStrut(20), BorderLayout.EAST);
            cards.get(cars.indexOf(car)).add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
            previewCards.add(cards.get(cars.indexOf(car)), car);
        }
    }

    /**
     * changes the selected car on the player stat picking menu
     * @param name name of the car to change preview to
     */
    public void changePreviewDisplay(String name) {
        previewName = name;
        nameLabel.setText(previewName.toUpperCase());
        previewLabel.setIcon(getScaledIcon(previewName, previewScale));
        uiGrid.revalidate();
    }

    /**
     * scales the selected car image and returns it as an ImageIcon
     * @param name name of the car preview image to scale
     * @param scaleFactor factor to scale by
     * @return ImageIcon of the scaled image
     */
    private ImageIcon getScaledIcon(String name, float scaleFactor) {
        int iconX = (int) (images.get(name).getWidth() * scaleFactor);
        int iconY = (int) (images.get(name).getHeight() * scaleFactor);

        Image scaledPreviewImage = images.get(name).getScaledInstance(iconX, iconY, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(iconX, iconY, BufferedImage.TYPE_INT_ARGB);

        image.getGraphics().drawImage(scaledPreviewImage, 0, 0, null);

        return new ImageIcon(image);
    }

    /**
     * Initializes the TrackPanel and adds it to the race panel.
     * This method is called when the Start Race button is pressed.
     * @param head head node of the track
     * @param cars list of cars to pass to track
     */
    public void toTrack(Node head, ArrayList<Car> cars) {
        race = new JPanel(new BorderLayout());
        race.setPreferredSize(new Dimension(gameWidth, frameHeight));
        track = new TrackPanel(head, cars);
        track.setBackground(backgroundColor);
        track.setPreferredSize(gameBounds);

        race.add(track, BorderLayout.CENTER);
        race.add(controls, BorderLayout.SOUTH);

        createPreviewCards(cars);

        game.removeAll();
        game.add(previewCards, BorderLayout.WEST);
        game.add(race, BorderLayout.CENTER);

        this.setContentPane(game);
        this.pack();
        this.revalidate();
        this.repaint();
        track.repaint();
    }

    /**
     * Shows the results of every race after it has been concluded and prompts the user to continue the game.
     *
     * @param placedCars The list of Cars in order from 1st to last in the previous race.
     * @param raceTime   How much time the race took in seconds.
     * @return A boolean indicated whether the user would like to continue the game.
     */
    public boolean showResults(ArrayList<Car> placedCars, int raceTime) {
        boolean userContinue;
        int i;
        int playerPlacement = 0;
        float placementScale;
        boolean gameWon;
        GridLayout placementGrid = new GridLayout(2, placedCars.size());
        String carName;
        Car lastCar = placedCars.get(placedCars.size() - 1);
        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        results.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel placementPanel = new JPanel(placementGrid);

        JLabel playerPositionLabel = new JLabel();
        playerPositionLabel.setMinimumSize(new Dimension(400, 0));
        playerPositionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel raceTimeLabel = new JLabel();
        raceTimeLabel.setMinimumSize(new Dimension(400, 0));
        raceTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel playerWinLabel = new JLabel();
        playerWinLabel.setMinimumSize(new Dimension(400, 0));
        playerWinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel loserLabel = new JLabel();
        loserLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel promptLabel = new JLabel();
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font messageFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        playerPositionLabel.setFont(messageFont);
        raceTimeLabel.setFont(messageFont);
        loserLabel.setFont(messageFont);
        promptLabel.setFont(messageFont);

        // Find the position that the player finished the race.
        for (i = 0; i < placedCars.size(); i++) {
            if (placedCars.get(i).isPlayer()) playerPlacement = i + 1;
        }

        gameWon = placedCars.size() == 2 && playerPlacement == 1;

        // If player beat the game show their stats and wrap up run.
        if (gameWon) {
            Car player = placedCars.get(0);
            Car opponent = placedCars.get(1);
            carName = player.getImageName();
            placementPanel.setLayout(new BoxLayout(placementPanel, BoxLayout.Y_AXIS));
            placementPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            playerPositionLabel.setText("You win!");
            playerPositionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
            raceTimeLabel.setText("The final race lasted " + raceTime + " seconds.");
            playerWinLabel.setText("Your winning car is:");
            playerWinLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));

            JLabel playerStats = new JLabel("Spd-" + player.getTopSpeed() + " Acc-" + player.getAcceleration() + " Han-" + player.getHandling());
            playerStats.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerStats.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));

            placementScale = ((float) (80)) / ((float) ((images.get(carName).getHeight())));
            JLabel playerImageLabel = new JLabel();
            playerImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerImageLabel.setIcon(getScaledIcon(carName, placementScale));

            carName = opponent.getImageName();
            placementScale = ((float) (60)) / ((float) ((images.get(carName).getHeight())));
            JLabel opponentImageLabel = new JLabel();
            opponentImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            opponentImageLabel.setIcon(getScaledIcon(carName, placementScale));

            JLabel loserStats = new JLabel("Spd-" + opponent.getTopSpeed() + " Acc-" + opponent.getAcceleration() + " Han-" + opponent.getHandling());
            loserStats.setAlignmentX(Component.CENTER_ALIGNMENT);
            loserStats.setFont(messageFont);

            loserLabel.setText("The car you bested was:");

            promptLabel.setText("Would you like to try another run?");

            placementPanel.add(playerWinLabel);
            placementPanel.add(playerImageLabel);
            placementPanel.add(playerStats);
            placementPanel.add(Box.createVerticalStrut(50));
            placementPanel.add(loserLabel);
            placementPanel.add(opponentImageLabel);
            placementPanel.add(loserStats);
        } else {
            String placementSuffix;
            // Find the proper suffix for player's placement.
            switch (playerPlacement) {
                case 1:
                    placementSuffix = "st";
                    break;
                case 2:
                    placementSuffix = "nd";
                    break;
                case 3:
                    placementSuffix = "rd";
                    break;
                default:
                    placementSuffix = "th";
            }

            playerPositionLabel.setText("You placed " + playerPlacement + placementSuffix);
            raceTimeLabel.setText("The race lasted " + raceTime + " seconds.");
            loserLabel.setText(((playerPlacement == placedCars.size()) ? "You lost. Your" : lastCar.getImageName() + "'s") + " ending stats were: " +
                    "Spd-" + lastCar.getTopSpeed() + ", Acc-" + lastCar.getAcceleration() + ", Han-" + lastCar.getHandling());
            // Change the message shown in the prompt label to reflect if the player has lost.
            promptLabel.setText((playerPlacement == placedCars.size()) ? "Try again?" : "Do you want to continue to the next round?");


            // Add every Car's preview image and the corresponding placement in the prior race to panels.
            for (i = 0; i < placedCars.size(); i++) {
                // Getting Cars in opposing order from labels to accommodate GridLayouts format.
                carName = placedCars.get((placedCars.size() - i) - 1).getImageName();
                placementScale = ((float) (80)) / ((float) ((images.get(carName).getHeight())));

                JLabel nextImageLabel = new JLabel("", JLabel.CENTER);
                nextImageLabel.setIcon(getScaledIcon(carName, placementScale));

                JLabel rankingLabel = new JLabel("#" + (i + 1), JLabel.CENTER);
                rankingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (nextImageLabel.getPreferredSize().getHeight() * 3 / 4)));

                placementPanel.add(nextImageLabel, 0, i);
                placementPanel.add(rankingLabel, 1, i);

            }
        }

        results.add(playerPositionLabel);
        results.add(raceTimeLabel);
        results.add(placementPanel);
        if (!gameWon) results.add(loserLabel);
        results.add(Box.createVerticalStrut(25));
        results.add(promptLabel);

        // Show all the components in the form of a ConfirmDialog. Save the user response as a boolean (false if no).
        userContinue = JOptionPane.showConfirmDialog(this, results, "Race Results", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) != 1;
        return userContinue;
    }
}