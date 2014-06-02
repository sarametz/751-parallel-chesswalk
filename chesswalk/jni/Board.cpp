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

#include "Board.h"
#include "Evaluation.h"
#include "Zobrist.h"

Board::Board() {
	// init board0x88 and pieceListIndexes
	for (int i = 0; i < 128; i++) {
		board0x88[i] = 0;
		pieceListIndexes[i] = -1;
	}

	toMove = WHITE;
	enPassant = -1;
	whiteCastle = CASTLE_NONE;
	blackCastle = CASTLE_NONE;
	wPawns.board = this;
	bPawns.board = this;
	wKnights.board = this;
	bKnights.board = this;
	wBishops.board = this;
	bBishops.board = this;
	wRooks.board = this;
	bRooks.board = this;
	wQueens.board = this;
	bQueens.board = this;
	wKing.board = this;
	bKing.board = this;
	zobristKey = 0;
	pawnZobristKey = 0;
}

Board::~Board() {
}

int Board::i2a(int n, char* buf) {
	int i = 0;
	if (n >= 100) {
		buf[i] = (n / 100) + 48;
		i++;
	}
	if (n >= 10) {
		buf[i] = ((n % 100) / 10) + 48;
		i++;
	}
	buf[i] = (n % 10) + 48;
	i++;

	return i;
}

void Board::fromFen(const char* fen) {
	historyIndex = 0;

	for (int i = 0; i < 128; i++)
		board0x88[i] = 0;

	for (int i = 0; i < 128; i++)
		pieceListIndexes[i] = -1;

	wPawns.reset();
	bPawns.reset();
	wKnights.reset();
	bKnights.reset();
	wBishops.reset();
	bBishops.reset();
	wRooks.reset();
	bRooks.reset();
	wQueens.reset();
	bQueens.reset();
	wKing.reset();
	bKing.reset();

	whiteCastle = CASTLE_NONE;
	blackCastle = CASTLE_NONE;

	char currentChar;
	int i = 0;
	int boardIndex = 112;
	int currentStep = 0;

	bool fenFinished = false;
	int fenLenght = strlen(fen);
	while (!fenFinished && i < fenLenght) {
		currentChar = fen[i];
		if (currentChar == ' ') {
			i++;
			currentChar = fen[i];
			currentStep++;
		}

		switch (currentStep) {
		case 0: // pieces
		{
			switch (currentChar) {
			case '/':
				boardIndex -= 24;
				break;
			case 'K':
				board0x88[boardIndex] = W_KING;
				wKing.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'Q':
				board0x88[boardIndex] = W_QUEEN;
				wQueens.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'R':
				board0x88[boardIndex] = W_ROOK;
				wRooks.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'B':
				board0x88[boardIndex] = W_BISHOP;
				wBishops.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'N':
				board0x88[boardIndex] = W_KNIGHT;
				wKnights.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'P':
				board0x88[boardIndex] = W_PAWN;
				wPawns.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'k':
				board0x88[boardIndex] = B_KING;
				bKing.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'q':
				board0x88[boardIndex] = B_QUEEN;
				bQueens.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'r':
				board0x88[boardIndex] = B_ROOK;
				bRooks.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'b':
				board0x88[boardIndex] = B_BISHOP;
				bBishops.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'n':
				board0x88[boardIndex] = B_KNIGHT;
				bKnights.addPiece(boardIndex);
				boardIndex++;
				break;
			case 'p':
				board0x88[boardIndex] = B_PAWN;
				bPawns.addPiece(boardIndex);
				boardIndex++;
				break;
			default:
				boardIndex += (currentChar - 48);
			}
			break;
		}
		case 1: // side to move
		{
			if (currentChar == 'w')
				toMove = WHITE;
			else
				toMove = BLACK;
			break;
		}
		case 2: // castling rights
		{
			switch (currentChar) {
			case 'K':
				whiteCastle = CASTLE_SHORT;
				break;
			case 'Q': {
				if (whiteCastle == CASTLE_SHORT)
					whiteCastle = CASTLE_BOTH;
				else
					whiteCastle = CASTLE_LONG;
				break;
			}
			case 'k':
				blackCastle = CASTLE_SHORT;
				break;
			case 'q': {
				if (blackCastle == CASTLE_SHORT)
					blackCastle = CASTLE_BOTH;
				else
					blackCastle = CASTLE_LONG;
				break;
			}
			}
			break;
		}
		case 3: // en passant
		{
			if (currentChar == '-')
				enPassant = -1;
			else {
				switch (currentChar) {
				case 'a':
					enPassant = 0;
					break;
				case 'b':
					enPassant = 1;
					break;
				case 'c':
					enPassant = 2;
					break;
				case 'd':
					enPassant = 3;
					break;
				case 'e':
					enPassant = 4;
					break;
				case 'f':
					enPassant = 5;
					break;
				case 'g':
					enPassant = 6;
					break;
				case 'h':
					enPassant = 7;
					break;
				}
				i++;
				currentChar = fen[i];

				if (currentChar == '3')
					enPassant += 32;
				else
					enPassant += 80;
			}
			break;
		}
		case 4: // half-moves (50 move rule) and full moves
		{
			char tempStr[4];
			tempStr[0] = fen[i];
			if (fen[i + 1] == ' ') {
				tempStr[1] = '\0';
				movesFifty = atoi(tempStr);
			} else {
				tempStr[1] = fen[i + 1];
				tempStr[2] = '\0';
				movesFifty = atoi(tempStr);
				i++;
			}
			i += 2;
			movesFull = atoi(&fen[i]);
			fenFinished = true;
			break;
		}
		}
		i++;
	}
	zobristKey = Zobrist::getZobristKey(this);
	pawnZobristKey = Zobrist::getPawnZobristKey(this);
}

int Board::genAllLegalMoves(int *moves, int startIndex) {
	int nMoves = 0;

	nMoves = genNonCaptures(moves, startIndex);

	nMoves += genCaptures(moves, nMoves + startIndex);

	// remove moves that leave the king in check
	int totalLegalMoves = 0;
	for (int i = startIndex; i < nMoves + startIndex; i++) {
		int thisMove = moves[i];
		makeMove(thisMove);

		moves[i] = 0;
		if (toMove == WHITE && !isAttacked(bKing.pieces[0], WHITE)) {
			moves[startIndex + totalLegalMoves] = thisMove;
			totalLegalMoves++;
		} else if (toMove == BLACK && !isAttacked(wKing.pieces[0], BLACK)) {
			moves[startIndex + totalLegalMoves] = thisMove;
			totalLegalMoves++;
		}
		unmakeMove(thisMove);
	}

	return totalLegalMoves;
}

