// IOnSharedPreferenceChangeListener.aidl
package cn.tinkling.prefs;

/**
 * Interface definition for a callback to be invoked when a shared
 * preference is changed.
 */
interface IOnSharedPreferenceChangeListener {
     /**
      * Called when a shared preference is changed, added, or removed. This
      * may be called even if a preference is set to its existing value.
      *
      * <p>This callback will be run on your main thread.
      *
      * @param key The key of the preference that was changed, added, or
      *            removed.
      */
    void onSharedPreferenceChanged(String key);
}
