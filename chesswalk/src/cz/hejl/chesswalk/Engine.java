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
//####[34]####
//-- ParaTask related imports//####[34]####
import pt.runtime.*;//####[34]####
import java.util.concurrent.ExecutionException;//####[34]####
import java.util.concurrent.locks.*;//####[34]####
import java.lang.reflect.*;//####[34]####
import pt.runtime.GuiThread;//####[34]####
import java.util.concurrent.BlockingQueue;//####[34]####
import java.util.ArrayList;//####[34]####
import java.util.List;//####[34]####
//####[34]####
public class Engine {//####[36]####
    static{ParaTask.init();}//####[36]####
    /*  ParaTask helper method to access private/protected slots *///####[36]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[36]####
        if (m.getParameterTypes().length == 0)//####[36]####
            m.invoke(instance);//####[36]####
        else if ((m.getParameterTypes().length == 1))//####[36]####
            m.invoke(instance, arg);//####[36]####
        else //####[36]####
            m.invoke(instance, arg, interResult);//####[36]####
    }//####[36]####
//####[37]####
    public AtomicInteger nodeCounter = new AtomicInteger(0);//####[37]####
//####[39]####
    private static final int WINDOW = 10;//####[39]####
//####[40]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[40]####
//####[42]####
    private static final String TAG = "Engine";//####[42]####
//####[44]####
    private boolean allowNullGlobal = true;//####[44]####
//####[45]####
    public int bestLineDepth;//####[45]####
//####[46]####
    private int bestLineEval;//####[46]####
//####[47]####
    private int bestMoveTimeLimit;//####[47]####
//####[48]####
    private long bestMoveStart;//####[48]####
//####[49]####
    private ArrayList<Move> bestLine;//####[49]####
//####[54]####
    private Integer alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[55]####
        MoveComparator moveComparator = new MoveComparator();//####[57]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[58]####
        return new Integer(1234567890);//####[60]####
        if (allowNullGlobal == false) //####[61]####
        allowNull = false;//####[62]####
        nodeCounter.incrementAndGet();//####[63]####
        int initialAlpha = alpha;//####[64]####
        int initialLineSize = line.size();//####[66]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[67]####
        ArrayList<Move> moves = null;//####[68]####
        moves = board.generateAllMoves();//####[69]####
        moveComparator.ply = currentDepth - depth + 1;//####[70]####
        Collections.sort(moves, moveComparator);//####[71]####
        if (depth <= 0) //####[73]####
        {//####[73]####
            int eval = board.evaluate();//####[74]####
            if (eval >= beta) //####[75]####
            return new Integer(beta);//####[76]####
            if (eval > alpha) //####[77]####
            alpha = eval;//####[78]####
            int capturesN = 0;//####[80]####
            for (int i = 0; i < moves.size(); i++) //####[81]####
            {//####[81]####
                if (moves.get(i).capture == 0) //####[82]####
                break;//####[83]####
                capturesN++;//####[84]####
            }//####[85]####
            moves.subList(capturesN, moves.size()).clear();//####[86]####
        }//####[87]####
        if (moves.size() == 0) //####[89]####
        return board.evaluate();//####[90]####
        if (allowNull && depth > 0) //####[93]####
        {//####[93]####
            if (!board.inCheck(board.toMove)) //####[94]####
            {//####[94]####
                board.toMove *= -1;//####[95]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false, board, currentDepth + 1);//####[96]####
                board.toMove *= -1;//####[99]####
                if (eval == -1234567890) //####[100]####
                return new Integer(1234567890);//####[101]####
                if (eval >= beta) //####[103]####
                {//####[103]####
                    return new Integer(beta);//####[104]####
                }//####[105]####
            }//####[106]####
        }//####[107]####
        for (int i = 0; i < moves.size(); i++) //####[109]####
        {//####[109]####
            locLine.clear();//####[110]####
            int eval;//####[111]####
            board.doMove(moves.get(i));//####[113]####
            if (board.isRepetition()) //####[114]####
            eval = -50; else if (board.isDraw50Move()) //####[116]####
            eval = -50; else {//####[118]####
                if (i >= 4 && currentDepth - depth >= 2 && !board.inCheck(board.toMove) && moves.get(i).capture == 0) //####[119]####
                {//####[121]####
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, board, currentDepth + 2);//####[122]####
                    if (eval > alpha) //####[124]####
                    {//####[124]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[125]####
                    }//####[127]####
                } else {//####[128]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[129]####
                }//####[131]####
            }//####[132]####
            board.undoMove(moves.get(i));//####[133]####
            if (eval == -1234567890) //####[134]####
            return new Integer(1234567890);//####[135]####
            if (eval >= beta) //####[137]####
            {//####[137]####
                return new Integer(beta);//####[138]####
            }//####[139]####
            if (eval > alpha) //####[141]####
            {//####[141]####
                alpha = eval;//####[142]####
                line.subList(initialLineSize, line.size()).clear();//####[144]####
                line.add(moves.get(i));//####[145]####
                line.addAll(locLine);//####[146]####
            }//####[147]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[150]####
            {//####[153]####
                updateBestLine(line, depth, eval);//####[154]####
            }//####[155]####
        }//####[156]####
        if (root && alpha > initialAlpha) //####[158]####
        {//####[158]####
            updateBestLine(line, depth, alpha);//####[159]####
        }//####[160]####
        return new Integer(alpha);//####[162]####
    }//####[163]####
