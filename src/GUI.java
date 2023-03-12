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
        preview.setPreferredSize(new Dimension(150, 180));
        preview.setMinimumSize(new Dimension(150, 180));
        preview.setMaximumSize(new Dimension(150, 180));
        preview.setBackground(foregroundColor);

        JPanel placeholderForJIcon = new JPanel();
        placeholderForJIcon.setBackground(Color.WHITE);
        placeholderForJIcon.setPreferredSize(new Dimension(130, 130));
        placeholderForJIcon.setMaximumSize(new Dimension(130, 130));

        JPanel placeholderForStats = new JPanel();
        placeholderForStats.add(new Box.Filler((new Dimension(130,20)), (new Dimension(130,20)), (new Dimension(130,20))));
        placeholderForStats.setBackground(Color.DARK_GRAY);

        JPanel previewSouth = new JPanel();
        previewSouth.setLayout(new BoxLayout(previewSouth, BoxLayout.Y_AXIS));
        previewSouth.setBackground(Color.lightGray);
        previewSouth.add(new Box.Filler((new Dimension(130,10)), (new Dimension(130,10)), (new Dimension(130,10))));
        previewSouth.add(placeholderForStats);
        previewSouth.add(new Box.Filler((new Dimension(130,10)), (new Dimension(130,10)), (new Dimension(130,10))));

        preview.add(new Box.Filler((new Dimension(130,10)), (new Dimension(130,10)), (new Dimension(130,10))), BorderLayout.NORTH);
        preview.add(placeholderForJIcon, BorderLayout.CENTER);
        preview.add(previewSouth, BorderLayout.SOUTH);
        preview.add(new Box.Filler((new Dimension(10,130)), (new Dimension(10,130)), (new Dimension(10,130))), BorderLayout.WEST);
        preview.add(new Box.Filler((new Dimension(10,130)), (new Dimension(10,130)), (new Dimension(10,130))), BorderLayout.EAST);
        return preview;
    }
    public void buildSegments(){

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
