package com.example.electronic_lock_face;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentLockDetails extends Fragment {
    private String PHAT_SERVER_PY = "http://192.168.0.31:7008/get_device_for_lock_json";
    private ListView list;
    private AdapterBluetooth adapterBluetooth;
    private static ArrayList<Bluetooth> bluetooths = new ArrayList<>();
    private Bluetooth bluetooth;

    TextView textView_name, textView_hash, textView_state;

    String id_lock, nick_lock, hash_lock, state_lock, id_user;
    ImageView imageView_history;
    FloatingActionButton floatingActionButton_addBle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences2 = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
        PHAT_SERVER_PY = preferences2.getString("ip08", "0") + "/get_device_for_lock_json";

        SharedPreferences preferences = getContext().getSharedPreferences("datos", getContext().MODE_PRIVATE);
        id_user = String.valueOf(preferences.getInt("id",0));

        SharedPreferences preferences1 = getContext().getSharedPreferences("lock",getContext().MODE_PRIVATE);
        id_lock = preferences1.getString("id_lock", "0");
        nick_lock = preferences1.getString("nick_lock", "None");
        hash_lock = preferences1.getString("hash_lock","None");
        state_lock = preferences1.getString("state", "None");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lock_details, container, false);
        adapterBluetooth = new AdapterBluetooth(getContext(), bluetooths);
        list = view.findViewById(R.id.listview_devices_bluetooth);

        textView_name = view.findViewById(R.id.textView_Name);
        textView_hash = view.findViewById(R.id.textView_HashCode);
        textView_state = view.findViewById(R.id.textView_State);
        imageView_history = view.findViewById(R.id.imageView_history);
        floatingActionButton_addBle = view.findViewById(R.id.floatingActionButton_addBle);
        floatingActionButton_addBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentAddBluetooth();
                FragmentManager fragmentManager =  ((AppCompatActivity)getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.escenario1,fragment).addToBackStack(null).commit();
            }
        });

        imageView_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentHistoryLock();
                FragmentManager fragmentManager =  ((AppCompatActivity)getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.escenario1,fragment).addToBackStack(null).commit();
            }
        });

        list.setAdapter(adapterBluetooth);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textView_name.setText(nick_lock);
        textView_hash.setText("HashCode: "+ hash_lock);

        if (state_lock.equals("0")){
            textView_state.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            textView_state.setText("Estado: inactivo");
        }else {
            textView_state.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            textView_state.setText("Estado: activo");
        }
        mostrar_datos(PHAT_SERVER_PY);
    }

    private void mostrar_datos(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    bluetooths.clear();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject object=jsonArray.getJSONObject(i);
                        String id_b = object.getString("id");
                        String name_b = object.getString("name_b");
                        String hash_b = object.getString("mac_b");

                        bluetooth = new Bluetooth();
                        bluetooth.setId(id_b);
                        bluetooth.setName(name_b);
                        bluetooth.setHash(hash_b);

                        bluetooths.add(bluetooth);
                        adapterBluetooth.notifyDataSetChanged();

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error"+error, Toast.LENGTH_SHORT).show();
                System.out.println(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id_lock", id_lock);
                parametros.put("id_user", id_user);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }
}