package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.ArrayList;//####[23]####
import java.util.Iterator;//####[24]####
import pt.runtime.*;//####[26]####
import java.util.concurrent.BrokenBarrierException;//####[28]####
import java.util.concurrent.atomic.*;//####[29]####
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
public class Board {//####[31]####
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
//####[32]####
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";//####[32]####
//####[34]####
    public boolean[] castlingRights = new boolean[4];//####[34]####
//####[35]####
    public int[] board0x88 = new int[128];//####[35]####
//####[36]####
    public int enPassant;//####[36]####
//####[37]####
    public int halfmoves = 0;//####[37]####
//####[38]####
    public int toMove;//####[38]####
//####[39]####
    public long hash;//####[39]####
//####[40]####
    public ArrayList<Long> hashHistory;//####[40]####
//####[44]####
    private int moveN;//####[44]####
//####[47]####
    private int[] bishopDeltas = { 15, 17, -17, -15 };//####[47]####
//####[48]####
    private int[] kingDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };//####[48]####
//####[49]####
    private int[] knightDeltas = { 31, 33, 14, 18, -18, -14, -33, -31 };//####[49]####
//####[50]####
    private int[] queenDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };//####[50]####
//####[51]####
    private int[] rookDeltas = { 1, -16, -1, 16 };//####[51]####
//####[52]####
    private Evaluation evaluation;//####[52]####
//####[53]####
    private Zobrist zobrist = new Zobrist();//####[53]####
//####[54]####
    private static AtomicLong executionTime;//####[54]####
//####[55]####
    private static AtomicInteger numOfExecutionTimes;//####[55]####
//####[57]####
    static {//####[57]####
        executionTime = new AtomicLong((long) 0.00);//####[58]####
        numOfExecutionTimes = new AtomicInteger(0);//####[59]####
    }//####[61]####
//####[64]####
    public Board() {//####[64]####
        evaluation = new Evaluation(this);//####[65]####
        init();//####[66]####
        executionTime.set(0);//####[67]####
        numOfExecutionTimes.set(0);//####[68]####
    }//####[69]####
//####[71]####
    public void doMove(Move move) {//####[71]####
        if (toMove == 1) //####[72]####
        moveN++;//####[73]####
        toMove *= -1;//####[74]####
        int diff = move.to - move.from;//####[75]####
        move.halfmoves = halfmoves;//####[78]####
        halfmoves++;//####[79]####
        if (move.piece == 1 || move.piece == -1 || move.capture != 0) //####[80]####
        halfmoves = 0;//####[81]####
        if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && board0x88[move.to] == 0) //####[84]####
        {//####[85]####
            if (move.piece == 1) //####[86]####
            board0x88[move.to - 16] = 0;//####[87]####
            if (move.piece == -1) //####[88]####
            board0x88[move.to + 16] = 0;//####[89]####
        }//####[90]####
        board0x88[move.from] = 0;//####[93]####
        move.capture = board0x88[move.to];//####[94]####
        board0x88[move.to] = move.piece;//####[95]####
        if (move.piece == 1 && move.from / 16 == 6) //####[98]####
        board0x88[move.to] = 5; else if (move.piece == -1 && move.from / 16 == 1) //####[100]####
        board0x88[move.to] = -5;//####[101]####
        if (move.from == 4 && move.to == 6 && move.piece == 6) //####[104]####
        {//####[104]####
            board0x88[7] = 0;//####[105]####
            board0x88[5] = 4;//####[106]####
        } else if (move.from == 4 && move.to == 2 && move.piece == 6) //####[107]####
        {//####[107]####
            board0x88[0] = 0;//####[108]####
            board0x88[3] = 4;//####[109]####
        } else if (move.from == 116 && move.to == 118 && move.piece == -6) //####[110]####
        {//####[110]####
            board0x88[119] = 0;//####[111]####
            board0x88[117] = -4;//####[112]####
        } else if (move.from == 116 && move.to == 114 && move.piece == -6) //####[113]####
        {//####[113]####
            board0x88[112] = 0;//####[114]####
            board0x88[115] = -4;//####[115]####
        }//####[116]####
        move.enPassant = enPassant;//####[119]####
        if ((move.piece == 1 || move.piece == -1) && Math.abs(diff) == 32) //####[120]####
        enPassant = move.from + diff / 2; else enPassant = -1;//####[121]####
        for (int i = 0; i < 4; i++) //####[126]####
        move.castlingRights[i] = castlingRights[i];//####[127]####
        if (board0x88[0] != 4) //####[129]####
        castlingRights[1] = false;//####[130]####
        if (board0x88[7] != 4) //####[131]####
        castlingRights[0] = false;//####[132]####
        if (board0x88[4] != 6) //####[133]####
        {//####[133]####
            castlingRights[0] = false;//####[134]####
            castlingRights[1] = false;//####[135]####
        }//####[136]####
        if (board0x88[112] != -4) //####[137]####
        castlingRights[3] = false;//####[138]####
        if (board0x88[119] != -4) //####[139]####
        castlingRights[2] = false;//####[140]####
        if (board0x88[116] != -6) //####[141]####
        {//####[141]####
            castlingRights[2] = false;//####[142]####
            castlingRights[3] = false;//####[143]####
        }//####[144]####
        zobrist.doMove(this, move);//####[147]####
        hashHistory.add(hash);//####[148]####
        printBoard();//####[149]####
    }//####[150]####
