package com.example.chicharo.call_blocker.models;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ContactModel extends RealmObject {

    @PrimaryKey
    private String phoneNumber;
    private String contactName;
    private String imageUri;
    private boolean checked;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
