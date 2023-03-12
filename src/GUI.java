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
    public GUI(Color backgroundColor){
        super("GroceryGrandPrix");
        this.backgroundColor = backgroundColor;
        foregroundColor = Color.lightGray;
        this.setBackground(backgroundColor);
        this.setForeground(foregroundColor);
        setLayout(new BorderLayout());
        Container c = this.getContentPane();

        previewBar = new JPanel();
        previewBar.setLayout(new BoxLayout(previewBar, BoxLayout.X_AXIS));

        center = new JPanel();
        center.setLayout(null);
        center.setPreferredSize(new Dimension(1600, 768));

        c.add(previewBar, BorderLayout.NORTH);
        c.add((center), BorderLayout.CENTER);
        c.add(southButtons, BorderLayout.SOUTH);

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
    }

    public void playerMenu(int round, int budget, Car playerCar) {
        JPanel menu = new JPanel();
        previewBar.add(createPreview(0));

        center.add(menu);
    }
    private JPanel createPreview(int car){
        JPanel preview = new JPanel();
        preview.setPreferredSize(new Dimension(256, 256));
        return preview;
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
