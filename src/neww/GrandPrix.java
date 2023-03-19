package neww;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import neww.graphics.GUI;
import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GrandPrix implements ActionListener {

    private final BufferedImage[] carImages;

    private String[] carNames;
    private ArrayList<Car> cars;
    private CarStats playerStats;
    private Car playerCar;
    private GUI gui;
    private JButton[] buttons;

    private Node trackHead;

    private int tickRateNs;

    private JFrame window;

    private int trackX;
    private int trackY;

    public GrandPrix() {
        File[] spriteFiles = (new File("assets/sprites")).listFiles();
        assert spriteFiles != null;
        carImages = new BufferedImage[spriteFiles.length];
        carNames = new String[spriteFiles.length];
        for (int i = 0; i < spriteFiles.length; i++) {
            carNames[i] = spriteFiles[i].getName().split("_")[1];
            try {
                carImages[i] = ImageIO.read(spriteFiles[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        tickRateNs = 1000;
        trackX = 1000;
        trackY = 800;
        setupButtons();
        initialize();
        setupWindow();
    }

    private void setupWindow() {
        window = new JFrame("Grocery Grand Prix");
        window.setPreferredSize(new Dimension(1000, 800));
        window.setResizable(false);
        window.add(gui);
        window.pack();
        window.setVisible(true);
    }

    private void initialize() {
        cars = new ArrayList<>();
        if (gui != null) {
            gui.removeAll();
        }
        gui = new GUI(buttons);
//        setupTempTrack();
        generateNodes(5);
        setupTempCars();
    }

    private void startSimulation() throws InterruptedException {
        gui.toTrack(trackHead, cars);
        double timeElapsed = 0.0;
        gui.revalidate();
        gui.repaint();

        final boolean[] finalDone = {false};
        final double[] finalTimeElapsed = {timeElapsed};
        new Timer(100, e -> {
//            if (!finalDone[0]) {
                for (Car car : cars) {
                    finalDone[0] = car.drive(1) || finalDone[0];
                }
                finalTimeElapsed[0] += 1;
                gui.revalidate(); //VERY IMPORTANT LINE
                gui.repaint();
//            } else {
//                Timer self = (Timer) e.getSource();
//                self.stop();
//                JOptionPane.showMessageDialog(gui, String.format("Time elapsed: %f", finalTimeElapsed[0]));
//            }
        }).start();
    }

    private void setupTempTrack() {
        Node d = new Node(400, 100);
        Node c = new Node(400, 400, d);
        Node b = new Node(300, 600, c);
        Node a = new Node(200, 400, b);
        trackHead = new Node(100, 100, a);
        d.setNext(trackHead);
    }

    private void generateNodes(int number) {
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
            temp = temp.next();
        }while (temp != trackHead);
    }

    private void setupTempCars() {
        playerStats = new CarStats(2, 4, 6);
        Car player = new Car(carNames[0], playerStats, trackHead, true);
        playerCar = player;
        cars.add(player);
        cars.add(new Car(carNames[0], new CarStats(3,2,7), trackHead.next(), false));
        cars.add(new Car(carNames[0], new CarStats(2,3,4), trackHead.next().next(), false));
        cars.add(new Car(carNames[0], new CarStats(4,2,9), trackHead.next().next().next(), false));
    }

    private void setupButtons() {
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
        JButton startRace = new JButton("start race");
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
        JButton step = new JButton("step");
        step.addActionListener(this);
        step.setActionCommand("step");
        buttons = new JButton[]{plus1, plus2, plus3, minus1, minus2, minus3, startRace, hurry, pause, restart, nextCar, step};
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton action = (JButton) e.getSource();
        switch (action.getActionCommand().substring(0, 4)) {
            case "step":
                for (Car car : cars) {
                    car.drive(1);
                }
                gui.revalidate(); //VERY IMPORTANT LINE
                gui.repaint();
                break;
            case "race":
                try {
                    startSimulation();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case "adj ":
                String command = action.getActionCommand().substring(4);
                switch (command) {
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
                        break;
                }
                gui.repaint();
        }
    }
}
