package com.example.electronic_lock_face;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.List;

public class AdapterBluetooth extends ArrayAdapter<Bluetooth> {
    Context context;
    List<Bluetooth> arraylistbluetooth;

    public AdapterBluetooth(@NonNull Context context, @NonNull List<Bluetooth> arraylistbluetooth) {
        super(context, R.layout.my_list_item_bluetooth, arraylistbluetooth);
        this.context = context;
        this.arraylistbluetooth= arraylistbluetooth;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convetView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_list_item_bluetooth, null, true);
        TextView name_bluetooth = view.findViewById(R.id.text_nickname_bluetooth);
        TextView hash_bluetooth = view.findViewById(R.id.text_hashcode_bluetooth);

        final String text_name = arraylistbluetooth.get(position).getName();
        final String text_hash = arraylistbluetooth.get(position).getHash();

        name_bluetooth.setText(text_name);
        hash_bluetooth.setText(text_hash);
        return view;
    }
}
