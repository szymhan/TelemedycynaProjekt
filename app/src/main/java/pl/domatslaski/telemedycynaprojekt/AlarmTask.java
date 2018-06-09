package pl.domatslaski.telemedycynaprojekt;

import android.content.ContentValues;

import static pl.domatslaski.telemedycynaprojekt.AlarmDbAdapter.KEY_HOURINTERVAL;
import static pl.domatslaski.telemedycynaprojekt.AlarmDbAdapter.KEY_ID;

public class AlarmTask {
    private long id;
    private int hourInterval;

    public AlarmTask (long id, int hourInterval)
    {
        this.id=id;
        this.hourInterval=hourInterval;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHourInterval() {
        return hourInterval;
    }

    public void setHourInterval(int hourInterval) {
        this.hourInterval = hourInterval;
    }


}