//####[152]####
    public int evaluate() {//####[152]####
        return evaluation.evaluate();//####[153]####
    }//####[154]####
//####[156]####
    public void fromFEN(String FEN) {//####[156]####
        String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };//####[157]####
        String[] symbols = { "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };//####[158]####
        int[] pieces = { 1, 2, 3, 4, 5, 6, -1, -2, -3, -4, -5, -6 };//####[160]####
        int pos = -1;//####[162]####
        int file = 0;//####[163]####
        int rank = 7;//####[164]####
        String c = "";//####[165]####
        while (true) //####[166]####
        {//####[166]####
            pos++;//####[167]####
            c = FEN.substring(pos, pos + 1);//####[168]####
            if (c.equals(" ")) //####[169]####
            break;//####[170]####
            int pieceType = 0;//####[173]####
            for (int i = 0; i < symbols.length; i++) //####[174]####
            {//####[174]####
                if (c.equals(symbols[i])) //####[175]####
                {//####[175]####
                    pieceType = pieces[i];//####[176]####
                    break;//####[177]####
                }//####[178]####
            }//####[179]####
            if (pieceType != 0) //####[180]####
            {//####[180]####
                board0x88[rank * 16 + file] = pieceType;//####[181]####
                file++;//####[182]####
                continue;//####[183]####
            }//####[184]####
            if (c.equals("/")) //####[187]####
            {//####[187]####
                file = 0;//####[188]####
                rank--;//####[189]####
                continue;//####[190]####
            }//####[191]####
            int n = Integer.parseInt(c);//####[194]####
            for (int i = 0; i < n; i++) //####[195]####
            {//####[195]####
                board0x88[rank * 16 + file] = 0;//####[196]####
                file++;//####[197]####
            }//####[198]####
        }//####[199]####
        pos++;//####[202]####
        c = FEN.substring(pos, pos + 1);//####[203]####
        pos++;//####[204]####
        if (c.equals("w")) //####[205]####
        toMove = 1; else if (c.equals("b")) //####[207]####
        toMove = -1;//####[208]####
        for (int i = 0; i < 4; i++) //####[211]####
        castlingRights[i] = false;//####[212]####
        while (true) //####[213]####
        {//####[213]####
            pos++;//####[214]####
            c = FEN.substring(pos, pos + 1);//####[215]####
            if (c.equals(" ")) //####[216]####
            break; else if (c.equals("K")) //####[218]####
            castlingRights[0] = true; else if (c.equals("Q")) //####[220]####
            castlingRights[1] = true; else if (c.equals("k")) //####[222]####
            castlingRights[2] = true; else if (c.equals("q")) //####[224]####
            castlingRights[3] = true;//####[225]####
        }//####[226]####
        pos++;//####[229]####
        c = FEN.substring(pos, pos + 1);//####[230]####
        if (c.equals("-")) //####[231]####
        enPassant = -1; else {//####[233]####
            int index;//####[234]####
            for (index = 0; index < fileSymbols.length; index++) //####[235]####
            {//####[235]####
                if (fileSymbols[index].equals(c)) //####[236]####
                break;//####[237]####
            }//####[238]####
            file = index;//####[239]####
            pos++;//####[240]####
            rank = Integer.parseInt(FEN.substring(pos, pos + 1)) - 1;//####[241]####
            enPassant = rank * 16 + file;//####[242]####
        }//####[243]####
        pos++;//####[244]####
        int startPos = pos + 1;//####[247]####
        while (true) //####[248]####
        {//####[248]####
            pos++;//####[249]####
            c = FEN.substring(pos, pos + 1);//####[250]####
            if (c.equals(" ")) //####[251]####
            break;//####[252]####
        }//####[253]####
        halfmoves = Integer.parseInt(FEN.substring(startPos, pos));//####[254]####
        startPos = pos + 1;//####[257]####
        while (true) //####[258]####
        {//####[258]####
            pos++;//####[259]####
            if (pos == FEN.length()) //####[260]####
            break;//####[261]####
            c = FEN.substring(pos, pos + 1);//####[262]####
        }//####[263]####
        moveN = Integer.parseInt(FEN.substring(startPos, pos));//####[264]####
        zobrist.setHash(this);//####[267]####
        hashHistory = new ArrayList<Long>();//####[270]####
        hashHistory.add(hash);//####[271]####
    }//####[272]####
