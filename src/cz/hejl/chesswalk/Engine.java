package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.ArrayList;//####[23]####
import java.util.Collections;//####[24]####
import java.util.Comparator;//####[25]####
import java.util.Currency;//####[26]####
import java.util.concurrent.ExecutionException;//####[27]####
import pt.runtime.CurrentTask;//####[29]####
import pt.runtime.TaskID;//####[30]####
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
    public int nodeCounter = 0;//####[35]####
//####[36]####
    public Board board = new Board();//####[36]####
//####[38]####
    private static final int WINDOW = 10;//####[38]####
//####[39]####
    private static final int[] PIECE_PRICES = { 0, 100, 300, 300, 500, 900, 1000 };//####[39]####
//####[41]####
    private static final String TAG = "Engine";//####[41]####
//####[43]####
    private boolean allowNullGlobal = true;//####[43]####
//####[44]####
    private int bestLineDepth;//####[44]####
//####[45]####
    private int bestLineEval;//####[45]####
//####[46]####
    private int bestMoveTimeLimit;//####[46]####
//####[47]####
    private int currentDepth;//####[47]####
//####[48]####
    private long bestMoveStart;//####[48]####
//####[49]####
    private ArrayList<Move> bestLine;//####[49]####
//####[50]####
    private MoveComparator moveComparator = new MoveComparator();//####[50]####
//####[51]####
    private Move[] primaryKillers = new Move[50];//####[51]####
//####[52]####
    private Move[] secondaryKillers = new Move[50];//####[52]####