int Board::genCaptures(int *moves, int startIndex) {
	int moveIndex = startIndex;
	int from, to;
	int pieceType;

	if (toMove == WHITE) {
		for (int i = 0; i < wPawns.count; i++) {
			from = wPawns.pieces[i];
			// TODO: queen promotions maybe belong here
			to = from + 17; // up right
			if ((to & 0x88) == 0) {
				pieceType = board0x88[to];
				if (pieceType < 0) {
					if (to / 16 == 7) { // promotion
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_QUEEN, 0);
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_ROOK, 0);
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_BISHOP, 0);
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_KNIGHT, 0);
					} else { // ordinary capture
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, ORDINARY_MOVE, 0);
					}
				}
			}

			to = from + 15; // up left
			if ((to & 0x88) == 0) {
				pieceType = board0x88[to];
				if (pieceType < 0) // Black piece
				{
					if (to / 16 == 7) {
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_QUEEN, 0);
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_ROOK, 0);
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_BISHOP, 0);
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, PROMOTION_KNIGHT, 0);
					} else { // ordinary capture
						moves[moveIndex++] = Move::createMove(W_PAWN, from, to,
								pieceType, ORDINARY_MOVE, 0);
					}
				}
			}
		}

		// en passant
		if (enPassant != -1 && enPassant / 16 == 5) {
			from = enPassant - 17;
			if ((from & 0x88) == 0) {
				if (board0x88[from] == W_PAWN) {
					moves[moveIndex++] = Move::createMove(W_PAWN, from,
							enPassant, B_PAWN, EN_PASSANT, 0);

				}
			}

			from = enPassant - 15;
			if ((from & 0x88) == 0) {
				if (board0x88[from] == W_PAWN) {
					moves[moveIndex++] = Move::createMove(W_PAWN, from,
							enPassant, B_PAWN, EN_PASSANT, 0);

				}
			}
		}

		// knights
		for (int i = 0; i < wKnights.count; i++) {
			moveIndex += genCapturesDelta(wKnights.pieces[i], KNIGHT_DELTA, 8,
					false, moves, moveIndex);
		}

		// bishops
		for (int i = 0; i < wBishops.count; i++) {
			moveIndex += genCapturesDelta(wBishops.pieces[i], BISHOP_DELTA, 4,
					true, moves, moveIndex);
		}

		// rooks
		for (int i = 0; i < wRooks.count; i++) {
			moveIndex += genCapturesDelta(wRooks.pieces[i], ROOK_DELTA, 4,
					true, moves, moveIndex);
		}

		// queen
		for (int i = 0; i < wQueens.count; i++) {
			moveIndex += genCapturesDelta(wQueens.pieces[i], QUEEN_DELTA, 8,
					true, moves, moveIndex);
		}

		// king
		moveIndex += genCapturesDelta(wKing.pieces[0], KING_DELTA, 8, false,
				moves, moveIndex);
	} else if (toMove == BLACK) {
		for (int i = 0; i < bPawns.count; i++) {
			from = bPawns.pieces[i];
			to = from - 17; // down right
			if ((to & 0x88) == 0) {
				pieceType = board0x88[to];
				if (pieceType > 0) { // promotion
					if (to / 16 == 0) {
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_QUEEN, 0);
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_ROOK, 0);
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_BISHOP, 0);
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_KNIGHT, 0);
					} else { // ordinary capture
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, ORDINARY_MOVE, 0);
					}
				}
			}

			to = from - 15; // down left
			if ((to & 0x88) == 0) {
				pieceType = board0x88[to];
				if (pieceType > 0) {
					if (to / 16 == 0) {
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_QUEEN, 0);
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_ROOK, 0);
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_BISHOP, 0);
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, PROMOTION_KNIGHT, 0);
					} else { // ordinary capture
						moves[moveIndex++] = Move::createMove(B_PAWN, from, to,
								pieceType, ORDINARY_MOVE, 0);
					}
				}
			}

		}

		// en passant
		if (enPassant != -1 && enPassant / 16 == 2) {
			from = enPassant + 17;
			if ((from & 0x88) == 0) {
				if (board0x88[from] == B_PAWN) {
					moves[moveIndex++] = Move::createMove(B_PAWN, from,
							enPassant, W_PAWN, EN_PASSANT, 0);

				}
			}

			from = enPassant + 15;
			if ((from & 0x88) == 0) {
				if (board0x88[from] == B_PAWN) {
					moves[moveIndex++] = Move::createMove(B_PAWN, from,
							enPassant, W_PAWN, EN_PASSANT, 0);

				}
			}
		}

		// knights
		for (int i = 0; i < bKnights.count; i++) {
			moveIndex += genCapturesDelta(bKnights.pieces[i], KNIGHT_DELTA, 8,
					false, moves, moveIndex);
		}

		// bishops
		for (int i = 0; i < bBishops.count; i++) {
			moveIndex += genCapturesDelta(bBishops.pieces[i], BISHOP_DELTA, 4,
					true, moves, moveIndex);
		}

		// rooks
		for (int i = 0; i < bRooks.count; i++) {
			moveIndex += genCapturesDelta(bRooks.pieces[i], ROOK_DELTA, 4,
					true, moves, moveIndex);
		}

		// queen
		for (int i = 0; i < bQueens.count; i++) {
			moveIndex += genCapturesDelta(bQueens.pieces[i], QUEEN_DELTA, 8,
					true, moves, moveIndex);
		}

		// king
		moveIndex += genCapturesDelta(bKing.pieces[0], KING_DELTA, 8, false,
				moves, moveIndex);
	}

	return moveIndex - startIndex;
}

int Board::genCapturesDelta(int index, const int *delta, int nDelta,
		bool sliding, int *moves, int startIndex) {
	int moveIndex = startIndex;
	for (int i = 0; i < nDelta; i++) {
		int deltaIndex = index;
		deltaIndex += delta[i];

		while ((deltaIndex & 0x88) == 0) {
			if (board0x88[deltaIndex] == EMPTY_SQUARE) {
				if (!sliding)
					break;
			} else if ((board0x88[deltaIndex] * board0x88[index]) < 0) {
				moves[moveIndex++] = Move::createMove(board0x88[index], index,
						deltaIndex, board0x88[deltaIndex], ORDINARY_MOVE, 0);
				break;
			} else {
				break;
			}

			deltaIndex += delta[i];
		}
	}
	return moveIndex - startIndex;
}

