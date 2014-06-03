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
import android.os.AsyncTask;//####[32]####
import android.util.Log;//####[33]####
//####[33]####
//-- ParaTask related imports//####[33]####
import pt.runtime.*;//####[33]####
import java.util.concurrent.ExecutionException;//####[33]####
import java.util.concurrent.locks.*;//####[33]####
import java.lang.reflect.*;//####[33]####
import pt.runtime.GuiThread;//####[33]####
import java.util.concurrent.BlockingQueue;//####[33]####
import java.util.ArrayList;//####[33]####
import java.util.List;//####[33]####
//####[33]####
public class Engine {//####[35]####
    static{ParaTask.init();}//####[35]####
    /*  ParaTask helper method to access private/protected slots *///####[35]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[35]####
        if (m.getParameterTypes().length == 0)//####[35]####
            m.invoke(instance);//####[35]####
        else if ((m.getParameterTypes().length == 1))//####[35]####
            m.invoke(instance, arg);//####[35]####
        else //####[35]####
            m.invoke(instance, arg, interResult);//####[35]####
    }//####[35]####
//####[36]####
    public AtomicInteger nodeCounter = new AtomicInteger(0);//####[36]####
//####[38]####
    private static final int WINDOW = 10;//####[38]####
//####[39]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[39]####
//####[41]####
    private static final String TAG = "Engine";//####[41]####
//####[43]####
    private boolean allowNullGlobal = true;//####[43]####
//####[44]####
    public int bestLineDepth;//####[44]####
//####[45]####
    private int bestLineEval;//####[45]####
//####[46]####
    private int bestMoveTimeLimit;//####[46]####
//####[47]####
    private long bestMoveStart;//####[47]####
//####[48]####
    private ArrayList<Move> bestLine;//####[48]####
//####[53]####
    private Integer alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[54]####
        MoveComparator moveComparator = new MoveComparator();//####[56]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[57]####
        return new Integer(1234567890);//####[59]####
        if (allowNullGlobal == false) //####[60]####
        allowNull = false;//####[61]####
        nodeCounter.incrementAndGet();//####[62]####
        int initialAlpha = alpha;//####[63]####
        int initialLineSize = line.size();//####[65]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[66]####
        ArrayList<Move> moves = null;//####[67]####
        moves = board.generateAllMoves();//####[68]####
        moveComparator.ply = currentDepth - depth + 1;//####[69]####
        Collections.sort(moves, moveComparator);//####[70]####
        if (depth <= 0) //####[72]####
        {//####[72]####
            int eval = board.evaluate();//####[73]####
            if (eval >= beta) //####[74]####
            return new Integer(beta);//####[75]####
            if (eval > alpha) //####[76]####
            alpha = eval;//####[77]####
            int capturesN = 0;//####[79]####
            for (int i = 0; i < moves.size(); i++) //####[80]####
            {//####[80]####
                if (moves.get(i).capture == 0) //####[81]####
                break;//####[82]####
                capturesN++;//####[83]####
            }//####[84]####
            moves.subList(capturesN, moves.size()).clear();//####[85]####
        }//####[86]####
        if (moves.size() == 0) //####[88]####
        return board.evaluate();//####[89]####
        if (allowNull && depth > 0) //####[92]####
        {//####[92]####
            if (!board.inCheck(board.toMove)) //####[93]####
            {//####[93]####
                board.toMove *= -1;//####[94]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false, board, currentDepth + 1);//####[95]####
                board.toMove *= -1;//####[98]####
                if (eval == -1234567890) //####[99]####
                return new Integer(1234567890);//####[100]####
                if (eval >= beta) //####[102]####
                {//####[102]####
                    return new Integer(beta);//####[103]####
                }//####[104]####
            }//####[105]####
        }//####[106]####
        for (int i = 0; i < moves.size(); i++) //####[108]####
        {//####[108]####
            locLine.clear();//####[109]####
            int eval;//####[110]####
            board.doMove(moves.get(i));//####[112]####
            if (board.isRepetition()) //####[113]####
            eval = -50; else if (board.isDraw50Move()) //####[115]####
            eval = -50; else {//####[117]####
                if (i >= 4 && currentDepth - depth >= 2 && !board.inCheck(board.toMove) && moves.get(i).capture == 0) //####[118]####
                {//####[120]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, board, currentDepth + 2);//####[121]####
                    if (eval > alpha) //####[123]####
                    {//####[123]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[124]####
                    }//####[126]####
                } else {//####[127]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[128]####
                }//####[130]####
            }//####[131]####
            board.undoMove(moves.get(i));//####[132]####
            if (eval == -1234567890) //####[133]####
            return new Integer(1234567890);//####[134]####
            if (eval >= beta) //####[136]####
            {//####[136]####
                return new Integer(beta);//####[137]####
            }//####[138]####
            if (eval > alpha) //####[140]####
            {//####[140]####
                alpha = eval;//####[141]####
                line.subList(initialLineSize, line.size()).clear();//####[143]####
                line.add(moves.get(i));//####[144]####
                line.addAll(locLine);//####[145]####
            }//####[146]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[149]####
            {//####[152]####
                updateBestLine(line, depth, eval);//####[153]####
            }//####[154]####
        }//####[155]####
        if (root && alpha > initialAlpha) //####[157]####
        {//####[157]####
            updateBestLine(line, depth, alpha);//####[158]####
        }//####[159]####
        return new Integer(alpha);//####[161]####
    }//####[162]####
