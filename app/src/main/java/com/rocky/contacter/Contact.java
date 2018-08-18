package com.rocky.contacter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Contact implements Parcelable {
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
    public String name;
    public ArrayList<String> emails;
    public ArrayList<String> phoneNumbers;

    public Contact() {
        emails = new ArrayList<>();
        phoneNumbers = new ArrayList<>();
    }

    public Contact(String name, ArrayList<String> emails, ArrayList<String> phoneNumbers) {
        this.name = name;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.emails = in.createStringArrayList();
        this.phoneNumbers = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.emails);
        dest.writeStringList(this.phoneNumbers);
    }
}
