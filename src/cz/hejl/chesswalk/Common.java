package cz.hejl.chesswalk;

import android.util.Log;

public class Common {

	public static final String PREF_COLOR = "color"; // int
	public static final String PREF_CUSTOM_TIME = "customTime"; // int
	public static final String PREF_CUSTOM_INCREMENT = "customIncrement"; // int
	public static final String PREF_LOGIN_AUTOMATICALLY = "loginAutomatically"; // boolean
	public static final String PREF_LOGIN_GUEST = "loginGuest"; // boolean
	public static final String PREF_LOGIN_OK = "loginOk"; // boolean
	public static final String PREF_LOGIN_PASSWORD = "loginPassword"; // String
	public static final String PREF_LOGIN_USERNAME = "loginUsername"; // String
	public static final String PREF_RATED = "rated"; // boolean
	public static final String PREF_TIME_SETTINGS_POS = "timeSettingsPos"; // int
	public static final String TAG = "ChessWalk";
	
	public static void log(int number) {
		Log.d(Common.TAG, number + "");
	}
	
	public static void log(String message) {
		Log.d(Common.TAG, message);
	}

}
