package ATJava;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class LoginForm extends JDialog {
    private JPanel contentPane;
    private JButton loginButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JLabel Pict;
    private JLabel stateLabel;
    private PswDialogResponse authdata;

    private static final int PswPoliticMinLength = 5;
    private static final int PswPoliticMaxLength = 30;
    private static final int LoginPoliticMinLength = 5;
    private static final int LoginPoliticMaxLength = 30;

    public LoginForm() {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(loginButton);
        loginButton.setEnabled(false);
        loginField.setEnabled(false);
        passwordField.setEnabled(false);
        ErrorSet(7);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        authdata = new PswDialogResponse();
    }

    private void onOK() {
        if (isCorrect() == 1) {
            authdata.setResponse(loginField.getText(),passwordField.getText());
            dispose();
        }
        else ErrorSet(isCorrect());

    }

    private void onExit() {
        // add your code here if necessary
        //dispose();
        System.exit(0);
    }

    public int isCorrect() {
        int errcode=0;
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) errcode = 2;
            else if (passwordField.getText().length() < PswPoliticMinLength) errcode = 3;
                else if (loginField.getText().length() < LoginPoliticMinLength) errcode = 4;
                    else if (loginField.getText().length() > LoginPoliticMaxLength || passwordField.getText().length() > PswPoliticMaxLength) errcode = 5;
                        else errcode = 1;
        return errcode;
    }

    public String ErrorSet (int errcode) {
        String errmsg = "";
        switch (errcode) {
            case 2: errmsg = "Логин или пароль не введены"; break;
            case 3: errmsg = "Пароль слишком короткий"; break;
            case 4: errmsg = "Логин слишком короткий"; break;
            case 5: errmsg = "Пароль или логин слишком длинный"; break;
            case 6: errmsg = "Неверные логин или пароль"; break;
            case 7: errmsg = "Подключение к базе данных..."; break;
            case 8: errmsg = "Введите логин и пароль"; break;
            default:errmsg = "Неизвестная ошибка"; break;
        }
        stateLabel.setText(errmsg);
        return errmsg;
    }

    public int loginSet (String Login) {
        loginField.setText(Login);
        loginButton.setEnabled(true);
        loginField.setEnabled(true);
        passwordField.setEnabled(true);
        ErrorSet(8);
        return 8;
    }

    public PswDialogResponse GetResponse() {
        return authdata;
    }
}
