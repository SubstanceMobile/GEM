/*
 * Copyright 2017 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package substance.music.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
//TODO: import substance.mobile.ui.custom.activity.GemActivity

object Options {
	private lateinit var context: Context
	private lateinit var prefs: SharePreferences

	fun create(ctx: Context) {
		context = ctx
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
	}

    ///////////////////////////////////////////////////////////////////////////
    // Update activities
    ///////////////////////////////////////////////////////////////////////////

    fun markChanged() = Options.prefs.edit().putLong("last_update_time", System.currentTimeMillis()).commit()

    fun invalidateActivity(GemActivity activity) {
        if (activity.lastSettingsUpdate < prefs.getLong("last_update_time", 0)) activity.recreate();
    }

   ///////////////////////////////////////////////////////////////////////////
   // Exception types
   ///////////////////////////////////////////////////////////////////////////

   class UnsupportedSettingTypeException(type: String, key: String, putting: Boolean): Exception("Could not ${if (putting) "store" else "retrieve"} setting of type $type at key $key")

   ///////////////////////////////////////////////////////////////////////////
   // Generic getters/setters
   ///////////////////////////////////////////////////////////////////////////

   /*
    * Usage: put(MY_KEY, someValue)
    */
   inline fun <T : Any> put(key: String, value: T) = when (value) {
      is Boolean -> prefs.edit().putBoolean(key, value).apply()
      is String -> prefs.edit().putString(key, value).apply()
      is Int -> prefs.edit().putInt(key, value).apply()
      else -> throw UnsupportedSettingTypeException(value::class.simpleName ?: "ERROR", key, true) //TODO: All other types
   }

   /*
    * Usage: fetch(MY_KEY, defaultValue) or fetch<SomeType>(MY_KEY)
    */
   inline fun <reified T> fetch(key: String, default: T? = null): T = when (T::class.simpleName) {
      "Boolean" -> prefs.getBoolean(key, default as Boolean? ?: false) as T
      "String" -> prefs.getString(key, default as String? ?: "") as T
      "Int" -> prefs.getInt(key, default as Int? ?: 0) as T
      "Long" -> prefs.getLong(key, default as Long? ?: 0L) as T
      else -> throw UnsupportedSettingTypeException(T::class.simpleName, key, false) // TODO: All other types.
   }

   ///////////////////////////////////////////////////////////////////////////
   // Keys
   ///////////////////////////////////////////////////////////////////////////

   private const val KEY_ICON = "icon"
   private const val KEY_TABS_MODE = "tab_mode"
   private const val KEY_CATEGORY_NAMES = "category_names"
   private const val KEY_PALETTE = "use_palette"
   private const val KEY_BIG_GRID_SPACE = "big_grid_space"

   ///////////////////////////////////////////////////////////////////////////
   // Options
   ///////////////////////////////////////////////////////////////////////////

   enum class TabsMode(val id: Int) {
      NONE(0), NORMAL(1), ICON(2), SCROLLING(3);
      companion object {
         private val map = TabsMode.values().associateBy(TabsMode::id)
         fun fromId(id: Int) = map[id]
      }
   }
   var tabsMode: TabsMode
      set(to) = put(KEY_TABS_MODE, to.id)
      get = TabsMode.fromId(fetch<Int>(KEY_TABS_MODE))
   val usingTabs: Boolean
      get = (tabsMode != TabsMode.NONE)

   var useCategoryNames: Boolean
      set(to) = put(KEY_CATEGORY_NAMES, to)
      get = fetch<Boolean>(KEY_CATEGORY_NAMES)

   var usePalette: Boolean
      set(to) = put(KEY_PALETTE, to)
      get = fetch<Boolean>(KEY_PALETTE)

   var useBigGridSpace: Boolean
      set(to) = put(KEY_BIG_GRID_SPACE, to)
      get = fetch<Boolean>(KEY_BIG_GRID_SPACE)

}

