package com.example.chicharo.call_blocker.models;

import io.realm.RealmObject;

public class PhoneModel extends RealmObject {
    private long _id;
    private String number;

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long get_id(){
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

}