//####[274]####
    public ArrayList<Move> generateAllMoves() {//####[274]####
        ArrayList<Move> moves = new ArrayList<Move>();//####[275]####
        long startTime = System.nanoTime();//####[278]####
        for (int i = 0; i < 128; i++) //####[279]####
        {//####[279]####
            if ((i & 0x88) != 0) //####[280]####
            continue;//####[281]####
            generateMoves(board0x88[i], i, moves);//####[282]####
        }//####[283]####
        removeIllegalMoves(moves);//####[294]####
        return moves;//####[296]####
    }//####[297]####
//####[299]####
    private void generateMoves(int piece, int from, ArrayList<Move> moves) {//####[299]####
        TaskID x = generateAsyncMoves(piece, from, moves);//####[300]####
        try {//####[304]####
            long startTime = System.currentTimeMillis();//####[305]####
            x.waitTillFinished();//####[306]####
            long endTime = System.currentTimeMillis();//####[307]####
            long duration = endTime - startTime;//####[308]####
            System.out.println(duration);//####[309]####
        } catch (Exception e) {//####[312]####
            e.printStackTrace();//####[313]####
        }//####[314]####
    }//####[316]####
//####[318]####
    private static volatile Method __pt__generateAsyncMoves_int_int_ArrayListMove_method = null;//####[318]####
    private synchronized static void __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet() {//####[318]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[318]####
            try {//####[318]####
                __pt__generateAsyncMoves_int_int_ArrayListMove_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__generateAsyncMoves", new Class[] {//####[318]####
                    int.class, int.class, ArrayList.class//####[318]####
                });//####[318]####
            } catch (Exception e) {//####[318]####
                e.printStackTrace();//####[318]####
            }//####[318]####
        }//####[318]####
    }//####[318]####
    private TaskID<Void> generateAsyncMoves(int piece, int from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, int from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(0);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, int from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(1);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(1);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(1);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(1);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(0);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0, 1);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, int from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(2);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, int from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, int from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(2);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(1);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(2);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(1);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0, 1);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(2);//####[319]####
        taskinfo.addDependsOn(moves);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(0);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0, 2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(1);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0, 2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(1);//####[319]####
        taskinfo.addDependsOn(from);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(1, 2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(1, 2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setTaskIdArgIndexes(0);//####[319]####
        taskinfo.addDependsOn(piece);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[319]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[319]####
        return generateAsyncMoves(piece, from, moves, new TaskInfo());//####[319]####
    }//####[319]####
    private TaskID<Void> generateAsyncMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[319]####
        // ensure Method variable is set//####[319]####
        if (__pt__generateAsyncMoves_int_int_ArrayListMove_method == null) {//####[319]####
            __pt__generateAsyncMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[319]####
        }//####[319]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[319]####
        taskinfo.setIsPipeline(true);//####[319]####
        taskinfo.setParameters(piece, from, moves);//####[319]####
        taskinfo.setMethod(__pt__generateAsyncMoves_int_int_ArrayListMove_method);//####[319]####
        taskinfo.setInstance(this);//####[319]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[319]####
    }//####[319]####
    public void __pt__generateAsyncMoves(int piece, int from, ArrayList<Move> moves) {//####[319]####
        if (toMove * piece < 0) //####[322]####
        return;//####[323]####
        int pieceType = Math.abs(piece);//####[324]####
        if (pieceType == 1) //####[326]####
        {//####[326]####
            generatePawnMoves(piece, from, moves);//####[327]####
        } else if (pieceType == 2 || pieceType == 6) //####[330]####
        {//####[330]####
            generateNonSlidingMoves(piece, from, moves);//####[331]####
        } else if (pieceType >= 3 && pieceType <= 5) //####[334]####
        {//####[334]####
            generateSlidingMoves(piece, from, moves);//####[335]####
        }//####[336]####
    }//####[340]####
