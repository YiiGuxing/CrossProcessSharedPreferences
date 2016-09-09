// IMyAidlInterface.aidl
package cn.tinkling.prefs.sample;

import cn.tinkling.prefs.IRemoteSharedPreferences;

interface IMyAidlInterface {

    IRemoteSharedPreferences getRemoteSharedPreferences();

}
