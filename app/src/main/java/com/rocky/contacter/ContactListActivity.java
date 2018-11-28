package com.rocky.contacter;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ContactListAdapter adapter;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactListAdapter(new ArrayList<Contact>());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GenRecyclerAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(ContactDetailActivity.CONTACT, adapter.getItem(position));
                startActivity(ContactDetailActivity.getIntent(ContactListActivity.this, bundle));
                isClicked = true;
            }
        });
        checkPermissionLocal();
    }

    private void checkPermissionLocal() {
        if (PermissionUtil.checkPermission(this, PermissionUtil.Permissions.READ_CONTACTS)) {
            getContacts();
        } else {
            PermissionUtil.getPermission(this, PermissionUtil.Permissions.READ_CONTACTS,
                    PermissionUtil.PermissionCode.READ_CONTACTS,
                    PermissionUtil.PermissionMessage.READ_CONTACTS,
                    null);
        }
    }

    private void getContacts() {
        progressBar.setVisibility(View.VISIBLE);
        new FetchContacts(new FetchContacts.ContactEventListener() {
            @Override
            public void onComplete() {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onProgressUpdated(Integer progress, List<Contact> contacts, Integer oldContactsSize) {
                progressBar.setProgress(progress);
                if (isClicked) {
                    adapter.saveForLater(new ArrayList<>(contacts));
                } else {
                    adapter.addAll(new ArrayList<>(contacts));
                }
            }
        }).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            isClicked = false;
            adapter.loadSaveForLaterContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.PermissionCode.READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                checkPermissionLocal();
            }
        }
    }
}
