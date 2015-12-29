package com.example.chicharo.call_blocker.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chicharo.call_blocker.R;
import com.example.chicharo.call_blocker.adapters.BlackListAdapter;
import com.example.chicharo.call_blocker.fragments.AddToMyBlackListFragment;
import com.example.chicharo.call_blocker.models.ContactModel;
import com.example.chicharo.call_blocker.service.ContactEvent;
import com.example.chicharo.call_blocker.service.ContactsListEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String SETTINGS_SHARED_PREFERENCES_NAME = "Settings";
    private static final String BLOCK_HIDDEN_NUMBERS = "allowHiddenNumbers";
    private static final String BLOCK_UNKNOWN_NUMBERS = "allowUnknownNumbers";
    private static final String BLOCK_BLACKLIST_NUMBERS = "allowBlacklistNumbers";
    private final String FTAG = "BLACKLIST";
    @Bind(R.id.switch_hidden_numbers)
    Switch switch_hidden_numbers;
    @Bind(R.id.switch_blacklist_numbers)
    Switch switch_blacklist_numbers;
    @Bind(R.id.switch_unknown_numbers)
    Switch switch_unknown_numbers;
    @Bind(R.id.txt_blocked_contacts_empty)
    TextView emptyRecycler;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.recycler_blocked_contacts)
    RecyclerView recyclerViewBlockedContacts;
    RealmResults<ContactModel> values;
    private Realm realm;
    private BlackListAdapter blockedContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        realm = Realm.getInstance(this);
        initializeSwitches();
        setUpRecycleView();
        setUpFab();

    }

    private void initializeSwitches() {
        final SharedPreferences preferences = getSharedPreferences(SETTINGS_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        initializeSwitch(preferences, switch_hidden_numbers, BLOCK_HIDDEN_NUMBERS);
        initializeSwitch(preferences, switch_blacklist_numbers, BLOCK_BLACKLIST_NUMBERS);
        initializeSwitch(preferences, switch_unknown_numbers, BLOCK_UNKNOWN_NUMBERS);
    }

    private void initializeSwitch(final SharedPreferences preferences, Switch swi, final String sharedPreferenceTag) {
        boolean hiddenCheck = preferences.getBoolean(sharedPreferenceTag, false);
        swi.setChecked(hiddenCheck);
        swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(sharedPreferenceTag, isChecked).apply();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void setUpRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBlockedContacts.setLayoutManager(linearLayoutManager);
        values = realm.where(ContactModel.class).findAll();
        setEmptyRecycler();
        blockedContactsAdapter = new BlackListAdapter(this, values);
        recyclerViewBlockedContacts.setAdapter(blockedContactsAdapter);
        ItemTouchHelper swipeToDismiss = buildSwipeToDismiss();
        swipeToDismiss.attachToRecyclerView(recyclerViewBlockedContacts);
    }

    private ItemTouchHelper buildSwipeToDismiss() {
        ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                realm.beginTransaction();
                values.get(viewHolder.getAdapterPosition()).removeFromRealm();
                realm.commitTransaction();
                blockedContactsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                blockedContactsAdapter.notifyDataSetChanged();
                setEmptyRecycler();
            }
        });
        return swipeToDismiss;
    }

    private void setEmptyRecycler() {
        if (values.isEmpty()) {
            recyclerViewBlockedContacts.setVisibility(View.INVISIBLE);
            emptyRecycler.setVisibility(View.VISIBLE);
        } else {
            recyclerViewBlockedContacts.setVisibility(View.VISIBLE);
            emptyRecycler.setVisibility(View.INVISIBLE);
        }
    }


    private void setUpFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                AddToMyBlackListFragment addToMyBlackListFragmentDialog = new AddToMyBlackListFragment();
                addToMyBlackListFragmentDialog.show(fm, FTAG);
            }
        });
    }

    public void onEvent(ContactEvent contactEvent) {
        ContactModel contactModel = contactEvent.getContactModel();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(contactModel);
        realm.commitTransaction();
        blockedContactsAdapter.notifyDataSetChanged();
        setEmptyRecycler();
        EventBus.getDefault().cancelEventDelivery(contactEvent);
    }

    public void onEvent(ContactsListEvent contactEvent) {
        List<ContactModel> contactModels = contactEvent.getContactModels();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(contactModels);
        realm.commitTransaction();
        blockedContactsAdapter.notifyDataSetChanged();
        setEmptyRecycler();
        EventBus.getDefault().cancelEventDelivery(contactEvent);
    }
}
