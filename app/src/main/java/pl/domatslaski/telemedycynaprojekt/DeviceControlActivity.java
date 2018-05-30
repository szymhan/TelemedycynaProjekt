package pl.domatslaski.telemedycynaprojekt;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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
    TextView mTextView1;
    TextView mTextView2;
    TextView mTextView3;
    TextView mTextView4;
    Button mAddPills1;
    Button mAddPills2;
    Button mAddPills3;
    Button mAddPills4;
    EditText mAddPillsnumber;
    private static int getAddedPillsNumber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mTextView1 = findViewById(R.id.ilosc_1);
        mTextView2=findViewById(R.id.ilosc_2);
        mTextView3=findViewById(R.id.ilosc_3);
        mTextView4 =findViewById(R.id.ilosc_4);
        mAddPills1 = findViewById(R.id.add_pills1);
        mAddPills2=findViewById(R.id.add_pills2);
        mAddPills3=findViewById(R.id.add_pills3);
        mAddPills4=findViewById(R.id.add_pills4);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);

        mBluetoothGatt = mDevice.connectGatt(this, true, mGattCallback);


        mAddPills1.setOnClickListener(new View.OnClickListener() {
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

                                        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                .getCharacteristic(CHARACTERISTIC1_UUID);
                                        addValueToPrzegrodka(characteristic,getAddedPillsNumber);
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

        mAddPills2.setOnClickListener(new View.OnClickListener() {
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

                                        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                .getCharacteristic(CHARACTERISTIC2_UUID);
                                        addValueToPrzegrodka(characteristic,getAddedPillsNumber);
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

        mAddPills3.setOnClickListener(new View.OnClickListener() {
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

                                        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID)
                                                .getCharacteristic(CHARACTERISTIC3_UUID);
                                        addValueToPrzegrodka(characteristic,getAddedPillsNumber);
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

        mAddPills4.setOnClickListener(new View.OnClickListener() {
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

                                        BluetoothGattCharacteristic characteristic =
                                                mBluetoothGatt.getService(SERVICE_UUID)
                                                .getCharacteristic(CHARACTERISTIC4_UUID);
                                        addValueToPrzegrodka(characteristic,getAddedPillsNumber);
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

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;


        }

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Brak wsparcia BLE.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //mDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothGatt = mDevice.connectGatt(this, true, mGattCallback);


    }
    /*MOJE NUMERY SERWISÓW I CHARAKTERYSTYK*/
    private static final UUID SERVICE_UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    private static final UUID CHARACTERISTIC1_UUID = UUID.fromString( "beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private static final UUID CHARACTERISTIC2_UUID = UUID.fromString( "324cc283-b005-4d4d-983d-4b869f282b47");
    private static final UUID CHARACTERISTIC3_UUID = UUID.fromString( "247dae46-a006-4e82-8ace-a09ec6f0cc01");
    private static final UUID CHARACTERISTIC4_UUID = UUID.fromString( "28439fca-4e20-4d59-9510-19b5c14bc918");
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


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


    private static final int MSG_1 = 101;
    private static final int MSG_2 = 102;
    private static final int MSG_3 = 103;
    private static final int MSG_4 = 104;
    //  private static final int MSG_PROGRESS = 201;
    // private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;
    private  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BluetoothGattCharacteristic characteristic;
            switch (msg.what) {
                case MSG_1:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Nie udalo się pozyskać danych dla 1 przegródki");
                        return;
                    }
                    updatePrzegrodka1(characteristic);
                    break;
                case MSG_2:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Nie udalo się pozyskać danych dla 2 przegródki");
                        return;
                    }
                    updatePrzegrodka2(characteristic);
                    break;
                case MSG_3:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Nie udalo się pozyskać danych dla 3 przegródki");
                        return;
                    }
                    updatePrzegrodka3(characteristic);
                    break;
                case MSG_4:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Nie udalo się pozyskać danych dla 4 przegródki");
                        return;
                    }
                    updatePrzegrodka4(characteristic);
                    break;
                case MSG_CLEAR:
                    clearDisplayValues();
                    break;
            }
        }
    };

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
        int przegrodka1 = toInt(dataInput1);
        a = a+ przegrodka1;
        a=a+48; //zeby zamienic na hex
        byte[] bytes = ByteBuffer.allocate(4).putInt(a).array();
        byte [] postBytes={bytes[3]};
        characteristic.setValue(postBytes);
        mBluetoothGatt.writeCharacteristic(characteristic);
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






}
