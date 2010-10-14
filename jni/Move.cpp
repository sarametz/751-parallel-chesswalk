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
#include "Move.h"
#include <string.h>

unsigned long Move::createMove(unsigned long pieceMoving,
		unsigned long fromIndex, unsigned long toIndex, unsigned long capture,
		unsigned long type, unsigned long ordering) {
	return 0 | fromIndex | (toIndex << TO_SHIFT) | ((pieceMoving + 7)
			<< PIECE_SHIFT) | ((capture + 7) << CAPTURE_SHIFT) | (type
			<< TYPE_SHIFT) | (ordering << ORDERING_SHIFT);
}

int Move::capture(unsigned long move) {
	return ((move >> CAPTURE_SHIFT) & PIECE_MASK) - 7;
}

int Move::fromIndex(unsigned long move) {
	return (move & SQUARE_MASK);
}

int Move::moveType(unsigned long move) {
	return ((move >> TYPE_SHIFT) & TYPE_MASK);
}

void Move::getNotation(unsigned long move, char* buf) {
	int piece = pieceMoving(move);
	int from = fromIndex(move);
	int to = toIndex(move);
	int capturePiece = capture(move);
	int type = moveType(move);

	int bufSize = 0;

	// add the piece notation
	switch (piece) {
	case W_KING: {
		if (type == SHORT_CASTLE) {
			strcpy(buf, "0-0");
			return;
		}
		if (type == LONG_CASTLE) {
			strcpy(buf, "0-0-0");
			return;
		}
		buf[bufSize] = 'K';
		bufSize++;
		break;
	}
	case B_KING: {
		if (type == SHORT_CASTLE) {
			strcpy(buf, "0-0");
			return;
		}
		if (type == LONG_CASTLE) {
			strcpy(buf, "0-0-0");
			return;
		}
		buf[bufSize] = 'K';
		bufSize++;
		break;
	}
	case W_QUEEN:
	case B_QUEEN:
		buf[bufSize] = 'Q';
		bufSize++;
		break;
	case W_ROOK:
	case B_ROOK:
		buf[bufSize] = 'R';
		bufSize++;
		break;
	case W_BISHOP:
	case B_BISHOP:
		buf[bufSize] = 'B';
		bufSize++;
		break;
	case W_KNIGHT:
	case B_KNIGHT:
		buf[bufSize] = 'N';
		bufSize++;
		break;
	}

	char abcdefgh[] = "abcdefgh";

	// the move is a capture
	if (capturePiece != 0) {
		if ((piece == W_PAWN) || (piece == B_PAWN)) {
			buf[bufSize] = abcdefgh[from % 16];
			bufSize++;
		}
		buf[bufSize] = 'x';
		bufSize++;
	}

	// find the row
	buf[bufSize] = abcdefgh[to % 16];
	bufSize++;

	// add the rank
	buf[bufSize] = (to / 16 + 1) + 48;
	bufSize++;

	// add promotion
	switch (type) {
	case PROMOTION_QUEEN:
		strncpy(&buf[bufSize], "=Q", 2);
		bufSize += 2;
		break;
	case PROMOTION_ROOK:
		strncpy(&buf[bufSize], "=R", 2);
		bufSize += 2;
		break;
	case PROMOTION_BISHOP:
		strncpy(&buf[bufSize], "=B", 2);
		bufSize += 2;
		break;
	case PROMOTION_KNIGHT:
		strncpy(&buf[bufSize], "=N", 2);
		bufSize += 2;
		break;
	}

	buf[bufSize] = '\0';
}

int Move::toIndex(unsigned long move) {
	return ((move >> TO_SHIFT) & SQUARE_MASK);
}

int Move::orderingValue(unsigned long move) {
	return (move >> ORDERING_SHIFT);
}

int Move::pieceMoving(unsigned long move) {
	return ((move >> PIECE_SHIFT) & PIECE_MASK) - 7;
}

unsigned long Move::setOrderingValue(unsigned long move, unsigned long value) {
	// clear the ordering value
	move = (move & ORDERING_CLEAR);
	// change the ordering value and return the new move integer
	return (move | (value << ORDERING_SHIFT));
}
