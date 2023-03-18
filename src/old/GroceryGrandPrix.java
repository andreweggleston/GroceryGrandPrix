package old;// Phoenix Ganz-Ratzat

import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener {
    private boolean paused;
    private int budget;
    private int round;
    private final int trackX = 1920;
    private final int trackY = 830;
    private int tickRate;
    private double timeElapsed;
    private GUI gui;
    private Node head;
    private ArrayList <Car> cars ;
    private JButton[] buttons;
    private BufferedImage[] carImages;
    public GroceryGrandPrix() {
        paused = false;
        budget = 5;
        round = 1;
        tickRate = 33;
        timeElapsed = 0;
        cars = new ArrayList<Car>();
        //generateCars();
        createButtons();
        gui = new GUI(Color.WHITE, buttons, trackX, trackY);

    }

    public void startGame() {
        gui.playerMenu(round, budget);
        for (int i = 0; i < cars.size(); i++) {
            gui.createPreviewCard(i);
        }
    }

    private void openMenu() {

    }

    private void showTrack() {
        paused = false;
        tickRate = 33;
        timeElapsed = 0;

        generateNodes(round*5);
        gui.buildTrack(head, cars);
        gui.drawTrack();
    }

    private void simulateRace() {
        int finished = 0;
        Car winner = null;
        double lastTime = System.currentTimeMillis();
        double accumulator = 0;
        double newTime;
        double frameTime;

        // loop until all cars racing in the round have finished
        while (finished < 5 - round) {
            newTime = System.currentTimeMillis();
            frameTime = newTime - lastTime;
            lastTime = newTime;
            accumulator += frameTime;
            // simulate a number of ticks based on the amount of time that has passed since the last simulation
            while (accumulator >= tickRate && !paused) {
                for (Car car : cars) {
                    // drive a car and increment finished if it finishes the race
                    finished += (car.drive(tickRate)) ? 1 : 0;
                    // save the winning car if one has not been chosen, and they are first
                    if (finished == 1 && winner == null) winner = car;
                }
                timeElapsed += ((double) tickRate)/1000;
                accumulator -= tickRate;
            }
            gui.drawTrack(/*timeElapsed*/);
        }

        if (winner.isPlayer()) gui.showWin();
        else gui.showLose();
    }

    private void restart() {
        budget = 5;
        round = 1;
        //generateCars();
        //gui.restart();
        startGame();
    }

    private void fileReader() throws IOException {
        File[] spriteFiles = (new File("assets/sprites")).listFiles();
        carImages = new BufferedImage[spriteFiles.length];

        for (int i = 0; i < spriteFiles.length; i++) {
            carImages[i] = ImageIO.read(spriteFiles[i]);
        }
    }


    private void generateCars() {
        Node temp = head;
        int imageIndex = 0;
        int maxStat = 10;
        int statPoints = 10;
        int stat1 = 5;
        int stat2 = 5;
        int stat3 = 5;
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
            }
            cars.set(i, new Car(carImages[imageIndex], new CarStats(stat1, stat2, stat3), temp, i==0));
            temp = temp.next();
        }
    }

    private void generateNodes(int number) {
        Random rand = new Random();
        double x = rand.nextDouble()*400 + 50;
        double y = rand.nextDouble()*200 + 250;
        head = new Node(x, y);
        int quad = 1; // 1 = top left, 2 = top right, 3 = bottom right, 4 = bottom left
        Node temp = head;
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
            temp.setNext(new Node(x, y, head));
            temp = temp.next();
        }
        /*for (int i = 0; i < number-1; i++) {
            x = rand.nextDouble()*800 + 50;
            y = rand.nextDouble()*400 + 50;
            temp.setNext(new Node(x, y, head));
            temp = temp.next();
        }*/
        temp = head;
        do{
            System.out.println(temp);
            temp = temp.next();
        }while (temp != head);
    }

    public void actionPerformed(ActionEvent event) {
        JButton action = (JButton) event.getSource();
        switch (action.getActionCommand().substring(0,4)) {
            case "race" :
                showTrack();
                simulateRace();
                break;
            case "fast" :
                tickRate = (tickRate == 16) ? 33 : 16;
                break;
            case "stop" :
                paused = !paused;
                break;
            case "redo" :
                restart();
                break;
            case "next" :
                for (int i = 0; i < carImages.length; i++) {
                    if (cars.get(0).getImage().equals(carImages[i])) {
                        //cars.get(0).setImage(carImages[i+1])
                        //gui.updatePreview(carImages[i+1])
                        break;
                    }
                }
                break;
            case "adj " :
                Car player = cars.get(0);
                switch (action.getActionCommand().substring(4, 8)) {
                    case "+spd":
                        player.incrementTopSpeed();
                    case "+acc":
                        player.incrementAcceleration();
                    case "+han":
                        player.incrementHandling();
                    case "-spd":
                        player.decrementTopSpeed();
                    case "-acc":
                        player.decrementAcceleration();
                    case "-han":
                        player.decrementHandling();
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
        JButton startRace = new JButton();
        startRace.addActionListener(this);
        startRace.setActionCommand("race");
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
        buttons = new JButton[] {plus1, plus2, plus3, minus1, minus2, minus3, startRace, hurry, pause, restart, nextCar};
    }

    public static void main(String[] args) {
        GroceryGrandPrix test = new GroceryGrandPrix();
        test.showTrack();
    }

}
