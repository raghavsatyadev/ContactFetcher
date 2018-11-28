/*
 * Copyright (C) 2016 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rocky.contacter;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

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
    private final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private ContentResolver resolver;

    private ContactFetcher(@NonNull Context context) {
        resolver = context.getContentResolver();
    }

    /**
     * Fetches all contacts from the contacts apps and social networking apps.
     *
     * @param context The context.
     * @return Observable that emits contacts on success.
     */
    public static Observable<Contact> fetch(@NonNull final Context context) {
        return Observable.create(emitter -> {
            new ContactFetcher(context).fetch(emitter);
        });
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
