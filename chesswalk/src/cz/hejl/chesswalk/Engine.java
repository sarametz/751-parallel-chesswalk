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
import pt.runtime.TaskIDGroup;//####[32]####
import android.os.AsyncTask;//####[33]####
import android.util.Log;//####[34]####
import java.util.*;//####[35]####
//####[35]####
//-- ParaTask related imports//####[35]####
import pt.runtime.*;//####[35]####
import java.util.concurrent.ExecutionException;//####[35]####
import java.util.concurrent.locks.*;//####[35]####
import java.lang.reflect.*;//####[35]####
import pt.runtime.GuiThread;//####[35]####
import java.util.concurrent.BlockingQueue;//####[35]####
import java.util.ArrayList;//####[35]####
import java.util.List;//####[35]####
//####[35]####
public class Engine {//####[37]####
    static{ParaTask.init();}//####[37]####
    /*  ParaTask helper method to access private/protected slots *///####[37]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[37]####
        if (m.getParameterTypes().length == 0)//####[37]####
            m.invoke(instance);//####[37]####
        else if ((m.getParameterTypes().length == 1))//####[37]####
            m.invoke(instance, arg);//####[37]####
        else //####[37]####
            m.invoke(instance, arg, interResult);//####[37]####
    }//####[37]####
//####[38]####
    public AtomicInteger nodeCounter = new AtomicInteger(0);//####[38]####
//####[40]####
    private static final int WINDOW = 10;//####[40]####
//####[41]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[41]####
//####[43]####
    private static final String TAG = "Engine";//####[43]####
//####[45]####
    private boolean allowNullGlobal = true;//####[45]####
//####[46]####
    public int bestLineDepth;//####[46]####
//####[47]####
    private int bestLineEval;//####[47]####
//####[48]####
    private int bestMoveTimeLimit;//####[48]####
//####[49]####
    private long bestMoveStart;//####[49]####
//####[50]####
    private ArrayList<Move> bestLine;//####[50]####
