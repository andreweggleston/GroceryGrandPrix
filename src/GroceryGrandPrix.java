// Phoenix Ganz-Ratzat
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener {
    private boolean hurried;
    private boolean paused;
    private int budget;
    private int round;
    private int tickRate;
    private double timeElapsed;
    private ArrayList <Car> cars = new ArrayList<Car>();
    private JButton[] buttons;
    private GUI gui;
    private Node head;
    public GroceryGrandPrix() {
        generateNodes(5);
        hurried = false;
        paused = false;
        budget = 5;
        round = 1;
        tickRate = 33;
        timeElapsed = 0;
        generateCars();
        createButtons();
        gui = new GUI(Color.WHITE, buttons);
        showTrack();
    }

    public void startGame() {
        gui.playerMenu(round, budget);
    }



    private void showTrack() {
        generateNodes(round*4);
        gui.buildSegments(head);
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
                    if (car == null) {
                        // drive a car and increment finished if it finishes the race
                        finished += (car.drive(tickRate)) ? 1 : 0;
                        // save the winning car if one has not been chosen, and they are first
                        if (finished == 1 && winner == null) {
                            winner = car;
                        }
                    }
                }
                accumulator -= tickRate;
            }
            gui.drawTrack();
        }

        if (winner.isPlayer()) {
            gui.showWin();
        }
        else {
            gui.showLose();
        }
    }

    private void restart() {
        // reset the game to round 1
    }

    private void generateCars() {
        // create array of cars
    }

    private void generateNodes(int number) {
        Random rand = new Random();
        double x = rand.nextDouble()*800 + 50;
        double y = rand.nextDouble()*400 + 50;
        head = new Node(x, y);
        Node temp = head;
        for (int i = 0; i < number-1; i++) {
            x = rand.nextDouble()*800 + 50;
            y = rand.nextDouble()*400 + 50;
            temp.setNext(new Node(x, y, head));
            temp = temp.next();
        }
        temp = head;
        do{
            System.out.println(temp);
            temp = temp.next();
        }while (temp != head);
    }

    private void updateCarStats() {
        // allocate budget for bots and update stats for all cars as race starts
    }

    public void actionPerformed(ActionEvent event) {
        JButton action = (JButton) event.getSource();
        switch (action.getActionCommand()){
            case "Start Race" :
                break;
            case "hurry" :
                tickRate = (hurried) ? 33 : 16;
                hurried = !hurried;
                break;
            case "pause" :
                paused = !paused;
                break;
            case "restart" :
                restart();
                break;
            case "plus1" :
                break;
            case "plus2" :
                break;
            case "plus3" :
                break;
            case "minus1" :
                break;
            case "minus2" :
                break;
            case "minus3" :
                break;
        }

    }

    private void createButtons() {
        JButton plus1 = new JButton();
        plus1.addActionListener(this);
        plus1.setActionCommand("plus1");
        JButton plus2 = new JButton();
        plus2.addActionListener(this);
        plus2.setActionCommand("plus2");
        JButton plus3 = new JButton();
        plus3.addActionListener(this);
        plus3.setActionCommand("plus3");
        JButton minus1 = new JButton();
        minus1.addActionListener(this);
        minus1.setActionCommand("minus1");
        JButton minus2 = new JButton();
        minus2.addActionListener(this);
        minus2.setActionCommand("minus2");
        JButton minus3 = new JButton();
        minus3.addActionListener(this);
        minus3.setActionCommand("minus3");
        JButton startRace = new JButton();
        startRace.addActionListener(this);
        startRace.setActionCommand("Start Race");
        JButton hurry = new JButton();
        hurry.addActionListener(this);
        hurry.setActionCommand("hurry");
        JButton pause = new JButton();
        pause.addActionListener(this);
        pause.setActionCommand("pause");
        JButton restart = new JButton();
        restart.addActionListener(this);
        restart.setActionCommand("restart");
        buttons = new JButton[] {plus1, plus2, plus3, minus1, minus2, minus3, startRace, hurry, pause, restart};
    }

}
