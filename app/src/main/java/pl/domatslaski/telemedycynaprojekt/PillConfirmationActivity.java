package pl.domatslaski.telemedycynaprojekt;

import android.app.AlarmManager;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PillConfirmationActivity extends AppCompatActivity {

    /*MOJE NUMERY SERWISÓW I CHARAKTERYSTYK*/
    public static final String TAG = PillConfirmationActivity.class.getSimpleName();
    public static final String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String ELEMENT_NUMBER = "ELEMENT_NUMBER";
    private static final UUID SERVICE2_UUID = UUID.fromString("02c0a600-9594-48e5-b5a9-f4564cc790b9");
    private static final UUID CHARACTERISTIC5_UUID = UUID.fromString("10d8d6ac-486a-44f8-a726-7eea92c6c759");
    private static final UUID CHARACTERISTIC6_UUID = UUID.fromString("67653fcc-33f8-4fb6-a96e-164188a0eb36");
    private static final UUID CHARACTERISTIC7_UUID = UUID.fromString("902e1dce-1f19-4477-8d48-9fd234ccf903");
    private static final UUID CHARACTERISTIC8_UUID = UUID.fromString("2ab9392a-0c39-418f-8448-a9211885afa8");
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    Button mButton;
    LinearLayout mLinearLayout;
    private AlarmDbAdapter alarmDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_confirmation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Intent intent = getIntent();
        final int id = intent.getIntExtra("PRZEGRODKA_NUMBER", 0);
        int counter = intent.getIntExtra("COUNTER", 0);
        int elementNumber = intent.getIntExtra(ELEMENT_NUMBER, 10);
        elementNumber+=1;
        String mDeviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
        Log.d("test", "4" + mDeviceAddress);


      /*  if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
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
        }*/
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Log.d("test", "5 " + mDeviceAddress);
        mDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        Log.d("test", "6 " + mDevice.getAddress());
        Log.d("test", "7 " + mDevice.getName());
        mBluetoothGatt = mDevice.connectGatt(this, true, mGattCallback);

        mLinearLayout = findViewById(R.id.pill_confirmation_backgroud_layout);


        switch (id) {
            case 1:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.yellowbackgroud, null));
                lightUpDiode(1);
                break;
            case 2:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.redbackground, null));
                lightUpDiode(2);
                break;
            case 3:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.bluebackground, null));
                lightUpDiode(3);
                break;
            case 4:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.greenbackground, null));
                lightUpDiode(4);
                break;
            default:
                break;
        }


        setNextAlarm( id, counter,elementNumber,mDeviceAddress);

        mButton = findViewById(R.id.i_took_a_pill_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothGatt.disconnect();
                finish();

            }
        });

        Vibrator v;
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(3000);
    }


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() {
            mState = 0;
        }

        private void advance() {
            mState++;
        }
        /*
         * Read the data characteristic's value for each sensor explicitly
         */


        /*
         * Enable notification of changes on the data characteristic for each sensor
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
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
                //   mHandler.sendEmptyMessage(MSG_CLEAR);
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "Services Discovered: " + status);
            // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
            /*
             * With services discovered, we are going to reset our state machine and start
             * working through the sensors we need to enable
             */
            reset();
            // enableNextSensor(gatt); TO JUŻ JEST NIEAKTUALNE, PONIEWAŻ NIE POTRZEBUJĘ KONFIGUROWANIA TEGO

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "Remote RSSI: " + rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };


    public void lightUpDiode(int id) {

        int oneInHex = 49;

        if (id == 1) {

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }


            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC5_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);

        } else if (id == 2) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC6_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);


        } else if (id == 3) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC7_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);

        } else if (id == 4) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC8_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);

        }

    }


    private void setNextAlarm(int id, int counter, int elementNumber, String mDeviceAddress) {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        alarmDbAdapter = new AlarmDbAdapter(this);
        alarmDbAdapter.open();

        AlarmTask[] alarms = alarmDbAdapter.getAlarmsByPRZEGRODKAID(id);
        AlarmTask nextAlarm;
        if (elementNumber <= alarms.length - 1) {
            nextAlarm = alarms[elementNumber];
        }
        else {
            nextAlarm = alarms[0];
            elementNumber=0;
            cal.add(Calendar.DATE,1);
        }



        //cal.add(Calendar.MINUTE,2);
        cal.set(Calendar.HOUR_OF_DAY, nextAlarm.getHour());
        cal.set(Calendar.MINUTE, nextAlarm.getMinute());
        Intent confirmationIntent = new Intent(this, AlarmReceiver.class);
        confirmationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        confirmationIntent.setAction("pl.domatslaski.telemedycynaprojekt.START_ALARM");
        confirmationIntent.putExtra("PRZEGRODKA_NUMBER", id);
        confirmationIntent.putExtra(ELEMENT_NUMBER, elementNumber);
        confirmationIntent.putExtra("COUNTER", counter);
        confirmationIntent.putExtra(DEVICE_ADDRESS, mDeviceAddress);
        super.onNewIntent(confirmationIntent);
        this.setIntent(confirmationIntent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, confirmationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        alarmDbAdapter.close();
    }

}
