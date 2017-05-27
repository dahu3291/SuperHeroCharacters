package com.ajibadedah.superherocharacters.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import static com.ajibadedah.superherocharacters.data.CharacterContract.*;

import com.ajibadedah.superherocharacters.R;
import com.ajibadedah.superherocharacters.model.Character;
import com.ajibadedah.superherocharacters.model.Comic;
import com.ajibadedah.superherocharacters.model.Data;
import com.ajibadedah.superherocharacters.model.Thumbnail;
import com.ajibadedah.superherocharacters.utility.MD5Hash;
import com.ajibadedah.superherocharacters.utility.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by ajibade on 5/22/17
 */

public class SyncTask {
    public static final String ACTION_DATA_UPDATED =
            "com.ajibadedah.superherocharacters.sync.ACTION_DATA_UPDATED";

    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Character.class, new Character.CharacterTypeAdapter())
            .registerTypeAdapter(Thumbnail.class, new Thumbnail.ThumbnailTypeAdapter())
            .create();

    public SyncTask(){

    }

    synchronized public static void doSomething(Context context){
        String[] names = context.getResources().getStringArray(R.array.character_list);

        context.getContentResolver().delete(CharacterEntry.CONTENT_URI, null, null);

        for (String name: names){
            getCharacterFromApi(context, name);
        }
//        updateWidgets();
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);

    }

    private static void getCharacterFromApi(Context context, String name){
        MD5Hash md5Hash = MD5Hash.create(context);
        Call<Data> characterCall =  NetworkUtil.getCharacterApi().
                getCharacter(name, String.valueOf(md5Hash.getTimeStamp()), md5Hash.getApiKey(),
                        md5Hash.getHashSignature());

        Character character;
        try {
            ArrayList<Character> characters = characterCall.execute().body().getCharacters();
            if (!(characters.size() > 0)) return;
            character = characters.get(0);
            String id = character.getId();

            Call<Data> comicCall =  NetworkUtil.getComicApi().
                    getComics(id, context.getResources().getString(R.string.comic_number_limit),
                            String.valueOf(md5Hash.getTimeStamp()), md5Hash.getApiKey(),
                            md5Hash.getHashSignature());
            ArrayList<Comic> comics = new ArrayList<>();
            comics = comicCall.execute().body().getComics();
            character.setComics(comics);
            String comicsJson;
            comicsJson = gson.toJson(comics);

            ContentValues values = new ContentValues();

            values.put(CharacterEntry.COLUMN_CHARACTER_ID, id);
            values.put(CharacterEntry.COLUMN_CHARACTER_NAME, character.getName());
            values.put(CharacterEntry.COLUMN_CHARACTER_BIO, character.getBiography());
            values.put(CharacterEntry.COLUMN_CHARACTER_THUMBNAIL,
                    character.getThumbnail().getFullUrl());
            values.put(CharacterEntry.COLUMN_CHARACTER_RESOURECE, character.getResourceUrl());
            values.put(CharacterEntry.COLUMN_CHARACTER_COMICS, comicsJson);

            context.getContentResolver().insert(CharacterEntry.CONTENT_URI, values);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
