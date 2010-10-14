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

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import cz.hejl.chesswalk.FicsParser.Rating;
import cz.hejl.chesswalk.Listeners.SeekListener;

public class LobbyActivity extends Activity implements SeekListener, OnClickListener {

	private static final int COLOR_WHITE = 0;
	private static final int COLOR_AUTO = 1;
	private static final int COLOR_BLACK = 2;
	private static final int DIALOG_SEEKING = 0;
	private static final int MENU_GAME_OFFERS = 0;
	private static final int REQUEST_TIME_SETTINGS = 0;
	private static final int REQUEST_ONLINE_GAME = 1;
	private static final int REQUEST_GAME_OFFERS = 2;

	private int color;
	private Button btColorAuto;
	private Button btColorBlack;
	private Button btColorWhite;
	private ChessClient chessClient = ChessClient.getInstance();
	private Drawable leftDrawable;
	private Drawable leftToggledDrawable;
	private Drawable centerDrawable;
	private Drawable centerToggledDrawable;
	private Drawable rightDrawable;
	private Drawable rightToggledDrawable;
	private RadioButton rbRatedNo;
	private RadioButton rbRatedYes;
	private SharedPreferences preferences;
	private Spinner spTimeSettings;
	private TimeSettingsAdapter timeSettingsAdapter;