//####[340]####
//####[342]####
    private void generateNonSlidingMoves(int piece, int from, ArrayList<Move> moves) {//####[343]####
        int[] deltas;//####[344]####
        if (piece == 2 || piece == -2) //####[345]####
        deltas = knightDeltas; else deltas = kingDeltas;//####[346]####
        for (int i = 0; i < deltas.length; i++) //####[350]####
        {//####[350]####
            int to = from + deltas[i];//####[351]####
            if ((to & 0x88) != 0) //####[352]####
            continue;//####[353]####
            if (board0x88[to] > 0 && piece > 0) //####[354]####
            continue;//####[355]####
            if (board0x88[to] < 0 && piece < 0) //####[356]####
            continue;//####[357]####
            moves.add(new Move(this, from, to));//####[358]####
        }//####[359]####
        if (piece == 6) //####[362]####
        {//####[362]####
            if (castlingRights[0]) //####[363]####
            {//####[363]####
                if (board0x88[5] == 0 && board0x88[6] == 0) //####[364]####
                {//####[364]####
                    if (squareAttacked(4, -1) == false && squareAttacked(5, -1) == false && squareAttacked(6, -1) == false) //####[365]####
                    {//####[367]####
                        moves.add(new Move(this, from, from + 2));//####[368]####
                    }//####[369]####
                }//####[370]####
            }//####[371]####
            if (castlingRights[1]) //####[372]####
            {//####[372]####
                if (board0x88[1] == 0 && board0x88[2] == 0 && board0x88[3] == 0) //####[373]####
                {//####[373]####
                    if (squareAttacked(2, -1) == false && squareAttacked(3, -1) == false && squareAttacked(4, -1) == false) //####[374]####
                    {//####[376]####
                        moves.add(new Move(this, from, from - 2));//####[377]####
                    }//####[378]####
                }//####[379]####
            }//####[380]####
        } else if (piece == -6) //####[381]####
        {//####[381]####
            if (castlingRights[2]) //####[382]####
            {//####[382]####
                if (board0x88[117] == 0 && board0x88[118] == 0) //####[383]####
                {//####[383]####
                    if (squareAttacked(116, 1) == false && squareAttacked(117, 1) == false && squareAttacked(118, 1) == false) //####[384]####
                    {//####[386]####
                        moves.add(new Move(this, from, from + 2));//####[387]####
                    }//####[388]####
                }//####[389]####
            }//####[390]####
            if (castlingRights[3]) //####[391]####
            {//####[391]####
                if (board0x88[113] == 0 && board0x88[114] == 0 && board0x88[115] == 0) //####[392]####
                {//####[393]####
                    if (squareAttacked(114, 1) == false && squareAttacked(115, 1) == false && squareAttacked(116, 1) == false) //####[394]####
                    {//####[396]####
                        moves.add(new Move(this, from, from - 2));//####[397]####
                    }//####[398]####
                }//####[399]####
            }//####[400]####
        }//####[401]####
    }//####[402]####
