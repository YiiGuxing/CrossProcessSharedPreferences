package cn.tinkling.prefs;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Wraps an existing {@link SharedPreferences} making it remotable.
 * <p>
 * Created by tinkling on 15/12/17.
 */
public class RemoteSharedPreferences extends IRemoteSharedPreferences.Stub {

    private static final String TAG = "RemoteSharedPreferences";

    private final SharedPreferences mSharedPreferences;

    final RemoteCallbackList<IOnSharedPreferenceChangeListener> mListenerList =
            new RemoteCallbackList<>();
    /* 不怕内存泄漏？如果mSharedPreferences来自于Context.getSharedPreferences()，那么不必担心，因为默认的
     * SharedPreferences的实现对Listener的引用为弱引用。但如果mSharedPreferences来自于其他来源，那就需要考
     * 虑考虑内存泄漏泄漏的问题了。 */
    private final SharedPreferences.OnSharedPreferenceChangeListener mListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    final int N = mListenerList.beginBroadcast();
                    for (int i = 0; i < N; i++) {
                        try {
                            mListenerList.getBroadcastItem(i).onSharedPreferenceChanged(key);
                        } catch (RemoteException e) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                    }
                    mListenerList.finishBroadcast();
                }
            };

    private int mRegisteredCount;


    public RemoteSharedPreferences(@NonNull SharedPreferences prefs) {
        mSharedPreferences = prefs;
    }

    public RemoteSharedPreferencesDescriptor getSharedPreferencesDescriptor() {
        synchronized (this) {
            return new RemoteSharedPreferencesDescriptor(this);
        }
    }

    @Override
    public Map<String, ?> getAll() throws RemoteException {
        return mSharedPreferences.getAll();
    }

    @Override
    public String getString(String key, String defValue) throws RemoteException {
        return mSharedPreferences.getString(key, defValue);
    }

    @Override
    public String[] getStringArray(String key) throws RemoteException {
        Set<String> v = mSharedPreferences.getStringSet(key, null);
        if (v != null) {
            String[] result = new String[v.size()];
            result = v.toArray(result);

            return result;
        }

        return null;
    }

    @Override
    public int getInt(String key, int defValue) throws RemoteException {
        return mSharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) throws RemoteException {
        return mSharedPreferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) throws RemoteException {
        return mSharedPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) throws RemoteException {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) throws RemoteException {
        return mSharedPreferences.contains(key);
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public IRemoteEditor edit() throws RemoteException {
        return new RemoteEditor(mSharedPreferences.edit());
    }

    @Override
    public boolean registerOnSharedPreferenceChangeListener(
            IOnSharedPreferenceChangeListener listener)
            throws RemoteException {
        synchronized (this) {
            if (listener != null) {
                if (mListenerList.register(listener)) {
                    if (mRegisteredCount == 0) {
                        mSharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        mRegisteredCount = mListenerList.getRegisteredCallbackCount();
                    } else {
                        mRegisteredCount++;
                    }

                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean unregisterOnSharedPreferenceChangeListener(
            IOnSharedPreferenceChangeListener listener) throws RemoteException {
        synchronized (this) {
            boolean result = false;
            if (listener != null) {
                result = mListenerList.unregister(listener);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mRegisteredCount = mListenerList.getRegisteredCallbackCount();
                } else {
                    mRegisteredCount--;
                }

                if (mRegisteredCount == 0) {
                    mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
                }
            }

            return result;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        Log.w(TAG, "finalize!!!");

        try {
            synchronized (this) {
                mListenerList.kill();

                if (mRegisteredCount > 0) {
                    mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
                }

                mRegisteredCount = 0;
            }
        } finally {
            super.finalize();
        }
    }
}
