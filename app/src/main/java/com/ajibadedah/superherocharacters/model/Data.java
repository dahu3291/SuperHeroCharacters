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
 * Created by ajibade on 5/23/17
 */

public class Data {

    private ArrayList<Character> characters;
    private ArrayList<Comic> comics;

    public Data(){

    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public ArrayList<Comic> getComics() {
        return comics;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public void setComics(ArrayList<Comic> comics) {
        this.comics = comics;
    }

    public static class CharacterDataTypeAdapter implements JsonDeserializer<Data> {

        @Override
        public Data deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject object = json.getAsJsonObject();
            JsonObject dataJson = object.getAsJsonObject("data");
            JsonArray resultJson = dataJson.getAsJsonArray("results");

            ArrayList<Character> characters = new ArrayList<>();
            for (JsonElement element : resultJson) {
                characters.add(context.<Character>deserialize(element, Character.class));
            }

            Data data = new Data();
            data.setCharacters(characters);
            return data;
        }

    }

    public static class ComicDataTypeAdapter implements JsonDeserializer<Data> {

        @Override
        public Data deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject object = json.getAsJsonObject();
            JsonObject dataJson = object.getAsJsonObject("data");
            JsonArray resultJson = dataJson.getAsJsonArray("results");

            ArrayList<Comic> comics = new ArrayList<>();
            for (JsonElement element : resultJson) {
                comics.add(context.<Comic>deserialize(element, Comic.class));
            }

            Data data = new Data();
            data.setComics(comics);
            return data;
        }

    }
}
