package com.oges.ttsec.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static Retrofit retrofit = null;
//    private static final String LOCAL_URL = "http://ogesindia.myfirewall.co:8081/ttseceventservices/public/api/";
//    private static final String LOCAL_URL_COPA = "http://ogesindia.ddns.net:8081/ttseceventservices/public/api/"; //test copa america
//    private static final String LOCAL_URL = "http://ogesindia.ddns.net:8086/ttseceventservices/public/api/"; //test saudi
//    private static final String LOCAL_URL = "http://ogesindia.ddns.net:8086/ttsec_accreditation/public/api/"; //test saudi
    //    private static final String LOCAL_URL = "http://192.168.10.57:8081/ttseceventservices/public/api/";
//    private static final String LOCAL_URL = "https://sandbox.ttsec-ess.com/api/";
//    private static final String LOCAL_URL = "https://ttsec2.ttsec-ess.com/api/";
//    private static final String LOCAL_URL = "https://sandbox.venue.ttsec-ess.com/api/";
    private static final String LOCAL_URL = "https://venues.ttsec-ess.com/api/";
//    private static final String LOCAL_URL = "https://acr.nec.gov.sa/api/";
//    private static final String LOCAL_URL = "http://ogesindia.ddns.net:8086/ttsec_accreditation/public/";
//    private static final String LOCAL_URL = "https://sandbox.ttsec-ess.com/api/";
//    private static final String LOCAL_URL = "https://ttsec2.ttsec-ess.com/api/";
//    private static final String LOCAL_URL = "https://sandbox.venue.ttsec-ess.com/api/";
//    private static final String TEST_BASE_URL = "http://sandbox.tt-sec.com/api/";
//    private static final String TEST_BASE_URL = "https://ttsec-ess.com/work_test/api/";
//    private static final String BASE_URL1 = "https://ttsec-ess.com/api/"; // live link (copa america)
//    private static final String BASE_URL = "https://acr.nec.gov.sa/api/"; //saudi URL
public static String FLASH_IDNO = "FLASH";
    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS).addInterceptor(interceptor).build();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(LOCAL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}
