package com.ajibadedah.superherocharacters.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ajibade on 5/22/17
 */

public class Thumbnail {

    private String path;
    private String extension;
    private String fullUrl;

    public Thumbnail(){

    }

    public Thumbnail(String path, String extension){
        this.path = path;
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFullUrl() {
        fullUrl = path + "." + extension;
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public static class ThumbnailTypeAdapter implements JsonDeserializer<Thumbnail> {
        @Override
        public Thumbnail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Thumbnail thumbnail = new Thumbnail();
            JsonObject thumbnailJson = json.getAsJsonObject();

            thumbnail.setPath(thumbnailJson.get("path").getAsString());
            thumbnail.setExtension(thumbnailJson.get("extension").getAsString());

            return thumbnail;
        }
    }
}
