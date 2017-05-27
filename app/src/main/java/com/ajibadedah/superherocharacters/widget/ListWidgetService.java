package com.ajibadedah.superherocharacters.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ajibadedah.superherocharacters.R;
import com.ajibadedah.superherocharacters.data.CharacterContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.ajibadedah.superherocharacters.MainActivity.STARTING_CHARACTER_ID;

/**
 * Created by ajibade on 5/26/17
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;

    private Cursor data = null;

    ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        // Nothing to do
    }

    @Override
    public void onDataSetChanged() {

        if (data != null) {
            data.close();
        }

        String[] projection = {
                CharacterContract.CharacterEntry._ID,
                CharacterContract.CharacterEntry.COLUMN_CHARACTER_NAME,
                CharacterContract.CharacterEntry.COLUMN_CHARACTER_THUMBNAIL
        };

        final long identityToken = Binder.clearCallingIdentity();
        data = mContext.getContentResolver().query(CharacterContract.CharacterEntry.CONTENT_URI,
                projection, null, null,null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.character_widget_list_item);

        String characterId = data.getString(0);
        String nameText = data.getString(1);
        String url = data.getString(2);
        Bitmap myBitmap;
        try {
            URL myUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }

//        Picasso picasso = Picasso.with(mContext);
//        picasso.load(url).into(views, R.id.image, appWidgetIds);
        if (myBitmap != null) {
            views.setImageViewBitmap(R.id.thumbnail, myBitmap);
        }
        views.setTextViewText(R.id.character_name, nameText);

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(STARTING_CHARACTER_ID, characterId);
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.character_widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {

        if (data.moveToPosition(position)) {
            return Long.parseLong(data.getString(0));
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
