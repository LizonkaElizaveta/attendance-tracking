package ATJava;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.Assert;
import org.junit.Test;


public class MainFormTest {

    @Test
    public void makeAuth() {
        MainForm form = new MainForm();
        boolean actual = form.makeAuth(form.dataAcessInitialization());
        Assert.assertTrue(!actual);
    }
}