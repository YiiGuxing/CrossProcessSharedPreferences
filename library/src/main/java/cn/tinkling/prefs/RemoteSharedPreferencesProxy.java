package cn.tinkling.prefs;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Adapts an {@link IRemoteSharedPreferences} to a {@link SharedPreferences} for use
 * in the local process.
 * <p>
 * Created by tinkling on 15/12/18.
 */
public class RemoteSharedPreferencesProxy implements SharedPreferences {

    private static final String TAG = "RemoteSharedPrefsProxy";

    /* 暂时引用mRemote，以防止其被回收，导致在finalize()方法无法反注册IOnSharedPreferenceChangeListener，
     * 从而导致OnSharedPreferenceChangeListenerProxy泄漏而无法被回收。 */
    private static final List<IRemoteSharedPreferences> sRemoteHolder =
            Collections.synchronizedList(new LinkedList<IRemoteSharedPreferences>());
    private static final Object sContent = new Object();

    private final IRemoteSharedPreferences mRemote;
    private IOnSharedPreferenceChangeListener mOnChangeListener;
    private boolean mListenerRegistered;

    final WeakHashMap<OnSharedPreferenceChangeListener, Object> mListeners = new WeakHashMap<>();

    public RemoteSharedPreferencesProxy(@NonNull RemoteSharedPreferencesDescriptor d) {
        this(d.remoteSharedPreferences);
    }

    public RemoteSharedPreferencesProxy(@NonNull IRemoteSharedPreferences sharedPreferences) {
        mRemote = sharedPreferences;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, ?> getAll() {
        try {
            return (Map<String, ?>) mRemote.getAll();
        } catch (RemoteException e) {
            Log.w(TAG, "getAll() threw RemoteException, returning empty map.", e);
        }

        return Collections.EMPTY_MAP;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        try {
            return mRemote.getString(key, defValue);
        } catch (RemoteException e) {
            Log.w(TAG, "getString() threw RemoteException, returning default value.", e);
        }

        return defValue;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            String[] strings = mRemote.getStringArray(key);
            if (strings != null) {
                return new HashSet<>(Arrays.asList(strings));
            }
        } catch (RemoteException e) {
            Log.w(TAG, "getStringSet() threw RemoteException, returning default value.", e);
        }


        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            return mRemote.getInt(key, defValue);
        } catch (RemoteException e) {
            Log.w(TAG, "getInt() threw RemoteException, returning default value.", e);
        }

        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            return mRemote.getLong(key, defValue);
        } catch (RemoteException e) {
            Log.w(TAG, "getLong() threw RemoteException, returning default value.", e);
        }

        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            return mRemote.getFloat(key, defValue);
        } catch (RemoteException e) {
            Log.w(TAG, "getFloat() threw RemoteException, returning default value.", e);
        }

        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return mRemote.getBoolean(key, defValue);
        } catch (RemoteException e) {
            Log.w(TAG, "getBoolean() threw RemoteException, returning default value.", e);
        }

        return defValue;
    }

    @Override
    public boolean contains(String key) {
        try {
            return mRemote.contains(key);
        } catch (RemoteException e) {
            Log.w(TAG, "contains() threw RemoteException, returning false.", e);
        }

        return false;
    }

    @Override
    public Editor edit() {
        try {
            return new RemoteEditorProxy(mRemote.edit());
        } catch (RemoteException e) {
            throw new AndroidRuntimeException(
                    "Unable to get edit because the remote process is dead.", e);
        }
    }

    private boolean registerOnRemoteSharedPreferenceChangeListener() {
        try {
            if (!mListenerRegistered) {
                if (mOnChangeListener == null) {
                    mOnChangeListener = new OnSharedPreferenceChangeListenerProxy(this);
                }

                mListenerRegistered =
                        mRemote.registerOnSharedPreferenceChangeListener(mOnChangeListener);
                sRemoteHolder.add(mRemote);
            }
            return mListenerRegistered;
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to register listener because the remote process is dead.", e);
        }

        return false;
    }

    private void unregisterOnRemoteSharedPreferenceChangeListener() {
        try {
            if (mListenerRegistered) {
                mRemote.unregisterOnSharedPreferenceChangeListener(mOnChangeListener);
                mListenerRegistered = false;
                sRemoteHolder.remove(mRemote);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to register listener because the remote process is dead.", e);
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            if (listener != null && registerOnRemoteSharedPreferenceChangeListener()) {
                mListeners.put(listener, sContent);
            }
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            if (listener != null) {
                mListeners.remove(listener);

                if (mListeners.size() == 0) {
                    unregisterOnRemoteSharedPreferenceChangeListener();
                }
            }
        }
    }

    Set<OnSharedPreferenceChangeListener> getListenerSet() {
        synchronized (this) {
            return mListeners.keySet();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            synchronized (this) {
                unregisterOnRemoteSharedPreferenceChangeListener();
            }
        } finally {
            super.finalize();
        }
    }

    private static class OnSharedPreferenceChangeListenerProxy
            extends IOnSharedPreferenceChangeListener.Stub {

        private Handler mHandler = new Handler(Looper.getMainLooper());
        private Reference<RemoteSharedPreferencesProxy> mPrefs;

        OnSharedPreferenceChangeListenerProxy(RemoteSharedPreferencesProxy prefs) {
            mPrefs = new WeakReference<>(prefs);
        }

        @Override
        public void onSharedPreferenceChanged(final String key) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    RemoteSharedPreferencesProxy prefs = mPrefs.get();
                    if (prefs == null)
                        return;

                    Set<OnSharedPreferenceChangeListener> listeners = prefs.getListenerSet();
                    for (OnSharedPreferenceChangeListener listener : listeners) {
                        if (listener != null)
                            listener.onSharedPreferenceChanged(prefs, key);
                    }
                }
            });
        }

    }

}
