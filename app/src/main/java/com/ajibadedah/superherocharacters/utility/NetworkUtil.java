package com.ajibadedah.superherocharacters.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ajibadedah.superherocharacters.model.Character;
import com.ajibadedah.superherocharacters.model.Comic;
import com.ajibadedah.superherocharacters.model.Data;
import com.ajibadedah.superherocharacters.model.Thumbnail;
import com.ajibadedah.superherocharacters.preferences.CharacterPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ajibade on 5/22/17
 */

public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName();

    private static final String MARVEL_URL = "https://gateway.marvel.com:443/v1/public/";

    private static final CharacterApi characterApi;
    private static final ComicApi comicApi;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Data.class, new Data.CharacterDataTypeAdapter())
                .registerTypeAdapter(Character.class, new Character.CharacterTypeAdapter())
                .registerTypeAdapter(Thumbnail.class, new Thumbnail.ThumbnailTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MARVEL_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        characterApi = retrofit.create(CharacterApi.class);
    }

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Data.class, new Data.ComicDataTypeAdapter())
                .registerTypeAdapter(Comic.class, new Comic.ComicTypeAdapter())
                .registerTypeAdapter(Thumbnail.class, new Thumbnail.ThumbnailTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MARVEL_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        comicApi = retrofit.create(ComicApi.class);
    }

    public static CharacterApi getCharacterApi() {
        return characterApi;
    }

    public static ComicApi getComicApi() {
        return comicApi;
    }

    public interface CharacterApi {
        @GET("characters")
        Call<Data> getCharacter(@Query("name")  String name, @Query("ts") String timestamp
                , @Query("apikey") String apikey, @Query("hash") String hashSignature);

    }

    public interface ComicApi {
        @GET("characters/{id}/comics")
        Call<Data> getComics(@Path("id") String id, @Query("limit") String limit
                , @Query("ts") String timestamp, @Query("apikey") String apikey
                , @Query("hash") String hashSignature);
    }

}
