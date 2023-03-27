import graphics.GUI;
import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * GroceryGrandPrix Creates and manages the components required for a grocery themed racing simulation game.
 * The loop of the game involves going through this series of events:
 *                      Assign Points -> Watch Race -> See Results -> Assign Points etc.
 * Depending on the result of the race the game may continue or be reset.
 *
 * A series of sliders utilizing GroceryGrandPrix are used to assign a set of allotted points to a users stats.
 * CarStats objects are passed to Cars to hold the three stats. The Cars drive on the track after being initialized.
 * A linked list of Nodes is used to procedurally generate a circular track.
 * GUI is used to render all elements of the game.
 */
public class GroceryGrandPrix implements ActionListener, ChangeListener {
    private final int framerate = 30;
    private boolean hurry;// Whether or not the game is in hurry mode.
    private int chosenCarNameIndex; // The index corresponding to the name of the player's car name in carNames.
    private int round;
    private int playerBudget; // Keeps track of the current budget of points the user has.
    private final int startBudget; // Holds the number of points the player will get to spend in the first menu.
    private final int postRaceBudget; // Holds the number of points the player will get to spend in menus after the first.
    private final int trackX = 1280;
    private final int trackY = 780;
    private final int maxStat = 10;
    private final int statStart; // Where each of the players stats will start.
    private int timeElapsedMs;
    private GUI gui;
    private Node trackHead; // The first node created in the linked list of nodes that make up the track.
    private String[] carNames; // Holds the names of all of the car types read from files.
    private ArrayList <Car> cars;
    private CarStats playerStats;
    private Timer gameLoop; // The Timer that has the race simulation as a listener.

    public GroceryGrandPrix() {
        this(6, 3, 3);
    }

