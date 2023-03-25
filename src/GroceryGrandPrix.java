import graphics.GUI;
import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener, ChangeListener {
    private final int framerate = 30;
    private boolean hurry;
    private int roundBudget;
    private int playerBudget;
    private int round;
    private final int trackX = 1600;
    private final int trackY = 900;
    private final int maxStat = 10;
    private final int statStart = 3;
    private double timeElapsed;
    private GUI gui;
    private Node trackHead;
    private String[] carNames;
    private ArrayList <Car> cars;
    private CarStats playerStats;
    private JComponent[] userInputs;
    private Timer gameLoop;

    public GroceryGrandPrix() {
        roundBudget = 6;
        playerBudget = roundBudget;
        round = 1;
        fileReader();
        createButtons();
        cars = new ArrayList<>();
        playerStats = new CarStats(statStart, statStart, statStart);
        initializeGUI();
        gui.playerMenu(playerBudget);
        gui.setVisible(true);
    }

    private void fileReader() {
        File[] spriteFiles = (new File("assets/sprites")).listFiles();
        assert spriteFiles != null;
        carNames = new String[spriteFiles.length];
        for (int i = 0; i < spriteFiles.length; i++) {
            carNames[i] = spriteFiles[i].getName().split("_")[1];
            //System.out.println("Name " + (i+1) + ": " + carNames[i] + "\n");
        }
    }

    private void initializeGUI(){
        if (gui != null){
            gui.dispose();
        }
        gui = new GUI("Grocery Grand Prix", new Color(222, 232, 243), new Color(115, 122, 148), userInputs, trackX, trackY);
    }

    public void showMenu() {
        gui.switchToPlayerMenu();

        /* existing startGame code
           for (int i = 0; i < cars.size(); i++) {
            gui.createPreviewCard(i);
        }*/
    }

    private void startNextRace() {
        timeElapsed = 0.0;
        generateNodes(round+3);

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
            double inGameTimePassed = (timerDelayMs/100.0) * ((hurry) ? 2.0 : 1.0);
            if (carPlacements.size() < 5 - round) {
                for (Car car : cars) {
                    if (!carPlacements.contains(car)) {
                        // save the cars in finishing order
                        if (car.drive(inGameTimePassed)) carPlacements.add(car);
                    }
                }
                timeElapsed += inGameTimePassed;
                gui.revalidate(); //VERY IMPORTANT LINE
                gui.repaint();
            } else {
                gameLoop.stop();
                boolean continueGame = gui.showResults(carPlacements, (int) timeElapsed);
                Car lastCar = carPlacements.get(carPlacements.size() - 1);

                if (continueGame) {
                    if (lastCar.isPlayer()) {
                        restart();
                    } else {
                        cars.remove(lastCar);
                        round++;
                        roundBudget = 2;
                        playerBudget = roundBudget;
                        gui.updateStatLabels(playerBudget);
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
        round = 1;
        playerStats = new CarStats(statStart, statStart, statStart);
        gui.updateStatLabels(playerStats.topSpeed.getStatNumeral(), playerStats.acceleration.getStatNumeral(),
                playerStats.handling.getStatNumeral(), playerBudget);
        showMenu();
    }

    private void generateCars() {
        cars.clear();
        Node carStartNode = trackHead;
        int imageNameIndex;
        int statPoints = roundBudget + (statStart * 3);
        int[] stats;
        //System.out.println(statPoints);
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                imageNameIndex = (int) (Math.random() * carNames.length);
                //System.out.println(imageNameIndex);
                stats = distributeCarStats(0, 0, 0, statPoints);
                cars.add(i, new Car(carNames[imageNameIndex], new CarStats(stats), carStartNode, false));
            } else {
                cars.add(i, new Car(carNames[0], playerStats, carStartNode, true));
            }
            //System.out.println("shared.Car " + (i+1) + ": " + cars.get(i).getImageName() + "\n");
            carStartNode = carStartNode.next();
        }
    }

    private int[] distributeCarStats(int stat1, int stat2, int stat3, int statPoints) {
        double statPicker;

        // Distribute all stat points randomly one at a time.
        for (int s = 0; s < statPoints; s++) {
            //System.out.println(s);
            statPicker = Math.random();
            // Add one to any stat under the maximum. Odds adjust if any stat reaches the maximum.
            if (((statPicker > (2.0/3.0) || ((stat2 == maxStat || stat3 == maxStat) && statPicker >= .5))
                    || (stat2 == maxStat && stat3 == maxStat)) && stat1 != maxStat) {
                stat1++;
            } else if ((((statPicker > (1.0/3.0) && stat1 != maxStat) || (statPicker >= .5)) || stat3 == maxStat) && stat2 != maxStat) {
                stat2++;
            } else if (stat3 != maxStat){
                stat3++;
            }
        }

        return new int[]{stat1, stat2, stat3};
    }

    private void generateNodes(int number) {

        //TODO make starting positions first

        Random rand = new Random();
        double x = rand.nextDouble()*400 + 50;
        double y = rand.nextDouble()*200 + 250;
        trackHead = new Node(x, y);
        int quad = 1; // 1 = top left, 2 = top right, 3 = bottom right, 4 = bottom left
        Node temp = trackHead;
        for (int i = 0; i < number-1; i++) {
            switch (quad) {
                case 1:
                    x = rand.nextDouble() * (trackX - temp.getCoord().getX()-50) + (temp.getCoord().getX()); //Goes right
                    y = rand.nextDouble() * (trackY - 50) + 50;
                    break;
                case 2:
                    x = rand.nextDouble() * (trackX - 50) + 50;
                    y = rand.nextDouble() * (temp.getCoord().getY() - 50) + 50; //Goes down
                    break;
                case 3:
                    x = rand.nextDouble() * (temp.getCoord().getX() - 50) + 50; //Goes left
                    y = rand.nextDouble() * (trackY - 50) + 50;
                    break;
                case 4:
                    x = rand.nextDouble() * (trackX - 50) + 50;
                    y = rand.nextDouble() * (trackY - temp.getCoord().getY()-50) + (temp.getCoord().getY()); //Goes down
            }
            //Checks new quadrant below:
            if (temp.getCoord().getX() >= trackX / 2) {
                if (temp.getCoord().getY() >= trackY / 2) {
                    quad = 2;
                }else {
                    quad = 3;
                }
            } else{
                if(temp.getCoord().getY() >= trackY / 2){
                    quad = 1;
                }else {
                    quad = 4;
                }
            }
            temp.setNext(new Node(x, y, trackHead));
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
            //System.out.println(temp);
            temp = temp.next();
        }while (temp != trackHead);
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
            case "redo" :
                restart();
                break;
            case "last" :
                if(round==1){
                    gui.setPreviewIndex(gui.getPreviewIndex()-1);
                }
                break;
            case "next" :
                if(round==1){
                    gui.setPreviewIndex(gui.getPreviewIndex()+1);
                }
                break;
            /*
            case "adj " :
                switch (pressedButton.getActionCommand().substring(4, 8)) {
                    case "+spd":
                        if (budget > 0 && playerStats.topSpeed() <= maxStat) {
                            playerStats.incrementTopSpeed();
                            budget--;
                        }
                        break;
                    case "+acc":
                        if (budget > 0 && playerStats.acceleration() <= maxStat) {
                            playerStats.incrementAcceleration();
                            budget--;
                        }
                        break;
                    case "+han":
                        if (budget > 0 && playerStats.handling() <= maxStat) {
                            playerStats.incrementHandling();
                            budget--;
                        }
                        break;
                    case "-spd":
                        if (playerStats.topSpeed() > 0) {
                            playerStats.decrementTopSpeed();
                            budget++;
                        }
                        break;
                    case "-acc":
                        if (playerStats.acceleration() > 0) {
                            playerStats.decrementAcceleration();
                            budget++;
                        }
                        break;
                    case "-han":
                        if (playerStats.handling() > 0) {
                            playerStats.decrementHandling();
                            budget++;
                        }
                        */
                }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();

        switch (slider.getName()) {
            case "spd":
                if (playerStats.topSpeed.getStatNumeral() < slider.getValue()) {
                    if (playerBudget > 0) {
                        playerStats.incrementTopSpeed();
                        playerBudget--;
                    } else {
                        slider.setValue(playerStats.topSpeed.getStatNumeral());
                    }
                } else if (playerStats.topSpeed.getStatNumeral() != slider.getValue()) {
                    playerStats.decrementTopSpeed();
                    playerBudget++;
                }
                //System.out.println(playerStats.topSpeed.getStatNumeral() + "spd, slider" + slider.getValue());
                break;
            case "acc":
                if (playerStats.acceleration.getStatNumeral() < slider.getValue()) {
                    if (playerBudget > 0) {
                        playerStats.incrementAcceleration();
                        playerBudget--;
                    } else {
                        slider.setValue(playerStats.acceleration.getStatNumeral());
                    }
                } else if (playerStats.acceleration.getStatNumeral() != slider.getValue()){
                    playerStats.decrementAcceleration();
                    playerBudget++;
                }
                //System.out.println(playerStats.acceleration.getStatNumeral() + "acc, slider" + slider.getValue());
                break;
            case "han":
                if (playerStats.handling.getStatNumeral() < slider.getValue()) {
                    if (playerBudget > 0) {
                        playerStats.incrementHandling();
                        playerBudget--;
                    } else {
                        slider.setValue(playerStats.handling.getStatNumeral());
                    }
                } else if (playerStats.handling.getStatNumeral() != slider.getValue()) {
                    playerStats.decrementHandling();
                    playerBudget++;
                }
                //System.out.println(playerStats.handling.getStatNumeral() + "han, slider" + slider.getValue());
        }
        gui.updateStatLabels(playerStats.topSpeed.getStatNumeral(), playerStats.acceleration.getStatNumeral(),
                playerStats.handling.getStatNumeral(), playerBudget);
    }

    private void createButtons() {
        /*
        JButton plus1 = new JButton();
        plus1.addActionListener(this);
        plus1.setActionCommand("adj +spd");
        JButton plus2 = new JButton();
        plus2.addActionListener(this);
        plus2.setActionCommand("adj +acc");
        JButton plus3 = new JButton();
        plus3.addActionListener(this);
        plus3.setActionCommand("adj +han");
        JButton minus1 = new JButton();
        minus1.addActionListener(this);
        minus1.setActionCommand("adj -spd");
        JButton minus2 = new JButton();
        minus2.addActionListener(this);
        minus2.setActionCommand("adj -acc");
        JButton minus3 = new JButton();
        minus3.addActionListener(this);
        minus3.setActionCommand("adj -han");
        */
        JButton helpSpeed = new JButton();
        helpSpeed.addActionListener(this);
        helpSpeed.setActionCommand("spd?");
        JButton helpAcceleration = new JButton();
        helpAcceleration.addActionListener(this);
        helpAcceleration.setActionCommand("acc?");
        JButton helpHandling = new JButton();
        helpHandling.addActionListener(this);
        helpHandling.setActionCommand("han?");
        JButton helpBudget = new JButton();
        helpBudget.addActionListener(this);
        helpBudget.setActionCommand("bud?");
        JButton startRace = new JButton();
        startRace.setActionCommand("race");
        startRace.addActionListener(this);
        JButton hurry = new JButton();
        hurry.addActionListener(this);
        hurry.setActionCommand("fast");
        JButton pause = new JButton();
        pause.addActionListener(this);
        pause.setActionCommand("stop");
        JButton restart = new JButton();
        restart.addActionListener(this);
        restart.setActionCommand("redo");
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
        userInputs = new JComponent[]{helpSpeed, helpAcceleration, helpHandling, helpBudget, startRace, hurry, pause, restart, previousCar, nextCar, speed, acceleration, handling};
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GroceryGrandPrix::new);
    }

}
