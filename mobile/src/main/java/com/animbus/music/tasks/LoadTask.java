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
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

abstract class LoadTask<Return> extends AsyncTask<Object, Return, List<Return>> {
    protected Context context;
    private boolean isExecuting = false;
    protected List<TaskListener<Return>> mListeners = new ArrayList<>();

    public LoadTask(Context context, Object... params) {
        this.context = context;
        this.runParams = params;
    }

    protected abstract List<Return> doJob(Object... params);

    ///////////////////////////////////////////////////////////////////////////
    // Update
    ///////////////////////////////////////////////////////////////////////////

    private List<Return> currentData = new ArrayList<>();
    private TaskListener<Return> mVerifyListener = new TaskListener<Return>() {
        @Override
        public void onOneLoaded(Return item) {
            if (!currentData.contains(item)) for (TaskListener<Return> listener : mListeners) listener.onOneLoaded(item);
        }

        @Override
        public void onCompleted(List<Return> result) {
            if (currentData != result) for (TaskListener<Return> listener : mListeners) listener.onCompleted(result);
        }
    };
    private boolean mUpdateQueued = false;

    public void update(final List<Return> currentData) {
        if (!isExecuting) {
            mUpdateQueued = false;
            this.currentData = currentData;
            run();
        } else {
            Log.e("LoadTask", "Update failed: is currently executing");
            mUpdateQueued = true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper/Convenience methods
    ///////////////////////////////////////////////////////////////////////////

    protected Object[] runParams;

    public void run() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, runParams);
    }

    public void addListener(TaskListener<Return> listener) {
        mListeners.add(listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Underlying AsyncTask
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected List<Return> doInBackground(Object... params) {
        isExecuting = true;
        try {
            return doJob(params);
        } finally {
            isExecuting = false;
        }
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Return... values) {
        super.onProgressUpdate(values);
        for (Return val : values) mVerifyListener.onOneLoaded(val);
    }

    @Override
    protected void onPostExecute(List<Return> result) {
        super.onPostExecute(result);
        mVerifyListener.onCompleted(result);
        if (mUpdateQueued) update(result);
    }
}
