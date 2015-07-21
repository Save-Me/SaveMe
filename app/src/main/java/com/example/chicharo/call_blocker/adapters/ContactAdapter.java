package com.example.chicharo.call_blocker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chicharo.call_blocker.models.ContactModel;
import com.example.chicharo.call_blocker.R;

import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<ContactModel> blockedContactsList;
    onItemClickListener mItemClickListener;

    public ContactAdapter(List<ContactModel> blockedContactsList){
        this.blockedContactsList = blockedContactsList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.bivalue_card, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactModel contactmodel = blockedContactsList.get(position);
        holder.contactName.setText(contactmodel.getContactName());
        holder.contactNumber.setText(contactmodel.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return blockedContactsList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView contactName;
        TextView contactNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = (TextView)itemView.findViewById(R.id.blocked_contact_title);
            contactNumber = (TextView)itemView.findViewById(R.id.blocked_contact_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

    }

    public interface onItemClickListener{
        public void onItemClick(View v, int position);
    }

    public void SetOnItemClickListener(final onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
