import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Instant;

public class GUI extends JFrame implements MouseListener{
    private Color backgroundColor;
    private Color foregroundColor;
    private JPanel previewBar;
    private JPanel center;
    private JPanel southButtons;
    public GUI(Color backgroundColor, JButton[] buttons){
        super("GroceryGrandPrix");
        this.backgroundColor = backgroundColor;
        foregroundColor = Color.lightGray;
        this.setBackground(backgroundColor);
        this.setForeground(foregroundColor);

        setLayout(new BorderLayout());
        Container c = this.getContentPane();

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
        previewBar.add(createPreview(0));
        center.add(menu);
    }
    private JPanel createPreview(int car){
        JPanel preview = new JPanel(new BorderLayout());
        preview.setPreferredSize(new Dimension(150, 150));
        preview.setMinimumSize(new Dimension(150, 150));
        preview.setMaximumSize(new Dimension(150, 150));
        preview.setBackground(foregroundColor);

        JPanel placeholderForJIcon = new JPanel();
        placeholderForJIcon.setBackground(Color.WHITE);
        placeholderForJIcon.setPreferredSize(new Dimension(120, 80));
        placeholderForJIcon.setMaximumSize(new Dimension(120, 80));

        JPanel speedBar = new JPanel();
        //speedBar.add(new Box.Filler((new Dimension(130,6)), (new Dimension(130,6)), (new Dimension(130,6))));
        speedBar.setBackground(Color.MAGENTA);

        JPanel accelerationBar = new JPanel();
        //accelerationBar.add(new Box.Filler((new Dimension(130,6)), (new Dimension(130,6)), (new Dimension(130,6))));
        accelerationBar.setBackground(Color.CYAN);

        JPanel handlingBar = new JPanel();
        //handlingBar.add(new Box.Filler((new Dimension(130,6)), (new Dimension(130,6)), (new Dimension(130,6))));
        handlingBar.setBackground(Color.GREEN);

        JPanel statsDisplay = new JPanel();
        statsDisplay.setLayout(new GridLayout(3,1,10,10));
        statsDisplay.setBackground(Color.lightGray);
        statsDisplay.setPreferredSize(new Dimension(120,34));
        statsDisplay.setMinimumSize(new Dimension(120,34));
        statsDisplay.setMaximumSize(new Dimension(120,34));
        statsDisplay.add(speedBar);
        //statsDisplay.add(new Box.Filler((new Dimension(130,1)), (new Dimension(130,1)), (new Dimension(130,1))));
        statsDisplay.add(accelerationBar);
        //statsDisplay.add(new Box.Filler((new Dimension(130,1)), (new Dimension(130,1)), (new Dimension(130,1))));
        statsDisplay.add(handlingBar);


        JPanel previewCenter = new JPanel();
        previewCenter.setBackground(Color.lightGray);
        previewCenter.setLayout(new BoxLayout(previewCenter, BoxLayout.Y_AXIS));
        previewCenter.add(placeholderForJIcon);
        previewCenter.add(new Box.Filler((new Dimension(150,15)), (new Dimension(150,15)), (new Dimension(150,15))));
        previewCenter.add(statsDisplay);




        //JPanel previewSouth = new JPanel();

        //previewSouth.setBackground(Color.lightGray);
        //previewSouth.add(new Box.Filler((new Dimension(150,10)), (new Dimension(150,10)), (new Dimension(150,10))));

        preview.add(new Box.Filler((new Dimension(120,15)), (new Dimension(120,15)), (new Dimension(120,15))), BorderLayout.NORTH);
        preview.add(previewCenter, BorderLayout.CENTER);

        preview.add(new Box.Filler((new Dimension(15,130)), (new Dimension(15,130)), (new Dimension(15,130))), BorderLayout.WEST);
        preview.add(new Box.Filler((new Dimension(15,130)), (new Dimension(15,130)), (new Dimension(15,130))), BorderLayout.EAST);
        preview.add(new Box.Filler((new Dimension(150,15)), (new Dimension(150,15)), (new Dimension(150,15))), BorderLayout.SOUTH);
        return preview;
    }
    public void buildSegments(){

    }
    public void drawTrack() {
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
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


}
