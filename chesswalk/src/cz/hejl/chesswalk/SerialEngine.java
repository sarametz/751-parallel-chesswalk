/*
 * Copyright (C) 2010 Franti��ek Hejl
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;

public class SerialEngine {
    public int nodeCounter = 0;
    public Board board = new Board();

    private static final int WINDOW = 10;
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900,
            1000 };
    private static final String TAG = "Engine";

    private boolean allowNullGlobal = true;
    public int bestLineDepth;
    private int bestLineEval;
    private int bestMoveTimeLimit;
    private int currentDepth;
    private long bestMoveStart;
    private ArrayList<Move> bestLine;
    private MoveComparator moveComparator = new MoveComparator();
    private Move[] primaryKillers = new Move[50];
    private Move[] secondaryKillers = new Move[50];

    // -----------------------------------------------------------------------------------------------------------

    private int alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line,
            boolean root, boolean allowNull) {
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit
                && !root)
            return 1234567890;// ***
        if (allowNullGlobal == false)
            allowNull = false;
        nodeCounter++;
        int initialAlpha = alpha;

        int initialLineSize = line.size();
        ArrayList<Move> locLine = new ArrayList<Move>();
        ArrayList<Move> moves = null;
        moves = board.generateAllMoves();
        moveComparator.ply = currentDepth - depth + 1;
        Collections.sort(moves, moveComparator);

        if (depth <= 0) {
            int eval = board.evaluate();
            if (eval >= beta)
                return beta;
            if (eval > alpha)
                alpha = eval;

            int capturesN = 0;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).capture == 0)
                    break;
                capturesN++;
            }
            moves.subList(capturesN, moves.size()).clear();
        }

        if (moves.size() == 0)
            return board.evaluate();

        if (allowNull && depth > 0) {
            if (!board.inCheck(board.toMove)) {
                board.toMove *= -1;
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine,
                        false, false);
                board.toMove *= -1;
                if (eval == -1234567890)
                    return 1234567890; // ***

                if (eval >= beta) {
                    return beta;
                }
            }
        }

        for (int i = 0; i < moves.size(); i++) {
            locLine.clear();
            int eval;

            board.doMove(moves.get(i));
            if (board.isRepetition())
                eval = -50;
            else if (board.isDraw50Move())
                eval = -50;
            else {
                if (i >= 4 && currentDepth - depth >= 2
                        && !board.inCheck(board.toMove)
                        && moves.get(i).capture == 0) {
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine,
                            false, true);
                    if (eval > alpha)
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine,
                                false, true);
                } else
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false,
                            true);
            }
            board.undoMove(moves.get(i));
            if (eval == -1234567890)
                return 1234567890; // ***

            if (eval >= beta) {
                // update killers
                if (primaryKillers[currentDepth - depth] != null)
                    secondaryKillers[currentDepth - depth] = primaryKillers[currentDepth
                            - depth];
                primaryKillers[currentDepth - depth] = moves.get(i);

                return beta;
            }

            if (eval > alpha) {
                alpha = eval;
                line.subList(initialLineSize, line.size()).clear();
                line.add(moves.get(i));
                line.addAll(locLine);
            }

            // set best line
            if (root
                    && (eval > bestLineEval || eval == bestLineEval
                            && depth > bestLineDepth)
                    && initialAlpha == -1000000) {
                updateBestLine(line, depth, eval);
            }
        }

        if (root && alpha > initialAlpha) {
            updateBestLine(line, depth, alpha);
        }

        return alpha;
    }

    // -----------------------------------------------------------------------------------------------------------

    public Move bestMove(int depth, int time) {
        return bestMove(depth, time, false);
    }

    public Move bestMove(int depth, int time, boolean verbose) {
        nodeCounter = 0;
        bestMoveTimeLimit = time;

        int eval = 0;
        bestLine = new ArrayList<Move>();
        bestLineDepth = 0;
        bestLineEval = -100000;
        bestMoveStart = System.currentTimeMillis();
        currentDepth = 1;
        int alpha = -1000000;
        int beta = 1000000;
        while (true) {
            // don't run alphabeta if only 1 posible move
            if (currentDepth == 1) {
                ArrayList<Move> moves = board.generateAllMoves();
                if (moves.size() == 1) {
                    bestLine = new ArrayList<Move>();
                    bestLine.add(moves.get(0));
                    break;
                }
            }

            eval = alphaBeta(currentDepth, alpha, beta, new ArrayList<Move>(),
                    true, true);

            if (eval == 1234567890)
                break;
            if (eval <= alpha || eval >= beta) {
                alpha = -1000000;
                beta = 1000000;
                continue;
            }
            alpha = eval - WINDOW;
            beta = eval + WINDOW;

            currentDepth++;
            if (currentDepth > depth)
                break;
            if (System.currentTimeMillis() - bestMoveStart > time)
                break;
        }

        // TODO have a look at this
        if (bestLine.size() == 0) {
            ArrayList<Move> moves = board.generateAllMoves();
            bestLine.add(moves.get(0));
        }
        System.out.println("DEBUG"+" : "+ "Number of nodes : "+nodeCounter + " depth :" + currentDepth);
        return bestLine.get(0);
    }

    // -----------------------------------------------------------------------------------------------------------

    private void updateBestLine(ArrayList<Move> line, int depth, int eval) {
        if (depth == bestLineDepth && eval == bestLineEval)
            return;
        bestLineDepth = depth;
        bestLineEval = eval;
        bestLine = line;

        String s = bestLineDepth + " : ";
        for (int i = 0; i < bestLine.size(); i++) {
            if (i == bestLineDepth)
                s += "| ";
            s += bestLine.get(i).toString() + " ";
        }
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : "
                + bestLineEval;
        System.out.println(TAG+" : "+ s);
    }

    // -----------------------------------------------------------------------------------------------------------

    private class MoveComparator implements Comparator<Move> {

        public int ply;

        public int compare(Move move1, Move move2) {
            int moveEval1 = moveEval(move1);
            int moveEval2 = moveEval(move2);
            if (moveEval1 > moveEval2)
                return -1;
            else if (moveEval2 > moveEval1)
                return 1;
            else
                return 0;
        }

        private int moveEval(Move move) {
            if (bestLine != null && bestLine.size() >= ply) {
                Move lastBest = bestLine.get(ply - 1);
                if (move.from == lastBest.from && move.to == lastBest.to
                        && move.piece == lastBest.piece)
                    return 100000;
            }

            /*
             * if(primaryKillers[ply - 1] != null) if(move.from ==
             * primaryKillers[ply - 1].from && move.to == primaryKillers[ply -
             * 1].to && move.piece == primaryKillers[ply - 1].piece) return
             * 90000;
             * 
             * if(secondaryKillers[ply - 1] != null) if(move.from ==
             * secondaryKillers[ply - 1].from && move.to == secondaryKillers[ply
             * - 1].to && move.piece == secondaryKillers[ply - 1].piece) return
             * 80000;
             */

            if (move.capture == 0)
                return 0;
            else {
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];
                return capturePrice - piecePrice + 2000;
            }
        }
    }
}
