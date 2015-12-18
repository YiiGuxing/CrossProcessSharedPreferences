package cn.tinkling.prefs;

import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Wraps an existing {@link SharedPreferences.Editor} making it remotable.
 * <p>
 * Created by tinkling on 15/12/18.
 */
public class RemoteEditor extends IRemoteEditor.Stub {

    private static final String TAG = "RemoteEditor";

    private final SharedPreferences.Editor mEditor;

    public RemoteEditor(@NonNull SharedPreferences.Editor editor) {
        mEditor = editor;
    }

    @NonNull
    @Override
    public IRemoteEditor putString(String key, String value) throws RemoteException {
        mEditor.putString(key, value);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor putStringArray(String key, String[] values) throws RemoteException {
        mEditor.putStringSet(key, values != null ? new HashSet<>(Arrays.asList(values)) : null);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor putInt(String key, int value) throws RemoteException {
        mEditor.putInt(key, value);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor putLong(String key, long value) throws RemoteException {
        mEditor.putLong(key, value);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor putFloat(String key, float value) throws RemoteException {
        mEditor.putFloat(key, value);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor putBoolean(String key, boolean value) throws RemoteException {
        mEditor.putBoolean(key, value);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor remove(String key) throws RemoteException {
        mEditor.remove(key);
        return this;
    }

    @NonNull
    @Override
    public IRemoteEditor clear() throws RemoteException {
        mEditor.clear();
        return this;
    }

    @Override
    public boolean commit() throws RemoteException {
        return mEditor.commit();
    }

    @Override
    public void apply() throws RemoteException {
        mEditor.apply();
    }

    @Override
    protected void finalize() throws Throwable {
        Log.w(TAG, "finalize!!!");

        super.finalize();
    }

}
