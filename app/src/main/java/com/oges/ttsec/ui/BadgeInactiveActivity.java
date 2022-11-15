package com.oges.ttsec.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.oges.ttsec.R;
import com.oges.ttsec.network.ApiInterface;
import com.oges.ttsec.network.ApiService;
import com.oges.ttsec.network.CheckNetwork;
import com.oges.ttsec.util.AppConstants;
import com.oges.ttsec.util.DataProcessor;
import com.oges.ttsec.util.UserStatusCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BadgeInactiveActivity extends AppCompatActivity {

    private ImageView iv_backButton;
    private boolean userStatus;
    private DataProcessor dataProcessor;
    private String userId;
    private CheckNetwork checkNetwork;
    private ApiInterface apiInterface;
    private TextView tv_badgeError,tv_error,tv_zones,tv_title,tv_area,tv_venue,tv_previlege,tv_title_area,tv_title_venue,tv_title_previlege;
    private ImageView iv_error;
    private String deviceStatus;
    private KProgressHUD hud;
    Bundle bundle = new Bundle();
    private String user_unique_id, eventId, companyId, deviceImei, contactcheck;
    private LinearLayout ll_badgeInactive,ll_permission_zone,ll_permission_area,ll_permission_venue,ll_permission_previlege;
    private CameraManager mCameraManager;
    private String mCameraId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dataProcessor = new DataProcessor(getApplicationContext());
        checkNetwork = new CheckNetwork(this);
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        userId = dataProcessor.getUserId(AppConstants.USERID);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            turnOffFlash();
//        }

        new Handler().postDelayed(new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                turnOffFlash();
            }
        }, 1000);





        bundle = getIntent().getExtras();
        companyId = bundle.getString("companyId");
        eventId = bundle.getString("eventId");
        user_unique_id = bundle.getString("user_unique_id");
        deviceImei = bundle.getString("imei_number");
        contactcheck = dataProcessor.getContactStatus("CONTACT_STATUS");
        Log.d("CONTACT_STATUS", " :" + contactcheck);
//        checkScanStatus(companyId, eventId, user_unique_id, deviceImei);
        checkScanStatus(userId, eventId, user_unique_id, deviceImei, contactcheck);

        setContentView(R.layout.activity_badge_inactive);
        iv_backButton = findViewById(R.id.iv_backButton);
        ll_badgeInactive = findViewById(R.id.ll_badgeInactive);
        ll_permission_zone = findViewById(R.id.ll_permission);
        ll_permission_area = findViewById(R.id.ll_permission_area);
        ll_permission_venue = findViewById(R.id.ll_permission_venue);
        ll_permission_previlege = findViewById(R.id.ll_permission_previlege);
        tv_badgeError = findViewById(R.id.tv_badgeError);
        tv_error = findViewById(R.id.tv_error);
        tv_zones = findViewById(R.id.tv_zones);
        tv_area = findViewById(R.id.tv_area);
        tv_venue = findViewById(R.id.tv_venue);
        tv_previlege = findViewById(R.id.tv_previlege);
        iv_error = findViewById(R.id.iv_error);
        tv_title = findViewById(R.id.tv_title);
        tv_title_area = findViewById(R.id.tv_title_area);
        tv_title_venue = findViewById(R.id.tv_title_venue);
        tv_title_previlege = findViewById(R.id.tv_title_previlege);

        ll_permission_zone.setVisibility(View.GONE);
        ll_permission_area.setVisibility(View.GONE);
        ll_permission_venue.setVisibility(View.GONE);
        ll_permission_previlege.setVisibility(View.GONE);
        tv_error.setVisibility(View.GONE);
        showProgress("Verifying");
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        if (checkNetwork.isNetworkConnected(BadgeInactiveActivity.this)) {

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_scanNext:
                            checkUserStatus(userId, new UserStatusCallback() {
                                @Override
                                public void onSuccess(boolean value) {
                                    if (value) {
                                        Toast.makeText(BadgeInactiveActivity.this, "Scan next", Toast.LENGTH_SHORT).show();
                                        Intent scanintent = new Intent(BadgeInactiveActivity.this, ScannedBarcodeActivity.class);
                                        scanintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(scanintent);
                                        finish();
                                    } else {
                                        dataProcessor.clear();
                                        showSnackbar(getString(R.string.user_status_error));
                                        Intent logoutIntent = new Intent(BadgeInactiveActivity.this, LoginActivity.class);
                                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(logoutIntent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onError() {
                                    showSnackbar(getString(R.string.network_error));
                                }
                            });
                            break;
                        case R.id.menu_exitApp:
                            Toast.makeText(BadgeInactiveActivity.this, "Exit", Toast.LENGTH_SHORT).show();
                            Intent logoutIntent = new Intent(BadgeInactiveActivity.this, EventSelectActivity.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logoutIntent);
                            finish();
                            break;
                    }
                    return true;
                }


            });

        } else {
            showSnackbar(getString(R.string.network_error));
        }
