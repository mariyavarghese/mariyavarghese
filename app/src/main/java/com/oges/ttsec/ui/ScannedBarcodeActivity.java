package com.oges.ttsec.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.oges.ttsec.R;
import com.oges.ttsec.model.ContactModel;
import com.oges.ttsec.network.ApiInterface;
import com.oges.ttsec.network.ApiService;
import com.oges.ttsec.network.CheckNetwork;
import com.oges.ttsec.util.AppConstants;
import com.oges.ttsec.util.DataProcessor;
import com.oges.ttsec.util.UserStatusCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScannedBarcodeActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    private TextView txtBarcodeValue;
    private ImageView iv_backButton;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private ApiInterface apiInterface;
    private CheckNetwork checkNetwork;
    private KProgressHUD hud;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    private DataProcessor dataProcessor;
    private String user_unique_id, eventId, companyId, scanId ,contactcheck;
    Bundle bundle = new Bundle();
    private String userId;
    private String deviceImei = "-1";
    private TelephonyManager mTelephonyManager;
    private String deviceStatus = "";
    private CameraManager mCameraManager;
    private String mCameraId;
    boolean isFlashAvailable;
    private ToggleButton toggleButton;
    private Camera.Parameters parameters;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                handleUncaughtException (thread, e);
//            }
//        });


        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }




        isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);








//        switchFlashLight(isChecked);

        apiInterface = ApiService.getClient().create(ApiInterface.class);
        checkNetwork = new CheckNetwork(ScannedBarcodeActivity.this);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        dataProcessor = new DataProcessor(this);
        eventId = getIntent().getStringExtra("EVENTID");
        //     scanId = getIntent().getStringExtra("SCANID");
        userId = dataProcessor.getUserId(AppConstants.USERID);
        deviceImei = dataProcessor.getImeIId(AppConstants.IMEIID);
        if (eventId == null || eventId.isEmpty()) {
            eventId = dataProcessor.getEventId(AppConstants.EVENTID);
        }
        companyId = dataProcessor.getCompanyId(AppConstants.COMPANYID);
        contactcheck = dataProcessor.getContactStatus("CONTACT_STATUS");
//        checkScanStatus(userId, eventId, user_unique_id, deviceImei, contactcheck);
        initViews();


        iv_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (!checkNetwork.isNetworkConnected(this)) {
            showSnackbar(getString(R.string.network_error));
        }
        if (deviceImei == null || deviceImei.isEmpty()) {
            deviceImei = "-1";
        }
    }

