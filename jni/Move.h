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

#ifndef MOVE_H_
#define MOVE_H_

const unsigned long TO_SHIFT = 7;
const unsigned long PIECE_SHIFT = 14;
const unsigned long CAPTURE_SHIFT = 18;
const unsigned long TYPE_SHIFT = 22;
const unsigned long ORDERING_CLEAR = 0x1FFFFFF;
const unsigned long ORDERING_SHIFT = 25;

const int SQUARE_MASK = 127; // 7 bits
const int PIECE_MASK = 15; // 4 bits
const int TYPE_MASK = 7; // 3 bits

class Move {
public:
	static int capture(unsigned long move);
	static unsigned long createMove(unsigned long pieceMoving,
			unsigned long fromIndex, unsigned long toIndex,
			unsigned long capture, unsigned long type, unsigned long ordering);
	static int fromIndex(unsigned long move);
	static void getNotation(unsigned long move, char* buf);
	static int toIndex(unsigned long move);
	static int moveType(unsigned long move);
	static int orderingValue(unsigned long move);
	static int pieceMoving(unsigned long move);
	static unsigned long setOrderingValue(unsigned long move,
			unsigned long value);
};

#endif /* MOVE_H_ */
