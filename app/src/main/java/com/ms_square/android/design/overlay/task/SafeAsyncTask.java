package com.ms_square.android.design.overlay.task;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class SafeAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    /**
     * Keep a weak reference to the activity to cancel automatically
     * if the activity is stopped
     */
    private final WeakReference<Activity> mWeakActivity;

    public SafeAsyncTask(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    @SafeVarargs
    @Override
    protected final Result doInBackground(Params... params) {
        return onRun(params);
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Progress... values) {
        onProgress(values);
    }

    @Override
    protected final void onPostExecute(Result result) {
        if(canContinue()) {
            onSuccess(result);
        }
    }

    private boolean canContinue() {
        Activity activity = mWeakActivity.get();
        return activity != null && !activity.isFinishing();
    }

    @SuppressWarnings("unchecked")
    protected void onProgress(Progress... values) {}

    @SuppressWarnings("unchecked")
    protected abstract Result onRun(Params... params);

    @SuppressWarnings("unchecked")
    protected abstract void onSuccess(Result result);
}