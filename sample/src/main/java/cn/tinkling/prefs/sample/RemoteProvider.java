package cn.tinkling.prefs.sample;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.tinkling.prefs.RemoteSharedPreferences;

public class RemoteProvider extends ContentProvider {

    private RemoteSharedPreferences mRemotePrefs;

    @Override
    public boolean onCreate() {
        final SharedPreferences preferences =
                getContext().getSharedPreferences("preferences-bundle", Context.MODE_PRIVATE);
        mRemotePrefs = new RemoteSharedPreferences(preferences);

        return true;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        if ("getRemoteSharedPreferences".equals(method)) {
            Bundle bundle = new Bundle();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bundle.putBinder("preferences", mRemotePrefs);
            } else {
                bundle.putParcelable("preferences", mRemotePrefs.getSharedPreferencesDescriptor());
            }

            return bundle;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