//####[404]####
    private void generateSlidingMoves(int piece, int from, ArrayList<Move> moves) {//####[404]####
        int[] deltas;//####[405]####
        if (piece == 3 || piece == -3) //####[406]####
        deltas = bishopDeltas; else if (piece == 4 || piece == -4) //####[408]####
        deltas = rookDeltas; else deltas = queenDeltas;//####[409]####
        for (int i = 0; i < deltas.length; i++) //####[413]####
        {//####[413]####
            int delta = deltas[i];//####[414]####
            int to = from;//####[415]####
            while (true) //####[416]####
            {//####[416]####
                to += delta;//####[417]####
                if ((to & 0x88) != 0) //####[418]####
                break;//####[419]####
                if (board0x88[to] > 0 && piece > 0 || board0x88[to] < 0 && piece < 0) //####[420]####
                break;//####[422]####
                if (board0x88[to] > 0 && piece < 0 || board0x88[to] < 0 && piece > 0) //####[423]####
                {//####[424]####
                    moves.add(new Move(this, from, to));//####[425]####
                    break;//####[426]####
                }//####[427]####
                moves.add(new Move(this, from, to));//####[428]####
            }//####[429]####
        }//####[430]####
    }//####[431]####
//####[433]####
    private void generatePawnMoves(int piece, int from, ArrayList<Move> moves) {//####[433]####
        if (CurrentTask.insideTask()) //####[437]####
        {//####[437]####
        } else {//####[440]####
        }//####[443]####
        if (piece == 1) //####[444]####
        {//####[444]####
            if (board0x88[from + 16] == 0) //####[446]####
            {//####[447]####
                moves.add(new Move(this, from, from + 16));//####[449]####
            }//####[451]####
            if (from / 16 == 1 && board0x88[from + 16] == 0 && board0x88[from + 2 * 16] == 0) //####[453]####
            {//####[455]####
                moves.add(new Move(this, from, from + 2 * 16));//####[457]####
            }//####[458]####
            if (board0x88[from + 15] < 0 && ((from + 15) & 0x88) == 0) //####[460]####
            {//####[461]####
                moves.add(new Move(this, from, from + 15));//####[463]####
            }//####[464]####
            if (board0x88[from + 17] < 0 && ((from + 17) & 0x88) == 0) //####[465]####
            {//####[466]####
                moves.add(new Move(this, from, from + 17));//####[468]####
            }//####[469]####
            if (enPassant != -1 && enPassant / 16 == 5) //####[471]####
            {//####[471]####
                if (from + 15 == enPassant || from + 17 == enPassant) //####[472]####
                moves.add(new Move(this, from, enPassant));//####[474]####
            }//####[475]####
        } else if (piece == -1) //####[479]####
        {//####[479]####
            if (board0x88[from - 16] == 0) //####[481]####
            moves.add(new Move(this, from, from - 16));//####[482]####
            if (from / 16 == 6 && board0x88[from - 16] == 0 && board0x88[from - 2 * 16] == 0) //####[484]####
            moves.add(new Move(this, from, from - 2 * 16));//####[486]####
            if (((from - 15) & 0x88) == 0 && board0x88[from - 15] > 0) //####[488]####
            moves.add(new Move(this, from, from - 15));//####[489]####
            if (((from - 17) & 0x88) == 0 && board0x88[from - 17] > 0) //####[490]####
            moves.add(new Move(this, from, from - 17));//####[491]####
            if (enPassant != -1 && enPassant / 16 == 2) //####[493]####
            {//####[493]####
                if (from - 15 == enPassant || from - 17 == enPassant) //####[494]####
                moves.add(new Move(this, from, enPassant));//####[495]####
            }//####[496]####
        }//####[497]####
    }//####[498]####
