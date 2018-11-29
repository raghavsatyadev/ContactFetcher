package com.rocky.contacter;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestBuilder;
import com.rocky.contactfetcher.Contact;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends GenRecyclerAdapter<ContactListAdapter.DataObjectHolder, Contact> {
    private final AppCompatActivity activity;
    private ArrayList<Contact> saveForLaterContacts = new ArrayList<>();

    public ContactListAdapter(AppCompatActivity activity, ArrayList<Contact> strings) {
        super(strings);
        this.activity = activity;
    }

    @Override
    protected DataObjectHolder creatingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    protected void bindingViewHolder(DataObjectHolder holder, int position) {
        Contact item = getItem(position);
        holder.txtRowContactName.setText(item.displayName);
        boolean isImagePresent = item.photo != null && !TextUtils.isEmpty(item.photo.toString());
        holder.imgContact.setVisibility(isImagePresent ? View.VISIBLE : View.GONE);
        holder.txtContactInitial.setVisibility(!isImagePresent ? View.VISIBLE : View.GONE);
        if (!isImagePresent) {
            if (!TextUtils.isEmpty(item.displayName))
                holder.txtContactInitial.setText(String.valueOf(item.displayName.charAt(0)));
        } else {
            GlideRequest<Drawable> load = GlideApp.with(activity).load(item.photo);
            if (item.thumbnail != null && !TextUtils.isEmpty(item.thumbnail.toString())) {
                RequestBuilder<Drawable> thumbnailRequest = GlideApp
                        .with(activity)
                        .load(item.thumbnail);
                load = load.thumbnail(thumbnailRequest);
            }
            load.into(holder.imgContact);
        }
    }

    public void loadSaveForLaterContacts() {
        addAll(saveForLaterContacts);
        saveForLaterContacts.clear();
    }

    class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private AppCompatTextView txtContactInitial;
        private CircleImageView imgContact;
        private AppCompatTextView txtRowContactName;

        DataObjectHolder(View view) {
            super(view);
            txtRowContactName = view.findViewById(R.id.txt_row_contact_name);
            imgContact = view.findViewById(R.id.img_contact);
            txtContactInitial = view.findViewById(R.id.txt_contact_initial);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getMyClickListener() != null)
                getMyClickListener().onItemClick(getLayoutPosition(), v);
        }
    }
}
