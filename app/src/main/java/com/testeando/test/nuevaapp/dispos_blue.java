package com.testeando.test.nuevaapp;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class dispos_blue extends AppCompatActivity {

    private BluetoothAdapter adaptador_bluetooth = BluetoothAdapter.getDefaultAdapter();
    private ListView listaEncontrados;
    private ArrayAdapter<String> btAdaptadores;

    public String MAC;

    private boolean hayPaired = false;
    private ArrayList<BluetoothDevice> pairedDeviceArrayList;

    public BluetoothSocket btSocket;

    private ProgressDialog progress;
    private EditText textoSend;


    //private TextView respText;

    //private Context contextoDis = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispositivos);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("  Dispositivos Bluetooth");
        Log.d("WHERE: ", "INICIAMOS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        textoSend = (EditText) findViewById(R.id.textoSend);
        //respText = (TextView) findViewById(R.id.respText);

        listaEncontrados = (ListView) findViewById(R.id.listaDispos);
        btAdaptadores = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listaEncontrados.setAdapter(btAdaptadores);

        btAdaptadores.clear();

        if(btSocket == null) {
            Log.d("WHERE: ", "btSocket es null!");
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(ActionFoundReceiver, filter);

            adaptador_bluetooth.startDiscovery();
        }
        else Log.d("WHERE: ", "btSocket no es null!");

        ////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////   BUSCAR POR DISPOSITIVOS YA SINCRONIZADOS   /////////////////////////
        Set<BluetoothDevice> paired_devices = adaptador_bluetooth.getBondedDevices();
        if(paired_devices.size() > 0) {
            hayPaired = true;
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : paired_devices) {
                pairedDeviceArrayList.add(device);
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        listaEncontrados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean conecta = false;

                String itemValue = (String) listaEncontrados.getItemAtPosition(position);
                MAC = itemValue.substring(itemValue.length() - 17);
                BluetoothDevice bluetoothDevice = adaptador_bluetooth.getRemoteDevice(MAC);

                if (hayPaired == true) {
                    for (int cPaired = 0; cPaired < pairedDeviceArrayList.size(); cPaired++) {
                        if (("" + bluetoothDevice).equals("" + pairedDeviceArrayList.get(cPaired))) {
                            tostada_blue("" + bluetoothDevice + "\n" + "" + pairedDeviceArrayList.get(cPaired) + "\n" + cPaired);
                            conecta = true;
                            break;
                        }
                    }
                }

                if (conecta != true) tostada_blue("Se necesita emparejar dispositivo.");
                new ConnectBT().execute();
            }
        });

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btSocket != null){
                    ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();

                    byte[] data = {};
                    if(textoSend.length() != 0){
                        String fuas =  textoSend.getText().toString();
                        data = new String(fuas + "\n").getBytes();
                    }

                    String fua = "Hola que tal xD\n";
                    byte[] fuaChar = new String(fua).getBytes();

                    ConnectedThread esc = new ConnectedThread(btSocket);
                    esc.write(data);
                }
                else{
                    tostada_blue("No hay conección.");
                }
            }
        });
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            progress = ProgressDialog.show(dispos_blue.this, "Buscando...", "Espera porfavor!");    //show a progress dialog
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);                      //Bloquear la pantalla mientras busca
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            progress.dismiss();
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);                      //Desbloquear la pantalla ya que buscó
        }
        else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if(device.getName() != null) {
                btAdaptadores.add(device.getName() + "\n" + device.getAddress());
                btAdaptadores.notifyDataSetChanged();
            }
        }
    }};

    static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = msg.arg1;
            int end = msg.arg2;
            switch(msg.what) {
                case 1
                        :
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;
            }
        }
    };

    public class ConnectedThread extends Thread {
        //private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            btSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length
                            -
                            bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(
                                    1
                                    , begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes
                                    -
                                    1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch
                    (IOException e) { }
        }
        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        //BluetoothSocket btSocket = null;
        BluetoothAdapter myBluetooth = null;
        private boolean isBtConnected = false;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(dispos_blue.this, "Conectando...", "Espera porfavor!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice disBluetooth = myBluetooth.getRemoteDevice(MAC);//connects to the device's address and checks if it's available
                    btSocket = disBluetooth.createInsecureRfcommSocketToServiceRecord(uuid); //create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)  //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                tostada_blue("Conección falida.");
                finish();
            } else {
                tostada_blue("Conectado.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("WHERE: ", "ON RESUME");
        if(btSocket != null) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            this.registerReceiver(ActionFoundReceiver, filter);
        }

    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d("WHERE: ", "ON STOP");
        //contextoDis = this.btSocket;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("WHERE: ", "ON DESTROY");
        this.unregisterReceiver(ActionFoundReceiver);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("WHERE: ", "ON RESTART");
        if(btSocket == null) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            this.registerReceiver(ActionFoundReceiver, filter);
        }
    }

    private void tostada_blue(String mensaje){
        Context context = getApplicationContext();
        CharSequence text = mensaje;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}

