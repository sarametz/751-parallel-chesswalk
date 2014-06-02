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
        if (allowNull && depth > 0) //####[90]####
        {//####[90]####
            if (!board.inCheck(board.toMove)) //####[91]####
            {//####[91]####
                board.toMove *= -1;//####[92]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false, board, currentDepth + 1);//####[93]####
                board.toMove *= -1;//####[96]####
                if (eval == -1234567890) //####[97]####
                return new Integer(1234567890);//####[98]####
                if (eval >= beta) //####[100]####
                {//####[100]####
                    return new Integer(beta);//####[101]####
                }//####[102]####
            }//####[103]####
        }//####[104]####
        for (int i = 0; i < moves.size(); i++) //####[106]####
        {//####[106]####
            locLine.clear();//####[107]####
            int eval;//####[108]####
            board.doMove(moves.get(i));//####[110]####
            if (board.isRepetition()) //####[111]####
            eval = -50; else if (board.isDraw50Move()) //####[113]####
            eval = -50; else {//####[115]####
                if (i >= 4 && currentDepth - depth >= 2 && !board.inCheck(board.toMove) && moves.get(i).capture == 0) //####[116]####
                {//####[118]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, board, currentDepth + 2);//####[119]####
                    if (eval > alpha) //####[121]####
                    {//####[121]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[122]####
                    }//####[124]####
                } else {//####[125]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[126]####
                }//####[128]####
            }//####[129]####
            board.undoMove(moves.get(i));//####[130]####
            if (eval == -1234567890) //####[131]####
            return new Integer(1234567890);//####[132]####
            if (eval >= beta) //####[134]####
            {//####[134]####
                return new Integer(beta);//####[135]####
            }//####[136]####
            if (eval > alpha) //####[138]####
            {//####[138]####
                alpha = eval;//####[139]####
                line.subList(initialLineSize, line.size()).clear();//####[140]####
                line.add(moves.get(i));//####[141]####
                line.addAll(locLine);//####[142]####
            }//####[143]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[146]####
            {//####[149]####
                updateBestLine(line, depth, eval);//####[150]####
            }//####[151]####
        }//####[152]####
        if (root && alpha > initialAlpha) //####[154]####
        {//####[154]####
            updateBestLine(line, depth, alpha);//####[155]####
        }//####[156]####
        return new Integer(alpha);//####[158]####
    }//####[159]####
