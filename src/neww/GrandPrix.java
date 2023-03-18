package neww;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import neww.graphics.GUI;
import shared.Car;
import shared.CarStats;
import shared.Node;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GrandPrix implements ActionListener {

    private final BufferedImage[] carImages;
    private ArrayList<Car> cars;
    private CarStats playerStats;
    private Car playerCar;
    private GUI gui;
    private JButton[] buttons;

    private Node trackHead;

    private int tickRateNs;

    private JFrame window;

    public GrandPrix() {
        File[] spriteFiles = (new File("assets/sprites")).listFiles();
        assert spriteFiles != null;
        carImages = new BufferedImage[spriteFiles.length];

        for (int i = 0; i < spriteFiles.length; i++) {
            System.out.println(spriteFiles[i].toURI());
            try {
                carImages[i] = ImageIO.read(spriteFiles[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        tickRateNs = 1000;
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
        setupTempTrack();
        setupTempCars();
    }

    private void startSimulation() throws InterruptedException {
        gui.toTrack(trackHead, cars);
        window.repaint();
        gui.repaint();
//        gui.repaintTrack();
        double timeElapsed = 0.0;

        final boolean[] finalDone = {false};
        final double[] finalTimeElapsed = {timeElapsed};
        new Timer(100, e -> {
            if (!finalDone[0]) {
                for (Car car : cars) {
                    finalDone[0] = finalDone[0] || car.drive(1);
                }
                finalTimeElapsed[0] += 1;
                gui.revalidate();
                gui.repaint();
            } else {
                Timer self = (Timer) e.getSource();
                self.stop();
                JOptionPane.showMessageDialog(gui, String.format("Time elapsed: %f", finalTimeElapsed[0]));
            }
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

    private void setupTempCars() {
        playerStats = new CarStats(7, 6, 6);
        System.out.println(playerStats);
        Car player = new Car(carImages[0], playerStats, trackHead, true);
        playerCar = player;
        Car ai = new Car(carImages[0], new CarStats(3,3,3), trackHead.next(), false);
        cars.add(player);
        cars.add(ai);
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
        buttons = new JButton[]{plus1, plus2, plus3, minus1, minus2, minus3, startRace, hurry, pause, restart, nextCar};
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton action = (JButton) e.getSource();
        switch (action.getActionCommand().substring(0, 4)) {
            case "race":
                try {
                    startSimulation();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            case "adj ":
                String command = action.getActionCommand().substring(4);
                System.out.println(command);
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
