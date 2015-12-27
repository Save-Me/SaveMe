package com.example.chicharo.call_blocker.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.chicharo.call_blocker.R;
import com.example.chicharo.call_blocker.TabDeployer;
import com.example.chicharo.call_blocker.adapters.FragmentListAdapter;
import com.example.chicharo.call_blocker.fragments.ContactsToBlockFragment;
import com.example.chicharo.call_blocker.fragments.RecentCallsToBlockFragment;
import com.example.chicharo.call_blocker.models.ContactModel;
import com.example.chicharo.call_blocker.service.ContactsListEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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
public class ChooseContactsToBlock extends AppCompatActivity {

    private static final String BUNDLE_LAYOUT = "saved_layout";
    private static final int TAB_SELECTOR = 0;
    public List<ContactModel> mBlockContacts = new ArrayList<>();
    @Bind(R.id.fragment_pager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    TabDeployer mTabs;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    private Parcelable mSavedLayoutState;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private List<Integer> mTabIcons = new ArrayList<>();
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contacts_to_block);
        ButterKnife.bind(this);
        createLists(savedInstanceState);
        setUpToolbar();
        setUpViewPager();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_LAYOUT, mViewPager.getAdapter().saveState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSavedLayoutState = savedInstanceState.getParcelable(BUNDLE_LAYOUT);
            if (mSavedLayoutState != null) {
                mViewPager.getAdapter().restoreState(mSavedLayoutState, null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_block, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.block_selected) {
            blockSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void blockSelected() {
        if (!mBlockContacts.isEmpty()) {
            EventBus.getDefault().post(new ContactsListEvent(mBlockContacts));
            finish();
        } else {
            Toast.makeText(this, R.string.no_contacts_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.inflateMenu(R.menu.menu_block);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(0);
        }
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_save_me_vector);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void createLists(Bundle savedInstanceState) {

        //Lists required to build the tab strip
        mFragments.add(new ContactsToBlockFragment());
        mFragments.add(new RecentCallsToBlockFragment());

        mTitles.add(getString(R.string.contacts));
        mTitles.add(getString(R.string.recent_calls));

        mTabIcons.add(R.drawable.ic_contacts);
        mTabIcons.add(R.drawable.ic_recent_calls);
    }

    private void setUpViewPager() {
        FragmentListAdapter fA = new FragmentListAdapter(getSupportFragmentManager(), this, mFragments, mTitles, mTabIcons);
        mViewPager.setAdapter(fA);
        mTabs.setupWithViewPager(mViewPager);

        setUpFirstSelectedTab();
        //  syncTabStrip();

    }

    private void setUpFirstSelectedTab() {
        mViewPager.setCurrentItem(TAB_SELECTOR);
    }
}
