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
#include <jni.h>

extern "C" jint Java_cz_hejl_chesswalk_OfflineGame_getBestMove(JNIEnv* env, jobject thiz,
		jstring fen, jint depth, jint moveTime) {
	Board* board = new Board;
	const char* fen_chars = env -> GetStringUTFChars(fen, 0);
	board -> fromFen(fen_chars);
	int move = Engine::search(board, depth, moveTime, false);
	delete board;
	env -> ReleaseStringUTFChars(fen, fen_chars);
	return move;
}

