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

public class Evaluation {
	private static final int CHECKMATED = -100000;
	private static final int CONTEMPT_FACTOR = -50;
	private static final int[] PIECE_PRICES = { 0, 100, 320, 330, 500, 900, 20000 };
	private static final int[] BLACK_KING_ENDGAME = new int[128];
	private static final int[] WHITE_KING_ENDGAME = { -50, -40, -30, -20, -20, -30, -40, -50, 0, 0, 0, 0, 0, 0, 0, 0, -30, -20, -10,
			0, 0, -10, -20, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -10, 20, 30, 30, 20, -10, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -10, 30, 40,
			40, 30, -10, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -10, 30, 40, 40, 30, -10, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -10, 20, 30,
			30, 20, -10, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -30, 0, 0, 0, 0, -30, -30, 0, 0, 0, 0, 0, 0, 0, 0, -50, -30, -30, -30, -30,
			-30, -30, -50, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static final int[][] BLACK_TABLES = new int[7][128];
	private static final int[][] WHITE_TABLES = {
			{},
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, -20, -20, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, -5, -10, 0,
					0, -10, -5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, -5, -10, 0, 0, -10, -5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 20, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 10, 25, 25, 10, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 10, 10, 20, 30, 30, 20, 10, 10,
					0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0 },
			{ -50, -40, -30, -30, -30, -30, -40, -50, 0, 0, 0, 0, 0, 0, 0, 0, -40, -20, 0, 5, 5, 0, -20, -40, 0, 0, 0, 0, 0, 0, 0, 0,
					-30, 5, 10, 15, 15, 10, 5, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, 0, 15, 20, 20, 15, 0, -30, 0, 0, 0, 0, 0, 0, 0, 0,
					-30, 5, 15, 20, 20, 15, 5, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, 0, 10, 15, 15, 10, 0, -30, 0, 0, 0, 0, 0, 0, 0, 0,
					-40, -20, 0, 0, 0, 0, -20, -40, 0, 0, 0, 0, 0, 0, 0, 0, -50, -40, -30, -30, -30, -30, -40, -50, 0, 0, 0, 0, 0, 0,
					0, 0 },
			{ -20, -10, -10, -10, -10, -10, -10, -20, 0, 0, 0, 0, 0, 0, 0, 0, -10, 5, 0, 0, 0, 0, 5, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10,
					10, 10, 10, 10, 10, 10, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 10, 10, 10, 10, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10,
					5, 5, 10, 10, 5, 5, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 5, 10, 10, 5, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 0,
					0, 0, 0, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -20, -10, -10, -10, -10, -10, -10, -20, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0,
					-5, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0,
					0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, 10, 10, 10, 10, 5, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ -20, -10, -10, -5, -5, -10, -10, -20, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 5, 0, 0, 0, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10,
					5, 5, 5, 5, 5, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, -5, 0, 5, 5, 5, 5,
					0, -5, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 5, 5, 5, 5, 0, -10, 0, 0, 0, 0, 0, 0, 0, 0, -10, 0, 0, 0, 0, 0, 0, -10, 0,
					0, 0, 0, 0, 0, 0, 0, -20, -10, -10, -5, -5, -10, -10, -20, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 20, 30, 10, 0, 0, 10, 30, 20, 0, 0, 0, 0, 0, 0, 0, 0, 20, 20, 0, 0, 0, 0, 20, 20, 0, 0, 0, 0, 0, 0, 0, 0, -10, -20, -20,
					-20, -20, -20, -20, -10, 0, 0, 0, 0, 0, 0, 0, 0, -20, -30, -30, -40, -40, -30, -30, -20, 0, 0, 0, 0, 0, 0, 0, 0,
					-30, -40, -40, -50, -50, -40, -40, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -40, -40, -50, -50, -40, -40, -30, 0, 0, 0,
					0, 0, 0, 0, 0, -30, -40, -40, -50, -50, -40, -40, -30, 0, 0, 0, 0, 0, 0, 0, 0, -30, -40, -40, -50, -50, -40, -40,
					-30, 0, 0, 0, 0, 0, 0, 0, 0 } };

	private Board board;

	// -----------------------------------------------------------------------------------------------------------

	public int evaluateSpecialCases(int eval) {
		// mate or stalemate
		if (board.generateAllMoves().size() == 0) {
			if (board.inCheck(board.toMove))
				return CHECKMATED;
			else
				return CONTEMPT_FACTOR;
		}

		// check for 3 fold repetition
		int hits = 1;
		for (int i = board.hashHistory.size() - 2; i >= 0; i--) {
			if (board.hashHistory.get(i) == board.hash)
				hits++;
		}
		if (hits >= 3) {
			return CONTEMPT_FACTOR;
		}

		// check 50 move rule
		if (board.halfmoves >= 100) {
			return CONTEMPT_FACTOR;
		}

		return eval;
	}

	// -----------------------------------------------------------------------------------------------------------

	public Evaluation(Board board) {
		this.board = board;

		// init BLACK_TABLES
		for (int i = 1; i <= 6; i++) {
			for (int j = 0; j < 128; j++) {
				if ((j & 0x88) != 0)
					continue;
				BLACK_TABLES[i][119 - j] = WHITE_TABLES[i][j];
			}
		}
		for (int j = 0; j < 128; j++) {
			if ((j & 0x88) != 0)
				continue;
			BLACK_KING_ENDGAME[119 - j] = WHITE_KING_ENDGAME[j];
		}
	}

	// -----------------------------------------------------------------------------------------------------------

	public int evaluate() {
		// endgame detection
		boolean isEndgame = board.isEndgame();

		int[] board0x88 = board.board0x88;
		int eval = 0;
		for (int i = 0; i < 128; i++) {
			if ((i & 0x88) != 0)
				continue;
			int pieceType = Math.abs(board0x88[i]);
			if (board0x88[i] >= 0) {
				eval += PIECE_PRICES[pieceType];
				if (pieceType != 0 && pieceType != 6)
					eval += WHITE_TABLES[pieceType][i];
				else if (pieceType == 6) {
					if (isEndgame)
						eval += WHITE_KING_ENDGAME[i];
					else
						eval += WHITE_TABLES[6][i];
				}
			} else {
				eval -= PIECE_PRICES[pieceType];
				if (pieceType != 0 && pieceType != 6)
					eval -= BLACK_TABLES[pieceType][i];
				else if (pieceType == 6) {
					if (isEndgame)
						eval -= BLACK_KING_ENDGAME[i];
					else
						eval -= BLACK_TABLES[6][i];
				}
			}
		}

		eval = eval * board.toMove;

		return evaluateSpecialCases(eval);
	}

}
