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

#include "definitions.h"
#include "Evaluation.h"

int Evaluation::evaluate(Board* board, int* moves, int movesSize) {
	int movesCount = board -> genAllLegalMoves(moves, movesSize);
	if (movesCount == 0) {
		if (board -> isInCheck(board -> toMove))
			return MATE_VALUE; // checkmate
		else
			return 0; // stalemate
	}

	int gamePhase = getGamePhase(board);

	int eval = material(board, WHITE) - material(board, BLACK);

	bool useEndingTables = true;
	if (gamePhase <= PHASE_OPENING)
		useEndingTables = false;

	// pawns
	for (int i = 0; i < board -> wPawns.count; i++) {
		if (useEndingTables)
			eval += W_PAWN_POS_ENDING[board -> wPawns.pieces[i]];
		else
			eval += W_PAWN_POS[board -> wPawns.pieces[i]];
	}
	for (int i = 0; i < board -> bPawns.count; i++) {
		if (useEndingTables)
			eval -= B_PAWN_POS_ENDING[board -> bPawns.pieces[i]];
		else
			eval -= B_PAWN_POS[board -> bPawns.pieces[i]];
	}

	// knights
	for (int i = 0; i < board -> wKnights.count; i++) {
		if (useEndingTables)
			eval += KNIGHT_POS_ENDING[board -> wKnights.pieces[i]];
		else
			eval += W_KNIGHT_POS[board -> wKnights.pieces[i]];
	}
	for (int i = 0; i < board -> bKnights.count; i++) {
		if (useEndingTables)
			eval -= KNIGHT_POS_ENDING[board -> bKnights.pieces[i]];
		else
			eval -= B_KNIGHT_POS[board -> bKnights.pieces[i]];
	}

	// bishops
	for (int i = 0; i < board -> wBishops.count; i++) {
		if (useEndingTables)
			eval += BISHOP_POS_ENDING[board -> wBishops.pieces[i]];
		else
			eval += W_BISHOP_POS[board -> wBishops.pieces[i]];
	}
	for (int i = 0; i < board -> bBishops.count; i++) {
		if (useEndingTables)
			eval -= BISHOP_POS_ENDING[board -> bBishops.pieces[i]];
		else
			eval -= B_BISHOP_POS[board -> bBishops.pieces[i]];
	}

	// rooks
	for (int i = 0; i < board -> wRooks.count; i++) {
		if (useEndingTables)
			eval += ROOK_POS_ENDING[board -> wRooks.pieces[i]];
		else
			eval += W_ROOK_POS[board -> wRooks.pieces[i]];
	}
	for (int i = 0; i < board -> bRooks.count; i++) {
		if (useEndingTables)
			eval -= ROOK_POS_ENDING[board -> bRooks.pieces[i]];
		else
			eval -= B_ROOK_POS[board -> bRooks.pieces[i]];
	}

	// queens
	for (int i = 0; i < board -> wQueens.count; i++) {
		if (useEndingTables)
			eval += QUEEN_POS_ENDING[board -> wQueens.pieces[i]];
		else
			eval += W_QUEEN_POS[board -> wQueens.pieces[i]];
	}
	for (int i = 0; i < board -> bQueens.count; i++) {
		if (useEndingTables)
			eval -= QUEEN_POS_ENDING[board -> bQueens.pieces[i]];
		else
			eval -= B_QUEEN_POS[board -> bQueens.pieces[i]];
	}

	// kings
	if (useEndingTables) {
		eval += KING_POS_ENDING[board -> wKing.pieces[0]];
		eval -= KING_POS_ENDING[board -> bKing.pieces[0]];
	} else {
		eval += W_KING_POS[board -> wKing.pieces[0]];
		eval -= B_KING_POS[board -> bKing.pieces[0]];
	}

	return eval * board -> toMove;
}

int Evaluation::getGamePhase(Board* board) {
	int phase = PHASE_OPENING;
	int gamePhaseCheck = 0;

	gamePhaseCheck += board -> wKnights.count;
	gamePhaseCheck += board -> bKnights.count;
	gamePhaseCheck += board -> wBishops.count;
	gamePhaseCheck += board -> bBishops.count;
	gamePhaseCheck += board -> wRooks.count * 2;
	gamePhaseCheck += board -> bRooks.count * 2;
	gamePhaseCheck += board -> wQueens.count * 4;
	gamePhaseCheck += board -> bQueens.count * 4;

	if (gamePhaseCheck == 0)
		phase = PHASE_PAWN_ENDING;
	else if (gamePhaseCheck <= 8)
		phase = PHASE_ENDING;
	else if (gamePhaseCheck > 20)
		phase = PHASE_OPENING;
	else
		phase = PHASE_MIDDLE;

	return phase;
}

int Evaluation::material(Board* board, int side) {
	int material = 0;
	if (side == WHITE) {
		material += board -> wPawns.count * PAWN_VALUE;
		material += board -> wRooks.count * ROOK_VALUE;
		material += board -> wQueens.count * QUEEN_VALUE;
		material += board -> wBishops.count * BISHOP_VALUE;
		material += board -> wKnights.count * KNIGHT_VALUE;
	} else {
		material += board -> bPawns.count * PAWN_VALUE;
		material += board -> bRooks.count * ROOK_VALUE;
		material += board -> bQueens.count * QUEEN_VALUE;
		material += board -> bBishops.count * BISHOP_VALUE;
		material += board -> bKnights.count * KNIGHT_VALUE;
	}

	return material;
}
