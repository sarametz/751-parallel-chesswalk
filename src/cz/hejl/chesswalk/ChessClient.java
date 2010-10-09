package cz.hejl.chesswalk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.util.Log;
import cz.hejl.chesswalk.FicsParser.Rating;
import cz.hejl.chesswalk.Listeners.GameOffersListener;
import cz.hejl.chesswalk.Listeners.OnlineGameListener;
import cz.hejl.chesswalk.Listeners.SeekListener;

public class ChessClient {
	public static int blackRating;
	public static int whiteRating;

	public boolean guest;
	public String username;

	private static final int EXCEPTION = 0;
	private static final int MATCH_STARTED = 1;
	private static final int UPDATE_SOUGHT = 2;
	private static final int ONLINE_MOVE = 3;
	private static final int DRAW_OFFER = 4;
	private static final int DRAW_ANSWER = 5;
	private static final int CHAT_MESSAGE = 6;
	private static final int MATCH_END = 7;
	private static final int SEEK_UNAVAILABLE = 8;
	private static final int RATING_CHANGE = 9;
	private static final int CREATING_MATCH = 10;
	private static final int RATING = 11;
	private static ChessClient instance;

	private AsyncTask<Void, Object, Integer> ficsListenerTask;
	private FicsParser ficsParser = new FicsParser();
	private GameOffersListener gameOffersListener;
	private InputStream in;
	private OnlineGameListener onlineGameListener;
	private OutputStream out;
	private SeekListener seekListener;
	private Socket socket;
	private Timer timer;

	// -------------------------------------------------------------------------------------------------------

