package stanevich.elizaveta.attendancetracking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "studentDB";
    public static final String TABLE_NAME = "StudentActivity";
    public static final String KEY_ID = "id";
    public static final String STUDENT_SURNAME = "surname";
    public static final String STUDENT_GROUP = "groups";
    public static final String STUDENT_LOGIN = "login";
    public static final String STUDENT_PASSWORD = "password";

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" + KEY_ID +
                " integer primary key, " + STUDENT_SURNAME + " text,"
                + STUDENT_LOGIN + " text," + STUDENT_PASSWORD + " text,"
                + STUDENT_GROUP + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);

    }

}
