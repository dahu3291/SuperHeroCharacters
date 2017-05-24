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
import com.ajibadedah.superherocharacters.model.Comic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ajibade on 5/24/17
 */

public class ComicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Comic> mComics;
    private Context mContext;
    private CharacterComicAdapter.AdapterClickListener mAdapterItemListener;

    public ComicAdapter(Context context, CharacterComicAdapter.AdapterClickListener listener,
                        ArrayList<Comic> comics){
        mContext = context;
        mAdapterItemListener = listener;
        this.mComics = comics;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        View view;
        view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.comic_item, parent, false);
        holder = new ComicAdapterViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mComics != null) {
            if (mComics.size() > 0) {

                ComicAdapterViewHolder comicHolder = (ComicAdapterViewHolder) holder;
                comicHolder.bind(position);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mComics != null ? mComics.size() : 0;
    }
    public interface AdapterClickListener{
        void ItemClicked(int id);
    }

//    @Override
//    public long getItemId(int position) {
//        return comics.get(position);
//    }

//    void swapComics(ArrayList<Comic> newComics) {
//        if (comics != null){
//            comics = null;
//        }
//        comics = newComics;
//        notifyDataSetChanged();
//    }

    public class ComicAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;

        public ComicAdapterViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.comic_name);
        }

        void bind(int postion){
            String nameText = mComics.get(postion).getTitle();
            name.setText(nameText);

            String url = mComics.get(postion).getThumbnail().getFullUrl();
            Picasso.with(mContext).load(url).into(thumbnail);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int i = getAdapterPosition();
//                    mCursor.moveToPosition(i);
//
//                    int charId = mCursor.getInt(mCursor.getColumnIndex(
//                            CharacterContract.CharacterEntry._ID));
//                    mAdapterItemListener.ItemClicked(charId);
//                }
//            });

        }
    }
}
