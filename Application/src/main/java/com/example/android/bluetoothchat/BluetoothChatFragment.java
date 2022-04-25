package com.example.android.bluetoothchat;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.android.common.logger.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private TextView textViewReceived;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button buttonON, buttonOFF;
    private String receiveBuffer = "";
    Button bProgrammation, bParametres, bTest, bFlash, bValider;
    TextView info, textView2, length, width, amplitude, way;
    Switch Sallumer, Scapteur;
    ImageView retour;
    Spinner Sseuil, artworkAmplitude, flashWidth;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;


    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    public ProgrammationMode mProgrammation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        FragmentActivity activity = getActivity();
        if (mBluetoothAdapter == null && activity != null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBluetoothAdapter == null) {
            return;
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        if (mChatService != null) {
            sendMessage("10000111"); //sendMessage de fin brute, ici la fermeture de l'application
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        textViewReceived = view.findViewById(R.id.textViewReceived);

        bProgrammation = view.findViewById(R.id.programmation);
        bParametres = view.findViewById(R.id.parametres);
        bTest = view.findViewById(R.id.test);
        bFlash = view.findViewById(R.id.flash);
        bValider = view.findViewById(R.id.valider);


        info = (TextView) view.findViewById(R.id.info);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        length = (TextView) view.findViewById(R.id.lengthText);
        amplitude = (TextView) view.findViewById(R.id.amplitudeText);
        width = (TextView) view.findViewById(R.id.widthText);
        way = (TextView) view.findViewById(R.id.way);

        artworkAmplitude = (Spinner) view.findViewById(R.id.artworkAmplitude);
        Sseuil = (Spinner) view.findViewById(R.id.seuil);
        flashWidth = (Spinner) view.findViewById(R.id.flashWidth);

        Sallumer = (Switch) view.findViewById(R.id.allumer);
        Scapteur = (Switch) view.findViewById(R.id.capteur);

        retour = (ImageView) view.findViewById(R.id.returnn);

        String[] tab={"1","2","3","4","5","6","7","8"};
        String[] tab1={"6","7","8","9","10","11","12","13"};
        String[] tab2={"20","30","40","50","60","70","80","90"};

        ArrayAdapter seuil = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_dropdown_item, tab);
        Sseuil.setAdapter(seuil);

        ArrayAdapter amp = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_dropdown_item, tab2);
        artworkAmplitude.setAdapter(amp);

        ArrayAdapter flash = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_dropdown_item, tab1);
        flashWidth.setAdapter(flash);

        bProgrammation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mProgrammation = new ProgrammationMode( bProgrammation,  bParametres,  bTest,  bFlash,  bValider,
                             Sseuil,  artworkAmplitude,  flashWidth,  info,  textView2,
                             length,  width,  amplitude,  way,
                             Sallumer,  Scapteur,
                             retour);

                }

        });

        bFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Flash
                sendMessage("10101000");
                System.out.println("flash");

            }
        });

        // détecte si le switch "VALIDER" est coche ou non
        Sallumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Sallumer.isChecked()) {
                    bFlash.setClickable(false);
                    Scapteur.setClickable(false);
                    sendMessage("10110110");
                    System.out.println("allumer");
                }
                else if (!Sallumer.isChecked()) {
                    bFlash.setClickable(true);
                    Scapteur.setClickable(true);
                    sendMessage("10110000");
                    System.out.println("eteindre");
                }
            }
        });

        Scapteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Scapteur.isChecked()) {
                    bFlash.setClickable(false);
                    Sallumer.setClickable(false);
                    sendMessage("10100111");
                    System.out.println("test capteur");
                }
                else if (!Scapteur.isChecked()) {
                    bFlash.setClickable(true);
                    Sallumer.setClickable(true);
                    sendMessage("10100000");
                    System.out.println("fin test capteur");
                }
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numstate=mProgrammation.numstate;
                String state = mProgrammation.getState();
                String[] tab_state = mProgrammation.tab_state;
                if (numstate>1) {
                    mProgrammation.numstate--;
                    state = tab_state[mProgrammation.numstate];
                    mProgrammation.setState(state);
                    mProgrammation.setScreen(state);
                }
                else {
                    mProgrammation.setConnect();
                    System.out.println("fin test/parametres");
                    sendMessage("10000111"); // retour dans le mode connect, fin de programmation
                }

            }
        });


    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Initialize the send button with a listener that for click events
        bValider.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    String s1= flashWidth.getSelectedItem().toString();
                    s1 = transformeFlashWidth(s1);
                    System.out.println("width flash : "+s1);

                    sendMessage(s1);// sendMessage du largeur du flash

                    String s2= artworkAmplitude.getSelectedItem().toString();
                    System.out.println("amplitude selectionnée :"+s2);
                    s2 = transformeAmplitude(s2);
                    System.out.println("amplitude : "+s2);

                    sendMessage(s2);// sendMessage du duty cycle

                    String s3= Sseuil.getSelectedItem().toString();
                    System.out.println("seuil distance selectionnée :"+s3);
                    s3 = transformeSeuil(s3);
                    System.out.println("seuil : "+s3);

                    sendMessage(s3);// sendMessage du duty cycle

                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(activity, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer();
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            System.out.println("send fonction");

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
                System.out.println("send message");
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private void messageHandler()
    {
        if (receiveBuffer != null) {
            textViewReceived.setText("Received: " + receiveBuffer);
        }
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            textViewReceived.setText("Received:");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    receiveBuffer += readMessage;
                    if(receiveBuffer.contains("\n")) {
                        receiveBuffer = receiveBuffer.substring(0, receiveBuffer.length() - 1);
                        messageHandler();
                        receiveBuffer = "";
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        Toast.makeText(activity, R.string.bt_not_enabled_leaving,
                                Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    public String transformeFlashWidth(String width){
        int dutyCycle = Integer.parseInt(width);
        String biStr = Integer.toBinaryString(dutyCycle-6);
        while(biStr.length()<3){
            biStr="0".concat(biStr);
        }

        width="10001".concat(biStr);
        return width;
    }


    public String transformeAmplitude(String s2) {
        int amp = Integer.parseInt(s2);
        System.out.println(amp/10-2);
        String biStr = Integer.toBinaryString(amp/10-2);
        while(biStr.length()<3){
            biStr="0".concat(biStr);
        }

        s2="10010".concat(biStr);
        return s2;
    }

    public String transformeSeuil(String s3){
        int seuil = Integer.parseInt(s3);
        System.out.println(seuil-1);
        String biStr = Integer.toBinaryString(seuil-1);
        while(biStr.length()<3){
            biStr="0".concat(biStr);
        }

        s3="10011".concat(biStr);
        return s3;
    }


}
