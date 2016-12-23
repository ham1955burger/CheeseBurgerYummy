package com.example.user.cheeseburgeryummy.Photo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 12/15/16.
 */

public class PhotoBook implements Serializable {
    @SerializedName("pk") private int pk;
    @SerializedName("image_file") private String imageFile;
    @SerializedName("image_thumb_file") private String imageThumbFile;
    @SerializedName("description") private String description;
    @SerializedName("created_at") private Date createdAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageThumbFile() {
        return imageThumbFile;
    }

    public void setImageThumbFile(String imageThumbFile) {
        this.imageThumbFile = imageThumbFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