//####[56]####
    private static volatile Method __pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_method = null;//####[56]####
    private synchronized static void __pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_ensureMethodVarSet() {//####[56]####
        if (__pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_method == null) {//####[56]####
            try {//####[56]####
                __pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__alphaBeta", new Class[] {//####[56]####
                    int.class, int.class, int.class, ArrayList.class, boolean.class, boolean.class//####[56]####
                });//####[56]####
            } catch (Exception e) {//####[56]####
                e.printStackTrace();//####[56]####
            }//####[56]####
        }//####[56]####
    }//####[56]####
    private TaskID<Integer> alphaBeta(Object depth, Object alpha, Object beta, Object line, Object root, Object allowNull) {//####[57]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[57]####
        return alphaBeta(depth, alpha, beta, line, root, allowNull, new TaskInfo());//####[57]####
    }//####[57]####
    private TaskID<Integer> alphaBeta(Object depth, Object alpha, Object beta, Object line, Object root, Object allowNull, TaskInfo taskinfo) {//####[57]####
        // ensure Method variable is set//####[57]####
        if (__pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_method == null) {//####[57]####
            __pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_ensureMethodVarSet();//####[57]####
        }//####[57]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[57]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[57]####
        if (depth instanceof BlockingQueue) {//####[57]####
            __pt__queueIndexList.add(0);//####[57]####
        }//####[57]####
        if (depth instanceof TaskID) {//####[57]####
            taskinfo.addDependsOn((TaskID)depth);//####[57]####
            __pt__taskIdIndexList.add(0);//####[57]####
        }//####[57]####
        if (alpha instanceof BlockingQueue) {//####[57]####
            __pt__queueIndexList.add(1);//####[57]####
        }//####[57]####
        if (alpha instanceof TaskID) {//####[57]####
            taskinfo.addDependsOn((TaskID)alpha);//####[57]####
            __pt__taskIdIndexList.add(1);//####[57]####
        }//####[57]####
        if (beta instanceof BlockingQueue) {//####[57]####
            __pt__queueIndexList.add(2);//####[57]####
        }//####[57]####
        if (beta instanceof TaskID) {//####[57]####
            taskinfo.addDependsOn((TaskID)beta);//####[57]####
            __pt__taskIdIndexList.add(2);//####[57]####
        }//####[57]####
        if (line instanceof BlockingQueue) {//####[57]####
            __pt__queueIndexList.add(3);//####[57]####
        }//####[57]####
        if (line instanceof TaskID) {//####[57]####
            taskinfo.addDependsOn((TaskID)line);//####[57]####
            __pt__taskIdIndexList.add(3);//####[57]####
        }//####[57]####
        if (root instanceof BlockingQueue) {//####[57]####
            __pt__queueIndexList.add(4);//####[57]####
        }//####[57]####
        if (root instanceof TaskID) {//####[57]####
            taskinfo.addDependsOn((TaskID)root);//####[57]####
            __pt__taskIdIndexList.add(4);//####[57]####
        }//####[57]####
        if (allowNull instanceof BlockingQueue) {//####[57]####
            __pt__queueIndexList.add(5);//####[57]####
        }//####[57]####
        if (allowNull instanceof TaskID) {//####[57]####
            taskinfo.addDependsOn((TaskID)allowNull);//####[57]####
            __pt__taskIdIndexList.add(5);//####[57]####
        }//####[57]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[57]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[57]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[57]####
        }//####[57]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[57]####
        if (__pt__queueIndexArray.length > 0) {//####[57]####
            taskinfo.setIsPipeline(true);//####[57]####
        }//####[57]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[57]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[57]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[57]####
        }//####[57]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[57]####
        taskinfo.setParameters(depth, alpha, beta, line, root, allowNull);//####[57]####
        taskinfo.setMethod(__pt__alphaBeta_int_int_int_ArrayListMove_boolean_boolean_method);//####[57]####
        taskinfo.setInstance(this);//####[57]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[57]####
    }//####[57]####
    public Integer __pt__alphaBeta(int depth, int alpha, int beta, ArrayList<Move> line, boolean root, boolean allowNull) {//####[57]####
        if (System.currentTimeMillis() - bestMoveStart > bestMoveTimeLimit && !root) //####[59]####
        return new Integer(1234567890);//####[61]####
        if (allowNullGlobal == false) //####[62]####
        allowNull = false;//####[63]####
        nodeCounter++;//####[64]####
        int initialAlpha = alpha;//####[65]####
        int initialLineSize = line.size();//####[67]####
        ArrayList<Move> locLine = new ArrayList<Move>();//####[68]####
        ArrayList<Move> moves = null;//####[69]####
        moves = board.generateAllMoves();//####[70]####
        moveComparator.ply = currentDepth - depth + 1;//####[71]####
        Collections.sort(moves, moveComparator);//####[72]####
        if (depth <= 0) //####[74]####
        {//####[74]####
            int eval = board.evaluate();//####[75]####
            if (eval >= beta) //####[76]####
            return new Integer(beta);//####[77]####
            if (eval > alpha) //####[78]####
            alpha = eval;//####[79]####
            int capturesN = 0;//####[81]####
            for (int i = 0; i < moves.size(); i++) //####[82]####
            {//####[82]####
                if (moves.get(i).capture == 0) //####[83]####
                break;//####[84]####
                capturesN++;//####[85]####
            }//####[86]####
            moves.subList(capturesN, moves.size()).clear();//####[87]####
        }//####[88]####
        if (moves.size() == 0) //####[90]####
        return board.evaluate();//####[91]####
        if (allowNull && depth > 0) //####[93]####
        {//####[93]####
            if (!board.inCheck(board.toMove)) //####[94]####
            {//####[94]####
                board.toMove *= -1;//####[95]####
                TaskID<Integer> id1 = alphaBeta(depth - 1 - 2, -beta, -beta + 1, locLine, false, false);//####[96]####
                int eval = 0;//####[98]####
                try {//####[99]####
                    eval = -((int) id1.getReturnResult());//####[100]####
                } catch (InterruptedException e) {//####[101]####
                    Log.e("Engine", "Interupted excpetion " + e.getMessage());//####[102]####
                } catch (ExecutionException e) {//####[103]####
                    Log.e("Engine", "Execution excpetion " + e.getMessage());//####[104]####
                }//####[105]####
                board.toMove *= -1;//####[106]####
                if (eval == -1234567890) //####[107]####
                return new Integer(1234567890);//####[108]####
                if (eval >= beta) //####[110]####
                {//####[110]####
                    return new Integer(beta);//####[111]####
                }//####[112]####
            }//####[113]####
        }//####[114]####
        for (int i = 0; i < moves.size(); i++) //####[116]####
        {//####[116]####
            locLine.clear();//####[117]####
            int eval;//####[118]####
            board.doMove(moves.get(i));//####[120]####
            if (board.isRepetition()) //####[121]####
            eval = -50; else if (board.isDraw50Move()) //####[123]####
            eval = -50; else {//####[125]####
                if (i >= 4 && currentDepth - depth >= 2 && !board.inCheck(board.toMove) && moves.get(i).capture == 0) //####[126]####
                {//####[128]####
                    TaskID<Integer> id2 = alphaBeta(depth - 2, -alpha - 1, -alpha, locLine, false, true);//####[129]####
                    eval = 0;//####[131]####
                    try {//####[132]####
                        eval = -((int) id2.getReturnResult());//####[133]####
                    } catch (InterruptedException e) {//####[134]####
                        Log.e("Engine", "Interupted excpetion " + e.getMessage());//####[135]####
                    } catch (ExecutionException e) {//####[136]####
                        Log.e("Engine", "Execution excpetion " + e.getMessage());//####[137]####
                    }//####[138]####
                    if (eval > alpha) //####[139]####
                    {//####[139]####
                        TaskID<Integer> id3 = alphaBeta(depth - 1, -beta, -alpha, locLine, false, true);//####[140]####
                        try {//####[142]####
                            eval = -((int) id3.getReturnResult());//####[143]####
                        } catch (InterruptedException e) {//####[144]####
                            Log.e("Engine", "Interupted excpetion " + e.getMessage());//####[145]####
                        } catch (ExecutionException e) {//####[146]####
                            Log.e("Engine", "Execution excpetion " + e.getMessage());//####[147]####
                        }//####[148]####
                    }//####[149]####
                } else {//####[150]####
                    TaskID<Integer> id4 = alphaBeta(depth - 1, -beta, -alpha, locLine, false, true);//####[151]####
                    eval = 0;//####[153]####
                    try {//####[154]####
                        eval = -((int) id4.getReturnResult());//####[155]####
                    } catch (InterruptedException e) {//####[156]####
                        Log.e("Engine", "Interupted excpetion " + e.getMessage());//####[157]####
                    } catch (ExecutionException e) {//####[158]####
                        Log.e("Engine", "Execution excpetion " + e.getMessage());//####[159]####
                    }//####[160]####
                }//####[161]####
            }//####[162]####
            board.undoMove(moves.get(i));//####[163]####
            if (eval == -1234567890) //####[164]####
            return new Integer(1234567890);//####[165]####
            if (eval >= beta) //####[167]####
            {//####[167]####
                if (primaryKillers[currentDepth - depth] != null) //####[169]####
                secondaryKillers[currentDepth - depth] = primaryKillers[currentDepth - depth];//####[170]####
                primaryKillers[currentDepth - depth] = moves.get(i);//####[172]####
                return new Integer(beta);//####[174]####
            }//####[175]####
            if (eval > alpha) //####[177]####
            {//####[177]####
                alpha = eval;//####[178]####
                line.subList(initialLineSize, line.size()).clear();//####[179]####
                line.add(moves.get(i));//####[180]####
                line.addAll(locLine);//####[181]####
            }//####[182]####
            if (root && (eval > bestLineEval || eval == bestLineEval && depth > bestLineDepth) && initialAlpha == -1000000) //####[185]####
            {//####[188]####
                updateBestLine(line, depth, eval);//####[189]####
            }//####[190]####
        }//####[191]####
        if (root && alpha > initialAlpha) //####[193]####
        {//####[193]####
            updateBestLine(line, depth, alpha);//####[194]####
        }//####[195]####
        return new Integer(alpha);//####[197]####
    }//####[198]####
