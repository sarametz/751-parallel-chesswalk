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
