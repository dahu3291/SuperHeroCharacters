package com.ajibadedah.superherocharacters;

import android.database.Cursor;
import android.net.Uri;
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
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ID_CHARACTER_LOADER = 44;
    private CharacterAdapter mCharacterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mCharacterAdapter = new CharacterAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.character_list_recycler);
        recyclerView.setAdapter(mCharacterAdapter);
        recyclerView.setLayoutManager(layoutManager);

        CharacterSyncManager.getInstance(this);
        getSupportLoaderManager().initLoader(ID_CHARACTER_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                CharacterEntry.COLUMN_CHARACTER_ID,
                CharacterEntry.COLUMN_CHARACTER_NAME,
                CharacterEntry.COLUMN_CHARACTER_THUMBNAIL
        };
        return new CursorLoader(this, CharacterEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCharacterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCharacterAdapter.swapCursor(null);
    }
}