//####[198]####
//####[202]####
    public Move bestMove(int depth, int time) {//####[202]####
        return bestMove(depth, time, false);//####[203]####
    }//####[204]####
//####[206]####
    public Move bestMove(int depth, int time, boolean verbose) {//####[206]####
        nodeCounter = 0;//####[207]####
        bestMoveTimeLimit = time;//####[208]####
        int eval = 0;//####[210]####
        bestLine = new ArrayList<Move>();//####[211]####
        bestLineDepth = 0;//####[212]####
        bestLineEval = -100000;//####[213]####
        bestMoveStart = System.currentTimeMillis();//####[214]####
        currentDepth = 1;//####[215]####
        int alpha = -1000000;//####[216]####
        int beta = 1000000;//####[217]####
        while (true) //####[218]####
        {//####[218]####
            if (currentDepth == 1) //####[220]####
            {//####[220]####
                ArrayList<Move> moves = board.generateAllMoves();//####[221]####
                if (moves.size() == 1) //####[222]####
                {//####[222]####
                    bestLine = new ArrayList<Move>();//####[223]####
                    bestLine.add(moves.get(0));//####[224]####
                    break;//####[225]####
                }//####[226]####
            }//####[227]####
            TaskID<Integer> id5 = alphaBeta(currentDepth, alpha, beta, new ArrayList<Move>(), true, true);//####[228]####
            try {//####[230]####
                eval = (int) id5.getReturnResult();//####[231]####
            } catch (InterruptedException e) {//####[232]####
                Log.e("Engine", "Interupted excpetion " + e.getMessage());//####[233]####
            } catch (ExecutionException e) {//####[234]####
                Log.e("Engine", "Execution excpetion " + e.getMessage());//####[235]####
            }//####[236]####
            if (eval == 1234567890) //####[237]####
            break;//####[238]####
            if (eval <= alpha || eval >= beta) //####[239]####
            {//####[239]####
                alpha = -1000000;//####[240]####
                beta = 1000000;//####[241]####
                continue;//####[242]####
            }//####[243]####
            alpha = eval - WINDOW;//####[244]####
            beta = eval + WINDOW;//####[245]####
            currentDepth++;//####[247]####
            if (currentDepth > depth) //####[248]####
            break;//####[249]####
            if (System.currentTimeMillis() - bestMoveStart > time) //####[250]####
            break;//####[251]####
        }//####[252]####
        if (bestLine.size() == 0) //####[255]####
        {//####[255]####
            ArrayList<Move> moves = board.generateAllMoves();//####[256]####
            bestLine.add(moves.get(0));//####[257]####
        }//####[258]####
        Log.d("ENGINE", "Depth = " + currentDepth + " , Nodes = " + nodeCounter);//####[260]####
        return bestLine.get(0);//####[261]####
    }//####[262]####
