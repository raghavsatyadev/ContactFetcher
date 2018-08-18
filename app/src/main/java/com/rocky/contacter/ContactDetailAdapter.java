package com.rocky.contacter;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ContactDetailAdapter extends GenRecyclerAdapter<ContactDetailAdapter.DataObjectHolder, String> {
    public ContactDetailAdapter(ArrayList<String> strings) {
        super(strings);
    }

    @Override
    protected DataObjectHolder creatingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact_detail, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    protected void bindingViewHolder(DataObjectHolder holder, int position) {
        holder.txtRowContactDetail.setText(getItem(position));
    }

    public void setContact(Contact contact) {
        addItem(contact.name);
        for (int i = 0; i < contact.emails.size(); i++) {
            addItem(contact.emails.get(i));
        }
        for (int i = 0; i < contact.phoneNumbers.size(); i++) {
            addItem(contact.phoneNumbers.get(i));
        }
    }

    class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private AppCompatImageButton btnCopy;
        private AppCompatTextView txtRowContactDetail;

        DataObjectHolder(View view) {
            super(view);
            txtRowContactDetail = view.findViewById(R.id.txt_row_contact_detail);
            btnCopy = view.findViewById(R.id.btn_copy);
            btnCopy.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getMyClickListener() != null)
                getMyClickListener().onItemClick(getLayoutPosition(), v);
        }
    }
}