    /**
     * A parameter constructor in case the default balance is not preferred.
     * @param startBudget Stat points the user will get to spend at the start of the game.
     * @param postRaceBudget Stat points the user will get to spend on the rounds after round one.
     * @param statStart Starting part for all three stats.
     */
    public GroceryGrandPrix(int startBudget, int postRaceBudget, int statStart) {
        chosenCarNameIndex = 0;
        this.postRaceBudget = postRaceBudget;
        this.statStart = statStart;
        this.startBudget = startBudget;
        playerBudget = this.startBudget;
        round = 1;
        cars = new ArrayList<>();
        playerStats = new CarStats(statStart, statStart, statStart);
        try {
            initializeGUI();
            gui.playerMenu(playerBudget);
            showMenu();
            gui.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates the GUI object used to display the game.
     * @throws IOException if the file reading of the required images goes wrong.
     */
    private void initializeGUI() throws IOException {
        if (gui != null){
            gui.dispose();
        }
        gui = new GUI("Grocery Grand Prix", createInputs(), fileReader(), trackX, trackY);
    }

    /**
     * Reads a group of image files from a preset relative directory.
     * @return A HashMap of the names of the image files to their images.
     * @throws IOException if the contents of the files cannot be read as an image.
     */
    private HashMap<String, BufferedImage> fileReader() throws IOException {
        File[] previewFiles = (new File("assets/previews")).listFiles();
        assert previewFiles != null;

        carNames = new String[previewFiles.length];
        HashMap<String, BufferedImage> previewMap = new HashMap<String, BufferedImage>();
        for (int i = 0; i < previewFiles.length; i++) {
            carNames[i] = previewFiles[i].getName().split("_")[1];
            BufferedImage readImage = ImageIO.read(previewFiles[i]);
            if (readImage == null) {
                throw new IOException(String.format("Image %s failed to read. Are you sure it is an image?", previewFiles[i]));
            }
            previewMap.put(carNames[i], readImage);
            //System.out.println("Name " + (i+1) + ": " + carNames[i] + "\n");
        }
        return previewMap;
    }

    /**
     * Simple method that transitions the game to the menu. Ensures that any changes to stats are represented in the GUI.
     */
    public void showMenu() {
        gui.updateStatLabels(playerStats.getTopSpeedNumeral(), playerStats.getAccelerationNumeral(),
                playerStats.getHandlingNumeral(), playerBudget, true);
        gui.switchToPlayerMenu();
    }

    /**
     * Guides the transition from the menu to the track.
     * Readies the cars depending on the round, and creates a new track.
     */
    private void startNextRace() {
        timeElapsedMs = 0;
        generateNodes(round+5);

        if (round == 1) {
            generateCars();
        } else {
            Node carStartNode = trackHead;
            for (Car car : cars) {
                if (!car.isPlayer()) {
                    car.setAllStats(distributeCarStats(car.getTopSpeed(), car.getAcceleration(), car.getHandling(), postRaceBudget));
                }
                car.setGoalNode(carStartNode);
                carStartNode = carStartNode.next();
            }
        }
        gui.toTrack(trackHead, cars);
    }

    /**
     * Used to start the race.
     * Creates and starts a Timer object that will control the Car objects owned by the game.
     * After the simulation has finished the simulator will restart the game if the player has lost and wishes to try again,
     * go to the next round if the player has not lost and wishes to continue or close the game if the player is done.
     */
    private void startSimulation() {
        ArrayList<Car> carPlacements = new ArrayList<Car>();
        final int timerDelayMs = 1000 / framerate;

        // Timer is given a lambda expression to control the full simulation of the Cars driving around the track.
        gameLoop = new Timer(timerDelayMs, e -> {
            int inGameTimePassed = (timerDelayMs) * ((hurry) ? 2 : 1);
            if (carPlacements.size() < 5 - round) { // Check if all the Cars have finished the race.
                for (Car car : cars) {
                    if (!carPlacements.contains(car)) { // Stop the Cars after they finish the race.
                        // Save the cars in finishing order.
                        if (car.drive(inGameTimePassed)) carPlacements.add(car);
                    }
                }
                timeElapsedMs += inGameTimePassed;
                gui.repaint();
            } else {
                gameLoop.stop();
                boolean continueGame = gui.showResults(carPlacements,  (int)(timeElapsedMs /1000.0));
                Car lastCar = carPlacements.get(carPlacements.size() - 1);

                // Determines how the game will continue based on user input and the user's placement.
                if (continueGame) {
                    if (lastCar.isPlayer()) {
                        restart();
                    } else {
                        cars.remove(lastCar);
                        round++;
                        playerBudget = postRaceBudget;
                        showMenu();
                    }
                } else gui.dispose();
            }
        });
        gameLoop.start();
    }

    /**
     * Restarts the game back to a state where it can be played again.
     */
    private void restart() {
        playerBudget = startBudget;
        chosenCarNameIndex = 0;
        round = 1;
        playerStats = new CarStats(statStart, statStart, statStart);
        showMenu();
    }

    /**
     * Generates four Car objects and adds them to the cars ArrayList.
     */
    private void generateCars() {
        cars.clear();
        Node carStartNode = trackHead;
        int nameIndex = chosenCarNameIndex;
        // 1 is subtracted from statStart to compensate for the bots starting at 1 in each stat.
        int statPoints = startBudget + ((statStart - 1) * 3);
        int[] stats;
        List<String> availableCarNames = new ArrayList<String>(Arrays.asList(carNames));

        // Creates 4 cars; 3 bots and 1 player.
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                nameIndex = (int) (Math.random() * availableCarNames.size());
                stats = distributeCarStats(1, 1, 1, statPoints);
                cars.add(new Car(availableCarNames.get(nameIndex), new CarStats(stats), carStartNode, false));
            } else {
                cars.add(new Car(availableCarNames.get(nameIndex), playerStats, carStartNode, true));
            }
            availableCarNames.remove(nameIndex);
            carStartNode = carStartNode.next();
        }
    }

