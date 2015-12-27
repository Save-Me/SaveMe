package com.example.chicharo.call_blocker.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
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
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class ContactsToBlockFragment extends android.support.v4.app.Fragment implements ContactsAdapter.onItemClickListener {
    ContactsAdapter contactsToBlockAdapter;
    List<ContactModel> contacts;

    public ContactsToBlockFragment() {
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
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            ContactModel contactModel = ContactToOwnContactModel(cursor);
            if(contactModel != null){
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
        int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if (hasPhoneNumber == 1) {
            contactModel.setContactName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            String PATTERN = "[^\\d]";
            Pattern pattern = Pattern.compile(PATTERN);
            String regularNumber = pattern.matcher(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).replaceAll("");
            contactModel.setPhoneNumber(regularNumber);
            contactModel.setImageUri(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
        } else {
            return null;
        }
        return contactModel;
    }

    @Override
    public void onItemClick(View v, int position) {
        EventBus.getDefault().post(new ContactEvent(contacts.get(position)));
        getActivity().finish();
    }
}
