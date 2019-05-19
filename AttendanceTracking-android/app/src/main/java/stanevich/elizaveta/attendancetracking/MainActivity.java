package stanevich.elizaveta.attendancetracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import stanevich.elizaveta.attendancetracking.constants.AppConstants;
import stanevich.elizaveta.attendancetracking.database.NotificationDbController;
import stanevich.elizaveta.attendancetracking.database.SQLite;
import stanevich.elizaveta.attendancetracking.model.NotificationModel;
import stanevich.elizaveta.attendancetracking.services.GPSService;
import stanevich.elizaveta.attendancetracking.services.OnLocationChanged;

import static stanevich.elizaveta.attendancetracking.services.GPSService.GPS_ACCESS_REQUEST;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button registration;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private Toolbar toolbar;

    private SQLite mSQLite;
    SQLiteDatabase mSQLiteDatabase;
    private Map<String, Map> mClassesMap;

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

        mSQLite = new SQLite(this);
        mSQLiteDatabase = mSQLite.getReadableDatabase();

        mReference = FirebaseDatabase.getInstance().getReference("attendancetracking-android");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_registration:

                if (true) {

                    GPSService.requestGpsPermissions(MainActivity.this);

                    checkIsStudentPresent();

                    break;
                } else {
                    mReference.child(user.getUid()).child("Attendance").push().setValue("X");
                    Toast.makeText(MainActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    registration.setBackgroundColor(getResources().getColor(R.color.f2));
                }
                break;
            case R.id.notificationView:
                Intent intent = new Intent(this, NotificationListActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void checkIsStudentPresent() {
        final GPSService gpsService = GPSService.getInstance(MainActivity.this);
        gpsService.locationChanged(new OnLocationChanged() {
            @Override
            public void changed(Location location) {
                gpsService.removeUpdates();
                Location roomLocation = new Location("ATProvider");
                roomLocation.setLatitude(59.998947);
                roomLocation.setLongitude(30.207399);
                final float accuracyMeters = 50.0f;
                if (location.distanceTo(roomLocation) < accuracyMeters) {
                    final Cursor cursor = mSQLiteDatabase.query(SQLite.TABLE_NAME,
                            null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        final int surnameIndex = cursor.getColumnIndex(SQLite.STUDENT_SURNAME);
                        final int groupIndex = cursor.getColumnIndex(SQLite.STUDENT_GROUP);

                        mReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //GenericTypeIndicator<Map> t = new GenericTypeIndicator<Map<String, Map>>() {};
                                // mClassesMap = dataSnapshot.child("Classes").getValue(t);
                                Map<String, Map> res = (Map<String, Map>) dataSnapshot.child("Classes").getValue();
                                if (res == null) return;

                                for (String key : res.keySet()) {
                                    Map map = res.get(key);
                                    if (map != null && map.containsKey("tracking")) {
                                        if ("true".equals(map.get("tracking"))) {
                                            mReference.child("Classes").child(key).child("groups").
                                                    child(cursor.getString(groupIndex)).child(user.getUid()).setValue(cursor.getString(surnameIndex)).
                                                    addOnCompleteListener(new OnCompleteListener<Void>() {

                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(MainActivity.this, "Вы зарегистрированы на занятии", Toast.LENGTH_SHORT).show();
                                                            registration.setBackgroundColor(getResources().getColor(R.color.ff));
                                                            registration.setEnabled(false);
                                                            Log.d("mLog", task.isSuccessful() + "");
                                                            Log.d("mLog", task.getException() + "");
                                                        }

                                                    });
                                            //found
                                            // break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                } else {
                    registration.setBackgroundColor(getResources().getColor(R.color.f2));
                    Toast.makeText(MainActivity.this, "Вы не зарегистрированы на занятии, ваше местоположение не соответствует аудитории", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            notificationCount.setVisibility(View.VISIBLE);
            notificationCount.setText(String.valueOf(totalUnread));
        } else {
            notificationCount.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GPS_ACCESS_REQUEST:
                checkIsStudentPresent();
                break;
        }
    }
}
