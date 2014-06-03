package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.ArrayList;//####[23]####
import java.util.Collections;//####[24]####
import java.util.Comparator;//####[25]####
import java.util.concurrent.ExecutionException;//####[26]####
import java.util.concurrent.atomic.AtomicInteger;//####[27]####
import pt.runtime.TaskID;//####[29]####
import pt.runtime.TaskIDGroup;//####[30]####
//####[30]####
//-- ParaTask related imports//####[30]####
import pt.runtime.*;//####[30]####
import java.util.concurrent.ExecutionException;//####[30]####
import java.util.concurrent.locks.*;//####[30]####
import java.lang.reflect.*;//####[30]####
import pt.runtime.GuiThread;//####[30]####
import java.util.concurrent.BlockingQueue;//####[30]####
import java.util.ArrayList;//####[30]####
import java.util.List;//####[30]####
//####[30]####
public class Engine {//####[32]####
    static{ParaTask.init();}//####[32]####
    /*  ParaTask helper method to access private/protected slots *///####[32]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[32]####
        if (m.getParameterTypes().length == 0)//####[32]####
            m.invoke(instance);//####[32]####
        else if ((m.getParameterTypes().length == 1))//####[32]####
            m.invoke(instance, arg);//####[32]####
        else //####[32]####
            m.invoke(instance, arg, interResult);//####[32]####
    }//####[32]####
//####[33]####
    public AtomicInteger nodeCounter = new AtomicInteger(0);//####[33]####
//####[35]####
    private static final int WINDOW = 10;//####[35]####
//####[36]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[36]####
//####[38]####
    private static final String TAG = "Engine";//####[38]####
//####[40]####
    private boolean allowNullGlobal = true;//####[40]####
//####[41]####
    public int bestLineDepth;//####[41]####
//####[42]####
    private int bestLineEval;//####[42]####
//####[43]####
    private int bestMoveTimeLimit;//####[43]####
//####[44]####
    private long bestMoveStart;//####[44]####
//####[45]####
    private ArrayList<Move> bestLine;//####[45]####
//####[49]####
    private Integer alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[50]####
        MoveComparator moveComparator = new MoveComparator();//####[51]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[52]####
        return new Integer(1234567890);//####[54]####
        if (allowNullGlobal == false) //####[55]####
        allowNull = false;//####[56]####
        nodeCounter.incrementAndGet();//####[57]####
        int initialAlpha = alpha;//####[58]####
        int initialLineSize = line.size();//####[60]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[61]####
        ArrayList<Move> moves = null;//####[62]####
        moves = board.generateAllMoves();//####[63]####
        moveComparator.ply = currentDepth - depth + 1;//####[64]####
        Collections.sort(moves, moveComparator);//####[65]####
        if (depth <= 0) //####[67]####
        {//####[67]####
            int eval = board.evaluate();//####[68]####
            if (eval >= beta) //####[69]####
            return new Integer(beta);//####[70]####
            if (eval > alpha) //####[71]####
            alpha = eval;//####[72]####
            int capturesN = 0;//####[74]####
            for (int i = 0; i < moves.size(); i++) //####[75]####
            {//####[75]####
                if (moves.get(i).capture == 0) //####[76]####
                break;//####[77]####
                capturesN++;//####[78]####
            }//####[79]####
            moves.subList(capturesN, moves.size()).clear();//####[80]####
        }//####[81]####
        if (moves.size() == 0) //####[83]####
        return board.evaluate();//####[84]####
        if (allowNull && depth > 0) //####[87]####
        {//####[87]####
            if (!board.inCheck(board.toMove)) //####[88]####
            {//####[88]####
                board.toMove *= -1;//####[89]####
                int eval = -alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false, board, currentDepth + 1);//####[90]####
                board.toMove *= -1;//####[93]####
                if (eval == -1234567890) //####[94]####
                return new Integer(1234567890);//####[95]####
                if (eval >= beta) //####[97]####
                {//####[97]####
                    return new Integer(beta);//####[98]####
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
                    eval = -alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true, board, currentDepth + 2);//####[116]####
                    if (eval > alpha) //####[118]####
                    {//####[118]####
                        eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[119]####
                    }//####[121]####
                } else {//####[122]####
                    eval = -alphaBeta(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[123]####
                }//####[125]####
            }//####[126]####
            board.undoMove(moves.get(i));//####[127]####
            if (eval == -1234567890) //####[128]####
            return new Integer(1234567890);//####[129]####
            if (eval >= beta) //####[131]####
            {//####[131]####
                return new Integer(beta);//####[132]####
            }//####[133]####
            if (eval > alpha) //####[135]####
            {//####[135]####
                alpha = eval;//####[136]####
                line.subList(initialLineSize, line.size()).clear();//####[138]####
                line.add(moves.get(i));//####[139]####
                line.addAll(locLine);//####[140]####
            }//####[141]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[144]####
            {//####[147]####
                updateBestLine(line, depth, eval);//####[148]####
            }//####[149]####
        }//####[150]####
        if (root && alpha > initialAlpha) //####[152]####
        {//####[152]####
            updateBestLine(line, depth, alpha);//####[153]####
        }//####[154]####
        return new Integer(alpha);//####[156]####
    }//####[157]####