/* OLD JAVA CODE. TODO: Remove
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import com.animbus.music.ui.activity.mainScreen.MainScreen;
import com.animbus.music.ui.custom.activity.ThemeActivity;
import static android.content.Intent.ACTION_MAIN;


public class Options {
    private static volatile Context context;
    private static volatile SharedPreferences prefs;

    //Keys
    private static final String base = "com.animbus.music";
    private static final String
            KEY_ICON = base + ".ICON",
            KEY_TABS_MODE = "tab_mode",
            KEY_CATEGORY_NAMES = "screen_name",
            KEY_PALETTE = "use_palette",
            KEY_BIG_GRID_SPACE = "big_grid_space",
            KEY_LIGHT_THEME = base + ".theme.IS_LIGHT";

    private Options() {

    }

    public static void init(final Context context) {
        Options.context = context;
        Options.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressLint("CommitPrefEdits")
    public static void resetPrefs() {
        Options.prefs.edit().clear().commit();
        context.getSharedPreferences("[[afollestad_theme-engine]]", 0).edit().clear().commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("did_reset", true).commit();
    }

    public static void restartApp() {
        PendingIntent mGEMIntent = PendingIntent.getActivity(context, 2138535432,
                new Intent(context, MainScreen.class).setAction(ACTION_MAIN).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_CANCEL_CURRENT);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, System.currentTimeMillis() + 2, mGEMIntent);
        System.exit(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Update activities
    ///////////////////////////////////////////////////////////////////////////

    public static void markChanged() {
        Options.prefs.edit().putLong("last_update_time", System.currentTimeMillis()).commit();
    }

    public static void invalidateActivity(ThemeActivity activity) {
        if (activity.lastSettingsUpdate < Options.prefs.getLong("last_update_time", 0))
            activity.recreate();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    public static void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public static void set(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public static void set(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public static boolean getBool(String key) {
        return prefs.getBoolean(key, false);
    }

    public static int getInt(String key) {
        return prefs.getInt(key, 0);
    }

    public static String getString(String key) {
        return prefs.getString(key, "");
    }

    ///////////////////////////////////////////////////////////////////////////
    // All of the settings
    ///////////////////////////////////////////////////////////////////////////

    //////////
    // Icon //
    //////////

    public static void setSavedIconID(int id) {
        set(KEY_ICON, id);
    }

    public static int getSavedIconID() {
        return getInt(KEY_ICON);
    }

    /////////////////
    // Light Theme //
    /////////////////

    public static void setLightTheme(boolean lightTheme) {
        set(KEY_LIGHT_THEME, lightTheme);
    }

    public static boolean isLightTheme() {
        return getBool(KEY_LIGHT_THEME);
    }

    //////////
    // Tabs //
    //////////

    public static void setTabMode(String mode) {
        set(KEY_TABS_MODE, mode);
    }

    public static boolean usingTabs() {
        try {
            return Integer.parseInt(getString(KEY_TABS_MODE)) != 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean usingIconTabs() {
        try {
            return Integer.parseInt(getString(KEY_TABS_MODE)) == 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean usingScrollableTabs() {
        try {
            return Integer.parseInt(getString(KEY_TABS_MODE)) == 3;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    ////////////////////
    // Category Names //
    ////////////////////

    public static boolean usingCategoryNames() {
        return getBool(KEY_CATEGORY_NAMES);
    }


    /////////////////
    // Use Palette //
    /////////////////

    public static boolean usingPalette() {
        return getBool(KEY_PALETTE);
    }

    ///////////////////////
    // Bigger List Space //
    ///////////////////////

    public static boolean usingBiggerSpaceInAlbumList() {
        return getBool(KEY_BIG_GRID_SPACE);
    }

   ///////////////////////////////////////////////////////////////////////////
   // Temporary
   // TODO: Remove
   ///////////////////////////////////////////////////////////////////////////

   public static boolean useStableService() {
       return true;
   }
}
*/