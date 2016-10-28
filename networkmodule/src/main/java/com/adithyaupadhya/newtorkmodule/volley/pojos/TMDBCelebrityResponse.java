package com.adithyaupadhya.newtorkmodule.volley.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adithya.upadhya on 27-01-2016.
 */
public class TMDBCelebrityResponse {
    public int page;
    public List<Results> results;
    public int total_results;
    public int total_pages;

    public static class Results implements Parcelable {
        public String profile_path;
        public Integer id;
        public List<KnownFor> known_for;
        public String name;

        public static class KnownFor implements Parcelable {
            public String poster_path;
            public String overview;
            public String release_date;
            public List<Integer> genre_ids;
            public Integer id;
            public String media_type;
            public String original_language;
            public String title;
            public String backdrop_path;
            public Integer vote_count;
            public Float vote_average;

            public String first_air_date;
            public String name;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.poster_path);
                dest.writeString(this.overview);
                dest.writeString(this.release_date);
                dest.writeList(this.genre_ids);
                dest.writeValue(this.id);
                dest.writeString(this.media_type);
                dest.writeString(this.original_language);
                dest.writeString(this.title);
                dest.writeString(this.backdrop_path);
                dest.writeValue(this.vote_count);
                dest.writeValue(this.vote_average);
                dest.writeString(this.first_air_date);
                dest.writeString(this.name);
            }

            public KnownFor() {
            }

            protected KnownFor(Parcel in) {
                this.poster_path = in.readString();
                this.overview = in.readString();
                this.release_date = in.readString();
                this.genre_ids = new ArrayList<Integer>();
                in.readList(this.genre_ids, Integer.class.getClassLoader());
                this.id = (Integer) in.readValue(Integer.class.getClassLoader());
                this.media_type = in.readString();
                this.original_language = in.readString();
                this.title = in.readString();
                this.backdrop_path = in.readString();
                this.vote_count = (Integer) in.readValue(Integer.class.getClassLoader());
                this.vote_average = (Float) in.readValue(Float.class.getClassLoader());
                this.first_air_date = in.readString();
                this.name = in.readString();
            }

            public static final Creator<KnownFor> CREATOR = new Creator<KnownFor>() {
                @Override
                public KnownFor createFromParcel(Parcel source) {
                    return new KnownFor(source);
                }

                @Override
                public KnownFor[] newArray(int size) {
                    return new KnownFor[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.profile_path);
            dest.writeValue(this.id);
            dest.writeList(this.known_for);
            dest.writeString(this.name);
        }

        public Results() {
        }

        protected Results(Parcel in) {
            this.profile_path = in.readString();
            this.id = (Integer) in.readValue(Integer.class.getClassLoader());
            this.known_for = new ArrayList<>();
            in.readList(this.known_for, KnownFor.class.getClassLoader());
            this.name = in.readString();
        }

        public static final Creator<Results> CREATOR = new Creator<Results>() {
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
