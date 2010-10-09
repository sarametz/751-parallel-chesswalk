package cz.hejl.chesswalk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cz.hejl.chesswalk.Listeners.GameOffersListener;

public class GameOffersActivity extends Activity implements GameOffersListener {

	public static final int RESULT_CONN_EXCEPTION = 100;

	private static final int DIALOG_WAITING = 0;
	private static final int REQUEST_ONLINE_GAME = 0;

	private ChessClient chessClient = ChessClient.getInstance();
	private GameOffersAdapter gameOffersAdapter;
	private String textBlack;
	private String textComputer;
	private String textRated;
	private String textSPerMove;
	private String textUnrated;
	private String textWhite;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ONLINE_GAME)
			finish();
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onConnException() {
		setResult(RESULT_CONN_EXCEPTION);
		finish();
	}

	// -------------------------------------------------------------------------------------------------------

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_offers);

		// load texts
		textBlack = getString(R.string.black);
		textComputer = getString(R.string.computer);
		textRated = getString(R.string.gameOfferRated);
		textSPerMove = getString(R.string.gameOfferSPerMove);
		textUnrated = getString(R.string.gameOfferUnrated);
		textWhite = getString(R.string.white);

		// set up games list
		gameOffersAdapter = new GameOffersAdapter(this);
		ListView lvGameOffers = (ListView) findViewById(R.id.lvGameOffers);
		lvGameOffers.setEmptyView(findViewById(R.id.tvGameOffersEmpty));
		lvGameOffers.setAdapter(gameOffersAdapter);
		lvGameOffers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				GameOffer game = gameOffersAdapter.games.get(position);
				chessClient.play(game.id);
				showDialog(DIALOG_WAITING);
			}
		});

		chessClient.setGameOffersListener(this);
	}

	// -------------------------------------------------------------------------------------------------------

	public Dialog onCreateDialog(int dialogId) {
		if (dialogId == DIALOG_WAITING) {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getText(R.string.waitingForOpponent));
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					chessClient.withdraw();
					removeDialog(DIALOG_WAITING);
				}
			});

			return progressDialog;
		} else
			return null;
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onDestroy() {
		super.onDestroy();
		chessClient.removeGameOffersListener();
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onMatchStarted(OnlineGameState onlineGameState) {
		try {
			removeDialog(DIALOG_WAITING);
		} catch (IllegalArgumentException e) {
			// dialog was not previously shown
		}
		chessClient.removeGameOffersListener();
		Intent intent = new Intent(this, OnlineGameActivity.class);
		intent.putExtra("onlineGameState", onlineGameState);
		startActivityForResult(intent, REQUEST_ONLINE_GAME);
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onSeekUnavailable() {
		Log.d(Common.TAG, "GameOffers.onSeekUnavailabe");
		try {
			removeDialog(DIALOG_WAITING);
		} catch (IllegalArgumentException e) {
			// dialog was not previously shown
		}
		Toast.makeText(this, R.string.seekUnavailable, Toast.LENGTH_SHORT).show();
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void onUpdate(ArrayList<GameOffer> games) {
		Log.d(Common.TAG, "GameOffers.onUpdate");
		Collections.sort(games, new Comparator<GameOffer>() {
			@Override
			public int compare(GameOffer game1, GameOffer game2) {
				if (game1.timeInt > game2.timeInt)
					return 1;
				else if (game1.timeInt < game2.timeInt)
					return -1;
				else
					return 0;
			}
		});
		gameOffersAdapter.games = games;
		gameOffersAdapter.notifyDataSetChanged();
	}

	// -------------------------------------------------------------------------------------------------------

	private class GameOffersAdapter extends BaseAdapter {

		public ArrayList<GameOffer> games = new ArrayList<GameOffer>();

		private LayoutInflater inflater;

		// -------------------------------------------------------------------------------------------------------

		public GameOffersAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public int getCount() {
			return games.size();
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public Object getItem(int position) {
			return position;
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public long getItemId(int position) {
			return position;
		}

		// -------------------------------------------------------------------------------------------------------

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.game_offer_item, null);
				holder = new ViewHolder();
				holder.rating = (TextView) convertView.findViewById(R.id.tvSeekRating);
				holder.name = (TextView) convertView.findViewById(R.id.tvSeekName);
				holder.nameInfo = (TextView) convertView.findViewById(R.id.tvSeekNameInfo);
				holder.time = (TextView) convertView.findViewById(R.id.tvSeekTime);
				holder.increment = (TextView) convertView.findViewById(R.id.tvSeekIncrement);
				holder.color = (TextView) convertView.findViewById(R.id.tvSeekColor);
				holder.rated = (TextView) convertView.findViewById(R.id.tvSeekRated);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String ratingString = "";
			if (games.get(position).rating != -1)
				ratingString = Integer.toString(games.get(position).rating);
			holder.rating.setText(ratingString);
			holder.name.setText(games.get(position).username);
			if (games.get(position).computer)
				holder.nameInfo.setText(textComputer);
			else
				holder.nameInfo.setText("");
			holder.time.setText(games.get(position).time + " min");
			holder.increment.setText("(+" + games.get(position).increment + textSPerMove);
			if (games.get(position).color == 1)
				holder.color.setText(textWhite);
			else if (games.get(position).color == -1)
				holder.color.setText(textBlack);
			if (games.get(position).color == 0)
				holder.color.setText("");
			String rated = "";
			if (!chessClient.guest) {
				if (games.get(position).rated)
					rated = textRated;
				else
					rated = textUnrated;
			}
			holder.rated.setText(rated);

			return convertView;
		}

		// -------------------------------------------------------------------------------------------------------

		private class ViewHolder {
			TextView rating;
			TextView name;
			TextView nameInfo;
			TextView time;
			TextView increment;
			TextView color;
			TextView rated;
		}

	}
}
