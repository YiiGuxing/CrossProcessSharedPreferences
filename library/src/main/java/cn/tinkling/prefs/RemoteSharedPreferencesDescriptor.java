package cn.tinkling.prefs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * SharedPreferencesDescriptor
 * <p/>
 * Created by tinkling on 15/12/18.
 */
public class RemoteSharedPreferencesDescriptor implements Parcelable {

    public static final Creator<RemoteSharedPreferencesDescriptor> CREATOR =
            new Creator<RemoteSharedPreferencesDescriptor>() {
                @Override
                public RemoteSharedPreferencesDescriptor createFromParcel(Parcel in) {
                    return new RemoteSharedPreferencesDescriptor(in);
                }

                @Override
                public RemoteSharedPreferencesDescriptor[] newArray(int size) {
                    return new RemoteSharedPreferencesDescriptor[size];
                }
            };

    public IRemoteSharedPreferences remoteSharedPreferences;

    public RemoteSharedPreferencesDescriptor(IRemoteSharedPreferences prefs) {
        this.remoteSharedPreferences = prefs;
    }

    protected RemoteSharedPreferencesDescriptor(Parcel in) {
        remoteSharedPreferences = IRemoteSharedPreferences.Stub.asInterface(in.readStrongBinder());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(remoteSharedPreferences.asBinder());
    }

}
