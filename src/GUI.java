import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GUI extends JFrame implements MouseListener{
    private Color backgroundColor;
    private Color foregroundColor;
    private ArrayList<JPanel> previews;
    private JPanel previewBar;
    private JPanel center;
    private JPanel track;
    private JPanel southButtons;
    boolean firstDraw;
    public GUI(Color backgroundColor, JButton[] buttons){
        super("GroceryGrandPrix");
        firstDraw = true;
        this.backgroundColor = backgroundColor;
        foregroundColor = Color.lightGray;
        this.setBackground(backgroundColor);
        this.setForeground(foregroundColor);

        setLayout(new BorderLayout());
        Container c = this.getContentPane();

        previews = new ArrayList<JPanel>();
        previewBar = new JPanel();
        previewBar.setLayout(new BoxLayout(previewBar, BoxLayout.X_AXIS));
        previewBar.setBackground(backgroundColor);

        center = new JPanel();
        center.setLayout(null);
        center.setPreferredSize(new Dimension(900, 540));
        center.setBackground(backgroundColor);
        this.playerMenu(0, 20);

        c.add(previewBar, BorderLayout.NORTH);
        c.add((center), BorderLayout.CENTER);
        //c.add(southButtons, BorderLayout.SOUTH);

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
    }

    public void playerMenu(int round, int budget /* , Car playerCar*/) {
        JPanel menu = new JPanel();
        createPreview(0);
        center.add(menu);
    }
    public void createPreview(int car){
        previews.add(car, new JPanel(new BorderLayout()));
        previews.get(car).setPreferredSize(new Dimension(150, 150));
        previews.get(car).setMinimumSize(new Dimension(150, 150));
        previews.get(car).setMaximumSize(new Dimension(150, 150));
        previews.get(car).setBackground(foregroundColor);

        JPanel placeholderForJIcon = new JPanel();
        placeholderForJIcon.setBackground(Color.WHITE);
        placeholderForJIcon.setPreferredSize(new Dimension(120, 80));
        placeholderForJIcon.setMaximumSize(new Dimension(120, 80));

        JPanel speedBar = new JPanel();
        speedBar.setBackground(Color.MAGENTA);

        JPanel accelerationBar = new JPanel();
        accelerationBar.setBackground(Color.CYAN);

        JPanel handlingBar = new JPanel();
        handlingBar.setBackground(Color.GREEN);

        JPanel statsDisplay = new JPanel();
        statsDisplay.setLayout(new GridLayout(3,1,10,10));
        statsDisplay.setBackground(Color.lightGray);
        statsDisplay.setPreferredSize(new Dimension(120,34));
        statsDisplay.setMinimumSize(new Dimension(120,34));
        statsDisplay.setMaximumSize(new Dimension(120,34));
        statsDisplay.add(speedBar);
        statsDisplay.add(accelerationBar);
        statsDisplay.add(handlingBar);


        JPanel previewCenter = new JPanel();
        previewCenter.setBackground(Color.lightGray);
        previewCenter.setLayout(new BoxLayout(previewCenter, BoxLayout.Y_AXIS));
        previewCenter.add(placeholderForJIcon);
        previewCenter.add(new Box.Filler((new Dimension(150,15)), (new Dimension(150,15)), (new Dimension(150,15))));
        previewCenter.add(statsDisplay);

        previews.get(car).add(new Box.Filler((new Dimension(120,15)), (new Dimension(120,15)), (new Dimension(120,15))), BorderLayout.NORTH);
        previews.get(car).add(previewCenter, BorderLayout.CENTER);
        previews.get(car).add(new Box.Filler((new Dimension(15,130)), (new Dimension(15,130)), (new Dimension(15,130))), BorderLayout.WEST);
        previews.get(car).add(new Box.Filler((new Dimension(15,130)), (new Dimension(15,130)), (new Dimension(15,130))), BorderLayout.EAST);
        previews.get(car).add(new Box.Filler((new Dimension(150,15)), (new Dimension(150,15)), (new Dimension(150,15))), BorderLayout.SOUTH);
        previewBar.add(previews.get(car), car);
    }
    public void buildSegments(){

    }
    public void drawTrack() {

        if(firstDraw){

        }
    }

    public void showWin() {
    }

    public void showLose() {
    }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {
        switch (((JButton) e.getSource()).getActionCommand()){
            case "Speed Help" :
                break;
            case "Acceleration Help" :
                break;
            case "Handling Help" :
                break;
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {}


}
