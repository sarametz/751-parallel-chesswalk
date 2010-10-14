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

#include "Board.h"
#include "Engine.h"
#include "Evaluation.h"
#include "Move.h"
#include <iostream>
#include <stdio.h>
using namespace std;

int main() {
	Board* board = new Board;
	board -> fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	board -> fromFen("2kr4/2p3p1/1p5p/p1b4P/P1P1P3/1B3P1R/5r2/1R3K2 w   - 15 40");
	board -> print();

	while (true) {
		char command;
		cin >> command;
		switch (command) {
		case 'm':
			board -> makeMove(Engine::search(board, 10, 1000, true));
			board -> print();
			break;
		case 'n':
			Engine::nullEnabled = !Engine::nullEnabled;
			if (Engine::nullEnabled)
				cout << "Null enabled" << endl;
			else
				cout << "Null disabled" << endl;
			break;
		case '.':
			char fileFrom;
			char fileTo;
			int rankFrom;
			int rankTo;
			cin >> fileFrom >> rankFrom >> fileTo >> rankTo;
			int from = (rankFrom - 1) * 16 + fileFrom - 97;
			int to = (rankTo - 1) * 16 + fileTo - 97;
			int move = Move::createMove(board -> board0x88[from], from, to,
					board -> board0x88[to], ORDINARY_MOVE, 0);
			board -> makeMove(move);
			board -> print();
			break;
		}
	}

	delete board;

	return 0;
}
