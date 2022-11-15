package com.oges.ttsec.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oges.ttsec.R;
import com.oges.ttsec.network.ApiInterface;
import com.oges.ttsec.network.ApiService;
import com.oges.ttsec.network.CheckNetwork;
import com.oges.ttsec.util.AppConstants;
import com.oges.ttsec.util.DataProcessor;
import com.oges.ttsec.util.UserStatusCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    private static int SPLASH_TIME_OUT = 3000;
    String user_id = "";
    private ApiInterface apiInterface;
    private CheckNetwork checkNetwork;
    private DataProcessor dataProcessor;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = (ImageView) findViewById(R.id.logo);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        dataProcessor = new DataProcessor(getApplicationContext());
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        checkNetwork = new CheckNetwork(SplashActivity.this);
        userId = dataProcessor.getToken(AppConstants.USERID);

        Animation zoom_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        logo.setAnimation(zoom_out);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 2, width / 2);
        logo.setLayoutParams(params);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkNetwork.isNetworkConnected(SplashActivity.this)) {
                    if (userId != null) {
                        checkUserStatus(userId, new UserStatusCallback() {
                            @Override
                            public void onSuccess(boolean value) {
                                if (value) {
                                    // Toast.makeText(SplashActivity.this, "Active", Toast.LENGTH_SHORT).show();
                                    loginIntent();
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                } else {
                                    showSnackbar(getString(R.string.user_status_error));
                                }
                            }

                            @Override
                            public void onError() {
                                showSnackbar(getString(R.string.network_error));
                            }
                        });

                    } else {
                        finish();
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                } else {
//                    showSnackbar(getString(R.string.network_error));
                    showNetworkErrorSnackbar();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void loginIntent() {
        Intent intent = new Intent(SplashActivity.this, EventSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT).show();
    }

    private void showNetworkErrorSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Enable Network & press OK", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkNetwork.isNetworkConnected(SplashActivity.this)) {
                            if (userId != null) {
                                checkUserStatus(userId, new UserStatusCallback() {
                                    @Override
                                    public void onSuccess(boolean value) {
                                        if (value) {
                                            // Toast.makeText(SplashActivity.this, "Active", Toast.LENGTH_SHORT).show();
                                            loginIntent();
                                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                        } else {
                                            showSnackbar(getString(R.string.user_status_error));
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        showSnackbar(getString(R.string.network_error));
                                    }
                                });

                            } else {
                                finish();
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }
                        } else {
                            showNetworkErrorSnackbar();
                        }
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.yellow))
                .show();
    }
}
