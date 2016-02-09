package com.testeando.test.nuevaapp;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;


public class clasesita extends AppCompatActivity {

    public BluetoothAdapter adaptador_bluetooth = BluetoothAdapter.getDefaultAdapter();
    private ToggleButton tgbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.segunda);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("  Conectividad Bluetooth");

        //final Context contexto = this;
        final dispos_blue calando = new dispos_blue();

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adaptador_bluetooth.isEnabled() && adaptador_bluetooth != null && calando.btSocket == null)
                    startActivity(new Intent(clasesita.this, dispos_blue.class));

                if (!adaptador_bluetooth.isEnabled()) tostada("Activa bluetooth.");
                else if (adaptador_bluetooth == null) tostada("Bluetooth no compatible.");
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adaptador_bluetooth.isEnabled()) {
                    Intent prendete = new Intent(adaptador_bluetooth.ACTION_REQUEST_ENABLE);
                    startActivityForResult(prendete, 1);
                } else tostada("Bluetooth está activado.");
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adaptador_bluetooth.isEnabled()) {
                    adaptador_bluetooth.disable();
                    tostada("Bluetooth desactivado");
                } else tostada("Bluetooth está desactivado");
            }
        });

        tgbutton = (ToggleButton) findViewById(R.id.toggleButton1);

        tgbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (tgbutton.isChecked()) {

                    tostada("Se prende");
                } else {

                    tostada("Se apaga");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void tostada(String mensaje){
        Context context = getApplicationContext();
        CharSequence text = mensaje;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                tostada("Bluetooth listo!");
            } else { // RESULT_CANCELED as user refuse or failed
                tostada("Bluetooth no activado");
            }
        }

    }

}







