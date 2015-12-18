package cn.tinkling.prefs;

import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.util.Set;

/**
 * Adapts an {@link IRemoteEditor} to a {@link SharedPreferences.Editor} for use
 * in the local process.
 * <p>
 * Created by tinkling on 15/12/18.
 */
public class RemoteEditorProxy implements SharedPreferences.Editor {

    private static final String TAG = "RemoteEditorProxy";

    private final IRemoteEditor mRemote;

    public RemoteEditorProxy(@NonNull IRemoteEditor remoteEditor) {
        mRemote = remoteEditor;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        try {
            mRemote.putString(key, value);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to put value because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
        if (values == null)
            return remove(key);

        try {
            String[] vs = new String[values.size()];
            vs = values.toArray(vs);
            mRemote.putStringArray(key, vs);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to put values because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        try {
            mRemote.putInt(key, value);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to put value because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        try {
            mRemote.putLong(key, value);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to put value because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        try {
            mRemote.putFloat(key, value);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to put value because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        try {
            mRemote.putBoolean(key, value);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to put value because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor remove(String key) {
        try {
            mRemote.remove(key);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to remove because the remote process is dead.", e);
        }

        return this;
    }

    @NonNull
    @Override
    public SharedPreferences.Editor clear() {
        try {
            mRemote.clear();
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to clear because the remote process is dead.", e);
        }

        return this;
    }

    @Override
    public boolean commit() {
        try {
            return mRemote.commit();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to commit because the remote process is dead.", e);
        }

        return false;
    }

    @Override
    public void apply() {
        try {
            mRemote.apply();
        } catch (RemoteException e) {
            throw new AndroidRuntimeException("Unable to apply because the remote process is dead.",
                    e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        Log.w(TAG, "finalize!!!");

        super.finalize();
    }

}
