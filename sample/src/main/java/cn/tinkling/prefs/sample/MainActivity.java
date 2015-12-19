package cn.tinkling.prefs.sample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashSet;
import java.util.Random;

import cn.tinkling.prefs.RemoteSharedPreferences;
import cn.tinkling.prefs.RemoteSharedPreferencesDescriptor;
import cn.tinkling.prefs.RemoteSharedPreferencesProxy;

public class MainActivity extends AppCompatActivity {

    private static final int FROM_BUNDLE = 0;
    private static final int FROM_BINDER = 1;

    SharedPreferences fromBundle;
    PreferenceChangeListener listenerBundle;

    SharedPreferences fromBinder;
    PreferenceChangeListener listenerBinder;


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            fromBinder = new RemoteSharedPreferencesProxy(RemoteSharedPreferences.asInterface(service));
            listenerBinder = new PreferenceChangeListener(FROM_BINDER);
            fromBinder.registerOnSharedPreferenceChangeListener(listenerBinder);
            onRemoteSharedPreferencesConnected(fromBinder, FROM_BINDER);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = Uri.parse("content://cn.tinkling.prefs");
        Bundle call = getContentResolver().call(uri, "getRemoteSharedPreferences", null, null);
        call.setClassLoader(RemoteSharedPreferencesDescriptor.class.getClassLoader());

        RemoteSharedPreferencesDescriptor d = call.getParcelable("preferences");
        fromBundle = new RemoteSharedPreferencesProxy(d);
        listenerBundle = new PreferenceChangeListener(FROM_BUNDLE);
        fromBundle.registerOnSharedPreferenceChangeListener(listenerBundle);
        onRemoteSharedPreferencesConnected(fromBundle, FROM_BUNDLE);

        Intent binder = new Intent(this, RemoteService.class);
        bindService(binder, conn, BIND_AUTO_CREATE);
    }

    private void onRemoteSharedPreferencesConnected(final SharedPreferences preferences, final int from) {
        log(from, "===================================");
        log(from, "getInt:       " + preferences.getInt("int", -1));
        log(from, "getLong:      " + preferences.getLong("long", -1));
        log(from, "getFloat:     " + preferences.getFloat("float", -1));
        log(from, "getString:    " + preferences.getString("string", null));
        log(from, "===================================");

        SharedPreferences.Editor editor = preferences.edit();
        Random random = new Random();
        editor.putInt("int", random.nextInt());
        editor.putLong("long", random.nextLong());
        editor.putFloat("float", random.nextFloat());
        editor.putString("string", "string---");
        editor.putBoolean("boolean", true);
        HashSet<String> set = new HashSet<>();
        set.add("set1");
        set.add("set2");
        set.add("set3");
        set.add("set4");
        editor.putStringSet("stringSet", set);
        editor.apply();

        log(from, "===================================");
        log(from, "getInt:       " + preferences.getInt("int", -1));
        log(from, "getLong:      " + preferences.getLong("long", -1));
        log(from, "getFloat:     " + preferences.getFloat("float", -1));
        log(from, "getString:    " + preferences.getString("string", null));
        log(from, "getBoolean:   " + preferences.getBoolean("boolean", false));
        log(from, "getStringSet: " + preferences.getStringSet("stringSet", null));
        log(from, "getAll:       " + preferences.getAll());
        log(from, "===================================");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = preferences.edit();

                editor.putInt("int", 100);
                editor.putLong("long", 200);
                editor.putFloat("float", 300.8f);
                editor.remove("string");
                editor.commit();

                log(from, "===================================");
                log(from, "getInt:       " + preferences.getInt("int", -1));
                log(from, "getLong:      " + preferences.getLong("long", -1));
                log(from, "getFloat:     " + preferences.getFloat("float", -1));
                log(from, "getString:    " + preferences.getString("string", null));
                log(from, "getAll:       " + preferences.getAll());
                log(from, "===================================");

                editor.clear();
                editor.apply();
                log(from, "getAll:       " + preferences.getAll());

                editor.putFloat("float", 333.8f);
                editor.putString("string", "ABCDEF");
                editor.apply();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        fromBundle.unregisterOnSharedPreferenceChangeListener(listenerBundle);
        fromBinder.unregisterOnSharedPreferenceChangeListener(listenerBinder);

        unbindService(conn);

        super.onDestroy();
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d("MainActivity", "finalize!!!");
        super.finalize();
    }

    static void log(int from, String msg) {
        switch (from) {
            case FROM_BUNDLE:
                Log.d("SharedPreferences", "Bundle - " + msg);
                break;
            case FROM_BINDER:
                Log.i("SharedPreferences", "Binder - " + msg);
                break;
        }
    }

    static class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        private int from;

        PreferenceChangeListener(int from) {
            this.from = from;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            log(from, "onSharedPreferenceChanged: key=" + key);
        }
    }

}
