package com.ajibadedah.superherocharacters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajibadedah.superherocharacters.data.CharacterContract;
import com.squareup.picasso.Picasso;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterComicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_CHARACTER_LIST = 0;
    private static final int VIEW_TYPE_CHARACTER_WITH_COMICS = 1;

    private Cursor mCursor;
    private Context mContext;
    private boolean mIsCharacter;
    private AdapterClickListener mAdapterItemListener;

    public CharacterComicAdapter(Context context, AdapterClickListener listener, boolean isCharacter){
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
        if (mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        notifyDataSetChanged();
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

    public interface AdapterClickListener{
        void ItemClicked(int id);
    }

    public class CharacterAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;

        public CharacterAdapterViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.character_name);
        }

        void bind(){
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
                    mAdapterItemListener.ItemClicked(charId);
                }
            });

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

        void bind(){
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
                    mAdapterItemListener.ItemClicked(charId);
                }
            });

        }
    }
}
