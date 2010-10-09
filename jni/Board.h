#ifndef BOARD_H_
#define BOARD_H_

#include "Move.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

class Board;

class PieceList {
public:
	Board *board;
	int count;
	int pieces[10];

	PieceList();
	void addPiece(int boardIndex);
	void removePiece(int boardIndex);
	void reset();
	void updateIndex(int from, int to);
};

class Board {
public:

	int board0x88[128];
	int captureHistory[4096];
	int history[4096];
	int pieceListIndexes[128];
	int enPassant;
	int historyIndex;
	int movesFifty;
	int movesFull;
	int toMove;

	unsigned long long pawnZobristKey;
	unsigned long long zobristKey;

	int blackCastle;
	int whiteCastle;

	unsigned long long pawnZobristHistory[4096];
	unsigned long long zobristHistory[4096];

	PieceList wPawns;
	PieceList bPawns;
	PieceList wKnights;
	PieceList bKnights;
	PieceList wBishops;
	PieceList bBishops;
	PieceList wRooks;
	PieceList bRooks;
	PieceList wQueens;
	PieceList bQueens;
	PieceList wKing;
	PieceList bKing;

	Board();
	virtual ~Board();
	int i2a(int n, char* buf);
	void fromFen(const char* fen);
	int genAllLegalMoves(int *moves, int startIndex);
	int genCaptures(int *moves, int startIndex);
	int genCapturesDelta(int index, const int *delta, int nDelta,
			bool sliding, int *moves, int startIndex);
	int genLegalCaptures(int *moves, int startIndex);
	int genNonCaptures(int *moves, int startIndex);
	int genNonCapturesDelta(int index, const int *delta, int nDelta,
			bool sliding, int *moves, int startIndex);
	bool isAttacked(int attacked, int side);
	bool isInCheck(int side);
	void makeMove(int move);
	void nullmoveToggle();
	void print();
	void toFen(char* fen);
	bool traverseDelta(int attacker, int attacked);
	void unmakeMove(int move);
};

#endif /* BOARD_H_ */
