package pl.domatslaski.telemedycynaprojekt;

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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PillConfirmationActivity extends AppCompatActivity {

    /*MOJE NUMERY SERWISÓW I CHARAKTERYSTYK*/
    public static final String TAG = PillConfirmationActivity.class.getSimpleName();
    private static final UUID SERVICE_UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    private static final UUID SERVICE2_UUID = UUID.fromString("02c0a600-9594-48e5-b5a9-f4564cc790b9");
    private static final UUID CHARACTERISTIC1_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private static final UUID CHARACTERISTIC2_UUID = UUID.fromString("324cc283-b005-4d4d-983d-4b869f282b47");
    private static final UUID CHARACTERISTIC3_UUID = UUID.fromString("247dae46-a006-4e82-8ace-a09ec6f0cc01");
    private static final UUID CHARACTERISTIC4_UUID = UUID.fromString("28439fca-4e20-4d59-9510-19b5c14bc918");
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
        String mDeviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
        Log.d("test", "4" + mDeviceAddress);

        alarmDbAdapter = new AlarmDbAdapter(this);
        alarmDbAdapter.open();
        alarmDbAdapter.close();
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


        mButton = findViewById(R.id.i_took_a_pill_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothGatt.disconnect();
                finish();

            }
        });
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
/*
    private static final int MSG_1 = 105;
    private static final int MSG_2 = 106;
    private static final int MSG_3 = 107;
    private static final int MSG_4 = 108;
    //  private static final int MSG_PROGRESS = 201;
    // private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;

   private static class MyHandler extends Handler
    {
        private final WeakReference<PillConfirmationActivity> mActivity;
        public MyHandler( PillConfirmationActivity activity){
            mActivity = new WeakReference<PillConfirmationActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PillConfirmationActivity activity = mActivity.get();
            if (activity!=null)
            {

                BluetoothGattCharacteristic characteristic;
                switch (msg.what) {
                    case MSG_1:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udało się zapalić diody 1");
                            return;
                        }

                        activity.lightUpDiode(1);
                        break;
                    case MSG_2:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udało się zapalić diody 2");
                            return;
                        }
                        activity.lightUpDiode(2);
                        break;
                    case MSG_3:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udało się zapalić diody 3");
                            return;
                        }
                        activity.lightUpDiode(3);
                        break;
                    case MSG_4:
                        characteristic = (BluetoothGattCharacteristic) msg.obj;
                        if (characteristic.getValue() == null) {
                            Log.w(TAG, "Nie udało się zapalić diody 4");
                            return;
                        }
                       activity.lightUpDiode(4);
                        break;

                }
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    */

    public void lightUpDiode(int id) {

        int oneInHex = 49;

        if (id == 1) {

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Vibrator v;
            v=(Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(3000);

            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC5_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);
            characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                    .getCharacteristic(CHARACTERISTIC1_UUID);
            deleteValueFromPrzegrodka(characteristic);

        } else if (id == 2) {
            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC6_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);
            characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                    .getCharacteristic(CHARACTERISTIC2_UUID);
            deleteValueFromPrzegrodka(characteristic);

        } else if (id == 3) {
            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC7_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);
            characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                    .getCharacteristic(CHARACTERISTIC3_UUID);
            deleteValueFromPrzegrodka(characteristic);
        } else if (id == 4) {
            BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE2_UUID)
                    .getCharacteristic(CHARACTERISTIC8_UUID);
            byte[] bytes = ByteBuffer.allocate(4).putInt(oneInHex).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);
            characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                    .getCharacteristic(CHARACTERISTIC4_UUID);
            deleteValueFromPrzegrodka(characteristic);
        }

    }

    private void deleteValueFromPrzegrodka(BluetoothGattCharacteristic characteristic) {
        byte[] dataInput1 = characteristic.getValue();
        int przegrodka1 = toInt(dataInput1);
        przegrodka1 = przegrodka1 - 1;
        if (przegrodka1 > 0) {
            przegrodka1 = przegrodka1 + 48; //zeby zamienic na hex
            byte[] bytes = ByteBuffer.allocate(4).putInt(przegrodka1).array();
            byte[] postBytes = {bytes[3]};
            characteristic.setValue(postBytes);
            mBluetoothGatt.writeCharacteristic(characteristic);
        } else {
            Toast.makeText(this, "Nie można mieć mniej niż 0 tabletek", Toast.LENGTH_SHORT).show();
        }

    }

    private String bytesToString(byte[] b) {
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Log.d(TAG, "NIE PYKŁO");
        }
        return null;
    }

    private int toInt(byte[] b1) {
        String s1 = bytesToString(b1);
        Log.d(TAG, s1);
        int y = Integer.parseInt(s1);
        return y;
    }

}
