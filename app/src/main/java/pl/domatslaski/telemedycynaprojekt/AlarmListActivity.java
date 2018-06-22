package pl.domatslaski.telemedycynaprojekt;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class AlarmListActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);


        AlarmDbAdapter alarmDbAdapter= new AlarmDbAdapter(getApplicationContext());
        alarmDbAdapter.open();
     //   Cursor alarmCursor = alarmDbAdapter.getAllAlarms();
        Cursor alarmCursor = alarmDbAdapter.getAllAlarms();

        ListView listView=findViewById(R.id.content_list);

        AlarmCursorAdapter alarmCursorAdapter = new AlarmCursorAdapter(this,alarmCursor);

        listView.setAdapter(alarmCursorAdapter);

        Button button=findViewById(R.id.content_list_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