//    private void handleUncaughtException(Thread thread, Throwable e) {
//        e.printStackTrace(); // not all Android versions will print the stack trace automatically
//        Toast.makeText(this, "Something went wrong...Try again", Toast.LENGTH_SHORT).show();
////        Intent intent = new Intent ();
////        intent.setAction ("com.mydomain.SEND_LOG"); // see step 5.
////        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
////        startActivity (intent);
//
//        System.exit(1); // kill off the crashed app
//    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        iv_backButton = findViewById(R.id.iv_backButton);
    }







    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Scanning...", Toast.LENGTH_SHORT).show();








        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {


//                    Camera camera = Camera.open();
//                    parameters = camera.getParameters();
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                    camera.setParameters(parameters);
//                    camera.startPreview();
                    Camera cam = Camera.open();
                    Camera.Parameters p = cam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();



                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
//                            if (barcodes.valueAt(0).email != null) {
//                                txtBarcodeValue.removeCallbacks(null);
//                                intentData = barcodes.valueAt(0).email.address;
//                                txtBarcodeValue.setText(intentData);
//                                isEmail = true;
//                                btnAction.setText("ADD CONTENT TO THE MAIL");
//                            } else {
//                                isEmail = false;
//                                btnAction.setText("LAUNCH URL");
//                                intentData = barcodes.valueAt(0).displayValue;
//                                txtBarcodeValue.setText(intentData);
//                            }


                            if (!isFlashAvailable) {
                                showNoFlashError();
                            }else{
//                                dataProcessor.setflashCode(ApiService.FLASH_IDNO, "1");
                                if(dataProcessor.getflashCode(ApiService.FLASH_IDNO).equals("1")) {
                                    try {
                                        mCameraManager.setTorchMode(mCameraId, true);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }







                            if (barcodes.valueAt(0) != null) {
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);
                                showProgress("code detected");
                                if (intentData != null) {
                                    checkUserStatus(userId, new UserStatusCallback() {
                                        @Override
                                        public void onSuccess(boolean value) {
                                            if (value) {
                                                bundle.putString("userId",userId);
                                                bundle.putString("companyId", companyId);
                                                bundle.putString("eventId", eventId);
                                                bundle.putString("user_unique_id", intentData);
                                                bundle.putString("imei_number", deviceImei);
                                                //user_unique_id=intentData;
                                                if (deviceImei == null || deviceImei.isEmpty()) {
                                                    deviceImei = "-1";
                                                }
                                                Log.d("eventVal", "" + companyId + " " + eventId + " " + intentData);
                                                callScan();
                                                // checkUserUniqueId(companyId, eventId, intentData, deviceImei);
                                                hud.dismiss();
                                                finish();  //newly added in 27.07.2020
                                            } else {
                                                dataProcessor.clear();
                                                showSnackbar(getString(R.string.user_status_error));
                                                Intent logoutIntent = new Intent(ScannedBarcodeActivity.this, LoginActivity.class);
                                                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(logoutIntent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onError() {
                                            hud.dismiss();
                                            showSnackbar(getString(R.string.network_error));
                                        }
                                    });

                                }
                            }
                        }
                    });
                }
            }
        });
    }













    private void callScan() {
        //checkScanStatus(companyId, eventId, intentData, deviceImei);
        Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
        badgeInactiveIntent.putExtras(bundle);
        startActivity(badgeInactiveIntent);
        hud.dismiss();
    }

    private void checkUserUniqueId(String companyId1, String eventId1, String user_unique_id1, String deviceImei1) {
        //showProgress(getString(R.string.loading));
        Call<ContactModel> call = apiInterface.userProfileDisplay(companyId1, eventId1, user_unique_id1, deviceImei1);
        call.enqueue(new Callback<ContactModel>() {
            @Override
            public void onResponse(Call<ContactModel> call, Response<ContactModel> response) {
                if (response.body() != null) {
                    Log.d("variables$$$", "" + companyId + " " + eventId + " " + user_unique_id);
                    ContactModel contactModel = response.body();
                    Log.d("response", " " + response.body().toString());
                    if (contactModel != null) {
                        if (contactModel.getCode().contentEquals("0")) {

                            Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
                            badgeInactiveIntent.putExtra("badge_status", "0");
                            startActivity(badgeInactiveIntent);
                            finish();
                        }
                        if (contactModel.getCode().contentEquals("1")) {

                            Intent userDisplayIntent = new Intent(ScannedBarcodeActivity.this, UserVerificationDisplayActivity.class);
                            userDisplayIntent.putExtras(bundle);
                            startActivity(userDisplayIntent);
                            finish();
                        }
                        if (contactModel.getCode().contentEquals("2")) {

                            Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
                            badgeInactiveIntent.putExtra("badge_status", "2");
                            startActivity(badgeInactiveIntent);
                            finish();
                        }
                    } else {
                        hud.dismiss();
                        showSnackbar(getString(R.string.network_error));
                    }
                } else {

                    Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
                    startActivity(badgeInactiveIntent);
                }
            }

            @Override
            public void onFailure(Call<ContactModel> call, Throwable t) {
                hud.dismiss();
                showSnackbar(getString(R.string.network_error));
            }
        });
    }

    //getDetails() not using anywhere
    public void getDetails(final String companyId1, final String eventId1, final String user_unique_id1, final String deviceImei1) {
        final RequestQueue requestQueue = Volley.newRequestQueue(ScannedBarcodeActivity.this);
        String url = "http://192.168.10.57:8081/ttseceventservices/public/api/contact_listing";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.contentEquals("0")) {
                        Toast.makeText(ScannedBarcodeActivity.this, "0", Toast.LENGTH_SHORT).show();
                    } else if (code.contentEquals("1")) {
                        Toast.makeText(ScannedBarcodeActivity.this, "1", Toast.LENGTH_SHORT).show();

                    } else if (code.contentEquals("2")) {
                        Toast.makeText(ScannedBarcodeActivity.this, "2", Toast.LENGTH_SHORT).show();

                    } else if (code.contentEquals("3")) {
                        Toast.makeText(ScannedBarcodeActivity.this, "3", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ScannedBarcodeActivity.this, "unable_to_connect", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("company_id", companyId1);
                params.put("event_id", eventId1);
                params.put("user_unique_id", user_unique_id1);
                params.put("imei_number", deviceImei1);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScannedBarcodeActivity.this, EventSelectActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (hud.isShowing())
//            hud.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (hud.isShowing())
//            hud.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
//        if (hud.isShowing())
//            hud.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        txtBarcodeValue.setText("Please place your camera properly");
        initialiseDetectorsAndSources();

    }





    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void switchFlashLight(int status) {
//        try {
//            mCameraManager.setTorchMode(mCameraId, status);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }











    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnonFlash() {

        try {
            mCameraManager.setTorchMode(mCameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }




    private void showProgress(String message) {
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setBackgroundColor(R.color.blueBg)
                .setLabel(message)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT).show();
    }

    private void checkScanStatus(String companyId1, String eventId1, String user_unique_id1, String deviceImei1,String contactcheck) {
        Call<JsonObject> call = apiInterface.checkScanStatus(companyId1, eventId1, user_unique_id1, deviceImei1,contactcheck);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        String verificationCode = jsonObject.get("verification_code").toString();
                        deviceStatus = jsonObject.get("device_status").toString();
                        String deviceCode = deviceStatus.replaceAll("^\"|\"$", "");
                        dataProcessor.setDeviceStatus(AppConstants.DEVICESTATUS, deviceCode);
                        Log.d("DEVICESTATUSOUT", " :" + deviceCode);

                        String vCode = verificationCode.replaceAll("^\"|\"$", "");
                        if (vCode.contentEquals("1")) {
                            dataProcessor.setVerificationCode(AppConstants.VERIFICATIONCODE, vCode);
                        } else if (vCode.contentEquals("0")) {
                            dataProcessor.setVerificationCode(AppConstants.VERIFICATIONCODE, vCode);
                        }
                        String userCode = jsonObject.get("code").toString();
                        String code = userCode.replaceAll("^\"|\"$", "");


                        if(deviceCode.equals("4")){  //Device not registered
                            Toast.makeText(ScannedBarcodeActivity.this, "Badge Already Used", Toast.LENGTH_SHORT).show();
                        }


                        if (code.contentEquals("0")) {

                            Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
                            badgeInactiveIntent.putExtra("badge_status", "0");
                            startActivity(badgeInactiveIntent);
                            finish();
                        }
                        if (code.contentEquals("1")) {

                            Intent userDisplayIntent = new Intent(ScannedBarcodeActivity.this, UserVerificationDisplayActivity.class);
                            userDisplayIntent.putExtras(bundle);
                            startActivity(userDisplayIntent);
                            finish();
                        }
                        if (code.contentEquals("2")) {
                            Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
                            badgeInactiveIntent.putExtra("badge_status", "2");
                            startActivity(badgeInactiveIntent);
                            finish();
                        }
                        if (code == null) {
                            Toast.makeText(ScannedBarcodeActivity.this, "Something went wrong. Try again...", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.body() == null) {
                        Intent badgeInactiveIntent = new Intent(ScannedBarcodeActivity.this, BadgeInactiveActivity.class);
                        badgeInactiveIntent.putExtra("badge_status", "2");
                        startActivity(badgeInactiveIntent);
                        finish();
                    }
                } else if (response.errorBody() != null) {
                    Toast.makeText(ScannedBarcodeActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ScannedBarcodeActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(String userId, final UserStatusCallback callback) {

        Call<JsonObject> call = apiInterface.checkUserStatus(userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    String jsonElement = jsonObject.get("company_status").toString();
                    String s = jsonElement.replaceAll("^\"|\"$", "");
                    Log.d("company_status1", "" + s);
                    if (s.contentEquals("1")) {
                        // userStatus = true;
                        callback.onSuccess(true);
                    } else if (s.contentEquals("0")) {
                        //userStatus = false;
                        callback.onSuccess(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError();
                showSnackbar(getString(R.string.network_error));
            }
        });
    }

}
