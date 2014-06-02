/*
 * Copyright (C) 2012 Ciaran Gultnieks, ciaran@ciarang.com
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
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cz.hejl.chesswalk.Listeners.MoveListener;
import cz.hejl.chesswalk.Listeners.OnlineGameListener;

public class OnlineGameActivity extends Activity implements MoveListener,
        OnlineGameListener, OnClickListener {
    public static final int NO_RESULT = 10;
    public static final int RESIGNATION = 1;
    public static final int STALEMATE = 2;
    public static final int RULE50 = 3;
    public static final int AGREEMENT = 4;
    public static final int CHECKMATE = 5;
    public static final int DISCONNECTION = 6;
    public static final int REPETITION = 7;
    public static final int TIME = 8;
    public static final int MENU_DRAW = 0;
    public static final int MENU_RESIGN = 1;
    public static final int MENU_CHAT = 2;
    public static final String TAG = "OnlineGame";

    private static final int DIALOG_CHAT = 0;
    private static final int DIALOG_RESIGN = 1;

    private boolean flipped = false;
    private int myTime;
    private int opponentTime;
    private int sideToMove;
    private int startClockTime;
    private long startTime;
    private Button btShowChat;
    private Button btOfferDraw;
    private Button btRequestAbort;
    private Button btResign;
    private ChatAdapter chatAdapter;
    private ChatDialog chatDialog;
    private ChessBoardView chessBoard;
    private ChessClient chessClient = ChessClient.getInstance();
    private ClocksTask clocksTask = new ClocksTask();
    private OnlineGameState lastOnlineGameState;
    private Handler handler = new Handler();
    private MatchEnd matchEnd = null;
    private String myName;
    private String opponentName;
    private String[] files = { "a", "b", "c", "d", "e", "f", "g", "h" };
    private TextView tvInfo;
    private TextView tvMyClocks;
    private TextView tvOpponentClocks;
    private TextView tvYourMove;

    // -------------------------------------------------------------------------------------------------------

    private void disableChessboard() {
        chessBoard.setBlackEnabled(false);
        chessBoard.setWhiteEnabled(false);
        tvYourMove.setText("");
    }

    // -------------------------------------------------------------------------------------------------------

    private void draw() {
        chessClient.write("draw\n");
        showMessage(getString(R.string.youOfferedDraw));
    }

    // -------------------------------------------------------------------------------------------------------

    private void abort() {
        chessClient.write("abort\n");
        showMessage(getString(R.string.youRequestedAbort));
    }

    // -------------------------------------------------------------------------------------------------------

    private String formatSeconds(int s) {
        int min = s / 60;
        int sec = s % 60;
        String zero = "";
        if (sec < 10)
            zero = "0";

        return min + ":" + zero + sec;
    }

    // -------------------------------------------------------------------------------------------------------

    private void showMessage(String s) {
        tvInfo.setText(s);
    }

    // -------------------------------------------------------------------------------------------------------

    public void onChat(String message) {
        int spaceIndex = opponentName.lastIndexOf(" ");
        if (spaceIndex == -1)
            spaceIndex = opponentName.length();
        SpannableString ss = new SpannableString(opponentName.substring(0,
                spaceIndex)
                + ": " + message);
        showMessage(ss.toString());
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, spaceIndex + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        chatAdapter.addMessage(ss);

        if (chatDialog == null || !chatDialog.isShowing())
            btShowChat.setTypeface(Typeface.DEFAULT_BOLD);
    }

    // -------------------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btShowChat) {
            btShowChat.setTypeface(Typeface.DEFAULT);
            showDialog(DIALOG_CHAT);
        } else if (v.getId() == R.id.btOfferDraw) {
            draw();
        } else if (v.getId() == R.id.btRequestAbort) {
            abort();
        } else if (v.getId() == R.id.btResign) {
            resign();
        }
    }

    // -------------------------------------------------------------------------------------------------------

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);

        OnlineGameState onlineGameState = (OnlineGameState) getIntent()
                .getSerializableExtra("onlineGameState");
        lastOnlineGameState = onlineGameState;

        // set up chat
        chatAdapter = new ChatAdapter();
        if (savedInstanceState != null)
            chatAdapter.messages = (ArrayList<SpannableString>) savedInstanceState
                    .getSerializable("messages");

        // init chessboard
        chessBoard = (ChessBoardView) findViewById(R.id.chessBoard);
        chessBoard.setMoveListener(this);

        // set flipped
        if (onlineGameState.blackName.equals(chessClient.username)) {
            flipped = true;
            chessBoard.setFlipped(true);
            chessBoard.setBlackEnabled(false);
            chessBoard.setWhiteEnabled(false);
        }

        TextView tvOpponentName = (TextView) findViewById(R.id.tvOpponentName);
        TextView tvMyName = (TextView) findViewById(R.id.tvMyName);
        String blackRating = "";
        String whiteRating = "";
        if (ChessClient.blackRating != -1)
            blackRating = " (" + ChessClient.blackRating + ")";
        if (ChessClient.whiteRating != -1)
            whiteRating = " (" + ChessClient.whiteRating + ")";
        if (flipped) {
            myName = onlineGameState.blackName + blackRating;
            opponentName = onlineGameState.whiteName + whiteRating;
        } else {
            myName = onlineGameState.whiteName + whiteRating;
            opponentName = onlineGameState.blackName + blackRating;
        }
        if (chessClient.guest)
            myName = getString(R.string.me);
        tvOpponentName.setText(opponentName);
        tvMyName.setText(myName);

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvOpponentClocks = (TextView) findViewById(R.id.tvOpponentClocks);
        tvMyClocks = (TextView) findViewById(R.id.tvMyClocks);
        tvYourMove = (TextView) findViewById(R.id.tvYourMove);

        // set up menu
        btShowChat = (Button) findViewById(R.id.btShowChat);
        btShowChat.setOnClickListener(this);
        btOfferDraw = (Button) findViewById(R.id.btOfferDraw);
        btOfferDraw.setOnClickListener(this);
        btRequestAbort = (Button) findViewById(R.id.btRequestAbort);
        btRequestAbort.setOnClickListener(this);
        btResign = (Button) findViewById(R.id.btResign);
        btResign.setOnClickListener(this);

        // restore instance state
        if (savedInstanceState != null) {
            Board board = new Board();
            board.fromFEN(savedInstanceState.getString("FEN"));
            chessBoard.setBoard(board);
            chessBoard.setBlackEnabled(savedInstanceState
                    .getBoolean("blackAllowed"));
            chessBoard.setWhiteEnabled(savedInstanceState
                    .getBoolean("whiteAllowed"));
            chessBoard.setFlipped(savedInstanceState.getBoolean("flipped"));
            chessBoard.setLastMoveFrom(savedInstanceState
                    .getInt("lastMoveFrom"));
            chessBoard.setLastMoveTo(savedInstanceState.getInt("lastMoveTo"));
            sideToMove = (chessBoard.getBlackEnabled() || chessBoard
                    .getWhiteEnabled()) ? 1 : -1;
            startTime = savedInstanceState.getLong("startTime");
            startClockTime = savedInstanceState.getInt("startClockTime");
            myTime = savedInstanceState.getInt("myTime");
            opponentTime = savedInstanceState.getInt("opponentTime");
            tvMyClocks.setText(formatSeconds(myTime));
            tvOpponentClocks.setText(formatSeconds(opponentTime));
            tvYourMove.setText(savedInstanceState
                    .getCharSequence("tvYourMoveText"));
            tvInfo.setText(savedInstanceState.getCharSequence("tvInfoText"));
        } else {
            onOnlineMove(onlineGameState);
        }

        chessClient.setOnlineGameListener(this);
    }

    // -------------------------------------------------------------------------------------------------------

    public void onDrawAnswer(int answer) {
        if (answer == FicsParser.ACCEPT)
            tvInfo.setText(MessageFormat.format(
                    getString(R.string.acceptsDraw), opponentName));
        else if (answer == FicsParser.DECLINE)
            tvInfo.setText(MessageFormat.format(
                    getString(R.string.declinesDraw), opponentName));
    }

    // -------------------------------------------------------------------------------------------------------

    public void onDrawOffer() {
        new AlertDialog.Builder(this).setMessage(
                opponentName + " offers you a draw.").setPositiveButton(
                "Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chessClient.write("accept\n");
                    }
                }).setNegativeButton("Decline",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chessClient.write("decline\n");
                    }
                }).show();
    }

    // -------------------------------------------------------------------------------------------------------

    public void onAbortAnswer(int answer) {
        if (answer == FicsParser.ACCEPT)
            tvInfo.setText(MessageFormat.format(
                    getString(R.string.acceptsAbort), opponentName));
        else if (answer == FicsParser.DECLINE)
            tvInfo.setText(MessageFormat.format(
                    getString(R.string.declinesAbort), opponentName));
    }

    // -------------------------------------------------------------------------------------------------------

    public void onAbortOffer() {
        new AlertDialog.Builder(this).setMessage(
                opponentName + " wants to abort.").setPositiveButton(
                "Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chessClient.write("abort\n");
                    }
                }).setNegativeButton("Decline",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chessClient.write("decline\n");
                    }
                }).show();
    }

    // -------------------------------------------------------------------------------------------------------

    public void onConnException() {
    }

    // -------------------------------------------------------------------------------------------------------

    @Override
    public Dialog onCreateDialog(int dialogId) {
        if (dialogId == DIALOG_CHAT) {
            chatDialog = new ChatDialog(this);
            return chatDialog;
        } else if (dialogId == DIALOG_RESIGN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.doYouWantToResign))
                    .setCancelable(true).setPositiveButton(
                            getString(R.string.btResign),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    chessClient.write("resign\n");
                                    chessClient.removeOnlineGameListener();
                                    // stop clocks
                                    handler.removeCallbacks(clocksTask);
                                    finish();
                                }
                            }).setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                }
                            });
            return builder.create();
        }
        return null;
    }

    // -------------------------------------------------------------------------------------------------------

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (matchEnd == null) { // game has not ended
                showDialog(DIALOG_RESIGN);
            } else {
                chessClient.removeOnlineGameListener();
                // stop clocks
                handler.removeCallbacks(clocksTask);
                finish();
            }
            return true;
        }

        return false;
    }

    // -------------------------------------------------------------------------------------------------------

    public void onMatchEnd(MatchEnd matchEnd) {
        this.matchEnd = matchEnd;
        // stop clocks
        handler.removeCallbacks(clocksTask);
        // disable chessboard
        disableChessboard();
        // disable buttons
        btOfferDraw.setEnabled(false);
        btRequestAbort.setEnabled(false);
        btResign.setEnabled(false);
        // show message
        showMessage(matchEnd.getMessage(flipped, this));
        tvYourMove.setText(getString(R.string.gameEnded));
    }

    // -------------------------------------------------------------------------------------------------------

    @Override
    public void onOnlineMove(OnlineGameState onlineGameState) {
        startTime = SystemClock.uptimeMillis();

        if (onlineGameState.moveToBeMade == 1 && onlineGameState.whiteToMove) {
            // First move - set up board...
            chessBoard.setBoard(onlineGameState.getBoard());
        } else if(!chessBoard.hasBoard()) {
            // Must be resuming - set up board...
            chessBoard.setBoard(onlineGameState.getBoard());
        } else if (onlineGameState.sideToMove == 1) {
            if (lastOnlineGameState.moveToBeMade != onlineGameState.moveToBeMade
                    || lastOnlineGameState.whiteToMove != onlineGameState.whiteToMove)
                chessBoard.movePiece(onlineGameState.from, onlineGameState.to);
        }
        lastOnlineGameState = onlineGameState;

        tvInfo.setText("");

        // turn off clock ticking
        handler.removeCallbacks(clocksTask);

        // update clocks
        myTime = flipped ? onlineGameState.blackTime
                : onlineGameState.whiteTime;
        opponentTime = flipped ? onlineGameState.whiteTime
                : onlineGameState.blackTime;
        tvMyClocks.setText(formatSeconds(myTime));
        tvOpponentClocks.setText(formatSeconds(opponentTime));

        sideToMove = onlineGameState.sideToMove;

        if (sideToMove == 1) {
            tvYourMove.setText(getString(R.string.yourMove));
            tvMyClocks.setTextColor(0xff339933);
            tvOpponentClocks.setTextColor(0xff323232);
            startClockTime = myTime;
            if (flipped)
                chessBoard.setColorToMove(-1);
            else
                chessBoard.setColorToMove(1);
        } else if (sideToMove == -1) {
            tvYourMove.setText("");
            tvMyClocks.setTextColor(0xff323232);
            tvOpponentClocks.setTextColor(0xff339933);
            startClockTime = opponentTime;
            disableChessboard();
        }

        if (onlineGameState.moveToBeMade > 1)
            handler.postAtTime(clocksTask, startTime + 1000);
    }

    // -------------------------------------------------------------------------------------------------------

    @Override
    public void onRatingChange(int[] ratings) {
        showMessage(matchEnd.message + ". Your rating: " + ratings[0] + " > "
                + ratings[1]);
    }

    // -------------------------------------------------------------------------------------------------------

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("FEN", chessBoard.board.toFEN());
        outState.putBoolean("blackAllowed", chessBoard.getBlackEnabled());
        outState.putBoolean("whiteAllowed", chessBoard.getWhiteEnabled());
        outState.putBoolean("flipped", chessBoard.getFlipped());
        outState.putInt("lastMoveFrom", chessBoard.getLastMoveFrom());
        outState.putInt("lastMoveTo", chessBoard.getLastMoveTo());
        outState.putLong("startTime", startTime);
        outState.putInt("startClockTime", startClockTime);
        outState.putInt("myTime", myTime);
        outState.putInt("opponentTime", opponentTime);
        outState.putCharSequence("tvYourMoveText", tvYourMove.getText());
        outState.putCharSequence("tvInfoText", tvInfo.getText());
        outState.putSerializable("messages", chatAdapter.messages);
    }

    // -------------------------------------------------------------------------------------------------------

    @Override
    public void pieceMoved(Move move) {
        chessBoard.setBlackEnabled(false);
        chessBoard.setWhiteEnabled(false);
        tvYourMove.setText("");
        chessClient.write(files[move.from & 7] + (move.from / 16 + 1) + "-"
                + files[move.to & 7] + (move.to / 16 + 1) + "\n");
    }

    // -------------------------------------------------------------------------------------------------------

    private void resign() {
        new AlertDialog.Builder(this).setMessage(R.string.doYouWantToResign)
                .setPositiveButton(R.string.btResign,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                chessClient.write("resign\n");
                            }
                        }).setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).show();
    }

    // -------------------------------------------------------------------------------------------------------

    private class ClocksTask implements Runnable {
        @Override
        public void run() {
            int passedSeconds = (int) (SystemClock.uptimeMillis() - startTime) / 1000;
            int clockTime = startClockTime - passedSeconds;
            if (clockTime < 0)
                clockTime = 0;
            if (sideToMove == 1) {
                myTime = clockTime;
                Log.d(TAG, formatSeconds(myTime));
                tvMyClocks.setText(formatSeconds(myTime));
            } else if (sideToMove == -1) {
                opponentTime = clockTime;
                tvOpponentClocks.setText(formatSeconds(opponentTime));
            }

            if (clockTime > 0)
                handler
                        .postAtTime(this, startTime + (passedSeconds + 1)
                                * 1000);
        }
    }

    // -------------------------------------------------------------------------------------------------------

    private class ChatDialog extends Dialog {

        private EditText edChat;

        protected ChatDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            setContentView(R.layout.chat);

            ListView lvChat = (ListView) findViewById(R.id.lvChat);
            lvChat.setAdapter(chatAdapter);

            edChat = (EditText) findViewById(R.id.edChat);
            edChat.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                        KeyEvent event) {
                    send();
                    return true;
                }
            });

            Button btReply = (Button) findViewById(R.id.btChat);
            btReply.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    send();
                }
            });
        }

        private void send() {
            String message = edChat.getText().toString();
            chessClient.say(message);
            edChat.setText("");
            String me = getString(R.string.me) + ": ";
            SpannableString ss = new SpannableString(me + message);
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, me.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatAdapter.addMessage(ss);
        }

    }

    // -------------------------------------------------------------------------------------------------------

    private class ChatAdapter extends BaseAdapter {

        public ArrayList<SpannableString> messages = new ArrayList<SpannableString>();

        private LayoutInflater inflater = getLayoutInflater();

        void addMessage(SpannableString message) {
            messages.add(message);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.chat_item, null);
            }
            ((TextView) convertView).setText(messages.get(position));

            return convertView;
        }

    }

    // -------------------------------------------------------------------------------------------------------

    public static class MatchEnd {
        int result;
        int type;
        String black;
        String message;
        String white;

        public String getMessage(boolean flipped, Context context) {
            boolean iWon = false;
            if (result == 1 && !flipped || result == -1 && flipped)
                iWon = true;
            String opponent = flipped ? white : black;

            if (type == RESIGNATION) {
                if (iWon)
                    return MessageFormat.format(context
                            .getString(R.string.matchEndHeResigned), opponent);
                else
                    return context.getString(R.string.matchEndIResigned);
            } else if (type == STALEMATE) {
                return context.getString(R.string.matchEndStalemate);
            } else if (type == RULE50) {
                return context.getString(R.string.matchEnd50);
            } else if (type == AGREEMENT) {
                return context.getString(R.string.matchEndAgreement);
            } else if (type == CHECKMATE) {
                if (iWon)
                    return context.getString(R.string.matchEndICheckmated);
                else
                    return MessageFormat
                            .format(context
                                    .getString(R.string.matchEndHeCheckmated),
                                    opponent);
            } else if (type == DISCONNECTION) {
                if (iWon)
                    return MessageFormat.format(context
                            .getString(R.string.matchEndHeDisconnected),
                            opponent);
                else
                    return context.getString(R.string.matchEndIDisconnected);
            } else if (type == TIME) {
                if (iWon)
                    return MessageFormat.format(context
                            .getString(R.string.matchEndHeOutOfTime), opponent);
                else
                    return MessageFormat.format(context
                            .getString(R.string.matchEndIOutOfTime), opponent);
            } else if (type == REPETITION) {
                return context.getString(R.string.matchEndRepetition);
            } else
                return message;
        }

        public MatchEnd(String white, String black, String message,
                String result) {
            this.white = white;
            this.black = black;
            this.message = message;
            if (message.indexOf("resign") != -1)
                type = RESIGNATION;
            else if (message.indexOf("stalemate") != -1)
                type = STALEMATE;
            else if (message.indexOf("50") != -1)
                type = RULE50;
            else if (message.indexOf("agreement") != -1)
                type = AGREEMENT;
            else if (message.indexOf("checkmated") != -1)
                type = CHECKMATE;
            else if (message.indexOf("disconnection") != -1)
                type = DISCONNECTION;
            else if (message.indexOf("time") != -1)
                type = TIME;
            else if (message.indexOf("repetition") != -1)
                type = REPETITION;
            else
                type = -1;

            if (result.equals("1-0"))
                this.result = 1;
            else if (result.equals("0-1"))
                this.result = -1;
            else if (result.equals("1/2-1/2"))
                this.result = 0;
            else if (result.equals("*"))
                this.result = NO_RESULT;
        }
    }

}
