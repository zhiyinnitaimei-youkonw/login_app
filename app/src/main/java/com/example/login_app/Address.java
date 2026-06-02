package com.example.login_app;

import java.io.Serializable;

public class Address implements Serializable {
    private int id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private boolean isDefault;

    public Address(int id, String name, String phone, String province,
                   String city, String district, String detail, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.isDefault = isDefault;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getProvince() { return province; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getDetail() { return detail; }
    public boolean isDefault() { return isDefault; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProvince(String province) { this.province = province; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setDetail(String detail) { this.detail = detail; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getFullAddress() {
        return province + city + district + " " + detail;
    }
}
