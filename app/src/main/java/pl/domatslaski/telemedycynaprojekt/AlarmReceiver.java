package pl.domatslaski.telemedycynaprojekt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String PRZEGRODKA_NUMBER="PRZEGRODKA_NUMBER";
    private static final String DELETE="DELETE";
    public static final String DEVICE_ADDRESS="DEVICE_ADDRESS";
    public static final String ELEMENT_NUMBER = "ELEMENT_NUMBER";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d( AlarmReceiver.class.getSimpleName(), "Wszedłem do AlarmReceiver");
        int id=intent.getIntExtra(PRZEGRODKA_NUMBER,0);
       int  toDelete=intent.getIntExtra(DELETE,0);
       int counter = intent.getIntExtra("COUNTER",0);
       int elementNumber = intent.getIntExtra(ELEMENT_NUMBER,10);
       String  mDeviceAddress=intent.getStringExtra(DEVICE_ADDRESS);
        Log.d("test","3" + mDeviceAddress);
        if(toDelete==1)
        {

        }
        else {
           // AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

           // Toast.makeText(context,"ALARM , id: "+id ,Toast.LENGTH_SHORT).show();


         /*   alarmDbAdapter=new AlarmDbAdapter(context);
            alarmDbAdapter.open();
           AlarmTask[] alarms = alarmDbAdapter.getAlarmsByPRZEGRODKAID(id);
           AlarmTask nextAlarm;
           if(elementNumber+1<=alarms.length-1)
           {
               nextAlarm = alarms[elementNumber+1];
           }
           else
           {
               nextAlarm = alarms[0];
           }


            Calendar cal= Calendar.getInstance();
            //cal.add(Calendar.MINUTE,2);
            cal.set(Calendar.HOUR_OF_DAY, nextAlarm.getHour());
            cal.set(Calendar.MINUTE,nextAlarm.getMinute());
            Intent confirmationIntent = new Intent(context, AlarmReceiver.class);
            confirmationIntent.setAction("pl.domatslaski.telemedycynaprojekt.START_ALARM");
            confirmationIntent.putExtra("PRZEGRODKA_NUMBER",id);
            confirmationIntent.putExtra("ELEMENT_NUMBER",elementNumber+1);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,id,confirmationIntent,0);
            am.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
            */
            // alarmDbAdapter.close();
            final Intent openNextIntent = new Intent(context,PillConfirmationActivity.class);
            openNextIntent.putExtra(PRZEGRODKA_NUMBER,id);
            openNextIntent.putExtra(DEVICE_ADDRESS,mDeviceAddress);
            openNextIntent.putExtra(ELEMENT_NUMBER,elementNumber);
            openNextIntent.putExtra("COUNTER", counter);
            context.startActivity(openNextIntent);

        }





    }

}
