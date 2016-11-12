package com.adithyaupadhya.newtorkmodule.volley.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adithya.upadhya on 17-01-2016.
 */
public class TMDBMoviesResponse {
    public int page;
    public List<Results> results;
    public int total_pages;
    public int total_results;

    public static class Results implements Parcelable {
        public String poster_path;
        public String overview;
        public String release_date;
        public List<Integer> genre_ids;
        public Integer id;
        public String original_language;
        public String title;
        public String backdrop_path;
        public Integer vote_count;
        public Float vote_average;

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
            dest.writeString(this.original_language);
            dest.writeString(this.title);
            dest.writeString(this.backdrop_path);
            dest.writeValue(this.vote_count);
            dest.writeValue(this.vote_average);
        }

        public Results() {
        }

        protected Results(Parcel in) {
            this.poster_path = in.readString();
            this.overview = in.readString();
            this.release_date = in.readString();
            this.genre_ids = new ArrayList<>();
            in.readList(this.genre_ids, Integer.class.getClassLoader());
            this.id = (Integer) in.readValue(Integer.class.getClassLoader());
            this.original_language = in.readString();
            this.title = in.readString();
            this.backdrop_path = in.readString();
            this.vote_count = (Integer) in.readValue(Integer.class.getClassLoader());
            this.vote_average = (Float) in.readValue(Float.class.getClassLoader());
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
