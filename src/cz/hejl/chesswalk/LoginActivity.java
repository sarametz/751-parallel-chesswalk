package cz.hejl.chesswalk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private boolean guest;
	private AsyncTask<String, Integer, Integer> loginTask;
	private Dialog dgLoggingIn;
	private TextView tvLoggingInState;
	private EditText edUsername;
	private EditText edPassword;

	// -----------------------------------------------------------------------------------------------------------

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}

	// -----------------------------------------------------------------------------------------------------------

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// set up edittexts
		edUsername = (EditText) findViewById(R.id.edUsername);
		edPassword = (EditText) findViewById(R.id.edPassword);

		// set up buttons
		Button btLogin = (Button) findViewById(R.id.btLogin);
		btLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showLoggingInDialog();
				guest = false;
				loginTask = new LoginTask(LoginActivity.this, dgLoggingIn, tvLoggingInState).execute(edUsername
						.getText().toString(), edPassword.getText().toString());
			}
		});

		Button btGuestLogin = (Button) findViewById(R.id.btGuestLogin);
		btGuestLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showLoggingInDialog();
				guest = true;
				loginTask = new LoginTask(LoginActivity.this, dgLoggingIn, tvLoggingInState).execute("guest", "");
			}
		});

		// setup tvCreateAccount
		TextView tvCreateAccount = (TextView) findViewById(R.id.tvCreateAccount);
		tvCreateAccount.setText(Html.fromHtml("<a href=\"http://www.freechess.org/Register/index.html\">"
				+ getString(R.string.tvCreateAccount) + "</a>"));
		tvCreateAccount.setMovementMethod(LinkMovementMethod.getInstance());

		// load settings (username, password, autologin)
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		edUsername.setText(settings.getString(Common.PREF_LOGIN_USERNAME, ""));
		edPassword.setText(settings.getString(Common.PREF_LOGIN_PASSWORD, ""));

		// possibly restore dialog
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("loggingDialogShowing", false) == true) {
			if (savedInstanceState.getBoolean("guest", false)) {
				showLoggingInDialog();
				guest = true;
				loginTask = new LoginTask(LoginActivity.this, dgLoggingIn, tvLoggingInState).execute("guest", "");
			} else {
				showLoggingInDialog();
				guest = false;
				loginTask = new LoginTask(LoginActivity.this, dgLoggingIn, tvLoggingInState).execute(edUsername
						.getText().toString(), edPassword.getText().toString());
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
	protected void onPause() {
		super.onPause();

		// save login info
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(Common.PREF_LOGIN_USERNAME, edUsername.getText().toString());
		editor.putString(Common.PREF_LOGIN_PASSWORD, edPassword.getText().toString());

		editor.commit();
	}

	// -----------------------------------------------------------------------------------------------------------

	@Override
	protected void onSaveInstanceState(Bundle state) {
		state.putBoolean("loggingDialogShowing", dgLoggingIn == null ? false : dgLoggingIn.isShowing());
		state.putBoolean("guest", guest);
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

		tvLoggingInState = (TextView) dgLoggingIn.findViewById(R.id.tvLoggingInState);
		tvLoggingInState.setText(R.string.loggingIn1);

		dgLoggingIn.show();
	}

}