int Board::genLegalCaptures(int *moves, int startIndex) {
	int movesCount = genCaptures(moves, startIndex);

	// remove moves that leave the king in check
	int legalMovesCount = 0;
	for (int i = startIndex; i < startIndex + movesCount; i++) {
		int move = moves[i];
		makeMove(move);
		moves[i] = 0;
		if (toMove == WHITE && !isAttacked(bKing.pieces[0], WHITE)) {
			moves[startIndex + legalMovesCount] = move;
			legalMovesCount++;
		} else if (toMove == BLACK && !isAttacked(wKing.pieces[0], BLACK)) {
			moves[startIndex + legalMovesCount] = move;
			legalMovesCount++;
		}
		unmakeMove(move);
	}

	return legalMovesCount;
}

int Board::genNonCaptures(int *moves, int startIndex) {
	int moveIndex = startIndex;
	int from, to;
	int pieceType;

	if (toMove == WHITE) {

		// castle short
		if (whiteCastle == CASTLE_SHORT || whiteCastle == CASTLE_BOTH) {
			// squares between king and rook must be empty
			if ((board0x88[F1] == EMPTY_SQUARE) && (board0x88[G1]
					== EMPTY_SQUARE)) {
				// king and the squares between king and rook can't be attacked
				if (!isAttacked(E1, BLACK) && !isAttacked(F1, BLACK)) {
					moves[moveIndex++] = Move::createMove(W_KING, E1, G1, 0,
							SHORT_CASTLE, 0);
				}
			}
		}

		// castle long
		if (whiteCastle == CASTLE_LONG || whiteCastle == CASTLE_BOTH) {
			if ((board0x88[D1] == EMPTY_SQUARE) && (board0x88[C1]
					== EMPTY_SQUARE) && (board0x88[B1]) == EMPTY_SQUARE) {
				if (!isAttacked(E1, BLACK) && !isAttacked(D1, BLACK)) {
					moves[moveIndex++] = Move::createMove(W_KING, E1, C1, 0,
							LONG_CASTLE, 0);
				}
			}
		}

		// pawns
		for (int i = 0; i < wPawns.count; i++) {
			from = wPawns.pieces[i];
			to = from + 16;
			pieceType = board0x88[to];
			if (pieceType == EMPTY_SQUARE) {
				if (to / 16 == 7) // promotion
				{
					moves[moveIndex++] = Move::createMove(W_PAWN, from, to, 0,
							PROMOTION_QUEEN, 0);
					moves[moveIndex++] = Move::createMove(W_PAWN, from, to, 0,
							PROMOTION_ROOK, 0);
					moves[moveIndex++] = Move::createMove(W_PAWN, from, to, 0,
							PROMOTION_BISHOP, 0);
					moves[moveIndex++] = Move::createMove(W_PAWN, from, to, 0,
							PROMOTION_KNIGHT, 0);
				} else // ordinary move
				{
					moves[moveIndex++] = Move::createMove(W_PAWN, from, to, 0,
							ORDINARY_MOVE, 0);
					if (from / 16 == 1) {
						to += 16;
						if (board0x88[to] == EMPTY_SQUARE) {
							moves[moveIndex++] = Move::createMove(W_PAWN, from,
									to, 0, ORDINARY_MOVE, 0);
						}
					}
				}
			}
		}

		// knights
		for (int i = 0; i < wKnights.count; i++) {
			moveIndex += genNonCapturesDelta(wKnights.pieces[i], KNIGHT_DELTA,
					8, false, moves, moveIndex);
		}

		// bishops
		for (int i = 0; i < wBishops.count; i++) {
			moveIndex += genNonCapturesDelta(wBishops.pieces[i], BISHOP_DELTA,
					4, true, moves, moveIndex);
		}

		// rooks
		for (int i = 0; i < wRooks.count; i++) {
			moveIndex += genNonCapturesDelta(wRooks.pieces[i], ROOK_DELTA, 4,
					true, moves, moveIndex);
		}

		// queen
		for (int i = 0; i < wQueens.count; i++) {
			moveIndex += genNonCapturesDelta(wQueens.pieces[i], QUEEN_DELTA, 8,
					true, moves, moveIndex);
		}

		// king
		moveIndex += genNonCapturesDelta(wKing.pieces[0], KING_DELTA, 8, false,
				moves, moveIndex);

	} else if (toMove == BLACK) {
		// castle short
		if (blackCastle == CASTLE_SHORT || blackCastle == CASTLE_BOTH) {
			// squares between king and rook must be empty
			if ((board0x88[F8] == EMPTY_SQUARE) && (board0x88[G8]
					== EMPTY_SQUARE)) {
				// king and the squares between king and rook can't be attacked
				if (!isAttacked(E8, WHITE) && !isAttacked(F8, WHITE)) {
					moves[moveIndex++] = Move::createMove(B_KING, E8, G8, 0,
							SHORT_CASTLE, 0);
				}
			}
		}

		// castle long
		if (blackCastle == CASTLE_LONG || blackCastle == CASTLE_BOTH) {
			if ((board0x88[D8] == EMPTY_SQUARE) && (board0x88[C8]
					== EMPTY_SQUARE) && (board0x88[B8]) == EMPTY_SQUARE) {
				if (!isAttacked(E8, WHITE) && !isAttacked(D8, WHITE)) {
					moves[moveIndex++] = Move::createMove(B_KING, E8, C8, 0,
							LONG_CASTLE, 0);
				}
			}
		}

		// pawns
		for (int i = 0; i < bPawns.count; i++) {
			from = bPawns.pieces[i];
			to = from - 16;
			pieceType = board0x88[to];
			if (pieceType == EMPTY_SQUARE) {
				if (to / 16 == 0) { // promotion
					moves[moveIndex++] = Move::createMove(B_PAWN, from, to, 0,
							PROMOTION_QUEEN, 0);
					moves[moveIndex++] = Move::createMove(B_PAWN, from, to, 0,
							PROMOTION_ROOK, 0);
					moves[moveIndex++] = Move::createMove(B_PAWN, from, to, 0,
							PROMOTION_BISHOP, 0);
					moves[moveIndex++] = Move::createMove(B_PAWN, from, to, 0,
							PROMOTION_KNIGHT, 0);
				} else { // ordinary move
					moves[moveIndex++] = Move::createMove(B_PAWN, from, to, 0,
							ORDINARY_MOVE, 0);
					if (from / 16 == 6) {
						to -= 16; // Move another square
						if (board0x88[to] == EMPTY_SQUARE) {
							moves[moveIndex++] = Move::createMove(B_PAWN, from,
									to, 0, ORDINARY_MOVE, 0);
						}
					}
				}
			}

		}

		// knights
		for (int i = 0; i < bKnights.count; i++) {
			moveIndex += genNonCapturesDelta(bKnights.pieces[i], KNIGHT_DELTA,
					8, false, moves, moveIndex);
		}

		// bishops
		for (int i = 0; i < bBishops.count; i++) {
			moveIndex += genNonCapturesDelta(bBishops.pieces[i], BISHOP_DELTA,
					4, true, moves, moveIndex);
		}

		// rooks
		for (int i = 0; i < bRooks.count; i++) {
			moveIndex += genNonCapturesDelta(bRooks.pieces[i], ROOK_DELTA, 4,
					true, moves, moveIndex);
		}

		// queen
		for (int i = 0; i < bQueens.count; i++) {
			moveIndex += genNonCapturesDelta(bQueens.pieces[i], QUEEN_DELTA, 8,
					true, moves, moveIndex);
		}

		// king
		moveIndex += genNonCapturesDelta(bKing.pieces[0], KING_DELTA, 8, false,
				moves, moveIndex);
	}

	return moveIndex - startIndex;
}

