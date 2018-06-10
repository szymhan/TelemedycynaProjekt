package pl.domatslaski.telemedycynaprojekt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private int id;
    private int hourInterval;
    private static final String PRZEGRODKA_NUMBER="PRZEGRODKA_NUMBER";
    private static final String HOUR_INTERVAL="HOUR_INTERVAL";
    @Override
    public void onReceive(Context context, Intent intent) {
        id=intent.getIntExtra(PRZEGRODKA_NUMBER,0);
        hourInterval=intent.getIntExtra(HOUR_INTERVAL,12);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Vibrator v;
        v=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(3000);
        Toast.makeText(context,"ALARM , id: "+id+" ,przerwa:"+hourInterval ,Toast.LENGTH_SHORT).show();

        Calendar cal= Calendar.getInstance();
        cal.add(Calendar.MINUTE,2);
        Intent confirmationIntent = new Intent(context, AlarmReceiver.class);
        confirmationIntent.setAction("pl.domatslaski.telemedycynaprojekt.START_ALARM");
        confirmationIntent.putExtra("PRZEGRODKA_NUMBER",id);
        confirmationIntent.putExtra("HOUR_INTERVAL",hourInterval);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,id,confirmationIntent,0);
        am.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);

        final Intent openNextIntent = new Intent(context,PillConfirmationActivity.class);
        openNextIntent.putExtra(PRZEGRODKA_NUMBER,id);
        openNextIntent.putExtra(HOUR_INTERVAL,hourInterval);
        context.startActivity(openNextIntent);




    }

}
