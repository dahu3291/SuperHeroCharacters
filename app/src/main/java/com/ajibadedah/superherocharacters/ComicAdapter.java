package com.ajibadedah.superherocharacters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
    private ComicAdapter.AdapterClickListener mAdapterItemListener;

    public ComicAdapter(Context context, ComicAdapter.AdapterClickListener listener,
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
        void ItemClicked(View view, String imageName, String imageUrl);
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

    public class ComicAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView thumbnail;
        TextView name;

        String url;
        String imageName;

        public ComicAdapterViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.comic_name);
        }

        void bind(int postion){
            imageName = mComics.get(postion).getTitle();
            name.setText(imageName);

            url = mComics.get(postion).getThumbnail().getFullUrl();
            Picasso.with(mContext).load(url).into(thumbnail);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mAdapterItemListener.ItemClicked(thumbnail, imageName, url);
            return false;
        }
    }
}