//####[162]####
    private Integer PVSplit(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[163]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[165]####
        return new Integer(1234567890);//####[167]####
        if (allowNullGlobal == false) //####[168]####
        allowNull = false;//####[169]####
        int initialAlpha = alpha;//####[170]####
        nodeCounter.incrementAndGet();//####[171]####
        int initialLineSize = line.size();//####[172]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[173]####
        ArrayList<Move> moves = null;//####[174]####
        moves = board.generateAllMoves();//####[175]####
        moveComparator.ply = currentDepth - depth + 1;//####[176]####
        Collections.sort(moves, moveComparator);//####[177]####
        if (depth <= 0) //####[179]####
        {//####[179]####
            int eval = board.evaluate();//####[180]####
            if (eval >= beta) //####[181]####
            return new Integer(beta);//####[182]####
            if (eval > alpha) //####[183]####
            alpha = eval;//####[184]####
            int capturesN = 0;//####[186]####
            for (int i = 0; i < moves.size(); i++) //####[187]####
            {//####[187]####
                if (moves.get(i).capture == 0) //####[188]####
                break;//####[189]####
                capturesN++;//####[190]####
            }//####[191]####
            moves.subList(capturesN, moves.size()).clear();//####[192]####
        }//####[193]####
        if (moves.size() == 0) //####[195]####
        return board.evaluate();//####[196]####
        board.doMove(moves.get(0));//####[198]####
        locLine.clear();//####[199]####
        int evalOne;//####[200]####
        if (board.isRepetition()) //####[202]####
        evalOne = -50; else if (board.isDraw50Move()) //####[204]####
        evalOne = -50; else {//####[206]####
            evalOne = -PVSplit(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[207]####
        }//####[209]####
        board.undoMove(moves.get(0));//####[210]####
        if (evalOne == -1234567890) //####[211]####
        return new Integer(1234567890);//####[212]####
        if (evalOne >= beta) //####[214]####
        {//####[214]####
            return new Integer(beta);//####[215]####
        }//####[216]####
        if (evalOne > alpha) //####[218]####
        {//####[218]####
            alpha = evalOne;//####[219]####
            line.subList(initialLineSize, line.size()).clear();//####[220]####
            line.add(moves.get(0));//####[221]####
            line.addAll(locLine);//####[222]####
        }//####[223]####
        if (root && (evalOne > bestLineEval || evalOne == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[226]####
        {//####[229]####
            updateBestLine(line, depth, evalOne);//####[230]####
        }//####[231]####
        for (int i = 1; i < moves.size(); i++) //####[234]####
        {//####[234]####
            Board b1 = new Board(board);//####[235]####
            locLine.clear();//####[236]####
            int eval;//####[237]####
            b1.doMove(moves.get(i));//####[239]####
            if (b1.isRepetition()) //####[240]####
            eval = -50; else if (b1.isDraw50Move()) //####[242]####
            eval = -50; else {//####[244]####
                if (i >= 4 && currentDepth - depth >= 2 && !b1.inCheck(b1.toMove) && moves.get(i).capture == 0) //####[245]####
                {//####[247]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, b1, currentDepth + 2);//####[248]####
                    if (eval > alpha) //####[250]####
                    {//####[250]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, b1, currentDepth + 1);//####[251]####
                    }//####[253]####
                } else {//####[254]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, b1, currentDepth + 1);//####[255]####
                }//####[257]####
            }//####[258]####
            b1.undoMove(moves.get(i));//####[259]####
            if (eval == -1234567890) //####[260]####
            return new Integer(1234567890);//####[261]####
            if (eval >= beta) //####[263]####
            {//####[263]####
                return new Integer(beta);//####[264]####
            }//####[265]####
            if (eval > alpha) //####[267]####
            {//####[267]####
                alpha = eval;//####[268]####
                line.subList(initialLineSize, line.size()).clear();//####[269]####
                line.add(moves.get(i));//####[270]####
                line.addAll(locLine);//####[271]####
            }//####[272]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[275]####
            {//####[278]####
                updateBestLine(line, depth, eval);//####[279]####
            }//####[280]####
        }//####[281]####
        if (root && alpha > initialAlpha) //####[283]####
        {//####[283]####
            updateBestLine(line, depth, alpha);//####[284]####
        }//####[285]####
        return new Integer(alpha);//####[287]####
    }//####[288]####
//####[291]####
    public Move bestMove(String FEN, int depth, int time) {//####[291]####
        return bestMove(FEN, depth, time, false);//####[292]####
    }//####[293]####
//####[295]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[295]####
        Board board = new Board(FEN);//####[296]####
        nodeCounter.set(0);//####[297]####
        ;//####[297]####
        bestMoveTimeLimit = time;//####[298]####
        int eval = 0;//####[300]####
        bestLine = new ArrayList<Move>();//####[301]####
        bestLineDepth = 0;//####[302]####
        bestLineEval = -100000;//####[303]####
        bestMoveStart = System.currentTimeMillis();//####[304]####
        int currentDepth = 1;//####[305]####
        int alpha = -1000000;//####[306]####
        int beta = 1000000;//####[307]####
        while (true) //####[308]####
        {//####[308]####
            if (currentDepth == 1) //####[310]####
            {//####[310]####
                ArrayList<Move> moves = board.generateAllMoves();//####[311]####
                if (moves.size() == 1) //####[312]####
                {//####[312]####
                    bestLine = new ArrayList<Move>();//####[313]####
                    bestLine.add(moves.get(0));//####[314]####
                    break;//####[315]####
                }//####[316]####
            }//####[317]####
            eval = PVSplit(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[318]####
            if (eval == 1234567890) //####[320]####
            break;//####[321]####
            if (eval <= alpha || eval >= beta) //####[322]####
            {//####[322]####
                alpha = -1000000;//####[323]####
                beta = 1000000;//####[324]####
                continue;//####[325]####
            }//####[326]####
            alpha = eval - WINDOW;//####[327]####
            beta = eval + WINDOW;//####[328]####
            currentDepth++;//####[330]####
            if (currentDepth > depth) //####[331]####
            break;//####[332]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[333]####
            break;//####[334]####
        }//####[335]####
        if (bestLine.size() == 0) //####[338]####
        {//####[338]####
            ArrayList<Move> moves = board.generateAllMoves();//####[339]####
            bestLine.add(moves.get(0));//####[340]####
        }//####[341]####
        Log.d("ENGINE", "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[343]####
        return bestLine.get(0);//####[344]####
    }//####[345]####
//####[349]####
    private void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[349]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[350]####
        return;//####[351]####
        bestLineDepth = depth;//####[352]####
        bestLineEval = eval;//####[353]####
        bestLine = line;//####[354]####
        String s = bestLineDepth + " : ";//####[356]####
        for (int i = 0; i < bestLine.size(); i++) //####[357]####
        {//####[357]####
            if (i == bestLineDepth) //####[358]####
            s += "| ";//####[359]####
            s += bestLine.get(i).toString() + " ";//####[360]####
        }//####[361]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[362]####
        Log.d(TAG, s);//####[364]####
    }//####[365]####
//####[369]####
    private class MoveComparator implements Comparator<Move> {//####[369]####
//####[369]####
        /*  ParaTask helper method to access private/protected slots *///####[369]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[369]####
            if (m.getParameterTypes().length == 0)//####[369]####
                m.invoke(instance);//####[369]####
            else if ((m.getParameterTypes().length == 1))//####[369]####
                m.invoke(instance, arg);//####[369]####
            else //####[369]####
                m.invoke(instance, arg, interResult);//####[369]####
        }//####[369]####
//####[371]####
        public int ply;//####[371]####
//####[373]####
        public int compare(Move move1, Move move2) {//####[373]####
            int moveEval1 = moveEval(move1);//####[374]####
            int moveEval2 = moveEval(move2);//####[375]####
            if (moveEval1 > moveEval2) //####[376]####
            return -1; else if (moveEval2 > moveEval1) //####[378]####
            return 1; else return 0;//####[379]####
        }//####[382]####
//####[384]####
        private int moveEval(Move move) {//####[384]####
            if (bestLine != null && bestLine.size() >= ply) //####[385]####
            {//####[385]####
                Move lastBest = bestLine.get(ply - 1);//####[386]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[387]####
                return 100000;//####[389]####
            }//####[390]####
            if (move.capture == 0) //####[404]####
            return 0; else {//####[406]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[407]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[408]####
                return capturePrice - piecePrice + 2000;//####[409]####
            }//####[410]####
        }//####[411]####
    }//####[411]####
}//####[411]####
