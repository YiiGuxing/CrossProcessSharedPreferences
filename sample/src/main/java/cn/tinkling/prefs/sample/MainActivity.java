package cn.tinkling.prefs.sample;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashSet;

import cn.tinkling.prefs.RemoteSharedPreferencesDescriptor;
import cn.tinkling.prefs.RemoteSharedPreferencesProxy;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = Uri.parse("content://cn.tinkling.prefs");
        Bundle call = getContentResolver().call(uri, "getRemoteSharedPreferences", null, null);
        call.setClassLoader(RemoteSharedPreferencesDescriptor.class.getClassLoader());

        RemoteSharedPreferencesDescriptor d = call.getParcelable("preferences");
        preferences = new RemoteSharedPreferencesProxy(d);

        preferences.registerOnSharedPreferenceChangeListener(this);

        Log.w("SharedPreferences", "===================================");
        Log.i("SharedPreferences", "getInt:       " + preferences.getInt("int", -1));
        Log.i("SharedPreferences", "getLong:      " + preferences.getLong("long", -1));
        Log.i("SharedPreferences", "getFloat:     " + preferences.getFloat("float", -1));
        Log.i("SharedPreferences", "getString:    " + preferences.getString("string", null));
        Log.w("SharedPreferences", "===================================");

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("int", 1);
        editor.putLong("long", 2);
        editor.putFloat("float", 3.8f);
        editor.putString("string", "string---");
        editor.putBoolean("boolean", true);
        HashSet<String> set = new HashSet<>();
        set.add("set1");
        set.add("set2");
        set.add("set3");
        set.add("set4");
        editor.putStringSet("stringSet", set);
        editor.apply();

        Log.w("SharedPreferences", "===================================");
        Log.i("SharedPreferences", "getInt:       " + preferences.getInt("int", -1));
        Log.i("SharedPreferences", "getLong:      " + preferences.getLong("long", -1));
        Log.i("SharedPreferences", "getFloat:     " + preferences.getFloat("float", -1));
        Log.i("SharedPreferences", "getString:    " + preferences.getString("string", null));
        Log.i("SharedPreferences", "getBoolean:   " + preferences.getBoolean("boolean", false));
        Log.i("SharedPreferences", "getStringSet: " + preferences.getStringSet("stringSet", null));
        Log.i("SharedPreferences", "getAll:       " + preferences.getAll());
        Log.w("SharedPreferences", "===================================");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = preferences.edit();

                editor.putInt("int", 100);
                editor.putLong("long", 200);
                editor.putFloat("float", 300.8f);
                editor.remove("string");
                editor.commit();

                Log.w("SharedPreferences", "===================================");
                Log.i("SharedPreferences", "getInt:       " + preferences.getInt("int", -1));
                Log.i("SharedPreferences", "getLong:      " + preferences.getLong("long", -1));
                Log.i("SharedPreferences", "getFloat:     " + preferences.getFloat("float", -1));
                Log.i("SharedPreferences",
                        "getString:    " + preferences.getString("string", null));
                Log.i("SharedPreferences", "getAll:       " + preferences.getAll());
                Log.w("SharedPreferences", "===================================");

                editor.clear();
                editor.apply();
                Log.e("SharedPreferences", "getAll:       " + preferences.getAll());


                editor.putFloat("float", 333.8f);
                editor.putString("string", "ABCDEF");
                editor.apply();
            }
        }).start();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("SharedPreferences", "onSharedPreferenceChanged: key=" + key + ", ThreadName=" +
                Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d("MainActivity", "finalize!!!");

        super.finalize();
    }
}
