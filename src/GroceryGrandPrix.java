import graphics.GUI;
import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener {

    private final int framerate = 30;
    private boolean paused;
    private boolean fast;
    private int budget;
    private int round;
    private final int trackX = 1440;
    private final int trackY = 830;
    private double timeElapsed = 0.0;
    private GUI gui;
    private Node trackHead;
    private String[] carNames;
    private ArrayList <Car> cars;

    private CarStats playerStats;
    private JButton[] buttons;

    public GroceryGrandPrix() {
        budget = 5;
        round = 1;
        timeElapsed = 0.0;
        File[] spriteFiles = (new File("assets/sprites")).listFiles();
        assert spriteFiles != null;
        carNames = new String[spriteFiles.length];
        for (int i = 0; i < spriteFiles.length; i++) {
            carNames[i] = spriteFiles[i].getName().split("_")[1];
        }
        createButtons();
        cars = new ArrayList<>();
        playerStats = new CarStats(0,0,0);
        initializeGUI();
        gui.playerMenu(1, 20);
        gui.setVisible(true);
    }

    private void initializeGUI(){
        if (gui != null){
            gui.dispose();
        }
        gui = new GUI("Grocery Grand Prix", Color.lightGray, buttons, trackX, trackY);
    }

    public void startGame() {
        gui.playerMenu(round, budget);
        for (int i = 0; i < cars.size(); i++) {
            gui.createPreviewCard(i);
        }
    }

    private void showTrack() {
        timeElapsed = 0;
        generateNodes(round*5);
        generateCars();
        gui.toTrack(trackHead, cars);
    }

    private void startSimulation() throws InterruptedException {
        double timeElapsed = 0.0;
        gui.revalidate();
        gui.repaint();

        final boolean[] finalDone = {false};
        final double[] finalTimeElapsed = {timeElapsed};
        final int timerDelayMs = 1000/framerate;
        new Timer(timerDelayMs, e -> {
            if (!finalDone[0]) {
            for (Car car : cars) { //TODO: hurry mode
                finalDone[0] = car.drive(timerDelayMs/100.0) || finalDone[0];
            }
            finalTimeElapsed[0] += 1;
            gui.revalidate(); //VERY IMPORTANT LINE
            gui.repaint();
            } else {
                Timer self = (Timer) e.getSource();
                self.stop();
                JOptionPane.showMessageDialog(gui, String.format("Time elapsed: %f", finalTimeElapsed[0]));
            }
        }).start();
    }

    private void restart() {
        budget = 5;
        round = 1;
        startGame();
    }

    private void generateCars() {
        Node carStartNode = trackHead;
        int imageIndex = 0;
        int maxStat = 10;
        int statPoints = 10;
        int stat1;
        int stat2;
        int stat3;
        double statPicker;

        for (int i = 0; i < 4; i++) {
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
                cars.add(0, new Car(carNames[imageIndex], playerStats, carStartNode, true));
            }
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
        JButton action = (JButton) event.getSource();
        switch (action.getActionCommand().substring(0,4)) {
            case "race" :
                showTrack();
                try {
                    startSimulation();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "fast" :
                fast = !fast;
                //TODO set fast button to indicate user is in fast mode
                break;
            case "stop" :
                paused = !paused;
                break;
            case "redo" :
                restart();
                break;
            case "next" :
                break;
            case "adj " :
                switch (action.getActionCommand().substring(4, 8)) {
                    case "+spd":
                        playerStats.incrementTopSpeed();
                        break;
                    case "+acc":
                        playerStats.incrementAcceleration();
                        break;
                    case "+han":
                        playerStats.incrementHandling();
                        break;
                    case "-spd":
                        playerStats.decrementTopSpeed();
                        break;
                    case "-acc":
                        playerStats.decrementAcceleration();
                        break;
                    case "-han":
                        playerStats.decrementHandling();
                }
                // gui.updatePreview(player.getTopSpeed(), player.getAcceleration(), player.getHandling());
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