//####[501]####
    /** color can be 1 for white or -1 for black *///####[501]####
    public boolean inCheck(int color) {//####[501]####
        int king = -1;//####[503]####
        for (int i = 0; i < 128; i++) //####[504]####
        {//####[504]####
            if ((i & 0x88) != 0) //####[505]####
            continue;//####[506]####
            if (board0x88[i] == 6 * color) //####[507]####
            {//####[507]####
                king = i;//####[508]####
                break;//####[509]####
            }//####[510]####
        }//####[511]####
        return squareAttacked(king, color * -1);//####[513]####
    }//####[514]####
//####[516]####
    public boolean isCheckmate() {//####[516]####
        if (generateAllMoves().size() == 0 && inCheck(toMove)) //####[517]####
        {//####[517]####
            return true;//####[518]####
        } else return false;//####[519]####
    }//####[521]####
//####[523]####
    public boolean isDraw50Move() {//####[523]####
        if (halfmoves >= 100) //####[524]####
        return true; else return false;//####[525]####
    }//####[528]####
//####[530]####
    public boolean isEndgame() {//####[530]####
        boolean wQueen = false;//####[531]####
        boolean bQueen = false;//####[532]####
        int wRooks = 0;//####[533]####
        int bRooks = 0;//####[534]####
        int wMinors = 0;//####[535]####
        int bMinors = 0;//####[536]####
        for (int i = 0; i < 128; i++) //####[538]####
        {//####[538]####
            if ((i & 0x88) != 0) //####[539]####
            continue;//####[540]####
            int piece = board0x88[i];//####[541]####
            switch(piece) {//####[542]####
                case 5://####[542]####
                    wQueen = true;//####[544]####
                case -5://####[544]####
                    bQueen = true;//####[546]####
                case 4://####[546]####
                    wRooks++;//####[548]####
                case -4://####[548]####
                    bRooks++;//####[550]####
                case 3://####[550]####
                    wMinors--;//####[552]####
                case -3://####[552]####
                    bMinors--;//####[554]####
                case 2://####[554]####
                    wMinors--;//####[556]####
                case -2://####[556]####
                    bMinors--;//####[558]####
            }//####[558]####
        }//####[560]####
        boolean endgame = true;//####[562]####
        if (wQueen && (wMinors > 1 || wRooks > 0)) //####[563]####
        endgame = false;//####[564]####
        if (bQueen && (bMinors > 1 || bRooks > 0)) //####[565]####
        endgame = false;//####[566]####
        return endgame;//####[568]####
    }//####[569]####
//####[571]####
    public boolean isRepetition() {//####[571]####
        int hits = 1;//####[572]####
        for (int i = hashHistory.size() - 2; i >= 0; i--) //####[573]####
        {//####[573]####
            if (hashHistory.get(i) == hash) //####[574]####
            hits++;//####[575]####
        }//####[576]####
        if (hits >= 3) //####[578]####
        {//####[578]####
            return true;//####[579]####
        } else return false;//####[580]####
    }//####[582]####
//####[584]####
    public boolean isStalemate() {//####[584]####
        if (generateAllMoves().size() == 0 && !inCheck(toMove)) //####[585]####
        {//####[585]####
            return true;//####[586]####
        } else return false;//####[587]####
    }//####[589]####
//####[591]####
    public void init() {//####[591]####
        fromFEN(STARTING_FEN);//####[592]####
        enPassant = -1;//####[595]####
        for (int i = 0; i < 4; i++) //####[598]####
        castlingRights[i] = true;//####[599]####
        toMove = 1;//####[601]####
        moveN = 0;//####[603]####
    }//####[604]####
