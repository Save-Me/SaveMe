package com.example.chicharo.call_blocker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chicharo.call_blocker.adapters.ContactAdapter;
import com.example.chicharo.call_blocker.dataBases.ContactsDataSource;
import com.example.chicharo.call_blocker.models.ContactModel;
import com.example.chicharo.call_blocker.R;

import java.util.ArrayList;
import java.util.List;

public class myBlackList extends ActionBarActivity implements View.OnClickListener{
    ContactsDataSource contactsDataSource;
    private static final String regexIsAValidPhoneNumber = "^[0-9]{8,12}$";
    private EditText editTextAddNewPhone;
    private ContactAdapter blockedContactAdapter;
    List<ContactModel> values;
    RecyclerView recyclerViewBlockedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myblacklist_layout);
        editTextAddNewPhone = (EditText)findViewById(R.id.edit_newPhone);
        Button btnAddNewPhone = (Button)findViewById(R.id.btn_newPhone);
        btnAddNewPhone.setOnClickListener(this);
        Button btnDeleletePhone = (Button)findViewById(R.id.btn_DeleteAll);
        btnDeleletePhone.setOnClickListener(this);
        Button btnBlockContact = (Button)findViewById(R.id.btn_block_contact);
        btnBlockContact.setOnClickListener(this);
        contactsDataSource = new ContactsDataSource(this);
        contactsDataSource.open();
        prepareRecyclerView();
    }

    private void prepareRecyclerView(){
        recyclerViewBlockedContacts = (RecyclerView)findViewById(R.id.recycler_blocked_contacts);
        recyclerViewBlockedContacts.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewBlockedContacts.setItemAnimator(new DefaultItemAnimator());
        values = contactsDataSource.getAllContacts();
        setEmptyRecycler();
        blockedContactAdapter = new ContactAdapter(values);
        recyclerViewBlockedContacts.setAdapter(blockedContactAdapter);
        ItemTouchHelper swipeToDismiss = buildSwipeToDismiss();
        swipeToDismiss.attachToRecyclerView(recyclerViewBlockedContacts);
    }

    private void setEmptyRecycler(){
        TextView emptyRecycler = (TextView)findViewById(R.id.txt_blocked_contacts_empty);
        if(values.size()==0){
            recyclerViewBlockedContacts.setVisibility(View.INVISIBLE);
            emptyRecycler.setVisibility(View.VISIBLE);
        } else {
            recyclerViewBlockedContacts.setVisibility(View.VISIBLE);
            emptyRecycler.setVisibility(View.INVISIBLE);
        }
    }

    private ItemTouchHelper buildSwipeToDismiss(){
        ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                contactsDataSource.deleteBlockedContact(values.get(viewHolder.getAdapterPosition()).get_id());
                values.remove(viewHolder.getAdapterPosition());
                blockedContactAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                setEmptyRecycler();
            }
        });
        return swipeToDismiss;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_newPhone) {
            if(editTextAddNewPhone.getText().toString().matches(regexIsAValidPhoneNumber)) {
                List<String> numbers = new ArrayList<>();
                numbers.add(editTextAddNewPhone.getText().toString());
                ContactModel ContactModel = contactsDataSource.addBlockedContact("Pedro", numbers);
                values.add(ContactModel);
                blockedContactAdapter.notifyItemInserted(values.indexOf(ContactModel));
                setEmptyRecycler();
            } else {
                Toast.makeText(getApplicationContext(), "This is not a valid phone number",
                        Toast.LENGTH_SHORT).show();
            }
        } else if(v.getId() == R.id.btn_DeleteAll){
            contactsDataSource.deleteAll();
            blockedContactAdapter.notifyItemRangeRemoved(0, values.size());
            values.clear();
            setEmptyRecycler();
        } else if(v.getId() == R.id.btn_block_contact){
            Intent blockContactActivity = new Intent(this, chooseContactsToBlock.class);
            startActivity(blockContactActivity);
        }
    }

    @Override
    protected void onResume() {
        contactsDataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        contactsDataSource.close();
        super.onPause();
    }
}
