package com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Created by adithya.upadhya on 18-06-2016.
 */
public class TMDBGenericGameResponse {
    public List<Results> results;

    public static class Results implements Parcelable {
        public int id;
        public String name;

        @JsonSetter("title")
        public void setTitle(String title) {
            this.name = title;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.name);
        }

        public Results() {
        }

        protected Results(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
        }

        public static final Parcelable.Creator<Results> CREATOR = new Parcelable.Creator<Results>() {
            @Override
            public Results createFromParcel(Parcel source) {
                return new Results(source);
            }

            @Override
            public Results[] newArray(int size) {
                return new Results[size];
            }
        };
    }
}