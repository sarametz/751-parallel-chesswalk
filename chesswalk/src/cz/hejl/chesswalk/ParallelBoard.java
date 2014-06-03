package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.*;//####[23]####
import java.util.concurrent.ExecutionException;//####[24]####
import java.util.concurrent.SynchronousQueue;//####[25]####
import java.util.concurrent.atomic.AtomicInteger;//####[26]####
import java.util.concurrent.atomic.AtomicLong;//####[27]####
import pt.runtime.TaskIDGroup;//####[29]####
//####[29]####
//-- ParaTask related imports//####[29]####
import pt.runtime.*;//####[29]####
import java.util.concurrent.ExecutionException;//####[29]####
import java.util.concurrent.locks.*;//####[29]####
import java.lang.reflect.*;//####[29]####
import pt.runtime.GuiThread;//####[29]####
import java.util.concurrent.BlockingQueue;//####[29]####
import java.util.ArrayList;//####[29]####
import java.util.List;//####[29]####
//####[29]####
public class ParallelBoard extends Board {//####[31]####
    static{ParaTask.init();}//####[31]####
    /*  ParaTask helper method to access private/protected slots *///####[31]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[31]####
        if (m.getParameterTypes().length == 0)//####[31]####
            m.invoke(instance);//####[31]####
        else if ((m.getParameterTypes().length == 1))//####[31]####
            m.invoke(instance, arg);//####[31]####
        else //####[31]####
            m.invoke(instance, arg, interResult);//####[31]####
    }//####[31]####
//####[33]####
    private static int[] bishopDeltas = { 15, 17, -17, -15 };//####[33]####
//####[34]####
    private static int[] kingDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };//####[34]####
//####[35]####
    private static int[] knightDeltas = { 31, 33, 14, 18, -18, -14, -33, -31 };//####[35]####
//####[36]####
    private static int[] queenDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };//####[36]####
//####[37]####
    private static int[] rookDeltas = { 1, -16, -1, 16 };//####[37]####
//####[39]####
    public ParallelBoard() {//####[39]####
        super();//####[40]####
    }//####[41]####
//####[43]####
    public ParallelBoard(String FEN) {//####[43]####
        super(FEN);//####[44]####
    }//####[45]####
//####[47]####
    public ParallelBoard(Board b) {//####[47]####
        super(b);//####[48]####
    }//####[49]####
//####[51]####
    public ArrayList<Move> generateAllMoves() {//####[51]####
        List<Move> moves = Collections.synchronizedList(new ArrayList<Move>());//####[52]####
        TaskIDGroup<Void> pieces = new TaskIDGroup<Void>(128);//####[54]####
        for (int i = 0; i < 128; i++) //####[57]####
        {//####[57]####
            if ((i & 0x88) != 0) //####[58]####
            continue;//####[59]####
            TaskID<Void> id = generateMoves(board0x88[i], i, moves);//####[60]####
            pieces.add(id);//####[61]####
        }//####[62]####
        try {//####[63]####
            pieces.waitTillFinished();//####[64]####
        } catch (InterruptedException e) {//####[65]####
            e.printStackTrace();//####[66]####
        } catch (ExecutionException e) {//####[67]####
            e.printStackTrace();//####[68]####
        }//####[69]####
        removeIllegalMoves(moves);//####[73]####
        return new ArrayList<Move>(moves);//####[75]####
    }//####[76]####
