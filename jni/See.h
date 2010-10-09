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
