package com.ajibadedah.superherocharacters;


import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajibadedah.superherocharacters.data.CharacterContract.CharacterEntry;
import com.ajibadedah.superherocharacters.firebase.ChatActivity;
import com.ajibadedah.superherocharacters.model.Comic;
import com.ajibadedah.superherocharacters.model.Thumbnail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnLongClickListener,
        CharacterComicAdapter.AdapterClickListener{

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ON_LONG_CLICK_TEXT = "arg_on_long_click_text";
    public static final String ARG_ON_LONG_CLICK_URL = "arg_on_long_click_url";
    private static final String TAG = "ArticleDetailFragment";
    private static final String ARG_CHARACTER_POSITION = "arg_character_position";
    private static final String ARG_STARTING_CHARACTER_POSITION = "arg_starting_character_position";
    private static final int ID_DETAIL_FRAGMENT_LOADER = 11;
    private Cursor mCursor;
    private String mItemId;
    private View mRootView;
    private ComicAdapter mComicAdapter;

    private String onLongClickText;
    private String onLongClickUrl;


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int itemId, int position, int startingPosition) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ITEM_ID, String.valueOf(itemId));
        arguments.putInt(ARG_CHARACTER_POSITION, position);
        arguments.putInt(ARG_STARTING_CHARACTER_POSITION, startingPosition);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getString(ARG_ITEM_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(ID_DETAIL_FRAGMENT_LOADER, null, this);
    }

    public DetailActivity getActivityCast() {
        return (DetailActivity) getActivity();
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        getActivityCast().setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivityCast(), ChatActivity.class);
//                intent.putExtra(ARG_ON_LONG_CLICK_TEXT, onLongClickText);
                startActivity(intent);
            }
        });

        bindViews();
        return mRootView;
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }
        CustomImageView thumbnail = (CustomImageView) mRootView.findViewById(R.id.detail_thumbnail);
        TextView nameView = (TextView) mRootView.findViewById(R.id.detail_name);
        TextView bioView = (TextView) mRootView.findViewById(R.id.bio);

        if (mCursor != null) {
            int index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_THUMBNAIL);
            String url = mCursor.getString(index);

            index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_NAME);
            String name = mCursor.getString(index);

            index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_BIO);
            String bio = mCursor.getString(index);

            Picasso.with(mRootView.getContext()).load(url).into(thumbnail);
            String description = "Image of " + name;
            thumbnail.setCustomUrl(mCursor.getString(index));
            thumbnail.setCustomImageName(description);
            thumbnail.setOnLongClickListener(this);

            nameView.setText(name);
            nameView.setOnLongClickListener(this);

            bioView.setText(bio);
            bioView.setOnLongClickListener(this);
        }


    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), CharacterEntry.buildItemUri(mItemId), null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;

        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        if (mCursor != null) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Comic.class, new Comic.ComicTypeAdapter())
                    .registerTypeAdapter(Thumbnail.class, new Thumbnail.ThumbnailTypeAdapter())
                    .create();

            int index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_COMICS);
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(mCursor.getString(index)).getAsJsonArray();
            ArrayList<Comic> comics = new ArrayList<>();
            for (int i = 0; i < array.size(); i++){
                comics.add(gson.fromJson(array.get(i), Comic.class));
            }

            mComicAdapter = new ComicAdapter(getActivity(), this, comics);
            GridLayoutManager layoutManager =
                    new GridLayoutManager(mRootView.getContext(), 2, GridLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.comic_list_recycler);
            recyclerView.setHasFixedSize(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(mComicAdapter);
            recyclerView.setLayoutManager(layoutManager);
        }
        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;

//        mComicAdapter.swapComics(null);

        bindViews();

    }

    @Override
    public void ItemClicked(int id) {

    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof CustomImageView) {
            CustomImageView thumbnail = (CustomImageView) v;
            onLongClickUrl = thumbnail.getCustomUrl();
            Snackbar.make(v, "Chat about " + thumbnail.getCustomImageName(), Snackbar.LENGTH_LONG)
                    .setAction("YES",  new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivityCast(), ChatActivity.class);
                            intent.putExtra(ARG_ON_LONG_CLICK_URL, onLongClickUrl);
                            startActivity(intent);
                        }
                    })
                    .show();
        } else {
            onLongClickUrl = "";
        }

        if(v instanceof TextView){
            TextView text = (TextView) v;
            onLongClickText = text.getText().toString();
            Snackbar.make(v, "Chat about " + onLongClickText, Snackbar.LENGTH_LONG)
                    .setAction("YES",  new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivityCast(), ChatActivity.class);
                            intent.putExtra(ARG_ON_LONG_CLICK_TEXT, onLongClickText);
                            startActivity(intent);
                        }
                    })
                    .show();

        } else {
            onLongClickText = "";
        }
        return false;
    }
}
