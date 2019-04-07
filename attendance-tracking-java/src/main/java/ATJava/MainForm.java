package ATJava;

import javax.swing.*;
import java.awt.event.*;

public class MainForm extends JFrame {
    private JProgressBar progressBar1;
    private JTable table1;
    private JTabbedPane tabbedPane1;
    private JTable table2;
    private JTable table3;
    private JButton button1;
    private JPanel contentPane;
    private JPanel centralPane;
    private JPanel footerPane;
    private JPanel dataPane;
    private JPanel instrumentPane;

    public MainForm() {
        super("Main form");
        getRootPane().setDefaultButton(button1);

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPress();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onPress();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPress();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onPress() {


    }
}
