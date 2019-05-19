package ATJava;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;

public class LoginFormTest {

    @Test
    public void isCorrect() {
        int expected = 2;
        LoginForm form = new LoginForm(new MainForm());
        int actual = form.isCorrect();
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void errorSet() {
        String expected = "Логин или пароль не введены";
        LoginForm form = new LoginForm(new MainForm());
        String actual = form.ErrorSet(2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loginSet() {
        int expected = 8;
        LoginForm form = new LoginForm(new MainForm());
        int actual = form.loginSet("testLogin");
        Assert.assertEquals(actual, expected);
    }


}