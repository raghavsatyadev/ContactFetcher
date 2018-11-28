package com.rocky.contacter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class FetchContacts extends AsyncTask<Void, Object, Void> {
    private static final String TAG = "FetchContacts";
    private final int REFRESH_COUNT = 50;
    @SuppressLint("ObsoleteSdkInt")
    private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };
    private ContactEventListener contactEventListener;
    private int oldContactsSize = 0;

    public FetchContacts(ContactEventListener contactEventListener) {
        this.contactEventListener = contactEventListener;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        contactEventListener.onProgressUpdated((Integer) values[0], (List<Contact>) values[1], (Integer) values[2]);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<Contact> contacts = new ArrayList<>();

        ContentResolver cr = CoreApp.getInstance().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            int totalContact = cursor.getCount();

            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                if (!TextUtils.isEmpty(name)) {

                    ArrayList<String> emails = addEmail(cr, id);

                    ArrayList<String> phones = addPhone(cursor, cr, id);

                    Helper.verifyAndAddContact(contacts, new Contact(name, emails, phones));

                    updateContacts(contacts, totalContact, false);
                }
            } while (cursor.moveToNext());
            updateContacts(contacts, totalContact, true);
            cursor.close();
        }
        return null;
    }

    private void updateContacts(ArrayList<Contact> contacts, int totalContact, boolean ignoreRefreshLimit) {
        if (contacts.size() % REFRESH_COUNT == 0 || ignoreRefreshLimit) {
            if (contacts.size() != oldContactsSize) {
                publishProgress((int) (((double) contacts.size() / (double) totalContact) * 100),
                        contacts.subList(oldContactsSize, contacts.size()),
                        oldContactsSize);
                this.oldContactsSize = contacts.size();
            }
        }
    }

    private ArrayList<String> addPhone(Cursor cursor, ContentResolver cr, String id) {
        ArrayList<String> phones = new ArrayList<>();
        int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if (hasPhone > 0) {
            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
            if (cp != null && cp.moveToFirst()) {
                do {
                    String phone = Helper.getFormattedNumber(cp.getString(cp.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    if (Helper.verifyPhone(phones, phone))
                        phones.add(phone);
                } while (cp.moveToNext());
                cp.close();
            }
        }
        return phones;
    }

    private ArrayList<String> addEmail(ContentResolver cr, String id) {
        ArrayList<String> emails = new ArrayList<>();
        Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
        if (ce != null && ce.moveToFirst()) {

            do {
                String email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                if (Helper.verifyEmail(emails, email)) emails.add(email);
            } while (ce.moveToNext());
            ce.close();
        }
        return emails;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        contactEventListener.onComplete();
    }

    public interface ContactEventListener {
        void onComplete();

        void onProgressUpdated(Integer progress, List<Contact> contacts, Integer oldContactsSize);
    }

}