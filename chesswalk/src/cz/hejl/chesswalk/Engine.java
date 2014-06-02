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
//####[31]####
//-- ParaTask related imports//####[31]####
import pt.runtime.*;//####[31]####
import java.util.concurrent.ExecutionException;//####[31]####
import java.util.concurrent.locks.*;//####[31]####
import java.lang.reflect.*;//####[31]####
import pt.runtime.GuiThread;//####[31]####
import java.util.concurrent.BlockingQueue;//####[31]####
import java.util.ArrayList;//####[31]####
import java.util.List;//####[31]####
//####[31]####
public class Engine {//####[33]####
    static{ParaTask.init();}//####[33]####
    /*  ParaTask helper method to access private/protected slots *///####[33]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[33]####
        if (m.getParameterTypes().length == 0)//####[33]####
            m.invoke(instance);//####[33]####
        else if ((m.getParameterTypes().length == 1))//####[33]####
            m.invoke(instance, arg);//####[33]####
        else //####[33]####
            m.invoke(instance, arg, interResult);//####[33]####
    }//####[33]####
//####[34]####
    public AtomicInteger nodeCounter = new AtomicInteger(0);//####[34]####
//####[36]####
    private static final int WINDOW = 10;//####[36]####
//####[37]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[37]####
//####[39]####
    private static final String TAG = "Engine";//####[39]####
//####[41]####
    private boolean allowNullGlobal = true;//####[41]####
//####[42]####
    private int bestLineDepth;//####[42]####
//####[43]####
    private int bestLineEval;//####[43]####
//####[44]####
    private int bestMoveTimeLimit;//####[44]####
//####[45]####
    private long bestMoveStart;//####[45]####
//####[46]####
    private ArrayList<Move> bestLine;//####[46]####
//####[47]####
    private MoveComparator moveComparator = new MoveComparator();//####[47]####
