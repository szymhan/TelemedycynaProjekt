package pl.domatslaski.telemedycynaprojekt;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.UUID;


public class DeviceControlActivity extends AppCompatActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    private final Context context = this;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static int mPrzegrodkaChosen;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceAddress;
    private BluetoothDevice mDevice;
    private boolean mConnected = false;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    TextView mTextView1, mTextView2, mTextView3,mTextView4;
    Button mAddPills1, mAddPills2,mAddPills3, mAddPills4;
    ImageButton mAddAlarm1,mAddAlarm2,mAddAlarm3,mAddAlarm4,mDelete1,mDelete2,mDelete3,mDelete4,mMakeAlarmListButton,mInfoButton;
    private static int getAddedPillsNumber;
    private AlarmDbAdapter alarmDbAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        Log.d("test","1" + mDeviceAddress);
        initUIElements();

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;


        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Brak wsparcia BLE.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothGatt = mDevice.connectGatt(this, true, mGattCallback);
        alarmDbAdapter=new AlarmDbAdapter(getApplicationContext());
        alarmDbAdapter.open();


            addPills(mAddPills1,1);
            addPills(mAddPills2,2);
            addPills(mAddPills3,3);
            addPills(mAddPills4,4);

            planAlarms(mAddAlarm1,1);
            planAlarms(mAddAlarm2,2);
            planAlarms(mAddAlarm3,3);
            planAlarms(mAddAlarm4,4);

            setDeleteAlarms(mDelete1,1);
            setDeleteAlarms(mDelete2,2);
            setDeleteAlarms(mDelete3,3);
            setDeleteAlarms(mDelete4,4);

            runInfo();

            mMakeAlarmListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent alarmListIntent = new Intent(getApplicationContext(),AlarmListActivity.class);
                    startActivity(alarmListIntent);
                }
            });


    }



    @Override
    protected void onResume() {
        super.onResume();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;


        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Brak wsparcia BLE.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //mDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothGatt = mDevice.connectGatt(this, true, mGattCallback);
        alarmDbAdapter.open();
    }

    @Override
    protected void onPause() {
        alarmDbAdapter.close();
        mBluetoothGatt.disconnect();
        super.onPause();
    }

    public void initUIElements(){

        mTextView1 = findViewById(R.id.ilosc_1);
        mTextView2=findViewById(R.id.ilosc_2);
        mTextView3=findViewById(R.id.ilosc_3);
        mTextView4 =findViewById(R.id.ilosc_4);
        mAddPills1 = findViewById(R.id.add_pills1);
        mAddPills2=findViewById(R.id.add_pills2);
        mAddPills3=findViewById(R.id.add_pills3);
        mAddPills4=findViewById(R.id.add_pills4);
        mAddAlarm1=findViewById(R.id.alarm_pills1);
        mAddAlarm2=findViewById(R.id.alarm_pills2);
        mAddAlarm3=findViewById(R.id.alarm_pills3);
        mAddAlarm4=findViewById(R.id.alarm_pills4);
        mDelete1=findViewById(R.id.deletebutton1);
        mDelete2=findViewById(R.id.deletebutton2);
        mDelete3=findViewById(R.id.deletebutton3);
        mDelete4=findViewById(R.id.deletebutton4);
        mMakeAlarmListButton=findViewById(R.id.make_alarm_list_button);
        mInfoButton=findViewById(R.id.info_button);

    }

    /*MOJE NUMERY SERWISÓW I CHARAKTERYSTYK*/
    private static final UUID SERVICE_UUID         = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    private static final UUID SERVICE2_UUID         = UUID.fromString("02c0a600-9594-48e5-b5a9-f4564cc790b9");
    private static final UUID CHARACTERISTIC1_UUID = UUID.fromString( "beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private static final UUID CHARACTERISTIC2_UUID = UUID.fromString( "324cc283-b005-4d4d-983d-4b869f282b47");
    private static final UUID CHARACTERISTIC3_UUID = UUID.fromString( "247dae46-a006-4e82-8ace-a09ec6f0cc01");
    private static final UUID CHARACTERISTIC4_UUID = UUID.fromString( "28439fca-4e20-4d59-9510-19b5c14bc918");
    private static final UUID CHARACTERISTIC5_UUID = UUID.fromString( "10d8d6ac-486a-44f8-a726-7eea92c6c759");
    private static final UUID CHARACTERISTIC6_UUID = UUID.fromString( "67653fcc-33f8-4fb6-a96e-164188a0eb36");
    private static final UUID CHARACTERISTIC7_UUID = UUID.fromString( "902e1dce-1f19-4477-8d48-9fd234ccf903");
    private static final UUID CHARACTERISTIC8_UUID = UUID.fromString( "2ab9392a-0c39-418f-8448-a9211885afa8");
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR    = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() { mState = 0; }

        private void advance() { mState++; }
        /*
         * Read the data characteristic's value for each sensor explicitly
         */
        private void readNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(TAG, "Reading characteristic1 (przegródka1)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC1_UUID);
                    break;
                case 1:
                    Log.d(TAG, "Reading characteristic2 (przegródka2)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC2_UUID);
                    break;
                case 2:
                    Log.d(TAG, "Reading characteristic3 (przegródka3)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC3_UUID);
                    break;
                case 3:
                    Log.d(TAG, "Reading characteristic4 (przegródka4)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC4_UUID);
                    break;
                default:
                  //  mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled");
                    return;
            }

            gatt.readCharacteristic(characteristic);
        }

        /*
         * Enable notification of changes on the data characteristic for each sensor
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */
        private void setNotifyNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(TAG, "Set notify characteristic1 (przegródka1)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC1_UUID);
                    break;
                case 1:
                    Log.d(TAG, "Set notify characteristic2 (przegródka2)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC2_UUID);
                    break;
                case 2:
                    Log.d(TAG, "Set notify characteristic3 (przegródka3)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC3_UUID);
                    break;
                case 3:
                    Log.d(TAG, "Set notify characteristic4 (przegródka4)");
                    characteristic = gatt.getService(SERVICE_UUID)
                            .getCharacteristic(CHARACTERISTIC4_UUID);
                    break;
                default:
                 //   mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled");
                    return;
            }

            //Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            //Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */
                gatt.discoverServices();
              //  mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*
                 * If at any point we disconnect, send a message to clear the weather values
                 * out of the UI
                 */
                mHandler.sendEmptyMessage(MSG_CLEAR);
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "Services Discovered: "+status);
           // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
            /*
             * With services discovered, we are going to reset our state machine and start
             * working through the sensors we need to enable
             */
            reset();
            // enableNextSensor(gatt); TO JUŻ JEST NIEAKTUALNE, PONIEWAŻ NIE POTRZEBUJĘ KONFIGUROWANIA TEGO
            readNextSensor(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //For each read, pass the data up to the UI thread to update the display
            if (CHARACTERISTIC1_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_1, characteristic));
            }
            if (CHARACTERISTIC2_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_2, characteristic));
            }
            if (CHARACTERISTIC3_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_3, characteristic));
            }
            if (CHARACTERISTIC4_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_4, characteristic));
            }

            //After reading the initial value, next we enable notifications
            setNotifyNextSensor(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value
            setNotifyNextSensor(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */
            if (CHARACTERISTIC1_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_1, characteristic));
            }
            if (CHARACTERISTIC2_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_2, characteristic));
            }
            if (CHARACTERISTIC3_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_3, characteristic));
            }
            if (CHARACTERISTIC4_UUID.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_4, characteristic));
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //Once notifications are enabled, we move to the next sensor and start over with enable
            advance();
            readNextSensor(gatt);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "Remote RSSI: "+rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "BAZINGA";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "BAZINGA";
                case BluetoothProfile.STATE_CONNECTING:
                    return "BAZINGA";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "BAZINGA";
                default:
                    return String.valueOf(status);
            }
        }
    };


    private static final int MSG_1 = 101;
    private static final int MSG_2 = 102;
    private static final int MSG_3 = 103;
    private static final int MSG_4 = 104;
    //  private static final int MSG_PROGRESS = 201;
    // private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;

    private static class MyHandler extends Handler
    {
        private final WeakReference<DeviceControlActivity> mActivity;
        public MyHandler( DeviceControlActivity activity){
            mActivity = new WeakReference<DeviceControlActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceControlActivity activity = mActivity.get();
            if (activity!=null)
            {

                BluetoothGattCharacteristic characteristic;
                switch (msg.what) {
                    case MSG_1:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udalo się pozyskać danych dla 1 przegródki");
                            return;
                        }

                        activity.updatePrzegrodka1(characteristic);
                        break;
                    case MSG_2:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udalo się pozyskać danych dla 2 przegródki");
                            return;
                        }
                        activity.updatePrzegrodka2(characteristic);
                        break;
                    case MSG_3:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udalo się pozyskać danych dla 3 przegródki");
                            return;
                        }
                        activity.updatePrzegrodka3(characteristic);
                        break;
                    case MSG_4:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udalo się pozyskać danych dla 4 przegródki");
                            return;
                        }
                        activity.updatePrzegrodka4(characteristic);
                        break;
                    case MSG_CLEAR:
                        activity.clearDisplayValues();
                        break;
                }
            }
        }
    }

    private  MyHandler mHandler = new MyHandler(this);


    /* Methods to extract sensor data and update the UI */

    private void updatePrzegrodka1(BluetoothGattCharacteristic characteristic) {
        //TODO: WYSWIETLENIE WARTOSCI DANEJ CHARAKTERYSTYKI1

        byte [] dataInput1 = characteristic.getValue();
        int przegrodka1 = toInt(dataInput1);
        mTextView1.setText(String.valueOf(przegrodka1));
        updateColor(mTextView1,przegrodka1);

    }

    private void updatePrzegrodka2(BluetoothGattCharacteristic characteristic) {
        //TODO: WYSWIETLENIE WARTOSCI DANEJ CHARAKTERYSTYKI2
        byte [] dataInput2 = characteristic.getValue();
        if(dataInput2==null)
        {
            Toast.makeText(getApplicationContext(),"Nie udało sie. Spróbuj ponownie za chwilę.",Toast.LENGTH_SHORT).show();
            return;
        }
        int przegrodka2 = toInt(dataInput2);
        mTextView2.setText(String.valueOf(przegrodka2));
        updateColor(mTextView2,przegrodka2);

    }

    private void updatePrzegrodka3(BluetoothGattCharacteristic characteristic) {
        //TODO: WYSWIETLENIE WARTOSCI DANEJ CHARAKTERYSTYKI3
        byte [] dataInput3 = characteristic.getValue();
        int przegrodka3 = toInt(dataInput3);
        mTextView3.setText(String.valueOf(przegrodka3));
        updateColor(mTextView3,przegrodka3);
    }
    private void updatePrzegrodka4(BluetoothGattCharacteristic characteristic) {
        //TODO: WYSWIETLENIE WARTOSCI DANEJ CHARAKTERYSTYKI4
        byte [] dataInput4 = characteristic.getValue();
        int przegrodka4 = toInt(dataInput4);
        mTextView4.setText(String.valueOf(przegrodka4));
        updateColor(mTextView4,przegrodka4);
    }


    private void addValueToPrzegrodka(BluetoothGattCharacteristic characteristic, int a)
    {
        byte [] dataInput1 = characteristic.getValue();
       // int przegrodka1 = toInt(dataInput1);
        if(a<=9)
        {
            a=a+48; //Procedura zamiany na HEX, 0x30 (czyli 48) ==0
            byte[] bytes = ByteBuffer.allocate(4).putInt(a).array();
            byte [] postBytes={bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);
        } else {
            Toast.makeText(this,"Nie można mieć więcej niż 9 tabletek",Toast.LENGTH_SHORT).show();
        }

        }


    private void clearDisplayValues() {
        // TODO: UZUPELNIENIE ZEROWANIA WYSWIETLANYCH WARTOSCI (ILOSCI TABLETEK)
        mTextView1.setText("---");
        mTextView2.setText("---");
        mTextView3.setText("---");
        mTextView4.setText("---");
    }

    private String bytesToString (byte []b)
    {
        try{
            return new String (b,"UTF-8");
        }
        catch (UnsupportedEncodingException ex){
            Log.d(TAG, "NIE PYKŁO");
        }
        return null;
    }

    private int toInt(byte [] b1)
    {
        String s1=bytesToString(b1);
        Log.d(TAG,s1);
        int y=Integer.parseInt(s1);
        return y;
    }

    private void updateColor (TextView textView, int a)
    {
        if(a==3||a==2)
        {
            textView.setTextColor(Color.rgb(230,126,34));
        }
        else if(a==1|| a==0)
        {
            textView.setTextColor(Color.RED);
        }
        else
        {
            textView.setTextColor(Color.GREEN);
        }
    }

    void addPills(Button button, final int switchInt){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = promptsView.findViewById(R.id.add_pills_number_edit_text);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // konwersja wprowadzonego tekstu do int
                                        getAddedPillsNumber=Integer.parseInt(userInput.getText().toString());

                                        if(switchInt==1)
                                        {
                                            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                    .getCharacteristic(CHARACTERISTIC1_UUID);
                                            addValueToPrzegrodka(characteristic,getAddedPillsNumber);
                                        }
                                        else if(switchInt==2)
                                        {
                                            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                    .getCharacteristic(CHARACTERISTIC2_UUID);
                                            addValueToPrzegrodka(characteristic,getAddedPillsNumber);
                                        }
                                        else if(switchInt==3)
                                        {
                                            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                    .getCharacteristic(CHARACTERISTIC3_UUID);
                                            addValueToPrzegrodka(characteristic,getAddedPillsNumber);
                                        }
                                        else if(switchInt==4)
                                        {
                                            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                    .getCharacteristic(CHARACTERISTIC4_UUID);
                                            addValueToPrzegrodka(characteristic,getAddedPillsNumber);
                                        }
                                        else if (switchInt==5)
                                        {BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                                                .getCharacteristic(CHARACTERISTIC5_UUID);
                                            int a=49; //zeby zamienic na hex
                                            byte[] bytes = ByteBuffer.allocate(4).putInt(a).array();
                                            byte [] postBytes={bytes[3]};
                                            characteristic.setValue(postBytes);
                                            mBluetoothGatt.writeCharacteristic(characteristic);

                                        }

                                        getAddedPillsNumber=0;
                                    }
                                })
                        .setNegativeButton("Anuluj",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }


    void setAlarm(int id, int hour, int minute,int counter)
    {
        Calendar cal= Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.SECOND,0);
      //  cal.add(Calendar.MINUTE,1);
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
      Intent intent = new Intent(DeviceControlActivity.this, AlarmReceiver.class);
     // Intent intent = new Intent(this, AlarmReceiver.class);
       intent.setAction("pl.domatslaski.telemedycynaprojekt.START_ALARM");
       intent.putExtra("PRZEGRODKA_NUMBER",id);
       intent.putExtra("COUNTER",counter);
       intent.putExtra("ELEMENT_NUMBER",0);
       intent.putExtra("DEVICE_ADDRESS",mDeviceAddress);
        Log.d("test","2" + mDeviceAddress);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),id,intent,0);
       // PendingIntent pendingIntent=PendingIntent.getBroadcast(this,id,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        Log.d(TAG,cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+" dodano alarm");
     //   alarmDbAdapter.insertAlarm(id,hour,minute);
        Toast.makeText(getApplicationContext(),cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+" dodano alarm",Toast.LENGTH_SHORT).show();

    }

    void setDeleteAlarms(ImageButton button, final int przegrodkaID){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View setAlarmView = li.inflate(R.layout.delete_request, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setView(setAlarmView);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("TAK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                        Calendar cal= Calendar.getInstance();
                                        //cal.add(Calendar.MINUTE,1);
                                        Intent deleteIntent = new Intent(DeviceControlActivity.this, AlarmReceiver.class);
                                        deleteIntent.setAction("pl.domatslaski.telemedycynaprojekt.START_ALARM");
                                        deleteIntent.putExtra("PRZEGRODKA_NUMBER",przegrodkaID);
                                        deleteIntent.putExtra("DELETE",1);
                                        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),przegrodkaID,deleteIntent,0);
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent); //procedura usunięcia powiadomienia
                                        alarmManager.cancel(pendingIntent);
                                        alarmDbAdapter.deleteAlarmWithPRZEGRODKAID(przegrodkaID);
                                        Toast.makeText(getApplicationContext(),"Usunięto",Toast.LENGTH_SHORT).show();

                                    }
                                })
                        .setNegativeButton("NIE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }

    void planAlarms(ImageButton button, int id) {
        final int przegrodka = id;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DeviceControlActivity.this,R.style.mydialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alarm_popup_window);

                dialog.setCanceledOnTouchOutside(true);

              final TimePicker timePicker1 = dialog.findViewById(R.id.timepicker1);
                final TimePicker timePicker2 = dialog.findViewById(R.id.timepicker2);
                final TimePicker timePicker3 = dialog.findViewById(R.id.timepicker3);
                final TimePicker timePicker4 = dialog.findViewById(R.id.timepicker4);
                final TimePicker timePicker5 = dialog.findViewById(R.id.timepicker5);
                timePicker1.setIs24HourView(true);
                timePicker2.setIs24HourView(true);
                timePicker3.setIs24HourView(true);
                timePicker4.setIs24HourView(true);
                timePicker5.setIs24HourView(true);
                timePicker1.setHour(0);
                timePicker1.setMinute(0);
                timePicker2.setHour(0);
                timePicker2.setMinute(0);
                timePicker3.setHour(0);
                timePicker3.setMinute(0);
                timePicker4.setHour(0);
                timePicker4.setMinute(0);
                timePicker5.setHour(0);
                timePicker5.setMinute(0);

                Button ok = dialog.findViewById(R.id.alarm_popup_accept_button);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    int counter=0;
                    if(timePicker1.getHour()!=0 || timePicker1.getMinute()!=0) {
                        counter++;
                        alarmDbAdapter.insertAlarm(przegrodka,timePicker1.getHour(),timePicker1.getMinute());
                    }
                    if(timePicker2.getHour()!=0|| timePicker2.getMinute()!=0)
                    { counter++;
                        alarmDbAdapter.insertAlarm(przegrodka,timePicker2.getHour(),timePicker2.getMinute());
                    }

                    if(timePicker3.getHour()!=0 || timePicker3.getMinute()!=0) {
                        counter++;
                        alarmDbAdapter.insertAlarm(przegrodka,timePicker3.getHour(),timePicker3.getMinute());
                    }
                    if(timePicker4.getHour()!=0|| timePicker4.getMinute()!=0) {
                        counter++;
                        alarmDbAdapter.insertAlarm(przegrodka,timePicker4.getHour(),timePicker4.getMinute());
                    }
                    if(timePicker5.getHour()!=0|| timePicker5.getMinute()!=0) {
                        counter++;
                        alarmDbAdapter.insertAlarm(przegrodka,timePicker5.getHour(),timePicker5.getMinute());
                    }
                    setAlarm(przegrodka,timePicker1.getHour(),timePicker1.getMinute(),counter);

                        dialog.cancel();
                    }
                });
                dialog.show();
             //   Window window = dialog.getWindow();
              //  window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

            }
        });

    }

    void runInfo(){

        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(DeviceControlActivity.this,R.style.mydialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.info);

                dialog.setCanceledOnTouchOutside(true);

                Button button = dialog.findViewById(R.id.welcome_exit);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });


                dialog.show();
            }
        });
    }



}