//        iv_backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(BadgeInactiveActivity.this, "Scan next", Toast.LENGTH_SHORT).show();
//                Intent scanintent = new Intent(BadgeInactiveActivity.this, ScannedBarcodeActivity.class);
//                scanintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(scanintent);
//                finish();
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnOffFlash() {

        try {
            mCameraManager.setTorchMode(mCameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        //Do nothing
        //Back button disabled
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

    private void checkScanStatus(final String userId1, final String eventId1, String user_unique_id1, String deviceImei1, final String contactcheck) {
        Call<JsonObject> call = apiInterface.checkScanStatus(userId1, eventId1, user_unique_id1, deviceImei1, contactcheck);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("ResponSE", " :" + response);
                        Log.d("request", " :" + userId1 + eventId1);
                        Log.d("ResponSE", " :" );

                        JsonObject jsonObject = response.body();
                        String verificationCode = jsonObject.get("verification_code").toString();
//                        String msg = jsonObject.get("message").toString();
                        deviceStatus = jsonObject.get("device_status").toString();
                        String deviceStatusCode = deviceStatus.replaceAll("^\"|\"$", "");
                        dataProcessor.setDeviceStatus(AppConstants.DEVICESTATUS, deviceStatusCode);
                        Log.d("DEVICESTATUSOUT", " :" + deviceStatusCode);

                        String vCode = verificationCode.replaceAll("^\"|\"$", "");


                        if (vCode.contentEquals("1")) {
                            dataProcessor.setVerificationCode(AppConstants.VERIFICATIONCODE, vCode);
                        } else if (vCode.contentEquals("0")) {
                            dataProcessor.setVerificationCode(AppConstants.VERIFICATIONCODE, vCode);
                        } else {
                            Log.d("VcodeValue", " " + vCode);
                        }
                        String userCode = jsonObject.get("code").toString();
                        String code = userCode.replaceAll("^\"|\"$", "");
                        Log.e("@code", "=" + code);

//                        if(vCode.contentEquals("0") && userCode.equals("0") && deviceStatusCode.equals("4")){
//                            Toast.makeText(BadgeInactiveActivity.this, "Badge is already used..", Toast.LENGTH_SHORT).show();
//                        }


                        if (deviceStatusCode.equals("0")) {  //Device not registered
                            ll_badgeInactive.setVisibility(View.VISIBLE);
                            tv_badgeError.setText(getString(R.string.device_not_registered));
                            Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                            hud.dismiss();
                        } else if (deviceStatusCode.equals("4")) {


                            if (code.contentEquals("0") && vCode.equals("0"))


                                ll_badgeInactive.setVisibility(View.VISIBLE);
                            tv_badgeError.setText(getString(R.string.badge_already));
                            Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                            hud.dismiss();


                        }else  if (deviceStatusCode.equals("00")) {


                            if (code.contentEquals("0") && vCode.equals("0"))
                                ll_badgeInactive.setVisibility(View.VISIBLE);
                                tv_badgeError.setText(jsonObject.get("message").getAsString());
                                tv_badgeError.setTextColor(Color.RED);
                                Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                                hud.dismiss();

                        }else  if (deviceStatusCode.equals("11")) {


                            if (code.contentEquals("0") && vCode.equals("0"))
                                ll_badgeInactive.setVisibility(View.VISIBLE);
                                tv_badgeError.setText(jsonObject.get("message").getAsString());

                                Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                                hud.dismiss();


                        }else if(deviceStatusCode.equals("5")){

                            if (code.contentEquals("0") && vCode.equals("0"))

                                ll_badgeInactive.setVisibility(View.VISIBLE);
                                tv_badgeError.setText(getString(R.string.badge_not_collected));
                                Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                                hud.dismiss();

                        }else if(deviceStatusCode.equals("1")){ //Device registered
                            if (code.contentEquals("0")) {

                                ll_badgeInactive.setVisibility(View.VISIBLE);
                                tv_badgeError.setText(getString(R.string.badge_inactive));
                                Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                                hud.dismiss();
                            } else if (code.contentEquals("1")) {
                                Intent userDisplayIntent = new Intent(BadgeInactiveActivity.this, UserVerificationDisplayActivity.class);
                                userDisplayIntent.putExtras(bundle);
                                userDisplayIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(userDisplayIntent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                                overridePendingTransition(0, 0);
                                finish();
                                hud.dismiss();
                            } else if (code.contentEquals("2")) {
                                ll_badgeInactive.setVisibility(View.VISIBLE);
                                tv_badgeError.setText(getString(R.string.badge_not_found));
                                Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_no_stopping).into(iv_error);
                                hud.dismiss();
                            } else {

                                Toast.makeText(BadgeInactiveActivity.this, "Something went wrong. Try again...", Toast.LENGTH_SHORT).show();
                                hud.dismiss();
                                finish();


                            }
                        }else if(deviceStatusCode.equals("3")){ //No access for zone
                            if(jsonObject.has("zones")){
                                JsonArray zones = jsonObject.getAsJsonArray("zones");
                                if(zones.size()>0){
                                    String strZones = "";
                                    for(int i=0;i<zones.size();i++){
                                        JsonObject zoneObject=zones.get(i).getAsJsonObject();
                                        if(i==0){
                                            strZones=zoneObject.get("zone_code").getAsString();
                                        }else{
                                            strZones=strZones+", "+zoneObject.get("zone_code").getAsString();
                                        }
                                    }
                                    tv_zones.setText(strZones);
                                    tv_title.setText("Permitted Zones");
                                    tv_error.setText("Access denied as individual does not have permission");
                                    ll_permission_zone.setVisibility(View.VISIBLE);
                                }else{
                                    tv_title.setVisibility(View.GONE);
                                    tv_zones.setVisibility(View.GONE);
                                    tv_error.setText("Access denied as individual does not have permission");
                                }
                            }
                            if(jsonObject.has("areas")){
                                JsonArray areas = jsonObject.getAsJsonArray("areas");
                                if(areas.size()>0){
                                    String strAreas = "";
                                    for(int i=0;i<areas.size();i++){
                                        JsonObject areaObject=areas.get(i).getAsJsonObject();
                                        if(i==0){
                                            strAreas=areaObject.get("area_code").getAsString();
                                        }else{
                                            strAreas=strAreas+", "+areaObject.get("area_code").getAsString();
                                        }
                                    }
                                    tv_area.setText(strAreas);
                                    tv_title_area.setText("Permitted Areas");
                                    tv_error.setText("Access denied as individual does not have permission");
                                    ll_permission_area.setVisibility(View.VISIBLE);
                                }else{
                                    tv_title_area.setVisibility(View.GONE);
                                    tv_area.setVisibility(View.GONE);
                                    tv_error.setText("Access denied as individual does not have permission");
                                }
                            }
                            if(jsonObject.has("venues")){
                                JsonArray venues = jsonObject.getAsJsonArray("venues");
                                if(venues.size()>0){
                                    String strVenues = "";
                                    for(int i=0;i<venues.size();i++){
                                        JsonObject venueObject=venues.get(i).getAsJsonObject();
                                        if(i==0){
                                            strVenues=venueObject.get("venue_code").getAsString();
                                        }else{
                                            strVenues=strVenues+", "+venueObject.get("venue_code").getAsString();
                                        }
                                    }
                                    tv_venue.setText(strVenues);
                                    tv_title_venue.setText("Permitted Venues");
                                    tv_error.setText("Access denied as individual does not have permission");
                                    ll_permission_venue.setVisibility(View.VISIBLE);
                                }else{
                                    tv_title_venue.setVisibility(View.GONE);
                                    tv_venue.setVisibility(View.GONE);
                                    tv_error.setText("Access denied as individual does not have permission");
                                }
                            }
                            if(jsonObject.has("privileges")){
                                JsonArray privileges = jsonObject.getAsJsonArray("privileges");
                                if(privileges.size()>0){
                                    String strPrivileges = "";
                                    for(int i=0;i<privileges.size();i++){
                                        JsonObject privilegeObject=privileges.get(i).getAsJsonObject();
                                        if(i==0){
                                            if(privilegeObject.has("privilege_code")) {
                                                strPrivileges = privilegeObject.get("privilege_code").getAsString();
                                            }
                                        }else{
                                            strPrivileges=strPrivileges+", "+privilegeObject.get("privilege_code").getAsString();
                                        }
                                    }


                                    if((!strPrivileges.equals(null)) || (!strPrivileges.equals(""))) {

                                        tv_previlege.setText(strPrivileges);
                                        tv_title_previlege.setText("Permitted Privileges");
                                        tv_error.setText("Access denied as individual does not have permission");
                                        ll_permission_previlege.setVisibility(View.VISIBLE);
                                    }else{
                                        tv_previlege.setText("");
                                        tv_title_previlege.setText("Permitted Privileges");
                                        tv_error.setText("Access denied as individual does not have permission");
                                        ll_permission_previlege.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    tv_title_previlege.setVisibility(View.GONE);
                                    tv_previlege.setVisibility(View.GONE);
                                    tv_error.setText("Access denied as individual does not have permission");
                                }
                            }
                            ll_badgeInactive.setVisibility(View.VISIBLE);
                            tv_badgeError.setVisibility(View.GONE);
                            tv_error.setVisibility(View.VISIBLE);
                            Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_badge_inactive).into(iv_error);
                            Log.e("val",""+deviceStatus);
                            hud.dismiss();

                        }else{
                            Log.e("val",""+deviceStatus);
                            hud.dismiss();
                        }

                    }
                    else if (response.body() == null) {
                        ll_badgeInactive.setVisibility(View.VISIBLE);
                        tv_badgeError.setText(getString(R.string.badge_not_found));
                        Glide.with(BadgeInactiveActivity.this).load(R.drawable.ic_no_stopping).into(iv_error);
                        hud.dismiss();
                    }
                } else if (response.errorBody() != null) {
//                    hud.dismiss();
                    Toast.makeText(BadgeInactiveActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(BadgeInactiveActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

}
