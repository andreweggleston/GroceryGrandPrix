// Phoenix Ganz-Ratzat
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener {
    private boolean hurried;
    private int budget;
    private int round;
    private int tickRate;
    private double timeElapsed;
    //private Car[] cars;
    private JButton[] buttons;
    //private GUI gui;
    //private Node head;
    public GroceryGrandPrix() {
        hurried = false;
        budget = 5;
        round = 1;
        tickRate = 33;
        timeElapsed = 0;
        //head = new Node();
        //gui = new GUI();
        //head = new Node();
        createButtons();
    }

    public void startGame() {
        openMenu();
    }

    private void openMenu() {
        /*
           gui.showPlayerMenu();
         */
    }

    private void showTrack() {
        /* generateNodes(round*4);
           gui.drawTrack(head);
         */
    }

    private void simulateRace() {}

    private void restart() {
        // reset the game to round 1
    }

    private void generateCars() {
        // create array of cars
    }

    private void endGame(/*Car winner*/) {
        // call either gui.showLose() or gui.showWin();
    }

    private void generateNodes(int number) {
        Random rand = new Random();
        double x = rand.nextDouble(800) + 50;
        double y = rand.nextDouble(400) + 50;
        Node head = new Node(x, y);
        Node temp = head;
        for (int i = 0; i < number-1; i++) {
            x = rand.nextDouble(800) + 50;
            y = rand.nextDouble(400) + 50;
            temp.setNext(new Node(x, y, head));
            temp = temp.next();
        }

    }

    private void updateCarStats() {
        // allocate budget for bots and update stats for all cars as race starts
    }

    public void actionPerformed(ActionEvent event) {
        // handle all buttons
    }

    private void createButtons() {
        JButton plus1 = new JButton();
        plus1.addActionListener(this);
        JButton plus2 = new JButton();
        plus2.addActionListener(this);
        JButton plus3 = new JButton();
        plus3.addActionListener(this);
        JButton minus1 = new JButton();
        minus1.addActionListener(this);
        JButton minus2 = new JButton();
        minus2.addActionListener(this);
        JButton minus3 = new JButton();
        minus3.addActionListener(this);
        JButton startRace = new JButton();
        startRace.addActionListener(this);
        JButton hurry = new JButton();
        hurry.addActionListener(this);
        JButton pause = new JButton();
        pause.addActionListener(this);
        JButton restart = new JButton();
        restart.addActionListener(this);
        buttons = new JButton[] {plus1, plus2, plus3, minus1, minus2, minus3, startRace, hurry, pause, restart};
    }

}