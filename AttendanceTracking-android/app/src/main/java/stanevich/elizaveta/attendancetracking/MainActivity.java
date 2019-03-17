package stanevich.elizaveta.attendancetracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registration = findViewById(R.id.button_registration);
        registration.setOnClickListener(this);
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
                    Toast.makeText(MainActivity.this,"Вы зарегистрированы на занятии",Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.ff));
                    break;
                }
                else{
                    Toast.makeText(MainActivity.this,"Ошибка сети",Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.f2));}
        }
    }
}