    /**
     * Distributes a given number of stat points evenly among three stats using a loop.
     * Stat parameters are to allow for starting values.
     * @param stat1 Represents the first stat given.
     * @param stat2 Represents the second stat given.
     * @param stat3 Represents the third stat given.
     * @param statPoints Determines the number of stats that will be distributed.
     * @return An array of integers holding the stat values after points have been distributed.
     */
    private int[] distributeCarStats(int stat1, int stat2, int stat3, int statPoints) {
        double statPicker;
        // Keeps track of how many stats have less than the maximum.
        int underMaxStatsCount = ((stat1 != maxStat) ?  1 : 0) + ((stat2 != maxStat) ?  1 : 0) + ((stat3 != maxStat) ?  1 : 0);
        // Distribute all stat points randomly one at a time.
        for (int s = 0; s < statPoints; s++) {
            statPicker = Math.random();
            // Add one to any stat under the maximum. Odds adjust if any stat reaches the maximum.
            if (statPicker < (1.0/underMaxStatsCount) && stat1 != maxStat) {
                stat1++;
                underMaxStatsCount -= (stat1 == maxStat) ? 1 : 0;
            // Long conditional to ensure that stat2 has a 33% to start, and a 50% chance when either stat has been maxed.
            } else if (((statPicker < (2.0/underMaxStatsCount) && stat1 != maxStat) || (statPicker < 1.0/underMaxStatsCount)) && stat2 != maxStat) {
                stat2++;
                underMaxStatsCount -= (stat2 == maxStat) ? 1 : 0;
            } else if (stat3 != maxStat) {
                stat3++;
                underMaxStatsCount -= (stat3 == maxStat) ? 1 : 0;
            }
        }

        return new int[]{stat1, stat2, stat3};
    }

