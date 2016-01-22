package com.dreedi.chicharo.call_blocker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dreedi.chicharo.call_blocker.R;
import com.dreedi.chicharo.call_blocker.activities.ChooseContactsToBlockActivity;
import com.dreedi.chicharo.call_blocker.models.ContactModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
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

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    onItemClickListener mItemClickListener;
    private List<ContactModel> blockedContactsList;
    private Context mContext;

    public ContactsAdapter(Context context, List<ContactModel> blockedContactsList) {
        this.mContext = context;
        this.blockedContactsList = blockedContactsList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_blocked_contacts_card, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.setContact(blockedContactsList.get(position));

    }

    @Override
    public int getItemCount() {
        return blockedContactsList.size();
    }

    public void SetOnItemClickListener(final onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(View v, int position);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.blocked_contact_title)
        TextView contactName;
        @Bind(R.id.blocked_contact_number)
        TextView contactNumber;
        @Bind(R.id.contact_image)
        CircleImageView contactImg;
        @Bind(R.id.checkBox)
        CheckBox checkBox;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        public void setContact(final ContactModel contactModel) {

            if (contactModel.getImageUri() != null) {
                Picasso.with(mContext).load(contactModel.getImageUri()).error(R.drawable.ic_contact_circle).into(contactImg);
            } else {
                contactImg.setImageResource(R.drawable.ic_contact_circle);
            }
            if (contactModel.getContactName() == null) {
                contactName.setText(R.string.unknown);
            } else {
                contactName.setText(contactModel.getContactName());
            }
            contactNumber.setText(contactModel.getPhoneNumber());
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(contactModel.isChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    contactModel.setChecked(isChecked);
                    if (mContext instanceof ChooseContactsToBlockActivity) {
                        if (isChecked) {
                            ((ChooseContactsToBlockActivity) mContext).mBlockContacts.add(contactModel);
                        } else {
                            ((ChooseContactsToBlockActivity) mContext).mBlockContacts.remove(contactModel);
                        }
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
