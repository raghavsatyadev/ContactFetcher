package com.rocky.contacter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ContactListAdapter extends GenRecyclerAdapter<ContactListAdapter.DataObjectHolder, Contact> {
    private ArrayList<Contact> saveForLaterContacts = new ArrayList<>();

    public ContactListAdapter(ArrayList<Contact> strings) {
        super(strings);
    }

    @Override
    protected DataObjectHolder creatingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    protected void bindingViewHolder(DataObjectHolder holder, int position) {
        holder.txtRowContactName.setText(getItem(position).displayName);
    }

    public void loadSaveForLaterContacts() {
        addAll(saveForLaterContacts);
        saveForLaterContacts.clear();
    }

    class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private AppCompatTextView txtRowContactName;

        DataObjectHolder(View view) {
            super(view);
            txtRowContactName = view.findViewById(R.id.txt_row_contact_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getMyClickListener() != null)
                getMyClickListener().onItemClick(getLayoutPosition(), v);
        }
    }
}
