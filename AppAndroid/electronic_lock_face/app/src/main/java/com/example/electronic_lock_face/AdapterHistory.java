package com.example.electronic_lock_face;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHistory extends ArrayAdapter<History>{

    Context context;
    List<History> arraylisthistory;

    public AdapterHistory(@NonNull Context context, List<History> arraylisthistory) {
        super(context, R.layout.my_list_item_history, arraylisthistory);
        this.context = context;
        this.arraylisthistory = arraylisthistory;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convetView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_list_item_history, null, true);

        TextView Name_user = view.findViewById(R.id.text_name_history);
        TextView Dni_user = view.findViewById(R.id.text_dni_history);
        TextView Date_time = view.findViewById(R.id.text_date_time_history);
        CircleImageView Image_stat= view.findViewById(R.id.image_state_history);

        final String name_us = arraylisthistory.get(position).getName_user();
        final String dni_us = arraylisthistory.get(position).getDni_user();
        final String date_time = arraylisthistory.get(position).getDate_in() + " - " + arraylisthistory.get(position).getTime_in();
        final String name_bluetooth = arraylisthistory.get(position).getName_bluetooth();
        final String type_rec = arraylisthistory.get(position).getType();

        Name_user.setText(name_us);
        Dni_user.setText(dni_us);
        Date_time.setText(date_time);

        if (type_rec.equals("FaceRecognition")){
            Image_stat.setImageResource(R.drawable.image_rec_facial_history);
        }else if (type_rec.equals("BleDevice")){
            Image_stat.setImageResource(R.drawable.image_bluetooth_history);
        }else {

        }


        return view;
    }
}
