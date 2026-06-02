package com.example.login_app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddressManager {
    private static AddressManager instance;
    private List<Address> addresses = new ArrayList<>();
    private AtomicInteger idGen = new AtomicInteger(1000);

    public static synchronized AddressManager getInstance() {
        if (instance == null) {
            instance = new AddressManager();
            instance.initSample();
        }
        return instance;
    }

    private void initSample() {
        addresses.add(new Address(idGen.getAndIncrement(),
                "张三", "13800138000", "浙江省", "杭州市", "余杭区",
                "文一西路969号阿里巴巴西溪园区", true));
    }

    public List<Address> getAddresses() {
        return new ArrayList<>(addresses);
    }

    public Address getById(int id) {
        for (Address a : addresses) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    public void add(Address addr) {
        addresses.add(addr);
    }

    public void update(int id, String name, String phone, String province,
                       String city, String district, String detail) {
        Address a = getById(id);
        if (a != null) {
            a.setName(name);
            a.setPhone(phone);
            a.setProvince(province);
            a.setCity(city);
            a.setDistrict(district);
            a.setDetail(detail);
        }
    }

    public void remove(int id) {
        Address target = null;
        for (Address a : addresses) {
            if (a.getId() == id) { target = a; break; }
        }
        if (target != null) addresses.remove(target);
    }

    public void setDefault(int id) {
        for (Address a : addresses) {
            a.setDefault(a.getId() == id);
        }
    }

    public int nextId() {
        return idGen.getAndIncrement();
    }
}
