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

#include "Engine.h"

bool Engine::nullEnabled = true;
int Engine::bestLine[1000];
int Engine::currentDepth;
int Engine::lastBestLine[MAX_DEPTH + 1];
int Engine::moves[2000];
int Engine::movesSize;
int Engine::moveTime;
int Engine::nodeCounter;
long Engine::startTime;

int Engine::alphaBeta(Board* board, int alpha, int beta, int depth, int ply,
		int bestLineIndex, bool allowNull) {
	// it doesn't matter what to return, this search iteration's result will be ignored
	if (timeInMillis() > startTime + moveTime)
		return 0;
	if (!nullEnabled)
		allowNull = false;
	nodeCounter++;
	int firstMoveIndex = movesSize;

	// check for 50 move draw
	if (board -> movesFifty >= 100)
		return DRAW_VALUE;

	if (depth <= 0) {
		// quiescence mode
		int eval = Evaluation::evaluate(board, moves, movesSize);
		if (eval >= beta)
			return beta;
		if (eval > alpha) {
			alpha = eval;
			bestLine[bestLineIndex] = 0;
		}

		movesSize += board -> genLegalCaptures(moves, firstMoveIndex);
	} else {
		movesSize += board -> genAllLegalMoves(moves, firstMoveIndex);

		if (allowNull) {
			if (!board -> isInCheck(board -> toMove)) {
				board -> nullmoveToggle();
				int eval = -alphaBeta(board, -beta, -beta + 1, depth - 1 - 2,
						ply + 1, bestLineIndex + (MAX_DEPTH + 1), false);
				board -> nullmoveToggle();

				if (eval >= beta) {
					movesSize = firstMoveIndex;
					return beta;
				}
			}
		}
	}

	// there are no legal moves (checkmate, stalemate or no captures while in quies. mode)
	if (movesSize == firstMoveIndex) {
		bestLine[bestLineIndex] = 0;
		return Evaluation::evaluate(board, moves, movesSize);
	}

	sortMoves(firstMoveIndex, movesSize - firstMoveIndex, ply);

	for (int i = firstMoveIndex; i < movesSize; i++) {
		/*printPadding(ply);
		 char notation[10];
		 Move::getNotation(moves[i], notation);
		 printf("%s\n", notation);*/

		board -> makeMove(moves[i]);
		int eval;
		if (i - firstMoveIndex >= 4 && ply >= 3 && !board -> isInCheck(
				board -> toMove) && Move::capture(moves[i]) == 0) {
			eval = -alphaBeta(board, -alpha - 1, -alpha, depth - 2, ply + 1,
					bestLineIndex + (MAX_DEPTH + 1), true);
			if (eval > alpha)
				eval = -alphaBeta(board, -beta, -alpha, depth - 1, ply + 1,
						bestLineIndex + (MAX_DEPTH + 1), true);
		} else
			eval = -alphaBeta(board, -beta, -alpha, depth - 1, ply + 1,
					bestLineIndex + (MAX_DEPTH + 1), true);
		board -> unmakeMove(moves[i]);

		if (eval >= beta) {
			movesSize = firstMoveIndex;
			return beta;
		}

		if (eval > alpha) {
			alpha = eval;

			// update bestLine
			bestLine[bestLineIndex] = moves[i];
			int j;
			for (j = bestLineIndex + 1; j < bestLineIndex + MAX_DEPTH; j++) {
				if (bestLine[j + (MAX_DEPTH + 1) - 1] == 0)
					break;
				bestLine[j] = bestLine[j + (MAX_DEPTH + 1) - 1];
			}
			bestLine[j] = 0;
		}
	}

	movesSize = firstMoveIndex;

	return alpha;
}

void Engine::prepareSortMoves(int fromIndex, int count, int ply) {
	for (int i = fromIndex; i < fromIndex + count; i++) {
		int bestLineMove = lastBestLine[ply];
		if (bestLineMove != 0 && Move::fromIndex(bestLineMove)
				== Move::fromIndex(moves[i]) && Move::toIndex(bestLineMove)
				== Move::toIndex(moves[i]) && Move::pieceMoving(bestLineMove)
				== Move::pieceMoving(moves[i])) {
			moves[i] = Move::setOrderingValue(moves[i], 100);
		} else {
			if (Move::capture(moves[i]) == 0)
				moves[i] = Move::setOrderingValue(moves[i], 0);
			else {
				int capturePrice = SIMPLE_PIECE_PRICES[abs(Move::capture(
						moves[i]))];
				int piecePrice = SIMPLE_PIECE_PRICES[abs(Move::pieceMoving(
						moves[i]))];
				moves[i] = Move::setOrderingValue(moves[i], capturePrice
						- piecePrice + 20);
			}
		}
	}
}

void Engine::printPadding(int howMuch) {
	for (int i = 0; i < howMuch; i++) {
		printf("  ");
	}
}

void Engine::reportBestLine(int eval, int depth) {
	printf("%2d) ", depth);
	for (int i = 0; i < MAX_DEPTH; i++) {
		if (bestLine[i] == 0)
			break;
		if (depth == i)
			printf("| ");
		char notation[10];
		Move::getNotation(bestLine[i], notation);
		printf("%s ", notation);
	}
	printf(": %d (%d nodes)\n", eval, nodeCounter);
}

int Engine::search(Board* board, int depth, int moveTime, bool verbose) {
	movesSize = 0;
	Engine::moveTime = moveTime;
	nodeCounter = 0;
	startTime = timeInMillis();
	for (int i = 0; i < MAX_DEPTH + 1; i++) {
		lastBestLine[i] = 0;
	}

	currentDepth = 1;
	int alpha = -INFINITY;
	int beta = INFINITY;
	while (true) {
		// clear first segment of bestLine
		for (int i = 0; i < MAX_DEPTH + 1; i++) {
			bestLine[i] = 0;
		}
		int eval = alphaBeta(board, alpha, beta, currentDepth, 0, 0, true);

		if (eval <= alpha || eval >= beta) {
			alpha = -INFINITY;
			beta = INFINITY;
			continue;
		}
		alpha = eval - 10;
		beta = eval + 10;

		// if time is up - that means last alphaBeta was probebly terminated prematurely
		if (timeInMillis() > startTime + moveTime)
			break;

		for (int i = 0; i < MAX_DEPTH + 1; i++) {
			lastBestLine[i] = bestLine[i];
		}

		reportBestLine(eval, currentDepth);

		if (timeInMillis() > startTime + moveTime * 0.5)
			break;

		currentDepth++;
		if (currentDepth > depth)
			break;
	}

	printf("!!%d!!\n", timeInMillis() - startTime);

	return lastBestLine[0];
}

void Engine::sortMoves(int fromIndex, int count, int ply) {
	prepareSortMoves(fromIndex, count, ply);
	for (int i = fromIndex; i < fromIndex + count; i++) {
		for (int j = i + 1; j < fromIndex + count; j++) {
			if (Move::orderingValue(moves[j]) > Move::orderingValue(moves[i])) {
				int tempMove = moves[i];
				moves[i] = moves[j];
				moves[j] = tempMove;
			}
		}
	}
}

long long Engine::timeInMillis() {
	struct timeval now;
	gettimeofday(&now, NULL);
	return now.tv_usec / 1000 + now.tv_sec * 1000;
}
