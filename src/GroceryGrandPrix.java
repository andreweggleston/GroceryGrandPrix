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


public class GroceryGrandPrix implements ActionListener, ChangeListener {
    private final int framerate = 30;
    private boolean hurry;
    private int roundBudget;
    private int playerBudget;
    private int playerNameIndex;
    private int round;
    private final int trackX = 1280;
    private final int trackY = 900;
    private final int maxStat = 10;
    private final int statStart = 3;
    private int timeElapsedMs;
    private GUI gui;
    private Node trackHead;
    private String[] carNames;
    private ArrayList <Car> cars;
    private CarStats playerStats;
    private JComponent[] userInputs;
    private Timer gameLoop;


    public GroceryGrandPrix() {
        playerNameIndex = 0;
        roundBudget = 6;
        playerBudget = roundBudget;
        round = 1;
        createButtons();
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

    private HashMap<String, BufferedImage> fileReader() throws IOException {
        File[] spriteFiles = (new File("assets/previews")).listFiles();
        assert spriteFiles != null;

        carNames = new String[spriteFiles.length];
        HashMap<String, BufferedImage> previewMap = new HashMap<String, BufferedImage>();
        for (int i = 0; i < spriteFiles.length; i++) {
            carNames[i] = spriteFiles[i].getName().split("_")[1];
            previewMap.put(carNames[i], ImageIO.read(spriteFiles[i]));
            //System.out.println("Name " + (i+1) + ": " + carNames[i] + "\n");
        }
        return previewMap;
    }


    private void initializeGUI() throws IOException {
        if (gui != null){
            gui.dispose();
        }
        gui = new GUI("Grocery Grand Prix", userInputs, fileReader(), trackX, trackY);
    }

    public void showMenu() {
        gui.updateStatLabels(playerStats.getTopSpeedNumeral(), playerStats.getAccelerationNumeral(),
                playerStats.getHandlingNumeral(), playerBudget, true);
        gui.switchToPlayerMenu();

        /* existing startGame code
           for (int i = 0; i < cars.size(); i++) {
            gui.createPreviewCard(i);
        }*/
    }

    private void startNextRace() {
        timeElapsedMs = 0;
        generateNodes(round+6);

        if (round == 1) {
            generateCars();
        }
        else {
            Node carStartNode = trackHead;
            for (Car car : cars) {
                if (!car.isPlayer()) {
                    car.setAllStats(distributeCarStats(car.getTopSpeed(), car.getAcceleration(), car.getHandling(), roundBudget));
                }
                car.setGoalNode(carStartNode);
                carStartNode = carStartNode.next();
            }
        }

        gui.toTrack(trackHead, cars);
    }

    private void startSimulation() {
        ArrayList<Car> carPlacements = new ArrayList<Car>();
        final int timerDelayMs = 1000 / framerate;

        gameLoop = new Timer(timerDelayMs, e -> {
            int inGameTimePassed = (timerDelayMs) * ((hurry) ? 2 : 1);
            if (carPlacements.size() < 5 - round) {
                for (Car car : cars) {
                    if (!carPlacements.contains(car)) {
                        // save the cars in finishing order
                        if (car.drive(inGameTimePassed)) carPlacements.add(car);
                    }
                }
                timeElapsedMs += inGameTimePassed;
                gui.revalidate(); //VERY IMPORTANT LINE
                gui.repaint();
            } else {
                gameLoop.stop();
                boolean continueGame = gui.showResults(carPlacements,  (timeElapsedMs /1000.0));
                Car lastCar = carPlacements.get(carPlacements.size() - 1);

                if (continueGame) {
                    if (lastCar.isPlayer()) {
                        restart();
                    } else {
                        cars.remove(lastCar);
                        round++;
                        roundBudget = 2;
                        playerBudget = roundBudget;
                        showMenu();
                    }
                } else gui.dispose();
            }
        });
        gameLoop.start();
    }

    private void restart() {
        roundBudget = 6;
        playerBudget = roundBudget;
        playerNameIndex = 0;
        round = 1;
        playerStats = new CarStats(statStart, statStart, statStart);
        showMenu();
    }

    private void generateCars() {
        cars.clear();
        Node carStartNode = trackHead;
        int imageNameIndex;
        int statPoints = roundBudget + (statStart * 3);
        int[] stats;
        List<String> availableCarNames = new ArrayList<String>(Arrays.asList(carNames));

        //System.out.println(statPoints);
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                imageNameIndex = (int) (Math.random() * availableCarNames.size());
                //System.out.println(imageNameIndex);
                stats = distributeCarStats(1, 1, 1, statPoints);
                cars.add(i, new Car(availableCarNames.get(imageNameIndex), new CarStats(stats), carStartNode, false));
                availableCarNames.remove(imageNameIndex);
            } else {
                cars.add(i, new Car(availableCarNames.get(playerNameIndex), playerStats, carStartNode, true));
                availableCarNames.remove(playerNameIndex);
            }
            //System.out.println("Car " + (i+1) + ": " + cars.get(i).getTopSpeed() + " " + cars.get(i).getAcceleration() + " " + cars.get(i).getHandling() + "\n");
            carStartNode = carStartNode.next();
        }
    }

    private int[] distributeCarStats(int stat1, int stat2, int stat3, int statPoints) {
        double statPicker;
        int underMaxStatsCount = ((stat1 != maxStat) ?  1 : 0) + ((stat2 != maxStat) ?  1 : 0) + ((stat3 != maxStat) ?  1 : 0);
        // Distribute all stat points randomly one at a time.
        for (int s = 0; s < statPoints; s++) {
            //System.out.println(underMaxStatsCount);
            //System.out.println(s);
            statPicker = Math.random();
            // Add one to any stat under the maximum. Odds adjust if any stat reaches the maximum.
            if (statPicker < (1.0/underMaxStatsCount) && stat1 != maxStat) {
                stat1++;
                underMaxStatsCount -= (stat1 == maxStat) ? 1 : 0;
            } else if (((statPicker < (2.0/underMaxStatsCount) && stat1 != maxStat) || (statPicker < 1.0/underMaxStatsCount)) && stat2 != maxStat) {
                stat2++;
                underMaxStatsCount -= (stat2 == maxStat) ? 1 : 0;
            } else if (stat3 != maxStat){
                stat3++;
                underMaxStatsCount -= (stat3 == maxStat) ? 1 : 0;
            }
        }

        return new int[]{stat1, stat2, stat3};
    }

    private void generateNodes(int number) {

        //TODO make starting positions first

        Random rand = new Random();
        double x = rand.nextDouble()*400 + 50;
        double y = rand.nextDouble()*200 + 50;
        trackHead = new Node(x, y, trackX, trackY);
        trackHead.setNext(new Node(x+125, y, trackX, trackY));
        trackHead.next().setNext(new Node(x+250, y, trackX, trackY));
        trackHead.next().next().setNext(new Node(x+375, y, trackX, trackY));
        Node temp = trackHead.next().next().next();
        for (int i = 0; i < number-1; i++) {
            do {
                switch (temp.getQuad()) {
                    case 1:
                        x = rand.nextDouble() * (trackX - temp.getCoord().getX() - 50) + temp.getCoord().getX(); //Goes right
                        y = rand.nextDouble() * (trackY / 2 - 50) + 50; //Stays top
                        break;
                    case 2:
                        x = rand.nextDouble() * (trackX / 2 - 50) + trackX / 2 + 50; //Stays right
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
                //Checks new quadrant below:
                temp.setNext(new Node(x, y, trackHead, trackX, trackY));
            }while (temp.distanceToNext() < 100 || Math.abs(temp.getAngle()) > 2.5);
            temp = temp.next();
        }
        /*for (int i = 0; i < number-1; i++) {
            x = rand.nextDouble()*800 + 50;
            y = rand.nextDouble()*400 + 50;
            temp.setNext(new shared.Node(x, y, head));
            temp = temp.next();
        }*/
        temp = trackHead;
        do{
            System.out.println(temp);
            temp = temp.next();
        } while (temp != trackHead );
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton pressedButton = (JButton) event.getSource();
        switch (pressedButton.getActionCommand().substring(0,4)) {
            case "race" :
                startNextRace();
                startSimulation();
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
                    if (playerNameIndex > 0) {
                        playerNameIndex--;
                    } else {
                        playerNameIndex = carNames.length - 1;
                    }
                    gui.changePreviewDisplay(carNames[playerNameIndex]);
                }
                break;
            case "next" :
                if(round == 1) {
                    if (playerNameIndex < carNames.length - 1) {
                        playerNameIndex++;
                    } else {
                        playerNameIndex = 0;
                    }
                    gui.changePreviewDisplay(carNames[playerNameIndex]);
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
                }
                else {
                    playerStats.setTopSpeedStat(playerTopSpeed + playerBudget);
                    playerBudget = 0;
                }
                //System.out.println(playerStats.topSpeed.getStatNumeral() + "spd, slider" + slider.getValue());
                break;
            case "acc":
                if (playerBudget >= (sliderValue - playerAcceleration)) {
                    playerStats.setAccelerationStat(sliderValue);
                    playerBudget -= sliderValue - playerAcceleration;
                }
                else {
                    playerStats.setAccelerationStat(playerAcceleration + playerBudget);
                    playerBudget = 0;
                }
                //System.out.println(playerStats.acceleration.getStatNumeral() + "acc, slider" + slider.getValue());
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
                //System.out.println(playerStats.handling.getStatNumeral() + "han, slider" + slider.getValue());
        }
        //System.out.println(sliderValue);
        gui.updateStatLabels(playerStats.getTopSpeedNumeral(), playerStats.getAccelerationNumeral(),
                playerStats.getHandlingNumeral(), playerBudget, !slider.getValueIsAdjusting());
    }

    private void createButtons() {
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
        speed.setPaintTicks(true);
        speed.setMajorTickSpacing(1);
        speed.addChangeListener(this);
        speed.setName("spd");
        JSlider acceleration = new JSlider(1, maxStat, statStart);
        acceleration.setSnapToTicks(true);
        acceleration.setPaintTicks(true);
        acceleration.setMajorTickSpacing(1);
        acceleration.addChangeListener(this);
        acceleration.setName("acc");
        JSlider handling = new JSlider(1, maxStat, statStart);
        handling.setSnapToTicks(true);
        handling.setMajorTickSpacing(1);
        handling.setPaintTicks(true);
        handling.addChangeListener(this);
        handling.setName("han");
        userInputs = new JComponent[]{startRace, hurry, pause, previousCar, nextCar, speed, acceleration, handling};
    }

    public static void main(String[] args) {
        new GroceryGrandPrix();
    }

}
