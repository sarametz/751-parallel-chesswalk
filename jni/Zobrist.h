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

#ifndef ZOBRIST_H_
#define ZOBRIST_H_

#include <stdlib.h>
#include "definitions.h"
#include "Board.h"

class Zobrist {
public:
	static unsigned long long pieces[6][2][120]; // [ abs(piece type) ][side to move][square]
	static unsigned long long wCastlingRights[4];
	static unsigned long long bCastlingRights[4];
	static unsigned long long enPassant[120];
	static unsigned long long side;

	static unsigned long long getPawnZobristKey(Board* board);
	static unsigned long long getZobristKey(Board* board);
	static void init();
};

#endif /* ZOBRIST_H_ */
