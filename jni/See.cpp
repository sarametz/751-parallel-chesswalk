#include "See.h"

int See::moveFrom;
int See::moveTo;
int See::wAttackers[16];
int See::bAttackers[16];
int See::wAttackersTotal;
int See::bAttackersTotal;
int See::scores[32];

void See::addHidden(Board* board, int startIndex) {
	int pieceType = ATTACK_ARRAY[moveTo - startIndex + 128];

	switch (pieceType) {
	case ATTACK_KQR:
	case ATTACK_QR:
		addSlider(board, startIndex, DELTA_ARRAY[startIndex - moveTo + 128],
				W_ROOK);
		break;
	case ATTACK_KQBwP:
	case ATTACK_KQBbP:
	case ATTACK_QB:
		addSlider(board, startIndex, DELTA_ARRAY[startIndex - moveTo + 128],
				W_BISHOP);
		break;
	}

}

void See::addSlider(Board* board, int startIndex, int delta, int piece) {
	int square = startIndex + delta;

	while ((square & 0x88) == 0 && board -> board0x88[square] == EMPTY_SQUARE)
		square += delta;

	if ((square & 0x88) == 0 && square != moveFrom) {
		if (board -> board0x88[square] == W_QUEEN || board -> board0x88[square]
				== piece) {
			wAttackers[wAttackersTotal] = square;
			wAttackersTotal++;
		} else if (board -> board0x88[square] == B_QUEEN
				|| board -> board0x88[square] == -piece) {
			bAttackers[bAttackersTotal] = square;
			bAttackersTotal++;
		}

	}
}

