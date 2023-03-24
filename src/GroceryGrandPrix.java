import graphics.GUI;
import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener {

    private final int framerate = 30;
    private boolean paused;
    private boolean hurry;
    private int budget;
    private int round;
    private final int trackX = 1440;
    private final int trackY = 830;
    private final int maxStat = 9;
    private double timeElapsed;
    private GUI gui;
    private Node trackHead;
    private String[] carNames;
    private ArrayList <Car> cars;
    private CarStats playerStats;

    private JButton[] buttons;
    private Timer gameLoop;

    public GroceryGrandPrix() {
        budget = 10;
        round = 1;
        timeElapsed = 0.0;
        fileReader();
        createButtons();
        cars = new ArrayList<>();
        playerStats = new CarStats(4,4,4);
        initializeGUI();
        gui.switchToPlayerMenu();
        gui.setVisible(true);

    }

    private void fileReader() {
        File[] spriteFiles = (new File("assets/sprites")).listFiles();
        assert spriteFiles != null;
        carNames = new String[spriteFiles.length];
        for (int i = 0; i < spriteFiles.length; i++) {
            carNames[i] = spriteFiles[i].getName().split("_")[1];
            System.out.println("Name " + (i+1) + ": " + carNames[i] + "\n");
        }
    }

    private void initializeGUI(){
        if (gui != null){
            gui.dispose();
        }
        gui = new GUI("Grocery Grand Prix", Color.lightGray, buttons, trackX, trackY);
    }

    public void showMenu() {
        gui.switchToPlayerMenu();

        /* existing startGame code
           for (int i = 0; i < cars.size(); i++) {
            gui.createPreviewCard(i);
        }*/
    }

    private void showTrack() {
        timeElapsed = 0.0;
        generateNodes(round+5);
        generateCars();
        gui.toTrack(trackHead, cars);
    }

    private void startSimulation() {
        ArrayList<Car> carPlacements = new ArrayList<Car>();
        final int timerDelayMs = 1000 / framerate;

        gameLoop = new Timer(timerDelayMs, e -> {
            double inGameTimePassed = (timerDelayMs/100.0) * ((hurry) ? 2 : 1);
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
                cars.remove(carPlacements.get(carPlacements.size() - 1));

                if (continueGame) {
                    round++;
                    budget = 2;
                    showMenu();
                } else restart();
            }
        });
        gameLoop.start();
    }

    private void restart() {
        budget = 5;
        round = 1;
        showMenu();
    }

    private void generateCars() {
        Node carStartNode = trackHead;
        int imageIndex = 0;
        int statPoints = 15;
        int stat1;
        int stat2;
        int stat3;
        double statPicker;

        for (int i = 0; i < 4; i++) {
            imageIndex=i;
            if (i > 0) {
                stat1 = 0;
                stat2 = 0;
                stat3 = 0;
                // Distribute all stat points randomly one at a time.
                for (int s = 0; s < statPoints; s++) {
                    statPicker = Math.random();
                    // Add one to any stat under the maximum. Odds adjust if any stat reaches the maximum.
                    if (((statPicker > (2.0/3.0) || ((stat2 == maxStat || stat3 == maxStat) && statPicker >= .5))
                            || (stat2 == maxStat && stat3 == maxStat)) && stat1 != maxStat) {
                        stat1++;
                    } else if ((((statPicker > (1.0/3.0) && stat1 != maxStat) || (statPicker >= .5)) || stat3 == maxStat) && stat2 != maxStat) {
                        stat2++;
                    } else {
                        stat3++;
                    }
                }
                cars.add(i, new Car(carNames[imageIndex], new CarStats(stat1, stat2, stat3), carStartNode, false));
            }else {
                cars.add(i, new Car(carNames[imageIndex], playerStats, carStartNode, true));
            }
            System.out.println("Car " + (i+1) + ": " + cars.get(i).getImageName() + "\n");
            carStartNode = carStartNode.next();
        }
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
            temp.setNext(new Node(x, y, head));
            temp = temp.next();
        }*/
        temp = trackHead;
        do{
            System.out.println(temp);
            temp = temp.next();
        }while (temp != trackHead);
    }

    public void actionPerformed(ActionEvent event) {
        JButton pressedButton = (JButton) event.getSource();
        switch (pressedButton.getActionCommand().substring(0,4)) {
            case "race" :
                showTrack();
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
            case "next" :
                break;
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
                }
                // gui.updatePlayer(budget, playerStats.TopSpeed(), playerStats.Acceleration(), playerStats.Handling());
        }

    }

    private void createButtons() {
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
        JButton nextCar = new JButton();
        nextCar.addActionListener(this);
        nextCar.setActionCommand("next");
        buttons = new JButton[] {plus1, plus2, plus3, minus1, minus2, minus3, helpSpeed, helpAcceleration, helpHandling, helpBudget, startRace, hurry, pause, restart, nextCar};
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GroceryGrandPrix::new);
    }

}
