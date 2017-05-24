package com.ajibadedah.superherocharacters;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ajibadedah.superherocharacters.data.CharacterContract.CharacterEntry;
import com.ajibadedah.superherocharacters.sync.CharacterSyncManager;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, CharacterComicAdapter.AdapterClickListener{

    public static final String STARTING_CHARACTER_ID = "character_id";
    private static final int ID_CHARACTER_LOADER = 44;
    private CharacterComicAdapter mCharacterComicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mCharacterComicAdapter = new CharacterComicAdapter(this, this, true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.character_list_recycler);
        recyclerView.setAdapter(mCharacterComicAdapter);
        recyclerView.setLayoutManager(layoutManager);

        CharacterSyncManager.getInstance(this);
        getSupportLoaderManager().initLoader(ID_CHARACTER_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                CharacterEntry._ID,
                CharacterEntry.COLUMN_CHARACTER_NAME,
                CharacterEntry.COLUMN_CHARACTER_THUMBNAIL
        };
        return new CursorLoader(this, CharacterEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCharacterComicAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCharacterComicAdapter.swapCursor(null);
    }

    @Override
    public void ItemClicked(int id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(STARTING_CHARACTER_ID, id);
        startActivity(intent);
    }
}
