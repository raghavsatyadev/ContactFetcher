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


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * Contact entity.
 *
 * @author Ulrich Raab
 */
public class Contact implements Parcelable, Comparable<Contact> {

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    /**
     * The unique id of this contact.
     */
    public final long id;
    /**
     * Flag indicating if this contact should be visible in any user interface.
     */
    public int inVisibleGroup;
    /**
     * The display name of this contact.
     */
    public String displayName;
    /**
     * Flag indicating if this contact is a favorite contact.
     */
    public boolean starred;
    /**
     * The URI of the full-size photo of this contact.
     */
    public Uri photo;
    /**
     * The URI of the thumbnail of the photo of this contact.
     */
    public Uri thumbnail;
    /**
     * The email addresses of this contact.
     */
    public ArrayList<String> emails = new ArrayList<>();
    /**
     * The phone numbers of this contact.
     */
    public ArrayList<String> phoneNumbers = new ArrayList<>();

    /**
     * Creates a new contact with the specified id.
     *
     * @param id The id of the contact.
     */
    Contact(long id) {
        this.id = id;
    }

    protected Contact(Parcel in) {
        this.id = in.readLong();
        this.inVisibleGroup = in.readInt();
        this.displayName = in.readString();
        this.starred = in.readByte() != 0;
        this.photo = in.readParcelable(Uri.class.getClassLoader());
        this.thumbnail = in.readParcelable(Uri.class.getClassLoader());
        this.emails = in.createStringArrayList();
        this.phoneNumbers = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return id == contact.id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.inVisibleGroup);
        dest.writeString(this.displayName);
        dest.writeByte(this.starred ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.photo, flags);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeStringList(this.emails);
        dest.writeStringList(this.phoneNumbers);
    }

    @Override
    public int compareTo(Contact o) {
        return this.displayName.compareTo(o.displayName);
    }
}
