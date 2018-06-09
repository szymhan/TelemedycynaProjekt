package pl.domatslaski.telemedycynaprojekt;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private int id;
    private int hourInterval;
    @Override
    public void onReceive(Context context, Intent intent) {
        id=intent.getIntExtra("PRZEGRODKA_NUMBER",0);
        hourInterval=intent.getIntExtra("HOUR_INTERVAL",12);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Vibrator v;
        v=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(3000);
        Toast.makeText(context,"ALARM , id: "+id+" ,przerwa:"+hourInterval ,Toast.LENGTH_SHORT).show();

    }

}
