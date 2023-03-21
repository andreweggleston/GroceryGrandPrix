package refactorBranch.graphics;

import graphics.TrackPanel;
import shared.Car;
import shared.Node;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI extends JPanel {

    private JFrame frame;

    private JPanel startScreen;
    private TrackPanel trackPanel;

    private JButton[] buttons;

    public GUI(JButton[] buttons, int width, int height) {
        setupWindow(width, height);
        this.setLayout(new BorderLayout());
        this.buttons = buttons;
        startScreen = new JPanel();
        startScreen.setLayout(new BorderLayout());
        startScreen.add(buttons[6], BorderLayout.CENTER);
        this.switchToPanel(startScreen);
    }

    private void switchToPanel(JPanel panel) {
        this.removeAll();
        this.add(panel);
//        window.setContentPane(this);
    }

    public void setupWindow(int trackX, int trackY) {
        if(frame != null) {
            frame.dispose();
        }
        frame = new JFrame("Grocery Grand Prix");
        frame.setPreferredSize(new Dimension(trackX, trackY));
        frame.setResizable(false);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void toTrack(Node head, List<Car> cars) {
        trackPanel = new TrackPanel(head, cars);
        trackPanel.setVisible(true);
        trackPanel.setLayout(new BorderLayout());
        trackPanel.add(buttons[11], BorderLayout.WEST);
        this.switchToPanel(trackPanel);
        JOptionPane.showMessageDialog(this, "The race will start now");
    }
}