	// -------------------------------------------------------------------------------------------------------

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TIME_SETTINGS) {
			if (data != null)
				spTimeSettings.setSelection(timeSettingsAdapter.setCustom(data.getIntExtra("time", -1), data
						.getIntExtra("increment", -1)));
			else
				spTimeSettings.setSelection(preferences.getInt(Common.PREF_TIME_SETTINGS_POS, 2));
		} else if (requestCode == REQUEST_ONLINE_GAME) {
			chessClient.resign();
			chessClient.removeOnlineGameListener();
			chessClient.setSeekListener(this);
		} else if (requestCode == REQUEST_GAME_OFFERS) {
			if (resultCode == GameOffersActivity.RESULT_CONN_EXCEPTION)
				onConnException();
			else
				chessClient.setSeekListener(this);
		}
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btColorWhite:
			color = COLOR_WHITE;
			btColorWhite.setBackgroundDrawable(leftToggledDrawable);
			btColorAuto.setBackgroundDrawable(centerDrawable);
			btColorBlack.setBackgroundDrawable(rightDrawable);
			break;

		case R.id.btColorAuto:
			color = COLOR_AUTO;
			btColorWhite.setBackgroundDrawable(leftDrawable);
			btColorAuto.setBackgroundDrawable(centerToggledDrawable);
			btColorBlack.setBackgroundDrawable(rightDrawable);
			break;

		case R.id.btColorBlack:
			color = COLOR_BLACK;
			btColorWhite.setBackgroundDrawable(leftDrawable);
			btColorAuto.setBackgroundDrawable(centerDrawable);
			btColorBlack.setBackgroundDrawable(rightToggledDrawable);
			break;

		default:
			break;
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public void onConnException() {
		ChessClient.getInstance().cancel();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Connection lost");
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();
	}

	// -------------------------------------------------------------------------------------------------------

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lobby);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		// set up time settings spinner
		timeSettingsAdapter = new TimeSettingsAdapter(this, preferences);
		spTimeSettings = (Spinner) findViewById(R.id.spTimeSettings);
		spTimeSettings.setAdapter(timeSettingsAdapter);
		spTimeSettings.setSelection(preferences.getInt(Common.PREF_TIME_SETTINGS_POS, 2));
		spTimeSettings.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (id == TimeSettingsAdapter.ITEM_ID_CUSTOM) {
					Intent i = new Intent(LobbyActivity.this, TimeSettingsActivity.class);
					startActivityForResult(i, REQUEST_TIME_SETTINGS);
				} else {
					Editor editor = preferences.edit();
					editor.putInt(Common.PREF_TIME_SETTINGS_POS, position);
					editor.commit();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// load background drawables for color buttons
		Resources resources = getResources();
		leftDrawable = resources.getDrawable(R.drawable.btn_left);
		leftToggledDrawable = resources.getDrawable(R.drawable.btn_left_toggled);
		centerDrawable = resources.getDrawable(R.drawable.btn_center);
		centerToggledDrawable = resources.getDrawable(R.drawable.btn_center_toggled);
		rightDrawable = resources.getDrawable(R.drawable.btn_right);
		rightToggledDrawable = resources.getDrawable(R.drawable.btn_right_toggled);

		// set up color toggle buttons
		btColorWhite = (Button) findViewById(R.id.btColorWhite);
		btColorWhite.setOnClickListener(this);
		btColorAuto = (Button) findViewById(R.id.btColorAuto);
		btColorAuto.setOnClickListener(this);
		btColorBlack = (Button) findViewById(R.id.btColorBlack);
		btColorBlack.setOnClickListener(this);

		// set up rated radiobutton
		rbRatedYes = (RadioButton) findViewById(R.id.rbRatedYes);
		rbRatedNo = (RadioButton) findViewById(R.id.rbRatedNo);
		if (!chessClient.guest) {
			((LinearLayout) findViewById(R.id.llRated)).setVisibility(View.VISIBLE);
		}

		// set up create game offer button
		Button btCreateGameOffer = (Button) findViewById(R.id.btCreateGameOffer);
		btCreateGameOffer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String colorSymbol = "";
				if (color == COLOR_WHITE)
					colorSymbol = "w";
				else if (color == COLOR_BLACK)
					colorSymbol = "b";

				String ratedSymbol = "u";
				if (rbRatedYes.isChecked())
					ratedSymbol = "r";

				int spTimeSettingsPos = spTimeSettings.getSelectedItemPosition();

				showDialog(DIALOG_SEEKING);
				chessClient.seek(timeSettingsAdapter.times.get(spTimeSettingsPos),
						timeSettingsAdapter.increments.get(spTimeSettingsPos), colorSymbol, ratedSymbol);
			}
		});

		chessClient.setSeekListener(LobbyActivity.this);
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	protected Dialog onCreateDialog(int dialogId) {
		if (dialogId == DIALOG_SEEKING) {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getText(R.string.waitingForOpponent));
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					chessClient.cancelSeek();
					removeDialog(DIALOG_SEEKING);
				}
			});

			return progressDialog;
		} else
			return null;
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_GAME_OFFERS, Menu.NONE, getString(R.string.gameOffers)).setIcon(
				R.drawable.menu_game_offers);
		return true;
	}

	// -------------------------------------------------------------------------------------------------------

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return false;
	}

	// -------------------------------------------------------------------------------------------------------

	public void onMatchStarted(OnlineGameState onlineGameState) {
		Log.d(Common.TAG, "Lobby.onMatchStarted()");
		try {
			removeDialog(DIALOG_SEEKING);
		} catch (IllegalArgumentException e) {
			// dialog was not previously shown
		}
		chessClient.removeSeekListener();
		Intent intent = new Intent(this, OnlineGameActivity.class);
		intent.putExtra("onlineGameState", onlineGameState);
		startActivityForResult(intent, REQUEST_ONLINE_GAME);
		Log.d(Common.TAG, "  Lobby.onMatchStarted() finished");
	}

	// -------------------------------------------------------------------------------------------------------

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_GAME_OFFERS:
			chessClient.removeSeekListener();
			startActivityForResult(new Intent(this, GameOffersActivity.class), REQUEST_GAME_OFFERS);
			return true;
		}
		return false;
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onPause() {
		super.onPause();

		Editor editor = preferences.edit();
		editor.putInt(Common.PREF_COLOR, color);
		if (!chessClient.guest)
			editor.putBoolean(Common.PREF_RATED, rbRatedYes.isChecked());
		editor.commit();
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onRating(Rating rating) {
		if (!chessClient.guest) {
			((LinearLayout) findViewById(R.id.llRatings)).setVisibility(View.VISIBLE);
		}

		if (rating.type == Rating.BLITZ) {
			((TextView) findViewById(R.id.tvRatingBlitz)).setText(rating.rating);
			((LinearLayout) findViewById(R.id.llRatingBlitz)).setVisibility(View.VISIBLE);
		} else if (rating.type == Rating.LIGHTNING) {
			((TextView) findViewById(R.id.tvRatingLightning)).setText(rating.rating);
			((LinearLayout) findViewById(R.id.llRatingLightning)).setVisibility(View.VISIBLE);
		} else if (rating.type == Rating.STANDARD) {
			((TextView) findViewById(R.id.tvRatingStandard)).setText(rating.rating);
			((LinearLayout) findViewById(R.id.llRatingStandard)).setVisibility(View.VISIBLE);
		}
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onResume() {
		super.onResume();

		// set color
		color = preferences.getInt(Common.PREF_COLOR, COLOR_AUTO);
		if (color == COLOR_WHITE)
			btColorWhite.setBackgroundDrawable(leftToggledDrawable);
		else if (color == COLOR_AUTO)
			btColorAuto.setBackgroundDrawable(centerToggledDrawable);
		else if (color == COLOR_BLACK)
			btColorBlack.setBackgroundDrawable(rightToggledDrawable);

		// set rated
		if (preferences.getBoolean(Common.PREF_RATED, true))
			rbRatedYes.setChecked(true);
		else
			rbRatedNo.setChecked(true);
		
		// update ratings
		chessClient.finger();
	}

	// -------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private class TimeSettingsAdapter extends ArrayAdapter {

		public static final int ITEM_ID_CUSTOM = 99;

		public ArrayList<Integer> increments = new ArrayList<Integer>(Arrays.asList(new Integer[] { 5, 5, 5,
				5, 5 }));
		public ArrayList<Integer> times = new ArrayList<Integer>(Arrays.asList(new Integer[] { 5, 10, 15, 20,
				30 }));

		private int customIncrement = -1;
		private int customTime = -1;
		private LayoutInflater inflater;
		private SharedPreferences preferences;

		// -------------------------------------------------------------------------------------------------------

		@Override
		public int getCount() {
			// number of times items plus one "Custom..." item
			return times.size() + 1;
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public Object getItem(int position) {
			return position;
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public long getItemId(int position) {
			if (position == times.size())
				return ITEM_ID_CUSTOM;
			else
				return position;
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			View item = inflater.inflate(R.layout.time_settings_dropdown_item, null);
			item.setMinimumHeight(60);

			if (position >= times.size()) {
				((CheckedTextView) item).setText(getText(R.string.customTime));
			} else {
				SpannableString ss = new SpannableString(times.get(position) + " " + getString(R.string.min)
						+ " (+" + increments.get(position) + getString(R.string.secPerMove) + ")");
				ss.setSpan(new ForegroundColorSpan(0xFF000000), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new ForegroundColorSpan(0xFF666666), 6, ss.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				((CheckedTextView) item).setText(ss);
			}

			return item;
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View item = inflater.inflate(R.layout.time_settings_item, null);

			if (position < times.size()) {
				SpannableString ss = new SpannableString(times.get(position) + " " + getString(R.string.min)
						+ " (+" + increments.get(position) + getString(R.string.secPerMove) + ")");
				ss.setSpan(new ForegroundColorSpan(0xFF000000), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new ForegroundColorSpan(0xFF666666), 6, ss.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				((TextView) item).setText(ss);
			}

			return item;
		}

		// -------------------------------------------------------------------------------------------------------

		public int setCustom(int time, int increment) {
			// is this custom a duplicate?
			for (int i = 0; i < times.size(); i++) {
				if (times.get(i) == time && increments.get(i) == increment)
					return i;
			}

			// check if there is an old custom to remove
			for (int i = 0; i < times.size(); i++) {
				if (times.get(i) == customTime && increments.get(i) == customIncrement) {
					times.remove(i);
					increments.remove(i);
				}

			}

			// save the new custom
			customTime = time;
			customIncrement = increment;
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt(Common.PREF_CUSTOM_TIME, time);
			editor.putInt(Common.PREF_CUSTOM_INCREMENT, increment);
			editor.commit();

			for (int i = 0; i < times.size(); i++) {
				if (times.get(i) >= time) {
					times.add(i, time);
					increments.add(i, increment);
					return i;
				}
			}

			times.add(time);
			increments.add(increment);

			return times.size() - 1;
		}

		// -------------------------------------------------------------------------------------------------------

		public TimeSettingsAdapter(Context context, SharedPreferences preferences) {
			super(context, 0);

			this.preferences = preferences;
			inflater = LayoutInflater.from(context);
			if (preferences.contains("customTime")) {
				setCustom(preferences.getInt(Common.PREF_CUSTOM_TIME, 1), preferences.getInt(
						Common.PREF_CUSTOM_INCREMENT, 0));
			}
		}

	}

}
