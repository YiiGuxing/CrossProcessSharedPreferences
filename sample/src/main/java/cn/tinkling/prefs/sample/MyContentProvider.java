package cn.tinkling.prefs.sample;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.tinkling.prefs.RemoteSharedPreferences;
import cn.tinkling.prefs.RemoteSharedPreferencesDescriptor;

public class MyContentProvider extends ContentProvider {

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Bundle call(String method, String arg, Bundle extras) {

        if ("getRemoteSharedPreferences".equals(method)) {
            SharedPreferences preferences =
                    getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            RemoteSharedPreferences rsp = new RemoteSharedPreferences(preferences);
            RemoteSharedPreferencesDescriptor descriptor = rsp.getSharedPreferencesDescriptor();

            Bundle bundle = new Bundle();
            bundle.putParcelable("preferences", descriptor);
            return bundle;
        }

        return null;
    }
}