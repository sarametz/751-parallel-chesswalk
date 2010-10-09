package cz.hejl.chesswalk;

import java.util.ArrayList;
import java.util.Iterator;

public class Board {
	public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	public boolean[] castlingRights = new boolean[4];
	public int[] board0x88 = new int[128];
	public int enPassant;
	public int halfmoves = 0;
	public int toMove; // whose turn it is, 1 - white, -1 - black
	public long hash;
	public ArrayList<Long> hashHistory;

	private int moveN;
	private int[] bishopDeltas = { 15, 17, -17, -15 };
	private int[] kingDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };
	private int[] knightDeltas = { 31, 33, 14, 18, -18, -14, -33, -31 };
	private int[] queenDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };
	private int[] rookDeltas = { 1, -16, -1, 16 };
	private Evaluation evaluation;
	private Zobrist zobrist = new Zobrist();

	public Board() {
		evaluation = new Evaluation(this);
		init();
	}

	public void doMove(Move move) {
		if (toMove == 1)
			moveN++;
		toMove *= -1;
		int diff = move.to - move.from;

		// update hlafmoves
		move.halfmoves = halfmoves;
		halfmoves++;
		if (move.piece == 1 || move.piece == -1 || move.capture != 0)
			halfmoves = 0;

		// en passant capture
		if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && board0x88[move.to] == 0) {
			if (move.piece == 1)
				board0x88[move.to - 16] = 0;
			if (move.piece == -1)
				board0x88[move.to + 16] = 0;
		}

		// transfer pieces
		board0x88[move.from] = 0;
		move.capture = board0x88[move.to];
		board0x88[move.to] = move.piece;

		// promoting
		if (move.piece == 1 && move.from / 16 == 6)
			board0x88[move.to] = 5;
		else if (move.piece == -1 && move.from / 16 == 1)
			board0x88[move.to] = -5;

		// castling
		if (move.from == 4 && move.to == 6 && move.piece == 6) {
			board0x88[7] = 0;
			board0x88[5] = 4;
		} else if (move.from == 4 && move.to == 2 && move.piece == 6) {
			board0x88[0] = 0;
			board0x88[3] = 4;
		} else if (move.from == 116 && move.to == 118 && move.piece == -6) {
			board0x88[119] = 0;
			board0x88[117] = -4;
		} else if (move.from == 116 && move.to == 114 && move.piece == -6) {
			board0x88[112] = 0;
			board0x88[115] = -4;
		}

		// set en passant
		move.enPassant = enPassant;
		if ((move.piece == 1 || move.piece == -1) && Math.abs(diff) == 32)
			enPassant = move.from + diff / 2;
		else
			enPassant = -1;

		// handle castling rights
		for (int i = 0; i < 4; i++)
			move.castlingRights[i] = castlingRights[i]; // copy current rights

		if (board0x88[0] != 4)
			castlingRights[1] = false; // white queenside
		if (board0x88[7] != 4)
			castlingRights[0] = false; // white kingside
		if (board0x88[4] != 6) { // white both
			castlingRights[0] = false;
			castlingRights[1] = false;
		}
		if (board0x88[112] != -4)
			castlingRights[3] = false; // black queenside
		if (board0x88[119] != -4)
			castlingRights[2] = false; // black kingside
		if (board0x88[116] != -6) { // black both
			castlingRights[2] = false;
			castlingRights[3] = false;
		}

		// update hash and hashHistory
		zobrist.doMove(this, move);
		hashHistory.add(hash);
	}

	public int evaluate() {
		return evaluation.evaluate();
	}

	public void fromFEN(String FEN) {
		String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };
		String[] symbols = { "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };
		int[] pieces = { 1, 2, 3, 4, 5, 6, -1, -2, -3, -4, -5, -6 };

		int pos = -1;
		int file = 0;
		int rank = 7;
		String c = "";
		while (true) {
			pos++;
			c = FEN.substring(pos, pos + 1);
			if (c.equals(" "))
				break;

			// handle piece symbol
			int pieceType = 0;
			for (int i = 0; i < symbols.length; i++) {
				if (c.equals(symbols[i])) {
					pieceType = pieces[i];
					break;
				}
			}
			if (pieceType != 0) {
				board0x88[rank * 16 + file] = pieceType;
				file++;
				continue;
			}

			// handle slash
			if (c.equals("/")) {
				file = 0;
				rank--;
				continue;
			}

			// handle number
			int n = Integer.parseInt(c);
			for (int i = 0; i < n; i++) {
				board0x88[rank * 16 + file] = 0;
				file++;
			}
		}

		// read color to move
		pos++;
		c = FEN.substring(pos, pos + 1);
		pos++;
		if (c.equals("w"))
			toMove = 1;
		else if (c.equals("b"))
			toMove = -1;

		// castling rights
		for (int i = 0; i < 4; i++)
			castlingRights[i] = false;
		while (true) {
			pos++;
			c = FEN.substring(pos, pos + 1);
			if (c.equals(" "))
				break;
			else if (c.equals("K"))
				castlingRights[0] = true;
			else if (c.equals("Q"))
				castlingRights[1] = true;
			else if (c.equals("k"))
				castlingRights[2] = true;
			else if (c.equals("q"))
				castlingRights[3] = true;
		}

		// en passant
		pos++;
		c = FEN.substring(pos, pos + 1);
		if (c.equals("-"))
			enPassant = -1;
		else {
			int index;
			for (index = 0; index < fileSymbols.length; index++) {
				if (fileSymbols[index].equals(c))
					break;
			}
			file = index;
			pos++;
			rank = Integer.parseInt(FEN.substring(pos, pos + 1)) - 1;
			enPassant = rank * 16 + file;
		}
		pos++;

		// halfmoves
		int startPos = pos + 1;
		while (true) {
			pos++;
			c = FEN.substring(pos, pos + 1);
			if (c.equals(" "))
				break;
		}
		halfmoves = Integer.parseInt(FEN.substring(startPos, pos));

		// move number
		startPos = pos + 1;
		while (true) {
			pos++;
			if (pos == FEN.length())
				break;
			c = FEN.substring(pos, pos + 1);
		}
		moveN = Integer.parseInt(FEN.substring(startPos, pos));

		// generate hash
		zobrist.setHash(this);

		// reset hashHistory
		hashHistory = new ArrayList<Long>();
		hashHistory.add(hash);
	}

	public ArrayList<Move> generateAllMoves() {
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			generateMoves(board0x88[i], i, moves);
		}

		removeIllegalMoves(moves);

		return moves;
	}

	private void generateMoves(int piece, int from, ArrayList<Move> moves) {
		if (toMove * piece < 0)
			return;
		int pieceType = Math.abs(piece);
		// pawns
		if (pieceType == 1) {
			generatePawnMoves(piece, from, moves);
		}
		// knight or king
		else if (pieceType == 2 || pieceType == 6) {
			generateNonSlidingMoves(piece, from, moves);
		}
		// bishop, rook or queen
		else if (pieceType >= 3 && pieceType <= 5) {
			generateSlidingMoves(piece, from, moves);
		}
	}

	private void generateNonSlidingMoves(int piece, int from, ArrayList<Move> moves) {
		int[] deltas;
		if (piece == 2 || piece == -2)
			deltas = knightDeltas;
		else
			deltas = kingDeltas;

		for (int i = 0; i < deltas.length; i++) {
			int to = from + deltas[i];
			if ((to & 0x88) != 0)
				continue;
			if (board0x88[to] > 0 && piece > 0)
				continue;
			if (board0x88[to] < 0 && piece < 0)
				continue;
			moves.add(new Move(this, from, to));
		}

		// castling moves
		if (piece == 6) {
			if (castlingRights[0]) {
				if (board0x88[5] == 0 && board0x88[6] == 0) {
					if (squareAttacked(4, -1) == false && squareAttacked(5, -1) == false && squareAttacked(6, -1) == false) {
						moves.add(new Move(this, from, from + 2));
					}
				}
			}
			if (castlingRights[1]) {
				if (board0x88[1] == 0 && board0x88[2] == 0 && board0x88[3] == 0) {
					if (squareAttacked(2, -1) == false && squareAttacked(3, -1) == false && squareAttacked(4, -1) == false) {
						moves.add(new Move(this, from, from - 2));
					}
				}
			}
		} else if (piece == -6) {
			if (castlingRights[2]) {
				if (board0x88[117] == 0 && board0x88[118] == 0) {
					if (squareAttacked(116, 1) == false && squareAttacked(117, 1) == false && squareAttacked(118, 1) == false) {
						moves.add(new Move(this, from, from + 2));
					}
				}
			}
			if (castlingRights[3]) {
				if (board0x88[113] == 0 && board0x88[114] == 0 && board0x88[115] == 0) {
					if (squareAttacked(114, 1) == false && squareAttacked(115, 1) == false && squareAttacked(116, 1) == false) {
						moves.add(new Move(this, from, from - 2));
					}
				}
			}
		}
	}

	private void generateSlidingMoves(int piece, int from, ArrayList<Move> moves) {
		int[] deltas;
		if (piece == 3 || piece == -3)
			deltas = bishopDeltas;
		else if (piece == 4 || piece == -4)
			deltas = rookDeltas;
		else
			deltas = queenDeltas;

		for (int i = 0; i < deltas.length; i++) {
			int delta = deltas[i];
			int to = from;
			while (true) {
				to += delta;
				if ((to & 0x88) != 0)
					break;
				if (board0x88[to] > 0 && piece > 0 || board0x88[to] < 0 && piece < 0)
					break;
				if (board0x88[to] > 0 && piece < 0 || board0x88[to] < 0 && piece > 0) {
					moves.add(new Move(this, from, to));
					break;
				}
				moves.add(new Move(this, from, to));
			}
		}
	}

	private void generatePawnMoves(int piece, int from, ArrayList<Move> moves) {
		// white pawns
		if (piece == 1) {
			// normal move (1 squar ahead)
			if (board0x88[from + 16] == 0)
				moves.add(new Move(this, from, from + 16));
			// 2 squares ahead
			if (from / 16 == 1 && board0x88[from + 16] == 0 && board0x88[from + 2 * 16] == 0)
				moves.add(new Move(this, from, from + 2 * 16));
			// capturing moves
			if (board0x88[from + 15] < 0 && ((from + 15) & 0x88) == 0)
				moves.add(new Move(this, from, from + 15));
			if (board0x88[from + 17] < 0 && ((from + 17) & 0x88) == 0)
				moves.add(new Move(this, from, from + 17));
			// en passant capture
			if (enPassant != -1 && enPassant / 16 == 5) {
				if (from + 15 == enPassant || from + 17 == enPassant)
					moves.add(new Move(this, from, enPassant));
			}
		}

		// black pawns
		else if (piece == -1) {
			// normal move (1 square ahead)
			if (board0x88[from - 16] == 0)
				moves.add(new Move(this, from, from - 16));
			// 2 squares ahead
			if (from / 16 == 6 && board0x88[from - 16] == 0 && board0x88[from - 2 * 16] == 0)
				moves.add(new Move(this, from, from - 2 * 16));
			// capturing moves
			if (((from - 15) & 0x88) == 0 && board0x88[from - 15] > 0)
				moves.add(new Move(this, from, from - 15));
			if (((from - 17) & 0x88) == 0 && board0x88[from - 17] > 0)
				moves.add(new Move(this, from, from - 17));
			// en passant capture
			if (enPassant != -1 && enPassant / 16 == 2) {
				if (from - 15 == enPassant || from - 17 == enPassant)
					moves.add(new Move(this, from, enPassant));
			}
		}
	}

	/** color can be 1 for white or -1 for black */
	public boolean inCheck(int color) {
		// find king
		int king = -1;
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			if (board0x88[i] == 6 * color) {
				king = i;
				break;
			}
		}

		return squareAttacked(king, color * -1);
	}

	public boolean isCheckmate() {
		if (generateAllMoves().size() == 0 && inCheck(toMove)) {
			return true;
		} else
			return false;
	}

	public boolean isDraw50Move() {
		if (halfmoves >= 100)
			return true;
		else
			return false;
	}

	public boolean isEndgame() {
		boolean wQueen = false;
		boolean bQueen = false;
		int wRooks = 0;
		int bRooks = 0;
		int wMinors = 0;
		int bMinors = 0;

		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			int piece = board0x88[i];
			switch (piece) {
			case 5:
				wQueen = true;
			case -5:
				bQueen = true;
			case 4:
				wRooks++;
			case -4:
				bRooks++;
			case 3:
				wMinors--;
			case -3:
				bMinors--;
			case 2:
				wMinors--;
			case -2:
				bMinors--;
			}
		}

		boolean endgame = true;
		if (wQueen && (wMinors > 1 || wRooks > 0))
			endgame = false;
		if (bQueen && (bMinors > 1 || bRooks > 0))
			endgame = false;

		return endgame;
	}

	public boolean isRepetition() {
		int hits = 1;
		for (int i = hashHistory.size() - 2; i >= 0; i--) {
			if (hashHistory.get(i) == hash)
				hits++;
		}

		if (hits >= 3) {
			return true;
		} else
			return false;
	}

	public boolean isStalemate() {
		if (generateAllMoves().size() == 0 && !inCheck(toMove)) {
			return true;
		} else
			return false;
	}

	public void init() {
		fromFEN(STARTING_FEN);

		// en passant
		enPassant = -1;

		// castling rights
		for (int i = 0; i < 4; i++)
			castlingRights[i] = true;

		toMove = 1;

		moveN = 0;
	}

	public boolean[] legalMovesMap(int from) {
		ArrayList<Move> moves = new ArrayList<Move>();
		generateMoves(board0x88[from], from, moves);
		removeIllegalMoves(moves);

		boolean[] map = new boolean[128];
		for (int i = 0; i < moves.size(); i++) {
			map[moves.get(i).to] = true;
		}

		return map;
	}

	private void removeIllegalMoves(ArrayList<Move> moves) {
		Iterator<Move> iter = moves.iterator();
		while (iter.hasNext()) {
			int color = toMove;
			Move move = (Move) iter.next();
			doMove(move);
			boolean illegalMove = false;
			if (inCheck(color))
				illegalMove = true;
			undoMove(move);
			if (illegalMove)
				iter.remove();
		}
	}

	/**
	 * by - if caller is asking whether the square is attacked by white(1) or
	 * black(-1)
	 */
	private boolean squareAttacked(int square, int by) {
		// check for attacking knight
		for (int i = 0; i < knightDeltas.length; i++) {
			int square2 = square + knightDeltas[i];
			if ((square2 & 0x88) == 0 && board0x88[square2] == 2 * by)
				return true;
		}

		// check for attacking pawns
		if (by == -1
				&& (validSquare(square + 15) && board0x88[square + 15] == -1 || validSquare(square + 17)
						&& board0x88[square + 17] == -1))
			return true;
		else if (by == 1
				&& (validSquare(square - 15) && board0x88[square - 15] == 1 || validSquare(square - 17) && board0x88[square - 17] == 1))
			return true;

		// check 8 rays
		for (int i = 0; i < queenDeltas.length; i++) {
			int delta = queenDeltas[i];

			// check for neighbouring king
			if (((square + delta) & 0x88) == 0 && board0x88[square + delta] == 6 * by)
				return true;

			int square2 = square;
			while (true) {
				square2 += delta;
				if ((square2 & 0x88) != 0)
					break; // out of board
				if (board0x88[square2] == 0)
					continue; // empty square
				if (board0x88[square2] * by < 0)
					break; // piece of friendly color
				if (board0x88[square2] * by > 0 && Math.abs(board0x88[square2]) >= 3 && Math.abs(board0x88[square2]) <= 5) {
					if (Math.abs(board0x88[square2]) == 5)
						return true;
					if (i <= 3 && Math.abs(board0x88[square2]) == 4)
						return true;
					if (i >= 4 && Math.abs(board0x88[square2]) == 3)
						return true;
				}
				break; // non-sliding opponent's piece encountered
			}
		}

		return false;
	}

	public String toFEN() {
		String[] symbols = { "", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };
		String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };

		StringBuffer FEN = new StringBuffer(100);
		for (int i = 7; i >= 0; i--) {
			int counter = 0;
			for (int j = 0; j <= 7; j++) {
				if (board0x88[i * 16 + j] == 0) {
					counter++;
					continue;
				} else {
					if (counter > 0)
						FEN.append(Integer.toString(counter));
					counter = 0;
					int index = -1;
					if (board0x88[i * 16 + j] < 0)
						index = board0x88[i * 16 + j] * -1 + 6;
					else if (board0x88[i * 16 + j] > 0)
						index = board0x88[i * 16 + j];
					FEN.append(symbols[index]);
				}
			}
			if (counter > 0)
				FEN.append(Integer.toString(counter));
			if (i != 0)
				FEN.append("/");
		}

		// white or black
		if (toMove == 1)
			FEN.append(" w ");
		else
			FEN.append(" b ");

		// castling rights
		String[] castlingSymbols = { "K", "Q", "k", "q" };
		boolean noCastling = true;
		for (int i = 0; i < 4; i++) {
			if (castlingRights[i]) {
				FEN.append(castlingSymbols[i]);
				noCastling = false;
			}
		}
		if (noCastling)
			FEN.append("-");
		FEN.append(" ");

		// en passant
		if (enPassant == -1)
			FEN.append("- ");
		else
			FEN.append(fileSymbols[enPassant & 7] + Integer.toString(enPassant / 16 + 1) + " ");

		// halfmoves
		FEN.append(Integer.toString(halfmoves) + " ");

		// move number
		FEN.append(Integer.toString(moveN));

		return FEN.toString();
	}

	public void undoMove(Move move) {
		// update hash and hashHistory
		zobrist.undoMove(this, move);
		hashHistory.remove(hashHistory.size() - 1);

		if (toMove == -1)
			moveN--;
		toMove *= -1;

		// take back promoting
		if (move.piece == 1 && board0x88[move.to] != 1)
			board0x88[move.to] = 1;
		else if (move.piece == -1 && board0x88[move.to] != -1)
			board0x88[move.to] = -1;

		// transfer pieces
		board0x88[move.from] = move.piece;
		board0x88[move.to] = move.capture;

		// take back castling
		if (move.from == 4 && move.to == 6 && move.piece == 6) {
			board0x88[7] = 4;
			board0x88[5] = 0;
		} else if (move.from == 4 && move.to == 2 && move.piece == 6) {
			board0x88[0] = 4;
			board0x88[3] = 0;
		} else if (move.from == 116 && move.to == 118 && move.piece == -6) {
			board0x88[119] = -4;
			board0x88[117] = 0;
		} else if (move.from == 116 && move.to == 114 && move.piece == -6) {
			board0x88[112] = -4;
			board0x88[115] = 0;
		}

		// restore halfmoves
		halfmoves = move.halfmoves;

		// restore en passant
		enPassant = move.enPassant;

		// take back en passant
		if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && move.capture == 0) {
			if (move.piece == 1)
				board0x88[move.to - 16] = -1;
			if (move.piece == -1)
				board0x88[move.to + 16] = 1;
		}

		// restore castlig rights
		for (int i = 0; i < 4; i++)
			castlingRights[i] = move.castlingRights[i];
	}

	private boolean validSquare(int square) {
		if ((square & 0x88) == 0)
			return true;
		else
			return false;
	}
}