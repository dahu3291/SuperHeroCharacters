package com.ajibadedah.superherocharacters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajibadedah.superherocharacters.data.CharacterContract;
import com.squareup.picasso.Picasso;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterComicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String STARTING_CHARACTER_ID = "character_id";

    private static final int VIEW_TYPE_CHARACTER_LIST = 0;
    private static final int VIEW_TYPE_CHARACTER_WITH_COMICS = 1;

    private Cursor mCursor;
    private Context mContext;
    private boolean mIsCharacter;
    private AdapterClickListener mAdapterItemListener;

    private int mPosition;
    private int lastPosition = -1;
    //make this big first
    private int stopAnimPosition = 20;

    public CharacterComicAdapter(Context context, AdapterClickListener listener, boolean isCharacter) {
        mContext = context;
        mAdapterItemListener = listener;
        mIsCharacter = isCharacter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        View view;
        if (mIsCharacter) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.character_item, parent, false);
            holder = new CharacterAdapterViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.comic_item, parent, false);
            holder = new ComicAdapterViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            if (mIsCharacter) {
                mPosition = position;
                CharacterAdapterViewHolder charHolder = (CharacterAdapterViewHolder) holder;
                charHolder.bind();
            } else {
                ComicAdapterViewHolder comicHolder = (ComicAdapterViewHolder) holder;
                comicHolder.bind();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return Long.getLong(mCursor.getString(0));
    }

    void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public void setLastPosition(int lastPosition){
        this.lastPosition = lastPosition;
    }

    public void setStopAnimPosition(int stopAnimPosition){
        this.stopAnimPosition = stopAnimPosition;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (mCursor.getCount() > 0) {
//            return VIEW_TYPE_CHARACTER_WITH_COMICS;
//        } else if (arrayList.get(position) instanceof Step ||
//                arrayList.get(position) instanceof String) {
//            return VIEW_TYPE_STEPS;
//        } else if (arrayList.get(position) instanceof Ingredient) {
//            return VIEW_TYPE_INGREDIENT;
//        }

//        return -1;
//    }

    public interface AdapterClickListener {
        void ItemClicked(Intent intent, ActivityOptionsCompat options, int id);
    }

    public class CharacterAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;

        public CharacterAdapterViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.character_name);
        }

        @SuppressWarnings("unchecked")
        void bind() {
            String nameText = mCursor.getString(1);
            name.setText(nameText);
            final String textTransitionName = mContext.getString(R.string.transition_text) + mCursor.getString(0);
            name.setTransitionName(textTransitionName);
            name.setTag(textTransitionName);

            String url = mCursor.getString(2);
            Picasso.with(mContext).load(url).into(thumbnail);

            final String thumbnailTransitionName = mContext.getString(R.string.transition_photo) + mCursor.getString(0);
            thumbnail.setTransitionName(thumbnailTransitionName);
            thumbnail.setTag(thumbnailTransitionName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = getAdapterPosition();
                    mCursor.moveToPosition(i);

                    int characterId = mCursor.getInt(mCursor.getColumnIndex(
                            CharacterContract.CharacterEntry._ID));

//                    Pair<View, String> p1 = Pair.create((View) thumbnail, thumbnailTransitionName);
//
//                    Pair<View, String> p2 = Pair.create((View) name, textTransitionName);
                    Activity activity = (Activity) mContext;
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity);

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(STARTING_CHARACTER_ID, characterId);
                    mAdapterItemListener.ItemClicked(intent, options, characterId);
                }
            });

            //only animate when view is first shown
            if (stopAnimPosition < 0) stopAnimPosition = 20; //onResume in MainActivity makes this -1 at first
            if(mPosition >lastPosition & mPosition < stopAnimPosition) {

                Animation animation = AnimationUtils.loadAnimation(mContext,
                        R.anim.up_from_bottom);
                itemView.startAnimation(animation);
                lastPosition = mPosition;
            }

        }
    }

    public class ComicAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;

        public ComicAdapterViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.comic_name);
        }

        void bind() {
            String nameText = mCursor.getString(1);
            name.setText(nameText);

            String url = mCursor.getString(2);
            Picasso.with(mContext).load(url).into(thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = getAdapterPosition();
                    mCursor.moveToPosition(i);

                    int charId = mCursor.getInt(mCursor.getColumnIndex(
                            CharacterContract.CharacterEntry._ID));
//                    mAdapterItemListener.ItemClicked(charId);
                }
            });

        }
    }
}
