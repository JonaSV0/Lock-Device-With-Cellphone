package com.example.electronic_lock_face;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentAddBluetooth extends Fragment {

    private String PHAT_SERVER_PY = "http://192.168.0.31:7008";

    String id_lock, id_user, dni_local;
    ArrayList<String> list_nick = new ArrayList<String>();
    ArrayList<String> list_names = new ArrayList<String>();
    ArrayList<String> list_macs = new ArrayList<String>();

    ArrayAdapter<String> setListAdapter;

    BroadcastReceiver mBroadcastReceiver;
    BluetoothAdapter bluetoothAdapter;
    ListView listView_devices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences2 = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
        PHAT_SERVER_PY = preferences2.getString("ip08", "0");

        SharedPreferences preferences1 = getContext().getSharedPreferences("lock",getContext().MODE_PRIVATE);
        id_lock = preferences1.getString("id_lock", "0");

        SharedPreferences preferences = getContext().getSharedPreferences("datos",getContext().MODE_PRIVATE);
        dni_local = preferences.getString("dni", "0");
        id_user = String.valueOf(preferences.getInt("id",0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_bluetooth, container, false);
        listView_devices = view.findViewById(R.id.list_devices_ble);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(getContext(), "BLUETOOTH NOT SUPPORT", Toast.LENGTH_SHORT).show();
        }  else{
            if (!bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_ADMIN)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 4);
        }
        bluetoothAdapter.startDiscovery();
        ArrayList<String> arrayList = new ArrayList<>();
        list_names = new ArrayList<>();
        list_macs = new ArrayList<>();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    System.out.println("Bluetooth device: "+device.getName());
                    arrayList.add(device.getName() + "\n" + device.getAddress());
                    list_names.add(device.getName());
                    list_macs.add(device.getAddress());
                }

                if (arrayList.size()!=0){
                    ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(getContext().getApplicationContext(), R.layout.my_list_item_bluetooth_scan, R.id.text_nickname_ble, arrayList);
                    listView_devices.setAdapter(itemAdapter);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);

        listView_devices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), arrayList.get(i).toString(), Toast.LENGTH_SHORT).show();
                if (!list_names.get(i).toString().equals(null)){
                    add_device_ble(list_names.get(i), list_macs.get(i));
                }else{
                    Toast.makeText(getContext(), "Error: Se le recomienda usar como llave un bluetooth con nombre definido", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }


    public void add_device_ble(String name, String mac){
        String URL = PHAT_SERVER_PY + "/insert_device_ble";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                if (response.equals("True")){
                    Handler handler = new Handler();
                    handler.postDelayed(() -> getActivity().onBackPressed(), 500);
                }else{
                    Toast.makeText(getContext(), "Error, verifique la funcion de servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id_user", id_user);
                parametros.put("id_lock", id_lock);
                parametros.put("name_ble", name);
                parametros.put("mac_ble", mac);
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}