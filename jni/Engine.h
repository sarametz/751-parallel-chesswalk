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

#ifndef ENGINE_H_
#define ENGINE_H_

#include "definitions.h"
#include "Board.h"
#include "Evaluation.h"
#include "Move.h"
#include <sys/time.h>

const int MAX_DEPTH = 30;

class Engine {
public:
	static bool nullEnabled;

	static int search(Board* board, int depth, int moveTime, bool verbose);
private:
	static int bestLine[1000];
	static int currentDepth;
	static int lastBestLine[MAX_DEPTH + 1];
	static int moves[2000];
	static int movesSize;
	static int moveTime;
	static int nodeCounter;
	static long startTime;

	static int alphaBeta(Board* board, int alpha, int beta, int depth, int ply,
			int bestLineIndex, bool allowNull);
	static void prepareSortMoves(int fromIndex, int count, int ply);
	static void printPadding(int howMuch);
	static void reportBestLine(int eval, int depth);
	static void sortMoves(int fromIndex, int count, int ply);
	static long long timeInMillis();
};

#endif /* ENGINE_H_ */
