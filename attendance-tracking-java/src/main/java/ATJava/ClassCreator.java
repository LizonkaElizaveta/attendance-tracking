package ATJava;

import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;

public class ClassCreator extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox disciplineComboBox;
    private JTextField timestampTextField;
    private JTextField latitudeTextField;
    private JTextField longitudeTextField;

    Data data = new Data();

    public ClassCreator(Vector<String> disciplineList, JFrame fr) {
        super(fr,"Запуск проверки посещаемости",true);
        setContentPane(contentPane);
        setModal(true);
        setLocation(200,100);
        getRootPane().setDefaultButton(buttonOK);
        for (String item : disciplineList) {
            disciplineComboBox.addItem(item);
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

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
    }

    public class Data {
        String discipline;
        String timestamp;
        String latitude;
        String longitude;
    }


    private void onOK() {
        if (checkCorrectness()) {
            data.discipline = disciplineComboBox.getSelectedItem().toString();
            data.timestamp = timestampTextField.getText();
            data.latitude = latitudeTextField.getText();
            data.longitude = longitudeTextField.getText();
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private boolean checkCorrectness() {
        if( disciplineComboBox.getSelectedItem() == null ) {
            JOptionPane.showMessageDialog(null, "Не выбрана дисциплина!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!timestampTextField.getText().matches("^\\d{8}T\\d{4}$")) {
            JOptionPane.showMessageDialog(null, "Неправильный формат даты/времени!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            return true;
        }
    }
}