//####[160]####
    private Integer PVSplit(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull, Board board, int currentDepth) {//####[161]####
        MoveComparator moveComparator = new MoveComparator();//####[162]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[163]####
        return new Integer(1234567890);//####[165]####
        if (allowNullGlobal == false) //####[166]####
        allowNull = false;//####[167]####
        int initialAlpha = alpha;//####[168]####
        nodeCounter.incrementAndGet();//####[169]####
        int initialLineSize = line.size();//####[170]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[171]####
        ArrayList<Move> moves = null;//####[172]####
        moves = board.generateAllMoves();//####[173]####
        moveComparator.ply = currentDepth - depth + 1;//####[174]####
        Collections.sort(moves, moveComparator);//####[175]####
        if (depth <= 0) //####[177]####
        {//####[177]####
            int eval = board.evaluate();//####[178]####
            if (eval >= beta) //####[179]####
            return new Integer(beta);//####[180]####
            if (eval > alpha) //####[181]####
            alpha = eval;//####[182]####
            int capturesN = 0;//####[184]####
            for (int i = 0; i < moves.size(); i++) //####[185]####
            {//####[185]####
                if (moves.get(i).capture == 0) //####[186]####
                break;//####[187]####
                capturesN++;//####[188]####
            }//####[189]####
            moves.subList(capturesN, moves.size()).clear();//####[190]####
        }//####[191]####
        if (moves.size() == 0) //####[193]####
        return board.evaluate();//####[194]####
        board.doMove(moves.get(0));//####[196]####
        locLine.clear();//####[197]####
        int evalOne;//####[198]####
        if (board.isRepetition()) //####[200]####
        evalOne = -50; else if (board.isDraw50Move()) //####[202]####
        evalOne = -50; else {//####[204]####
            evalOne = -PVSplit(depth - 1, -beta, -alpha, locLine, false, true, board, currentDepth + 1);//####[205]####
        }//####[207]####
        board.undoMove(moves.get(0));//####[208]####
        if (evalOne == -1234567890) //####[209]####
        return new Integer(1234567890);//####[210]####
        if (evalOne >= beta) //####[212]####
        {//####[212]####
            return new Integer(beta);//####[213]####
        }//####[214]####
        if (evalOne > alpha) //####[216]####
        {//####[216]####
            alpha = evalOne;//####[217]####
            line.subList(initialLineSize, line.size()).clear();//####[218]####
            line.add(moves.get(0));//####[219]####
            line.addAll(locLine);//####[220]####
        }//####[221]####
        if (root && (evalOne > bestLineEval || evalOne == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[224]####
        {//####[227]####
            updateBestLine(line, depth, evalOne);//####[228]####
        }//####[229]####
        AtomicInteger atomicAlpha = new AtomicInteger(alpha);//####[232]####
        AtomicInteger atomicBeta = new AtomicInteger(beta);//####[233]####
        AtomicInteger atomicEval = new AtomicInteger(0);//####[234]####
        TaskIDGroup<Void> g = new TaskIDGroup<Void>(moves.size());//####[235]####
        for (int i = 1; i < moves.size(); i++) //####[237]####
        {//####[237]####
            Board b1 = new Board(board);//####[238]####
            DoParallelLoop d = new DoParallelLoop(atomicAlpha, atomicBeta, atomicEval, b1, moves, i, depth, currentDepth, root, line, line.size(), initialAlpha);//####[239]####
            TaskID<Void> id = runParallelLoop(d);//####[241]####
            ;//####[241]####
            g.add(id);//####[242]####
        }//####[243]####
        try {//####[244]####
            g.waitTillFinished();//####[245]####
        } catch (InterruptedException e) {//####[246]####
            e.printStackTrace();//####[247]####
        } catch (ExecutionException e) {//####[248]####
            e.printStackTrace();//####[249]####
        }//####[250]####
        if (root && atomicAlpha.get() > initialAlpha) //####[253]####
        {//####[253]####
            updateBestLine(line, depth, atomicAlpha.get());//####[254]####
        }//####[255]####
        return new Integer(atomicAlpha.get());//####[257]####
    }//####[258]####
//####[261]####
    public Move bestMove(String FEN, int depth, int time) {//####[261]####
        return bestMove(FEN, depth, time, false);//####[262]####
    }//####[263]####
//####[265]####
    public Move bestMove(String FEN, int depth, int time, boolean verbose) {//####[265]####
        Board board = new Board(FEN);//####[266]####
        nodeCounter.set(0);//####[267]####
        ;//####[267]####
        bestMoveTimeLimit = time;//####[268]####
        int eval = 0;//####[270]####
        bestLine = new ArrayList<Move>();//####[271]####
        bestLineDepth = 0;//####[272]####
        bestLineEval = -100000;//####[273]####
        bestMoveStart = System.currentTimeMillis();//####[274]####
        int currentDepth = 1;//####[275]####
        int alpha = -1000000;//####[276]####
        int beta = 1000000;//####[277]####
        while (true) //####[278]####
        {//####[278]####
            if (currentDepth == 1) //####[280]####
            {//####[280]####
                ArrayList<Move> moves = board.generateAllMoves();//####[281]####
                if (moves.size() == 1) //####[282]####
                {//####[282]####
                    bestLine = new ArrayList<Move>();//####[283]####
                    bestLine.add(moves.get(0));//####[284]####
                    break;//####[285]####
                }//####[286]####
            }//####[287]####
            eval = PVSplit(currentDepth, alpha, beta, new ArrayList<Move>(), true, true, board, currentDepth);//####[288]####
            if (eval == 1234567890) //####[290]####
            break;//####[291]####
            if (eval <= alpha || eval >= beta) //####[292]####
            {//####[292]####
                alpha = -1000000;//####[293]####
                beta = 1000000;//####[294]####
                continue;//####[295]####
            }//####[296]####
            alpha = eval - WINDOW;//####[297]####
            beta = eval + WINDOW;//####[298]####
            currentDepth++;//####[300]####
            if (currentDepth > depth) //####[301]####
            break;//####[302]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[303]####
            break;//####[304]####
        }//####[305]####
        if (bestLine.size() == 0) //####[308]####
        {//####[308]####
            ArrayList<Move> moves = board.generateAllMoves();//####[309]####
            bestLine.add(moves.get(0));//####[310]####
        }//####[311]####
        System.out.println("ENGINE" + " : " + "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[313]####
        return bestLine.get(0);//####[314]####
    }//####[315]####
//####[319]####
    private synchronized void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[319]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[320]####
        return;//####[321]####
        bestLineDepth = depth;//####[322]####
        bestLineEval = eval;//####[323]####
        bestLine = line;//####[324]####
        String s = bestLineDepth + " : ";//####[326]####
        for (int i = 0; i < bestLine.size(); i++) //####[327]####
        {//####[327]####
            if (i == bestLineDepth) //####[328]####
            s += "| ";//####[329]####
            s += bestLine.get(i).toString() + " ";//####[330]####
        }//####[331]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[332]####
        System.out.println(TAG + " : " + s);//####[334]####
    }//####[335]####
//####[339]####
    private class MoveComparator implements Comparator<Move> {//####[339]####
//####[339]####
        /*  ParaTask helper method to access private/protected slots *///####[339]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[339]####
            if (m.getParameterTypes().length == 0)//####[339]####
                m.invoke(instance);//####[339]####
            else if ((m.getParameterTypes().length == 1))//####[339]####
                m.invoke(instance, arg);//####[339]####
            else //####[339]####
                m.invoke(instance, arg, interResult);//####[339]####
        }//####[339]####
//####[341]####
        public int ply;//####[341]####
//####[343]####
        public int compare(Move move1, Move move2) {//####[343]####
            int moveEval1 = moveEval(move1);//####[344]####
            int moveEval2 = moveEval(move2);//####[345]####
            if (moveEval1 > moveEval2) //####[346]####
            return -1; else if (moveEval2 > moveEval1) //####[348]####
            return 1; else return 0;//####[349]####
        }//####[352]####
//####[354]####
        private int moveEval(Move move) {//####[354]####
            if (bestLine != null && bestLine.size() >= ply) //####[355]####
            {//####[355]####
                Move lastBest = bestLine.get(ply - 1);//####[356]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[357]####
                return 100000;//####[359]####
            }//####[360]####
            if (move.capture == 0) //####[362]####
            return 0; else {//####[364]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[365]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[366]####
                return capturePrice - piecePrice + 2000;//####[367]####
            }//####[368]####
        }//####[369]####
    }//####[369]####
//####[372]####
    private static volatile Method __pt__runParallelLoop_DoParallelLoop_method = null;//####[372]####
    private synchronized static void __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet() {//####[372]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[372]####
            try {//####[372]####
                __pt__runParallelLoop_DoParallelLoop_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__runParallelLoop", new Class[] {//####[372]####
                    DoParallelLoop.class//####[372]####
                });//####[372]####
            } catch (Exception e) {//####[372]####
                e.printStackTrace();//####[372]####
            }//####[372]####
        }//####[372]####
    }//####[372]####
    private TaskID<Void> runParallelLoop(DoParallelLoop l) {//####[372]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[372]####
        return runParallelLoop(l, new TaskInfo());//####[372]####
    }//####[372]####
    private TaskID<Void> runParallelLoop(DoParallelLoop l, TaskInfo taskinfo) {//####[372]####
        // ensure Method variable is set//####[372]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[372]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[372]####
        }//####[372]####
        taskinfo.setParameters(l);//####[372]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[372]####
        taskinfo.setInstance(this);//####[372]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[372]####
    }//####[372]####
    private TaskID<Void> runParallelLoop(TaskID<DoParallelLoop> l) {//####[372]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[372]####
        return runParallelLoop(l, new TaskInfo());//####[372]####
    }//####[372]####
    private TaskID<Void> runParallelLoop(TaskID<DoParallelLoop> l, TaskInfo taskinfo) {//####[372]####
        // ensure Method variable is set//####[372]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[372]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[372]####
        }//####[372]####
        taskinfo.setTaskIdArgIndexes(0);//####[372]####
        taskinfo.addDependsOn(l);//####[372]####
        taskinfo.setParameters(l);//####[372]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[372]####
        taskinfo.setInstance(this);//####[372]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[372]####
    }//####[372]####
    private TaskID<Void> runParallelLoop(BlockingQueue<DoParallelLoop> l) {//####[372]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[372]####
        return runParallelLoop(l, new TaskInfo());//####[372]####
    }//####[372]####
    private TaskID<Void> runParallelLoop(BlockingQueue<DoParallelLoop> l, TaskInfo taskinfo) {//####[372]####
        // ensure Method variable is set//####[372]####
        if (__pt__runParallelLoop_DoParallelLoop_method == null) {//####[372]####
            __pt__runParallelLoop_DoParallelLoop_ensureMethodVarSet();//####[372]####
        }//####[372]####
        taskinfo.setQueueArgIndexes(0);//####[372]####
        taskinfo.setIsPipeline(true);//####[372]####
        taskinfo.setParameters(l);//####[372]####
        taskinfo.setMethod(__pt__runParallelLoop_DoParallelLoop_method);//####[372]####
        taskinfo.setInstance(this);//####[372]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[372]####
    }//####[372]####
    public void __pt__runParallelLoop(DoParallelLoop l) {//####[372]####
        l.run();//####[373]####
    }//####[374]####
