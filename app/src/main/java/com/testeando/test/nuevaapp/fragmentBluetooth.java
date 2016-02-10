package com.testeando.test.nuevaapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by publica on 09/02/2016.
 */
public class fragmentBluetooth extends Fragment {

    private ProgressDialog progress;
    private ToggleButton tgbutton;

    private ArrayAdapter<String> btAdaptadores;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subTitulo4);
        View view = inflater.inflate(R.layout.bluetooth_fragment, container, false);

        final MainActivity paraDatos = new MainActivity();

        view.findViewById(R.id.escanea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paraDatos.adaptador_bluetooth.isEnabled() && paraDatos.adaptador_bluetooth != null){
                    paraDatos.fragment = new fragmentAnalog();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, paraDatos.fragment).addToBackStack(null).commit();
                }

                if (!paraDatos.adaptador_bluetooth.isEnabled()) tostada(R.string.blueActiva);
                else if (paraDatos.adaptador_bluetooth == null) tostada(R.string.blueNoComp);
            }
        });

       view.findViewById(R.id.bluetoothOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paraDatos.adaptador_bluetooth.isEnabled()) {
                    Intent prendete = new Intent(paraDatos.adaptador_bluetooth.ACTION_REQUEST_ENABLE);
                    startActivityForResult(prendete, 1);
                } else tostada(R.string.blueEsActi);
            }
        });

        view.findViewById(R.id.bluetoothOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paraDatos.adaptador_bluetooth.isEnabled()) {
                    paraDatos.adaptador_bluetooth.disable();
                    tostada(R.string.blueDesact);
                } else tostada(R.string.blueEsDesa);
            }
        });

        tgbutton = (ToggleButton) view.findViewById(R.id.botonToggle);

        tgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (tgbutton.isChecked()) {

                    tostada(R.string.turnOn);
                } else {

                    tostada(R.string.turnOf);
                }
            }
        });
        return view;
    }

    public void tostada(int mss){
        Toast.makeText(getActivity(), mss, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) tostada(R.string.blueTostSi);
            else tostada(R.string.blueTostNo);
        }
    }






    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //progress = ProgressDialog.show(MainActivity.this, "Buscando...", "Espera porfavor!");    //show a progress dialog
                progress = ProgressDialog.show(context,"Buscando...", "Porfavor espera mientras busco dispositivos.");
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progress.dismiss();
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getName() != null) {
                    btAdaptadores.add(device.getName() + "\n" + device.getAddress());
                    btAdaptadores.notifyDataSetChanged();
                }
            }
        }};

}
