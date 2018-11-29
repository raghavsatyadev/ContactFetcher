package com.rocky.contacter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.rocky.contactfetcher.Contact;
import com.rocky.contactfetcher.ContactFetcher;
import com.rocky.contactfetcher.ContactListener;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = ContactListActivity.class.getCanonicalName();
    private ContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactListAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((position, v) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ContactDetailActivity.CONTACT, adapter.getItem(position));
            startActivity(ContactDetailActivity.getIntent(ContactListActivity.this, bundle));
        });
        getContacts();
    }

    private void getContacts() {
        ContactFetcher.getContacts(this, new ContactListener<Contact>() {
            @Override
            public void onNext(Contact contact) {
                adapter.addItem(contact);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError: ", error);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.loadSaveForLaterContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        ContactFetcher.resolvePermissionResult(this, requestCode, permissions, grantResults);
    }
}