//####[166]####
    private Integer PVSplit(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[167]####
        MoveComparator moveComparator = new MoveComparator();//####[169]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[170]####
        return new Integer(1234567890);//####[172]####
        if (allowNullGlobal == false) //####[173]####
        allowNull = false;//####[174]####
        int initialAlpha = alpha;//####[175]####
        nodeCounter.incrementAndGet();//####[176]####
        int initialLineSize = line.size();//####[177]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[178]####
        ArrayList<Move> moves = null;//####[179]####
        moves = board.generateAllMoves();//####[180]####
        moveComparator.ply = currentDepth - depth + 1;//####[181]####
        Collections.sort(moves, moveComparator);//####[182]####
        if (depth <= 0) //####[184]####
        {//####[184]####
            int eval = board.evaluate();//####[185]####
            if (eval >= beta) //####[186]####
            return new Integer(beta);//####[187]####
            if (eval > alpha) //####[188]####
            alpha = eval;//####[189]####
            int capturesN = 0;//####[191]####
            for (int i = 0; i < moves.size(); i++) //####[192]####
            {//####[192]####
                if (moves.get(i).capture == 0) //####[193]####
                break;//####[194]####
                capturesN++;//####[195]####
            }//####[196]####
            moves.subList(capturesN, moves.size()).clear();//####[197]####
        }//####[198]####
        if (moves.size() == 0) //####[200]####
        return board.evaluate();//####[201]####
        board.doMove(moves.get(0));//####[203]####
        locLine.clear();//####[204]####
        int evalOne;//####[205]####
        if (board.isRepetition()) //####[207]####
        evalOne = -50; else if (board.isDraw50Move()) //####[209]####
        evalOne = -50; else {//####[211]####
            evalOne = -PVSplit(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[212]####
        }//####[214]####
        board.undoMove(moves.get(0));//####[215]####
        if (evalOne == -1234567890) //####[216]####
        return new Integer(1234567890);//####[217]####
        if (evalOne >= beta) //####[219]####
        {//####[219]####
            return new Integer(beta);//####[220]####
        }//####[221]####
        if (evalOne > alpha) //####[223]####
        {//####[223]####
            alpha = evalOne;//####[224]####
            line.subList(initialLineSize, line.size()).clear();//####[225]####
            line.add(moves.get(0));//####[226]####
            line.addAll(locLine);//####[227]####
        }//####[228]####
        if (root && (evalOne > bestLineEval || evalOne == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[231]####
        {//####[234]####
            updateBestLine(line, depth, evalOne);//####[235]####
        }//####[236]####
        AtomicInteger atomicAlpha = new AtomicInteger(alpha);//####[239]####
        AtomicInteger atomicBeta = new AtomicInteger(beta);//####[240]####
        AtomicInteger atomicEval = new AtomicInteger(0);//####[241]####
        TaskIDGroup<Void> g = new TaskIDGroup<Void>(moves.size());//####[242]####
        for (int i = 1; i < moves.size(); i++) //####[243]####
        {//####[243]####
            Board b1 = new Board(board);//####[244]####
            DoParallelLoop d = new DoParallelLoop(atomicAlpha, atomicBeta, atomicEval, b1, moves, i, depth, currentDepth, root, line, line.size(), initialAlpha);//####[245]####
            TaskID<Void> id = runParallelLoop(d);//####[247]####
            ;//####[247]####
            g.add(id);//####[248]####
        }//####[249]####
        try {//####[250]####
            g.waitTillFinished();//####[251]####
        } catch (InterruptedException e) {//####[252]####
            e.printStackTrace();//####[253]####
        } catch (ExecutionException e) {//####[254]####
            e.printStackTrace();//####[255]####
        }//####[256]####
        if (root && atomicAlpha.get() > initialAlpha) //####[259]####
        {//####[259]####
            updateBestLine(line, depth, atomicAlpha.get());//####[260]####
        }//####[261]####
        return new Integer(atomicAlpha.get());//####[263]####
    }//####[264]####
//####[267]####
    public Move bestMove(String FEN, int depth, int time) {//####[267]####
        return bestMove(FEN, depth, time, false);//####[268]####
    }//####[269]####
//####[271]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[271]####
        Board board = new Board(FEN);//####[272]####
        nodeCounter.set(0);//####[273]####
        ;//####[273]####
        bestMoveTimeLimit = time;//####[274]####
        int eval = 0;//####[276]####
        bestLine = new ArrayList<Move>();//####[277]####
        bestLineDepth = 0;//####[278]####
        bestLineEval = -100000;//####[279]####
        bestMoveStart = System.currentTimeMillis();//####[280]####
        int currentDepth = 1;//####[281]####
        int alpha = -1000000;//####[282]####
        int beta = 1000000;//####[283]####
        while (true) //####[284]####
        {//####[284]####
            if (currentDepth == 1) //####[286]####
            {//####[286]####
                ArrayList<Move> moves = board.generateAllMoves();//####[287]####
                if (moves.size() == 1) //####[288]####
                {//####[288]####
                    bestLine = new ArrayList<Move>();//####[289]####
                    bestLine.add(moves.get(0));//####[290]####
                    break;//####[291]####
                }//####[292]####
            }//####[293]####
            eval = PVSplit(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[294]####
            if (eval == 1234567890) //####[296]####
            break;//####[297]####
            if (eval <= alpha || eval >= beta) //####[298]####
            {//####[298]####
                alpha = -1000000;//####[299]####
                beta = 1000000;//####[300]####
                continue;//####[301]####
            }//####[302]####
            alpha = eval - WINDOW;//####[303]####
            beta = eval + WINDOW;//####[304]####
            currentDepth++;//####[306]####
            if (currentDepth > depth) //####[307]####
            break;//####[308]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[309]####
            break;//####[310]####
        }//####[311]####
        if (bestLine.size() == 0) //####[314]####
        {//####[314]####
            ArrayList<Move> moves = board.generateAllMoves();//####[315]####
            bestLine.add(moves.get(0));//####[316]####
        }//####[317]####
        System.out.println("ENGINE" + " : " + "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[319]####
        return bestLine.get(0);//####[320]####
    }//####[321]####
//####[325]####
    private synchronized void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[325]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[326]####
        return;//####[327]####
        bestLineDepth = depth;//####[328]####
        bestLineEval = eval;//####[329]####
        bestLine = line;//####[330]####
        String s = bestLineDepth + " : ";//####[332]####
        for (int i = 0; i < bestLine.size(); i++) //####[333]####
        {//####[333]####
            if (i == bestLineDepth) //####[334]####
            s += "| ";//####[335]####
            s += bestLine.get(i).toString() + " ";//####[336]####
        }//####[337]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[338]####
        System.out.println(TAG + " : " + s);//####[340]####
    }//####[341]####
//####[345]####
    private class MoveComparator implements Comparator<Move> {//####[345]####
//####[345]####
        /*  ParaTask helper method to access private/protected slots *///####[345]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[345]####
            if (m.getParameterTypes().length == 0)//####[345]####
                m.invoke(instance);//####[345]####
            else if ((m.getParameterTypes().length == 1))//####[345]####
                m.invoke(instance, arg);//####[345]####
            else //####[345]####
                m.invoke(instance, arg, interResult);//####[345]####
        }//####[345]####
//####[347]####
        public int ply;//####[347]####
//####[349]####
        public int compare(Move move1, Move move2) {//####[349]####
            int moveEval1 = moveEval(move1);//####[350]####
            int moveEval2 = moveEval(move2);//####[351]####
            if (moveEval1 > moveEval2) //####[352]####
            return -1; else if (moveEval2 > moveEval1) //####[354]####
            return 1; else return 0;//####[355]####
        }//####[358]####
//####[360]####
        private int moveEval(Move move) {//####[360]####
            if (bestLine != null && bestLine.size() >= ply) //####[361]####
            {//####[361]####
                Move lastBest = bestLine.get(ply - 1);//####[362]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[363]####
                return 100000;//####[365]####
            }//####[366]####
            if (move.capture == 0) //####[380]####
            return 0; else {//####[382]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[383]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[384]####
                return capturePrice - piecePrice + 2000;//####[385]####
            }//####[386]####
        }//####[387]####
    }//####[387]####
//####[390]####
    private static volatile Method __pt__runParallelLoop_DoParallelLoop_method = null;//####[390]####
    private synchronized static void __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet() {//####[390]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[390]####
            try {//####[390]####
                __pt__runParallelLoop_DoParallelLoop_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__runParallelLoop", new Class[] {//####[390]####
                    DoParallelLoop.class//####[390]####
                });//####[390]####
            } catch (Exception e) {//####[390]####
                e.printStackTrace();//####[390]####
            }//####[390]####
        }//####[390]####
    }//####[390]####
    private TaskID<Void> runParallelLoop(DoParallelLoop l) {//####[390]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[390]####
        return runParallelLoop(l, new TaskInfo());//####[390]####
    }//####[390]####
    private TaskID<Void> runParallelLoop(DoParallelLoop l, TaskInfo taskinfo) {//####[390]####
        // ensure Method variable is set//####[390]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[390]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[390]####
        }//####[390]####
        taskinfo.setParameters(l);//####[390]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[390]####
        taskinfo.setInstance(this);//####[390]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[390]####
    }//####[390]####
    private TaskID<Void> runParallelLoop(TaskID<DoParallelLoop> l) {//####[390]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[390]####
        return runParallelLoop(l, new TaskInfo());//####[390]####
    }//####[390]####
    private TaskID<Void> runParallelLoop(TaskID<DoParallelLoop> l, TaskInfo taskinfo) {//####[390]####
        // ensure Method variable is set//####[390]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[390]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[390]####
        }//####[390]####
        taskinfo.setTaskIdArgIndexes(0);//####[390]####
        taskinfo.addDependsOn(l);//####[390]####
        taskinfo.setParameters(l);//####[390]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[390]####
        taskinfo.setInstance(this);//####[390]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[390]####
    }//####[390]####
    private TaskID<Void> runParallelLoop(BlockingQueue<DoParallelLoop> l) {//####[390]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[390]####
        return runParallelLoop(l, new TaskInfo());//####[390]####
    }//####[390]####
    private TaskID<Void> runParallelLoop(BlockingQueue<DoParallelLoop> l, TaskInfo taskinfo) {//####[390]####
        // ensure Method variable is set//####[390]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[390]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[390]####
        }//####[390]####
        taskinfo.setQueueArgIndexes(0);//####[390]####
        taskinfo.setIsPipeline(true);//####[390]####
        taskinfo.setParameters(l);//####[390]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[390]####
        taskinfo.setInstance(this);//####[390]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[390]####
    }//####[390]####
    public void __pt__runParallelLoop(DoParallelLoop l) {//####[390]####
        l.run();//####[391]####
    }//####[392]####
