package com.testeando.test.nuevaapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by publica on 04/02/2016.
 */
public class fragmentAnalog extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subTitulo2);
        return inflater.inflate(R.layout.primer_fragment, container, false);
    }
}
