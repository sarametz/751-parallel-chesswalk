package cz.hejl.chesswalk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cz.hejl.chesswalk.Listeners.MoveListener;

public class ChessBoardViewOld extends View {

	/** Can player move with black or white pieces? Can be NONE, WHITE or BLACK. */
	public boolean blackAllowed = false;
	public boolean flipped = false;
	public int lastMoveFrom = -1;
	public int lastMoveTo = -1;
	public boolean whiteAllowed = true;
	public Board board;

	private static final int MODE_NORMAL = 0;
	private static final int MODE_PIECE_DOWN = 1;
	private static final int MODE_PIECE_SELECTED = 2;
	private static final int MODE_PIECE_DRAGGED = 3;

	private boolean[] legalSquares;
	private char[] piecesChars = { '-', 'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k' };
	private int mode = MODE_NORMAL;
	private int selectedPiece;
	private int size;
	private int[] piecesInts = { 0, 1, 2, 3, 4, 5, 6, -1, -2, -3, -4, -5, -6 };
	private Drawable squareHighlight;
	private Drawable squareFrame;
	private Drawable[] pieces = new Drawable[12];
	private Paint blackPaint = new Paint();
	private Paint whitePaint = new Paint();
	private Paint selectedPiecePaint = new Paint();;

	MoveListener listener;

	// -----------------------------------------------------------------------------------------------------------