//####[392]####
//####[393]####
    private class DoParallelLoop {//####[393]####
//####[393]####
        /*  ParaTask helper method to access private/protected slots *///####[393]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[393]####
            if (m.getParameterTypes().length == 0)//####[393]####
                m.invoke(instance);//####[393]####
            else if ((m.getParameterTypes().length == 1))//####[393]####
                m.invoke(instance, arg);//####[393]####
            else //####[393]####
                m.invoke(instance, arg, interResult);//####[393]####
        }//####[393]####
//####[395]####
        AtomicInteger alpha;//####[395]####
//####[396]####
        AtomicInteger beta;//####[396]####
//####[397]####
        AtomicInteger result;//####[397]####
//####[398]####
        Board b1;//####[398]####
//####[399]####
        ArrayList<Move> locLine;//####[399]####
//####[400]####
        ArrayList<Move> moves;//####[400]####
//####[401]####
        int index;//####[401]####
//####[402]####
        int depth;//####[402]####
//####[403]####
        int currentDepth;//####[403]####
//####[404]####
        ArrayList<Move> line;//####[404]####
//####[405]####
        int initialLineSize;//####[405]####
//####[406]####
        boolean root;//####[406]####
//####[407]####
        int initialAlpha;//####[407]####
//####[408]####
        private DoParallelLoop(AtomicInteger alpha, AtomicInteger beta, AtomicInteger eval, Board board, ArrayList<Move> moves, int index, int depth, int currentDepth, boolean root, ArrayList<Move> line, int intialLineSize, int initialAlpha) {//####[410]####
            this.alpha = alpha;//####[412]####
            this.beta = beta;//####[413]####
            this.result = eval;//####[414]####
            this.b1 = board;//####[415]####
            this.locLine = new ArrayList<Move>();//####[416]####
            this.moves = moves;//####[417]####
            this.index = index;//####[418]####
            this.depth = depth;//####[419]####
            this.currentDepth = currentDepth;//####[420]####
            this.root = root;//####[421]####
            this.line = line;//####[422]####
            this.initialLineSize = intialLineSize;//####[423]####
            this.initialAlpha = initialAlpha;//####[424]####
        }//####[425]####
