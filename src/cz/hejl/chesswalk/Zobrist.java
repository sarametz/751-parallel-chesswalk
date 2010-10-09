package cz.hejl.chesswalk;

import java.util.Random;

public class Zobrist {
	private long keyBlack;
	private long[] castlingKeys = new long[4];
	private long[] enPassantKeys = new long[8];
	private long[] keys = new long[64 * 12];

	public void doMove(Board board, Move move) {
		long hash = board.hash;

		// change sides
		hash ^= keyBlack;

		// transfer piece
		int from64 = (move.from / 16) * 8 + (move.from & 7);
		hash ^= keys[from64 * 12 + pieceTypeToIndex(move.piece)];
		int to64 = (move.to / 16) * 8 + (move.to & 7);
		if (move.capture != 0)
			hash ^= keys[to64 * 12 + pieceTypeToIndex(move.capture)];
		hash ^= keys[to64 * 12 + pieceTypeToIndex(move.piece)];

		// castling rights
		for (int i = 0; i < 4; i++) {
			if (move.castlingRights[i] != board.castlingRights[i])
				hash ^= castlingKeys[i];
		}

		// enPassant
		if (move.enPassant != -1)
			hash ^= enPassantKeys[move.enPassant & 7];
		if (board.enPassant != -1)
			hash ^= enPassantKeys[board.enPassant & 7];

		board.hash = hash;
	}

	private int pieceTypeToIndex(int pieceType) {
		if (pieceType > 0)
			return pieceType - 1;
		else
			return pieceType * -1 + 5;
	}

	public void setHash(Board board) {
		long hash = 0;

		for (int i = 0; i < 64; i++) {
			int piece = board.board0x88[(i / 8) * 16 + (i % 8)];
			if (piece != 0) {
				int pieceIndex = pieceTypeToIndex(piece);
				hash ^= keys[i * 12 + pieceIndex];
			}
		}

		for (int i = 0; i < 4; i++) {
			if (board.castlingRights[i])
				hash ^= castlingKeys[i];
		}

		if (board.enPassant != -1)
			hash ^= enPassantKeys[board.enPassant & 7];

		if (board.toMove == -1)
			hash ^= keyBlack;

		board.hash = hash;
	}

	public void undoMove(Board board, Move move) {
		long hash = board.hash;

		// change sides
		hash ^= keyBlack;

		// transfer piece
		int to64 = (move.to / 16) * 8 + (move.to & 7);
		hash ^= keys[to64 * 12 + pieceTypeToIndex(move.piece)];
		if (move.capture != 0)
			hash ^= keys[to64 * 12 + pieceTypeToIndex(move.capture)];
		int from64 = (move.from / 16) * 8 + (move.from & 7);
		hash ^= keys[from64 * 12 + pieceTypeToIndex(move.piece)];

		// castling rights
		for (int i = 0; i < 4; i++) {
			if (move.castlingRights[i] != board.castlingRights[i])
				hash ^= castlingKeys[i];
		}

		// enPassant
		if (board.enPassant != -1)
			hash ^= enPassantKeys[board.enPassant & 7];
		if (move.enPassant != -1)
			hash ^= enPassantKeys[move.enPassant & 7];

		board.hash = hash;
	}

	public Zobrist() {
		Random r = new Random();
		for (int i = 0; i < keys.length; i++) {
			keys[i] = r.nextLong();
		}
		for (int i = 0; i < 4; i++) {
			castlingKeys[i] = r.nextLong();
		}
		for (int i = 0; i < 8; i++) {
			enPassantKeys[i] = r.nextLong();
		}
		keyBlack = r.nextLong();
	}

}