int See::see(Board* board, int move) {
	// if this move isn't a capture return 0
	if (Move::capture(move) == 0)
		return 0;

	// initialize global variables
	moveTo = Move::toIndex(move);
	moveFrom = Move::fromIndex(move);
	wAttackersTotal = 0;
	bAttackersTotal = 0;

	int score; // is set after each capture in the sequence
	int captureValue = 0; // will be set to the total gain of the caputure sequence
	int tempAttack;
	int sideToMove;
	int attackedPieceValue; // value of the piece standing on the attacked square after each capture

	// add attacking pawns
	if (((moveTo - 15) & 0x88) == 0 && board -> board0x88[moveTo - 15]
			== W_PAWN && moveFrom != (moveTo - 15)) {
		wAttackers[wAttackersTotal] = (moveTo - 15);
		wAttackersTotal++;
	}
	if (((moveTo - 17) & 0x88) == 0 && board -> board0x88[moveTo - 17]
			== W_PAWN && moveFrom != (moveTo - 17)) {
		wAttackers[wAttackersTotal] = (moveTo - 17);
		wAttackersTotal++;
	}
	if (((moveTo + 15) & 0x88) == 0 && board -> board0x88[moveTo + 15]
			== B_PAWN && moveFrom != (moveTo + 15)) {
		bAttackers[bAttackersTotal] = (moveTo + 15);
		bAttackersTotal++;
	}
	if (((moveTo + 17) & 0x88) == 0 && board -> board0x88[moveTo + 17]
			== B_PAWN && moveFrom != (moveTo + 17)) {
		bAttackers[bAttackersTotal] = (moveTo + 17);
		bAttackersTotal++;
	}

	// add white attacking knights
	for (int i = 0; i < board -> wKnights.count; i++) {
		if (board -> wKnights.pieces[i] != moveFrom) {
			if (ATTACK_ARRAY[moveTo - board -> wKnights.pieces[i] + 128]
					== ATTACK_N) {
				wAttackers[wAttackersTotal] = board -> wKnights.pieces[i];
				wAttackersTotal++;
			}
		}
	}

	// and black knights
	for (int i = 0; i < board -> bKnights.count; i++) {
		if (board -> bKnights.pieces[i] != moveFrom) {
			if (ATTACK_ARRAY[moveTo - board -> bKnights.pieces[i] + 128]
					== ATTACK_N) {
				bAttackers[bAttackersTotal] = board -> bKnights.pieces[i];
				bAttackersTotal++;
			}
		}
	}

	// add attacking kings
	if (board -> wKing.pieces[0] != moveFrom) {
		tempAttack = ATTACK_ARRAY[moveTo - board -> wKing.pieces[0] + 128];
		if (tempAttack == ATTACK_KQR || tempAttack == ATTACK_KQBwP
				|| tempAttack == ATTACK_KQBbP) {
			wAttackers[wAttackersTotal] = board -> wKing.pieces[0];
			wAttackersTotal++;
		}
	}
	if (board -> bKing.pieces[0] != moveFrom) {
		tempAttack = ATTACK_ARRAY[moveTo - board -> bKing.pieces[0] + 128];
		if (tempAttack == ATTACK_KQR || tempAttack == ATTACK_KQBwP
				|| tempAttack == ATTACK_KQBbP) {
			bAttackers[bAttackersTotal] = board -> bKing.pieces[0];
			bAttackersTotal++;
		}
	}

	// add attacking sliders

	// sliders that move diagonally
	addSlider(board, moveTo, 17, W_BISHOP);
	addSlider(board, moveTo, 15, W_BISHOP);
	addSlider(board, moveTo, -15, W_BISHOP);
	addSlider(board, moveTo, -17, W_BISHOP);

	// sliders that move straight
	addSlider(board, moveTo, 16, W_ROOK);
	addSlider(board, moveTo, -16, W_ROOK);
	addSlider(board, moveTo, 1, W_ROOK);
	addSlider(board, moveTo, -1, W_ROOK);

	// all obvious attackers are now added to the arrays

	// Now we start with 'making' the initial move so we can find out if
	// there is a hidden piece behind it, that is a piece that is able
	// to capture on the moveTo square if the inital piece captures
	// We do this to get the inital move out of the way since it will
	// always happen first and should not be ordered

	// Important: Below we don't actually carry out the moves on the board
	// for each capture
	// we simply simulate it by toggling the sideToMove variable and
	// setting attackedPieceValue to the value of the piece that
	// 'captured'
	// any reference to a piece on the moveTo square below is simply the
	// piece there after a simulated capture

	score = PIECE_VALUES[Move::capture(move) + 7];
	attackedPieceValue = PIECE_VALUES[(Move::pieceMoving(move)) + 7];
	sideToMove = board -> toMove * -1;

	addHidden(board, Move::fromIndex(move));

	scores[0] = score;

	int scoresIndex = 1;
	int wAttackersCount = 0;
	int bAttackersCount = 0;
	int lowestValueIndex;
	int lowestValue;
	int tempSwap;

	while (true) {
		if ((sideToMove == WHITE && wAttackersCount == wAttackersTotal)
				|| (sideToMove == BLACK && bAttackersCount == bAttackersTotal)) {
			break;
		}

		scores[scoresIndex] = attackedPieceValue - scores[scoresIndex - 1];
		scoresIndex++;

		if (sideToMove == WHITE) {
			lowestValueIndex = wAttackersCount;
			lowestValue
					= PIECE_VALUES[board -> board0x88[wAttackers[wAttackersCount]]
							+ 7];

			for (int i = wAttackersCount + 1; i < wAttackersTotal; i++) {
				if (PIECE_VALUES[board -> board0x88[wAttackers[i]] + 7]
						< lowestValue) {
					lowestValueIndex = i;
					lowestValue
							= PIECE_VALUES[board -> board0x88[wAttackers[i]]
									+ 7];
				}
			}

			if (lowestValueIndex != wAttackersCount) {
				tempSwap = wAttackers[lowestValueIndex];
				wAttackers[lowestValueIndex] = wAttackers[wAttackersCount];
				wAttackers[wAttackersCount] = tempSwap;
			}

			addHidden(board, wAttackers[wAttackersCount]);

			attackedPieceValue = lowestValue;
			sideToMove = BLACK;

			wAttackersCount++;
		} else {
			lowestValueIndex = bAttackersCount;
			lowestValue
					= PIECE_VALUES[board -> board0x88[bAttackers[bAttackersCount]]
							+ 7];
			for (int i = bAttackersCount + 1; i < bAttackersTotal; i++) {
				if (PIECE_VALUES[board -> board0x88[bAttackers[i]] + 7]
						< lowestValue) {
					lowestValueIndex = i;
					lowestValue
							= PIECE_VALUES[board -> board0x88[bAttackers[i]]
									+ 7];
				}
			}
			if (lowestValueIndex != bAttackersCount) {
				tempSwap = bAttackers[lowestValueIndex];
				bAttackers[lowestValueIndex] = bAttackers[bAttackersCount];
				bAttackers[bAttackersCount] = tempSwap;
			}
			addHidden(board, bAttackers[bAttackersCount]);
			attackedPieceValue = lowestValue;
			sideToMove = WHITE;
			bAttackersCount++;
		}
	}

	while (scoresIndex > 1) {
		scoresIndex--;
		if (scores[scoresIndex - 1] > -scores[scoresIndex]) {
			scores[scoresIndex - 1] = -scores[scoresIndex];
		}
	}

	captureValue = scores[0];

	return (captureValue * 100);
}