//####[78]####
    private static volatile Method __pt__generateMoves_int_int_ListMove_method = null;//####[78]####
    private synchronized static void __pt__generateMoves_int_int_ListMove_ensureMethodVarSet() {//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            try {//####[78]####
                __pt__generateMoves_int_int_ListMove_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__generateMoves", new Class[] {//####[78]####
                    int.class, int.class, List.class//####[78]####
                });//####[78]####
            } catch (Exception e) {//####[78]####
                e.printStackTrace();//####[78]####
            }//####[78]####
        }//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, int from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, int from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, int from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, int from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(0);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, int from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, int from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, TaskID<Integer> from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, TaskID<Integer> from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(1);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(1);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, BlockingQueue<Integer> from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, BlockingQueue<Integer> from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(1);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(1);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(0);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, List<Move> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, List<Move> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0, 1);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, int from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, int from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(2);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, int from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, int from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, int from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, int from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(2);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, TaskID<Integer> from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, TaskID<Integer> from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, BlockingQueue<Integer> from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, BlockingQueue<Integer> from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(1);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(2);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(1);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0, 1);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(2);//####[78]####
        taskinfo.addDependsOn(moves);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, int from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, int from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, int from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, int from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(0);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0, 2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, TaskID<Integer> from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, TaskID<Integer> from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(1);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0, 2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(1);//####[78]####
        taskinfo.addDependsOn(from);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(1, 2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(1, 2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setTaskIdArgIndexes(0);//####[78]####
        taskinfo.addDependsOn(piece);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<List<Move>> moves) {//####[78]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[78]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[78]####
    }//####[78]####
    private TaskID<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<List<Move>> moves, TaskInfo taskinfo) {//####[78]####
        // ensure Method variable is set//####[78]####
        if (__pt__generateMoves_int_int_ListMove_method == null) {//####[78]####
            __pt__generateMoves_int_int_ListMove_ensureMethodVarSet();//####[78]####
        }//####[78]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[78]####
        taskinfo.setIsPipeline(true);//####[78]####
        taskinfo.setParameters(piece, from, moves);//####[78]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ListMove_method);//####[78]####
        taskinfo.setInstance(this);//####[78]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[78]####
    }//####[78]####
    public void __pt__generateMoves(int piece, int from, List<Move> moves) {//####[78]####
        long start = System.currentTimeMillis();//####[79]####
        if (toMove * piece < 0) //####[80]####
        return;//####[81]####
        int pieceType = Math.abs(piece);//####[82]####
        if (pieceType == 1) //####[84]####
        {//####[84]####
            generatePawnMoves(piece, from, moves);//####[85]####
        } else if (pieceType == 2 || pieceType == 6) //####[88]####
        {//####[88]####
            generateNonSlidingMoves(piece, from, moves);//####[89]####
        } else if (pieceType >= 3 && pieceType <= 5) //####[92]####
        {//####[92]####
            generateSlidingMoves(piece, from, moves);//####[93]####
        }//####[94]####
        long fin = System.currentTimeMillis();//####[96]####
    }//####[98]####
//####[98]####
//####[100]####
    private void generateNonSlidingMoves(int piece, int from, List<Move> moves) {//####[101]####
        int[] deltas;//####[102]####
        if (piece == 2 || piece == -2) //####[103]####
        deltas = knightDeltas; else deltas = kingDeltas;//####[104]####
        for (int i = 0; i < deltas.length; i++) //####[108]####
        {//####[108]####
            int to = from + deltas[i];//####[109]####
            if ((to & 0x88) != 0) //####[110]####
            continue;//####[111]####
            if (board0x88[to] > 0 && piece > 0) //####[112]####
            continue;//####[113]####
            if (board0x88[to] < 0 && piece < 0) //####[114]####
            continue;//####[115]####
            moves.add(new Move(this, from, to));//####[116]####
        }//####[117]####
        if (piece == 6) //####[120]####
        {//####[120]####
            if (castlingRights[0]) //####[121]####
            {//####[121]####
                if (board0x88[5] == 0 && board0x88[6] == 0) //####[122]####
                {//####[122]####
                    if (squareAttacked(4, -1) == false && squareAttacked(5, -1) == false && squareAttacked(6, -1) == false) //####[123]####
                    {//####[125]####
                        moves.add(new Move(this, from, from + 2));//####[126]####
                    }//####[127]####
                }//####[128]####
            }//####[129]####
            if (castlingRights[1]) //####[130]####
            {//####[130]####
                if (board0x88[1] == 0 && board0x88[2] == 0 && board0x88[3] == 0) //####[131]####
                {//####[131]####
                    if (squareAttacked(2, -1) == false && squareAttacked(3, -1) == false && squareAttacked(4, -1) == false) //####[132]####
                    {//####[134]####
                        moves.add(new Move(this, from, from - 2));//####[135]####
                    }//####[136]####
                }//####[137]####
            }//####[138]####
        } else if (piece == -6) //####[139]####
        {//####[139]####
            if (castlingRights[2]) //####[140]####
            {//####[140]####
                if (board0x88[117] == 0 && board0x88[118] == 0) //####[141]####
                {//####[141]####
                    if (squareAttacked(116, 1) == false && squareAttacked(117, 1) == false && squareAttacked(118, 1) == false) //####[142]####
                    {//####[144]####
                        moves.add(new Move(this, from, from + 2));//####[145]####
                    }//####[146]####
                }//####[147]####
            }//####[148]####
            if (castlingRights[3]) //####[149]####
            {//####[149]####
                if (board0x88[113] == 0 && board0x88[114] == 0 && board0x88[115] == 0) //####[150]####
                {//####[151]####
                    if (squareAttacked(114, 1) == false && squareAttacked(115, 1) == false && squareAttacked(116, 1) == false) //####[152]####
                    {//####[154]####
                        moves.add(new Move(this, from, from - 2));//####[155]####
                    }//####[156]####
                }//####[157]####
            }//####[158]####
        }//####[159]####
    }//####[160]####
