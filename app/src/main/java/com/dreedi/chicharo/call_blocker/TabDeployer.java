package com.dreedi.chicharo.call_blocker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;

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
public class TabDeployer extends TabLayout {

    private ViewPager pager;
    private int tabCount;
    private Context context;

    public TabDeployer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public TabDeployer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public TabDeployer(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void setupWithViewPager(@NonNull ViewPager viewPager) {
        this.pager = viewPager;
        tabCount = pager.getAdapter().getCount();
        super.setupWithViewPager(viewPager);
        updateTabStyles();
    }

    private void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {

            LinearLayout tabStyle = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.partial_tab_layout, null);
            TextView tabText = ButterKnife.findById(tabStyle, R.id.tab_txt);
            ImageView tabImg = ButterKnife.findById(tabStyle, R.id.tab_img);
            tabText.setText(pager.getAdapter().getPageTitle(i));
            tabText.setAllCaps(true);
            if (pager.getAdapter() instanceof IconTab) {
                tabImg.setImageResource(((IconTab) pager.getAdapter()).getIcon(i));
            }
            Tab t = getTabAt(i);
            assert t != null;
            t.setCustomView(tabStyle);

        }
    }

    public interface IconTab {
        int getIcon(int position);
    }
}