//####[54]####
    private Integer alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[55]####
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
        MoveComparator moveComparator = new MoveComparator();//####[167]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[168]####
        return new Integer(1234567890);//####[170]####
        if (allowNullGlobal == false) //####[171]####
        allowNull = false;//####[172]####
        int initialAlpha = alpha;//####[173]####
        nodeCounter.incrementAndGet();//####[174]####
        int initialLineSize = line.size();//####[175]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[176]####
        ArrayList<Move> moves = null;//####[177]####
        moves = board.generateAllMoves();//####[178]####
        moveComparator.ply = currentDepth - depth + 1;//####[179]####
        Collections.sort(moves, moveComparator);//####[180]####
        if (depth <= 0) //####[182]####
        {//####[182]####
            int eval = board.evaluate();//####[183]####
            if (eval >= beta) //####[184]####
            return new Integer(beta);//####[185]####
            if (eval > alpha) //####[186]####
            alpha = eval;//####[187]####
            int capturesN = 0;//####[189]####
            for (int i = 0; i < moves.size(); i++) //####[190]####
            {//####[190]####
                if (moves.get(i).capture == 0) //####[191]####
                break;//####[192]####
                capturesN++;//####[193]####
            }//####[194]####
            moves.subList(capturesN, moves.size()).clear();//####[195]####
        }//####[196]####
        if (moves.size() == 0) //####[198]####
        return board.evaluate();//####[199]####
        board.doMove(moves.get(0));//####[201]####
        locLine.clear();//####[202]####
        int evalOne;//####[203]####
        if (board.isRepetition()) //####[205]####
        evalOne = -50; else if (board.isDraw50Move()) //####[207]####
        evalOne = -50; else {//####[209]####
            evalOne = -PVSplit(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[210]####
        }//####[212]####
        board.undoMove(moves.get(0));//####[213]####
        if (evalOne == -1234567890) //####[214]####
        return new Integer(1234567890);//####[215]####
        if (evalOne >= beta) //####[217]####
        {//####[217]####
            return new Integer(beta);//####[218]####
        }//####[219]####
        if (evalOne > alpha) //####[221]####
        {//####[221]####
            alpha = evalOne;//####[222]####
            line.subList(initialLineSize, line.size()).clear();//####[223]####
            line.add(moves.get(0));//####[224]####
            line.addAll(locLine);//####[225]####
        }//####[226]####
        if (root && (evalOne > bestLineEval || evalOne == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[229]####
        {//####[232]####
            updateBestLine(line, depth, evalOne);//####[233]####
        }//####[234]####
        AtomicInteger atomicAlpha = new AtomicInteger(alpha);//####[237]####
        AtomicInteger atomicBeta = new AtomicInteger(beta);//####[238]####
        AtomicInteger atomicEval = new AtomicInteger(0);//####[239]####
        TaskIDGroup<Void> g = new TaskIDGroup<Void>(moves.size());//####[240]####
        for (int i = 1; i < moves.size(); i++) //####[242]####
        {//####[242]####
            Board b1 = new Board(board);//####[243]####
            DoParallelLoop d = new DoParallelLoop(atomicAlpha, atomicBeta, atomicEval, b1, moves, i, depth, currentDepth, root, line, line.size(), initialAlpha, g);//####[244]####
            TaskID<Void> id = runParallelLoop(d);//####[246]####
            ;//####[246]####
            g.add(id);//####[247]####
        }//####[248]####
        try {//####[249]####
            g.waitTillFinished();//####[250]####
        } catch (InterruptedException e) {//####[251]####
            e.printStackTrace();//####[252]####
        } catch (ExecutionException e) {//####[253]####
            e.printStackTrace();//####[254]####
        }//####[255]####
        if (root && atomicAlpha.get() > initialAlpha) //####[258]####
        {//####[258]####
            updateBestLine(line, depth, atomicAlpha.get());//####[259]####
        }//####[260]####
        return new Integer(atomicAlpha.get());//####[262]####
    }//####[263]####
//####[266]####
    public Move bestMove(String FEN, int depth, int time) {//####[266]####
        return bestMove(FEN, depth, time, false);//####[267]####
    }//####[268]####
//####[270]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[270]####
        Board board = new Board(FEN);//####[271]####
        nodeCounter.set(0);//####[272]####
        ;//####[272]####
        bestMoveTimeLimit = time;//####[273]####
        int eval = 0;//####[275]####
        bestLine = new ArrayList<Move>();//####[276]####
        bestLineDepth = 0;//####[277]####
        bestLineEval = -100000;//####[278]####
        bestMoveStart = System.currentTimeMillis();//####[279]####
        int currentDepth = 1;//####[280]####
        int alpha = -1000000;//####[281]####
        int beta = 1000000;//####[282]####
        while (true) //####[283]####
        {//####[283]####
            if (currentDepth == 1) //####[285]####
            {//####[285]####
                ArrayList<Move> moves = board.generateAllMoves();//####[286]####
                if (moves.size() == 1) //####[287]####
                {//####[287]####
                    bestLine = new ArrayList<Move>();//####[288]####
                    bestLine.add(moves.get(0));//####[289]####
                    break;//####[290]####
                }//####[291]####
            }//####[292]####
            eval = PVSplit(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[293]####
            if (eval == 1234567890) //####[295]####
            break;//####[296]####
            if (eval <= alpha || eval >= beta) //####[297]####
            {//####[297]####
                alpha = -1000000;//####[298]####
                beta = 1000000;//####[299]####
                continue;//####[300]####
            }//####[301]####
            alpha = eval - WINDOW;//####[302]####
            beta = eval + WINDOW;//####[303]####
            currentDepth++;//####[305]####
            if (currentDepth > depth) //####[306]####
            break;//####[307]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[308]####
            break;//####[309]####
        }//####[310]####
        if (bestLine.size() == 0) //####[313]####
        {//####[313]####
            ArrayList<Move> moves = board.generateAllMoves();//####[314]####
            bestLine.add(moves.get(0));//####[315]####
        }//####[316]####
        System.out.println("ENGINE" + " : " + "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[318]####
        return bestLine.get(0);//####[319]####
    }//####[320]####
//####[324]####
    private synchronized void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[324]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[325]####
        return;//####[326]####
        bestLineDepth = depth;//####[327]####
        bestLineEval = eval;//####[328]####
        bestLine = line;//####[329]####
        String s = bestLineDepth + " : ";//####[331]####
        for (int i = 0; i < bestLine.size(); i++) //####[332]####
        {//####[332]####
            if (i == bestLineDepth) //####[333]####
            s += "| ";//####[334]####
            s += bestLine.get(i).toString() + " ";//####[335]####
        }//####[336]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[337]####
        System.out.println(TAG + " : " + s);//####[339]####
    }//####[340]####
//####[344]####
    private class MoveComparator implements Comparator<Move> {//####[344]####
//####[344]####
        /*  ParaTask helper method to access private/protected slots *///####[344]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[344]####
            if (m.getParameterTypes().length == 0)//####[344]####
                m.invoke(instance);//####[344]####
            else if ((m.getParameterTypes().length == 1))//####[344]####
                m.invoke(instance, arg);//####[344]####
            else //####[344]####
                m.invoke(instance, arg, interResult);//####[344]####
        }//####[344]####
//####[346]####
        public int ply;//####[346]####
//####[348]####
        public int compare(Move move1, Move move2) {//####[348]####
            int moveEval1 = moveEval(move1);//####[349]####
            int moveEval2 = moveEval(move2);//####[350]####
            if (moveEval1 > moveEval2) //####[351]####
            return -1; else if (moveEval2 > moveEval1) //####[353]####
            return 1; else return 0;//####[354]####
        }//####[357]####
//####[359]####
        private int moveEval(Move move) {//####[359]####
            if (bestLine != null && bestLine.size() >= ply) //####[360]####
            {//####[360]####
                Move lastBest = bestLine.get(ply - 1);//####[361]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[362]####
                return 100000;//####[364]####
            }//####[365]####
            if (move.capture == 0) //####[367]####
            return 0; else {//####[369]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[370]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[371]####
                return capturePrice - piecePrice + 2000;//####[372]####
            }//####[373]####
        }//####[374]####
    }//####[374]####
//####[377]####
    private static volatile Method __pt__runParallelLoop_DoParallelLoop_method = null;//####[377]####
    private synchronized static void __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet() {//####[377]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[377]####
            try {//####[377]####
                __pt__runParallelLoop_DoParallelLoop_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__runParallelLoop", new Class[] {//####[377]####
                    DoParallelLoop.class//####[377]####
                });//####[377]####
            } catch (Exception e) {//####[377]####
                e.printStackTrace();//####[377]####
            }//####[377]####
        }//####[377]####
    }//####[377]####
    private TaskID<Void> runParallelLoop(DoParallelLoop l) {//####[377]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[377]####
        return runParallelLoop(l, new TaskInfo());//####[377]####
    }//####[377]####
    private TaskID<Void> runParallelLoop(DoParallelLoop l, TaskInfo taskinfo) {//####[377]####
        // ensure Method variable is set//####[377]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[377]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[377]####
        }//####[377]####
        taskinfo.setParameters(l);//####[377]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[377]####
        taskinfo.setInstance(this);//####[377]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[377]####
    }//####[377]####
    private TaskID<Void> runParallelLoop(TaskID<DoParallelLoop> l) {//####[377]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[377]####
        return runParallelLoop(l, new TaskInfo());//####[377]####
    }//####[377]####
    private TaskID<Void> runParallelLoop(TaskID<DoParallelLoop> l, TaskInfo taskinfo) {//####[377]####
        // ensure Method variable is set//####[377]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[377]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[377]####
        }//####[377]####
        taskinfo.setTaskIdArgIndexes(0);//####[377]####
        taskinfo.addDependsOn(l);//####[377]####
        taskinfo.setParameters(l);//####[377]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[377]####
        taskinfo.setInstance(this);//####[377]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[377]####
    }//####[377]####
    private TaskID<Void> runParallelLoop(BlockingQueue<DoParallelLoop> l) {//####[377]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[377]####
        return runParallelLoop(l, new TaskInfo());//####[377]####
    }//####[377]####
    private TaskID<Void> runParallelLoop(BlockingQueue<DoParallelLoop> l, TaskInfo taskinfo) {//####[377]####
        // ensure Method variable is set//####[377]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[377]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[377]####
        }//####[377]####
        taskinfo.setQueueArgIndexes(0);//####[377]####
        taskinfo.setIsPipeline(true);//####[377]####
        taskinfo.setParameters(l);//####[377]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[377]####
        taskinfo.setInstance(this);//####[377]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[377]####
    }//####[377]####
    public void __pt__runParallelLoop(DoParallelLoop l) {//####[377]####
        l.run();//####[378]####
    }//####[379]####
