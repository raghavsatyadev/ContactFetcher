package com.rocky.contactfetcher;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.LongSparseArray;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactFetcher {
    private static final String[] PROJECTION = {
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.STARRED,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.IN_VISIBLE_GROUP
    };
    private static ContactListener<Contact> contactListener;

    private final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private ContentResolver resolver;
    private static CompositeDisposable compositeDisposable;

    private ContactFetcher(@NonNull Context context) {
        resolver = context.getContentResolver();
    }

    /**
     * get contacts stored on phone. returns all details listed in {@link Contact}
     *
     * @param contactListener for receiving contacts {@link ContactListener<Contact>}
     */
    public static void getContacts(Fragment fragment, ContactListener<Contact> contactListener) {
        ContactFetcher.contactListener = contactListener;
        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.READ_CONTACTS)) {
            fetch(fragment.getContext());
        } else {
            PermissionUtil.getPermission(fragment, PermissionUtil.Permissions.READ_CONTACTS,
                    PermissionUtil.PermissionCode.READ_CONTACTS,
                    fragment.getString(R.string.read_contact_permission_title),
                    null);
        }
    }

    /**
     * Fetches all contacts from the contacts apps and social networking apps.
     * <p>
     * returns all details listed in {@link Contact}
     *
     * @param contactListener for receiving contacts {@link ContactListener<Contact>}
     */
    public static void getContacts(Activity activity, ContactListener<Contact> contactListener) {
        ContactFetcher.contactListener = contactListener;
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.READ_CONTACTS)) {
            fetch(activity);
        } else {
            PermissionUtil.getPermission(activity, PermissionUtil.Permissions.READ_CONTACTS,
                    PermissionUtil.PermissionCode.READ_CONTACTS,
                    activity.getString(R.string.read_contact_permission_title),
                    null);
        }
    }

    private static void fetch(@NonNull final Context context) {
        Observable.create((ObservableOnSubscribe<Contact>)
                emitter -> new ContactFetcher(context).fetch(emitter))
                .sorted()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Contact>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onNext(Contact contact) {
                        if (contactListener != null) contactListener.onNext(contact);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (contactListener != null) contactListener.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        if (contactListener != null) contactListener.onComplete();
                    }
                });
    }

    /**
     * for resolving permission result
     */
    public static void resolvePermissionResult(Activity activity,
                                               int requestCode,
                                               String[] permissions,
                                               int[] grantResults) {
        if (requestCode == PermissionUtil.PermissionCode.READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                getContacts(activity, contactListener);
            }
        }
    }

    /**
     * for resolving permission result
     */
    public static void resolvePermissionResult(Fragment fragment,
                                               int requestCode,
                                               String[] permissions,
                                               int[] grantResults) {
        if (requestCode == PermissionUtil.PermissionCode.READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                getContacts(fragment, contactListener);
            }
        }
    }

    private void fetch(ObservableEmitter<Contact> subscriber) {
        LongSparseArray<Contact> contacts = new LongSparseArray<>();
        // Create a new cursor and go to the first position
        Cursor cursor = createCursor();
        cursor.moveToFirst();
        // Get the column indexes
        int idxId = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int idxInVisibleGroup = cursor.getColumnIndex(ContactsContract.Data.IN_VISIBLE_GROUP);
        int idxDisplayNamePrimary = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        int idxStarred = cursor.getColumnIndex(ContactsContract.Data.STARRED);
        int idxPhoto = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        int idxThumbnail = cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI);
        int idxMimeType = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int idxData1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        // Map the columns to the fields of the contact
        while (!cursor.isAfterLast()) {
            // Get the id and the contact for this id. The contact may be a null.
            long id = cursor.getLong(idxId);
            Contact contact = contacts.get(id, null);
            if (contact == null) {
                // Create a new contact
                contact = new Contact(id);
                // Map the non collection attributes
                ColumnMapper.mapInVisibleGroup(cursor, contact, idxInVisibleGroup);
                ColumnMapper.mapDisplayName(cursor, contact, idxDisplayNamePrimary);
                ColumnMapper.mapStarred(cursor, contact, idxStarred);
                ColumnMapper.mapPhoto(cursor, contact, idxPhoto);
                ColumnMapper.mapThumbnail(cursor, contact, idxThumbnail);
                // Add the contact to the collection
                contacts.put(id, contact);
            }

            // map phone number or email address
            String mimetype = cursor.getString(idxMimeType);
            switch (mimetype) {
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                    ColumnMapper.mapEmail(cursor, contact, idxData1);
                    break;
                }
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                    ColumnMapper.mapPhoneNumber(cursor, contact, idxData1);
                    break;
                }
            }

            cursor.moveToNext();
        }
        // Close the cursor
        cursor.close();
        // Emit the contacts
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.valueAt(i);
            if (Helper.verifyAndAddContact(contact))
                subscriber.onNext(contact);
        }
        subscriber.onComplete();
    }

    /**
     * get {@link CompositeDisposable} for RXJava
     *
     * @return {@link CompositeDisposable}
     */
    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        if (compositeDisposable.isDisposed()) compositeDisposable = new CompositeDisposable();
        return compositeDisposable;
    }

    /**
     * stops the process of finding contacts
     */
    public static void stop() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    private Cursor createCursor() {
        return resolver.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                FILTER,
                null,
                ContactsContract.Data.CONTACT_ID
        );
    }
}
