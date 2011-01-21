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

package cz.hejl.chesswalk;

public class Move {
    public boolean[] castlingRights = new boolean[4];
    public int capture;
    public int enPassant;
    public int halfmoves;
    public int piece;
    public int from;
    public int to;
    public String info; // TODO: maybe delete this

    private String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };
    private String[] pieceSymbols = { "", "N", "B", "R", "Q", "K" };

    public Move(Board board, int from, int to) {
        this.piece = board.board0x88[from];
        this.capture = board.board0x88[to];
        this.from = from;
        this.to = to;
    }

    // TODO: halfmoves, board
    public Move(int from, int to, int piece, int capture, int enPassant,
            boolean[] castlingRights, String info) {
        this.piece = piece;
        this.capture = capture;
        this.from = from;
        this.to = to;
        this.enPassant = enPassant;
        this.castlingRights = castlingRights;
        this.info = info;
    }

    public String toString() {
        int pieceType = Math.abs(piece);
        if (pieceType == 6) {
            if (to - from == 2)
                return "0-0";
            else if (to - from == -2)
                return "0-0-0";
        }

        StringBuffer sb = new StringBuffer(7);
        sb.append(pieceSymbols[(pieceType - 1)]);
        if (capture != 0) {
            if (pieceType == 1)
                sb.append(fileSymbols[from & 7]);
            sb.append("x");
        }
        sb.append(fileSymbols[to & 7]);
        sb.append((to / 16 + 1));
        if ((pieceType == 1) && (to / 16 == 0 || to / 16 == 7))
            sb.append("=Q");

        return sb.toString();
    }
}