//####[379]####
//####[380]####
    private class DoParallelLoop {//####[380]####
//####[380]####
        /*  ParaTask helper method to access private/protected slots *///####[380]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[380]####
            if (m.getParameterTypes().length == 0)//####[380]####
                m.invoke(instance);//####[380]####
            else if ((m.getParameterTypes().length == 1))//####[380]####
                m.invoke(instance, arg);//####[380]####
            else //####[380]####
                m.invoke(instance, arg, interResult);//####[380]####
        }//####[380]####
//####[381]####
        AtomicInteger alpha;//####[381]####
//####[382]####
        AtomicInteger beta;//####[382]####
//####[383]####
        AtomicInteger result;//####[383]####
//####[384]####
        Board b1;//####[384]####
//####[385]####
        ArrayList<Move> locLine;//####[385]####
//####[386]####
        ArrayList<Move> moves;//####[386]####
//####[387]####
        int index;//####[387]####
//####[388]####
        int depth;//####[388]####
//####[389]####
        int currentDepth;//####[389]####
//####[390]####
        ArrayList<Move> line;//####[390]####
//####[391]####
        int initialLineSize;//####[391]####
//####[392]####
        boolean root;//####[392]####
//####[393]####
        int initialAlpha;//####[393]####
//####[394]####
        TaskIDGroup<Void> g;//####[394]####
