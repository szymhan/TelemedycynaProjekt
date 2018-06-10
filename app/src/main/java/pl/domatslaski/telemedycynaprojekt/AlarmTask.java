package pl.domatslaski.telemedycynaprojekt;


public class AlarmTask {
    private long id;
    private int przegrodkaID;
    private int hour;
    private int minute;

    public AlarmTask(long id, int przegrodkaID, int hour, int minute) {
        this.id = id;
        this.przegrodkaID = przegrodkaID;
        this.hour = hour;
        this.minute = minute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPrzegrodkaID() {
        return przegrodkaID;
    }

    public void setPrzegrodkaID(int przegrodkaID) {
        this.przegrodkaID = przegrodkaID;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