	/** Releases all resources - threads, connection... */
	public void cancel() {
		try {
			if (timer != null)
				timer.cancel();
			if (ficsListenerTask != null && !ficsListenerTask.isCancelled())
				ficsListenerTask.cancel(true);
			seekListener = null;
			gameOffersListener = null;
			onlineGameListener = null;
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null)
				socket.close();
			instance = null;
		} catch (IOException e) {
			Log.d(Common.TAG, "ChessClient.cancel IOException: " + e.getMessage());
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public void cancelSeek() {
		write("unseek\n");
	}

	// -------------------------------------------------------------------------------------------------------

	private boolean endsWith(StringBuffer sb, String pattern) {
		if (sb.length() < pattern.length())
			return false;
		else {
			int pos1 = sb.length() - 1;
			int pos2 = pattern.length() - 1;
			boolean found = true;
			while (pos2 >= 0) {
				if (sb.charAt(pos1) != pattern.charAt(pos2)) {
					found = false;
					break;
				}
				pos1--;
				pos2--;
			}
			return found;
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public void finger() {
		write("finger\n");
	}

	// -------------------------------------------------------------------------------------------------------

	public static ChessClient getInstance() {
		if (ChessClient.instance == null)
			ChessClient.instance = new ChessClient();
		return ChessClient.instance;
	}

	// -------------------------------------------------------------------------------------------------------

	public void login(String username, String password, LoginTask loginTask) throws LoginException,
			IOException {
		this.username = username;
		if (username.equals("guest"))
			guest = true;
		else
			guest = false;

		// try to establish connection
		socket = new Socket("freechess.org", 23);
		loginTask.publishState(2);
		in = socket.getInputStream();
		out = socket.getOutputStream();

		// do the log in
		readUntil("login:");
		loginTask.publishState(3);
		write(username + "\n");
		StringBuffer sb = readUntil(":");
		loginTask.publishState(4);

		if (endsWith(sb, "password:")) {
			// username exists
			write(password + "\n");
			StringBuffer sb1 = readUntil(new String[] { "fics%", "login:" });
			// wrong password
			if (endsWith(sb1, "login:")) {
				cancel();
				throw new LoginException();
			} else {
				postLoginCommands();
				loginTask.publishState(5);
			}

		} else if (endsWith(sb, "login:")) {
			// username too short or blank
			cancel();
			throw new LoginException();
		} else if (endsWith(sb, "\":")) {
			// username is guest or doesn't exist
			if (username.equals("guest")) {
				this.username = sb.substring(sb.length() - 11, sb.length() - 2);
				postLoginCommands();
				loginTask.publishState(5);
			} else {
				cancel();
				throw new LoginException();
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public void play(String id) {
		write("play " + id + "\n");
	}

	// -------------------------------------------------------------------------------------------------------

	private void postLoginCommands() throws IOException {
		write("set shout off\n");
		write("set seek off\n");
		write("set pin off\n");
		write("set style 12\n");
		write("set autoflag 1\n");
	}

	// -------------------------------------------------------------------------------------------------------

	private StringBuffer readUntil(String[] patterns) throws IOException {
		StringBuffer sb = new StringBuffer();
		char ch = (char) in.read();
		while (true) {
			sb.append(ch);
			boolean found = false;
			for (int i = 0; i < patterns.length; i++) {
				if (endsWith(sb, patterns[i]))
					found = true;
			}
			if (found)
				break;
			ch = (char) in.read();
		}

		return sb;
	}

	// -------------------------------------------------------------------------------------------------------

	public void removeGameOffersListener() {
		// cancel timer
		if (timer != null)
			timer.cancel();

		gameOffersListener = null;
	}

	// -------------------------------------------------------------------------------------------------------

	public void removeOnlineGameListener() {
		onlineGameListener = null;
	}

	// -------------------------------------------------------------------------------------------------------

	public void removeSeekListener() {
		seekListener = null;
	}

	// -------------------------------------------------------------------------------------------------------

	public void resign() {
		write("resign\n");
	}

	// -------------------------------------------------------------------------------------------------------
	
	public void say(String message) {
		write("say " + message + "\n");
	}
	
	// -------------------------------------------------------------------------------------------------------

	public void setGameOffersListener(GameOffersListener l) {
		if (gameOffersListener != null)
			return;
		gameOffersListener = l;
		if (ficsListenerTask == null)
			ficsListenerTask = new FicsListenerTask().execute();
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				write("sought\n");
			}
		}, 1000, 5000);
	}

	// -------------------------------------------------------------------------------------------------------

	public void setOnlineGameListener(OnlineGameListener l) {
		onlineGameListener = l;
		if (ficsListenerTask == null)
			ficsListenerTask = new FicsListenerTask().execute();
	}

	// -------------------------------------------------------------------------------------------------------

	private StringBuffer readUntil(String pattern) throws IOException {
		return readUntil(new String[] { pattern });
	}

	// -------------------------------------------------------------------------------------------------------

	public void seek(int minutes, int seconds, String colorSymbol, String ratedSymbol) {
		write("seek " + minutes + " " + seconds + " " + colorSymbol + " " + ratedSymbol + " \n");
	}

	// -------------------------------------------------------------------------------------------------------

	public void setSeekListener(SeekListener l) {
		seekListener = l;
		if (ficsListenerTask == null)
			ficsListenerTask = new FicsListenerTask().execute();
	}

	// -------------------------------------------------------------------------------------------------------

	public void withdraw() {
		write("withdraw\n");
	}

	// -------------------------------------------------------------------------------------------------------

	public void write(String s) {
		Log.d(Common.TAG, "ChessClient.write: \"" + s.trim() + "\"");
		try {
			out.write(s.getBytes());
		} catch (IOException e) {
			if (gameOffersListener != null)
				gameOffersListener.onConnException();
			if (onlineGameListener != null)
				onlineGameListener.onConnException();
			if (seekListener != null)
				seekListener.onConnException();
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private class FicsListenerTask extends AsyncTask<Void, Object, Integer> {
		ArrayList<GameOffer> gameOffers = new ArrayList<GameOffer>();
		OnlineGameState onlineGameState;

		// ---------------------------------------------------------------------------------------------------

		@Override
		protected Integer doInBackground(Void... nothing) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				while (true) {
					String line = "";

					line = br.readLine();
					if (line == null) {
						Log.d(Common.TAG, "'null'");
						if (seekListener != null)
							seekListener.onConnException();
						if (gameOffersListener != null)
							gameOffersListener.onConnException();
						if (onlineGameListener != null)
							onlineGameListener.onConnException();
						break;
					} else
						Log.d(Common.TAG, "'" + line + "'");

					if (seekListener != null) {
						Object o = null;
						if ((o = (Object) ficsParser.parseCreatingMatch(line)) != null) {
							publishProgress(CREATING_MATCH, o);
						} else if ((o = (Object) ficsParser.parseStyle12(line)) != null) {
							publishProgress(MATCH_STARTED, o);
						} else if ((o = (Object) ficsParser.parseRatingLine(line)) != null) {
							publishProgress(RATING, o);
						}
					}

					if (gameOffersListener != null) {
						Object o = null;
						onlineGameState = null;
						GameOffer game = ficsParser.parseSoughtLine(line);
						if (game != null) {
							gameOffers.add(game);
							continue;
						}
						if (ficsParser.parseSoughtEnd(line) > 0) {
							publishProgress(UPDATE_SOUGHT);
							continue;
						} else
							onlineGameState = ficsParser.parseStyle12(line);

						if (onlineGameState != null) {
							publishProgress(MATCH_STARTED, onlineGameState);
						} else if (ficsParser.parseSeekUnavailable(line)) {
							publishProgress(SEEK_UNAVAILABLE);
						} else if ((o = (Object) ficsParser.parseCreatingMatch(line)) != null) {
							publishProgress(CREATING_MATCH, o);
						}
					}

					else if (onlineGameListener != null) {
						Object o = null;
						if ((o = (Object) ficsParser.parseStyle12(line)) != null) {
							this.onlineGameState = (OnlineGameState) o;
							publishProgress(ONLINE_MOVE);
						} else if (ficsParser.parseDrawOffer(line)) {
							publishProgress(DRAW_OFFER);
						} else if ((Integer) (o = (Object) ficsParser.parseDrawAnswer(line)) != FicsParser.NULL) {
							int answer = (Integer) o;
							publishProgress(DRAW_ANSWER, answer);
						} else if ((o = (Object) ficsParser.parseMatchEnd(line)) != null) {
							publishProgress(MATCH_END, o);
						} else if ((o = ficsParser.parseChat(line)) != null) {
							publishProgress(CHAT_MESSAGE, o);
						} else if ((o = ficsParser.parseRatingChange(line)) != null) {
							publishProgress(RATING_CHANGE, o);
						}

					}
				}
			} catch (IOException e) {
				publishProgress(EXCEPTION);
			}

			
			return 0;
		}

		// ---------------------------------------------------------------------------------------------------

		@Override
		protected void onProgressUpdate(Object... o) {
			if (gameOffersListener == null && onlineGameListener == null && seekListener == null)
				return;
			int what = (Integer) o[0];
			if (what == UPDATE_SOUGHT) {
				Log.d("Ch", "update sought");
				gameOffersListener.onUpdate(gameOffers);
				gameOffers = new ArrayList<GameOffer>();
			} else if (what == EXCEPTION && gameOffersListener != null) {
				gameOffersListener.onConnException();
			} else if (what == EXCEPTION && onlineGameListener != null) {

			} else if (what == MATCH_STARTED) {
				if (seekListener != null)
					seekListener.onMatchStarted((OnlineGameState) o[1]);
				else if (gameOffersListener != null)
					gameOffersListener.onMatchStarted((OnlineGameState) o[1]);
			} else if (what == ONLINE_MOVE) {
				onlineGameListener.onOnlineMove(onlineGameState);
			} else if (what == DRAW_OFFER) {
				onlineGameListener.onDrawOffer();
			} else if (what == DRAW_ANSWER) {
				int answer = (Integer) o[1];
				onlineGameListener.onDrawAnswer(answer);
			} else if (what == CHAT_MESSAGE) {
				onlineGameListener.onChat((String) o[1]);
			} else if (what == MATCH_END) {
				onlineGameListener.onMatchEnd((OnlineGameActivity.MatchEnd) o[1]);
			} else if (what == SEEK_UNAVAILABLE) {
				gameOffersListener.onSeekUnavailable();
			} else if (what == RATING_CHANGE) {
				int[] ratings = (int[]) o[1];
				onlineGameListener.onRatingChange(ratings);
			} else if (what == CREATING_MATCH) {
				int[] ratings = (int[]) o[1];
				whiteRating = ratings[0];
				blackRating = ratings[1];
			} else if (what == RATING) {
				Rating rating = (Rating) o[1];
				seekListener.onRating(rating);
			}
		}

		// ---------------------------------------------------------------------------------------------------

		@Override
		protected void onPostExecute(Integer result) {
		}
	}
}
