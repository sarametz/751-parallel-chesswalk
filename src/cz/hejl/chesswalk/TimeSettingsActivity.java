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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TimeSettingsActivity extends Activity {

	private SeekBar skIncrement;
	private SeekBar skTime;
	private TextView tvIncrementValue;
	private TextView tvTimeValue;

	// -------------------------------------------------------------------------------------------------------

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.time_settings);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		// set up textviews
		tvIncrementValue = (TextView) findViewById(R.id.tvIncrementValue);
		tvIncrementValue.setText(" " + preferences.getInt(Common.PREF_CUSTOM_INCREMENT, 5) + " "
				+ getString(R.string.seconds));
		tvTimeValue = (TextView) findViewById(R.id.tvTimeValue);
		tvTimeValue.setText(" " + preferences.getInt(Common.PREF_CUSTOM_TIME, 15) + " "
				+ getString(R.string.minutes));

		// set up done button
		((Button) findViewById(R.id.btTimeSettingsDone)).setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("time", skTime.getProgress() + 1);
				resultIntent.putExtra("increment", skIncrement.getProgress());
				setResult(0, resultIntent);
				finish();
			}
		});

		// set seek bars
		skTime = (SeekBar) findViewById(R.id.skTime);
		skTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvTimeValue.setText(" " + (progress + 1) + " " + getString(R.string.minutes));
			}
		});
		skTime.setProgress(preferences.getInt(Common.PREF_CUSTOM_TIME, 15) - 1);

		skIncrement = (SeekBar) findViewById(R.id.skIncrement);
		skIncrement.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvIncrementValue.setText(" " + progress + " " + getString(R.string.seconds));
			}
		});
		skIncrement.setProgress(preferences.getInt(Common.PREF_CUSTOM_INCREMENT, 5));
	}

}