//####[374]####
//####[375]####
    private class DoParallelLoop {//####[375]####
//####[375]####
        /*  ParaTask helper method to access private/protected slots *///####[375]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[375]####
            if (m.getParameterTypes().length == 0)//####[375]####
                m.invoke(instance);//####[375]####
            else if ((m.getParameterTypes().length == 1))//####[375]####
                m.invoke(instance, arg);//####[375]####
            else //####[375]####
                m.invoke(instance, arg, interResult);//####[375]####
        }//####[375]####
//####[376]####
        AtomicInteger alpha;//####[376]####
//####[377]####
        AtomicInteger beta;//####[377]####
//####[378]####
        AtomicInteger result;//####[378]####
//####[379]####
        Board b1;//####[379]####
//####[380]####
        ArrayList<Move> locLine;//####[380]####
//####[381]####
        ArrayList<Move> moves;//####[381]####
//####[382]####
        int index;//####[382]####
//####[383]####
        int depth;//####[383]####
//####[384]####
        int currentDepth;//####[384]####
//####[385]####
        ArrayList<Move> line;//####[385]####
//####[386]####
        int initialLineSize;//####[386]####
//####[387]####
        boolean root;//####[387]####
//####[388]####
        int initialAlpha;//####[388]####
//####[390]####
        private DoParallelLoop(AtomicInteger alpha, AtomicInteger beta, AtomicInteger eval, Board board, ArrayList<Move> moves, int index, int depth, int currentDepth, boolean root, ArrayList<Move> line, int intialLineSize, int initialAlpha) {//####[392]####
            this.alpha = alpha;//####[393]####
            this.beta = beta;//####[394]####
            this.result = eval;//####[395]####
            this.b1 = board;//####[396]####
            this.locLine = new ArrayList<Move>();//####[397]####
            this.moves = moves;//####[398]####
            this.index = index;//####[399]####
            this.depth = depth;//####[400]####
            this.currentDepth = currentDepth;//####[401]####
            this.root = root;//####[402]####
            this.line = line;//####[403]####
            this.initialLineSize = intialLineSize;//####[404]####
            this.initialAlpha = initialAlpha;//####[405]####
        }//####[406]####
