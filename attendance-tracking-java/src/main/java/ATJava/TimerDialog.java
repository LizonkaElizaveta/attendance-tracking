package ATJava;

import javax.swing.*;
import java.awt.event.*;

public class TimerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JProgressBar timerBar;
    private JLabel timerPane;


    private Integer attendanceCheckTimeout = 60;
    private Timer timer;

    public TimerDialog(JFrame fr) {
        super(fr,"Запущена проверка посещаемости",true);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        timerBar.setMaximum(attendanceCheckTimeout);
        timerBar.setMinimum(0);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ActionListener listener = new ActionListener() {
            Integer counter = attendanceCheckTimeout;
            public void actionPerformed(ActionEvent ae) {
                counter--;
                timerBar.setValue(counter);
                timerPane.setText(counter.toString());
                if (counter<1) {
                    timer.stop();
                    onCancel();
                }
            }
        };

        timer = new Timer(1000, listener);
        timer.start();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
