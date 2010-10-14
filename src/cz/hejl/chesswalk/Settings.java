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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class Settings extends Activity {

	private CheckBox cbAutoLogin;
	private EditText edUsername;
	private EditText edPassword;
	private RadioButton rbGuest;
	private RadioButton rbRegistered;
	private TextView tvSettingsCreateAccount;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		edUsername = (EditText) findViewById(R.id.edUsernameSettings);
		edPassword = (EditText) findViewById(R.id.edPasswordSettings);
		cbAutoLogin = (CheckBox) findViewById(R.id.cbAutoLoginSettings);
		rbGuest = (RadioButton) findViewById(R.id.rbGuest);
		rbRegistered = (RadioButton) findViewById(R.id.rbRegistered);

		// setup tvSettingsCreateAccount
		tvSettingsCreateAccount = (TextView) findViewById(R.id.tvSettingsCreateAccount);
		tvSettingsCreateAccount.setText(Html
				.fromHtml("<a href=\"http://www.freechess.org/Register/index.html\">"
						+ getResources().getString(R.string.tvCreateAccount) + "</a>"));
		tvSettingsCreateAccount.setMovementMethod(LinkMovementMethod.getInstance());

		cbAutoLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				update();
			}
		});

		((RadioButton) findViewById(R.id.rbGuest)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				update();
			}
		});

		((RadioButton) findViewById(R.id.rbRegistered)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				update();
			}
		});

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		edUsername.setText(settings.getString(Common.PREF_LOGIN_USERNAME, ""));
		edPassword.setText(settings.getString(Common.PREF_LOGIN_PASSWORD, ""));
		cbAutoLogin.setChecked(settings.getBoolean(Common.PREF_LOGIN_AUTOMATICALLY, true));
		if (settings.getBoolean(Common.PREF_LOGIN_GUEST, false))
			rbGuest.setChecked(true);
		else
			rbRegistered.setChecked(true);

		update();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Common.PREF_LOGIN_USERNAME, edUsername.getText().toString());
		editor.putString(Common.PREF_LOGIN_PASSWORD, edPassword.getText().toString());
		editor.putBoolean(Common.PREF_LOGIN_AUTOMATICALLY, cbAutoLogin.isChecked());
		editor.putBoolean(Common.PREF_LOGIN_GUEST, rbGuest.isChecked());
		editor.commit();
	}

	private void update() {
		if (cbAutoLogin.isChecked()) {
			rbGuest.setVisibility(View.VISIBLE);
			rbRegistered.setVisibility(View.VISIBLE);
			edUsername.setVisibility(View.VISIBLE);
			edPassword.setVisibility(View.VISIBLE);
			tvSettingsCreateAccount.setVisibility(View.VISIBLE);
			if (rbRegistered.isChecked()) {
				edUsername.setEnabled(true);
				edPassword.setEnabled(true);
			} else {
				edUsername.setEnabled(false);
				edPassword.setEnabled(false);
			}

		} else {
			rbGuest.setVisibility(View.GONE);
			rbRegistered.setVisibility(View.GONE);
			edUsername.setVisibility(View.GONE);
			edPassword.setVisibility(View.GONE);
			tvSettingsCreateAccount.setVisibility(View.GONE);
		}
	}

}