//####[165]####
    private Integer PVSplit(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[166]####
        MoveComparator moveComparator = new MoveComparator();//####[168]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[169]####
        return new Integer(1234567890);//####[171]####
        if (allowNullGlobal == false) //####[172]####
        allowNull = false;//####[173]####
        int initialAlpha = alpha;//####[174]####
        nodeCounter.incrementAndGet();//####[175]####
        int initialLineSize = line.size();//####[176]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[177]####
        ArrayList<Move> moves = null;//####[178]####
        moves = board.generateAllMoves();//####[179]####
        moveComparator.ply = currentDepth - depth + 1;//####[180]####
        Collections.sort(moves, moveComparator);//####[181]####
        if (depth <= 0) //####[183]####
        {//####[183]####
            int eval = board.evaluate();//####[184]####
            if (eval >= beta) //####[185]####
            return new Integer(beta);//####[186]####
            if (eval > alpha) //####[187]####
            alpha = eval;//####[188]####
            int capturesN = 0;//####[190]####
            for (int i = 0; i < moves.size(); i++) //####[191]####
            {//####[191]####
                if (moves.get(i).capture == 0) //####[192]####
                break;//####[193]####
                capturesN++;//####[194]####
            }//####[195]####
            moves.subList(capturesN, moves.size()).clear();//####[196]####
        }//####[197]####
        if (moves.size() == 0) //####[199]####
        return board.evaluate();//####[200]####
        board.doMove(moves.get(0));//####[202]####
        locLine.clear();//####[203]####
        int evalOne;//####[204]####
        if (board.isRepetition()) //####[206]####
        evalOne = -50; else if (board.isDraw50Move()) //####[208]####
        evalOne = -50; else {//####[210]####
            evalOne = -PVSplit(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[211]####
        }//####[213]####
        board.undoMove(moves.get(0));//####[214]####
        if (evalOne == -1234567890) //####[215]####
        return new Integer(1234567890);//####[216]####
        if (evalOne >= beta) //####[218]####
        {//####[218]####
            return new Integer(beta);//####[219]####
        }//####[220]####
        if (evalOne > alpha) //####[222]####
        {//####[222]####
            alpha = evalOne;//####[223]####
            line.subList(initialLineSize, line.size()).clear();//####[224]####
            line.add(moves.get(0));//####[225]####
            line.addAll(locLine);//####[226]####
        }//####[227]####
        if (root && (evalOne > bestLineEval || evalOne == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[230]####
        {//####[233]####
            updateBestLine(line, depth, evalOne);//####[234]####
        }//####[235]####
        AtomicInteger atomicAlpha = new AtomicInteger(alpha);//####[238]####
        AtomicInteger atomicBeta = new AtomicInteger(beta);//####[239]####
        AtomicInteger atomicEval = new AtomicInteger(0);//####[240]####
        ArrayList<Thread> threads = new ArrayList<Thread>();//####[241]####
        for (int i = 1; i < moves.size(); i++) //####[242]####
        {//####[242]####
            Board b1 = new Board(board);//####[243]####
            DoParallelLoop d = new DoParallelLoop(threads, atomicAlpha, atomicBeta, atomicEval, b1, moves, i, depth, currentDepth, root, line, line.size(), initialAlpha);//####[244]####
            Thread t = new Thread(d);//####[246]####
            threads.add(t);//####[247]####
            t.start();//####[248]####
        }//####[297]####
        for (Thread t : threads) //####[298]####
        {//####[298]####
            try {//####[299]####
                t.join();//####[300]####
            } catch (InterruptedException e) {//####[301]####
                e.printStackTrace();//####[302]####
            }//####[303]####
        }//####[304]####
        if (root && atomicAlpha.get() > initialAlpha) //####[306]####
        {//####[306]####
            updateBestLine(line, depth, atomicAlpha.get());//####[307]####
        }//####[308]####
        return new Integer(atomicAlpha.get());//####[310]####
    }//####[311]####
//####[314]####
    public Move bestMove(String FEN, int depth, int time) {//####[314]####
        return bestMove(FEN, depth, time, false);//####[315]####
    }//####[316]####
//####[318]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[318]####
        Board board = new Board(FEN);//####[319]####
        nodeCounter.set(0);//####[320]####
        ;//####[320]####
        bestMoveTimeLimit = time;//####[321]####
        int eval = 0;//####[323]####
        bestLine = new ArrayList<Move>();//####[324]####
        bestLineDepth = 0;//####[325]####
        bestLineEval = -100000;//####[326]####
        bestMoveStart = System.currentTimeMillis();//####[327]####
        int currentDepth = 1;//####[328]####
        int alpha = -1000000;//####[329]####
        int beta = 1000000;//####[330]####
        while (true) //####[331]####
        {//####[331]####
            if (currentDepth == 1) //####[333]####
            {//####[333]####
                ArrayList<Move> moves = board.generateAllMoves();//####[334]####
                if (moves.size() == 1) //####[335]####
                {//####[335]####
                    bestLine = new ArrayList<Move>();//####[336]####
                    bestLine.add(moves.get(0));//####[337]####
                    break;//####[338]####
                }//####[339]####
            }//####[340]####
            eval = PVSplit(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[341]####
            if (eval == 1234567890) //####[343]####
            break;//####[344]####
            if (eval <= alpha || eval >= beta) //####[345]####
            {//####[345]####
                alpha = -1000000;//####[346]####
                beta = 1000000;//####[347]####
                continue;//####[348]####
            }//####[349]####
            alpha = eval - WINDOW;//####[350]####
            beta = eval + WINDOW;//####[351]####
            currentDepth++;//####[353]####
            if (currentDepth > depth) //####[354]####
            break;//####[355]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[356]####
            break;//####[357]####
        }//####[358]####
        if (bestLine.size() == 0) //####[361]####
        {//####[361]####
            ArrayList<Move> moves = board.generateAllMoves();//####[362]####
            bestLine.add(moves.get(0));//####[363]####
        }//####[364]####
        System.out.println("ENGINE" + " : " + "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[366]####
        return bestLine.get(0);//####[367]####
    }//####[368]####
//####[372]####
    private synchronized void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[372]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[373]####
        return;//####[374]####
        bestLineDepth = depth;//####[375]####
        bestLineEval = eval;//####[376]####
        bestLine = line;//####[377]####
        String s = bestLineDepth + " : ";//####[379]####
        for (int i = 0; i < bestLine.size(); i++) //####[380]####
        {//####[380]####
            if (i == bestLineDepth) //####[381]####
            s += "| ";//####[382]####
            s += bestLine.get(i).toString() + " ";//####[383]####
        }//####[384]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[385]####
        System.out.println(TAG + " : " + s);//####[387]####
    }//####[388]####
//####[392]####
    private class MoveComparator implements Comparator<Move> {//####[392]####
//####[392]####
        /*  ParaTask helper method to access private/protected slots *///####[392]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[392]####
            if (m.getParameterTypes().length == 0)//####[392]####
                m.invoke(instance);//####[392]####
            else if ((m.getParameterTypes().length == 1))//####[392]####
                m.invoke(instance, arg);//####[392]####
            else //####[392]####
                m.invoke(instance, arg, interResult);//####[392]####
        }//####[392]####
//####[394]####
        public int ply;//####[394]####
//####[396]####
        public int compare(Move move1, Move move2) {//####[396]####
            int moveEval1 = moveEval(move1);//####[397]####
            int moveEval2 = moveEval(move2);//####[398]####
            if (moveEval1 > moveEval2) //####[399]####
            return -1; else if (moveEval2 > moveEval1) //####[401]####
            return 1; else return 0;//####[402]####
        }//####[405]####
//####[407]####
        private int moveEval(Move move) {//####[407]####
            if (bestLine != null && bestLine.size() >= ply) //####[408]####
            {//####[408]####
                Move lastBest = bestLine.get(ply - 1);//####[409]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[410]####
                return 100000;//####[412]####
            }//####[413]####
            if (move.capture == 0) //####[427]####
            return 0; else {//####[429]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[430]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[431]####
                return capturePrice - piecePrice + 2000;//####[432]####
            }//####[433]####
        }//####[434]####
    }//####[434]####
//####[437]####
    private class DoParallelLoop implements Runnable {//####[437]####
//####[437]####
        /*  ParaTask helper method to access private/protected slots *///####[437]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[437]####
            if (m.getParameterTypes().length == 0)//####[437]####
                m.invoke(instance);//####[437]####
            else if ((m.getParameterTypes().length == 1))//####[437]####
                m.invoke(instance, arg);//####[437]####
            else //####[437]####
                m.invoke(instance, arg, interResult);//####[437]####
        }//####[437]####
//####[438]####
        ArrayList<Thread> otherThreads;//####[438]####
//####[439]####
        AtomicInteger alpha;//####[439]####
//####[440]####
        AtomicInteger beta;//####[440]####
//####[441]####
        AtomicInteger result;//####[441]####
//####[442]####
        Board b1;//####[442]####
//####[443]####
        ArrayList<Move> locLine;//####[443]####
//####[444]####
        ArrayList<Move> moves;//####[444]####
//####[445]####
        int index;//####[445]####
//####[446]####
        int depth;//####[446]####
//####[447]####
        int currentDepth;//####[447]####
//####[448]####
        ArrayList<Move> line;//####[448]####
//####[449]####
        int initialLineSize;//####[449]####
//####[450]####
        boolean root;//####[450]####
//####[451]####
        int initialAlpha;//####[451]####
//####[452]####
        private DoParallelLoop(ArrayList<Thread> otherThreads, AtomicInteger alpha, AtomicInteger beta, AtomicInteger eval, Board board, ArrayList<Move> moves, int index, int depth, int currentDepth, boolean root, ArrayList<Move> line, int intialLineSize, int initialAlpha) {//####[454]####
            this.otherThreads = otherThreads;//####[455]####
            this.alpha = alpha;//####[456]####
            this.beta = beta;//####[457]####
            this.result = eval;//####[458]####
            this.b1 = board;//####[459]####
            this.locLine = new ArrayList<Move>();//####[460]####
            this.moves = moves;//####[461]####
            this.index = index;//####[462]####
            this.depth = depth;//####[463]####
            this.currentDepth = currentDepth;//####[464]####
            this.root = root;//####[465]####
            this.line = line;//####[466]####
            this.initialLineSize = intialLineSize;//####[467]####
            this.initialAlpha = initialAlpha;//####[468]####
        }//####[469]####
//####[470]####
        public void run() {//####[470]####
            locLine.clear();//####[471]####
            int eval;//####[472]####
            b1.doMove(moves.get(index));//####[474]####
            if (b1.isRepetition()) //####[475]####
            eval = -50; else if (b1.isDraw50Move()) //####[477]####
            eval = -50; else {//####[479]####
                if (index >= 4 && currentDepth - depth >= 2 && !b1.inCheck(b1.toMove) && moves.get(index).capture == 0) //####[480]####
                {//####[482]####
                    eval = -alphaBeta(depth - 2, -alpha.get() - 1, -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 2);//####[483]####
                    if (eval > alpha.get()) //####[485]####
                    {//####[485]####
                        eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[486]####
                    }//####[488]####
                } else {//####[489]####
                    eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[490]####
                }//####[492]####
            }//####[493]####
            b1.undoMove(moves.get(index));//####[494]####
            if (eval == -1234567890) //####[495]####
            result.set(1234567890);//####[497]####
            stop();//####[498]####
            if (eval >= beta.get()) //####[501]####
            {//####[501]####
                result.set(1234567890);//####[503]####
                stop();//####[504]####
            }//####[507]####
            if (eval > alpha.get()) //####[509]####
            {//####[509]####
                alpha.set(eval);//####[510]####
                line.subList(initialLineSize, line.size()).clear();//####[511]####
                line.add(moves.get(index));//####[512]####
                line.addAll(locLine);//####[513]####
            }//####[515]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[518]####
            {//####[521]####
                updateBestLine(line, depth, eval);//####[522]####
            }//####[523]####
            result.set(alpha.get());//####[524]####
        }//####[525]####
//####[527]####
        private void stop() {//####[527]####
        }//####[531]####
    }//####[531]####
}//####[531]####
