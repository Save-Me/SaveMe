package com.example.chicharo.call_blocker.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chicharo.call_blocker.R;
import com.example.chicharo.call_blocker.activities.ChooseContactsToBlock;
import com.example.chicharo.call_blocker.models.ContactModel;
import com.example.chicharo.call_blocker.service.ContactEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;

public class AddToMyBlackListFragment extends DialogFragment implements View.OnClickListener {
    private static final String regexIsAValidPhoneNumber = "^[0-9]{8,12}$";
    private static final int REQUEST_READ_CALL_LOG = 0;
    private static final int REQUEST_READ_CONTACTS = 0;

    @Bind(R.id.edit_newPhone)
    EditText editTextAddNewPhone;
    @Bind(R.id.btn_newPhone)
    Button btnAddNewPhone;
    @Bind(R.id.btn_DeleteAll)
    Button btnDeleletePhone;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_block_contact)
    ImageButton btnBlockContact;

    public AddToMyBlackListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_to_blacklist, container, false);
        ButterKnife.bind(this, view);
        getDialog().setTitle(R.string.add_contact_dialog);
        setupButtons();
        return view;
    }

    private void setupButtons() {
        btnAddNewPhone.setOnClickListener(this);
        btnDeleletePhone.setOnClickListener(this);
        btnBlockContact.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_newPhone) {
            if (editTextAddNewPhone.getText().toString().matches(regexIsAValidPhoneNumber)) {
                String phone = editTextAddNewPhone.getText().toString();
                ContactModel rT = new ContactModel();
                rT.setPhoneNumber(phone);
                EventBus.getDefault().post(new ContactEvent(rT));
                dismiss();
            } else {
                Toast.makeText(getContext(), "This is not a valid phone number",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_DeleteAll) {
            editTextAddNewPhone.setText("");
        } else if (v.getId() == R.id.btn_block_contact) {
            if (!readContactsPermit()) {
                Toast.makeText(getContext(), R.string.permissions_denied, Toast.LENGTH_LONG).show();
                return;
            }
            if (!readCallLogsPermit()) {
                Toast.makeText(getContext(), R.string.permissions_denied, Toast.LENGTH_LONG).show();
                return;
            }
            Intent blockContactActivity = new Intent(getContext(), ChooseContactsToBlock.class);
            startActivity(blockContactActivity);
            dismiss();
        } else if (v.getId() == R.id.btn_cancel) {
            dismiss();
        }
    }

    private boolean readCallLogsPermit() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CALL_LOG)) {
            Snackbar.make(editTextAddNewPhone, R.string.permission_read_call_logs, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
        }
        return false;
    }

    private boolean readContactsPermit() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(editTextAddNewPhone, R.string.permission_read_contacts, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
    }

}
