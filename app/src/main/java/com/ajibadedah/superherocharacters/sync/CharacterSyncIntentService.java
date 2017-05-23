package com.ajibadedah.superherocharacters.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterSyncIntentService extends IntentService {

    public CharacterSyncIntentService() {
        super("CharacterSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SyncTask.doSomething(this);
    }
}
