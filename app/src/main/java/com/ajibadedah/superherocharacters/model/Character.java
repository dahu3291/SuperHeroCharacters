package com.ajibadedah.superherocharacters.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ajibade on 5/22/17
 */

public class Character {
    private String name;
    private String id;
    private String biography;
    private Thumbnail thumbnailUrl;
    private String resourceUrl;
    private ArrayList<Comic> comics;

    public Character(){

    }

    public Character(String name, String resourceUrl, String id, String biography, ArrayList<Comic> comics){
        this.name = name;
        this.resourceUrl = resourceUrl;
        this.id = id;
        this.biography = biography;
        this.comics = comics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Thumbnail getThumbnail() {
        return thumbnailUrl;
    }

    public void setThumbnail(Thumbnail thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public ArrayList<Comic> getComics(){
        return comics;
    }

    public void setComics(ArrayList<Comic> comics) {
        this.comics = comics;
    }

    public static class CharacterTypeAdapter implements JsonDeserializer<Character> {

        @Override
        public Character deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Character character = new Character();

            JsonObject characterJson = json.getAsJsonObject();
            character.setName(characterJson.get("name").getAsString());
            character.setId(characterJson.get("id").getAsString());
            character.setBiography(characterJson.get("description").getAsString());

            Thumbnail thumbnail;
            JsonObject thumbnailObject = characterJson.get("thumbnail").getAsJsonObject();
            thumbnail = context.<Thumbnail>deserialize(thumbnailObject, Thumbnail.class);
            character.setThumbnail(thumbnail);

            JsonArray urls = characterJson.getAsJsonArray("urls");
            String type;
            String resultUrl = "";
            for (JsonElement element : urls) {
                type = element.getAsJsonObject().get("type").getAsString();
                if (type.equals("detail")) {
                    resultUrl = element.getAsJsonObject().get("url").getAsString();
                }
            }
            if (resultUrl.equals("")){
                resultUrl = "http://marvel.com";
            }
            character.setResourceUrl(resultUrl);


            return character;
        }
    }

}
