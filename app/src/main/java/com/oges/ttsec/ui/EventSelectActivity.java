package com.oges.ttsec.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oges.ttsec.R;
import com.oges.ttsec.model.Event;
import com.oges.ttsec.model.EventModel;
import com.oges.ttsec.network.ApiInterface;
import com.oges.ttsec.network.ApiService;
import com.oges.ttsec.network.CheckNetwork;
import com.oges.ttsec.util.AppConstants;
import com.oges.ttsec.util.DataProcessor;
import com.oges.ttsec.util.UserStatusCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventSelectActivity extends AppCompatActivity {

    private Spinner sp_event, sp_scanType;
    private Button bt_submit,bt_okImei;
    private ImageView iv_logout, iv_showImei;
    private TextView tv_showImei;
    private CheckNetwork checkNetwork;
    private ApiInterface apiInterface;
    private DataProcessor dataProcessor;
    private KProgressHUD hud;
    private ArrayList<String> eventNameList = new ArrayList<>();
    private ArrayList<String> eventIdList = new ArrayList<>();
    private ArrayList<String> scanTypeList = new ArrayList<>();
    private ArrayList<String> scanTypeIdList = new ArrayList<>();
    private ArrayAdapter eventAdapter;
    private ArrayAdapter scanTypeAdapter;
    private String companyId;
    private boolean userStatus;
    private boolean bt_submitStatus = false;
    private String userId;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final int REQUEST_PHONE_STATE = 101;
    String serial;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        checkNetwork = new CheckNetwork(EventSelectActivity.this);
        dataProcessor = new DataProcessor(EventSelectActivity.this);
        userId = dataProcessor.getUserId(AppConstants.USERID);
        companyId = dataProcessor.getCompanyId(AppConstants.COMPANYID);
        Log.d("companyId", " " + companyId);
        setContentView(R.layout.activity_event_select);

        initViews();
        if (checkNetwork.isNetworkConnected(EventSelectActivity.this)) {

            fillEventList();
            // fillScanTypeList();
//            checkImeiPermission();
            serial = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            dataProcessor.setImeIId(AppConstants.IMEIID, serial);

            bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showProgress(getString(R.string.loading));
                    checkUserStatus(userId, new UserStatusCallback() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onSuccess(boolean value) {
                            if (value) {
                                String eventId = eventIdList.get(sp_event.getSelectedItemPosition());
                                dataProcessor.setEventId(AppConstants.EVENTID, eventId);
                                //String scanTypeId = scanTypeIdList.get(sp_scanType.getSelectedItemPosition());
                                Log.d("EVENTID", " " + eventId);
                                if (eventId.contentEquals("-1")) {
                                    hud.dismiss();
                                    showSnackbar("Select Event");
                                } else {

                                    Intent intent = new Intent(EventSelectActivity.this, ScannedBarcodeActivity.class);
                                    intent.putExtra("EVENTID", eventId);
                                    hud.dismiss();
                                    if (checkCameraPermission()) {
                                        startActivity(intent);
                                    }
                                }

//                                if (scanTypeId.contentEquals("-1")) {
//                                    showSnackbar("Select Scan Type");
//                                }
//                                if (scanTypeId.contentEquals("1")) {
//
//                                    Intent intent = new Intent(EventSelectActivity.this, ScannedBarcodeActivity.class);
//                                    intent.putExtra("EVENTID", eventId);
//                                    intent.putExtra("SCANID", scanTypeId);
//                                    checkCameraPermission();
//                                    //startActivity(intent);
//                                } else if (scanTypeId.contentEquals("2")) {
//                                    Intent intent = new Intent(EventSelectActivity.this, ScannedBarcodeActivity.class);
//                                    intent.putExtra("EVENTID", eventId);
//                                    intent.putExtra("SCANID", scanTypeId);
//                                    checkCameraPermission();
//                                    //startActivity(intent);
//                                }
                            } else {
                                dataProcessor.clear();
                                showSnackbar(getString(R.string.user_status_error));
                                Intent logoutIntent = new Intent(EventSelectActivity.this, LoginActivity.class);
                                //logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            });


        } else {
            showSnackbar(getString(R.string.network_error));
        }


        iv_logout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final View view) {
                PopupMenu popup_user_settings = new PopupMenu(EventSelectActivity.this, iv_logout);
                //popup_user_settings.setGravity(Gravity.END);
                popup_user_settings.getMenuInflater().inflate(R.menu.logout_menu, popup_user_settings.getMenu());
                popup_user_settings.setGravity(Gravity.CENTER_HORIZONTAL);
                popup_user_settings.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        if (menuItem.getTitle().toString().trim().equals("Show IMEI")) {
                        if (menuItem.getTitle().toString().trim().equals(getResources().getString(R.string.show_serial_no))) {
                            showPopup();
                        }else if (menuItem.getTitle().toString().trim().equals(getResources().getString(R.string.config))) {
                            configPopup();
                        } else if (menuItem.getTitle().toString().trim().equals("Logout")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(EventSelectActivity.this);
                            builder.setTitle(R.string.app_name);
                            builder.setMessage("Do you really want to logout ?");
                            builder.setIcon(R.drawable.ttsec_logo);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    dataProcessor.clear();
                                    Intent logoutIntent = new Intent(EventSelectActivity.this, LoginActivity.class);
                                    startActivity(logoutIntent);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        return true;
                    }
                });
                popup_user_settings.show();
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


    private void fillScanTypeList() {
        try {
            if (!scanTypeList.isEmpty()) {
                scanTypeList.clear();
            }
            if (!scanTypeIdList.isEmpty()) {
                scanTypeIdList.clear();
            }
        } catch (Exception e) {

        }
        scanTypeIdList.add("-1");
        scanTypeIdList.add("1");
        scanTypeIdList.add("2");
        scanTypeList.add(getString(R.string.select_scanType));
        scanTypeList.add(getString(R.string.barcode));
        scanTypeList.add(getString(R.string.qrcode));
        scanTypeAdapter.notifyDataSetChanged();
    }

    public void getDetails() {
        final RequestQueue requestQueue = Volley.newRequestQueue(EventSelectActivity.this);
        String url = "http://192.168.10.57:8081/ttseceventservices/public/api/event_listing";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("event_listing");
                    for (int i = 0; i <= jsonArray.length(); i++) {
                        Event eventModel = new Event();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        eventModel.setId(jsonObject1.getString("id"));
                        eventModel.setEventName(jsonObject1.getString("evnt_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(EventSelectActivity.this, "unable_to_connect", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("company_id", "1");
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

    private void fillEventList() {
        showProgress(getString(R.string.verifying));
        try {
            if (!eventNameList.isEmpty()) {
                eventNameList.clear();
            }
            if (!eventIdList.isEmpty()) {
                eventIdList.clear();
            }
        } catch (Exception e) {

        }
        eventIdList.add("-1");
        eventNameList.add(getString(R.string.select_event));

        Call<EventModel> call = apiInterface.getEventList(userId);
        call.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {

                if (response.body() != null) {
                    Log.d("responseBody", " " + response.body());
                    EventModel eventModel = response.body();
                    if (eventModel != null) {
                        List<EventModel.EventListing> eventListing = eventModel.getEventListing();
                        for (int i = 0; i < eventListing.size(); i++) {
                            eventNameList.add(eventListing.get(i).getEvntName());
                            eventIdList.add(eventListing.get(i).getId() + "");
                            eventAdapter.notifyDataSetChanged();
                        }
                        hud.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
                showSnackbar(getString(R.string.network_error));
                hud.dismiss();
            }
        });
    }

    private void initViews() {
        sp_event = findViewById(R.id.sp_event);
        // sp_scanType = findViewById(R.id.sp_scanType);
        bt_submit = findViewById(R.id.bt_submit);
        iv_logout = findViewById(R.id.iv_logout);
        tv_showImei=findViewById(R.id.tv_showImei);
        eventAdapter = new ArrayAdapter(EventSelectActivity.this, R.layout.spinner_item, eventNameList);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_event.setAdapter(eventAdapter);
//        scanTypeAdapter = new ArrayAdapter(EventSelectActivity.this, R.layout.spinner_item, scanTypeList);
//        scanTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sp_scanType.setAdapter(scanTypeAdapter);
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

    public void showPopup() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.imei_popup_layout, null, false), 200, 200, true);
        pw.setWidth(width - 50);
        pw.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        pw.showAtLocation(findViewById(R.id.layoutTemp), Gravity.CENTER, 0, 0);
       // Toast.makeText(this, "IMEI:"+dataProcessor.getImeIId(AppConstants.IMEIID), Toast.LENGTH_SHORT).show();
        String imeIId = dataProcessor.getImeIId(AppConstants.IMEIID);
        ((TextView)pw.getContentView().findViewById(R.id.tv_showImei)).setText("Device Serial No: "+imeIId);
//        ((TextView)pw.getContentView().findViewById(R.id.tv_showImei)).setText("IMEI No: "+imeIId);
        bt_okImei=(Button) pw.getContentView().findViewById(R.id.bt_okImei);
        bt_okImei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
            }
        });
    }

    private boolean checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(EventSelectActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(EventSelectActivity.this, ScannedBarcodeActivity.class);
//            startActivity(intent);
            return true;
        } else {
            ActivityCompat.requestPermissions(EventSelectActivity.this, new
                    String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
    }

    private void checkImeiPermission() {
        ActivityCompat.requestPermissions(EventSelectActivity.this,  //Device IMEI no permission
                new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_PHONE_STATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  Log.d(TAG,"permission was granted! Do your stuff");
                    if (ActivityCompat.checkSelfPermission(EventSelectActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(EventSelectActivity.this, ScannedBarcodeActivity.class);
                        startActivity(intent);

                    }
                } else {
                    // Log.d(TAG,"permission denied! Disable the function related with permission.");
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_PHONE_STATE: {
                String deviceImei;
                String serial = null;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                    //continue using `getImei()` or `getDeviceId()`
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    deviceImei = telephonyManager.getImei(0);
                    // Toast.makeText(this, " Dev Id" + deviceImei, Toast.LENGTH_SHORT).show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        serial = Build.getSerial();
                        Log.e("serialId",serial);
                    }else{
                        serial = Build.SERIAL;
                    }
                } else {

//                    deviceImei = "-1";
                    serial = "-1";
                }
//                dataProcessor.setImeIId(AppConstants.IMEIID, deviceImei);
                dataProcessor.setImeIId(AppConstants.IMEIID, serial);
                Log.d("imei@@@",": "+serial);
                return;
            }
        }
    }


    //--------------Newly added 26.04.2021 for punch in/punch out--------------------------------------
    public void configPopup() {

        dialog = new Dialog(EventSelectActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_switch_option);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Switch sw = (Switch) dialog.findViewById(R.id.switch1);
        Switch sw1 = (Switch) dialog.findViewById(R.id.switch2);
        Switch sw2 = (Switch) dialog.findViewById(R.id.switch3);
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

        if(dataProcessor.getConfigStatus("CONFIG_STATUS").equals("1")){
            sw.setChecked(true);
        }else {
            sw.setChecked(false);
        }


        if(dataProcessor.getContactStatus("CONTACT_STATUS").equals("1")){
            sw1.setChecked(true);
        }else {
            sw1.setChecked(false);
        }

        if(dataProcessor.getflashCode("FLASH").equals("1")){
            sw2.setChecked(true);
        }else {
            sw2.setChecked(false);
        }



        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    dataProcessor.setConfigStatus("CONFIG_STATUS","1");
                } else {
                    // The toggle is disabled
                    dataProcessor.setConfigStatus("CONFIG_STATUS","0");
                }
            }
        });

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    dataProcessor.setContactStatus("CONTACT_STATUS","1");
                } else {
                    // The toggle is disabled
                    dataProcessor.setContactStatus("CONTACT_STATUS","0");
                }
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    dataProcessor.setflashCode("FLASH","1");
                } else {
                    // The toggle is disabled
                    dataProcessor.setflashCode("FLASH","0");
                }
            }
        });


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //--------------Newly added 26.04.2021--------------------------------------
}
