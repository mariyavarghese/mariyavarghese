package com.oges.ttsec.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {


    @Expose
    @SerializedName("company_details")
    private Company_details company_details;
    @Expose
    @SerializedName("user_id")
    private int user_id;
    @Expose
    @SerializedName("login_status")
    private String login_status;

    public Company_details getCompany_details() {
        return company_details;
    }

    public void setCompany_details(Company_details company_details) {
        this.company_details = company_details;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLogin_status() {
        return login_status;
    }

    public void setLogin_status(String login_status) {
        this.login_status = login_status;
    }

    public static class Company_details {
        @Expose
        @SerializedName("updated_at")
        private String updated_at;
        @Expose
        @SerializedName("created_at")
        private String created_at;
        @Expose
        @SerializedName("c_logo_file")
        private String c_logo_file;
        @Expose
        @SerializedName("c_serial_number")
        private int c_serial_number;
        @Expose
        @SerializedName("c_email")
        private String c_email;
        @Expose
        @SerializedName("c_crlimit")
        private int c_crlimit;
        @Expose
        @SerializedName("c_status")
        private int c_status;
        @Expose
        @SerializedName("c_cat")
        private int c_cat;
        @Expose
        @SerializedName("c_phone")
        private String c_phone;
        @Expose
        @SerializedName("c_street")
        private String c_street;
        @Expose
        @SerializedName("c_address1")
        private String c_address1;
        @Expose
        @SerializedName("c_name")
        private String c_name;
        @Expose
        @SerializedName("c_cucat_id")
        private int c_cucat_id;
        @Expose
        @SerializedName("c_group")
        private int c_group;
        @Expose
        @SerializedName("c_business_type_id")
        private int c_business_type_id;
        @Expose
        @SerializedName("c_cy_id")
        private String c_cy_id;
        @Expose
        @SerializedName("id")
        private int id;

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getC_logo_file() {
            return c_logo_file;
        }

        public void setC_logo_file(String c_logo_file) {
            this.c_logo_file = c_logo_file;
        }

        public int getC_serial_number() {
            return c_serial_number;
        }

        public void setC_serial_number(int c_serial_number) {
            this.c_serial_number = c_serial_number;
        }

        public String getC_email() {
            return c_email;
        }

        public void setC_email(String c_email) {
            this.c_email = c_email;
        }

        public int getC_crlimit() {
            return c_crlimit;
        }

        public void setC_crlimit(int c_crlimit) {
            this.c_crlimit = c_crlimit;
        }

        public int getC_status() {
            return c_status;
        }

        public void setC_status(int c_status) {
            this.c_status = c_status;
        }

        public int getC_cat() {
            return c_cat;
        }

        public void setC_cat(int c_cat) {
            this.c_cat = c_cat;
        }

        public String getC_phone() {
            return c_phone;
        }

        public void setC_phone(String c_phone) {
            this.c_phone = c_phone;
        }

        public String getC_street() {
            return c_street;
        }

        public void setC_street(String c_street) {
            this.c_street = c_street;
        }

        public String getC_address1() {
            return c_address1;
        }

        public void setC_address1(String c_address1) {
            this.c_address1 = c_address1;
        }

        public String getC_name() {
            return c_name;
        }

        public void setC_name(String c_name) {
            this.c_name = c_name;
        }

        public int getC_cucat_id() {
            return c_cucat_id;
        }

        public void setC_cucat_id(int c_cucat_id) {
            this.c_cucat_id = c_cucat_id;
        }

        public int getC_group() {
            return c_group;
        }

        public void setC_group(int c_group) {
            this.c_group = c_group;
        }

        public int getC_business_type_id() {
            return c_business_type_id;
        }

        public void setC_business_type_id(int c_business_type_id) {
            this.c_business_type_id = c_business_type_id;
        }

        public String getC_cy_id() {
            return c_cy_id;
        }

        public void setC_cy_id(String c_cy_id) {
            this.c_cy_id = c_cy_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
