/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.animbus.music.tasks;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.animbus.music.media.objects.MediaObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that needs to be extended in order to load a list of a certain type of object.
 * @param <Return> The type to return when done loading. This type should <b>NOT</b> be a {@link List}
 */
public abstract class Loader<Return extends MediaObject> {
    protected Context context;

    public Loader(Context context, Object... params) {
        this.context = context.getApplicationContext();
        this.runParams = params;
        mObserver = getObserver();
    }

    @WorkerThread
    @Nullable
    protected abstract Return buildObject(@NonNull Cursor cursor);

    ///////////////////////////////////////////////////////////////////////////
    // Used for generating the Cursor
    ///////////////////////////////////////////////////////////////////////////

    protected abstract Uri getUri();

    protected String[] getProjection() {
        return null;
    }

    protected String getSelection() {
        return null;
    }

    protected String[] getSelectionArgs() {
        return null;
    }

    protected String getSortOrder() {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Underlying AsyncTask
    ///////////////////////////////////////////////////////////////////////////

    private LoadTask mTask;

    class LoadTask extends AsyncTask<Object, Return, List<Return>> {
        private boolean isExecuting = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            unregisterMediaStoreListenerTemporarily();
        }

        @Override
        protected List<Return> doInBackground(Object... params) {
            isExecuting = true;
            Cursor cursor = getContext().getContentResolver().query(getUri(), getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
            try {
                //If there is no data return an empty list
                if (cursor == null || !cursor.moveToFirst()) return new ArrayList<>();

                //If there is data then continue
                List<Return> generated = new ArrayList<>();
                do {
                    Return obj = buildObject(cursor);
                    if (obj != null) {
                        obj.setPosInList(cursor.getPosition())
                                .setContext(getContext())
                                .lock();
                        generated.add(obj);
                        notifyOneLoaded(obj);
                    }
                } while (cursor.moveToNext() && !cursor.isClosed() && !isCancelled());
                return generated;
            } finally {
                if (cursor != null && !cursor.isClosed()) cursor.close();
                isExecuting = false;
            }
        }

        @SafeVarargs
        @WorkerThread
        public final void oneLoaded(Return... progress) {
            publishProgress(progress);
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(Return... values) {
            super.onProgressUpdate(values);
            for (Return val : values) mVerifyListener.onOneLoaded(val, val.getPosInList());
        }

        @Override
        protected void onPostExecute(List<Return> result) {
            super.onPostExecute(result);
            sort(result);
            mVerifyListener.onCompleted(result);
            if (!mObserverLock) registerMediaStoreListener();
            if (mUpdateQueued) update(result);
        }

        public boolean isExecuting() {
            return isExecuting;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Run
    ///////////////////////////////////////////////////////////////////////////

    protected Object[] runParams;

    @UiThread
    public void run() {
        if (mTask == null || mTask.getStatus() == AsyncTask.Status.FINISHED) mTask = new LoadTask();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, runParams);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sorting
    ///////////////////////////////////////////////////////////////////////////

    @UiThread
    protected void sort(List<Return> data) {
        //Do nothing
    }

    ///////////////////////////////////////////////////////////////////////////
    // Update
    ///////////////////////////////////////////////////////////////////////////

    //Currently loaded data. When calling update() this will be set, then emptied
    private List<Return> currentData = new ArrayList<>();
    private TaskListener<Return> mVerifyListener = new TaskListener<Return>() {
        @Override
        public void onOneLoaded(Return item, int pos) {
            if (!currentData.contains(item)) for (TaskListener<Return> listener : mListeners) listener.onOneLoaded(item, pos);
        }

        @Override
        public void onCompleted(List<Return> result) {
            if (currentData != result) for (TaskListener<Return> listener : mListeners) listener.onCompleted(result);
            currentData.clear();
        }
    };
    private boolean mUpdateQueued = false;
    protected final ContentObserver mObserver;
    private boolean mObserverLock = false;

    /**
     * Make a content observer to be registered/unregistered. Do <b>NOT</b> pass variables. Whatever is returned here will be set in a final variable and accessed from there
     * @return The content observer
     */
    @Nullable
    @UiThread
    protected ContentObserver getObserver() {
        return null;
    }

    @UiThread
    public void update(final List<Return> currentData) {
        if (mTask != null && !mTask.isExecuting() && currentData != null) {
            mUpdateQueued = false;
            this.currentData = currentData;
            run();
        } else {
            Log.e(getClass().getSimpleName(), "Update: FAILED");
            mUpdateQueued = true;
        }
    }

    /**
     * Registers a {@link ContentObserver} and removes any previous calls to {@link #unregisterMediaStoreListener()}
     */
    @UiThread
    public void registerMediaStoreListener() {
        if (mObserver != null) {
            getContext().getContentResolver().registerContentObserver(getUri(), true, mObserver);
            mObserverLock = false;
        }
    }


    /**
     * Unregisters a content observer until the async task will register it again when it completes.
     */
    @UiThread
    protected void unregisterMediaStoreListenerTemporarily() {
        if (mObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
        }
    }

    /**
     * Same as {@link #unregisterMediaStoreListenerTemporarily()} except it also makes sure the async task doesn't ever register the listener again until {@link #registerMediaStoreListener()} is called from an outside source
     */
    @UiThread
    public void unregisterMediaStoreListener() {
        if (mObserver != null) {
            unregisterMediaStoreListenerTemporarily();
            mObserverLock = true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The listener for {@link Loader} events
     * @param <Return> What type of variable should be passed to the listener. When extending {@link Loader}, you will specify what this should be
     */
    public interface TaskListener<Return> {
        void onOneLoaded(Return item, int pos);

        void onCompleted(List<Return> result);
    }

    protected List<TaskListener<Return>> mListeners = new ArrayList<>();

    @UiThread
    public void addListener(TaskListener<Return> listener) {
        mListeners.add(listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Wrapper method around {@link AsyncTask#publishProgress(Object[])}
     * @param progress What to pass to the AsyncTask
     */
    @WorkerThread
    public void notifyOneLoaded(Return progress){
        mTask.oneLoaded(progress);
    }

    /**
     * @return Application congress taken from provided congress
     */
    public Context getContext() {
        return context;
    }
}
