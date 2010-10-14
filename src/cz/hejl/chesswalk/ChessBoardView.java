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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cz.hejl.chesswalk.Listeners.MoveListener;

public class ChessBoardView extends SurfaceView implements SurfaceHolder.Callback {

	public Board board;

	private static final int CHESSBOARD_WIDTH = 320;
	private static final int DRAGGING_DELAY = 450;
	private static final int Z_ORDER_CHESSBOARD = 0;
	private static final int Z_ORDER_CHESSPIECE = 5;
	private static final float LEFT_OFFSET = 4;
	private static final float TOP_OFFSET = 4;
	private static final float SQUARE_SIZE = 39;

	private boolean blackEnabled = false;
	private boolean flipped = false;
	private boolean pieceDragged = false;
	private boolean pieceSelected = false;
	private boolean whiteEnabled = false;
	private boolean[] legalSquares;
	private float draggingStartX;
	private float draggingStartY;
	private float leftOffset = 21f;
	private float scale = 1;
	private float squareSize = 38f;
	private float topOffset = 21f;
	private int lastMoveFrom = -1;
	private int lastMoveTo = -1;
	private long whenShouldThreadPause;
	private ArrayList<MotionEvent> inputEvents = new ArrayList<MotionEvent>(20);
	private ArrayList<Sprite> legalSquareSprites = new ArrayList<Sprite>();
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	private Bitmap chessboardBitmap;
	private Bitmap lastMoveHighlightBitmap;
	private Bitmap legalMoveHighlightBitmap;
	private Bitmap selectedPieceBitmap;
	private Bitmap[] blackPieceBitmaps = new Bitmap[6];
	private Bitmap[] whitePieceBitmaps = new Bitmap[6];
	private ChessPieceSprite draggedPiece;
	private ChessPieceSprite selectedPiece;
	private DrawingThread thread;
	private Handler handler = new Handler();
	private MoveListener moveListener;
	private Paint alphaPaint = new Paint();
	private Paint rectanglePaint = new Paint();
	private RectangleSprite horizontalRectangle;
	private RectangleSprite verticalRectangle;
	private Sprite selectedPieceSprite;
	private Sprite[] lastMoveHighlights = new Sprite[2];
	private SurfaceHolder surfaceHolder;

	// -------------------------------------------------------------------------------------------------------

