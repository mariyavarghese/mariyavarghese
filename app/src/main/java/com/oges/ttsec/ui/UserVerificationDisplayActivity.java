package com.oges.ttsec.ui;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.oges.ttsec.R;
import com.oges.ttsec.adapter.AreaAdapter;
import com.oges.ttsec.adapter.PrevilegeAdapter;
import com.oges.ttsec.adapter.VenueAdapter;
import com.oges.ttsec.adapter.ZoneAdapter;
import com.oges.ttsec.model.ContactModel;
import com.oges.ttsec.network.ApiInterface;
import com.oges.ttsec.network.ApiService;
import com.oges.ttsec.network.CheckNetwork;
import com.oges.ttsec.util.AppConstants;
import com.oges.ttsec.util.DataProcessor;
import com.oges.ttsec.util.UserStatusCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserVerificationDisplayActivity extends AppCompatActivity {

    private String companyId, eventId, user_unique_id;
    private TextView tv_userName, tv_organization, tv_contactId, tv_badgeType, tv_category;
    private RecyclerView rv_zone, rv_venue, rv_previlege,rv_area;
    private ImageView iv_userProfilePic;
    private ApiInterface apiInterface;
    private CheckNetwork checkNetwork;
    private KProgressHUD hud;
    private BottomNavigationView bottomNavigationView;
    private boolean userStatus;
    private DataProcessor dataProcessor;
    private String userId;
    private String deviceImei;
    private NestedScrollView nsv_contact_view;
    private TelephonyManager mTelephonyManager;

    private String contact_pk_id;
    private TextView tx_checkin, tx_checkout;
    private Button btn_checkin, btn_checkout;
    private LinearLayout ll_checkin_checkout, ll_check_out;
    private LinearLayout ll_zone,ll_venue,ll_previlege,ll_area;
    private String checkViewStatus = "1";
    private int checkStatus;
    Menu menu;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress("verifying");
        setContentView(R.layout.activity_user_verification_display);

        apiInterface = ApiService.getClient().create(ApiInterface.class);
        checkNetwork = new CheckNetwork(UserVerificationDisplayActivity.this);
        dataProcessor = new DataProcessor(getApplicationContext());
        userId = dataProcessor.getUserId(AppConstants.USERID);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        Bundle bundle = getIntent().getExtras();
        companyId = bundle.getString("companyId");
        eventId = bundle.getString("eventId");
        user_unique_id = bundle.getString("user_unique_id");
        deviceImei = bundle.getString("imei_number");

        if (deviceImei == null || deviceImei.isEmpty()) {
            deviceImei = "-1";
        }

        Log.d("Jithu_Imei", " " + deviceImei);

        //Log.d("IMEINumber", " " + deviceImei);
        //Toast.makeText(UserVerificationDisplayActivity.this, "IMEI No"+deviceImei, Toast.LENGTH_SHORT).show();
        Log.d("variables$$$", "" + companyId + " " + eventId + " " + user_unique_id);
        initViews();

        if (checkNetwork.isNetworkConnected(UserVerificationDisplayActivity.this)) {
            displayUserProfile(); // for scanned details display
            String deviceStatus = dataProcessor.getDeviceStatus(AppConstants.DEVICESTATUS);
            Log.d("dev_status", " :" + deviceStatus);
            System.out.println("dev_status" + deviceStatus);
            if (deviceStatus.equals("0")) {
                Toast.makeText(UserVerificationDisplayActivity.this, "Device not registered", Toast.LENGTH_SHORT).show();
                Log.d("DEVICESTATUS0", " :" + deviceStatus);
            } else if (deviceStatus.equals("1")) {
                //Toast.makeText(UserVerificationDisplayActivity.this, "Device registered", Toast.LENGTH_SHORT).show();
                Log.d("DEVICESTATUS1", " :" + deviceStatus);
            }
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_scanNext:
                            checkUserStatus(userId, new UserStatusCallback() {
                                @Override
                                public void onSuccess(boolean value) {
                                    if (value) {
                                        Toast.makeText(UserVerificationDisplayActivity.this, "Scan next", Toast.LENGTH_SHORT).show();
                                        Intent scanintent = new Intent(UserVerificationDisplayActivity.this, ScannedBarcodeActivity.class);
                                        scanintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(scanintent);
                                        finish();
                                    } else {
                                        dataProcessor.clear();
                                        showSnackbar(getString(R.string.user_status_error));
                                        Intent logoutIntent = new Intent(UserVerificationDisplayActivity.this, LoginActivity.class);
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
                            // Toast.makeText(UserVerificationDisplayActivity.this, "Exit", Toast.LENGTH_SHORT).show();
                            Intent logoutIntent = new Intent(UserVerificationDisplayActivity.this, EventSelectActivity.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logoutIntent);
                            finish();
                            break;
                        case R.id.menu_check:
                            CharSequence title = menu.findItem(R.id.menu_check).getTitle();
                            Log.e("@title", "=" + title);
                            if (title.toString().equalsIgnoreCase("Check In")) {
                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                Log.e("@dd", "date=" + date);
                                showProgress("Loading...");
                                callCheckInCheckOut(date, "0");
                            } else if (title.toString().equalsIgnoreCase("Check Out")) {
                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                Log.e("@dd", "date=" + date);
                                showProgress("Loading...");
                                callCheckInCheckOut(date, "1");
                            }
                            break;
                        case R.id.menu_verified:
                            CharSequence ver = menu.findItem(R.id.menu_verified).getTitle();
                            Log.e("@title", "=" + ver);
                            if (ver.toString().equalsIgnoreCase("Verified")) {
                                showProgress("Loading...");
                                callVerification("1");
                            } else if (ver.toString().equalsIgnoreCase("Unverified")) {
                                showProgress("Loading...");
                                callVerification("0");
                            }
                            break;
                    }
                    return true;
                }
            });

        } else {
            showSnackbar(getString(R.string.network_error));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent scanintent = new Intent(UserVerificationDisplayActivity.this, ScannedBarcodeActivity.class);
        scanintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(scanintent);
        finish();
    }

    private void initViews() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        menu = bottomNavigationView.getMenu();

        nsv_contact_view = findViewById(R.id.nsv_contact_view);

        tv_userName = findViewById(R.id.tv_username);
        tv_organization = findViewById(R.id.tv_organization);
        tv_contactId = findViewById(R.id.tv_contactId);
        tv_badgeType = findViewById(R.id.tv_badgeType);
        tv_category = findViewById(R.id.tv_category);
        rv_zone = findViewById(R.id.rv_zone);
        rv_venue = findViewById(R.id.rv_venue);
        rv_previlege = findViewById(R.id.rv_previlege);
        rv_area = findViewById(R.id.rv_area);
        iv_userProfilePic = findViewById(R.id.iv_userProfilePic);
        ll_checkin_checkout = findViewById(R.id.ll_checkin_checkout);
        ll_check_out = findViewById(R.id.ll_check_out);
        ll_zone = findViewById(R.id.ll_zone);
        ll_venue = findViewById(R.id.ll_venue);
        ll_previlege = findViewById(R.id.ll_previlege);
        ll_area = findViewById(R.id.ll_area);

        tx_checkin = findViewById(R.id.tx_checkin);
        tx_checkout = findViewById(R.id.tx_checkout);
        btn_checkin = findViewById(R.id.btn_checkin);
        btn_checkout = findViewById(R.id.btn_checkout);
        tx_checkin.setVisibility(View.GONE);
        tx_checkout.setVisibility(View.GONE);
        btn_checkin.setVisibility(View.GONE);
        btn_checkout.setVisibility(View.GONE);


        ll_checkin_checkout.setVisibility(View.GONE);
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

    private void callCheckInCheckOut(String check_date_time, final String status) {
        Call<JsonObject> call = apiInterface.checkInCheckOut(contact_pk_id, userId, eventId, user_unique_id, deviceImei,
                status, check_date_time);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    String status = jsonObject.get("status").toString();
                    status = status.replaceAll("^\"|\"$", "");
                    if (status.equals("1")) {
                        String message = jsonObject.get("message").toString();
                        message = message.replaceAll("^\"|\"$", "");
                        showSnackbar(message);
                        btn_checkin.setVisibility(View.GONE);
                        btn_checkout.setVisibility(View.GONE);
                        String check_status = jsonObject.get("check_status").toString();
                        check_status = check_status.replaceAll("^\"|\"$", "");
                        if (check_status.equals("0")) {
                            String check_in_time = jsonObject.get("check_in_time").toString();
                            tx_checkin.setText(getLocalTimeFromUtcTime(check_in_time.replaceAll("^\"|\"$", "")));
                            tx_checkin.setVisibility(View.VISIBLE);
                            menu.findItem(R.id.menu_check).setVisible(false);
                            if (dataProcessor.getConfigStatus("CONFIG_STATUS").equals("1")) {
                                menu.findItem(R.id.menu_verified).setVisible(true);
                            }
                            ll_check_out.setVisibility(View.GONE);
                            ll_checkin_checkout.setVisibility(View.VISIBLE);

                            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                            Display display = wm.getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int width = size.x;
                            int height = size.y;
                            int w1 = width / 2;
                            int h1 = height / 6;
                            int h2 = height / 10;
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsv_contact_view.getLayoutParams();
                            layoutParams.setMargins(0, h2, 0, 80);
                            nsv_contact_view.setLayoutParams(layoutParams);

                        } else if (check_status.equals("1")) {
                            String check_in_time = jsonObject.get("check_in_time").toString();
                            tx_checkin.setText(getLocalTimeFromUtcTime(check_in_time.replaceAll("^\"|\"$", "")));
                            String check_out_time = jsonObject.get("check_out_time").toString();
                            tx_checkout.setText(getLocalTimeFromUtcTime(check_out_time.replaceAll("^\"|\"$", "")));
                            tx_checkin.setVisibility(View.VISIBLE);
                            tx_checkout.setVisibility(View.VISIBLE);
                            ll_check_out.setVisibility(View.VISIBLE);
                            menu.findItem(R.id.menu_check).setVisible(false);
                            menu.findItem(R.id.menu_verified).setVisible(false);
                        }
                    } else {
                        Log.e("@error", "=" + jsonObject.get("message").toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                showSnackbar("Something went wronng...Try again...");
            }
        });
    }

    private void callVerification(String verification_status) {
        Call<JsonObject> call = apiInterface.verification(userId, eventId, user_unique_id, deviceImei,
                verification_status);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    String status = jsonObject.get("status").toString();
                    status = status.replaceAll("^\"|\"$", "");
                    if (status.equals("1")) {
                        String message = jsonObject.get("message").toString();
                        message = message.replaceAll("^\"|\"$", "");
                        showSnackbar(message);

                        String verification_status = jsonObject.get("verification_status").toString();
                        verification_status = verification_status.replaceAll("^\"|\"$", "");
                        if (verification_status.equals("1")) {

                            menu.findItem(R.id.menu_verified).setIcon(R.drawable.ic_verified);
                            menu.findItem(R.id.menu_verified).setTitle("Verified");

                        } else if (verification_status.equals("0")) {
                            menu.findItem(R.id.menu_verified).setIcon(R.drawable.ic_not_verified);
                            menu.findItem(R.id.menu_verified).setTitle("Unverified");
                        }
                    } else {
                        Log.e("@error", "=" + jsonObject.get("message").toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                showSnackbar("Something went wronng...Try again...");
            }
        });
    }

    private void displayUserProfile() {
        // showProgress(getString(R.string.loading));
        Call<ContactModel> call = apiInterface.userProfileDisplay(userId, eventId, user_unique_id, deviceImei);
        call.enqueue(new Callback<ContactModel>() {
            @Override
            public void onResponse(Call<ContactModel> call, Response<ContactModel> response) {
                if (response.body() != null) {
                    Log.d("variables$$$", "" + companyId + " " + eventId + " " + user_unique_id);
                    ContactModel contactModel = response.body();
                    Log.d("response", " " + response.body().toString());

                    if (contactModel != null) {

                        if (contactModel.getCode().contentEquals("0")) {
                            hud.dismiss();
                            Intent badgeInactiveIntent = new Intent(UserVerificationDisplayActivity.this, BadgeInactiveActivity.class);
                            startActivity(badgeInactiveIntent);
                        } else if (contactModel.getCode().contentEquals("1")) {

                            String proPicStatus = contactModel.getUser_profile_pic_status().toString();
                            if (proPicStatus.contentEquals("0")) { //No profile pic
                                Glide.with(UserVerificationDisplayActivity.this)
                                        .asBitmap()
                                        .load(R.drawable.profile_pic)
                                        .transition(withCrossFade())
                                        .apply(new RequestOptions()
                                                .placeholder(R.drawable.user_placeholder)
                                                .fitCenter())
                                        .into(iv_userProfilePic);
                            } else if (proPicStatus.contentEquals("1")) { //pro pic available
//                                String userPic = contactModel.getUser_profile_pic().getFile_files(); //commented 22.11.2020
                                //Above line commented as the image is displayed using its URL
                                String userPic = contactModel.getImage_url().toString();
                                displayUserprofilePic(userPic);
                            }
                            tv_userName.setText(contactModel.getUser_full_name());
                            tv_organization.setText(contactModel.getUser_org_name());
                            tv_contactId.setText(contactModel.getUser_contact_id());
                            tv_badgeType.setText(contactModel.getUser_badge_name());
                            tv_category.setText(contactModel.getUser_category());

                            List<String> zones = contactModel.getZones();
                            if(zones.isEmpty()){
                                rv_zone.setVisibility(View.GONE);
                                ll_zone.setVisibility(View.GONE);
                            }else{
                                ZoneAdapter zoneAdapter = new ZoneAdapter(UserVerificationDisplayActivity.this, zones);
                                rv_zone.setAdapter(zoneAdapter);
                                rv_zone.setHasFixedSize(true);
                                rv_zone.setLayoutManager(new GridLayoutManager(UserVerificationDisplayActivity.this, 1));
                            }

                            List<String> venues = contactModel.getVenues();
                            if(venues.isEmpty()){
                                rv_venue.setVisibility(View.GONE);
                                ll_venue.setVisibility(View.GONE);
                            }else{
                                VenueAdapter venueAdapter = new VenueAdapter(UserVerificationDisplayActivity.this, venues);
                                rv_venue.setAdapter(venueAdapter);
                                rv_venue.setHasFixedSize(true);
                                rv_venue.setLayoutManager(new GridLayoutManager(UserVerificationDisplayActivity.this, 1));
                            }

                            List<String> privileges = contactModel.getPrivileges();
                            if(privileges.isEmpty()){
                                rv_previlege.setVisibility(View.GONE);
                                ll_previlege.setVisibility(View.GONE);
                            }else{
                                PrevilegeAdapter previlegeAdapter = new PrevilegeAdapter(UserVerificationDisplayActivity.this, privileges);
                                rv_previlege.setAdapter(previlegeAdapter);
                                rv_previlege.setHasFixedSize(true);
                                rv_previlege.setLayoutManager(new GridLayoutManager(UserVerificationDisplayActivity.this, 1));
                            }

                            List<String> area = contactModel.getArea();
                            if(area.isEmpty()){
                                rv_area.setVisibility(View.GONE);
                                ll_area.setVisibility(View.GONE);
                            }else{
                                AreaAdapter areaAdapter = new AreaAdapter(UserVerificationDisplayActivity.this, area);
                                rv_area.setAdapter(areaAdapter);
                                rv_area.setHasFixedSize(true);
                                rv_area.setLayoutManager(new GridLayoutManager(UserVerificationDisplayActivity.this, 1));
                            }

                            nsv_contact_view.setVisibility(View.VISIBLE);
                            contact_pk_id = contactModel.getContact_pk_id();
                            if (contactModel.getCheck_status().equals("1")) { //Already checked in, now going to checkout
//                                Toast.makeText(UserVerificationDisplayActivity.this, "Check out", Toast.LENGTH_SHORT).show();
                                tx_checkin.setText(getLocalTimeFromUtcTime(contactModel.getCheck_date_time()));
                                tx_checkin.setVisibility(View.VISIBLE);
//                                btn_checkout.setVisibility(View.VISIBLE);
                                menu.findItem(R.id.menu_check).setTitle(R.string.check_out);
                                menu.findItem(R.id.menu_check).setIcon(R.drawable.ic_check_out);
                                checkStatus = 1;
                                if (dataProcessor.getConfigStatus("CONFIG_STATUS").equals("1")) {
                                    menu.findItem(R.id.menu_verified).setVisible(true);
                                }
                            } else if (contactModel.getCheck_status().equals("0")) { //check in
//                                btn_checkin.setVisibility(View.VISIBLE);
                                menu.findItem(R.id.menu_check).setTitle(R.string.check_in);
                                menu.findItem(R.id.menu_check).setIcon(R.drawable.ic_check_in);
                                checkStatus = 0;

                                menu.findItem(R.id.menu_verified).setVisible(false);
                            }
                            if (dataProcessor.getConfigStatus("CONFIG_STATUS").equals("1")) {
                                menu.findItem(R.id.menu_check).setVisible(true);
                                if (checkStatus == 1) {
                                    ll_checkin_checkout.setVisibility(View.VISIBLE);
                                    ll_check_out.setVisibility(View.GONE);
                                } else {
                                    ll_checkin_checkout.setVisibility(View.GONE);
                                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsv_contact_view.getLayoutParams();
                                    layoutParams.setMargins(0, 10, 0, 80);
                                    nsv_contact_view.setLayoutParams(layoutParams);
                                }
                                if (contactModel.getVerification_status().equals("1")) { //show not verfied icon
                                    menu.findItem(R.id.menu_verified).setIcon(R.drawable.ic_not_verified);
                                    menu.findItem(R.id.menu_verified).setTitle("Unverified");
                                } else { //show verified icon
                                    menu.findItem(R.id.menu_verified).setIcon(R.drawable.ic_verified);
                                    menu.findItem(R.id.menu_verified).setTitle("Verified");
                                }
                            } else {
                                ll_checkin_checkout.setVisibility(View.GONE);
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsv_contact_view.getLayoutParams();
                                layoutParams.setMargins(0, 10, 0, 80);
                                nsv_contact_view.setLayoutParams(layoutParams);

                                menu.findItem(R.id.menu_check).setVisible(false);
                            }
                            hud.dismiss();

                        }
                        String verification_code = "";
                        verification_code = dataProcessor.getVerificationCode(AppConstants.VERIFICATIONCODE);
                        Log.d("vCode", "" + verification_code);
                        if (verification_code.contentEquals("1")) { // User already verified
                            showSnackbar(getString(R.string.badge_already_verified));
                        }

                    } else {
                        hud.dismiss();
                        showSnackbar(getString(R.string.network_error));
                    }
                } else {
                    hud.dismiss();
                    Intent badgeInactiveIntent = new Intent(UserVerificationDisplayActivity.this, BadgeInactiveActivity.class);
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


    private void displayUserprofilePic(String userPic) {
//        final String encodedString = userPic;
//        final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",") + 1);
//        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        //Above 4 lines commented as the image is displayed using its URL
        Glide.with(UserVerificationDisplayActivity.this)
                .asBitmap()
                .load(userPic)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.user_placeholder)
                        .fitCenter())
                .into(iv_userProfilePic);
    }

    private String getLocalTimeFromUtcTime(String dateStr) {
//        String dateStr = "Jul 16, 2013 12:08:59 AM";
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);
        Log.e("@local@", "=" + formattedDate);
        return formattedDate;
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
//        Snackbar.make(findViewById(android.R.id.content),
//                message,
//                Snackbar.LENGTH_SHORT).show();
        Toast toast= Toast.makeText(UserVerificationDisplayActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 40);
        toast.show();
    }

}