int Board::genNonCapturesDelta(int index, const int *delta, int nDelta,
		bool sliding, int *moves, int startIndex) {
	int moveIndex = startIndex;
	for (int i = 0; i < nDelta; i++) {
		int deltaIndex = index;
		deltaIndex += delta[i];

		while ((deltaIndex & 0x88) == 0 && board0x88[deltaIndex]
				== EMPTY_SQUARE) {
			moves[moveIndex++] = Move::createMove(board0x88[index], index,
					deltaIndex, 0, ORDINARY_MOVE, 0);
			if (!sliding)
				break;
			deltaIndex += delta[i];
		}
	}
	return moveIndex - startIndex;
}

/* attacked: attacked square index, side: attacking side */
bool Board::isAttacked(int attacked, int side) {
	int pieceAttack;

	if (side == WHITE) {
		// pawns
		if (((attacked - 17) & 0x88) == 0 && board0x88[attacked - 17] == W_PAWN)
			return true;
		if (((attacked - 15) & 0x88) == 0 && board0x88[attacked - 15] == W_PAWN)
			return true;

		// knights
		for (int i = 0; i < wKnights.count; i++) {
			if (ATTACK_ARRAY[attacked - wKnights.pieces[i] + 128] == ATTACK_N)
				return true;
		}

		// bishops
		for (int i = 0; i < wBishops.count; i++) {
			pieceAttack = ATTACK_ARRAY[attacked - wBishops.pieces[i] + 128];
			if (pieceAttack == ATTACK_KQBwP || pieceAttack == ATTACK_KQBbP
					|| pieceAttack == ATTACK_QB) {
				if (traverseDelta(wBishops.pieces[i], attacked))
					return true;
			}
		}

		// rooks
		for (int i = 0; i < wRooks.count; i++) {
			pieceAttack = ATTACK_ARRAY[attacked - wRooks.pieces[i] + 128];
			if (pieceAttack == ATTACK_KQR || pieceAttack == ATTACK_QR) {
				if (traverseDelta(wRooks.pieces[i], attacked))
					return true;
			}
		}

		// queen
		for (int i = 0; i < wQueens.count; i++) {
			pieceAttack = ATTACK_ARRAY[attacked - wQueens.pieces[i] + 128];
			if (pieceAttack != ATTACK_NONE && pieceAttack != ATTACK_N) {
				if (traverseDelta(wQueens.pieces[i], attacked))
					return true;
			}
		}

		// king
		pieceAttack = ATTACK_ARRAY[attacked - wKing.pieces[0] + 128];
		if (pieceAttack == ATTACK_KQBwP || pieceAttack == ATTACK_KQBbP
				|| pieceAttack == ATTACK_KQR) {
			return true;
		}
	} else if (side == BLACK) {
		// pawns
		if (((attacked + 17) & 0x88) == 0 && board0x88[attacked + 17] == B_PAWN)
			return true;
		if (((attacked + 15) & 0x88) == 0 && board0x88[attacked + 15] == B_PAWN)
			return true;

		// knights
		for (int i = 0; i < bKnights.count; i++) {
			if (ATTACK_ARRAY[attacked - bKnights.pieces[i] + 128] == ATTACK_N)
				return true;
		}

		// bishops
		for (int i = 0; i < bBishops.count; i++) {
			pieceAttack = ATTACK_ARRAY[attacked - bBishops.pieces[i] + 128];
			if (pieceAttack == ATTACK_KQBwP || pieceAttack == ATTACK_KQBbP
					|| pieceAttack == ATTACK_QB) {
				if (traverseDelta(bBishops.pieces[i], attacked))
					return true;
			}
		}

		// rooks
		for (int i = 0; i < bRooks.count; i++) {
			pieceAttack = ATTACK_ARRAY[attacked - bRooks.pieces[i] + 128];
			if (pieceAttack == ATTACK_KQR || pieceAttack == ATTACK_QR) {
				if (traverseDelta(bRooks.pieces[i], attacked))
					return true;
			}
		}

		// queen
		for (int i = 0; i < bQueens.count; i++) {
			pieceAttack = ATTACK_ARRAY[attacked - bQueens.pieces[i] + 128];
			if (pieceAttack != ATTACK_NONE && pieceAttack != ATTACK_N) {
				if (traverseDelta(bQueens.pieces[i], attacked))
					return true;
			}
		}

		// king
		pieceAttack = ATTACK_ARRAY[attacked - bKing.pieces[0] + 128];
		if (pieceAttack == ATTACK_KQBwP || pieceAttack == ATTACK_KQBbP
				|| pieceAttack == ATTACK_KQR) {
			return true;
		}
	}

	return false;
}

bool Board::isInCheck(int side) {
	if (side == BLACK && isAttacked(bKing.pieces[0], WHITE))
		return true;
	else if (side == WHITE && isAttacked(wKing.pieces[0], BLACK))
		return true;
	else
		return false;

}