	private void addSprite(Sprite sprite) {
		for (int i = 0; i <= sprites.size(); i++) {
			if (i == sprites.size() || sprite.z >= sprites.get(i).z) {
				sprites.add(i, sprite);
				break;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public ChessBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	// -------------------------------------------------------------------------------------------------------

	private void doSecondaryMoveAnimation(Move move, long startTime) {
		if (move.capture != 0) {
			// capture move
			ChessPieceSprite capturedPiece = findChessPieceSprite(move.to % 16, move.to / 16);
			capturedPiece.animations.add(new ScaleAnimation(7, 1, startTime, 200));
			capturedPiece.animations.add(new AlphaAnimation(-255, 255, startTime, 200));
			capturedPiece.animations.add(new RemoveAnimation(startTime, 200));
		} else if ((move.piece == 6 || move.piece == -6) && Math.abs(move.from - move.to) == 2) {
			// catsling move
			int destFile;
			int rookFile;
			int rookRank = move.from / 16;
			if (move.from > move.to) {
				destFile = 3;
				rookFile = 0;
			} else {
				destFile = 5;
				rookFile = 7;
			}
			ChessPieceSprite rook = findChessPieceSprite(rookFile, rookRank);
			rook.file = destFile;
			rook.animations.add(new MoveAnimation(getXFromFile(destFile) - rook.baseX, getYFromRank(rookRank)
					- rook.baseY, rook.baseX, rook.baseY, startTime, 400));
		} else if ((move.piece == 1 || move.piece == -1) && move.capture == 0
				&& move.from % 16 != move.to % 16) {
			// en passant move
			int file = move.to % 16;
			int rank = 0;
			if (move.piece == 1)
				rank = 4;
			else if (move.piece == -1)
				rank = 3;
			ChessPieceSprite capturedPawn = findChessPieceSprite(file, rank);
			capturedPawn.animations.add(new ScaleAnimation(7, 1, startTime, 200));
			capturedPawn.animations.add(new AlphaAnimation(-255, 255, startTime, 200));
			capturedPawn.animations.add(new RemoveAnimation(startTime, 200));
		}

		if ((move.piece == 1 && move.to / 16 == 7) || (move.piece == -1 && move.to / 16 == 0)) {
			// promoting to queen
			ChessPieceSprite promotingPawn = findChessPieceSprite(move.from % 16, move.from / 16);
			if (move.piece == 1)
				promotingPawn.bitmap = whitePieceBitmaps[4];
			else if (move.piece == -1)
				promotingPawn.bitmap = blackPieceBitmaps[4];
		}

		// set last move highlight
		lastMoveHighlights[0].baseX = getXFromFile(move.from % 16);
		lastMoveHighlights[0].baseY = getYFromRank(move.from / 16);
		lastMoveHighlights[0].animations.add(new AlphaAnimation(255, 0, startTime, 100));
		lastMoveHighlights[1].baseX = getXFromFile(move.to % 16);
		lastMoveHighlights[1].baseY = getYFromRank(move.to / 16);
		lastMoveHighlights[1].animations.add(new AlphaAnimation(255, 0, startTime, 100));
	}

	// -------------------------------------------------------------------------------------------------------

	private ChessPieceSprite findChessPieceSprite(int file, int rank) {
		for (int i = 0; i < sprites.size(); i++) {
			if (sprites.get(i) instanceof ChessPieceSprite) {
				ChessPieceSprite chessPiece = (ChessPieceSprite) sprites.get(i);
				if (chessPiece.file == file && chessPiece.rank == rank)
					return chessPiece;
			}
		}
		return null;
	}

	// -------------------------------------------------------------------------------------------------------

	public boolean getBlackEnabled() {
		return blackEnabled;
	}

	// -------------------------------------------------------------------------------------------------------

	private int getFileFromX(int x) {
		if (flipped)
			return 7 - (int) ((x - leftOffset) / squareSize);
		else
			return (int) ((x - leftOffset) / squareSize);
	}

	// -------------------------------------------------------------------------------------------------------

	public boolean getFlipped() {
		return flipped;
	}

	// -------------------------------------------------------------------------------------------------------

	public int getLastMoveFrom() {
		return lastMoveFrom;
	}

	// -------------------------------------------------------------------------------------------------------

	public int getLastMoveTo() {
		return lastMoveTo;
	}

	// -------------------------------------------------------------------------------------------------------

	private int getRankFromY(int y) {
		if (flipped)
			return (int) ((y - topOffset) / squareSize);
		else
			return 7 - (int) ((y - topOffset) / squareSize);
	}

	// -------------------------------------------------------------------------------------------------------

	private float getXFromFile(int file) {
		if (flipped)
			file = 7 - file;
		return leftOffset + file * squareSize + squareSize / 2;
	}

	// -------------------------------------------------------------------------------------------------------

	public boolean getWhiteEnabled() {
		return whiteEnabled;
	}

	// -------------------------------------------------------------------------------------------------------

	private float getYFromRank(int rank) {
		if (flipped)
			rank = 7 - rank;
		return topOffset + (7 - rank) * squareSize + squareSize / 2;
	}

	// -------------------------------------------------------------------------------------------------------

	private void initSprites() {
		sprites.clear();

		// add chessboard sprite
		addSprite(new Sprite(chessboardBitmap, chessboardBitmap.getWidth() / 2,
				chessboardBitmap.getHeight() / 2, Z_ORDER_CHESSBOARD));

		// rectangles
		horizontalRectangle = new RectangleSprite(leftOffset + 4 * squareSize, 0, 8 * squareSize, squareSize,
				3);
		horizontalRectangle.alpha = 0;
		addSprite(horizontalRectangle);
		verticalRectangle = new RectangleSprite(0, topOffset + 4 * squareSize, squareSize, 8 * squareSize, 3);
		verticalRectangle.alpha = 0;
		addSprite(verticalRectangle);

		// selected piece highlight
		selectedPieceSprite = new Sprite(selectedPieceBitmap, 0, 0, 1);

		// last move highlights
		lastMoveHighlights[0] = new Sprite(lastMoveHighlightBitmap, 0, 0, 3);
		lastMoveHighlights[0].alpha = 0;
		addSprite(lastMoveHighlights[0]);
		lastMoveHighlights[1] = new Sprite(lastMoveHighlightBitmap, 0, 0, 3);
		lastMoveHighlights[1].alpha = 0;
		addSprite(lastMoveHighlights[1]);

		// add chess pieces
		int[] board0x88 = board.board0x88;
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if (board0x88[i] > 0)
				addSprite(new ChessPieceSprite(whitePieceBitmaps[board0x88[i] - 1], i % 8, i / 16,
						Z_ORDER_CHESSPIECE));
			else if (board0x88[i] < 0)
				addSprite(new ChessPieceSprite(blackPieceBitmaps[-(board0x88[i] + 1)], i % 8, i / 16,
						Z_ORDER_CHESSPIECE));
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private void inputDown(float x, float y, long currentTime) {
		if (pieceSelected) {
			pieceSelected = false;

			// remove highlight under selected piece
			selectedPieceSprite.animations.clear();
			selectedPieceSprite.animations.add(new RemoveAnimation(currentTime, 100));
			selectedPieceSprite.animations.add(new AlphaAnimation(-selectedPieceSprite.alpha,
					selectedPieceSprite.alpha, currentTime, 100));

			// remove legal moves highlights
			for (int i = 0; i < legalSquareSprites.size(); i++) {
				Sprite sprite = legalSquareSprites.get(i);
				sprite.animations.add(new RemoveAnimation(currentTime, 100));
				sprite.animations.add(new AlphaAnimation(-selectedPieceSprite.alpha,
						selectedPieceSprite.alpha, currentTime, 100));
			}
			legalSquareSprites.clear();

			int file = getFileFromX((int) x);
			int rank = getRankFromY((int) y);

			if (rank >= 0 && rank <= 7 && file >= 0 && file <= 7 && legalSquares[rank * 16 + file]) {
				// move piece
				pieceMoved(selectedPiece.rank * 16 + selectedPiece.file, rank * 16 + file, currentTime + 200);
				selectedPiece.file = file;
				selectedPiece.rank = rank;

				selectedPiece.animations.add(new MoveAnimation(getXFromFile(file) - selectedPiece.baseX,
						getYFromRank(rank) - selectedPiece.baseY, selectedPiece.baseX, selectedPiece.baseY,
						currentTime, 150));
			} else {
				ChessPieceSprite chessPiece = findChessPieceSprite(file, rank);
				if (chessPiece != null) {
					int position = rank * 16 + file;
					if (whiteEnabled && board.board0x88[position] > 0 || blackEnabled
							&& board.board0x88[position] < 0) {
						legalSquares = board.legalMovesMap(position);
						selectPiece(file, rank, currentTime);
					}
				}
			}
		} else {
			int file = getFileFromX((int) x);
			int rank = getRankFromY((int) y);

			ChessPieceSprite chessPiece = findChessPieceSprite(file, rank);
			if (chessPiece != null) {
				int position = rank * 16 + file;
				if (whiteEnabled && board.board0x88[position] > 0 || blackEnabled
						&& board.board0x88[position] < 0) {
					pieceDragged = true;
					legalSquares = board.legalMovesMap(position);

					draggingStartX = x;
					draggingStartY = y;

					draggedPiece = chessPiece;
					setSpriteZOrder(draggedPiece, Z_ORDER_CHESSPIECE + 1);
					draggedPiece.animations.add(new MoveAnimation(x - draggedPiece.baseX, y - 1.5f
							* squareSize - draggedPiece.baseY, draggedPiece.baseX, draggedPiece.baseY,
							currentTime + DRAGGING_DELAY - 350, 100));
					draggedPiece.animations.add(new AlphaAnimation(-100, 255, currentTime + DRAGGING_DELAY
							- 350, 100));
					draggedPiece.animations.add(new ScaleAnimation(1.5f, 1, currentTime + DRAGGING_DELAY
							- 350, 100));
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private void inputMove(MotionEvent event, long currentTime) {
		if (pieceDragged) {
			draggedPiece.draggedX = event.getX() - draggingStartX;
			draggedPiece.draggedY = event.getY() - draggingStartY;
			int rank = getRankFromY((int) event.getY());
			int file = getFileFromX((int) event.getX());
			if (rank != horizontalRectangle.rank || file != verticalRectangle.file) {
				horizontalRectangle.rank = rank;
				verticalRectangle.file = file;
				if (file == draggedPiece.file && rank == draggedPiece.rank) {
					horizontalRectangle.animations.clear();
					horizontalRectangle.animations.add(new AlphaAnimation(-horizontalRectangle.alpha,
							horizontalRectangle.alpha, currentTime, 100));
					verticalRectangle.animations.clear();
					verticalRectangle.animations.add(new AlphaAnimation(-verticalRectangle.alpha,
							verticalRectangle.alpha, currentTime, 100));
				} else {
					if (rank >= 0 && rank <= 7 && file >= 0 && file <= 7 && legalSquares[rank * 16 + file]) {
						horizontalRectangle.setRGB(0, 255, 0);
						verticalRectangle.setRGB(0, 255, 0);
					} else {
						horizontalRectangle.setRGB(255, 0, 0);
						verticalRectangle.setRGB(255, 0, 0);
					}

					if (horizontalRectangle.alpha == 0) {
						horizontalRectangle.baseY = getYFromRank(rank);
						verticalRectangle.baseX = getXFromFile(file);
					}

					horizontalRectangle.animations.clear();
					horizontalRectangle.animations.add(new MoveAnimation(0, getYFromRank(rank)
							- horizontalRectangle.baseY, horizontalRectangle.baseX,
							horizontalRectangle.baseY, currentTime, 50));
					horizontalRectangle.animations.add(new AlphaAnimation(50 - horizontalRectangle.alpha,
							horizontalRectangle.alpha, currentTime, 50));

					verticalRectangle.animations.clear();
					verticalRectangle.animations.add(new MoveAnimation(getXFromFile(file)
							- verticalRectangle.baseX, 0, verticalRectangle.baseX, verticalRectangle.baseY,
							currentTime, 50));
					verticalRectangle.animations.add(new AlphaAnimation(50 - verticalRectangle.alpha,
							verticalRectangle.alpha, currentTime, 50));
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private void inputUp(MotionEvent event, long currentTime) {
		if (pieceDragged) {
			pieceDragged = false;
			draggedPiece.animations.clear();

			int file = getFileFromX((int) event.getX());
			int rank = getRankFromY((int) event.getY());

			float destinationX = getXFromFile(file);
			float destinationY = getYFromRank(rank);

			if (draggedPiece.file == file && draggedPiece.rank == rank
					&& (SystemClock.uptimeMillis() - event.getDownTime()) < DRAGGING_DELAY) {
				draggedPiece.baseX = destinationX;
				draggedPiece.baseY = destinationY;

				selectPiece(file, rank, currentTime);
			} else {
				// hide rectangles
				horizontalRectangle.animations.clear();
				verticalRectangle.animations.clear();
				horizontalRectangle.animations.add(new AlphaAnimation(-horizontalRectangle.alpha,
						horizontalRectangle.alpha, currentTime, 100));
				verticalRectangle.animations.add(new AlphaAnimation(-verticalRectangle.alpha,
						verticalRectangle.alpha, currentTime, 100));
				horizontalRectangle.rank = -1;
				verticalRectangle.file = -1;

				if (rank >= 0 && rank <= 7 && file >= 0 && file <= 7 && legalSquares[rank * 16 + file]) {
					pieceMoved(draggedPiece.rank * 16 + draggedPiece.file, rank * 16 + file,
							currentTime + 100);
					draggedPiece.file = file;
					draggedPiece.rank = rank;
				} else {
					destinationX = getXFromFile(draggedPiece.file);
					destinationY = getYFromRank(draggedPiece.rank);
				}

				draggedPiece.baseX += draggedPiece.draggedX;
				draggedPiece.baseY += draggedPiece.draggedY;

				draggedPiece.animations.add(new MoveAnimation(destinationX - draggedPiece.baseX, destinationY
						- draggedPiece.baseY, draggedPiece.baseX, draggedPiece.baseY, currentTime, 200));
				draggedPiece.animations.add(new ScaleAnimation(1 - draggedPiece.scale, draggedPiece.scale,
						currentTime, 200));
				draggedPiece.animations.add(new AlphaAnimation(55, 200, currentTime, 200));
			}

			draggedPiece.draggedX = 0;
			draggedPiece.draggedY = 0;
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private Bitmap loadScaledBitmap(Resources resources, int resourceId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId, options);
		return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale),
				(int) (bitmap.getHeight() * scale), true);
	}

	// -------------------------------------------------------------------------------------------------------

	public void movePiece(int from, int to) {
		Move move = new Move(board, from, to);
		doSecondaryMoveAnimation(move, System.currentTimeMillis() + 400);
		board.doMove(move);

		ChessPieceSprite chessPiece = findChessPieceSprite(from % 16, from / 16);
		float vectorX = getXFromFile(to % 16) - chessPiece.baseX;
		float vectorY = getYFromRank(to / 16) - chessPiece.baseY;
		chessPiece.animations.add(new MoveAnimation(vectorX, vectorY, chessPiece.baseX, chessPiece.baseY,
				System.currentTimeMillis(), 400));
		chessPiece.file = to % 16;
		chessPiece.rank = to / 16;

		if (thread != null) {
			synchronized (thread) {
				thread.notify();
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public boolean onTouchEvent(MotionEvent event) {
		synchronized (inputEvents) {
			inputEvents.add(MotionEvent.obtain(event));
		}

		whenShouldThreadPause = System.currentTimeMillis() + 1000;
		synchronized (thread) {
			thread.notify();
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------

	private void pieceMoved(int from, int to, long secondaryAnimationTime) {
		final Move move = new Move(board, from, to);
		doSecondaryMoveAnimation(move, secondaryAnimationTime);
		board.doMove(move);
		whiteEnabled = !whiteEnabled;
		blackEnabled = !blackEnabled;
		if (moveListener != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					moveListener.pieceMoved(move);
				}
			});
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private void selectPiece(int file, int rank, long currentTime) {
		selectedPiece = findChessPieceSprite(file, rank);

		selectedPiece.animations.clear();
		selectedPiece.baseX = getXFromFile(file);
		selectedPiece.baseY = getYFromRank(rank);
		selectedPiece.alpha = 255;
		selectedPiece.scale = 1;

		// create sprite under selected piece
		sprites.remove(selectedPieceSprite);
		selectedPieceSprite.baseX = getXFromFile(file);
		selectedPieceSprite.baseY = getYFromRank(rank);
		selectedPieceSprite.alpha = 0;
		selectedPieceSprite.animations.clear();
		selectedPieceSprite.animations.add(new AlphaAnimation(255, 0, currentTime, 100));
		addSprite(selectedPieceSprite);

		// create sprites highlighting possible moves
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if (legalSquares[i]) {
				Sprite legalSquareSprite = new Sprite(legalMoveHighlightBitmap, getXFromFile(i % 16),
						getYFromRank(i / 16), 6);
				legalSquareSprite.alpha = 0;
				legalSquareSprite.animations.add(new AlphaAnimation(200, 0, currentTime, 100));
				addSprite(legalSquareSprite);
				legalSquareSprites.add(legalSquareSprite);
			}
		}

		pieceSelected = true;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setBlackEnabled(boolean enabled) {
		blackEnabled = enabled;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setBoard(Board board) {
		this.board = board;

		if (thread != null) {
			initSprites();
			synchronized (thread) {
				thread.notify();
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	public void setColorToMove(int color) {
		if (color == 1) {
			blackEnabled = false;
			whiteEnabled = true;
		} else if (color == -1) {
			blackEnabled = true;
			whiteEnabled = false;
		}
		board.toMove = color;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setLastMoveFrom(int lastMoveFrom) {
		this.lastMoveFrom = lastMoveFrom;
		if (lastMoveFrom == -1)
			lastMoveHighlights[0].alpha = 0;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setLastMoveTo(int lastMoveTo) {
		this.lastMoveTo = lastMoveTo;
		if (lastMoveTo == -1)
			lastMoveHighlights[1].alpha = 0;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setMoveListener(MoveListener moveListener) {
		this.moveListener = moveListener;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setWhiteEnabled(boolean enabled) {
		whiteEnabled = enabled;
	}

	// -------------------------------------------------------------------------------------------------------

	public void setOnlineGameState(OnlineGameState onlineGameState) {

	}

	// -------------------------------------------------------------------------------------------------------

	public void setSpriteZOrder(Sprite sprite, int z) {
		sprites.remove(sprite);
		sprite.z = z;
		addSprite(sprite);
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.sendStopRequest();

		synchronized (thread) {
			thread.notify();
		}

		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				thread = null;
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		scale = (float) width / CHESSBOARD_WIDTH;
		leftOffset = LEFT_OFFSET * scale;
		topOffset = TOP_OFFSET * scale;
		squareSize = SQUARE_SIZE * scale;

		// load bitmaps
		Resources resources = getResources();
		chessboardBitmap = loadScaledBitmap(resources, R.drawable.chessboard);
		lastMoveHighlightBitmap = loadScaledBitmap(resources, R.drawable.square_frame);
		legalMoveHighlightBitmap = loadScaledBitmap(resources, R.drawable.legal_move_highlight);
		selectedPieceBitmap = loadScaledBitmap(resources, R.drawable.selected_piece);
		blackPieceBitmaps[0] = loadScaledBitmap(resources, R.drawable.bpawn);
		blackPieceBitmaps[1] = loadScaledBitmap(resources, R.drawable.bknight);
		blackPieceBitmaps[2] = loadScaledBitmap(resources, R.drawable.bbishop);
		blackPieceBitmaps[3] = loadScaledBitmap(resources, R.drawable.brook);
		blackPieceBitmaps[4] = loadScaledBitmap(resources, R.drawable.bqueen);
		blackPieceBitmaps[5] = loadScaledBitmap(resources, R.drawable.bking);
		whitePieceBitmaps[0] = loadScaledBitmap(resources, R.drawable.wpawn);
		whitePieceBitmaps[1] = loadScaledBitmap(resources, R.drawable.wknight);
		whitePieceBitmaps[2] = loadScaledBitmap(resources, R.drawable.wbishop);
		whitePieceBitmaps[3] = loadScaledBitmap(resources, R.drawable.wrook);
		whitePieceBitmaps[4] = loadScaledBitmap(resources, R.drawable.wqueen);
		whitePieceBitmaps[5] = loadScaledBitmap(resources, R.drawable.wking);

		// if setBoard has been called
		if (board != null) {
			initSprites();
		}

		thread = new DrawingThread();
		thread.start();
	}

	// -------------------------------------------------------------------------------------------------------

	public void undoMove(Move move) {
		board.undoMove(move);
		blackEnabled = !blackEnabled;
		whiteEnabled = !whiteEnabled;

		Board newBoard = new Board();
		newBoard.fromFEN(board.toFEN());
		setBoard(newBoard);

		if (thread != null) {
			synchronized (thread) {
				thread.notify();
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------

	private class DrawingThread extends Thread {

		private boolean stopRequest = false;

		@Override
		public void run() {
			while (!stopRequest) {
				// process input events
				synchronized (inputEvents) {
					long currentTime = System.currentTimeMillis();
					for (int i = 0; i < inputEvents.size(); i++) {
						MotionEvent event = inputEvents.get(i);
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							inputDown(event.getX(), event.getY(), currentTime);
						} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
							inputMove(event, currentTime);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							inputUp(event, currentTime);
						}
						event.recycle();
					}
					inputEvents.clear();
				}

				boolean noOngoingAnimations = true;
				Canvas canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas();
					long time = System.currentTimeMillis();
					for (int i = sprites.size() - 1; i >= 0; i--) {
						Sprite sprite = sprites.get(i);
						sprite.draw(canvas, time);
						if (sprite.animations.size() > 0)
							noOngoingAnimations = false;
					}
				} finally {
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				}

				if (noOngoingAnimations && System.currentTimeMillis() > whenShouldThreadPause) {
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}

		public void sendStopRequest() {
			stopRequest = true;
		}

	}

	// -------------------------------------------------------------------------------------------------------

	private class Sprite {
		public int z;

		protected float baseX;
		protected float baseY;
		protected float draggedX;
		protected float draggedY;
		protected float height;
		protected float scale = 1;
		protected float width;
		protected int alpha = 255;
		protected ArrayList<Animation> animations = new ArrayList<Animation>();
		protected Bitmap bitmap;

		private PaintFlagsDrawFilter antialiasingOn = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
		private PaintFlagsDrawFilter antialiasingOff = new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);

		public void draw(Canvas canvas, long time) {
			// update animations
			for (int i = 0; i < animations.size(); i++) {
				animations.get(i).update(this, time);
			}

			// remove finished animations
			for (int i = animations.size() - 1; i >= 0; i--) {
				if (animations.get(i).finished)
					animations.remove(i);
			}

			// draw
			canvas.setDrawFilter(antialiasingOff);
			Paint paint = null;
			if (alpha < 255) {
				paint = alphaPaint;
				paint.setAlpha(alpha);
			}
			canvas.save();
			if (scale != 1) {
				canvas.setDrawFilter(antialiasingOn);
				canvas.scale(scale, scale, baseX + draggedX, baseY + draggedY);
			}
			canvas.drawBitmap(bitmap, baseX + draggedX - width / 2, baseY + draggedY - height / 2, paint);
			canvas.restore();
		}

		public Sprite(Bitmap bitmap, float x, float y, int z) {
			this.bitmap = bitmap;
			this.baseX = x;
			this.baseY = y;
			this.z = z;
			height = bitmap.getHeight();
			width = bitmap.getWidth();
		}

		public Sprite(float x, float y, float width, float height, int z) {
			this.baseX = x;
			this.baseY = y;
			this.z = z;
			this.height = height;
			this.width = width;
		}

	}

	private class ChessPieceSprite extends Sprite {

		private int file;
		private int rank;

		public ChessPieceSprite(Bitmap bitmap, int file, int rank, int z) {
			super(bitmap, getXFromFile(file), getYFromRank(rank), z);
			this.file = file;
			this.rank = rank;
		}
	}

	private class RectangleSprite extends Sprite {

		public int file = -1;
		public int rank = -1;

		private int r;
		private int g;
		private int b;

		public void draw(Canvas canvas, long time) {
			// update animations
			for (int i = 0; i < animations.size(); i++) {
				animations.get(i).update(this, time);
			}

			// remove finished animations
			for (int i = animations.size() - 1; i >= 0; i--) {
				if (animations.get(i).finished)
					animations.remove(i);
			}

			// draw
			rectanglePaint.setARGB(alpha, r, g, b);
			canvas.drawRect(baseX - width / 2, baseY - height / 2, baseX + width / 2, baseY + height / 2,
					rectanglePaint);
		}

		public void setRGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public RectangleSprite(float x, float y, float width, float height, int z) {
			super(x, y, width, height, z);
		}

	}

	private abstract class Animation {

		public boolean finished = false;

		protected long startTime;
		protected long length;

		public Animation(long startTime, long length) {
			this.length = length;
			this.startTime = startTime;
		}

		public abstract void update(Sprite sprite, long time);
	}

	private class MoveAnimation extends Animation {

		private float baseX;
		private float baseY;
		private float vectorX;
		private float vectorY;

		public MoveAnimation(float vectorX, float vectorY, float baseX, float baseY, long startTime,
				long length) {
			super(startTime, length);
			this.vectorX = vectorX;
			this.vectorY = vectorY;
			this.baseX = baseX;
			this.baseY = baseY;
		}

		public void update(Sprite sprite, long time) {
			float progress;
			if (time < startTime + length) {
				if (time - startTime < 0)
					progress = 0.0f;
				else
					progress = (time - startTime) / (float) length;
			} else {
				progress = 1.0f;
				finished = true;
			}

			sprite.baseX = vectorX * progress + baseX;
			sprite.baseY = vectorY * progress + baseY;
		}
	}

	private class AlphaAnimation extends Animation {

		private int alphaChange;
		private int baseAlpha;

		public AlphaAnimation(int alphaChange, int baseAlpha, long startTime, long length) {
			super(startTime, length);
			this.alphaChange = alphaChange;
			this.baseAlpha = baseAlpha;
		}

		public void update(Sprite sprite, long time) {
			float progress;
			if (time < startTime + length) {
				if (time - startTime < 0)
					progress = 0.0f;
				else
					progress = (time - startTime) / (float) length;
			} else {
				progress = 1.0f;
				finished = true;
			}

			sprite.alpha = (int) (baseAlpha + alphaChange * progress);
		}
	}

	private class RemoveAnimation extends Animation {

		public RemoveAnimation(long startTime, long length) {
			super(startTime, length);
		}

		public void update(Sprite sprite, long time) {
			if (time > startTime + length) {
				sprites.remove(sprite);
			}
		}
	}

	private class ScaleAnimation extends Animation {

		private float baseScale;
		private float scaleChange;

		public ScaleAnimation(float scaleChange, float baseScale, long startTime, long length) {
			super(startTime, length);
			this.baseScale = baseScale;
			this.scaleChange = scaleChange;
		}

		public void update(Sprite sprite, long time) {
			float progress;
			if (time < startTime + length) {
				if (time - startTime < 0)
					progress = 0.0f;
				else
					progress = (time - startTime) / (float) length;
			} else {
				progress = 1.0f;
				finished = true;
			}

			sprite.scale = baseScale + scaleChange * progress;
		}
	}

}
