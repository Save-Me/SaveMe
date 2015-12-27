package com.example.chicharo.call_blocker.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chicharo.call_blocker.TabDeployer;

import java.util.List;

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
public class FragmentListAdapter extends FragmentPagerAdapter implements TabDeployer.IconTab {
    Context context;
    private List<Fragment> fragments;
    private List<String> titles;
    private List<Integer> tabIcons;


    public FragmentListAdapter(FragmentManager fm, Context context, List<Fragment> fragments, List<String> titles, List<Integer> tabIcons) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
        this.tabIcons = tabIcons;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


    @Override
    public int getIcon(int position) {
        return tabIcons.get(position);
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        SpannableString spannableString = new SpannableString("  " + titles.get(position));
//        spannableString.setSpan(new ImageSpan(context, tabIcons.get(position), ImageSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }


}