void Board::makeMove(int move) {
	// backup information about the position for use in unmakeMove
	history[historyIndex] = 0;
	if (enPassant != -1) {
		history[historyIndex] = enPassant;
	}
	history[historyIndex] = history[historyIndex] | (whiteCastle << 7)
			| (blackCastle << 9) | (movesFifty << 16);
	captureHistory[historyIndex] = board0x88[Move::toIndex(move)]; // en passant will be handled later
	zobristHistory[historyIndex] = zobristKey;
	pawnZobristHistory[historyIndex] = pawnZobristKey;

	if (enPassant != -1)
		zobristKey ^= Zobrist::enPassant[enPassant];
	zobristKey ^= Zobrist::side;
	zobristKey ^= Zobrist::wCastlingRights[whiteCastle];
	zobristKey ^= Zobrist::bCastlingRights[blackCastle];

	enPassant = -1; // reset en passant (can be set again later)

	toMove *= -1;

	// update piece list index of moving piece
	switch (Move::pieceMoving(move)) {
	case W_PAWN:
		wPawns.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case B_PAWN:
		bPawns.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case W_KNIGHT:
		wKnights.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case B_KNIGHT:
		bKnights.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case W_BISHOP:
		wBishops.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case B_BISHOP:
		bBishops.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case W_ROOK:
		wRooks.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case B_ROOK:
		bRooks.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case W_QUEEN:
		wQueens.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case B_QUEEN:
		bQueens.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case W_KING:
		wKing.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	case B_KING:
		bKing.updateIndex(Move::fromIndex(move), Move::toIndex(move));
		break;
	}

	if (Move::pieceMoving(move) != W_PAWN && Move::pieceMoving(move) != B_PAWN
			&& Move::capture(move) == 0)
		movesFifty++;
	else
		movesFifty = 0;

	if (Move::pieceMoving(move) < 0)
		movesFull++;

	switch (Move::moveType(move)) {
	case ORDINARY_MOVE: {
		// remove and replace the piece in the zobrist key (note: toMove was switched)
		if (toMove == -1) {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::toIndex(
							move)];
			if (Move::pieceMoving(move) == W_PAWN) {
				pawnZobristKey ^= Zobrist::pieces[abs(Move::pieceMoving(move))
						- 1][0][Move::fromIndex(move)];
				pawnZobristKey ^= Zobrist::pieces[abs(Move::pieceMoving(move))
						- 1][0][Move::toIndex(move)];
			}
		} else {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::toIndex(
							move)];
			if (Move::pieceMoving(move) == B_PAWN) {
				pawnZobristKey ^= Zobrist::pieces[abs(Move::pieceMoving(move))
						- 1][1][Move::fromIndex(move)];
				pawnZobristKey ^= Zobrist::pieces[abs(Move::pieceMoving(move))
						- 1][1][Move::toIndex(move)];
			}
		}

		board0x88[Move::toIndex(move)] = board0x88[Move::fromIndex(move)];
		board0x88[Move::fromIndex(move)] = EMPTY_SQUARE;

		// en passant
		if ((Move::pieceMoving(move) == W_PAWN || Move::pieceMoving(move)
				== B_PAWN) && abs(Move::toIndex(move) - Move::fromIndex(move))
				== 32) {
			enPassant = Move::fromIndex(move) + (Move::toIndex(move)
					- Move::fromIndex(move)) / 2;
			break;
		}
		break;
	}

	case SHORT_CASTLE: {
		if (Move::pieceMoving(move) == W_KING) {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::toIndex(
							move)];

			// and the rook
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][0][7];
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][0][5];

			// change the place of the rook in the piece list
			wRooks.updateIndex(H1, F1);

			board0x88[6] = board0x88[4]; // move white king
			board0x88[5] = board0x88[7]; // move white rook
			board0x88[7] = EMPTY_SQUARE; // empty the rook square
			board0x88[4] = EMPTY_SQUARE; // empty the king square

			whiteCastle = CASTLE_NONE;
		} else if (Move::pieceMoving(move) == B_KING) {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::toIndex(
							move)];
			// and the rook, stils W_ROOK since '1' takes care of color
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][1][119];
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][1][117];

			// change the place of the rook in the piece list
			bRooks.updateIndex(H8, F8);

			board0x88[118] = board0x88[116]; // black king
			board0x88[117] = board0x88[119]; // black rook
			board0x88[119] = EMPTY_SQUARE; // empty the rook square
			board0x88[116] = EMPTY_SQUARE; // empty the king square

			blackCastle = CASTLE_NONE;
		}
		break;
	}

	case LONG_CASTLE: {
		if (Move::pieceMoving(move) == W_KING) {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::toIndex(
							move)];
			// and the rook
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][0][0];
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][0][3];

			wRooks.updateIndex(A1, D1);

			board0x88[2] = board0x88[4]; // white king
			board0x88[3] = board0x88[0]; // white rook
			board0x88[0] = EMPTY_SQUARE; // wmpty the rook square
			board0x88[4] = EMPTY_SQUARE; // wmpty the king square

			whiteCastle = CASTLE_NONE;
		} else {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::toIndex(
							move)];
			// and the rook, still W_ROOK since '1' takes care of color
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][1][112];
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][1][115];

			bRooks.updateIndex(A8, D8);

			board0x88[114] = board0x88[116]; // black king
			board0x88[115] = board0x88[112]; // black rook
			board0x88[112] = EMPTY_SQUARE; // empty the rook square
			board0x88[116] = EMPTY_SQUARE; // empty the king square

			blackCastle = CASTLE_NONE;
		}
		break;
	}

	case EN_PASSANT: {
		if (toMove == -1) { // we have switched sides, so that means white is moving
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::toIndex(
							move)];
			pawnZobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::fromIndex(
							move)];
			pawnZobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][0][Move::toIndex(
							move)];

			bPawns.removePiece(Move::toIndex(move) - 16); // remove black pawn from piece list

			captureHistory[historyIndex] = board0x88[Move::toIndex(move) - 16];

			board0x88[Move::toIndex(move) - 16] = EMPTY_SQUARE; // remove black pawn from board

			// black pawn is to be removed
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::toIndex(move)
					- 16];
			pawnZobristKey
					^= Zobrist::pieces[W_PAWN - 1][1][Move::toIndex(move) - 16];

		} else {
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::fromIndex(
							move)];
			zobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::toIndex(
							move)];
			pawnZobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::fromIndex(
							move)];
			pawnZobristKey
					^= Zobrist::pieces[abs(Move::pieceMoving(move)) - 1][1][Move::toIndex(
							move)];

			wPawns.removePiece(Move::toIndex(move) + 16);

			captureHistory[historyIndex] = board0x88[Move::toIndex(move) + 16];

			board0x88[Move::toIndex(move) + 16] = EMPTY_SQUARE;

			// white pawn is to be removed
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::toIndex(move)
					+ 16];
			pawnZobristKey
					^= Zobrist::pieces[W_PAWN - 1][0][Move::toIndex(move) + 16];
		}
		board0x88[Move::toIndex(move)] = board0x88[Move::fromIndex(move)];
		board0x88[Move::fromIndex(move)] = EMPTY_SQUARE;
		break;
	}

		// note: toMove has been switched already
	case PROMOTION_QUEEN: {
		board0x88[Move::toIndex(move)] = W_QUEEN * (-toMove);

		if (toMove == -1) {
			zobristKey ^= Zobrist::pieces[W_QUEEN - 1][0][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(move)];
			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(
					move)];

			wPawns.removePiece(Move::toIndex(move));
			wQueens.addPiece(Move::toIndex(move));
		} else {
			zobristKey ^= Zobrist::pieces[W_QUEEN - 1][1][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(move)];
			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(
					move)];

			bPawns.removePiece(Move::toIndex(move));
			bQueens.addPiece(Move::toIndex(move));
		}
		board0x88[Move::fromIndex(move)] = EMPTY_SQUARE;
		break;
	}
	case PROMOTION_ROOK: {
		board0x88[Move::toIndex(move)] = W_ROOK * (-toMove);

		if (toMove == -1) {
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][0][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(move)];
			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(
					move)];

			wPawns.removePiece(Move::toIndex(move));
			wRooks.addPiece(Move::toIndex(move));
		} else {
			zobristKey ^= Zobrist::pieces[W_ROOK - 1][1][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(move)];
			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(
					move)];

			bPawns.removePiece(Move::toIndex(move));
			bRooks.addPiece(Move::toIndex(move));
		}
		board0x88[Move::fromIndex(move)] = EMPTY_SQUARE;
		break;
	}
	case PROMOTION_BISHOP: {
		board0x88[Move::toIndex(move)] = W_BISHOP * (-toMove);

		if (toMove == -1) {
			zobristKey ^= Zobrist::pieces[W_BISHOP - 1][0][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(move)];

			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(
					move)];

			wPawns.removePiece(Move::toIndex(move));
			wBishops.addPiece(Move::toIndex(move));
		} else {
			zobristKey ^= Zobrist::pieces[W_BISHOP - 1][1][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(move)];

			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(
					move)];

			bPawns.removePiece(Move::toIndex(move));
			bBishops.addPiece(Move::toIndex(move));
		}
		board0x88[Move::fromIndex(move)] = EMPTY_SQUARE;
		break;
	}
	case PROMOTION_KNIGHT: {
		board0x88[Move::toIndex(move)] = W_KNIGHT * (-toMove);

		if (toMove == -1) {
			zobristKey ^= Zobrist::pieces[W_KNIGHT - 1][0][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(move)];
			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][0][Move::fromIndex(
					move)];
			wPawns.removePiece(Move::toIndex(move));
			wKnights.addPiece(Move::toIndex(move));
		} else {
			zobristKey ^= Zobrist::pieces[W_KNIGHT - 1][1][Move::toIndex(move)];
			zobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(move)];
			pawnZobristKey ^= Zobrist::pieces[W_PAWN - 1][1][Move::fromIndex(
					move)];
			bPawns.removePiece(Move::toIndex(move));
			bKnights.addPiece(Move::toIndex(move));
		}
		board0x88[Move::fromIndex(move)] = EMPTY_SQUARE;
		break;
	}

	}

	// Check for castling rights changes
	if (whiteCastle != CASTLE_NONE) {
		if (Move::pieceMoving(move) == W_KING)
			whiteCastle = CASTLE_NONE;
		else if (Move::toIndex(move) == A1 || Move::fromIndex(move) == A1) {
			if (whiteCastle == CASTLE_BOTH || whiteCastle == CASTLE_SHORT)
				whiteCastle = CASTLE_SHORT;
			else
				whiteCastle = CASTLE_NONE;
		} else if (Move::toIndex(move) == H1 || Move::fromIndex(move) == H1) {
			if (whiteCastle == CASTLE_BOTH || whiteCastle == CASTLE_LONG)
				whiteCastle = CASTLE_LONG;
			else
				whiteCastle = CASTLE_NONE;
		}

	}
	if (blackCastle != CASTLE_NONE) {
		if (Move::pieceMoving(move) == B_KING)
			blackCastle = CASTLE_NONE;
		else if (Move::toIndex(move) == A8 || Move::fromIndex(move) == A8) {
			if (blackCastle == CASTLE_BOTH || blackCastle == CASTLE_SHORT)
				blackCastle = CASTLE_SHORT;
			else
				blackCastle = CASTLE_NONE;
		} else if (Move::toIndex(move) == H8 || Move::fromIndex(move) == H8) {
			if (blackCastle == CASTLE_BOTH || blackCastle == CASTLE_LONG)
				blackCastle = CASTLE_LONG;
			else
				blackCastle = CASTLE_NONE;
		}
	}

	if (toMove == -1) {
		if (Move::capture(move) != 0 && Move::moveType(move) != EN_PASSANT) {
			zobristKey
					^= Zobrist::pieces[abs(Move::capture(move)) - 1][1][Move::toIndex(
							move)];

			if (Move::capture(move) == B_PAWN)
				pawnZobristKey
						^= Zobrist::pieces[abs(Move::capture(move)) - 1][1][Move::toIndex(
								move)];
		}

	} else {
		if (Move::capture(move) != 0 && Move::moveType(move) != EN_PASSANT) {
			zobristKey
					^= Zobrist::pieces[abs(Move::capture(move)) - 1][0][Move::toIndex(
							move)];

			if (Move::capture(move) == W_PAWN)
				pawnZobristKey
						^= Zobrist::pieces[abs(Move::capture(move)) - 1][0][Move::toIndex(
								move)];
		}
	}

	// apply the changed castling rights to zobrist key
	zobristKey ^= Zobrist::wCastlingRights[whiteCastle];
	zobristKey ^= Zobrist::bCastlingRights[blackCastle];

	// apply the changed en passant square to zobrist key
	if (enPassant != -1)
		zobristKey ^= Zobrist::enPassant[enPassant];

	historyIndex++;
}

