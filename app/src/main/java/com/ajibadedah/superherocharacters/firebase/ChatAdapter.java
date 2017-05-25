package com.ajibadedah.superherocharacters.firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ajibadedah.superherocharacters.CustomImageView;
import com.ajibadedah.superherocharacters.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ajibade on 5/24/17
 */

class ChatAdapter  extends ArrayAdapter<Chat> {

    ChatAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.chat_item, parent, false);
        }

        CustomImageView photoImageView = (CustomImageView) convertView.findViewById(R.id.photoImageView);
        TextView referenceTextView = (TextView) convertView.findViewById(R.id.referenceTextView);
        TextView chatTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        Chat chat = getItem(position);

        if (chat != null) {
            boolean isPhoto = chat.getPhotoUrl() != null;
            if (isPhoto) {
                chatTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                Picasso.with(photoImageView.getContext())
                        .load(chat.getPhotoUrl())
                        .into(photoImageView);
            }

            if (chat.getTextReference() != null && !chat.getTextReference().equals("")){
                referenceTextView.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                referenceTextView.setText(chat.getTextReference());
            }

            if (chat.getTextChat() != null && !chat.getTextChat().equals("")){
                chatTextView.setVisibility(View.VISIBLE);
                chatTextView.setText(chat.getTextChat());
            }
            authorTextView.setText(chat.getName());
        }

        return convertView;
    }

}
