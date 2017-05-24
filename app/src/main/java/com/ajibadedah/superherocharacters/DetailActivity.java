package com.ajibadedah.superherocharacters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.ajibadedah.superherocharacters.data.CharacterContract.CharacterEntry;

import static com.ajibadedah.superherocharacters.MainActivity.STARTING_CHARACTER_ID;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ID_DETAIL_LOADER = 22;
    private CharacterComicAdapter mCharacterComicAdapter;
    private MyPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private DetailFragment mCurrentDetailsFragment;
    private int mStartId;
    private int mSelectedItemId;

    private Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStartId = getIntent().getIntExtra(STARTING_CHARACTER_ID, 0);
        mSelectedItemId = mStartId;


        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    int index = mCursor.getColumnIndex(CharacterEntry._ID);
                    mSelectedItemId = mCursor.getInt(index);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, CharacterEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId >= 0) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                int index = mCursor.getColumnIndex(CharacterEntry._ID);
                if (mCursor.getInt(index) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentDetailsFragment = (DetailFragment) object;
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            int index = mCursor.getColumnIndex(CharacterEntry._ID);
            return DetailFragment.newInstance(mCursor.getInt(index), position, mStartId);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