//####[606]####
    public boolean[] legalMovesMap(int from) {//####[606]####
        ArrayList<Move> moves = new ArrayList<Move>();//####[607]####
        generateMoves(board0x88[from], from, moves);//####[608]####
        removeIllegalMoves(moves);//####[609]####
        boolean[] map = new boolean[128];//####[611]####
        for (int i = 0; i < moves.size(); i++) //####[612]####
        {//####[612]####
            map[moves.get(i).to] = true;//####[613]####
        }//####[614]####
        return map;//####[616]####
    }//####[617]####
//####[619]####
    private void removeIllegalMoves(ArrayList<Move> moves) {//####[619]####
        Iterator<Move> iter = moves.iterator();//####[620]####
        while (iter.hasNext()) //####[621]####
        {//####[621]####
            int color = toMove;//####[622]####
            Move move = (Move) iter.next();//####[623]####
            doMove(move);//####[624]####
            boolean illegalMove = false;//####[625]####
            if (inCheck(color)) //####[626]####
            illegalMove = true;//####[627]####
            undoMove(move);//####[628]####
            if (illegalMove) //####[629]####
            iter.remove();//####[630]####
        }//####[631]####
    }//####[632]####
//####[638]####
    /**
     * by - if caller is asking whether the square is attacked by white(1) or
     * black(-1)
     *///####[638]####
    private boolean squareAttacked(int square, int by) {//####[638]####
        for (int i = 0; i < knightDeltas.length; i++) //####[640]####
        {//####[640]####
            int square2 = square + knightDeltas[i];//####[641]####
            if ((square2 & 0x88) == 0 && board0x88[square2] == 2 * by) //####[642]####
            return true;//####[643]####
        }//####[644]####
        if (by == -1 && (validSquare(square + 15) && board0x88[square + 15] == -1 || validSquare(square + 17) && board0x88[square + 17] == -1)) //####[647]####
        return true; else if (by == 1 && (validSquare(square - 15) && board0x88[square - 15] == 1 || validSquare(square - 17) && board0x88[square - 17] == 1)) //####[651]####
        return true;//####[654]####
        for (int i = 0; i < queenDeltas.length; i++) //####[657]####
        {//####[657]####
            int delta = queenDeltas[i];//####[658]####
            if (((square + delta) & 0x88) == 0 && board0x88[square + delta] == 6 * by) //####[661]####
            return true;//####[663]####
            int square2 = square;//####[665]####
            while (true) //####[666]####
            {//####[666]####
                square2 += delta;//####[667]####
                if ((square2 & 0x88) != 0) //####[668]####
                break;//####[669]####
                if (board0x88[square2] == 0) //####[670]####
                continue;//####[671]####
                if (board0x88[square2] * by < 0) //####[672]####
                break;//####[673]####
                if (board0x88[square2] * by > 0 && Math.abs(board0x88[square2]) >= 3 && Math.abs(board0x88[square2]) <= 5) //####[674]####
                {//####[676]####
                    if (Math.abs(board0x88[square2]) == 5) //####[677]####
                    return true;//####[678]####
                    if (i <= 3 && Math.abs(board0x88[square2]) == 4) //####[679]####
                    return true;//####[680]####
                    if (i >= 4 && Math.abs(board0x88[square2]) == 3) //####[681]####
                    return true;//####[682]####
                }//####[683]####
                break;//####[684]####
            }//####[685]####
        }//####[686]####
        return false;//####[688]####
    }//####[689]####