//####[426]####
        public void run() {//####[426]####
            locLine.clear();//####[427]####
            int eval;//####[428]####
            b1.doMove(moves.get(index));//####[430]####
            if (b1.isRepetition()) //####[431]####
            eval = -50; else if (b1.isDraw50Move()) //####[433]####
            eval = -50; else {//####[435]####
                if (index >= 4 && currentDepth - depth >= 2 && !b1.inCheck(b1.toMove) && moves.get(index).capture == 0) //####[436]####
                {//####[438]####
                    eval = -alphaBeta(depth - 2, -alpha.get() - 1, -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 2);//####[439]####
                    if (eval > alpha.get()) //####[441]####
                    {//####[441]####
                        eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[442]####
                    }//####[444]####
                } else {//####[445]####
                    eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[446]####
                }//####[448]####
            }//####[449]####
            b1.undoMove(moves.get(index));//####[450]####
            if (eval == -1234567890) //####[451]####
            result.set(1234567890);//####[453]####
            stop();//####[454]####
            if (eval >= beta.get()) //####[457]####
            {//####[457]####
                result.set(1234567890);//####[459]####
                stop();//####[460]####
            }//####[463]####
            if (eval > alpha.get()) //####[465]####
            {//####[465]####
                alpha.set(eval);//####[466]####
                line.subList(initialLineSize, line.size()).clear();//####[467]####
                line.add(moves.get(index));//####[468]####
                line.addAll(locLine);//####[469]####
            }//####[471]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[474]####
            {//####[477]####
                updateBestLine(line, depth, eval);//####[478]####
            }//####[479]####
            result.set(alpha.get());//####[480]####
        }//####[481]####
//####[483]####
        private void stop() {//####[483]####
        }//####[487]####
    }//####[487]####
}//####[487]####
