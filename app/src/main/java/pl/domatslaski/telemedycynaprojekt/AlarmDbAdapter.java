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
    private DatabaseHelper dbHelper;
    public static final int DB_VERSION =1;
    public static final String DB_NAME = "database.db";
    public static final String DB_TABLE = "alarm";

    public static final String KEY_ID="_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN=0;
    public static final String KEY_HOURINTERVAL = "hourinterval";
    public static final String HOURINTERVAL_OPTIONS = "INTEGER";
    public static final int HOURINTERVAL_COLUMN =1;

    private static final String DB_CREATE_ALARM_TABLE="CREATE TABLE "+ DB_TABLE +"( "
            + KEY_ID+ " "+ ID_OPTIONS + ", " + KEY_HOURINTERVAL + " "+ HOURINTERVAL_OPTIONS+" );";

    private  static final String DROP_ALARM_TABLE = "DROP TABLE IF EXISTS "+DB_TABLE;

    public AlarmDbAdapter(Context context)
    {
        this.context=context;
    }

    public AlarmDbAdapter open(){
        dbHelper= new DatabaseHelper(context,DB_NAME,null,DB_VERSION);
        try {
            db=dbHelper.getWritableDatabase();
        }catch (SQLException e)
        {
            db=dbHelper.getReadableDatabase();
        }
        return  this;
    }

    public void close(){
        dbHelper.close();
    }
    public long insertAlarm(int hourInterval)
    {
        ContentValues  newAlarmValues = new ContentValues();
        newAlarmValues.put(KEY_HOURINTERVAL,hourInterval);
        return db.insert(DB_TABLE,null,newAlarmValues);
    }

    public  boolean updateAlarm(AlarmTask alarmTask)
    {
        long id=alarmTask.getId();
        int hourInterval=alarmTask.getHourInterval();
        return updateAlarm(id,hourInterval);
    }

    public boolean updateAlarm(long id, int hourInterval)
    {
        String where=KEY_ID +"="+id;
        ContentValues updateAlarmValues = new ContentValues();
        updateAlarmValues.put(KEY_HOURINTERVAL,hourInterval);
        return db.update(DB_TABLE,updateAlarmValues,where,null)>0 ;
    }

    public boolean deleteAlarm(long id)
    {
        String where= KEY_ID+"="+ id;
        return db.delete(DB_TABLE,where,null)>0;
    }

    public Cursor getAllAlarms(){
        String[] columns = {KEY_ID,KEY_HOURINTERVAL};
        return db.query(DB_TABLE,columns,null,null,null,null,null);
    }
    public AlarmTask getAlarm(long id)
    {
        String[] columns = {KEY_ID,KEY_HOURINTERVAL};
        String where=KEY_ID+"="+id;
        Cursor cursor = db.query(DB_TABLE,columns,where,null,null,null,null);
        AlarmTask alarmTask=null;
        if(cursor!=null&& cursor.moveToFirst()){
            int alarmHourInterval=cursor.getInt(HOURINTERVAL_COLUMN);
            alarmTask=new AlarmTask(id,alarmHourInterval);
        }
        return alarmTask;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper (Context context, String name,
                               SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context,name,factory,version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_ALARM_TABLE);
            Log.d(DEBUG_TAG,"Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_TABLE+" ver."+DB_VERSION+" created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_ALARM_TABLE);
            Log.d(DEBUG_TAG,"All data lost, database updated to ver. "+DB_VERSION);

            onCreate(db);
        }
    }
}
