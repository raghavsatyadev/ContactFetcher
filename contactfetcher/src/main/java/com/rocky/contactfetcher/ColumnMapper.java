package com.rocky.contactfetcher;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

class ColumnMapper {

    // Utility class -> No instances allowed
    private ColumnMapper() {
    }

    static void mapInVisibleGroup(Cursor cursor, Contact contact, int columnIndex) {
        contact.inVisibleGroup = cursor.getInt(columnIndex);
    }

    static void mapDisplayName(Cursor cursor, Contact contact, int columnIndex) {
        String displayName = cursor.getString(columnIndex);
        if (displayName != null && !displayName.isEmpty()) {
            contact.displayName = displayName;
        }
    }

    static void mapEmail(Cursor cursor, Contact contact, int columnIndex) {
        String email = cursor.getString(columnIndex);
        if (Helper.verifyEmail(contact.emails, email)) {
            contact.emails.add(email);
        }
    }

    static void mapPhoneNumber(Cursor cursor, Contact contact, int columnIndex) {
        String phoneNumber = cursor.getString(columnIndex);
        if (!TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = Helper.getFormattedNumber(phoneNumber);
            if (Helper.verifyPhone(contact.phoneNumbers, phoneNumber)) {
                contact.phoneNumbers.add(phoneNumber);
            }
        }
    }

    static void mapPhoto(Cursor cursor, Contact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.photo = Uri.parse(uri);
        }
    }

    static void mapStarred(Cursor cursor, Contact contact, int columnIndex) {
        contact.starred = cursor.getInt(columnIndex) != 0;
    }

    static void mapThumbnail(Cursor cursor, Contact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.thumbnail = Uri.parse(uri);
        }
    }
}