void Board::nullmoveToggle() {
	toMove *= -1;
	zobristKey ^= Zobrist::side;
}

void Board::print() {
	printf("-----------------\n");
	char fen[80];
	toFen(fen);
	printf("%s\n", fen);
	int moves[100];
	printf("Evaluation: %d\n", Evaluation::evaluate(this, moves, 0));
	printf("Halfmoves: %d, fullmoves: %d\n", movesFifty, movesFull);
	printf("-----------------\n");
	char pieces[] = { 'k', 'q', 'r', 'b', 'n', 'p', '.', 'P', 'N', 'B', 'R',
			'Q', 'K' };
	for (int rank = 7; rank >= 0; rank--) {
		printf(" ");
		for (int file = 0; file <= 7; file++) {
			int pieceType = board0x88[file + rank * 16];
			char c = pieces[pieceType + 6];
			printf("%c ", c);
		}
		printf("\n");
	}
	printf("-----------------\n\n");
}

void Board::toFen(char fen[]) {
	int fenSize = 0;
	int index = 112;
	int empties = 0;

	while (index >= 0) {
		if ((index & 0x88) != 0) { // reached end of a rank
			if (empties != 0) {
				fenSize += i2a(empties, &fen[fenSize]);
				empties = 0;
			}
			index -= 24; // jump to the next rank
			if (index >= 0) {
				fen[fenSize] = '/';
				fenSize++;
			}
		} else {// the index is on the real board
			if (board0x88[index] != EMPTY_SQUARE) {
				if (empties != 0) {
					fenSize += i2a(empties, &fen[fenSize]);
				}
				empties = 0;
			}

			switch (board0x88[index]) {
			case W_KING:
				fen[fenSize] = 'K';
				fenSize++;
				break;
			case W_QUEEN:
				fen[fenSize] = 'Q';
				fenSize++;
				break;
			case W_ROOK:
				fen[fenSize] = 'R';
				fenSize++;
				break;
			case W_BISHOP:
				fen[fenSize] = 'B';
				fenSize++;
				break;
			case W_KNIGHT:
				fen[fenSize] = 'N';
				fenSize++;
				break;
			case W_PAWN:
				fen[fenSize] = 'P';
				fenSize++;
				break;
			case B_KING:
				fen[fenSize] = 'k';
				fenSize++;
				break;
			case B_QUEEN:
				fen[fenSize] = 'q';
				fenSize++;
				break;
			case B_ROOK:
				fen[fenSize] = 'r';
				fenSize++;
				break;
			case B_BISHOP:
				fen[fenSize] = 'b';
				fenSize++;
				break;
			case B_KNIGHT:
				fen[fenSize] = 'n';
				fenSize++;
				break;
			case B_PAWN:
				fen[fenSize] = 'p';
				fenSize++;
				break;
			default:
				empties++;
			}
			index++;
		}

	}

	fen[fenSize] = ' ';
	fenSize++;

	// side to move
	if (toMove == WHITE)
		fen[fenSize] = 'w';
	else
		fen[fenSize] = 'b';
	fenSize++;

	fen[fenSize] = ' ';
	fenSize++;

	// castling rights
	if (whiteCastle == CASTLE_NONE && blackCastle == CASTLE_NONE) {
		fen[fenSize] = ' ';
		fenSize++;
	} else {
		switch (whiteCastle) {
		case CASTLE_SHORT:
			fen[fenSize] = 'K';
			fenSize++;
			break;
		case CASTLE_LONG:
			fen[fenSize] = 'Q';
			fenSize++;
			break;
		case CASTLE_BOTH:
			fen[fenSize] = 'K';
			fenSize++;
			fen[fenSize] = 'Q';
			fenSize++;
			break;
		}

		switch (blackCastle) {
		case CASTLE_SHORT:
			fen[fenSize] = 'k';
			fenSize++;
			break;
		case CASTLE_LONG:
			fen[fenSize] = 'q';
			fenSize++;
			break;
		case CASTLE_BOTH:
			fen[fenSize] = 'k';
			fenSize++;
			fen[fenSize] = 'q';
			fenSize++;
			break;
		}
	}

	fen[fenSize] = ' ';
	fenSize++;

	// en passant square
	if (enPassant == -1) {
		fen[fenSize] = '-';
		fenSize++;
	} else {
		switch (enPassant % 16) {
		case 0:
			fen[fenSize] = 'a';
			fenSize++;
			break;
		case 1:
			fen[fenSize] = 'b';
			fenSize++;
			break;
		case 2:
			fen[fenSize] = 'c';
			fenSize++;
			break;
		case 3:
			fen[fenSize] = 'd';
			fenSize++;
			break;
		case 4:
			fen[fenSize] = 'e';
			fenSize++;
			break;
		case 5:
			fen[fenSize] = 'f';
			fenSize++;
			break;
		case 6:
			fen[fenSize] = 'g';
			fenSize++;
			break;
		case 7:
			fen[fenSize] = 'h';
			fenSize++;
			break;
		}
		switch (enPassant / 16) {
		case 2:
			fen[fenSize] = '3';
			fenSize++;
			break;
		case 5:
			fen[fenSize] = '6';
			fenSize++;
			break;
		}
	}

	fen[fenSize] = ' ';
	fenSize++;

	// half moves since last capture / pawn move
	fenSize += i2a(movesFifty, &fen[fenSize]);

	fen[fenSize] = ' ';
	fenSize++;

	fenSize += i2a(movesFull, &fen[fenSize]);

	fen[fenSize] = '\0';
}

