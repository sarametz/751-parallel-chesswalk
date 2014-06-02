package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.ArrayList;//####[23]####
import java.util.Collections;//####[24]####
import java.util.Comparator;//####[25]####
import java.util.Currency;//####[26]####
import java.util.concurrent.ExecutionException;//####[27]####
import java.util.concurrent.atomic.AtomicInteger;//####[28]####
import pt.runtime.CurrentTask;//####[30]####
import pt.runtime.TaskID;//####[31]####
import android.util.Log;//####[32]####
//####[32]####
//-- ParaTask related imports//####[32]####
import pt.runtime.*;//####[32]####
import java.util.concurrent.ExecutionException;//####[32]####
import java.util.concurrent.locks.*;//####[32]####
import java.lang.reflect.*;//####[32]####
import pt.runtime.GuiThread;//####[32]####
import java.util.concurrent.BlockingQueue;//####[32]####
import java.util.ArrayList;//####[32]####
import java.util.List;//####[32]####
//####[32]####
public class Engine {//####[34]####
    static{ParaTask.init();}//####[34]####
    /*  ParaTask helper method to access private/protected slots *///####[34]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[34]####
        if (m.getParameterTypes().length == 0)//####[34]####
            m.invoke(instance);//####[34]####
        else if ((m.getParameterTypes().length == 1))//####[34]####
            m.invoke(instance, arg);//####[34]####
        else //####[34]####
            m.invoke(instance, arg, interResult);//####[34]####
    }//####[34]####
//####[35]####
    public AtomicInteger nodeCounter = new AtomicInteger(0);//####[35]####
//####[37]####
    private static final int WINDOW = 10;//####[37]####
//####[38]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[38]####
//####[40]####
    private static final String TAG = "Engine";//####[40]####
//####[42]####
    private boolean allowNullGlobal = true;//####[42]####
//####[43]####
    private int bestLineDepth;//####[43]####
//####[44]####
    private int bestLineEval;//####[44]####
//####[45]####
    private int bestMoveTimeLimit;//####[45]####
//####[46]####
    private long bestMoveStart;//####[46]####
//####[47]####
    private ArrayList<Move> bestLine;//####[47]####
//####[48]####
    private MoveComparator moveComparator = new MoveComparator();//####[48]####
//####[52]####
    private Integer alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[53]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[55]####
        return new Integer(1234567890);//####[57]####
        if (allowNullGlobal == false) //####[58]####
        allowNull = false;//####[59]####
        nodeCounter.incrementAndGet();//####[60]####
        int initialAlpha = alpha;//####[61]####
        int initialLineSize = line.size();//####[63]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[64]####
        ArrayList<Move> moves = null;//####[65]####
        moves = board.generateAllMoves();//####[66]####
        moveComparator.ply = currentDepth - depth + 1;//####[67]####
        Collections.sort(moves, moveComparator);//####[68]####
        if (depth <= 0) //####[70]####
        {//####[70]####
            int eval = board.evaluate();//####[71]####
            if (eval >= beta) //####[72]####
            return new Integer(beta);//####[73]####
            if (eval > alpha) //####[74]####
            alpha = eval;//####[75]####
            int capturesN = 0;//####[77]####
            for (int i = 0; i < moves.size(); i++) //####[78]####
            {//####[78]####
                if (moves.get(i).capture == 0) //####[79]####
                break;//####[80]####
                capturesN++;//####[81]####
            }//####[82]####
            moves.subList(capturesN, moves.size()).clear();//####[83]####
        }//####[84]####
        if (moves.size() == 0) //####[86]####
        return board.evaluate();//####[87]####
        if (allowNull && depth > 0) //####[89]####
        {//####[89]####
            if (!board.inCheck(board.toMove)) //####[90]####
            {//####[90]####
                board.toMove *= -1;//####[91]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false, new Board(board), currentDepth + 1);//####[92]####
                board.toMove *= -1;//####[95]####
                if (eval == -1234567890) //####[96]####
                return new Integer(1234567890);//####[97]####
                if (eval >= beta) //####[99]####
                {//####[99]####
                    return new Integer(beta);//####[100]####
                }//####[101]####
            }//####[102]####
        }//####[103]####
        for (int i = 0; i < moves.size(); i++) //####[105]####
        {//####[105]####
            locLine.clear();//####[106]####
            int eval;//####[107]####
            board.doMove(moves.get(i));//####[109]####
            if (board.isRepetition()) //####[110]####
            eval = -50; else if (board.isDraw50Move()) //####[112]####
            eval = -50; else {//####[114]####
                if (i >= 4 && currentDepth - depth >= 2 && !board.inCheck(board.toMove) && moves.get(i).capture == 0) //####[115]####
                {//####[117]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, new Board(board), currentDepth + 2);//####[118]####
                    if (eval > alpha) //####[120]####
                    {//####[120]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, new Board(board), currentDepth + 1);//####[121]####
                    }//####[123]####
                } else {//####[124]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, new Board(board), currentDepth + 1);//####[125]####
                }//####[127]####
            }//####[128]####
            board.undoMove(moves.get(i));//####[129]####
            if (eval == -1234567890) //####[130]####
            return new Integer(1234567890);//####[131]####
            if (eval >= beta) //####[133]####
            {//####[133]####
                return new Integer(beta);//####[134]####
            }//####[135]####
            if (eval > alpha) //####[137]####
            {//####[137]####
                alpha = eval;//####[138]####
                line.subList(initialLineSize, line.size()).clear();//####[139]####
                line.add(moves.get(i));//####[140]####
                line.addAll(locLine);//####[141]####
            }//####[142]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[145]####
            {//####[148]####
                updateBestLine(line, depth, eval);//####[149]####
            }//####[150]####
        }//####[151]####
        if (root && alpha > initialAlpha) //####[153]####
        {//####[153]####
            updateBestLine(line, depth, alpha);//####[154]####
        }//####[155]####
        return new Integer(alpha);//####[157]####
    }//####[158]####
