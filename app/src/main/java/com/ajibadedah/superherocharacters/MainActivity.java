package com.ajibadedah.superherocharacters;

import android.content.Intent;
import android.database.Cursor;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ajibadedah.superherocharacters.data.CharacterContract.CharacterEntry;
import com.ajibadedah.superherocharacters.firebase.ChatActivity;
import com.ajibadedah.superherocharacters.sync.CharacterSyncManager;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, CharacterComicAdapter.AdapterClickListener
        , FragmentManager.OnBackStackChangedListener{

    public static final String STARTING_CHARACTER_ID = "character_id";
    static final String EXTRA_STARTING_CHARACTER_POSITION = "extra_starting_item_position";
    static final String EXTRA_CURRENT_CHARACTER_POSITION = "extra_current_item_position";
    private static final int ID_CHARACTER_LOADER = 44;
    private static final String CHARACTER_DETAIL_FRAGMENT_TAG = "character_detail_fragment_tag";

    private boolean mTwoPane;
    private FloatingActionButton fab;

    private CharacterComicAdapter mCharacterComicAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private Bundle mTmpReenterState;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_CHARACTER_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_CHARACTER_POSITION);
                if (startingPosition != currentPosition) {
                    // If startingPosition != currentPosition the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.

                    String imageTransitionName = getString(R.string.transition_photo) + currentPosition;
                    View imageSharedElement = mRecyclerView.findViewWithTag(imageTransitionName);
//                    mRecyclerView.findViewHolderForAdapterPosition(currentPosition).itemView.findViewById(R.id.thumbnail)
                    if (imageSharedElement != null) {
                        names.clear();
                        names.add(imageTransitionName);
                        sharedElements.clear();
                        sharedElements.put(imageTransitionName, imageSharedElement);
                    }

                    String textTransitionName = getString(R.string.transition_text) + currentPosition;
                    View textSharedElement = mRecyclerView.findViewWithTag(textTransitionName);
                    if (textSharedElement != null) {
                        names.clear();
                        names.add(textTransitionName);
                        sharedElements.clear();
                        sharedElements.put(textTransitionName, textSharedElement);
                    }
                }

                mTmpReenterState = null;
            } else {
                // If mTmpReenterState is null, then the activity is exiting.
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };
    private boolean mIsDetailsActivityStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setExitSharedElementCallback(mCallback);

        // Determine if you're creating a two-pane or single-pane display
        mTwoPane = findViewById(R.id.detail_container) != null ;

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mCharacterComicAdapter = new CharacterComicAdapter(this, this, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.character_list_recycler);
        mRecyclerView.setAdapter(mCharacterComicAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        CharacterSyncManager.getInstance(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        getSupportLoaderManager().initLoader(ID_CHARACTER_LOADER, null, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsDetailsActivityStarted = false;

        // dont animate more than we need to
        int stopPos = layoutManager.findLastVisibleItemPosition();
        mCharacterComicAdapter.setStopAnimPosition(stopPos);

        if (mTwoPane){
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (getIntent().hasExtra(STARTING_CHARACTER_ID)){
            String stringId = getIntent().getStringExtra(STARTING_CHARACTER_ID);
            int characterId = Integer.parseInt(stringId);
            startDetailFragment(characterId);
        }

        transitionMainActivityBounds();
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = data.getIntExtra(EXTRA_STARTING_CHARACTER_POSITION,0);
        int currentPosition = data.getIntExtra(EXTRA_CURRENT_CHARACTER_POSITION,0);
        if (startingPosition != currentPosition) {
            mRecyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                mRecyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });

        //reset animation
        mCharacterComicAdapter.setLastPosition(-1);
        mCharacterComicAdapter.notifyDataSetChanged();
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
        int stopPos = layoutManager.findLastVisibleItemPosition();
        mCharacterComicAdapter.setStopAnimPosition(stopPos);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCharacterComicAdapter.swapCursor(null);
    }

    @Override
    public void onBackStackChanged() {
        transitionMainActivityBounds();
    }

    @Override
    public void ItemClicked(Intent intent, ActivityOptionsCompat options, int characterId) {
//        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra(STARTING_CHARACTER_ID, id);
        if (mTwoPane){
            startDetailFragment(characterId);
        }
        else if (!mIsDetailsActivityStarted) {
            mIsDetailsActivityStarted = true;
            startActivity(intent, options.toBundle());
        }
    }

    private void transitionMainActivityBounds(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detail_container);
        if (fragment != null) {
            fab.setVisibility(View.VISIBLE);

            ConstraintSet set = new ConstraintSet();
            ConstraintLayout mConstraintLayout = (ConstraintLayout) findViewById(R.id.activity_main_layout);
            set.clone(mConstraintLayout);
            set.setGuidelinePercent(R.id.start_divider, 0.0f);
            set.setGuidelinePercent(R.id.end_divider, 0.4f);
            set.applyTo(mConstraintLayout);
        } else {
            if (fab != null ) fab.setVisibility(View.GONE);

            ConstraintSet set = new ConstraintSet();
            ConstraintLayout mConstraintLayout = (ConstraintLayout) findViewById(R.id.activity_main_layout);
            set.clone(mConstraintLayout);
            set.setGuidelinePercent(R.id.start_divider, 0.25f);
            set.setGuidelinePercent(R.id.end_divider, 0.75f);
            set.applyTo(mConstraintLayout);
        }
    }

    private void startDetailFragment(int characterId){
        fab.setVisibility(View.VISIBLE);

        DetailFragment newFragment = DetailFragment.newInstance(characterId);
//            newFragment.setSharedElementEnterTransition(new DetailsTransition());
        newFragment.setEnterTransition(new DetailsTransition());
        newFragment.setExitTransition(new Fade());
        newFragment.setSharedElementReturnTransition(new DetailsTransition());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, newFragment, CHARACTER_DETAIL_FRAGMENT_TAG)
                .addToBackStack(CHARACTER_DETAIL_FRAGMENT_TAG)
                .commit();
    }

    private class DetailsTransition extends TransitionSet {
        DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            setDuration(500);
            addTransition(new Slide(Gravity.BOTTOM));
//            addTransition(new ChangeBounds()).
//                    addTransition(new ChangeTransform()).
//                    addTransition(new ChangeImageTransform());
        }
    }
}
