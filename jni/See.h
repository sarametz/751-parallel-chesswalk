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

#ifndef SEE_H_
#define SEE_H_

#include "definitions.h"
#include "Board.h"
#include "Move.h"

const int PIECE_VALUES[] = { 0, 1, 3, 3, 5, 9, 99, 0, 99, 9, 5, 3, 3, 1 };

class See {
public:
	static int see(Board* inputBoard, int move);
private:
	static int moveFrom;
	static int moveTo;
	static int wAttackers[16];
	static int bAttackers[16];
	static int wAttackersTotal;
	static int bAttackersTotal;
	static int scores[32];

	static void addHidden(Board* board, int startIndex);
	static void addSlider(Board* board, int startIndex, int delta, int piece);
};

#endif /* SEE_H_ */
