package stanevich.elizaveta.attendancetracking;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registration;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    FirebaseListAdapter mAdapter;

    ArrayList<String> ListUserTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registration = findViewById(R.id.button_registration);
        registration.setOnClickListener(this);

        ListUserTasks = new ArrayList<>();

        mReference = FirebaseDatabase.getInstance().getReference("attendancetracking-android");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_registration :

                if(true) {
                    mReference.child(user.getUid()).child("Attendance").push().setValue("+").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("mLog",task.isSuccessful() + "");
                            Log.d("mLog",task.getException() + "");

                        }
                    });
                    Toast.makeText(MainActivity.this,"Вы зарегистрированы на занятии",Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.ff));
                    break;
                }
                else{
                    mReference.child(user.getUid()).child("Attendance").push().setValue("X");
                    Toast.makeText(MainActivity.this,"Ошибка сети",Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.f2));}
        }
    }
}
