package neww.graphics;

import shared.Car;
import shared.Node;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI extends JPanel {

    private JFrame window;

    private JPanel startScreen;
    private TrackPanel trackPanel;

    private JButton[] buttons;

    public GUI(JButton[] buttons) {
        this.setLayout(new BorderLayout());
        this.buttons = buttons;
        startScreen = new JPanel();
        startScreen.setLayout(new BorderLayout());
        startScreen.add(buttons[6], BorderLayout.CENTER);
        this.add(startScreen, BorderLayout.CENTER);
    }

    public void setupWindow(int trackX, int trackY) {
        if(window != null) {
            window.dispose();
        }
        window = new JFrame("Grocery Grand Prix");
        window.setPreferredSize(new Dimension(trackX, trackY));
        window.setResizable(false);
        window.add(this);
        window.pack();
        window.setVisible(true);
    }

    public void toTrack(Node head, List<Car> cars) {
        trackPanel = new TrackPanel(head, cars);
        trackPanel.setVisible(true);
        this.removeAll();
        this.add(trackPanel, BorderLayout.CENTER);
        this.add(buttons[11], BorderLayout.WEST);
        JOptionPane.showMessageDialog(this, "The race will start now");
    }
}
