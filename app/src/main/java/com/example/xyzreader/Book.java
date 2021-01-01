package com.example.xyzreader;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "books")
public class Book {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("thumb")
    @Expose
    public String thumb;
    @SerializedName("photo")
    @Expose
    public String photo;
    @SerializedName("aspect_ratio")
    @Expose
    public Double aspectRatio;
    @SerializedName("published_date")
    @Expose
    public String publishedDate;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public Double getAspectRatio() {
        return aspectRatio;
    }
}
