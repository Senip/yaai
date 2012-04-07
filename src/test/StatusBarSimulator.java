
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class StatusBarSimulator {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception e) {
        }

        JFrame frame = new JFrame();
        frame.setBounds(200, 200, 600, 200);
        frame.setTitle("Status bar simulator");

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        StatusBar statusBar = new StatusBar();
        contentPane.add(statusBar, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}