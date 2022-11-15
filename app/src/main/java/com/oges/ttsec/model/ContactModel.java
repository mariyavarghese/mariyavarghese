package com.oges.ttsec.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactModel {


    @Expose
    @SerializedName("user_profile_pic")
    private User_profile_pic user_profile_pic;
    @Expose
    @SerializedName("privileges")
    private List<String> privileges;
    @Expose
    @SerializedName("venues")
    private List<String> venues;
    @Expose
    @SerializedName("zones")
    private List<String> zones;
    @Expose
    @SerializedName("area")
    private List<String> area;
    @Expose
    @SerializedName("user_category")
    private String user_category;
    @Expose
    @SerializedName("user_badge_name")
    private String user_badge_name;
    @Expose
    @SerializedName("user_org_name")
    private String user_org_name;
    @Expose
    @SerializedName("user_contact_id")
    private String user_contact_id;
    @Expose
    @SerializedName("user_full_name")
    private String user_full_name;
    @Expose
    @SerializedName("code")
    private String code;
    @Expose
    @SerializedName("user_profile_pic_status")
    private String user_profile_pic_status;

    //Added by JSA 22.11.2020
    @Expose
    @SerializedName("image_url")
    private String image_url;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    //Added by JSA 27.04.2021
    @Expose
    @SerializedName("contact_pk_id")
    private String contact_pk_id;
    @Expose
    @SerializedName("check_status")
    private String check_status;
    @Expose
    @SerializedName("check_date_time")
    private String check_date_time;
    @Expose
    @SerializedName("verification_status")
    private String verification_status;

    public String getVerification_status() {
        return verification_status;
    }

    public void setVerification_status(String verification_status) {
        this.verification_status = verification_status;
    }

    public String getCheck_date_time() {
        return check_date_time;
    }

    public void setCheck_date_time(String check_date_time) {
        this.check_date_time = check_date_time;
    }

    public String getContact_pk_id() {
        return contact_pk_id;
    }

    public void setContact_pk_id(String contact_pk_id) {
        this.contact_pk_id = contact_pk_id;
    }

    public String getCheck_status() {
        return check_status;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public ContactModel(User_profile_pic user_profile_pic, List<String> privileges, List<String> venues, List<String> zones,List<String> area, String user_category, String user_badge_name, String user_org_name, String user_contact_id, String user_full_name, String code, String user_profile_pic_status,
                        String image_url,String contact_pk_id,String check_status,String check_date,String check_time) {
        this.user_profile_pic = user_profile_pic;
        this.privileges = privileges;
        this.venues = venues;
        this.zones = zones;
        this.area = area;
        this.user_category = user_category;
        this.user_badge_name = user_badge_name;
        this.user_org_name = user_org_name;
        this.user_contact_id = user_contact_id;
        this.user_full_name = user_full_name;
        this.code = code;
        this.user_profile_pic_status = user_profile_pic_status;
        this.image_url = image_url;

        this.contact_pk_id = contact_pk_id;
        this.check_status = check_status;
        this.check_date_time = check_date_time;
    }

    //....................................................................
    public ContactModel(String code) {
        this.code = code;
    }

    public ContactModel(User_profile_pic user_profile_pic, List<String> privileges, List<String> venues, List<String> zones, String user_category, String user_badge_name, String user_org_name, String user_contact_id, String user_full_name, String code) {
        this.user_profile_pic = user_profile_pic;
        this.privileges = privileges;
        this.venues = venues;
        this.zones = zones;
        this.user_category = user_category;
        this.user_badge_name = user_badge_name;
        this.user_org_name = user_org_name;
        this.user_contact_id = user_contact_id;
        this.user_full_name = user_full_name;
        this.code = code;

    }

    public ContactModel(User_profile_pic user_profile_pic, List<String> privileges, List<String> venues, List<String> zones, String user_category, String user_badge_name, String user_org_name, String user_contact_id, String user_full_name, String code, String user_profile_pic_status) {
        this.user_profile_pic = user_profile_pic;
        this.privileges = privileges;
        this.venues = venues;
        this.zones = zones;
        this.user_category = user_category;
        this.user_badge_name = user_badge_name;
        this.user_org_name = user_org_name;
        this.user_contact_id = user_contact_id;
        this.user_full_name = user_full_name;
        this.code = code;
        this.user_profile_pic_status = user_profile_pic_status;
    }
//    public ContactModel(User_profile_pic user_profile_pic, List<String> privileges, List<String> venues, List<String> zones, String user_category, String user_badge_name, String user_org_name, String user_contact_id, String user_full_name, String code) {
//        this.user_profile_pic = user_profile_pic;
//        this.privileges = privileges;
//        this.venues = venues;
//        this.zones = zones;
//        this.user_category = user_category;
//        this.user_badge_name = user_badge_name;
//        this.user_org_name = user_org_name;
//        this.user_contact_id = user_contact_id;
//        this.user_full_name = user_full_name;
//        this.code = code;
//
//    }

    public User_profile_pic getUser_profile_pic() {
        return user_profile_pic;
    }

    public void setUser_profile_pic(User_profile_pic user_profile_pic) {
        this.user_profile_pic = user_profile_pic;
    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public List<String> getVenues() {
        return venues;
    }

    public void setVenues(List<String> venues) {
        this.venues = venues;
    }

    public List<String> getZones() {
        return zones;
    }

    public void setZones(List<String> zones) {
        this.zones = zones;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public String getUser_category() {
        return user_category;
    }

    public void setUser_category(String user_category) {
        this.user_category = user_category;
    }

    public String getUser_badge_name() {
        return user_badge_name;
    }

    public void setUser_badge_name(String user_badge_name) {
        this.user_badge_name = user_badge_name;
    }

    public String getUser_org_name() {
        return user_org_name;
    }

    public void setUser_org_name(String user_org_name) {
        this.user_org_name = user_org_name;
    }

    public String getUser_contact_id() {
        return user_contact_id;
    }

    public void setUser_contact_id(String user_contact_id) {
        this.user_contact_id = user_contact_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUser_profile_pic_status() {
        return user_profile_pic_status;
    }

    public void setUser_profile_pic_status(String user_profile_pic_status) {
        this.user_profile_pic_status = user_profile_pic_status;
    }
//    public String getVerification_code() {
//        return verification_code;
//    }
//
//    public void setVerification_code(String verification_code) {
//        this.verification_code = verification_code;
//    }

    public static class User_profile_pic {
        @Expose
        @SerializedName("file_files")
        private String file_files;
        @Expose
        @SerializedName("file_mime_type")
        private String file_mime_type;
        @Expose
        @SerializedName("file_name")
        private String file_name;

        public String getFile_files() {
            return file_files;
        }

        public void setFile_files(String file_files) {
            this.file_files = file_files;
        }

        public String getFile_mime_type() {
            return file_mime_type;
        }

        public void setFile_mime_type(String file_mime_type) {
            this.file_mime_type = file_mime_type;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }
    }
}



