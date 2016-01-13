package com.example.chicharo.call_blocker.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chicharo.call_blocker.R;
import com.example.chicharo.call_blocker.adapters.ContactsAdapter;
import com.example.chicharo.call_blocker.models.ContactModel;
import com.example.chicharo.call_blocker.service.ContactEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/*
* Copyright 2015 Emilio Ruano Noé

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-NOTICE-
This software makes use of Realm library (https://realm.io/) under the
Apache License, Version 2.0
This program makes use of Butterknife library (https://github.com/JakeWharton/butterknife) under the
Apache License, Version 2.0
*/
public class RecentCallsToBlockFragment extends android.support.v4.app.Fragment implements ContactsAdapter.onItemClickListener {
    ContactsAdapter contactsToBlockAdapter;
    List<ContactModel> contacts;

    public RecentCallsToBlockFragment() {
        //Required public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_choose_contacts_to_block, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactsToBlockAdapter = new ContactsAdapter(getContext(), getAllContacts());
        contactsToBlockAdapter.SetOnItemClickListener(this);
        recyclerView.setAdapter(contactsToBlockAdapter);
        return view;
    }

    public List<ContactModel> getAllContacts() {
        contacts = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return contacts;
        }
        Cursor cursor = getActivity().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
        while (cursor != null && cursor.moveToNext()) {
            ContactModel contactModel = ContactToOwnContactModel(cursor);
            if (contactModel != null) {
                if (!contacts.isEmpty()) {
                    if (contactModel.getPhoneNumber().equals(contacts.get(contacts.size() - 1).getPhoneNumber())) {
                        continue;
                    }
                }
                contacts.add(contactModel);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return contacts;
    }

    public ContactModel ContactToOwnContactModel(Cursor cursor) {
        ContactModel contactModel = new ContactModel();
        contactModel.setContactName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
        contactModel.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contactModel.setImageUri(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI)));
        }

        return contactModel;
    }


    @Override
    public void onItemClick(View v, int position) {
        EventBus.getDefault().post(new ContactEvent(contacts.get(position)));
        getActivity().finish();
    }
}