bool Board::traverseDelta(int attacker, int attacked) {
	int deltaIndex = attacker;
	int delta = DELTA_ARRAY[attacked - attacker + 128];

	while (true) {
		deltaIndex += delta;
		if (deltaIndex == attacked)
			return true;
		if (board0x88[deltaIndex] != EMPTY_SQUARE)
			return false;
	}
}

void Board::unmakeMove(int move) {
	historyIndex--;

	// use the history to reset known variables
	if (((history[historyIndex]) & 127) == 0) {
		enPassant = -1;
	} else {
		enPassant = ((history[historyIndex]) & 127);
	}
	whiteCastle = ((history[historyIndex] >> 7) & 3);
	blackCastle = ((history[historyIndex] >> 9) & 3);
	movesFifty = ((history[historyIndex] >> 16) & 127);
	zobristKey = zobristHistory[historyIndex];
	pawnZobristKey = pawnZobristHistory[historyIndex];

	// wait with resetting the capture until we know if it is an en passant or not

	toMove *= -1;

	switch (board0x88[Move::toIndex(move)]) {
	case W_PAWN:
		wPawns.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case B_PAWN:
		bPawns.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case W_KNIGHT:
		wKnights.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case B_KNIGHT:
		bKnights.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case W_BISHOP:
		wBishops.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case B_BISHOP:
		bBishops.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case W_ROOK:
		wRooks.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case B_ROOK:
		bRooks.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case W_QUEEN:
		wQueens.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case B_QUEEN:
		bQueens.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case W_KING:
		wKing.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	case B_KING:
		bKing.updateIndex(Move::toIndex(move), Move::fromIndex(move));
		break;
	}

	if (Move::pieceMoving(move) < 0)
		movesFull--;

	switch (Move::moveType(move)) {
	case SHORT_CASTLE:
		if (Move::pieceMoving(move) == W_KING) {
			wRooks.updateIndex(F1, H1);

			board0x88[4] = board0x88[6];
			board0x88[7] = board0x88[5];
			board0x88[5] = EMPTY_SQUARE;
			board0x88[6] = EMPTY_SQUARE;

		} else if (Move::pieceMoving(move) == B_KING) {
			bRooks.updateIndex(F8, H8);

			board0x88[116] = board0x88[118];
			board0x88[119] = board0x88[117];
			board0x88[117] = EMPTY_SQUARE;
			board0x88[118] = EMPTY_SQUARE;

		}
		break;

	case LONG_CASTLE:

		if (Move::pieceMoving(move) == W_KING) {
			wRooks.updateIndex(D1, A1);

			board0x88[4] = board0x88[2];
			board0x88[0] = board0x88[3];
			board0x88[2] = EMPTY_SQUARE;
			board0x88[3] = EMPTY_SQUARE;

		} else if (Move::pieceMoving(move) == B_KING) {
			bRooks.updateIndex(D8, A8);

			board0x88[116] = board0x88[114];
			board0x88[112] = board0x88[115];
			board0x88[114] = EMPTY_SQUARE;
			board0x88[115] = EMPTY_SQUARE;
		}
		break;

	case EN_PASSANT:
		if (toMove == 1) {
			board0x88[Move::toIndex(move) - 16] = B_PAWN;
			bPawns.addPiece(Move::toIndex(move) - 16);
		} else {
			board0x88[Move::toIndex(move) + 16] = W_PAWN;
			wPawns.addPiece(Move::toIndex(move) + 16);
		}

		board0x88[Move::fromIndex(move)] = board0x88[Move::toIndex(move)];
		board0x88[Move::toIndex(move)] = EMPTY_SQUARE;
		break;

	default: {
		board0x88[Move::fromIndex(move)] = Move::pieceMoving(move);
		board0x88[Move::toIndex(move)] = captureHistory[historyIndex];
		// if it was a capture put it back in the piece list
		if (captureHistory[historyIndex] != 0) {
			switch (board0x88[Move::toIndex(move)]) {
			case W_PAWN:
				wPawns.addPiece(Move::toIndex(move));
				break;
			case B_PAWN:
				bPawns.addPiece(Move::toIndex(move));
				break;
			case W_KNIGHT:
				wKnights.addPiece(Move::toIndex(move));
				break;
			case B_KNIGHT:
				bKnights.addPiece(Move::toIndex(move));
				break;
			case W_BISHOP:
				wBishops.addPiece(Move::toIndex(move));
				break;
			case B_BISHOP:
				bBishops.addPiece(Move::toIndex(move));
				break;
			case W_ROOK:
				wRooks.addPiece(Move::toIndex(move));
				break;
			case B_ROOK:
				bRooks.addPiece(Move::toIndex(move));
				break;
			case W_QUEEN:
				wQueens.addPiece(Move::toIndex(move));
				break;
			case B_QUEEN:
				bQueens.addPiece(Move::toIndex(move));
				break;
			}
		}

		// handle promotions
		if (Move::moveType(move) >= PROMOTION_QUEEN) {
			if (toMove == 1) {
				switch (Move::moveType(move)) {
				case PROMOTION_QUEEN:
					wQueens.removePiece(Move::fromIndex(move));
					wPawns.addPiece(Move::fromIndex(move));
					break;
				case PROMOTION_ROOK:
					wRooks.removePiece(Move::fromIndex(move));
					wPawns.addPiece(Move::fromIndex(move));
					break;
				case PROMOTION_BISHOP:
					wBishops.removePiece(Move::fromIndex(move));
					wPawns.addPiece(Move::fromIndex(move));
					break;
				case PROMOTION_KNIGHT:
					wKnights.removePiece(Move::fromIndex(move));
					wPawns.addPiece(Move::fromIndex(move));
					break;
				}

			} else { // black promoted
				switch (Move::moveType(move)) {
				case PROMOTION_QUEEN:
					bQueens.removePiece(Move::fromIndex(move));
					bPawns.addPiece(Move::fromIndex(move));
					break;
				case PROMOTION_ROOK:
					bRooks.removePiece(Move::fromIndex(move));
					bPawns.addPiece(Move::fromIndex(move));
					break;
				case PROMOTION_BISHOP:
					bBishops.removePiece(Move::fromIndex(move));
					bPawns.addPiece(Move::fromIndex(move));
					break;
				case PROMOTION_KNIGHT:
					bKnights.removePiece(Move::fromIndex(move));
					bPawns.addPiece(Move::fromIndex(move));
					break;
				}
			}
		}
		break;
	}
	}
}