//####[162]####
    private void generateSlidingMoves(int piece, int from, List<Move> moves) {//####[162]####
        int[] deltas;//####[163]####
        if (piece == 3 || piece == -3) //####[164]####
        deltas = bishopDeltas; else if (piece == 4 || piece == -4) //####[166]####
        deltas = rookDeltas; else deltas = queenDeltas;//####[167]####
        for (int i = 0; i < deltas.length; i++) //####[171]####
        {//####[171]####
            int delta = deltas[i];//####[172]####
            int to = from;//####[173]####
            while (true) //####[174]####
            {//####[174]####
                to += delta;//####[175]####
                if ((to & 0x88) != 0) //####[176]####
                break;//####[177]####
                if (board0x88[to] > 0 && piece > 0 || board0x88[to] < 0 && piece < 0) //####[178]####
                break;//####[180]####
                if (board0x88[to] > 0 && piece < 0 || board0x88[to] < 0 && piece > 0) //####[181]####
                {//####[182]####
                    moves.add(new Move(this, from, to));//####[183]####
                    break;//####[184]####
                }//####[185]####
                moves.add(new Move(this, from, to));//####[186]####
            }//####[187]####
        }//####[188]####
    }//####[189]####
//####[191]####
    private void generatePawnMoves(int piece, int from, List<Move> moves) {//####[191]####
        if (piece == 1) //####[193]####
        {//####[193]####
            if (board0x88[from + 16] == 0) //####[195]####
            moves.add(new Move(this, from, from + 16));//####[196]####
            if (from / 16 == 1 && board0x88[from + 16] == 0 && board0x88[from + 2 * 16] == 0) //####[198]####
            moves.add(new Move(this, from, from + 2 * 16));//####[200]####
            if (board0x88[from + 15] < 0 && ((from + 15) & 0x88) == 0) //####[202]####
            moves.add(new Move(this, from, from + 15));//####[203]####
            if (board0x88[from + 17] < 0 && ((from + 17) & 0x88) == 0) //####[204]####
            moves.add(new Move(this, from, from + 17));//####[205]####
            if (enPassant != -1 && enPassant / 16 == 5) //####[207]####
            {//####[207]####
                if (from + 15 == enPassant || from + 17 == enPassant) //####[208]####
                moves.add(new Move(this, from, enPassant));//####[209]####
            }//####[210]####
        } else if (piece == -1) //####[214]####
        {//####[214]####
            if (board0x88[from - 16] == 0) //####[216]####
            moves.add(new Move(this, from, from - 16));//####[217]####
            if (from / 16 == 6 && board0x88[from - 16] == 0 && board0x88[from - 2 * 16] == 0) //####[219]####
            moves.add(new Move(this, from, from - 2 * 16));//####[221]####
            if (((from - 15) & 0x88) == 0 && board0x88[from - 15] > 0) //####[223]####
            moves.add(new Move(this, from, from - 15));//####[224]####
            if (((from - 17) & 0x88) == 0 && board0x88[from - 17] > 0) //####[225]####
            moves.add(new Move(this, from, from - 17));//####[226]####
            if (enPassant != -1 && enPassant / 16 == 2) //####[228]####
            {//####[228]####
                if (from - 15 == enPassant || from - 17 == enPassant) //####[229]####
                moves.add(new Move(this, from, enPassant));//####[230]####
            }//####[231]####
        }//####[232]####
    }//####[233]####
