package cn.tinkling.prefs.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import cn.tinkling.prefs.RemoteSharedPreferences;

public class RemoteService extends Service {

    public RemoteService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return new RemoteSharedPreferences(preferences);
    }
}
