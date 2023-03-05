package com.example.electronic_lock_face;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class FragmentInitAplication extends Fragment {

    Boolean state_log = false;
    ImageView log;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_init_aplication, container, false);
        log = view.findViewById(R.id.imageView_log_anim);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        log.startAnimation(animation);
        log.setVisibility(View.VISIBLE);


        Handler handler = new Handler();
        handler.postDelayed(() -> exit_animation(), 2000);
    }

    private void exit_animation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        log.startAnimation(animation);
        log.setVisibility(View.INVISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(() -> exit_animation_2(), 300);

    }

    private void exit_animation_2() {
        SharedPreferences preferences = getContext().getSharedPreferences("datos", getContext().MODE_PRIVATE);
        state_log = preferences.getBoolean("auto_log",false);

        if (state_log){
            Intent intent= new Intent(getContext(), MainActivityPrincipal.class);
            startActivity(intent);
        }else{
            Fragment fragment =new FragmentInitSession();
            FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.escenario,fragment).addToBackStack(null).commit();
        }

    }


}