//####[266]####
    private void updateBestLine(ArrayList<Move> line, int depth, int eval) {//####[266]####
        if (depth == bestLineDepth && eval == bestLineEval) //####[267]####
        return;//####[268]####
        bestLineDepth = depth;//####[269]####
        bestLineEval = eval;//####[270]####
        bestLine = line;//####[271]####
        String s = bestLineDepth + " : ";//####[273]####
        for (int i = 0; i < bestLine.size(); i++) //####[274]####
        {//####[274]####
            if (i == bestLineDepth) //####[275]####
            s += "| ";//####[276]####
            s += bestLine.get(i).toString() + " ";//####[277]####
        }//####[278]####
        s += " : " + (System.currentTimeMillis() - bestMoveStart) + " : " + bestLineEval;//####[279]####
        Log.d(TAG, s);//####[281]####
    }//####[282]####
//####[286]####
    private class MoveComparator implements Comparator<Move> {//####[286]####
//####[286]####
        /*  ParaTask helper method to access private/protected slots *///####[286]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[286]####
            if (m.getParameterTypes().length == 0)//####[286]####
                m.invoke(instance);//####[286]####
            else if ((m.getParameterTypes().length == 1))//####[286]####
                m.invoke(instance, arg);//####[286]####
            else //####[286]####
                m.invoke(instance, arg, interResult);//####[286]####
        }//####[286]####
//####[288]####
        public int ply;//####[288]####
//####[290]####
        public int compare(Move move1, Move move2) {//####[290]####
            int moveEval1 = moveEval(move1);//####[291]####
            int moveEval2 = moveEval(move2);//####[292]####
            if (moveEval1 > moveEval2) //####[293]####
            return -1; else if (moveEval2 > moveEval1) //####[295]####
            return 1; else return 0;//####[296]####
        }//####[299]####
//####[301]####
        private int moveEval(Move move) {//####[301]####
            if (bestLine != null && bestLine.size() >= ply) //####[302]####
            {//####[302]####
                Move lastBest = bestLine.get(ply - 1);//####[303]####
                if (move.from == lastBest.from && move.to == lastBest.to && move.piece == lastBest.piece) //####[304]####
                return 100000;//####[306]####
            }//####[307]####
            if (move.capture == 0) //####[321]####
            return 0; else {//####[323]####
                int capturePrice = PIECE_PRICES[Math.abs(move.capture)];//####[324]####
                int piecePrice = PIECE_PRICES[Math.abs(move.piece)];//####[325]####
                return capturePrice - piecePrice + 2000;//####[326]####
            }//####[327]####
        }//####[328]####
    }//####[328]####
}//####[328]####
