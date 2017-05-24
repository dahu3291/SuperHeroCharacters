package com.ajibadedah.superherocharacters.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajibade on 5/22/17
 */

public class Comic{
    private String title;
    private Thumbnail thumbnail;
    private String resourceUrl;

    public Comic(){

    }

    public Comic(String name,Thumbnail thumbnail, String resourceUrl){
        this.title = name;
        this.thumbnail = thumbnail;
        this.resourceUrl = resourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public static class ComicTypeAdapter implements JsonDeserializer<Comic> {

        @Override
        public Comic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            ArrayList<Comic> comics = new ArrayList<>();

//            JsonObject comicJson = json.getAsJsonObject();

            JsonObject comicJson = json.getAsJsonObject();

            Thumbnail thumbnail;
            JsonObject thumbnailObject;

            Comic comic = new Comic();
            comic.setTitle(comicJson.get("title").getAsString());

            thumbnailObject = comicJson.get("thumbnail").getAsJsonObject();
            thumbnail = context.<Thumbnail>deserialize(thumbnailObject, Thumbnail.class);
            comic.setThumbnail(thumbnail);

            JsonArray urls = comicJson.getAsJsonArray("urls");
            String type;
            String resultUrl = "";
            if (urls != null) {
                for (JsonElement element : urls) {
                    type = element.getAsJsonObject().get("type").getAsString();
                    if (type.equals("detail")) {
                        resultUrl = element.getAsJsonObject().get("url").getAsString();
                    }
                }
                if (resultUrl.equals("")) {
                    resultUrl = "http://marvel.com";
                }
                comic.setResourceUrl(resultUrl);
            }

            return comic;
        }
    }

}