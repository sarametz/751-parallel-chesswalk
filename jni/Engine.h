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
