package com.ajibadedah.superherocharacters;


import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
        ComicAdapter.AdapterClickListener{

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
    private ImageView mThumbnail;
    private TextView mNameView;
    private ComicAdapter mComicAdapter;

    private String onLongClickText;
    private String onLongClickImageUrl;
    private String onLongClickImageName;


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int itemId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ITEM_ID, String.valueOf(itemId));
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
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
        mThumbnail = (ImageView) mRootView.findViewById(R.id.detail_thumbnail);
        mNameView = (TextView) mRootView.findViewById(R.id.detail_name);

        String newTransitionName = getString(R.string.transition_photo) + String.valueOf(mItemId);
        mThumbnail.setTransitionName(newTransitionName);

        newTransitionName = getString(R.string.transition_photo) + String.valueOf(mItemId);
        mNameView.setTransitionName(newTransitionName);

//        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
//        getActivityCast().setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivityCast(), ChatActivity.class);
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

        TextView bioView = (TextView) mRootView.findViewById(R.id.bio);

        if (mCursor != null) {
            int index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_THUMBNAIL);
            String url = mCursor.getString(index);

            index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_NAME);
            String name = mCursor.getString(index);

            index = mCursor.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_BIO);
            String bio = mCursor.getString(index);

            Picasso.with(mRootView.getContext()).load(url).into(mThumbnail);

            onLongClickImageUrl = mCursor.getString(index);
            onLongClickImageName = "Image of " + name;
            mThumbnail.setOnLongClickListener(this);

            mNameView.setText(name);
            mNameView.setOnLongClickListener(this);

            bioView.setText(bio);
            bioView.setOnLongClickListener(this);

            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(700);

//            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            TransitionManager.beginDelayedTransition((ViewGroup) mRootView, slide);
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
            recyclerView.setAdapter(mComicAdapter);
            recyclerView.setLayoutManager(layoutManager);
        }

        mThumbnail.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mThumbnail.getViewTreeObserver().removeOnPreDrawListener(this);
//                ActivityCompat.startPostponedEnterTransition(getActivity());
                getActivityCast().startPostponedEnterTransition();
                return true;
            }
        });
        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;

//        mComicAdapter.swapComics(null);

        bindViews();

    }

    @Override
    public void ItemClicked(View view, String imageName, String imageUrl) {
        FirebaseChat(view, imageName, imageUrl, null);
    }

    @Override
    public boolean onLongClick(View view) {
        FirebaseChat(view, onLongClickImageName, onLongClickImageUrl, onLongClickText);
        return false;
    }

    public void FirebaseChat(View view, String imageName, final String imageUrl, final String textName){

        if (view instanceof ImageView) {
            Snackbar.make(view, "Chat about " + imageName, Snackbar.LENGTH_LONG)
                    .setAction("YES",  new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivityCast(), ChatActivity.class);
                            intent.putExtra(ARG_ON_LONG_CLICK_URL, imageUrl);
                            startActivity(intent);
                        }
                    })
                    .show();
        }

        if(view instanceof TextView){
            TextView text = (TextView) view;
            onLongClickText = text.getText().toString();
            Snackbar.make(view, "Chat about " + textName, Snackbar.LENGTH_LONG)
                    .setAction("YES",  new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivityCast(), ChatActivity.class);
                            intent.putExtra(ARG_ON_LONG_CLICK_TEXT, textName);
                            startActivity(intent);
                        }
                    })
                    .show();

        }
    }

    /**
     * Returns the shared element that should be transitioned back to the previous Activity,
     * or null if the view is not visible on the screen.
     */
    @Nullable
    ImageView getCharacterImage() {
        if (isViewInBounds(getActivity().getWindow().getDecorView(), mThumbnail)) {
            return mThumbnail;
        }
        return null;
    }

    @Nullable
    TextView getCharacterText() {
        if (isViewInBounds(getActivity().getWindow().getDecorView(), mNameView)) {
            return mNameView;
        }
        return null;
    }
}
