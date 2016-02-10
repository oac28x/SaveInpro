package com.testeando.test.nuevaapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    public BluetoothAdapter adaptador_bluetooth = BluetoothAdapter.getDefaultAdapter();
    public BluetoothSocket btSocket;

    public Fragment fragment;
    public Context context = this;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > 14){
            try{
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }catch(Exception e){
                try{
                    View decorView = getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }catch(Exception hu){ //hu no se hace nada
                }
            }
        }
        setContentView(R.layout.activity_main);
        tituloIcon("SaveIN", R.string.beWelcome);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adaptador_bluetooth == null) {
                    noExisteBluetooth();
                }
                startActivity(new Intent(MainActivity.this, clasesita.class));
            }
        });

        if (!adaptador_bluetooth.isEnabled()) {
            Intent prendete = new Intent(adaptador_bluetooth.ACTION_REQUEST_ENABLE);
            startActivityForResult(prendete, 1);
        }
        else if(adaptador_bluetooth == null) noExisteBluetooth();
    }

    protected void noExisteBluetooth(){
        new AlertDialog.Builder(context).setTitle(R.string.blueNo).setMessage(R.string.blueNoEx)
                .setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No hace nada
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).setIcon(R.drawable.warning).show();
    }

    public void tituloIcon(String dato, int datito){
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.rayo_p);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(dato);
        getSupportActionBar().setSubtitle(datito);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.bluetooth_set:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.bluetooth:
                fragment = new fragmentBluetooth();
                replaceFragment();
                break;
            case R.id.menu_sleep:
                startActivity(new Intent(Settings.ACTION_ADD_ACCOUNT));  //ACTION_SOUND_SETTINGS
                break;
            case R.id.reloj_analogo:
                fragment = new fragmentAnalog();
                replaceFragment();
                break;
            case R.id.reloj_digital:
                fragment = new fragmentDigital();
                replaceFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) tituloIcon("SaveIN", R.string.beWelcome);
        }
        else
        {
            new AlertDialog.Builder(context).setTitle(R.string.salirdeApp).setMessage(R.string.compSalir)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }
            ).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    public void replaceFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
