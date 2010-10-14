/*
 * Copyright (C) 2010 František Hejl
 *
 * This file is part of Chesswalk.
 *
 * Chesswalk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chesswalk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
*/

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
