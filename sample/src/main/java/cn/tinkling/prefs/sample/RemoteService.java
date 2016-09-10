package cn.tinkling.prefs.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

import cn.tinkling.prefs.IRemoteSharedPreferences;
import cn.tinkling.prefs.RemoteSharedPreferences;

public class RemoteService extends Service {

    public static final String ACTION_REMOTE_SHARED_PREFERENCES = "remote_shared_preferences";
    public static final String ACTION_REMOTE_SHARED_PREFERENCES_AIDL =
            "remote_shared_preferences_aidl";

    private RemoteSharedPreferences mRemotePrefsBinder;
    private RemoteSharedPreferences mRemotePrefsAidl;

    private final IBinder mMyAidlInterface = new IMyAidlInterface.Stub() {
        @Override
        public IRemoteSharedPreferences getRemoteSharedPreferences() throws RemoteException {
            return mRemotePrefsAidl;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefsBinder =
                getSharedPreferences("preferences-binder", Context.MODE_PRIVATE);
        mRemotePrefsBinder = new RemoteSharedPreferences(prefsBinder);

        SharedPreferences prefsAidl =
                getSharedPreferences("preferences-aidl", Context.MODE_PRIVATE);
        mRemotePrefsAidl = new RemoteSharedPreferences(prefsAidl);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        if (ACTION_REMOTE_SHARED_PREFERENCES.equals(action)) { // binder
            return mRemotePrefsBinder.asBinder();
        } else if (ACTION_REMOTE_SHARED_PREFERENCES_AIDL.equals(action)) { // aidl
            return mMyAidlInterface;
        }

        throw new UnsupportedOperationException("Action:" + action);
    }
}