//####[691]####
    public String toFEN() {//####[691]####
        String[] symbols = { "", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };//####[692]####
        String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };//####[694]####
        StringBuffer FEN = new StringBuffer(100);//####[696]####
        for (int i = 7; i >= 0; i--) //####[697]####
        {//####[697]####
            int counter = 0;//####[698]####
            for (int j = 0; j <= 7; j++) //####[699]####
            {//####[699]####
                if (board0x88[i * 16 + j] == 0) //####[700]####
                {//####[700]####
                    counter++;//####[701]####
                    continue;//####[702]####
                } else {//####[703]####
                    if (counter > 0) //####[704]####
                    FEN.append(Integer.toString(counter));//####[705]####
                    counter = 0;//####[706]####
                    int index = -1;//####[707]####
                    if (board0x88[i * 16 + j] < 0) //####[708]####
                    index = board0x88[i * 16 + j] * -1 + 6; else if (board0x88[i * 16 + j] > 0) //####[710]####
                    index = board0x88[i * 16 + j];//####[711]####
                    FEN.append(symbols[index]);//####[712]####
                }//####[713]####
            }//####[714]####
            if (counter > 0) //####[715]####
            FEN.append(Integer.toString(counter));//####[716]####
            if (i != 0) //####[717]####
            FEN.append("/");//####[718]####
        }//####[719]####
        if (toMove == 1) //####[722]####
        FEN.append(" w "); else FEN.append(" b ");//####[723]####
        String[] castlingSymbols = { "K", "Q", "k", "q" };//####[728]####
        boolean noCastling = true;//####[729]####
        for (int i = 0; i < 4; i++) //####[730]####
        {//####[730]####
            if (castlingRights[i]) //####[731]####
            {//####[731]####
                FEN.append(castlingSymbols[i]);//####[732]####
                noCastling = false;//####[733]####
            }//####[734]####
        }//####[735]####
        if (noCastling) //####[736]####
        FEN.append("-");//####[737]####
        FEN.append(" ");//####[738]####
        if (enPassant == -1) //####[741]####
        FEN.append("- "); else FEN.append(fileSymbols[enPassant & 7] + Integer.toString(enPassant / 16 + 1) + " ");//####[742]####
        FEN.append(Integer.toString(halfmoves) + " ");//####[748]####
        FEN.append(Integer.toString(moveN));//####[751]####
        return FEN.toString();//####[753]####
    }//####[754]####
//####[756]####
    public void undoMove(Move move) {//####[756]####
        zobrist.undoMove(this, move);//####[758]####
        hashHistory.remove(hashHistory.size() - 1);//####[759]####
        if (toMove == -1) //####[761]####
        moveN--;//####[762]####
        toMove *= -1;//####[763]####
        if (move.piece == 1 && board0x88[move.to] != 1) //####[766]####
        board0x88[move.to] = 1; else if (move.piece == -1 && board0x88[move.to] != -1) //####[768]####
        board0x88[move.to] = -1;//####[769]####
        board0x88[move.from] = move.piece;//####[772]####
        board0x88[move.to] = move.capture;//####[773]####
        if (move.from == 4 && move.to == 6 && move.piece == 6) //####[776]####
        {//####[776]####
            board0x88[7] = 4;//####[777]####
            board0x88[5] = 0;//####[778]####
        } else if (move.from == 4 && move.to == 2 && move.piece == 6) //####[779]####
        {//####[779]####
            board0x88[0] = 4;//####[780]####
            board0x88[3] = 0;//####[781]####
        } else if (move.from == 116 && move.to == 118 && move.piece == -6) //####[782]####
        {//####[782]####
            board0x88[119] = -4;//####[783]####
            board0x88[117] = 0;//####[784]####
        } else if (move.from == 116 && move.to == 114 && move.piece == -6) //####[785]####
        {//####[785]####
            board0x88[112] = -4;//####[786]####
            board0x88[115] = 0;//####[787]####
        }//####[788]####
        halfmoves = move.halfmoves;//####[791]####
        enPassant = move.enPassant;//####[794]####
        if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && move.capture == 0) //####[797]####
        {//####[798]####
            if (move.piece == 1) //####[799]####
            board0x88[move.to - 16] = -1;//####[800]####
            if (move.piece == -1) //####[801]####
            board0x88[move.to + 16] = 1;//####[802]####
        }//####[803]####
        for (int i = 0; i < 4; i++) //####[806]####
        castlingRights[i] = move.castlingRights[i];//####[807]####
    }//####[808]####
//####[810]####
    private boolean validSquare(int square) {//####[810]####
        if ((square & 0x88) == 0) //####[811]####
        return true; else return false;//####[812]####
    }//####[815]####
//####[817]####
    public void printBoard() {//####[817]####
    }//####[824]####
}//####[824]####
