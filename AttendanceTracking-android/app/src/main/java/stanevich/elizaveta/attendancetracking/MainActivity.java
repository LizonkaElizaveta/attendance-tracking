package stanevich.elizaveta.attendancetracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import stanevich.elizaveta.attendancetracking.constants.AppConstants;
import stanevich.elizaveta.attendancetracking.database.NotificationDbController;
import stanevich.elizaveta.attendancetracking.listeners.ListItemClickListener;
import stanevich.elizaveta.attendancetracking.model.NotificationModel;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button registration;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private Toolbar toolbar;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    private RelativeLayout mNotificationView;

    ArrayList<String> ListUserTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationView = (RelativeLayout) findViewById(R.id.notificationView);
        mNotificationView.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registration = findViewById(R.id.button_registration);
        registration.setOnClickListener(this);

        ListUserTasks = new ArrayList<>();

        mReference = FirebaseDatabase.getInstance().getReference("attendancetracking-android");


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_registration:

                if (true) {
                    mReference.child(user.getUid()).child("Attendance").push().setValue("+").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("mLog", task.isSuccessful() + "");
                            Log.d("mLog", task.getException() + "");

                        }
                    });
                    Toast.makeText(MainActivity.this, "Вы зарегистрированы на занятии", Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.ff));
                    break;
                } else {
                    mReference.child(user.getUid()).child("Attendance").push().setValue("X");
                    Toast.makeText(MainActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.f2));
                }
                break;
            case R.id.notificationView:
                Intent intent = new Intent(this,NotificationListActivity.class);
                startActivity(intent);
                break;
        }
    }


    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("mLog", "OnResume");
//register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstants.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();

    }

    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(this);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }


    }


}
