// Phoenix Ganz-Ratzat
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GroceryGrandPrix implements ActionListener {
    private boolean hurried;
    private boolean paused;
    private int budget;
    private int round;
    private int trackX;
    private int trackY;
    private int tickRate;
    private double timeElapsed;
    private GUI gui;
    private Node head;
    private ArrayList <Car> cars ;
    private JButton[] buttons;
    private ImageIcon[] icons;
    public GroceryGrandPrix() {
        hurried = false;
        paused = false;
        budget = 5;
        round = 1;
        tickRate = 33;
        timeElapsed = 0;
        cars = new ArrayList<Car>();
        //generateCars();
        createButtons();
        trackX = 1920;
        trackY = 830;
        gui = new GUI(Color.WHITE, buttons, trackX, trackY);
        showTrack();
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
        generateNodes(round*6);
        gui.buildSegments(head);
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

    private void fileReader() throws IOException {
        File[] spriteFiles = (new File("sprites")).listFiles();
        icons = new ImageIcon[spriteFiles.length];

        for (int i = 0; i < spriteFiles.length; i++) {
            icons[i] = new ImageIcon(ImageIO.read(spriteFiles[i]));
        }
    }


    private void generateCars() {
        Random rand = new Random();
        Car car;
        Node temp = head;
        int iconIndex;
        double statPicker;
        int maxStat = 10;
        int statPoints = 10;
        // The number at which stat2's lower bound becomes 1 after which the upper bound will begin to decrease.
        int boundOffset = (statPoints - maxStat) - 1;
        int stat1;
        int stat2;
        int stat3;


        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                car = new Car(icons[0], 5, 5, 5, temp, true);
            }
            // Generate 3 random stat numbers, then create a car with a random stat number passed in for each of the cars stats.
            else {
                // Determines which stat variable will be passed to each Car stat.
                statPicker = Math.random();
                /* Generates a number from 1 to the maximum number of points that can be allocated to any stat, inclusive.
                   If the stat points allocated across all stats is less than the stat maximum, stat points - 2 is used instead to ensure each stat gets at least 1 point. */
                stat1 = rand.nextInt(Math.min(maxStat, statPoints - 2)) + 1;
                /* Generates a number with an upper and lower bound dependent on stat1, with the minimum being 1 and the maximum being equal to maxStat.
                   If stat1 only leaves 4 stats to be divided between the other two stats this stat could have any value from 1-3.
                   If stat1 leaves 20 stats this will always equal 10. */
                stat2 = rand.nextInt(maxStat - Math.abs((boundOffset) - stat1)) + Math.max(1, (boundOffset + 1) - stat1);
                // Assigns the remaining unallocated stat points to the last stat.
                stat3 = (statPoints - stat1) - stat2;

                iconIndex = rand.nextInt(icons.length);

                // Randomizes the order in which each of the stats are passed to Car's constructor to offset any bias towards each stat.
                if (statPicker > (2.0/3.0)) {
                    car = new Car(icons[iconIndex], stat1, (statPicker >= (5.0/6.0)) ? stat2 : stat3,  (statPicker >= (5.0/6.0)) ? stat3 : stat2, temp, false);
                }
                else if (statPicker >= (1.0/3.0)) {
                    car = new Car(icons[iconIndex], stat2, (statPicker >= .5) ? stat1 : stat3,  (statPicker >= .5) ? stat3 : stat1, temp, false);
                }
                else {
                    car = new Car(icons[iconIndex], stat3, (statPicker > (1.0/6.0)) ? stat1 : stat2,  (statPicker > (1.0/6.0)) ? stat2 : stat1, temp,  false);
                }
            }
            cars.set(i, car);
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

    public static void main(String[] args) {
        GroceryGrandPrix test = new GroceryGrandPrix();
    }

}
