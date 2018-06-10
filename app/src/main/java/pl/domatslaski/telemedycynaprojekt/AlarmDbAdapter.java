package pl.domatslaski.telemedycynaprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ProgressBar;

public class AlarmDbAdapter {
    private static final String DEBUG_TAG = "SqLiteAlarmManager";
    private SQLiteDatabase db;
    private Context context;
    //private DatabaseHelper dbHelper;
    public static final int DB_VERSION =1;
    public static final String DB_NAME = "database.db";
    public static final String DB_TABLE = "alarm";


}
