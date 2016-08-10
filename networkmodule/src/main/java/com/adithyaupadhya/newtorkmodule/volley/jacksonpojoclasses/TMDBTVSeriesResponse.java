package com.adithyaupadhya.newtorkmodule.volley.jacksonpojoclasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adithya.upadhya on 23-01-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TMDBTVSeriesResponse {
    public Integer page;
    public List<Results> results;
    public Integer total_results;
    public Integer total_pages;

    public static class Results implements Parcelable {
        public String poster_path;
        public Float popularity;
        public Integer id;
        public String backdrop_path;
        public Float vote_average;
        public String overview;
        public String first_air_date;
        public List<String> origin_country;
        public List<Integer> genre_ids;
        public String original_language;
        public Integer vote_count;
        public String name;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.poster_path);
            dest.writeValue(this.popularity);
            dest.writeValue(this.id);
            dest.writeString(this.backdrop_path);
            dest.writeValue(this.vote_average);
            dest.writeString(this.overview);
            dest.writeString(this.first_air_date);
            dest.writeStringList(this.origin_country);
            dest.writeList(this.genre_ids);
            dest.writeString(this.original_language);
            dest.writeValue(this.vote_count);
            dest.writeString(this.name);
        }

        public Results() {
        }

        protected Results(Parcel in) {
            this.poster_path = in.readString();
            this.popularity = (Float) in.readValue(Float.class.getClassLoader());
            this.id = (Integer) in.readValue(Integer.class.getClassLoader());
            this.backdrop_path = in.readString();
            this.vote_average = (Float) in.readValue(Float.class.getClassLoader());
            this.overview = in.readString();
            this.first_air_date = in.readString();
            this.origin_country = in.createStringArrayList();
            this.genre_ids = new ArrayList<>();
            in.readList(this.genre_ids, Integer.class.getClassLoader());
            this.original_language = in.readString();
            this.vote_count = (Integer) in.readValue(Integer.class.getClassLoader());
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