//####[407]####
        public void run() {//####[407]####
            locLine.clear();//####[408]####
            int eval;//####[409]####
            b1.doMove(moves.get(index));//####[411]####
            if (b1.isRepetition()) //####[412]####
            eval = -50; else if (b1.isDraw50Move()) //####[414]####
            eval = -50; else {//####[416]####
                if (index >= 4 && currentDepth - depth >= 2 && !b1.inCheck(b1.toMove) && moves.get(index).capture == 0) //####[417]####
                {//####[419]####
                    eval = -alphaBeta(depth - 2, -alpha.get() - 1, -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 2);//####[420]####
                    if (eval > alpha.get()) //####[422]####
                    {//####[422]####
                        eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[423]####
                    }//####[425]####
                } else {//####[426]####
                    eval = -alphaBeta(depth - 1, -beta.get(), -alpha.get(), locLine, false, true, new Board(b1), currentDepth + 1);//####[427]####
                }//####[429]####
            }//####[430]####
            b1.undoMove(moves.get(index));//####[431]####
            if (eval == -1234567890) //####[432]####
            result.set(1234567890);//####[434]####
            if (eval >= beta.get()) //####[436]####
            {//####[436]####
                result.set(1234567890);//####[438]####
            }//####[439]####
            if (eval > alpha.get()) //####[441]####
            {//####[441]####
                alpha.set(eval);//####[442]####
                line.subList(initialLineSize, line.size()).clear();//####[443]####
                line.add(moves.get(index));//####[444]####
                line.addAll(locLine);//####[445]####
            }//####[447]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[450]####
            {//####[453]####
                updateBestLine(line, depth, eval);//####[454]####
            }//####[455]####
            result.set(alpha.get());//####[456]####
        }//####[457]####
    }//####[457]####
}//####[457]####
