package stanevich.elizaveta.attendancetracking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText mETemail, mETpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {

                } else {

                }
            }
        };

        mETemail = findViewById(R.id.et_email);
        mETpassword = findViewById(R.id.et_password);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_sign_in){
            signin(mETemail.getText().toString(), mETpassword.getText().toString());

        } else if (v.getId() == R.id.btn_registration){
            registration(mETemail.getText().toString(), mETpassword.getText().toString());

        }

    }

    public void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(AuthActivity.this, "Авторизация успешна",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(AuthActivity.this, "Авторизация провалена",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    Toast.makeText(AuthActivity.this, "Регистрация успешна",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(AuthActivity.this, "Регистрация провалена",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
