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