PieceList::PieceList() {
	count = 0;
}

void PieceList::addPiece(int boardIndex) {
	board -> pieceListIndexes[boardIndex] = count;
	pieces[count] = boardIndex;
	count++;
}

void PieceList::reset() {
	count = 0;
}

void PieceList::removePiece(int boardIndex) {
	count--;
	int listIndex = board -> pieceListIndexes[boardIndex];
	board -> pieceListIndexes[boardIndex] = -1;
	pieces[listIndex] = pieces[count];
	board -> pieceListIndexes[pieces[count]] = listIndex;
}

void PieceList::updateIndex(int from, int to) {
	int listIndex = board -> pieceListIndexes[from];
	board -> pieceListIndexes[from] = -1;

	if (board -> board0x88[to] != 0) {
		switch (board -> board0x88[to]) {
		case W_PAWN:
			board -> wPawns.removePiece(to);
			break;
		case B_PAWN:
			board -> bPawns.removePiece(to);
			break;
		case W_KNIGHT:
			board -> wKnights.removePiece(to);
			break;
		case B_KNIGHT:
			board -> bKnights.removePiece(to);
			break;
		case W_BISHOP:
			board -> wBishops.removePiece(to);
			break;
		case B_BISHOP:
			board -> bBishops.removePiece(to);
			break;
		case W_ROOK:
			board -> wRooks.removePiece(to);
			break;
		case B_ROOK:
			board -> bRooks.removePiece(to);
			break;
		case W_QUEEN:
			board -> wQueens.removePiece(to);
			break;
		case B_QUEEN:
			board -> bQueens.removePiece(to);
			break;
		}

	}
	board -> pieceListIndexes[to] = listIndex;

	pieces[listIndex] = to;
}
