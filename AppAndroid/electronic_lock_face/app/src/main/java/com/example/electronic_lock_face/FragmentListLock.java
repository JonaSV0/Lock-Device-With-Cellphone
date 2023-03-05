package com.example.electronic_lock_face;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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


public class FragmentListLock extends Fragment {
    private String PHAT_SERVER_PY = "http://192.168.0.31:7008/locks_user";
    private ListView list;
    private Adapter adapter;
    private static ArrayList<Lock> locks = new ArrayList<>();
    private Lock lock;

    private String id;
    private  String name;

    private Boolean stat_get;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences2 = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
        PHAT_SERVER_PY = preferences2.getString("ip08", "0") + "/locks_user";

        SharedPreferences preferences = getContext().getSharedPreferences("datos", getContext().MODE_PRIVATE);
        id = String.valueOf(preferences.getInt("id",0));
        name = preferences.getString("name","-") + " " + preferences.getString("surname","-");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_lock, container, false);
        adapter = new Adapter(getContext(), locks, 2);

        list = view.findViewById(R.id.listview_locks);
        list.setAdapter(adapter);
        mostrar_datos(PHAT_SERVER_PY );
        return view;
    }

    private void mostrar_datos(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    locks.clear();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject object=jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        String nickname = object.getString("nickname");
                        String unicode = object.getString("unicode");
                        String stat = object.getString("stat");
                        String stat_lock = object.getString("stat_lock");
                        lock = new Lock();
                        lock.setId(id);
                        lock.setNickname(nickname);
                        lock.setUnicode(unicode);
                        lock.setStat(stat);
                        lock.setStat_lock(stat_lock);

                        locks.add(lock);
                        adapter.notifyDataSetChanged();

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
                parametros.put("id", id);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        stat_get = true;
        actualizar_data();
    }

    @Override
    public void onPause() {
        super.onPause();
        stat_get = false;
    }

    private void actualizar_data(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PHAT_SERVER_PY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (stat_get){
                    Handler handler = new Handler();
                    handler.postDelayed(() ->actualizar_data(), 1000);
                }
                try {
                    locks.clear();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject object=jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        String nickname = object.getString("nickname");
                        String unicode = object.getString("unicode");
                        String stat = object.getString("stat");
                        String stat_lock = object.getString("stat_lock");
                        lock = new Lock();
                        lock.setId(id);
                        lock.setNickname(nickname);
                        lock.setUnicode(unicode);
                        lock.setStat(stat);
                        lock.setStat_lock(stat_lock);

                        locks.add(lock);
                        adapter.notifyDataSetChanged();

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
                parametros.put("id", id);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}