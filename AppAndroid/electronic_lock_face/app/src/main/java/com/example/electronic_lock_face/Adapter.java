package com.example.electronic_lock_face;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends ArrayAdapter<Lock> {
    Context context;
    List<Lock>arraylistlock;
    Integer i;

    public Adapter(@NonNull Context context, List<Lock> arraylistlock, Integer i){
        super(context, R.layout.my_list_item, arraylistlock);
        this.context = context;
        this.arraylistlock = arraylistlock;
        this.i = i;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convetView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_list_item,null,true);

        LinearLayout layout_item = view.findViewById(R.id.layout_item);
        TextView nickname = view.findViewById(R.id.text_nickname);
        TextView status = view.findViewById(R.id.text_status);
        CircleImageView image = view.findViewById(R.id.image_state);

        final String id = arraylistlock.get(position).getId();
        final String nick_t = arraylistlock.get(position).getNickname();
        final String hash_t = arraylistlock.get(position).getUnicode();
        final String sta_t = arraylistlock.get(position).getStat();
        final String sta_lock_t = arraylistlock.get(position).getStat_lock();

        nickname.setText(nick_t);

        if (sta_t.equals("0")){
            status.setTextColor(ContextCompat.getColor(context, R.color.red));
            status.setText("Estado: inactivo");
        }else {
            status.setTextColor(ContextCompat.getColor(context, R.color.green));
            status.setText("Estado: activo");
        }

        if (sta_lock_t.equals("0")){
            image.setImageResource(R.drawable.icon_lock_close1);
        }else {
            image.setImageResource(R.drawable.icon_lock_open1);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getContext().getSharedPreferences("lock",getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id_lock",id);
                editor.putString("nick_lock",nick_t);
                editor.commit();

                Fragment fragment = new FragmentFaceRecognition();
                FragmentManager fragmentManager =  ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.escenario1,fragment).addToBackStack(null).commit();
                //Toast.makeText(context, id+" Aqui", Toast.LENGTH_SHORT).show();
            }
        });

        layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getContext().getSharedPreferences("lock",getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id_lock",id);
                editor.putString("nick_lock",nick_t);
                editor.putString("hash_lock",hash_t);
                editor.putString("state", sta_t);

                editor.commit();
                Fragment fragment = new FragmentLockDetails();
                FragmentManager fragmentManager =  ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.escenario1,fragment).addToBackStack(null).commit();
            }
        });


        return view;

    }
}
