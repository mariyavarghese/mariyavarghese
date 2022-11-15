package com.oges.ttsec.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.oges.ttsec.R;
import com.oges.ttsec.model.LoginModel;
import com.oges.ttsec.network.ApiInterface;
import com.oges.ttsec.network.ApiService;
import com.oges.ttsec.network.CheckNetwork;
import com.oges.ttsec.util.AppConstants;
import com.oges.ttsec.util.DataProcessor;
import com.oges.ttsec.util.UserStatusCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText ed_username, ed_password;
    private Button bt_submit, bt_okImei;
    private CheckNetwork checkNetwork;
    private ApiInterface apiInterface;
    private DataProcessor dataProcessor;
    private ProgressDialog progressDialog;
    private KProgressHUD hud;
    private boolean bt_submit_status;
    private String str_username, str_password;
    private String userId;
    private String companyId;
    private ImageView iv_sample, iv_show;
    private boolean userStatus;
    private static final int REQUEST_PHONE_STATE = 101;
    String serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProcessor = new DataProcessor(getApplicationContext());
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        checkNetwork = new CheckNetwork(LoginActivity.this);
        String userId = dataProcessor.getToken(AppConstants.USERID);
        Log.d("dataproId", "" + userId);
        if (checkNetwork.isNetworkConnected(this)) {
            if (userId != null) {
                checkUserStatus(userId, new UserStatusCallback() {
                    @Override
                    public void onSuccess(boolean value) {
                        if (value) {
                            Toast.makeText(LoginActivity.this, "Active", Toast.LENGTH_SHORT).show();
                            loginIntent();
                        } else {
                            showSnackbar(getString(R.string.user_status_error));
                        }
                    }

                    @Override
                    public void onError() {
                        showSnackbar(getString(R.string.network_error));
                    }
                });

            }
        }
        setContentView(R.layout.activity_login);
        initViews();

        serial = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        dataProcessor.setImeIId(AppConstants.IMEIID, serial);

        bt_submit_status = true;

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt_submit_status) {
                    bt_submit_status = false;
                    getUsercredentials();
                    if (checkNetwork.isNetworkConnected(LoginActivity.this)) {
                        if (checkEmptyFields()) {
                            Log.d("call###", "before api call");
                            userLoginApiCall();
                        }
                    } else {
                        showSnackbar(getString(R.string.network_error));
                        bt_submit_status = true;
                    }
                }
            }
        });
        iv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopup();

            }
        });
    }

    private void checkImeiPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this,  //Device IMEI no permission
                new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_PHONE_STATE);
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
        ((TextView) pw.getContentView().findViewById(R.id.tv_showImei)).setText("Device Serial No: \n\n" + imeIId);
//        ((TextView)pw.getContentView().findViewById(R.id.tv_showImei)).setText("IMEI No: "+imeIId);
        bt_okImei = (Button) pw.getContentView().findViewById(R.id.bt_okImei);
        bt_okImei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
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

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    private void userLoginApiCall() {
        //showProgressBar();
        showProgress(getString(R.string.verifying));
        Call<LoginModel> call = apiInterface.loginUser(str_username, str_password);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                Log.d("api###", "inside api call");
                if (response.body() != null) {
                    LoginModel loginModel = response.body();
                    Log.d("jsondata", " " + response.body().toString());
                    if (loginModel != null) {
                        if (loginModel.getLogin_status().contentEquals("1")) {
                            userId = String.valueOf(loginModel.getUser_id());
                            companyId = loginModel.getCompany_details().getId() + "";
                            Log.d("userId###", "" + userId);
                            dataProcessor.setToken(AppConstants.USERID, userId);
                            dataProcessor.setUserId(AppConstants.USERID, userId);
                            dataProcessor.setCompanyId(AppConstants.COMPANYID, companyId);
                            LoginModel.Company_details company_details = loginModel.getCompany_details();
                            // JsonObject jsonObject=loginModel.getCompany_details();
                            //dismissProgressBar();
                            hud.dismiss();
                            Toast.makeText(LoginActivity.this, "success...", Toast.LENGTH_SHORT).show();
                            //displayBaseImage(company_details.getC_logo_file());
                            loginIntent();
                            bt_submit_status = true;
                        }
                        if (loginModel.getLogin_status().contentEquals("0")) {
                            //dismissProgressBar();
                            hud.dismiss();
                            Toast.makeText(LoginActivity.this, "Check your username and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (response.errorBody() != null && response.errorBody().source() != null) {
                    hud.dismiss();
                    Toast.makeText(LoginActivity.this, "error...", Toast.LENGTH_SHORT).show();
                    Log.d("api###", "inside error body");
                }
                bt_submit_status = true;
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                //dismissProgressBar();
                hud.dismiss();
                showSnackbar(getString(R.string.network_error));
                bt_submit_status = true;
            }
        });

    }

    private void loginIntent() {
        Intent intent = new Intent(LoginActivity.this, EventSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean checkEmptyFields() {
        boolean field_empty_status = false;
        if (str_username.length() == 0) {
            ed_username.setError("Please enter username");
            field_empty_status = false;
        }
        if (str_password.length() == 0) {
            ed_password.setError("Please enter password");
            field_empty_status = false;
        }
        if (str_username.length() != 0 && str_password.length() != 0) {
            field_empty_status = true;
        }
        bt_submit_status = true;
        return field_empty_status;
    }


    private void getUsercredentials() {
        str_username = ed_username.getText().toString().trim();
        str_password = ed_password.getText().toString().trim();
    }

    private void initViews() {
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);
        bt_submit = findViewById(R.id.bt_submit);
        iv_sample = findViewById(R.id.iv_sample);
        iv_show = findViewById(R.id.iv_show);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT).show();
    }

    private void showProgressBar() {
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void dismissProgressBar() {
        progressDialog.dismiss();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                serial = null;
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
                    String imei = telephonyManager.getImei(0);
                    Toast.makeText(this, " Dev Id" + imei, Toast.LENGTH_SHORT).show();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        serial = Build.getSerial();
                        Log.e("serialId", serial);
                    } else {
                        serial = Build.SERIAL;
                    }
                } else {
                    serial = "-1";
                    //Use device Id or use fallback case
                }
                dataProcessor.setImeIId(AppConstants.IMEIID, serial);
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
                        Log.e("serialId", serial);
                    } else {
                        serial = Build.SERIAL;
                    }
                } else {

//                    deviceImei = "-1";
                    serial = "-1";
                }
//                dataProcessor.setImeIId(AppConstants.IMEIID, deviceImei);
                dataProcessor.setImeIId(AppConstants.IMEIID, serial);
                Log.d("imei@@@", ": " + serial);
                return;
            }
        }
    }

}
