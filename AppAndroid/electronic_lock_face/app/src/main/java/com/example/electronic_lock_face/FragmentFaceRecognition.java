package com.example.electronic_lock_face;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FragmentFaceRecognition extends Fragment {

    public static final int REQUEST_CODE_PERMISSION = 101;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private TextureView tv;
    private ImageView iv;
    private static final String TAG = "FaceTrackingActivity";
    public static CameraX.LensFacing lens = CameraX.LensFacing.FRONT;

    String PHAT_SERVER_PY = "http://192.168.0.31:7007";
    String PHAT_SERVER_PY1 = "http://192.168.0.31:7008";

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint linePaint;
    private float widthScaleFactor = 1.0f;
    private float heightScaleFactor = 1.0f;
    private FirebaseVisionImage fbImage;

    private int process_id;
    private int number_photos = 0;
    private ArrayList<String> fotos_face = new ArrayList<String>();

    String id_us = "kkkkkkkka";
    int number = 1;

    boolean in_process = false;

    Snackbar mySnackbar;
    Bitmap face_;

    private Handler handler_desconocido;
    private Runnable runnable_desconocido;

    private String dni, dni_local, id_lock, nick_lock, id_emp, id_divition, id_identity, name_user;
    Integer id_user;


    TextView textView_nick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences2 = getContext().getSharedPreferences("ip",getContext().MODE_PRIVATE);
        PHAT_SERVER_PY = preferences2.getString("ip07", "0");
        PHAT_SERVER_PY1 = preferences2.getString("ip08", "0");

        id_emp = "9";
        id_divition = "0";
        id_identity = "4";

        SharedPreferences preferences = getContext().getSharedPreferences("datos",getContext().MODE_PRIVATE);
        dni_local = preferences.getString("dni", "0");
        id_user = preferences.getInt("id",0);
        name_user = preferences.getString("name", "None") + " " + preferences.getString("surname","None");

        SharedPreferences preferences1 = getContext().getSharedPreferences("lock",getContext().MODE_PRIVATE);
        id_lock = preferences1.getString("id_lock", "0");
        nick_lock = preferences1.getString("nick_lock", "None");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_face_recognition, container, false);
        textView_nick = view.findViewById(R.id.textview_lock);
        tv = view.findViewById(R.id.tracking_texture_view);
        iv = view.findViewById(R.id.tracking_image_view);
        mySnackbar = Snackbar.make(view.findViewById(R.id.myCoordinatorLayout), "Desconocido", Snackbar.LENGTH_SHORT);

        if (allPermissionsGranted()) {
            FirebaseApp.initializeApp(getContext());
            tv.post(this::startCameraX);
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
        }
        runnable_desconocido = new Runnable() {
            @Override
            public void run() {
                in_process = false;
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textView_nick.setText(nick_lock);
    }

    private void startCameraX() {
        Toast.makeText(getContext(), (tv.getWidth() + " | " + tv.getHeight()), Toast.LENGTH_SHORT).show();
        CameraX.unbindAll();
        PreviewConfig pc = new PreviewConfig
                .Builder()
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        Preview preview = new Preview(pc);
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup vg = (ViewGroup) tv.getParent();
            vg.removeView(tv);
            vg.addView(tv, 0);
            tv.setSurfaceTexture(output.getSurfaceTexture());
        });

        ImageAnalysisConfig iac = new ImageAnalysisConfig
                .Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(iac);

        imageAnalysis.setAnalyzer(Runnable::run, (image, rotationDegrees) -> {
            if (image == null || image.getImage() == null) {
                return;
            }
            int rotation = degreesToFirebaseRotation(rotationDegrees);
            fbImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);

            initDrawingUtils();
            initDetector();
        });
        CameraX.bindToLifecycle( this, preview, imageAnalysis);
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedimage = Base64.encodeToString(imageByteArray,Base64.DEFAULT);

        return encodedimage;
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy image) {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    private Bitmap bitmap_scale (Bitmap bmh){
        float proporcion = 160 / (float) bmh.getWidth();
        Bitmap miniBitmap = Bitmap.createScaledBitmap(bmh,160,(int) (bmh.getHeight() * proporcion),false);

        return miniBitmap;
    }


    public void send_photo(String id_us, String data_phot, int number){

        String URL = PHAT_SERVER_PY + "/add_photo_validity";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), (response + " Subido"), Toast.LENGTH_SHORT).show();
                proccess_photos_face(id_us);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
                in_process = false;
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id", String.valueOf(id_us));
                parametros.put("data",data_phot);
                parametros.put("number", String.valueOf(number));
                parametros.put("id_emp", id_emp);
                parametros.put("id_divition", id_divition);
                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void proccess_photos_face(String id_us){

        String URL = PHAT_SERVER_PY + "/validity_face";
        final int[] ret = {0};

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.equals("desconocido")){
                    //Toast.makeText(MainActivity.this, "Desconocido", Toast.LENGTH_SHORT).show();
                    mySnackbar.show();
                    handler_desconocido = new Handler();
                    handler_desconocido.postDelayed(runnable_desconocido, 3000);

                }else if (response.equals("norostro")){
                    Toast.makeText(getContext(), "Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                    in_process = false;
                }else {
                    dni = response;
                    if (dni.equals(dni_local)){
                        open_lock();
                        //Continuar
                    }else{
                        Toast.makeText(getContext(), "User Error", Toast.LENGTH_SHORT).show();
                        in_process = false;
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();
                in_process = false;
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id", id_us);
                parametros.put("id_device", "1");
                parametros.put("id_identity", id_identity);
                parametros.put("id_emp", id_emp);
                parametros.put("id_divition", id_divition);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void open_lock(){

        String URL = PHAT_SERVER_PY1 + "/lock_on";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.equals("True")){
                    Toast.makeText(getActivity(), "Desconocido", Toast.LENGTH_SHORT).show();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("!Autenticacion completa!")
                            .show();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> getActivity().onBackPressed(), 1000);


                }else if (response.equals("False")){
                    Toast.makeText(getContext(), "Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                    in_process = false;
                }else {
                    in_process = false;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(), Toast.LENGTH_SHORT).show();
                in_process = false;
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("id_user", id_user.toString());
                parametros.put("id_lock", id_lock);
                parametros.put("type", "FaceRecognition");
                parametros.put("name_bluet", "");
                parametros.put("name_user", name_user);
                parametros.put("dni", dni_local);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    //Clase para Procesar las texturas
    private void initDetector() {

        FirebaseVisionFaceDetectorOptions detectorOptions = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .enableTracking()
                .build();
        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions);
        faceDetector.detectInImage(fbImage).addOnSuccessListener(firebaseVisionFaces -> {
            if (!firebaseVisionFaces.isEmpty()) {
                //System.out.println("Siiiiii");
                processFaces(firebaseVisionFaces);
            } else {
                //System.out.println("Noooooo");
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                iv.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(e -> Log.i(TAG, e.toString()));
    }

    private void processFaces(List<FirebaseVisionFace> faces) {


        for (FirebaseVisionFace face : faces) {

            if ((face.getBoundingBox().width())>300){


                if (process_id != face.getTrackingId() && in_process == false){
                    //Deteccion interrumpida
                    process_id = face.getTrackingId();
                    fotos_face.clear();
                    number_photos = 0;

                }else{
                    //imagenes de un mismo Tracking y de Anchura > 370
                    if (number_photos<5){
                        Bitmap bitmapi = tv.getBitmap();
                        Bitmap bitmaprec = bitmap_scale(cropBitmap(bitmapi, face.getBoundingBox()));
                        fotos_face.add(getStringImage(bitmaprec));
                        if (number_photos == 4){
                            face_ = bitmaprec;
                        }
                        number_photos = number_photos + 1;
                    }

                    if (number_photos == 5){
                        in_process = true;
                        send_photo(id_us,fotos_face.get(4),number);
                        number_photos = number_photos + 1;
                    }


                    Rect box = new Rect(
                            (int) translateX(face.getBoundingBox().left),
                            (int) translateY(face.getBoundingBox().top),
                            (int) translateX(face.getBoundingBox().right),
                            (int) translateY(face.getBoundingBox().bottom));

                    canvas.drawText(String.valueOf((face.getTrackingId())),
                            translateX(face.getBoundingBox().centerX()),
                            translateY(face.getBoundingBox().centerY()),
                            linePaint);

                    Log.i(TAG, "top: " + (int) translateY(face.getBoundingBox().top)
                            + "left: " + (int) translateX(face.getBoundingBox().left)
                            + "bottom: " + (int) translateY(face.getBoundingBox().bottom)
                            + "right: " + (int) translateX(face.getBoundingBox().right));

                    Log.i(TAG, "top: " + face.getBoundingBox().top
                            + " left: " + face.getBoundingBox().left
                            + " bottom: " + face.getBoundingBox().bottom
                            + " right: " + face.getBoundingBox().right);

                    canvas.drawRect(box, linePaint);
                }
            }
        }
        iv.setImageBitmap(bitmap);
    }


    private float translateY(float y) {
        return y * heightScaleFactor;
    }

    private float translateX(float x) {
        float scaledX = x * widthScaleFactor;
        if (lens == CameraX.LensFacing.FRONT) {
            return canvas.getWidth() - scaledX;
        } else {
            return scaledX;
        }
    }

    private void initDrawingUtils() {
        bitmap = Bitmap.createBitmap(tv.getWidth(), tv.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.blueCL));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        linePaint.setTextSize(40);
        widthScaleFactor = canvas.getWidth() / (fbImage.getBitmap().getWidth() * 1.0f);
        heightScaleFactor = canvas.getHeight() / (fbImage.getBitmap().getHeight() * 1.0f);
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270.");
        }
    }

    public Bitmap cropBitmap(Bitmap bitmapx, Rect rect) {

        Bitmap imagex = Bitmap.createBitmap((int) translateX(rect.left) - (int) translateX(rect.right), (int) translateY(rect.bottom)-(int) translateY(rect.top), Bitmap.Config.ARGB_8888);
        new Canvas(imagex).drawBitmap(bitmapx,(int) translateX(rect.right)*-1,(int) translateY(rect.top)*-1, null);

        return imagex;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                FirebaseApp.initializeApp(getContext());
                tv.post(this::startCameraX);
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}