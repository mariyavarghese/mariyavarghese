
package com.oges.ttsec.network;


import com.google.gson.JsonObject;
import com.oges.ttsec.model.ContactModel;
import com.oges.ttsec.model.EventModel;
import com.oges.ttsec.model.LoginModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> loginUser(@Field("user_name") String str_username,
                               @Field("password") String str_password);


    @FormUrlEncoded
    @POST("event_listing")
    Call<EventModel> getEventList(@Field("company_id") String companyId,
                                  @Field("event_id") String eventId);


    @FormUrlEncoded
    @POST("contact_listing")
    Call<ContactModel> userProfileDisplay(@Field("company_id") String companyId,
                                          @Field("event_id") String eventId,
                                          @Field("user_unique_id") String userUniqueId,
                                          @Field("imei_number") String deviceImei);

    @FormUrlEncoded
    @POST("contact_exist")
    Call<JsonObject> checkScanStatus(@Field("company_id") String companyId,
                                     @Field("event_id") String eventId,
                                     @Field("user_unique_id") String userUniqueId,
                                     @Field("imei_number") String deviceImei,
                                     @Field("contact_unique_check") String contactuniquecheck);

    @FormUrlEncoded
    @POST("company_status")
    Call<JsonObject> checkUserStatus(@Field("user_id") String userId);

    @FormUrlEncoded
    @POST("check_in_check_out")
    Call<JsonObject> checkInCheckOut(@Field("contact_pk_id") String contactPkId,
                                     @Field("company_id") String companyId,
                                     @Field("event_id") String eventId,
                                     @Field("user_unique_id") String userUniqueId,
                                     @Field("imei_number") String deviceImei,
                                     @Field("check_status") String check_status,
                                     @Field("check_date_time") String check_date_time);

    @FormUrlEncoded
    @POST("verification_status")
    Call<JsonObject> verification(@Field("company_id") String companyId,
                                     @Field("event_id") String eventId,
                                     @Field("user_unique_id") String userUniqueId,
                                     @Field("imei_number") String deviceImei,
                                     @Field("verification_status") String verification_status);


}

