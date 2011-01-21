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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cz.hejl.chesswalk.Listeners.MoveListener;

public class OfflineGame extends Activity implements MoveListener,
        OnClickListener {
    private static final String DATABASE_NAME = "chess";
    private static final String MOVES_TABLE_NAME = "moves";

    private boolean gameEnded = false;
    private boolean blackHuman = false;
    private boolean whiteHuman = true;
    private int strength;
    private ArrayList<Move> moveHistory = new ArrayList<Move>();
    private ChessBoardView chessBoard;
    private Engine engine = new Engine();
    private ProgressBar pbThinking;
    private TextView tvOfflineGameInfo;
    private TextView tvOfflineTopInfo;

    static {
        System.loadLibrary("chesswalk");
    }

    public native int getBestMove(String fen, int depth, int moveTime);

    private void computerMove() {
        if (gameEnded) {
            setTopInfo();
            return;
        }
        chessBoard.setWhiteEnabled(false);
        chessBoard.setBlackEnabled(false);
        new EngineTask().execute();
    }

    private void postDoMove(Move move) {
        tvOfflineGameInfo.setText(move.info);
        chessBoard.setLastMoveFrom(move.from);
        chessBoard.setLastMoveTo(move.to);
        setTopInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        chessBoard.setWhiteEnabled(true);
        chessBoard.setBlackEnabled(false);
        chessBoard.setLastMoveFrom(-1);
        chessBoard.setLastMoveTo(-1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btNewGame) {
            Intent i = new Intent(OfflineGame.this, NewGame.class);
            startActivityForResult(i, 0);
        } else if (v.getId() == R.id.btUndo) {
            if (moveHistory.size() > 0) {
                chessBoard.undoMove(moveHistory.get(moveHistory.size() - 1));
                moveHistory.remove(moveHistory.size() - 1);
                if (chessBoard.getWhiteEnabled() && whiteHuman == false
                        || chessBoard.getBlackEnabled() && blackHuman == false) {
                    if (moveHistory.size() > 0) {
                        chessBoard.undoMove(moveHistory
                                .get(moveHistory.size() - 1));
                        moveHistory.remove(moveHistory.size() - 1);
                    }
                }
                if (moveHistory.size() == 0) {
                    tvOfflineGameInfo.setText("");
                    chessBoard.setLastMoveFrom(-1);
                    chessBoard.setLastMoveTo(-1);
                } else {
                    Move lastMove = moveHistory.get(moveHistory.size() - 1);
                    postDoMove(lastMove);
                }
                setTopInfo();
            }
        } else if (v.getId() == R.id.btFlip) {
            chessBoard.setFlipped(chessBoard.getFlipped() ? false : true);
            chessBoard.setBoard(chessBoard.board);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offline_game);

        tvOfflineGameInfo = (TextView) findViewById(R.id.tvOfflineGameInfo);
        pbThinking = (ProgressBar) findViewById(R.id.pbThinking);
        tvOfflineTopInfo = (TextView) findViewById(R.id.tvOfflineTopInfo);

        // set up buttons
        ((Button) findViewById(R.id.btNewGame)).setOnClickListener(this);
        ((Button) findViewById(R.id.btUndo)).setOnClickListener(this);
        ((Button) findViewById(R.id.btFlip)).setOnClickListener(this);

        // set up chessboard
        chessBoard = (ChessBoardView) findViewById(R.id.offlineChessBoard);
        chessBoard.setMoveListener(this);
    }

    @Override
    protected void onPause() {
        // TODO: handle Board.hashHistory (computer move, save/restore, undo)
        super.onPause();

        // save chessboard, whiteHuman, blackHuman and flipped
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("FEN", chessBoard.board.toFEN());
        editor.putBoolean("whiteHuman", whiteHuman);
        editor.putBoolean("blackHuman", blackHuman);
        editor.putBoolean("flipped", chessBoard.getFlipped());
        editor.putBoolean("gameEnded", gameEnded);
        editor.commit();

        SQLiteDatabase db = openOrCreateDatabase(DATABASE_NAME, 0, null);
        db.execSQL("DROP TABLE IF EXISTS " + MOVES_TABLE_NAME + ";");
        db
                .execSQL("CREATE TABLE IF NOT EXISTS "
                        + MOVES_TABLE_NAME
                        + " (id INTEGER, sq_from INTEGER, sq_to INTEGER, piece INTEGER, capture INTEGER, en_passant INTEGER, "
                        + "castling0 INTEGER, castling1 INTEGER, castling2 INTEGER, castling3 INTEGER, info VARCHAR(60));");
        db.execSQL("DELETE FROM " + MOVES_TABLE_NAME + ";");

        for (int i = 0; i < moveHistory.size(); i++) {
            Move move = moveHistory.get(i);
            db.execSQL("INSERT INTO " + MOVES_TABLE_NAME + " VALUES (" + i
                    + ", " + move.from + ", " + move.to + ", " + move.piece
                    + ", " + move.capture + ", " + move.enPassant + ", "
                    + (move.castlingRights[0] ? 1 : 0) + ", "
                    + (move.castlingRights[1] ? 1 : 0) + ", "
                    + (move.castlingRights[2] ? 1 : 0) + ", "
                    + (move.castlingRights[3] ? 1 : 0) + ", '" + move.info
                    + "');");
        }

        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restore chessboard, flipped, strength, whiteHuman and
        // blackHuman etc.
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        String FEN = settings.getString("FEN", Board.STARTING_FEN);
        Board board = new Board();
        board.fromFEN(FEN);
        chessBoard.setBoard(board);
        engine.board = chessBoard.board;
        if (chessBoard.board.toMove == -1) {
            chessBoard.setBlackEnabled(true);
            chessBoard.setWhiteEnabled(false);
        } else if (chessBoard.board.toMove == 1) {
            chessBoard.setBlackEnabled(false);
            chessBoard.setWhiteEnabled(true);
        }
        whiteHuman = settings.getBoolean("whiteHuman", true);
        blackHuman = settings.getBoolean("blackHuman", true);
        boolean flipped = settings.getBoolean("flipped", false);
        chessBoard.setFlipped(flipped);
        strength = settings.getInt("strength", 1);
        gameEnded = settings.getBoolean("gameEnded", false);

        // restore move history
        if (databaseList().length > 0) {
            moveHistory = new ArrayList<Move>();
            SQLiteDatabase db = openOrCreateDatabase(DATABASE_NAME, 0, null);
            Cursor cursor = db.query(MOVES_TABLE_NAME,
                    new String[] { "id", "sq_from", "sq_to", "piece",
                            "capture", "en_passant", "castling0", "castling1",
                            "castling2", "castling3", "info" }, null, null,
                    null, null, "id");
            if (cursor.moveToFirst()) {
                int columnFrom = cursor.getColumnIndex("sq_from");
                int columnTo = cursor.getColumnIndex("sq_to");
                int columnPiece = cursor.getColumnIndex("piece");
                int columnCapture = cursor.getColumnIndex("capture");
                int columnEnPassant = cursor.getColumnIndex("en_passant");
                int columnCastling0 = cursor.getColumnIndex("castling0");
                int columnCastling1 = cursor.getColumnIndex("castling1");
                int columnCastling2 = cursor.getColumnIndex("castling2");
                int columnCastling3 = cursor.getColumnIndex("castling3");
                int columnInfo = cursor.getColumnIndex("info");
                do {
                    Move move = new Move(cursor.getInt(columnFrom), cursor
                            .getInt(columnTo), cursor.getInt(columnPiece),
                            cursor.getInt(columnCapture), cursor
                                    .getInt(columnEnPassant), new boolean[] {
                                    cursor.getInt(columnCastling0) != 0,
                                    cursor.getInt(columnCastling1) != 0,
                                    cursor.getInt(columnCastling2) != 0,
                                    cursor.getInt(columnCastling3) != 0 },
                            cursor.getString(columnInfo));
                    moveHistory.add(move);
                } while (cursor.moveToNext());
            }
            db.close();

            if (moveHistory.size() != 0) {
                Move lastMove = moveHistory.get(moveHistory.size() - 1);
                postDoMove(lastMove);
            }

            if (whiteHuman == false && chessBoard.getWhiteEnabled()
                    && gameEnded == false)
                computerMove();

            setTopInfo();
        }
    }

    @Override
    public void pieceMoved(Move move) {
        moveHistory.add(move);

        if (move.piece > 0)
            move.info = "White moved " + move.toString() + ". ";
        else
            move.info = "Black moved " + move.toString() + ". ";

        if (chessBoard.board.inCheck(chessBoard.board.toMove)) {
            if (chessBoard.board.isCheckmate()) {
                move.info += "Checkmate!";
                gameEnded = true;
            } else
                move.info += "Check!";
        } else if (chessBoard.board.isStalemate()) {
            move.info += "Stalemate!";
            gameEnded = true;
        }

        postDoMove(move);

        if (chessBoard.board.toMove == 1 && whiteHuman == false
                || chessBoard.board.toMove == -1 && blackHuman == false) {
            computerMove();
        }

        setTopInfo();
    }

    private void setTopInfo() {
        pbThinking.setVisibility(View.GONE);
        if (gameEnded) {
            tvOfflineTopInfo.setText("Game ended");
            return;
        }
        if (chessBoard.getBlackEnabled()) {
            if (whiteHuman)
                tvOfflineTopInfo.setText("Black's move");
            else
                tvOfflineTopInfo.setText("Your move");
        } else if (chessBoard.getWhiteEnabled()) {
            if (blackHuman)
                tvOfflineTopInfo.setText("White's move");
            else
                tvOfflineTopInfo.setText("Your move");
        } else {
            pbThinking.setVisibility(View.VISIBLE);
            if (whiteHuman)
                tvOfflineTopInfo.setText("Black is thinking...");
            else if (blackHuman)
                tvOfflineTopInfo.setText("White is thinking...");
        }
    }

    private class EngineTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... nothing) {
            int intMove = getBestMove(chessBoard.board.toFEN(), 10,
                    strength * 1000);
            return intMove;
            /*
             * engine.board = new Board();
             * engine.board.fromFEN(chessBoard.board.toFEN()); return
             * engine.bestMove(10, strength * 10);
             */
        }

        @Override
        protected void onProgressUpdate(Void... nothing) {
        }

        @Override
        protected void onPostExecute(Integer intMove) {
            Move move = new Move(chessBoard.board, intMove & 127,
                    (intMove >> 7) & 127);

            chessBoard.movePiece(move.from, move.to);

            moveHistory.add(move);

            if (move.piece > 0)
                move.info = "White moved " + move.toString() + ". ";
            else
                move.info = "Black moved " + move.toString() + ". ";

            if (chessBoard.board.inCheck(chessBoard.board.toMove)) {
                if (chessBoard.board.isCheckmate())
                    move.info += "Checkmate!";
                else
                    move.info += "Check!";
            } else if (chessBoard.board.isStalemate())
                move.info += "Stalemate!";

            chessBoard.setColorToMove(chessBoard.board.toMove);
            postDoMove(move);
        }
    }

}