//####[51]####
    private Integer alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[52]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[54]####
        return new Integer(1234567890);//####[56]####
        if (allowNullGlobal == false) //####[57]####
        allowNull = false;//####[58]####
        nodeCounter.incrementAndGet();//####[59]####
        int initialAlpha = alpha;//####[60]####
        int initialLineSize = line.size();//####[62]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[63]####
        ArrayList<Move> moves = null;//####[64]####
        moves = board.generateAllMoves();//####[65]####
        moveComparator.ply = currentDepth - depth + 1;//####[66]####
        Collections.sort(moves, moveComparator);//####[67]####
        if (depth <= 0) //####[69]####
        {//####[69]####
            int eval = board.evaluate();//####[70]####
            if (eval >= beta) //####[71]####
            return new Integer(beta);//####[72]####
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
        if (allowNull && depth > 0) //####[89]####
        {//####[89]####
            if (!board.inCheck(board.toMove)) //####[90]####
            {//####[90]####
                board.toMove *= -1;//####[91]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false, board, currentDepth + 1);//####[92]####
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
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, board, currentDepth + 2);//####[118]####
                    if (eval > alpha) //####[120]####
                    {//####[120]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[121]####
                    }//####[123]####
                } else {//####[124]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[125]####
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
//####[161]####
    private Integer PVSplit(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[162]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[164]####
        return new Integer(1234567890);//####[166]####
        if (allowNullGlobal == false) //####[167]####
        allowNull = false;//####[168]####
        int initialAlpha = alpha;//####[169]####
        nodeCounter.incrementAndGet();//####[170]####
        int initialLineSize = line.size();//####[171]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[172]####
        ArrayList<Move> moves = null;//####[173]####
        moves = board.generateAllMoves();//####[174]####
        moveComparator.ply = currentDepth - depth + 1;//####[175]####
        Collections.sort(moves, moveComparator);//####[176]####
        if (depth <= 0) //####[178]####
        {//####[178]####
            int eval = board.evaluate();//####[179]####
            if (eval >= beta) //####[180]####
            return new Integer(beta);//####[181]####
            if (eval > alpha) //####[182]####
            alpha = eval;//####[183]####
            int capturesN = 0;//####[185]####
            for (int i = 0; i < moves.size(); i++) //####[186]####
            {//####[186]####
                if (moves.get(i).capture == 0) //####[187]####
                break;//####[188]####
                capturesN++;//####[189]####
            }//####[190]####
            moves.subList(capturesN, moves.size()).clear();//####[191]####
        }//####[192]####
        if (moves.size() == 0) //####[194]####
        return board.evaluate();//####[195]####
        board.doMove(moves.get(0));//####[197]####
        locLine.clear();//####[198]####
        int evalOne;//####[199]####
        if (board.isRepetition()) //####[201]####
        evalOne = -50; else if (board.isDraw50Move()) //####[203]####
        evalOne = -50; else {//####[205]####
            evalOne = -PVSplit(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[206]####
        }//####[208]####
        board.undoMove(moves.get(0));//####[209]####
        if (evalOne == -1234567890) //####[210]####
        return new Integer(1234567890);//####[211]####
        if (evalOne >= beta) //####[213]####
        {//####[213]####
            return new Integer(beta);//####[214]####
        }//####[215]####
        if (evalOne > alpha) //####[217]####
        {//####[217]####
            alpha = evalOne;//####[218]####
            line.subList(initialLineSize, line.size()).clear();//####[219]####
            line.add(moves.get(0));//####[220]####
            line.addAll(locLine);//####[221]####
        }//####[222]####
        if (root && (evalOne > bestLineEval || evalOne == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[225]####
        {//####[228]####
            updateBestLine(line, depth, evalOne);//####[229]####
        }//####[230]####
        for (int i = 1; i < moves.size(); i++) //####[233]####
        {//####[233]####
            Board b1 = new Board(board);//####[234]####
            locLine.clear();//####[235]####
            int eval;//####[236]####
            b1.doMove(moves.get(i));//####[238]####
            if (b1.isRepetition()) //####[239]####
            eval = -50; else if (b1.isDraw50Move()) //####[241]####
            eval = -50; else {//####[243]####
                if (i >= 4 && currentDepth - depth >= 2 && !b1.inCheck(b1.toMove) && moves.get(i).capture == 0) //####[244]####
                {//####[246]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, b1, currentDepth + 2);//####[247]####
                    if (eval > alpha) //####[249]####
                    {//####[249]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, b1, currentDepth + 1);//####[250]####
                    }//####[252]####
                } else {//####[253]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, b1, currentDepth + 1);//####[254]####
                }//####[256]####
            }//####[257]####
            b1.undoMove(moves.get(i));//####[258]####
            if (eval == -1234567890) //####[259]####
            return new Integer(1234567890);//####[260]####
            if (eval >= beta) //####[262]####
            {//####[262]####
                return new Integer(beta);//####[263]####
            }//####[264]####
            if (eval > alpha) //####[266]####
            {//####[266]####
                alpha = eval;//####[267]####
                line.subList(initialLineSize, line.size()).clear();//####[268]####
                line.add(moves.get(i));//####[269]####
                line.addAll(locLine);//####[270]####
            }//####[271]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[274]####
            {//####[277]####
                updateBestLine(line, depth, eval);//####[278]####
            }//####[279]####
        }//####[280]####
        if (root && alpha > initialAlpha) //####[282]####
        {//####[282]####
            updateBestLine(line, depth, alpha);//####[283]####
        }//####[284]####
        return new Integer(alpha);//####[286]####
    }//####[287]####
//####[290]####
    public Move bestMove(String FEN, int depth, int time) {//####[290]####
        return bestMove(FEN, depth, time, false);//####[291]####
    }//####[292]####
//####[294]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[294]####
        Board board = new Board(FEN);//####[295]####
        nodeCounter.set(0);//####[296]####
        ;//####[296]####
        bestMoveTimeLimit = time;//####[297]####
        int eval = 0;//####[299]####
        bestLine = new ArrayList<Move>();//####[300]####
        bestLineDepth = 0;//####[301]####
        bestLineEval = -100000;//####[302]####
        bestMoveStart = System.currentTimeMillis();//####[303]####
        int currentDepth = 1;//####[304]####
        int alpha = -1000000;//####[305]####
        int beta = 1000000;//####[306]####
        while (true) //####[307]####
        {//####[307]####
            if (currentDepth == 1) //####[309]####
            {//####[309]####
                ArrayList<Move> moves = board.generateAllMoves();//####[310]####
                if (moves.size() == 1) //####[311]####
                {//####[311]####
                    bestLine = new ArrayList<Move>();//####[312]####
                    bestLine.add(moves.get(0));//####[313]####
                    break;//####[314]####
                }//####[315]####
            }//####[316]####
            eval = PVSplit(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[317]####
            if (eval == 1234567890) //####[319]####
            break;//####[320]####
            if (eval <= alpha || eval >= beta) //####[321]####
            {//####[321]####
                alpha = -1000000;//####[322]####
                beta = 1000000;//####[323]####
                continue;//####[324]####
            }//####[325]####
            alpha = eval - WINDOW;//####[326]####
            beta = eval + WINDOW;//####[327]####
            currentDepth++;//####[329]####
            if (currentDepth > depth) //####[330]####
            break;//####[331]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[332]####
            break;//####[333]####
        }//####[334]####
        if (bestLine.size() == 0) //####[337]####
        {//####[337]####
            ArrayList<Move> moves = board.generateAllMoves();//####[338]####
            bestLine.add(moves.get(0));//####[339]####
        }//####[340]####
        System.out.println("ENGINE" + " : " + "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[342]####
        return bestLine.get(0);//####[343]####
    }//####[344]####
//####[348]####
    private void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[348]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[349]####
        return;//####[350]####
        bestLineDepth = depth;//####[351]####
        bestLineEval = eval;//####[352]####
        bestLine = line;//####[353]####
        String s = bestLineDepth + " : ";//####[355]####
        for (int i = 0; i < bestLine.size(); i++) //####[356]####
        {//####[356]####
            if (i == bestLineDepth) //####[357]####
            s += "| ";//####[358]####
            s += bestLine.get(i).toString() + " ";//####[359]####
        }//####[360]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[361]####
        System.out.println(TAG + " : " + s);//####[363]####
    }//####[364]####
//####[368]####
    private class MoveComparator implements Comparator<Move> {//####[368]####
//####[368]####
        /*  ParaTask helper method to access private/protected slots *///####[368]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[368]####
            if (m.getParameterTypes().length == 0)//####[368]####
                m.invoke(instance);//####[368]####
            else if ((m.getParameterTypes().length == 1))//####[368]####
                m.invoke(instance, arg);//####[368]####
            else //####[368]####
                m.invoke(instance, arg, interResult);//####[368]####
        }//####[368]####
//####[370]####
        public int ply;//####[370]####
//####[372]####
        public int compare(Move move1, Move move2) {//####[372]####
            int moveEval1 = moveEval(move1);//####[373]####
            int moveEval2 = moveEval(move2);//####[374]####
            if (moveEval1 > moveEval2) //####[375]####
            return -1; else if (moveEval2 > moveEval1) //####[377]####
            return 1; else return 0;//####[378]####
        }//####[381]####
//####[383]####
        private int moveEval(Move move) {//####[383]####
            if (bestLine != null && bestLine.size() >= ply) //####[384]####
            {//####[384]####
                Move lastBest = bestLine.get(ply - 1);//####[385]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[386]####
                return 100000;//####[388]####
            }//####[389]####
            if (move.capture == 0) //####[403]####
            return 0; else {//####[405]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[406]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[407]####
                return capturePrice - piecePrice + 2000;//####[408]####
            }//####[409]####
        }//####[410]####
    }//####[410]####
}//####[410]####
