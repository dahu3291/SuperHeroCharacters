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

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterAdapterViewHolder>{

    Cursor mCursor;
    Context mContext;

    public CharacterAdapter(Context context){
        mContext = context;
    }

    @Override
    public CharacterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.character_item, parent, false);

        return new CharacterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterAdapter.CharacterAdapterViewHolder holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            holder.bind();
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

        }
    }
}
