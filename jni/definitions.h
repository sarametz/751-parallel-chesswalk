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

#ifndef DEFINITIONS_H_
#define DEFINITIONS_H_

const int A1 = 0;
const int A2 = 16;
const int B1 = 1;
const int B2 = 17;
const int C1 = 2;
const int C2 = 18;
const int D1 = 3;
const int D2 = 19;
const int E1 = 4;
const int E2 = 20;
const int F1 = 5;
const int F2 = 21;
const int G1 = 6;
const int G2 = 22;
const int H1 = 7;
const int H2 = 23;

const int A3 = 32;
const int A4 = 48;
const int B3 = 33;
const int B4 = 49;
const int C3 = 34;
const int C4 = 50;
const int D3 = 35;
const int D4 = 51;
const int E3 = 36;
const int E4 = 52;
const int F3 = 37;
const int F4 = 53;
const int G3 = 38;
const int G4 = 54;
const int H3 = 39;
const int H4 = 55;

const int A5 = 64;
const int A6 = 80;
const int B5 = 65;
const int B6 = 81;
const int C5 = 66;
const int C6 = 82;
const int D5 = 67;
const int D6 = 83;
const int E5 = 68;
const int E6 = 84;
const int F5 = 69;
const int F6 = 85;
const int G5 = 70;
const int G6 = 86;
const int H5 = 71;
const int H6 = 87;

const int A7 = 96;
const int A8 = 112;
const int B7 = 97;
const int B8 = 113;
const int C7 = 98;
const int C8 = 114;
const int D7 = 99;
const int D8 = 115;
const int E7 = 100;
const int E8 = 116;
const int F7 = 101;
const int F8 = 117;
const int G7 = 102;
const int G8 = 118;
const int H7 = 103;
const int H8 = 119;

// move types
const int ORDINARY_MOVE = 0;
const int SHORT_CASTLE = 1;
const int LONG_CASTLE = 2;
const int EN_PASSANT = 3;
const int PROMOTION_QUEEN = 4;
const int PROMOTION_ROOK = 5;
const int PROMOTION_BISHOP = 6;
const int PROMOTION_KNIGHT = 7;

const int WHITE = 1;
const int BLACK = -1;

const int W_KING = 6;
const int W_QUEEN = 5;
const int W_ROOK = 4;
const int W_BISHOP = 3;
const int W_KNIGHT = 2;
const int W_PAWN = 1;

const int B_KING = -6;
const int B_QUEEN = -5;
const int B_ROOK = -4;
const int B_BISHOP = -3;
const int B_KNIGHT = -2;
const int B_PAWN = -1;

const int EMPTY_SQUARE = 0;

// piece deltas
const int BISHOP_DELTA[] = { -15, -17, 15, 17, 0, 0, 0, 0 };
const int ROOK_DELTA[] = { -1, -16, 1, 16, 0, 0, 0, 0 };
const int QUEEN_DELTA[] = { -15, -17, 15, 17, -1, -16, 1, 16 };
const int KING_DELTA[] = { -15, -17, 15, 17, -1, -16, 1, 16 };
const int KNIGHT_DELTA[] = { 18, 33, 31, 14, -31, -33, -18, -14 };
const int PAWN_DELTA[] = { 16, 32, 17, 15, 0, 0, 0, 0 };

const int CASTLE_NONE = 0;
const int CASTLE_SHORT = 1;
const int CASTLE_LONG = 2;
const int CASTLE_BOTH = 3;

const int ATTACK_NONE = 0; // deltas that no piece can move
const int ATTACK_KQR = 1; // one square up down left and right
const int ATTACK_QR = 2; // more than one square up down left and right
const int ATTACK_KQBwP = 3; // one square diagonally up
const int ATTACK_KQBbP = 4; // one square diagonally down
const int ATTACK_QB = 5; // more than one square diagonally
const int ATTACK_N = 6; // knight moves

// usage: ATTACK_ARRAY[attacked_square - attacking_square + 128] = ATTACK_NONE/ATTACK_QB/...
const int ATTACK_ARRAY[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 2,
		0, 0, 0, // 0-19
		0, 0, 0, 5, 0, 0, 5, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, // 20-39
		0, 0, 0, 5, 0, 0, 0, 0, 2, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, // 40-59
		5, 0, 0, 0, 2, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, // 60-79
		2, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 2, 6, 5, 0, // 80-99
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 4, 1, 4, 6, 0, 0, 0, 0, 0, // 100-119
		0, 2, 2, 2, 2, 2, 2, 1, 0, 1, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, // 120-139
		0, 0, 6, 3, 1, 3, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, // 140-159
		2, 6, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 2, 0, 0, 5, // 160-179
		0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 2, 0, 0, 0, 5, 0, 0, 0, // 180-199
		0, 0, 0, 5, 0, 0, 0, 0, 2, 0, 0, 0, 0, 5, 0, 0, 0, 0, 5, 0, // 200-219
		0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, 0, 5, 0, 0, 0, 0, 0, 0, // 220-239
		2, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // 240-256

// usage: similar to ATTACK_ARRAY
const int DELTA_ARRAY[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, -17, 0, 0, 0, 0, 0, 0,
		-16, 0, 0, 0, 0, 0, 0, -15, 0, 0, -17, 0, 0, 0, 0, 0, -16, 0, 0, 0, 0,
		0, -15, 0, 0, 0, 0, -17, 0, 0, 0, 0, -16, 0, 0, 0, 0, -15, 0, 0, 0, 0,
		0, 0, -17, 0, 0, 0, -16, 0, 0, 0, -15, 0, 0, 0, 0, 0, 0, 0, 0, -17, 0,
		0, -16, 0, 0, -15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -17, -33, -16, -31,
		-15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -18, -17, -16, -15, -14, 0, 0, 0,
		0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
		0, 0, 0, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 31,
		16, 33, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 16, 0, 0, 17, 0, 0,
		0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 16, 0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 15,
		0, 0, 0, 0, 16, 0, 0, 0, 0, 17, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 16, 0,
		0, 0, 0, 0, 17, 0, 0, 15, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 17,
		0, 0, 0, 0, 0, 0, 0, 0, 0 };

const int SIMPLE_PIECE_PRICES[] = { 0, 1, 3, 3, 5, 9, 50 };

// evaluation
const int QUEEN_VALUE = 975;
const int ROOK_VALUE = 500;
const int BISHOP_VALUE = 325;
const int KNIGHT_VALUE = 325;
const int PAWN_VALUE = 70;
const int MATE_VALUE = -31999;
const int MATE_BOUND = 31000;
const int DRAW_VALUE = 0;
const int INFINITY = 32000;
const int EVALNOTFOUND = 32001;

// move generation states
const int GEN_HASH = 0;
const int GEN_CAPS = 1;
const int GEN_KILLERS = 2;
const int GEN_NONCAPS = 3;
const int GEN_LOSINGCAPS = 4;
const int GEN_END = 5;

// game phase
const int PHASE_OPENING = 0;
const int PHASE_MIDDLE = 1;
const int PHASE_ENDING = 2;
const int PHASE_PAWN_ENDING = 3; // no null-moves in this phase

// contempt factors
const int CONTEMPT_OPENING = 50;
const int CONTEMPT_MIDDLE = 25;
const int CONTEMPT_ENDING = 0;

// hashtable
const int HASH_EXACT = 0;
const int HASH_ALPHA = 1;
const int HASH_BETA = 2;

#endif /* DEFINITIONS_H_ */
