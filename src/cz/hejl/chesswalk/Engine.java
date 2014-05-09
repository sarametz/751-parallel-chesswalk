package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.ArrayList;//####[23]####
import java.util.Collections;//####[24]####
import java.util.Comparator;//####[25]####
import android.util.Log;//####[27]####
//####[27]####
//-- ParaTask related imports//####[27]####
import pt.runtime.*;//####[27]####
import java.util.concurrent.ExecutionException;//####[27]####
import java.util.concurrent.locks.*;//####[27]####
import java.lang.reflect.*;//####[27]####
import pt.runtime.GuiThread;//####[27]####
import java.util.concurrent.BlockingQueue;//####[27]####
import java.util.ArrayList;//####[27]####
import java.util.List;//####[27]####
//####[27]####
public class Engine {//####[29]####
    static{ParaTask.init();}//####[29]####
    /*  ParaTask helper method to access private/protected slots *///####[29]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[29]####
        if (m.getParameterTypes().length == 0)//####[29]####
            m.invoke(instance);//####[29]####
        else if ((m.getParameterTypes().length == 1))//####[29]####
            m.invoke(instance, arg);//####[29]####
        else //####[29]####
            m.invoke(instance, arg, interResult);//####[29]####
    }//####[29]####
//####[30]####
    public int nodeCounter = 0;//####[30]####
//####[31]####
    public Board board = new Board();//####[31]####
//####[33]####
    private static final int WINDOW = 10;//####[33]####
//####[34]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[34]####
//####[36]####
    private static final String TAG = "Engine";//####[36]####
//####[38]####
    private boolean allowNullGlobal = true;//####[38]####
//####[39]####
    private int bestLineDepth;//####[39]####
//####[40]####
    private int bestLineEval;//####[40]####
//####[41]####
    private int bestMoveTimeLimit;//####[41]####
//####[42]####
    private int currentDepth;//####[42]####
//####[43]####
    private long bestMoveStart;//####[43]####
//####[44]####
    private ArrayList<Move> bestLine;//####[44]####
//####[45]####
    private MoveComparator moveComparator = new MoveComparator();//####[45]####
//####[46]####
    private Move[] primaryKillers = new Move[50];//####[46]####
//####[47]####
    private Move[] secondaryKillers = new Move[50];//####[47]####
//####[51]####
    private int alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull) {//####[52]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[53]####
        return 1234567890;//####[55]####
        if (allowNullGlobal == false) //####[56]####
        allowNull = false;//####[57]####
        nodeCounter++;//####[58]####
        int initialAlpha = alpha;//####[59]####
        int initialLineSize = line.size();//####[61]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[62]####
        ArrayList<Move> moves = null;//####[63]####
        moves = board.generateAllMoves();//####[65]####
        moveComparator.ply = currentDepth - depth + 1;//####[66]####
        Collections.sort(moves, moveComparator);//####[67]####
        if (depth <= 0) //####[69]####
        {//####[69]####
            int eval = board.evaluate();//####[70]####
            if (eval >= beta) //####[71]####
            return beta;//####[72]####
            if (eval > alpha) //####[73]####
            alpha = eval;//####[74]####
            int capturesN = 0;//####[76]####
            for (int i = 0; i < moves.size(); i++) //####[77]####
            {//####[77]####
                if (moves.get(i).capture == 0) //####[78]####
                break;//####[79]####
                capturesN++;//####[80]####
            }//####[81]####
            moves.subList(capturesN, moves.size()).clear();//####[82]####
        }//####[83]####
        if (moves.size() == 0) //####[85]####
        return board.evaluate();//####[86]####
        if (allowNull && depth > 0) //####[88]####
        {//####[88]####
            if (!board.inCheck(board.toMove)) //####[89]####
            {//####[89]####
                board.toMove *= -1;//####[90]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false);//####[91]####
                board.toMove *= -1;//####[93]####
                if (eval == -1234567890) //####[94]####
                return 1234567890;//####[95]####
                if (eval >= beta) //####[97]####
                {//####[97]####
                    return beta;//####[98]####
                }//####[99]####
            }//####[100]####
        }//####[101]####
        for (int i = 0; i < moves.size(); i++) //####[103]####
        {//####[103]####
            locLine.clear();//####[104]####
            int eval;//####[105]####
            board.doMove(moves.get(i));//####[107]####
            if (board.isRepetition()) //####[108]####
            eval = -50; else if (board.isDraw50Move()) //####[110]####
            eval = -50; else {//####[112]####
                if (i >= 4 && currentDepth - depth >= 2 && !board.inCheck(board.toMove) && moves.get(i).capture == 0) //####[113]####
                {//####[115]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true);//####[116]####
                    if (eval > alpha) //####[118]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true);//####[119]####
                } else eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true);//####[121]####
            }//####[124]####
            board.undoMove(moves.get(i));//####[125]####
            if (eval == -1234567890) //####[126]####
            return 1234567890;//####[127]####
            if (eval >= beta) //####[129]####
            {//####[129]####
                if (primaryKillers[currentDepth - depth] != null) //####[131]####
                secondaryKillers[currentDepth - depth] = primaryKillers[currentDepth - depth];//####[132]####
                primaryKillers[currentDepth - depth] = moves.get(i);//####[134]####
                return beta;//####[136]####
            }//####[137]####
            if (eval > alpha) //####[139]####
            {//####[139]####
                alpha = eval;//####[140]####
                line.subList(initialLineSize, line.size()).clear();//####[141]####
                line.add(moves.get(i));//####[142]####
                line.addAll(locLine);//####[143]####
            }//####[144]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[147]####
            {//####[150]####
                updateBestLine(line, depth, eval);//####[151]####
            }//####[152]####
        }//####[153]####
        if (root && alpha > initialAlpha) //####[155]####
        {//####[155]####
            updateBestLine(line, depth, alpha);//####[156]####
        }//####[157]####
        return alpha;//####[159]####
    }//####[160]####
