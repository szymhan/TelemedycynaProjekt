package pl.domatslaski.telemedycynaprojekt;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AlarmCursorAdapter extends CursorAdapter {

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
    public AlarmCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_alarm, parent, false);

    }

   @Override
    public void bindView(View view, Context context, Cursor cursor) {

       TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
       TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);
       TextView tvPriority2=view.findViewById(R.id.tvPriority2);
       String body = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRZEGRODKAID));
       int priority = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HOUR));
       int priority2= cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MINUTE));

       tvBody.setText(body);
       tvPriority.setText(String.valueOf(priority));
       tvPriority2.setText(String.valueOf(priority2));
    }
}