//####[236]####
    /** color can be 1 for white or -1 for black *///####[236]####
    public boolean inCheck(int color) {//####[236]####
        int king = -1;//####[238]####
        for (int i = 0; i < 128; i++) //####[239]####
        {//####[239]####
            if ((i & 0x88) != 0) //####[240]####
            continue;//####[241]####
            if (board0x88[i] == 6 * color) //####[242]####
            {//####[242]####
                king = i;//####[243]####
                break;//####[244]####
            }//####[245]####
        }//####[246]####
        return squareAttacked(king, color * -1);//####[248]####
    }//####[249]####
//####[251]####
    public boolean isCheckmate() {//####[251]####
        if (generateAllMoves().size() == 0 && inCheck(toMove)) //####[252]####
        {//####[252]####
            return true;//####[253]####
        } else return false;//####[254]####
    }//####[256]####
//####[258]####
    public boolean isDraw50Move() {//####[258]####
        if (halfmoves >= 100) //####[259]####
        return true; else return false;//####[260]####
    }//####[263]####
//####[265]####
    public boolean isEndgame() {//####[265]####
        boolean wQueen = false;//####[266]####
        boolean bQueen = false;//####[267]####
        int wRooks = 0;//####[268]####
        int bRooks = 0;//####[269]####
        int wMinors = 0;//####[270]####
        int bMinors = 0;//####[271]####
        for (int i = 0; i < 128; i++) //####[273]####
        {//####[273]####
            if ((i & 0x88) != 0) //####[274]####
            continue;//####[275]####
            int piece = board0x88[i];//####[276]####
            switch(piece) {//####[277]####
                case 5://####[277]####
                    wQueen = true;//####[279]####
                case -5://####[279]####
                    bQueen = true;//####[281]####
                case 4://####[281]####
                    wRooks++;//####[283]####
                case -4://####[283]####
                    bRooks++;//####[285]####
                case 3://####[285]####
                    wMinors--;//####[287]####
                case -3://####[287]####
                    bMinors--;//####[289]####
                case 2://####[289]####
                    wMinors--;//####[291]####
                case -2://####[291]####
                    bMinors--;//####[293]####
            }//####[293]####
        }//####[295]####
        boolean endgame = true;//####[297]####
        if (wQueen && (wMinors > 1 || wRooks > 0)) //####[298]####
        endgame = false;//####[299]####
        if (bQueen && (bMinors > 1 || bRooks > 0)) //####[300]####
        endgame = false;//####[301]####
        return endgame;//####[303]####
    }//####[304]####
//####[306]####
    public boolean isRepetition() {//####[306]####
        int hits = 1;//####[307]####
        for (int i = hashHistory.size() - 2; i >= 0; i--) //####[308]####
        {//####[308]####
            if (hashHistory.get(i) == hash) //####[309]####
            hits++;//####[310]####
        }//####[311]####
        if (hits >= 3) //####[313]####
        {//####[313]####
            return true;//####[314]####
        } else return false;//####[315]####
    }//####[317]####
//####[319]####
    public boolean isStalemate() {//####[319]####
        if (generateAllMoves().size() == 0 && !inCheck(toMove)) //####[320]####
        {//####[320]####
            return true;//####[321]####
        } else return false;//####[322]####
    }//####[324]####
//####[326]####
    private void removeIllegalMoves(List<Move> moves) {//####[326]####
        Iterator<Move> iter = moves.iterator();//####[327]####
        while (iter.hasNext()) //####[328]####
        {//####[328]####
            int color = toMove;//####[329]####
            Move move = (Move) iter.next();//####[330]####
            doMove(move);//####[331]####
            boolean illegalMove = false;//####[332]####
            if (inCheck(color)) //####[333]####
            illegalMove = true;//####[334]####
            undoMove(move);//####[335]####
            if (illegalMove) //####[336]####
            iter.remove();//####[337]####
        }//####[338]####
    }//####[339]####
}//####[339]####
