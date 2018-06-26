package pl.domatslaski.telemedycynaprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AlarmDbAdapter {
    private static final String DEBUG_TAG = "SqLiteAlarmManager";
    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;
    public static final int DB_VERSION =1;
    public static final String DB_NAME = "database.db";
    public static final String DB_ALARM_TABLE = "alarm";
    public static final String KEY_ID="_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN=0;
    public static final String KEY_PRZEGRODKAID="przegrodkaid";
    public static final String PRZEGRODKAID_OPTIONS = "INTEGER";
    public static final int PRZEGRODKAID_COLUMN=1;
    public static final String KEY_HOUR="hour";
    public static final String HOUR_OPTIONS="INTEGER";
    public static final int HOUR_COLUMN=2;
    public static final String KEY_MINUTE="minute";
    public static final String MINUTE_OPTIONS="INTEGER";
    public static final int MINUTE_COLUMN=3;

    private static final String DB_CREATE_ALARM_TABLE = "CREATE TABLE "+DB_ALARM_TABLE+"( "+
            KEY_ID+ " " + ID_OPTIONS + ","+KEY_PRZEGRODKAID+" "+ PRZEGRODKAID_OPTIONS+", "+
            KEY_HOUR+" "+ HOUR_OPTIONS + ","+ KEY_MINUTE+" "+ MINUTE_OPTIONS+" );";

    private static final String DROP_ALARM_TABLE = "DROP TABLE IF EXISTS "+DB_ALARM_TABLE;


    public AlarmDbAdapter (Context context)
    {
        this.context=context;
    }

    public AlarmDbAdapter open()
    {
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public long insertAlarm(int przegrodkaID,int hour,int minute)
    {
        ContentValues newAlarmValues=new ContentValues();
        newAlarmValues.put(KEY_PRZEGRODKAID,przegrodkaID);
        newAlarmValues.put(KEY_HOUR,hour);
        newAlarmValues.put(KEY_MINUTE,minute);
        return db.insert(DB_ALARM_TABLE,null,newAlarmValues);
    }
    public boolean deleteAlarmWithPRZEGRODKAID(int przegrodkaID)
    {
        String where = KEY_PRZEGRODKAID+"="+przegrodkaID;
        return db.delete(DB_ALARM_TABLE,where,null)>0;
    }

    public Cursor getAllAlarms( ){
        String [] columns = {KEY_ID,KEY_PRZEGRODKAID,KEY_HOUR,KEY_MINUTE};
        return db.query(DB_ALARM_TABLE,columns,null,null,null,null,null);
    }
    public Cursor getAllAlarmsByPRZEGRODKAID(int przegrodkaid ){
        String where = KEY_PRZEGRODKAID+"="+przegrodkaid;
        String [] columns = {KEY_ID,KEY_PRZEGRODKAID,KEY_HOUR,KEY_MINUTE};
        return db.query(DB_ALARM_TABLE,columns,where,null,null,null,KEY_HOUR+" ASC");
    }

    public AlarmTask[]  getAlarmsByPRZEGRODKAID(int id){
        String [] columns = {KEY_ID,KEY_PRZEGRODKAID,KEY_HOUR,KEY_MINUTE};
        String where = KEY_PRZEGRODKAID+"="+id;
        Cursor cursor= db.query(DB_ALARM_TABLE,columns,where,null,null,null,KEY_HOUR+" ASC");

        if (cursor!=null)
        {
            AlarmTask[] result=new AlarmTask[cursor.getCount()];
            int i=0;
            while(cursor.moveToNext())
            {
                int _id=cursor.getInt(ID_COLUMN);
                int przegrodkaID=cursor.getInt(PRZEGRODKAID_COLUMN);
                int hour = cursor.getInt(HOUR_COLUMN);
                int minute=cursor.getInt(MINUTE_COLUMN);
                result[i]=new AlarmTask(_id,przegrodkaID,hour,minute);
                i++;
            }
            cursor.close();
            return result;
        }
        else {return null;}

    }
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_ALARM_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_ALARM_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_ALARM_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_ALARM_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }
}
