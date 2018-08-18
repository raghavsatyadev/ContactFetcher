


package com.rocky.contacter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactDetailActivity extends AppCompatActivity {

    public static final String CONTACT = "Contact";
    private ContactDetailAdapter adapter;

    public static Intent getIntent(Context context, Bundle bundle) {
        return new Intent(context, ContactDetailActivity.class).putExtras(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactDetailAdapter(new ArrayList<String>());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GenRecyclerAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Helper.copyToClipboard(adapter.getItem(position), ContactDetailActivity.this);
                Toast.makeText(ContactDetailActivity.this, "Copied ...!!!", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CONTACT)) {
            Contact contact = intent.getParcelableExtra(CONTACT);
            adapter.setContact(contact);
        }
    }
}
