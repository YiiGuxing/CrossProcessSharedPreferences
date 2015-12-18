// IRemoteSharedPreferences.aidl
package cn.tinkling.prefs;

import java.util.Map;
import cn.tinkling.prefs.IRemoteEditor;
import cn.tinkling.prefs.IOnSharedPreferenceChangeListener;

interface IRemoteSharedPreferences {

        /**
         * Retrieve all values from the preferences.
         *
         * <p>Note that you <em>must not</em> modify the collection returned
         * by this method, or alter any of its contents.  The consistency of your
         * stored data is not guaranteed if you do.
         *
         * @return Returns a map containing a list of pairs key/value representing
         * the preferences.
         *
         * @throws NullPointerException
         */
        Map getAll();

        /**
         * Retrieve a String value from the preferences.
         *
         * @param key The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         *
         * @return Returns the preference value if it exists, or defValue.  Throws
         * ClassCastException if there is a preference with this name that is not
         * a String.
         *
         * @throws ClassCastException
         */
        String getString(String key, String defValue);

        /**
         * Retrieve a set of String values from the preferences.
         *
         * <p>Note that you <em>must not</em> modify the set instance returned
         * by this call.  The consistency of the stored data is not guaranteed
         * if you do, nor is your ability to modify the instance at all.
         *
         * @param key The name of the preference to retrieve.
         * @param defValues Values to return if this preference does not exist.
         *
         * @return Returns the preference values if they exist, or defValues.
         * Throws ClassCastException if there is a preference with this name
         * that is not a Set.
         *
         * @throws ClassCastException
         */
        String[] getStringArray(String key);

        /**
         * Retrieve an int value from the preferences.
         *
         * @param key The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         *
         * @return Returns the preference value if it exists, or defValue.  Throws
         * ClassCastException if there is a preference with this name that is not
         * an int.
         *
         * @throws ClassCastException
         */
        int getInt(String key, int defValue);

        /**
         * Retrieve a long value from the preferences.
         *
         * @param key The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         *
         * @return Returns the preference value if it exists, or defValue.  Throws
         * ClassCastException if there is a preference with this name that is not
         * a long.
         *
         * @throws ClassCastException
         */
        long getLong(String key, long defValue);

        /**
         * Retrieve a float value from the preferences.
         *
         * @param key The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         *
         * @return Returns the preference value if it exists, or defValue.  Throws
         * ClassCastException if there is a preference with this name that is not
         * a float.
         *
         * @throws ClassCastException
         */
        float getFloat(String key, float defValue);

        /**
         * Retrieve a boolean value from the preferences.
         *
         * @param key The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         *
         * @return Returns the preference value if it exists, or defValue.  Throws
         * ClassCastException if there is a preference with this name that is not
         * a boolean.
         *
         * @throws ClassCastException
         */
        boolean getBoolean(String key, boolean defValue);

        /**
         * Checks whether the preferences contains a preference.
         *
         * @param key The name of the preference to check.
         * @return Returns true if the preference exists in the preferences,
         *         otherwise false.
         */
        boolean contains(String key);

        /**
         * Create a new Editor for these preferences, through which you can make
         * modifications to the data in the preferences and atomically commit those
         * changes back to the SharedPreferences object.
         *
         * <p>Note that you <em>must</em> call {@link IRemoteEditor#commit} to have any
         * changes you perform in the Editor actually show up in the
         * SharedPreferences.
         *
         * @return Returns a new instance of the {@link IRemoteEditor} interface, allowing
         * you to modify the values in this SharedPreferences object.
         */
        IRemoteEditor edit();

        /**
         * Registers a callback to be invoked when a change happens to a preference.
         *
         * <p class="caution"><strong>Caution:</strong> The preference manager does
         * not currently store a strong reference to the listener. You must store a
         * strong reference to the listener, or it will be susceptible to garbage
         * collection. We recommend you keep a reference to the listener in the
         * instance data of an object that will exist as long as you need the
         * listener.</p>
         *
         * @param listener The callback that will run.
         * @see #unregisterOnSharedPreferenceChangeListener
         */
        boolean registerOnSharedPreferenceChangeListener(IOnSharedPreferenceChangeListener listener);

        /**
         * Unregisters a previous callback.
         *
         * @param listener The callback that should be unregistered.
         * @see #registerOnSharedPreferenceChangeListener
         */
        boolean unregisterOnSharedPreferenceChangeListener(IOnSharedPreferenceChangeListener listener);
}
