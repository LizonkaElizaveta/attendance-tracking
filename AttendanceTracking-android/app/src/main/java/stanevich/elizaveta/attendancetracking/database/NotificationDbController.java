package stanevich.elizaveta.attendancetracking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import stanevich.elizaveta.attendancetracking.model.NotificationModel;

public class NotificationDbController {

    private SQLiteDatabase mDb;

    private static final String mREAD = "read", mUNREAD = "unread";

    public NotificationDbController(Context context) {
        mDb = DbHelper.getInstance(context).getWritableDatabase();
    }

    public int insertData(String title, String message, String contentUrl) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_NOTI_TITLE, title);
        values.put(DbConstants.COLUMN_NOTI_MESSAGE, message);
        values.put(DbConstants.COLUMN_NOTI_READ_STATUS, mUNREAD);
        values.put(DbConstants.COLUMN_NOTI_CONTENT_URL, contentUrl);

        // Insert the new row, returning the primary key value of the new row
        return (int) mDb.insert(
                DbConstants.NOTIFICATION_TABLE_NAME,
                DbConstants.COLUMN_NAME_NULLABLE,
                values);
    }

}