//####[396]####
        private DoParallelLoop(AtomicInteger alpha, AtomicInteger beta, AtomicInteger eval, Board board, ArrayList<Move> moves, int index, int depth, int currentDepth, boolean root, ArrayList<Move> line, int intialLineSize, int initialAlpha, TaskIDGroup<Void> g) {//####[398]####
            this.g = g;//####[399]####
            this.alpha = alpha;//####[400]####
            this.beta = beta;//####[401]####
            this.result = eval;//####[402]####
            this.b1 = board;//####[403]####
            this.locLine = new ArrayList<Move>();//####[404]####
            this.moves = moves;//####[405]####
            this.index = index;//####[406]####
            this.depth = depth;//####[407]####
            this.currentDepth = currentDepth;//####[408]####
            this.root = root;//####[409]####
            this.line = line;//####[410]####
            this.initialLineSize = intialLineSize;//####[411]####
            this.initialAlpha = initialAlpha;//####[412]####
        }//####[413]####
//####[414]####
        public void run() {//####[414]####
            locLine.clear();//####[415]####
            int eval;//####[416]####
            b1.doMove(moves.get(index));//####[418]####
            if (b1.isRepetition()) //####[419]####
            eval = -50; else if (b1.isDraw50Move()) //####[421]####
            eval = -50; else {//####[423]####
                if (index >= 4 && currentDepth - depth >= 2 && !b1.inCheck(b1.toMove) && moves.get(index).capture == 0) //####[424]####
                {//####[426]####
                    eval = -alphaBeta(depth - 2, -alpha.get() - 1, -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 2);//####[427]####
                    if (eval > alpha.get()) //####[429]####
                    {//####[429]####
                        eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[430]####
                    }//####[432]####
                } else {//####[433]####
                    eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[434]####
                }//####[436]####
            }//####[437]####
            b1.undoMove(moves.get(index));//####[438]####
            if (eval == -1234567890) //####[439]####
            result.set(1234567890);//####[441]####
            stop();//####[442]####
            if (eval >= beta.get()) //####[445]####
            {//####[445]####
                result.set(1234567890);//####[447]####
                stop();//####[448]####
            }//####[449]####
            if (eval > alpha.get()) //####[451]####
            {//####[451]####
                alpha.set(eval);//####[452]####
                line.subList(initialLineSize, line.size()).clear();//####[453]####
                line.add(moves.get(index));//####[454]####
                line.addAll(locLine);//####[455]####
            }//####[457]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[460]####
            {//####[463]####
                updateBestLine(line, depth, eval);//####[464]####
            }//####[465]####
            result.set(alpha.get());//####[466]####
        }//####[467]####
//####[469]####
        private synchronized void stop() {//####[469]####
            if (!g.hasCompleted()) //####[470]####
            {//####[470]####
                while (g.groupMembers().hasNext()) //####[471]####
                {//####[472]####
                    g.groupMembers().next().cancelAttempt();//####[473]####
                }//####[474]####
            }//####[475]####
        }//####[476]####
    }//####[476]####
}//####[476]####
