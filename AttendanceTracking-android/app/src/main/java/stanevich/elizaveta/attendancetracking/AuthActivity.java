package stanevich.elizaveta.attendancetracking;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import stanevich.elizaveta.attendancetracking.database.SQLite;


public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private EditText mETemail, mETpassword, mETsurname, mETgroups;
    private SQLite mSQLite;
    SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        mETemail = findViewById(R.id.et_email);
        mETpassword = findViewById(R.id.et_password);
        mETgroups = findViewById(R.id.et_groups);
        mETsurname = findViewById(R.id.et_surname);

        mSQLite = new SQLite(this);
        mSQLiteDatabase = mSQLite.getWritableDatabase();

        Cursor cursor = mSQLiteDatabase.query(SQLite.TABLE_NAME,
                null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int surnameIndex = cursor.getColumnIndex(SQLite.STUDENT_SURNAME);
                int groupIndex = cursor.getColumnIndex(SQLite.STUDENT_GROUP);
                int loginIndex = cursor.getColumnIndex(SQLite.STUDENT_LOGIN);
                int passwordIndex = cursor.getColumnIndex(SQLite.STUDENT_PASSWORD);

                mETemail.setText(cursor.getString(loginIndex));
                mETpassword.setText(cursor.getString(passwordIndex));
                mETsurname.setText(cursor.getString(surnameIndex));
                mETgroups.setText(cursor.getString(groupIndex));
            }

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
        Cursor cursor = mSQLiteDatabase.query(SQLite.TABLE_NAME,
                null, null, null, null, null, null);
        if (v.getId() == R.id.btn_sign_in) {
            if(!cursor.moveToFirst()) {
            saveDataInDB();
            }
            signIn(mETemail.getText().toString(), mETpassword.getText().toString());
            showDataInLog(mSQLiteDatabase);
        } else if (v.getId() == R.id.btn_registration) {
            if(!cursor.moveToFirst()) {
                saveDataInDB();
            }
            registration(mETemail.getText().toString(), mETpassword.getText().toString());
        }

    }

    private void saveDataInDB() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLite.STUDENT_LOGIN,String.valueOf(mETemail.getText()));
            contentValues.put(SQLite.STUDENT_PASSWORD,String.valueOf(mETpassword.getText()));
            contentValues.put(SQLite.STUDENT_SURNAME, String.valueOf(mETsurname.getText()));
            contentValues.put(SQLite.STUDENT_GROUP, String.valueOf(mETgroups.getText()));
            mSQLiteDatabase.insert(SQLite.TABLE_NAME,null,contentValues);

    }

    public static void showDataInLog(SQLiteDatabase database) {
        Cursor cursor = database.query(SQLite.TABLE_NAME,
                null, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(SQLite.KEY_ID);
                int surnameIndex = cursor.getColumnIndex(SQLite.STUDENT_SURNAME);
                int groupIndex = cursor.getColumnIndex(SQLite.STUDENT_GROUP);
                int loginIndex = cursor.getColumnIndex(SQLite.STUDENT_LOGIN);
                int passwordIndex = cursor.getColumnIndex(SQLite.STUDENT_PASSWORD);


                do {
                    Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                            " , login = " + cursor.getString(loginIndex) +
                            " , password = " + cursor.getString(passwordIndex) +
                            " , surname = " + cursor.getString(surnameIndex) +
                            " , group = " + cursor.getString(groupIndex));
                } while (cursor.moveToNext());

            } else {
                Log.d("mLog", "0 rows");
            }
        } finally {
            cursor.close();
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
                        getOnCompleteListener("Регистрация успешна", "Регистрация провалена, возможно необходимо увеличить пароль"));

    }
}