//####[162]####
    public Move bestMove(String FEN, int depth, int time) {//####[162]####
        return bestMove(FEN, depth, time, false);//####[163]####
    }//####[164]####
//####[166]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[166]####
        Board board = new Board(FEN);//####[167]####
        nodeCounter.set(0);//####[168]####
        ;//####[168]####
        bestMoveTimeLimit = time;//####[169]####
        int eval = 0;//####[171]####
        bestLine = new ArrayList<Move>();//####[172]####
        bestLineDepth = 0;//####[173]####
        bestLineEval = -100000;//####[174]####
        bestMoveStart = System.currentTimeMillis();//####[175]####
        int currentDepth = 1;//####[176]####
        int alpha = -1000000;//####[177]####
        int beta = 1000000;//####[178]####
        while (true) //####[179]####
        {//####[179]####
            if (currentDepth == 1) //####[181]####
            {//####[181]####
                ArrayList<Move> moves = board.generateAllMoves();//####[182]####
                if (moves.size() == 1) //####[183]####
                {//####[183]####
                    bestLine = new ArrayList<Move>();//####[184]####
                    bestLine.add(moves.get(0));//####[185]####
                    break;//####[186]####
                }//####[187]####
            }//####[188]####
            eval = alphaBeta(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[189]####
            if (eval == 1234567890) //####[191]####
            break;//####[192]####
            if (eval <= alpha || eval >= beta) //####[193]####
            {//####[193]####
                alpha = -1000000;//####[194]####
                beta = 1000000;//####[195]####
                continue;//####[196]####
            }//####[197]####
            alpha = eval - WINDOW;//####[198]####
            beta = eval + WINDOW;//####[199]####
            currentDepth++;//####[201]####
            if (currentDepth > depth) //####[202]####
            break;//####[203]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[204]####
            break;//####[205]####
        }//####[206]####
        if (bestLine.size() == 0) //####[209]####
        {//####[209]####
            ArrayList<Move> moves = board.generateAllMoves();//####[210]####
            bestLine.add(moves.get(0));//####[211]####
        }//####[212]####
        Log.d("ENGINE", "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[214]####
        return bestLine.get(0);//####[215]####
    }//####[216]####
//####[220]####
    private void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[220]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[221]####
        return;//####[222]####
        bestLineDepth = depth;//####[223]####
        bestLineEval = eval;//####[224]####
        bestLine = line;//####[225]####
        String s = bestLineDepth + " : ";//####[227]####
        for (int i = 0; i < bestLine.size(); i++) //####[228]####
        {//####[228]####
            if (i == bestLineDepth) //####[229]####
            s += "| ";//####[230]####
            s += bestLine.get(i).toString() + " ";//####[231]####
        }//####[232]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[233]####
        Log.d(TAG, s);//####[235]####
    }//####[236]####
//####[240]####
    private class MoveComparator implements Comparator<Move> {//####[240]####
//####[240]####
        /*  ParaTask helper method to access private/protected slots *///####[240]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[240]####
            if (m.getParameterTypes().length == 0)//####[240]####
                m.invoke(instance);//####[240]####
            else if ((m.getParameterTypes().length == 1))//####[240]####
                m.invoke(instance, arg);//####[240]####
            else //####[240]####
                m.invoke(instance, arg, interResult);//####[240]####
        }//####[240]####
//####[242]####
        public int ply;//####[242]####
//####[244]####
        public int compare(Move move1, Move move2) {//####[244]####
            int moveEval1 = moveEval(move1);//####[245]####
            int moveEval2 = moveEval(move2);//####[246]####
            if (moveEval1 > moveEval2) //####[247]####
            return -1; else if (moveEval2 > moveEval1) //####[249]####
            return 1; else return 0;//####[250]####
        }//####[253]####
//####[255]####
        private int moveEval(Move move) {//####[255]####
            if (bestLine != null && bestLine.size() >= ply) //####[256]####
            {//####[256]####
                Move lastBest = bestLine.get(ply - 1);//####[257]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[258]####
                return 100000;//####[260]####
            }//####[261]####
            if (move.capture == 0) //####[275]####
            return 0; else {//####[277]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[278]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[279]####
                return capturePrice - piecePrice + 2000;//####[280]####
            }//####[281]####
        }//####[282]####
    }//####[282]####
}//####[282]####
