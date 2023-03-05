package com.example.electronic_lock_face;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentHistoryLock extends Fragment {
    String id_lock, nick_lock, dni_local, id_user;
    private String PHAT_SERVER_PY = "http://192.168.0.31:7008/get_history_for_lock";
    private ListView list;
    private AdapterHistory adapterHistory;
    private static ArrayList<History> historys = new ArrayList<>();
    private History history;

    private TextView textView_name_lock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences2 = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
        PHAT_SERVER_PY = preferences2.getString("ip08", "0") + "/get_history_for_lock";

        SharedPreferences preferences1 = getContext().getSharedPreferences("lock",getContext().MODE_PRIVATE);
        id_lock = preferences1.getString("id_lock", "0");
        nick_lock = preferences1.getString("nick_lock", "None");

        SharedPreferences preferences = getContext().getSharedPreferences("datos",getContext().MODE_PRIVATE);
        dni_local = preferences.getString("dni", "0");
        id_user = String.valueOf(preferences.getInt("id",0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_lock, container, false);
        adapterHistory = new AdapterHistory(getContext(), historys);
        textView_name_lock = view.findViewById(R.id.textView_name_lock_history);

        list = view.findViewById(R.id.listview_history_locks);
        list.setAdapter(adapterHistory);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textView_name_lock.setText(nick_lock);
        mostrar_datos(PHAT_SERVER_PY);
    }

    private void mostrar_datos(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    historys.clear();

                    Toast.makeText(getContext(), "AQUIIII", Toast.LENGTH_SHORT).show();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject object=jsonArray.getJSONObject(i);
                        String id_user_txt = object.getString("id_user");
                        String id_lock_txt = object.getString("id_lock");
                        String type_txt = object.getString("type_in");
                        String name_ble_txt = object.getString("name_bluet");
                        String name_in_txt = object.getString("name_in");
                        String dni_in_txt = object.getString("dni_in");
                        String date_in_txt = object.getString("date_in");
                        String time_in_txt = object.getString("time_in");

                        history = new History();
                        history.setId_user(id_user_txt);
                        history.setId_lock(id_lock_txt);
                        history.setType(type_txt);
                        history.setName_bluetooth(name_ble_txt);
                        history.setName_user(name_in_txt);
                        history.setDni_user(dni_in_txt);
                        history.setDate_in(date_in_txt);
                        history.setTime_in(time_in_txt);

                        historys.add(history);
                        adapterHistory.notifyDataSetChanged();

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
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}