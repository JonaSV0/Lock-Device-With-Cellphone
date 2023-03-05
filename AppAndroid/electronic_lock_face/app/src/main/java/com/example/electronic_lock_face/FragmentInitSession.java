package com.example.electronic_lock_face;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FragmentInitSession extends Fragment {

    private Button button_iniciar_sesion;
    private TextInputLayout cajaValidationCorreo, cajaValidationPass, cajaValidationIp;
    private EditText editTextCorreo, editTextPass, editTextIp;
    private Switch switch_autolog;
    private Boolean state_log;

    private String PHAT_SERVER_PY = "http://192.168.0.31:7008";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences2 = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
        PHAT_SERVER_PY = preferences2.getString("ip08", "0");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_init_session, container, false);

        switch_autolog = view.findViewById(R.id.switchRecordar);

        cajaValidationCorreo = view.findViewById(R.id.edit_text_user);
        cajaValidationPass = view.findViewById(R.id.edit_text_pass);
        cajaValidationIp = view.findViewById(R.id.edit_text_ip);

        editTextCorreo = cajaValidationCorreo.getEditText();
        editTextPass = cajaValidationPass.getEditText();
        editTextIp = cajaValidationIp.getEditText();

        button_iniciar_sesion = view.findViewById(R.id.button_iniciar_session);
        button_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean valor = true;
                if (editTextIp.getText().toString().isEmpty()){
                    editTextIp.setError("Este campo es requerido");
                    valor = false;
                }
                if (editTextCorreo.getText().toString().isEmpty()){
                    editTextCorreo.setError("Este campo es requerido");
                    valor = false;
                }
                if (editTextPass.getText().toString().isEmpty()){
                    editTextPass.setError("Este campo es requerido");
                    valor = false;
                }

                if (valor){
                    String URL08 = "http://" + editTextIp.getText().toString() + ":7008";
                    String URL07 = "http://" + editTextIp.getText().toString() + ":7007";
                    PHAT_SERVER_PY = URL08;
                    SharedPreferences preferences = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ip07",URL07);
                    editor.putString("ip08",URL08);
                    editor.commit();

                    iniciar_session();

                    /*SharedPreferences preferences = getContext().getSharedPreferences("datos",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user",editTextCorreo.getText().toString());
                    editor.putString("pass",editTextPass.getText().toString());
                    editor.putInt("id", s_.getInt("id"));
                    editor.putBoolean("auto_log",switch_autolog.isChecked());
                    editor.commit();

                    Intent intent= new Intent(getContext(), MainActivityPrincipal.class);
                    startActivity(intent);*/
                }
            }
        });

        return view;
    }

    private void iniciar_session() {

        //final String id = preferences.getString("id", "Datos invalidos");

        String URL = PHAT_SERVER_PY + "/session";
        final int[] ret = {0};
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                if (response.equals("{}")){
                    Toast.makeText(getContext(), "Usuario/Contraseña incorrecto", Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(response);

                        SharedPreferences preferences = getContext().getSharedPreferences("datos",getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user",editTextCorreo.getText().toString());
                        editor.putString("pass",editTextPass.getText().toString());
                        editor.putInt("id", jsonObject.getInt("id"));
                        editor.putString("dni", jsonObject.getString("dni"));
                        editor.putString("name", jsonObject.getString("name"));
                        editor.putString("surname", jsonObject.getString("surname"));
                        editor.putBoolean("auto_log",switch_autolog.isChecked());
                        editor.commit();

                        Intent intent= new Intent(getContext(), MainActivityPrincipal.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("user", editTextCorreo.getText().toString());
                parametros.put("pass", editTextPass.getText().toString());
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}