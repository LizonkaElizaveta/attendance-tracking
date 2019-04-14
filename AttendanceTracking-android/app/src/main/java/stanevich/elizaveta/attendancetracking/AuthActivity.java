package stanevich.elizaveta.attendancetracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import stanevich.elizaveta.attendancetracking.database.NotificationDbController;


public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private EditText mETemail, mETpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        mETemail = findViewById(R.id.et_email);
        mETpassword = findViewById(R.id.et_password);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);

        retrieveDataFromNotification();
    }

    private void retrieveDataFromNotification() {
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");
        Log.d("mLog", title + " " + message);
        if (title != null && message != null) {
            NotificationDbController notificationDbController = new NotificationDbController(this);
            notificationDbController.insertData(title, message, "");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sign_in) {
            signIn(mETemail.getText().toString(), mETpassword.getText().toString());
        } else if (v.getId() == R.id.btn_registration) {
            registration(mETemail.getText().toString(), mETpassword.getText().toString());
        }

    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, getOnCompleteListener("Авторизация успешна", "Авторизация провалена"));
    }

    public OnCompleteListener<AuthResult> getOnCompleteListener(final String successful, final String fail) {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AuthActivity.this, successful, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(AuthActivity.this, fail, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void registration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                        getOnCompleteListener("Регистрация успешна", "Регистрация провалена"));

    }
}
