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

import java.text.MessageFormat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class NewGame extends Activity implements OnClickListener {

    private static final String DATABASE_NAME = "chess";
    private static final String MOVES_TABLE_NAME = "moves";

    private boolean whiteHuman;
    private boolean blackHuman;
    private Button btBlackComputer;
    private Button btBlackHuman;
    private Button btWhiteComputer;
    private Button btWhiteHuman;
    private SeekBar skDifficulty;
    private SharedPreferences settings;
    private TextView tvDifficulty;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btWhiteHuman:
            whiteHuman = true;
            setWhite();
            break;
        case R.id.btWhiteComputer:
            whiteHuman = false;
            setWhite();
            break;
        case R.id.btBlackHuman:
            blackHuman = true;
            setBlack();
            break;
        case R.id.btBlackComputer:
            blackHuman = false;
            setBlack();
            break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        tvDifficulty = (TextView) findViewById(R.id.tvDifficulty);
        tvDifficulty.setText(MessageFormat.format(
                getString(R.string.computerStrength), settings.getInt(
                        "strength", 4)));

        // set up buttons
        btWhiteHuman = (Button) findViewById(R.id.btWhiteHuman);
        btWhiteHuman.setOnClickListener(this);

        btWhiteComputer = (Button) findViewById(R.id.btWhiteComputer);
        btWhiteComputer.setOnClickListener(this);

        whiteHuman = settings.getBoolean("whiteHuman", true);
        setWhite();

        btBlackHuman = (Button) findViewById(R.id.btBlackHuman);
        btBlackHuman.setOnClickListener(this);

        btBlackComputer = (Button) findViewById(R.id.btBlackComputer);
        btBlackComputer.setOnClickListener(this);

        blackHuman = settings.getBoolean("blackHuman", false);
        setBlack();

        Button btStart = (Button) findViewById(R.id.btNewGamePlay);
        btStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!whiteHuman && !blackHuman) {
                    Toast.makeText(NewGame.this, R.string.compVsCompToast,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(NewGame.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("FEN", Board.STARTING_FEN);
                editor.putBoolean("whiteHuman", whiteHuman);
                editor.putBoolean("blackHuman", blackHuman);
                editor.putBoolean("flipped", false);
                editor.putBoolean("gameEnded", false);
                editor.putInt("strength", skDifficulty.getProgress() + 1);
                editor.commit();

                SQLiteDatabase db = openOrCreateDatabase(DATABASE_NAME, 0, null);
                db.execSQL("DROP TABLE IF EXISTS " + MOVES_TABLE_NAME + ";");
                db
                        .execSQL("CREATE TABLE IF NOT EXISTS "
                                + MOVES_TABLE_NAME
                                + " (id INTEGER, sq_from INTEGER, sq_to INTEGER, piece INTEGER, capture INTEGER, en_passant INTEGER, "
                                + "castling0 INTEGER, castling1 INTEGER, castling2 INTEGER, castling3 INTEGER, info VARCHAR(60));");
                db.execSQL("DELETE FROM " + MOVES_TABLE_NAME + ";");

                db.close();

                finish();
            }
        });

        // set up difficulty bar
        skDifficulty = (SeekBar) findViewById(R.id.skDifficulty);
        skDifficulty.setProgress(settings.getInt("strength", 3) - 1);
        skDifficulty.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                tvDifficulty.setText(MessageFormat.format(
                        getString(R.string.computerStrength), progress + 1));
            }
        });
    }

    private void setBlack() {
        if (blackHuman) {
            btBlackHuman.setBackgroundResource(R.drawable.btn_left_toggled);
            btBlackComputer.setBackgroundResource(R.drawable.btn_right);
        } else {
            btBlackHuman.setBackgroundResource(R.drawable.btn_left);
            btBlackComputer.setBackgroundResource(R.drawable.btn_right_toggled);
        }
    }

    private void setWhite() {
        if (whiteHuman) {
            btWhiteHuman.setBackgroundResource(R.drawable.btn_left_toggled);
            btWhiteComputer.setBackgroundResource(R.drawable.btn_right);
        } else {
            btWhiteHuman.setBackgroundResource(R.drawable.btn_left);
            btWhiteComputer.setBackgroundResource(R.drawable.btn_right_toggled);
        }
    }

}
