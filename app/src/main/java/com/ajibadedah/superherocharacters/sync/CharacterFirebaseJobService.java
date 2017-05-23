package com.ajibadedah.superherocharacters.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.ajibadedah.superherocharacters.model.Character;
import com.ajibadedah.superherocharacters.utility.NetworkUtil;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.List;

import retrofit2.Call;

/**
 * Created by ajibade on 5/22/17
 */

public class CharacterFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchCharacterTask;


    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchCharacterTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                SyncTask.doSomething(CharacterFirebaseJobService.this);
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchCharacterTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchCharacterTask != null) {
            mFetchCharacterTask.cancel(true);
        }
        return true;    }
}