	public ChessBoardViewOld(Context context, AttributeSet attrs) {
		super(context, attrs);

		// load images
		Resources res = this.getResources();
		pieces[0] = res.getDrawable(R.drawable.bpawn);
		pieces[1] = res.getDrawable(R.drawable.bknight);
		pieces[2] = res.getDrawable(R.drawable.bbishop);
		pieces[3] = res.getDrawable(R.drawable.brook);
		pieces[4] = res.getDrawable(R.drawable.bqueen);
		pieces[5] = res.getDrawable(R.drawable.bking);
		pieces[6] = res.getDrawable(R.drawable.wpawn);
		pieces[7] = res.getDrawable(R.drawable.wknight);
		pieces[8] = res.getDrawable(R.drawable.wbishop);
		pieces[9] = res.getDrawable(R.drawable.wrook);
		pieces[10] = res.getDrawable(R.drawable.wqueen);
		pieces[11] = res.getDrawable(R.drawable.wking);
		squareHighlight = res.getDrawable(R.drawable.selected_piece);
		squareFrame = res.getDrawable(R.drawable.square_frame);

		// initialize board
		board = new Board();

		// init paints
		selectedPiecePaint.setARGB(255, 0, 0, 255);
		selectedPiecePaint.setStyle(Style.STROKE);
		selectedPiecePaint.setStrokeWidth(2);
		whitePaint.setARGB(255, 146, 182, 253);
		blackPaint.setARGB(255, 71, 121, 251);

		size = Integer.parseInt(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width").substring(0, 3));
	}

	// -----------------------------------------------------------------------------------------------------------

	private void drawPiece(Canvas canvas, int square) {
		int squareX = (square & 7);
		int squareY = 7 - (square / 16);
		if (flipped) {
			squareX = 7 - squareX;
			squareY = 7 - squareY;
		}

		// draw black or white background
		int rank = square / 16;
		int file = square & 7;
		if (rank % 2 == file % 2)
			canvas.drawRect(squareX * (size / 8), squareY * (size / 8), (squareX + 1) * (size / 8), (squareY + 1) * (size / 8),
					blackPaint);
		else
			canvas.drawRect(squareX * (size / 8), squareY * (size / 8), (squareX + 1) * (size / 8), (squareY + 1) * (size / 8),
					whitePaint);

		// highlight last move from and to
		if (square == lastMoveFrom || square == lastMoveTo) {
			squareFrame.setBounds(squareX * (size / 8), squareY * (size / 8), (squareX + 1) * (size / 8), (squareY + 1) * (size / 8));
			squareFrame.draw(canvas);
		}

		// highlight selected piece
		if (mode != MODE_NORMAL && square == selectedPiece) {
			squareHighlight.setBounds(squareX * (size / 8), squareY * (size / 8), (squareX + 1) * (size / 8), (squareY + 1)
					* (size / 8));
			squareHighlight.draw(canvas);
		}

		int pieceType = board.board0x88[square];
		if (pieceType != 0) {
			if (pieceType > 0)
				pieceType += 5;
			else
				pieceType = (pieceType * -1) - 1;
			pieces[pieceType].setBounds(squareX * (size / 8), squareY * (size / 8), (squareX + 1) * (size / 8), (squareY + 1)
					* (size / 8));
			pieces[pieceType].draw(canvas);
		}

		// higlight legal square
		if (mode != MODE_NORMAL && legalSquares[square]) {
			squareHighlight.setBounds(squareX * (size / 8), squareY * (size / 8), (squareX + 1) * (size / 8), (squareY + 1)
					* (size / 8));
			squareHighlight.draw(canvas);
		}
	}

	// -----------------------------------------------------------------------------------------------------------

	/**
	 * Returns square index in board.board0x88 for a given point on board. Takes
	 * squared into account.
	 */
	private int getSquare(int x, int y) {
		int file = x / (size / 8);
		int rank = 7 - y / (size / 8);
		if (flipped) {
			file = 7 - file;
			rank = 7 - rank;
		}

		return rank * 16 + file;
	}

	// -----------------------------------------------------------------------------------------------------------

	public void movePiece(int from, int to) {
		Log.d("Ch", "move");
		Move move = new Move(board, from, to);
		board.doMove(move);
		invalidate();
	}

	// -----------------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		// go through all squares
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			drawPiece(canvas, i);
		}
	}

	// -----------------------------------------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(size, size);
	}

	// -----------------------------------------------------------------------------------------------------------

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mode == MODE_NORMAL) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				selectedPiece = getSquare((int) event.getX(), (int) event.getY());
				int pieceType = board.board0x88[selectedPiece];
				if (pieceType != 0 && ((pieceType < 0 && blackAllowed) || (pieceType > 0 && whiteAllowed))) {
					legalSquares = board.legalMovesMap(selectedPiece);
					mode = MODE_PIECE_DOWN;
					invalidate();
				}
			}
		} else if (mode == MODE_PIECE_DOWN) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mode = MODE_PIECE_SELECTED;
				invalidate();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				mode = MODE_PIECE_DRAGGED;
				invalidate();
			}
		} else if (mode == MODE_PIECE_SELECTED) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mode = MODE_NORMAL;

				int to = getSquare((int) event.getX(), (int) event.getY());
				if (legalSquares[to]) {
					Move move = new Move(board, selectedPiece, to);
					board.doMove(move);
					lastMoveFrom = selectedPiece;
					lastMoveTo = to;
					switchColorToMove();
					if (listener != null)
						listener.pieceMoved(move);
				} else {
					int pieceType = board.board0x88[to];
					if (to != selectedPiece && pieceType != 0 && ((pieceType < 0 && blackAllowed) || (pieceType > 0 && whiteAllowed))) {
						selectedPiece = to;
						legalSquares = board.legalMovesMap(selectedPiece);
						mode = MODE_PIECE_DOWN;
					}
				}

				invalidate();
			}
		} else if (mode == MODE_PIECE_DRAGGED) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mode = MODE_PIECE_SELECTED;
			}
		}

		return true;
	}

	// -----------------------------------------------------------------------------------------------------------

	public void reset() {
		board.init();
		setColorToMove(1);
		lastMoveFrom = -1;
		lastMoveTo = -1;
	}

	// -----------------------------------------------------------------------------------------------------------

	public void setChessMoveListener(MoveListener listener) {
		this.listener = listener;
	}

	// -----------------------------------------------------------------------------------------------------------

	public void setColorToMove(int color) {
		if (color == 1) {
			blackAllowed = false;
			whiteAllowed = true;
		} else if (color == -1) {
			blackAllowed = true;
			whiteAllowed = false;
		}
		board.toMove = color;
	};

	// -----------------------------------------------------------------------------------------------------------

	public void switchColorToMove() {
		if (whiteAllowed) {
			setColorToMove(-1);
		} else if (blackAllowed) {
			setColorToMove(1);
		}
	};

	// -----------------------------------------------------------------------------------------------------------

	public void undoMove(Move move) {
		board.undoMove(move);
		blackAllowed = !blackAllowed;
		whiteAllowed = !whiteAllowed;
	}

	// -----------------------------------------------------------------------------------------------------------

	public void updatePieces(OnlineGameState onlineGameState) {
		/*for (int i = 0; i <= 7; i++) {
			String s = onlineGameState.ranks[i];
			int rank = 7 - i;
			for (int j = 0; j < 8; j++) {
				int pieceType = 0;
				for (int k = 0; k < piecesChars.length; k++) {
					if (s.charAt(j) == piecesChars[k]) {
						pieceType = piecesInts[k];
						break;
					}
				}
				board.board0x88[rank * 16 + j] = pieceType;
			}
		}

		lastMoveFrom = onlineGameState.from;
		lastMoveTo = onlineGameState.to;

		invalidate();*/
	}
}