//####[164]####
    public Move bestMove(int depth, int time) {//####[164]####
        return bestMove(depth, time, false);//####[165]####
    }//####[166]####
//####[175]####
    /**
     * Returns the next best move available within time frame.
     * @param depth
     * @param time time limit to compute the next best move
     * @param verbose
     * @return best possible move
     *///####[175]####
    public Move bestMove(int depth, int time, boolean verbose) {//####[175]####
        nodeCounter = 0;//####[176]####
        bestMoveTimeLimit = time;//####[177]####
        int eval = 0;//####[180]####
        bestLine = new ArrayList<Move>();//####[181]####
        bestLineDepth = 0;//####[182]####
        bestLineEval = -100000;//####[183]####
        bestMoveStart = System.currentTimeMillis();//####[184]####
        currentDepth = 1;//####[185]####
        int alpha = -1000000;//####[186]####
        int beta = 1000000;//####[187]####
        while (true) //####[190]####
        {//####[190]####
            if (currentDepth == 1) //####[196]####
            {//####[196]####
                ArrayList<Move> moves = board.generateAllMoves();//####[197]####
                if (moves.size() == 1) //####[198]####
                {//####[198]####
                    bestLine = new ArrayList<Move>();//####[199]####
                    bestLine.add(moves.get(0));//####[200]####
                    break;//####[201]####
                }//####[202]####
            }//####[203]####
            eval = alphaBeta(currentDepth, alpha, beta, new ArrayList<Move>(), true, true);//####[205]####
            if (eval == 1234567890) //####[208]####
            break;//####[209]####
            if (eval <= alpha || eval >= beta) //####[210]####
            {//####[210]####
                alpha = -1000000;//####[211]####
                beta = 1000000;//####[212]####
                continue;//####[213]####
            }//####[214]####
            alpha = eval - WINDOW;//####[215]####
            beta = eval + WINDOW;//####[216]####
            currentDepth++;//####[218]####
            if (currentDepth > depth) //####[219]####
            break;//####[220]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[221]####
            break;//####[222]####
        }//####[223]####
        if (bestLine.size() == 0) //####[226]####
        {//####[226]####
            ArrayList<Move> moves = board.generateAllMoves();//####[227]####
            bestLine.add(moves.get(0));//####[228]####
        }//####[229]####
        return bestLine.get(0);//####[231]####
    }//####[232]####
//####[236]####
    private void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[236]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[237]####
        return;//####[238]####
        bestLineDepth = depth;//####[239]####
        bestLineEval = eval;//####[240]####
        bestLine = line;//####[241]####
        String s = bestLineDepth + " : ";//####[243]####
        for (int i = 0; i < bestLine.size(); i++) //####[244]####
        {//####[244]####
            if (i == bestLineDepth) //####[245]####
            s += "| ";//####[246]####
            s += bestLine.get(i).toString() + " ";//####[247]####
        }//####[248]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[249]####
        Log.d(TAG, s);//####[251]####
    }//####[252]####
//####[256]####
    private class MoveComparator implements Comparator<Move> {//####[256]####
//####[256]####
        /*  ParaTask helper method to access private/protected slots *///####[256]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[256]####
            if (m.getParameterTypes().length == 0)//####[256]####
                m.invoke(instance);//####[256]####
            else if ((m.getParameterTypes().length == 1))//####[256]####
                m.invoke(instance, arg);//####[256]####
            else //####[256]####
                m.invoke(instance, arg, interResult);//####[256]####
        }//####[256]####
//####[258]####
        public int ply;//####[258]####
//####[260]####
        public int compare(Move move1, Move move2) {//####[260]####
            int moveEval1 = moveEval(move1);//####[261]####
            int moveEval2 = moveEval(move2);//####[262]####
            if (moveEval1 > moveEval2) //####[263]####
            return -1; else if (moveEval2 > moveEval1) //####[265]####
            return 1; else return 0;//####[266]####
        }//####[269]####
//####[271]####
        private int moveEval(Move move) {//####[271]####
            if (bestLine != null && bestLine.size() >= ply) //####[272]####
            {//####[272]####
                Move lastBest = bestLine.get(ply - 1);//####[273]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[274]####
                return 100000;//####[276]####
            }//####[277]####
            if (move.capture == 0) //####[291]####
            return 0; else {//####[293]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[294]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[295]####
                return capturePrice - piecePrice + 2000;//####[296]####
            }//####[297]####
        }//####[298]####
    }//####[298]####
}//####[298]####
