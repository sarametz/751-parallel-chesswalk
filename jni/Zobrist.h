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