    private void generateNodes(int number) {
        Random rand = new Random();
        double x = rand.nextDouble()*400 + 50;
        double y = rand.nextDouble()*200 + 50;
        trackHead = new Node(x, y);
        trackHead.setNext(new Node(x+125, y));
        trackHead.next().setNext(new Node(x+250, y));
        trackHead.next().next().setNext(new Node(x+375, y));
        Node temp = trackHead.next().next().next();
        for (int i = 0; i < number-1; i++) {
            int failsafe = 1000;
            do {
                switch (temp.getQuad()) {
                    case 1:
                        x = rand.nextDouble() * (trackX - temp.getCoord().getX() - 100) + temp.getCoord().getX(); //Goes right
                        y = rand.nextDouble() * (trackY / 2 - 50) + 50; //Stays top
                        break;
                    case 2:
                        x = rand.nextDouble() * (trackX / 2 - 100) + trackX / 2 + 50; //Stays right
                        y = rand.nextDouble() * (temp.getCoord().getY() - 50) + temp.getCoord().getY(); //Goes down
                        break;
                    case 3:
                        x = rand.nextDouble() * (temp.getCoord().getX() - 50) + 50; //Goes left
                        y = rand.nextDouble() * (trackY / 2 - 100) + trackY / 2 + 50; //Stays down
                        break;
                    case 4:
                        x = rand.nextDouble() * (trackX / 2 - 50) + 50;
                        y = rand.nextDouble() * (trackY - temp.getCoord().getY() - 50) + 50; //Goes up
                }
                failsafe -= 1;
                //Checks new quadrant below:
                temp.setNext(new Node(x, y, trackHead, temp));
            }while (((temp.distanceToNext() < 250 && failsafe > 995)|| temp.turn() > 2) && failsafe>0); //Puts a cap on how sharp the turns can be
            temp = temp.next();
        }
        temp = trackHead;
        do {
            System.out.println(temp);
            temp = temp.next();
        } while (temp != trackHead );
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton pressedButton = (JButton) event.getSource();
        switch (pressedButton.getActionCommand().substring(0,4)) {
            case "race" :
                if (playerBudget == 0) {
                    startNextRace();
                    startSimulation();
                }
                break;
            case "fast" :
                hurry = !hurry;
                pressedButton.setText("Set " + ((hurry) ? 1:2) + "x Speed");
                break;
            case "stop" :
                switch (pressedButton.getText()) {
                    case "Unpause":
                        pressedButton.setText("Pause");
                        gameLoop.start();
                        break;
                    case "Pause" :
                        pressedButton.setText("Unpause");
                        gameLoop.stop();
                }
                break;
            case "last" :
                if(round == 1) {
                    if (chosenCarNameIndex > 0) {
                        chosenCarNameIndex--;
                    } else {
                        chosenCarNameIndex = carNames.length - 1;
                    }
                    gui.changePreviewDisplay(carNames[chosenCarNameIndex]);
                }
                break;
            case "next" :
                if(round == 1) {
                    if (chosenCarNameIndex < carNames.length - 1) {
                        chosenCarNameIndex++;
                    } else {
                        chosenCarNameIndex = 0;
                    }
                    gui.changePreviewDisplay(carNames[chosenCarNameIndex]);
                }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        final int sliderValue = slider.getValue();
        final int playerTopSpeed = playerStats.getTopSpeedNumeral();
        final int playerAcceleration = playerStats.getAccelerationNumeral();
        final int playerHandling = playerStats.getHandlingNumeral();

        switch (slider.getName()) {
            case "spd":
                if (playerBudget >= (sliderValue - playerTopSpeed)) {
                    playerStats.setTopSpeedStat(sliderValue);
                    playerBudget -= sliderValue - playerTopSpeed;
                } else {
                    playerStats.setTopSpeedStat(playerTopSpeed + playerBudget);
                    playerBudget = 0;
                }
                break;
            case "acc":
                if (playerBudget >= (sliderValue - playerAcceleration)) {
                    playerStats.setAccelerationStat(sliderValue);
                    playerBudget -= sliderValue - playerAcceleration;
                } else {
                    playerStats.setAccelerationStat(playerAcceleration + playerBudget);
                    playerBudget = 0;
                }
                break;
            case "han":
                if (playerBudget >= (sliderValue - playerHandling)) {
                    playerStats.setHandlingStat(sliderValue);
                    playerBudget -= sliderValue - playerHandling;
                }
                else {
                    playerStats.setHandlingStat(playerHandling + playerBudget);
                    playerBudget = 0;
                }
        }
        // Only forces the sliders to match the corresponding stat value if the player has stopped adjusting the slider.
        gui.updateStatLabels(playerStats.getTopSpeedNumeral(), playerStats.getAccelerationNumeral(),
                playerStats.getHandlingNumeral(), playerBudget, !slider.getValueIsAdjusting());
    }

    /**
     * Gives all the JButtons and JSliders the requisite game functionality.
     */
    private JComponent[] createInputs() {
        JButton startRace = new JButton();
        startRace.setActionCommand("race");
        startRace.addActionListener(this);
        JButton hurry = new JButton();
        hurry.addActionListener(this);
        hurry.setActionCommand("fast");
        JButton pause = new JButton();
        pause.addActionListener(this);
        pause.setActionCommand("stop");
        JButton previousCar = new JButton();
        previousCar.addActionListener(this);
        previousCar.setActionCommand("last");
        JButton nextCar = new JButton();
        nextCar.addActionListener(this);
        nextCar.setActionCommand("next");

        JSlider speed = new JSlider(1, maxStat, statStart);
        speed.setSnapToTicks(true);
        speed.addChangeListener(this);
        speed.setName("spd");
        JSlider acceleration = new JSlider(1, maxStat, statStart);
        acceleration.setSnapToTicks(true);
        acceleration.addChangeListener(this);
        acceleration.setName("acc");
        JSlider handling = new JSlider(1, maxStat, statStart);
        handling.setSnapToTicks(true);
        handling.addChangeListener(this);
        handling.setName("han");
        return new JComponent[]{startRace, hurry, pause, previousCar, nextCar, speed, acceleration, handling};
    }

    public static void main(String[] args) {
        new GroceryGrandPrix();
    }

}
