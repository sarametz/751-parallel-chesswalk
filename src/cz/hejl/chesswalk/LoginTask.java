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

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

public class LoginTask extends AsyncTask<String, Integer, Integer> {
	
	private static final int LOGIN_IO_ERROR = 2;
	private static final int LOGIN_OK = 0;
	private static final int LOGIN_WRONG = 1;
	private static final int REQUEST_LOBBY = 0;
	
	private Activity activity;
	private Dialog dgLoggingIn;
	private String[] states = new String[5];
	private TextView tvLoggingInState;
	
	protected Integer doInBackground(String... login) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
		ChessClient client = ChessClient.getInstance();
		try {
			client.login(login[0].trim(), login[1].trim(), this);
		}
		catch (LoginException e) {
			editor.putBoolean(Common.PREF_LOGIN_OK, false);
			editor.commit();
			return LOGIN_WRONG;
		}
		catch (IOException e) {
			return LOGIN_IO_ERROR;
		}

		if (login[0].equals("guest"))
			editor.putBoolean(Common.PREF_LOGIN_GUEST, true);
		else
			editor.putBoolean(Common.PREF_LOGIN_GUEST, false);
		editor.putBoolean(Common.PREF_LOGIN_OK, true);
		editor.commit();

		return LOGIN_OK;
	}

	// -------------------------------------------------------------------------------------------------------
	
	public LoginTask(Activity activity, Dialog dgLoggingIn, TextView tvLoggingInState) {
		this.activity = activity;
		this.dgLoggingIn = dgLoggingIn;
		this.tvLoggingInState = tvLoggingInState;
		
		// load logging states strings
		Resources resources = activity.getResources();
		states[0] = resources.getString(R.string.loggingIn1);
		states[1] = resources.getString(R.string.loggingIn2);
		states[2] = resources.getString(R.string.loggingIn3);
		states[3] = resources.getString(R.string.loggingIn4);
		states[4] = resources.getString(R.string.loggingIn5);
	}
	
	// -------------------------------------------------------------------------------------------------------

	protected void onPostExecute(Integer result) {
		if (result == LOGIN_OK) {
			dgLoggingIn.dismiss();
			Intent intent = new Intent(activity, LobbyActivity.class);
			activity.startActivityForResult(intent, REQUEST_LOBBY);
		} else if (result == LOGIN_WRONG) {
			ChessClient.getInstance().cancel();
			dgLoggingIn.dismiss();
			Toast.makeText(activity, R.string.loginWrong, Toast.LENGTH_LONG).show();
		} else if (result == LOGIN_IO_ERROR) {
			ChessClient.getInstance().cancel();
			dgLoggingIn.dismiss();
			Toast.makeText(activity, R.string.loginIoError, Toast.LENGTH_LONG).show();
		}
	}

	// -------------------------------------------------------------------------------------------------------

	protected void onProgressUpdate(Integer... state) {
		tvLoggingInState.setText(states[state[0] - 1]);
	}

	// -------------------------------------------------------------------------------------------------------

	public void publishState(int state) {
		publishProgress(state);
	}

}
