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

    public RemoteService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final RemoteSharedPreferences remotePrefs = new RemoteSharedPreferences(prefs);

        if (ACTION_REMOTE_SHARED_PREFERENCES.equals(action)) {
            return remotePrefs;
        } else if (ACTION_REMOTE_SHARED_PREFERENCES_AIDL.equals(action)) {
            return new IMyAidlInterface.Stub() {
                @Override
                public IRemoteSharedPreferences getRemoteSharedPreferences()
                        throws RemoteException {
                    return remotePrefs;
                }
            };
        }

        throw new UnsupportedOperationException("Action:" + action);
    }
}
