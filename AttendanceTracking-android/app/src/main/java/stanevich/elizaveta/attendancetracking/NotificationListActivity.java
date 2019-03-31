package stanevich.elizaveta.attendancetracking;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import stanevich.elizaveta.attendancetracking.adapters.NotificationAdapter;
import stanevich.elizaveta.attendancetracking.constants.AppConstants;
import stanevich.elizaveta.attendancetracking.database.NotificationDbController;
import stanevich.elizaveta.attendancetracking.listeners.ListItemClickListener;
import stanevich.elizaveta.attendancetracking.model.NotificationModel;
import stanevich.elizaveta.attendancetracking.utilities.DialogUtilities;

public class NotificationListActivity extends BaseActivity implements DialogUtilities.OnCompleteListener {

    private Context mContext;
    private Activity mActivity;

    private RecyclerView mRecycler;
    private NotificationAdapter mNotificationAdapter;
    private ArrayList<NotificationModel> mNotificationList;
    private MenuItem mMenuItemDeleteAll;
    private NotificationDbController mNotificationDbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = NotificationListActivity.this;
        mContext = mActivity.getApplicationContext();

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mNotificationList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_notification);

        mRecycler = (RecyclerView) findViewById(R.id.rv_recycler);
        mNotificationAdapter = new NotificationAdapter(mActivity, mNotificationList);
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecycler.setAdapter(mNotificationAdapter);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getString(R.string.notifications));
        enableUpButton();
    }

    private void updateUI() {
        showLoader();

        if (mNotificationDbController == null) {
            mNotificationDbController = new NotificationDbController(mContext);
        }
        mNotificationList.clear();
        mNotificationList.addAll(mNotificationDbController.getAllData());
        mNotificationAdapter.notifyDataSetChanged();

        hideLoader();

        if (mNotificationList.size() == 0) {
            showEmptyView();
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(false);
            }
        } else {
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(true);
            }
        }
    }

    private void initListener() {
        // recycler list item click listener
        mNotificationAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mNotificationDbController.updateStatus(mNotificationList.get(position).getId(), true);

            }
        });

    }

}
