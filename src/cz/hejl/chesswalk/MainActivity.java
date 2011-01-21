package cz.hejl.chesswalk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int REQUEST_LOBBY = 0;
    private static final int REQUEST_OFFLINE_GAME = 1;

    private AsyncTask<String, Integer, Integer> loginTask;
    private Dialog dgLoggingIn;
    private SharedPreferences settings;
    private TextView tvLoggingState;

    // -----------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOBBY) {
            if (dgLoggingIn != null)
                dgLoggingIn.dismiss();
            ChessClient.getInstance().cancel();
        } else if (requestCode == REQUEST_OFFLINE_GAME) {
            Intent i = new Intent(MainActivity.this, OfflineGame.class);
            startActivity(i);
        }
    }

    // -----------------------------------------------------------------------------------------------------------

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        getWindow().setBackgroundDrawableResource(R.drawable.background);

        // set up button listeners
        Button btPlayOnline = (Button) findViewById(R.id.btPlayOnline);
        btPlayOnline.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (settings.getBoolean(Common.PREF_LOGIN_AUTOMATICALLY, true)
                        && settings.getBoolean(Common.PREF_LOGIN_OK, false)) {
                    if (settings.getBoolean(Common.PREF_LOGIN_GUEST, false)) {
                        showLoggingInDialog();
                        loginTask = new LoginTask(MainActivity.this,
                                dgLoggingIn, tvLoggingState).execute("guest",
                                "");
                    } else {
                        showLoggingInDialog();
                        loginTask = new LoginTask(MainActivity.this,
                                dgLoggingIn, tvLoggingState).execute(settings
                                .getString(Common.PREF_LOGIN_USERNAME, ""),
                                settings.getString(Common.PREF_LOGIN_PASSWORD,
                                        ""));
                    }
                } else {
                    Intent i = new Intent(MainActivity.this,
                            LoginActivity.class);
                    startActivity(i);
                }
            }
        });

        Button btPlayOffline = (Button) findViewById(R.id.btPlayOffline);
        btPlayOffline.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (settings.contains("FEN")) {
                    Intent i = new Intent(MainActivity.this, OfflineGame.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, NewGame.class);
                    startActivityForResult(i, REQUEST_OFFLINE_GAME);
                }

            }
        });

        Button btSettings = (Button) findViewById(R.id.btSettings);
        btSettings.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        });

        Button btAbout = (Button) findViewById(R.id.btAbout);
        btAbout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, About.class);
                startActivity(i);
            }
        });

        // load preferences
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        // possibly restore dialog
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("loggingDialogShowing", false) == true) {
            if (settings.getBoolean(Common.PREF_LOGIN_GUEST, false)) {
                showLoggingInDialog();
                loginTask = new LoginTask(MainActivity.this, dgLoggingIn,
                        tvLoggingState).execute("guest", "");
            } else {
                showLoggingInDialog();
                loginTask = new LoginTask(MainActivity.this, dgLoggingIn,
                        tvLoggingState).execute(settings.getString(
                        Common.PREF_LOGIN_USERNAME, ""), settings.getString(
                        Common.PREF_LOGIN_PASSWORD, ""));
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // cancel LoginTask if running
        if (loginTask != null && !loginTask.isCancelled())
            loginTask.cancel(true);
        ChessClient.getInstance().cancel();

        // dismiss logging dialog
        if (dgLoggingIn != null && dgLoggingIn.isShowing())
            dgLoggingIn.dismiss();
    }

    // -----------------------------------------------------------------------------------------------------------

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("loggingDialogShowing", dgLoggingIn == null ? false
                : dgLoggingIn.isShowing());
    }

    // -----------------------------------------------------------------------------------------------------------

    private void showLoggingInDialog() {
        dgLoggingIn = new Dialog(this);
        dgLoggingIn.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dgLoggingIn.setContentView(R.layout.logging_dialog);
        dgLoggingIn.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginTask != null && !loginTask.isCancelled())
                    loginTask.cancel(true);
                ChessClient.getInstance().cancel();
            }
        });

        tvLoggingState = (TextView) dgLoggingIn
                .findViewById(R.id.tvLoggingInState);
        tvLoggingState.setText(R.string.loggingIn1);

        dgLoggingIn.show();
    }
}