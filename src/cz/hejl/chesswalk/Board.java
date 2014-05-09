package cz.hejl.chesswalk;//####[21]####
//####[21]####
import java.util.ArrayList;//####[23]####
import java.util.Iterator;//####[24]####
import pt.runtime.*;//####[25]####
//####[25]####
//-- ParaTask related imports//####[25]####
import pt.runtime.*;//####[25]####
import java.util.concurrent.ExecutionException;//####[25]####
import java.util.concurrent.locks.*;//####[25]####
import java.lang.reflect.*;//####[25]####
import pt.runtime.GuiThread;//####[25]####
import java.util.concurrent.BlockingQueue;//####[25]####
import java.util.ArrayList;//####[25]####
import java.util.List;//####[25]####
//####[25]####
public class Board {//####[27]####
    static{ParaTask.init();}//####[27]####
    /*  ParaTask helper method to access private/protected slots *///####[27]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[27]####
        if (m.getParameterTypes().length == 0)//####[27]####
            m.invoke(instance);//####[27]####
        else if ((m.getParameterTypes().length == 1))//####[27]####
            m.invoke(instance, arg);//####[27]####
        else //####[27]####
            m.invoke(instance, arg, interResult);//####[27]####
    }//####[27]####
//####[28]####
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";//####[28]####
//####[30]####
    public boolean[] castlingRights = new boolean[4];//####[30]####
//####[31]####
    public int[] board0x88 = new int[128];//####[31]####
//####[32]####
    public int enPassant;//####[32]####
//####[33]####
    public int halfmoves = 0;//####[33]####
//####[34]####
    public int toMove;//####[34]####
//####[35]####
    public long hash;//####[35]####
//####[36]####
    public ArrayList<Long> hashHistory;//####[36]####
//####[40]####
    private int moveN;//####[40]####
//####[43]####
    private int[] bishopDeltas = { 15, 17, -17, -15 };//####[43]####
//####[44]####
    private int[] kingDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };//####[44]####
//####[45]####
    private int[] knightDeltas = { 31, 33, 14, 18, -18, -14, -33, -31 };//####[45]####
//####[46]####
    private int[] queenDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };//####[46]####
//####[47]####
    private int[] rookDeltas = { 1, -16, -1, 16 };//####[47]####
//####[48]####
    private Evaluation evaluation;//####[48]####
//####[49]####
    private Zobrist zobrist = new Zobrist();//####[49]####
//####[51]####
    public Board() {//####[51]####
        evaluation = new Evaluation(this);//####[52]####
        init();//####[53]####
    }//####[54]####
//####[56]####
    public void doMove(Move move) {//####[56]####
        if (toMove == 1) //####[57]####
        moveN++;//####[58]####
        toMove *= -1;//####[59]####
        int diff = move.to - move.from;//####[60]####
        move.halfmoves = halfmoves;//####[63]####
        halfmoves++;//####[64]####
        if (move.piece == 1 || move.piece == -1 || move.capture != 0) //####[65]####
        halfmoves = 0;//####[66]####
        if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && board0x88[move.to] == 0) //####[69]####
        {//####[70]####
            if (move.piece == 1) //####[71]####
            board0x88[move.to - 16] = 0;//####[72]####
            if (move.piece == -1) //####[73]####
            board0x88[move.to + 16] = 0;//####[74]####
        }//####[75]####
        board0x88[move.from] = 0;//####[78]####
        move.capture = board0x88[move.to];//####[79]####
        board0x88[move.to] = move.piece;//####[80]####
        if (move.piece == 1 && move.from / 16 == 6) //####[83]####
        board0x88[move.to] = 5; else if (move.piece == -1 && move.from / 16 == 1) //####[85]####
        board0x88[move.to] = -5;//####[86]####
        if (move.from == 4 && move.to == 6 && move.piece == 6) //####[89]####
        {//####[89]####
            board0x88[7] = 0;//####[90]####
            board0x88[5] = 4;//####[91]####
        } else if (move.from == 4 && move.to == 2 && move.piece == 6) //####[92]####
        {//####[92]####
            board0x88[0] = 0;//####[93]####
            board0x88[3] = 4;//####[94]####
        } else if (move.from == 116 && move.to == 118 && move.piece == -6) //####[95]####
        {//####[95]####
            board0x88[119] = 0;//####[96]####
            board0x88[117] = -4;//####[97]####
        } else if (move.from == 116 && move.to == 114 && move.piece == -6) //####[98]####
        {//####[98]####
            board0x88[112] = 0;//####[99]####
            board0x88[115] = -4;//####[100]####
        }//####[101]####
        move.enPassant = enPassant;//####[104]####
        if ((move.piece == 1 || move.piece == -1) && Math.abs(diff) == 32) //####[105]####
        enPassant = move.from + diff / 2; else enPassant = -1;//####[106]####
        for (int i = 0; i < 4; i++) //####[111]####
        move.castlingRights[i] = castlingRights[i];//####[112]####
        if (board0x88[0] != 4) //####[114]####
        castlingRights[1] = false;//####[115]####
        if (board0x88[7] != 4) //####[116]####
        castlingRights[0] = false;//####[117]####
        if (board0x88[4] != 6) //####[118]####
        {//####[118]####
            castlingRights[0] = false;//####[119]####
            castlingRights[1] = false;//####[120]####
        }//####[121]####
        if (board0x88[112] != -4) //####[122]####
        castlingRights[3] = false;//####[123]####
        if (board0x88[119] != -4) //####[124]####
        castlingRights[2] = false;//####[125]####
        if (board0x88[116] != -6) //####[126]####
        {//####[126]####
            castlingRights[2] = false;//####[127]####
            castlingRights[3] = false;//####[128]####
        }//####[129]####
        zobrist.doMove(this, move);//####[132]####
        hashHistory.add(hash);//####[133]####
        printBoard();//####[134]####
    }//####[135]####
//####[137]####
    public int evaluate() {//####[137]####
        return evaluation.evaluate();//####[138]####
    }//####[139]####
//####[141]####
    public void fromFEN(String FEN) {//####[141]####
        String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };//####[142]####
        String[] symbols = { "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };//####[143]####
        int[] pieces = { 1, 2, 3, 4, 5, 6, -1, -2, -3, -4, -5, -6 };//####[145]####
        int pos = -1;//####[147]####
        int file = 0;//####[148]####
        int rank = 7;//####[149]####
        String c = "";//####[150]####
        while (true) //####[151]####
        {//####[151]####
            pos++;//####[152]####
            c = FEN.substring(pos, pos + 1);//####[153]####
            if (c.equals(" ")) //####[154]####
            break;//####[155]####
            int pieceType = 0;//####[158]####
            for (int i = 0; i < symbols.length; i++) //####[159]####
            {//####[159]####
                if (c.equals(symbols[i])) //####[160]####
                {//####[160]####
                    pieceType = pieces[i];//####[161]####
                    break;//####[162]####
                }//####[163]####
            }//####[164]####
            if (pieceType != 0) //####[165]####
            {//####[165]####
                board0x88[rank * 16 + file] = pieceType;//####[166]####
                file++;//####[167]####
                continue;//####[168]####
            }//####[169]####
            if (c.equals("/")) //####[172]####
            {//####[172]####
                file = 0;//####[173]####
                rank--;//####[174]####
                continue;//####[175]####
            }//####[176]####
            int n = Integer.parseInt(c);//####[179]####
            for (int i = 0; i < n; i++) //####[180]####
            {//####[180]####
                board0x88[rank * 16 + file] = 0;//####[181]####
                file++;//####[182]####
            }//####[183]####
        }//####[184]####
        pos++;//####[187]####
        c = FEN.substring(pos, pos + 1);//####[188]####
        pos++;//####[189]####
        if (c.equals("w")) //####[190]####
        toMove = 1; else if (c.equals("b")) //####[192]####
        toMove = -1;//####[193]####
        for (int i = 0; i < 4; i++) //####[196]####
        castlingRights[i] = false;//####[197]####
        while (true) //####[198]####
        {//####[198]####
            pos++;//####[199]####
            c = FEN.substring(pos, pos + 1);//####[200]####
            if (c.equals(" ")) //####[201]####
            break; else if (c.equals("K")) //####[203]####
            castlingRights[0] = true; else if (c.equals("Q")) //####[205]####
            castlingRights[1] = true; else if (c.equals("k")) //####[207]####
            castlingRights[2] = true; else if (c.equals("q")) //####[209]####
            castlingRights[3] = true;//####[210]####
        }//####[211]####
        pos++;//####[214]####
        c = FEN.substring(pos, pos + 1);//####[215]####
        if (c.equals("-")) //####[216]####
        enPassant = -1; else {//####[218]####
            int index;//####[219]####
            for (index = 0; index < fileSymbols.length; index++) //####[220]####
            {//####[220]####
                if (fileSymbols[index].equals(c)) //####[221]####
                break;//####[222]####
            }//####[223]####
            file = index;//####[224]####
            pos++;//####[225]####
            rank = Integer.parseInt(FEN.substring(pos, pos + 1)) - 1;//####[226]####
            enPassant = rank * 16 + file;//####[227]####
        }//####[228]####
        pos++;//####[229]####
        int startPos = pos + 1;//####[232]####
        while (true) //####[233]####
        {//####[233]####
            pos++;//####[234]####
            c = FEN.substring(pos, pos + 1);//####[235]####
            if (c.equals(" ")) //####[236]####
            break;//####[237]####
        }//####[238]####
        halfmoves = Integer.parseInt(FEN.substring(startPos, pos));//####[239]####
        startPos = pos + 1;//####[242]####
        while (true) //####[243]####
        {//####[243]####
            pos++;//####[244]####
            if (pos == FEN.length()) //####[245]####
            break;//####[246]####
            c = FEN.substring(pos, pos + 1);//####[247]####
        }//####[248]####
        moveN = Integer.parseInt(FEN.substring(startPos, pos));//####[249]####
        zobrist.setHash(this);//####[252]####
        hashHistory = new ArrayList<Long>();//####[255]####
        hashHistory.add(hash);//####[256]####
    }//####[257]####
//####[259]####
    public ArrayList<Move> generateAllMoves() {//####[259]####
        ArrayList<Move> moves = new ArrayList<Move>();//####[260]####
        long startTime = System.nanoTime();//####[262]####
        for (int i = 0; i < 128; i++) //####[266]####
        {//####[266]####
            if ((i & 0x88) != 0) //####[269]####
            continue;//####[270]####
            generateMoves(board0x88[i], i, moves);//####[271]####
        }//####[272]####
        long endTime = System.nanoTime();//####[273]####
        long duration = endTime - startTime;//####[274]####
        System.out.println("Execution time is: " + duration);//####[276]####
        removeIllegalMoves(moves);//####[277]####
        return moves;//####[279]####
    }//####[280]####
//####[282]####
    private static volatile Method __pt__generateMoves_int_int_ArrayListMove_method = null;//####[282]####
    private synchronized static void __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet() {//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            try {//####[282]####
                __pt__generateMoves_int_int_ArrayListMove_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__generateMoves", new Class[] {//####[282]####
                    int.class, int.class, ArrayList.class//####[282]####
                });//####[282]####
            } catch (Exception e) {//####[282]####
                e.printStackTrace();//####[282]####
            }//####[282]####
        }//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(0);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(1);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(1);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(1);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(1);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(0);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0, 1);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(2);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(2);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(1);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(2);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(1);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0, 1);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(2);//####[282]####
        taskinfo.addDependsOn(moves);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(0);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0, 2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(1);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0, 2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(1);//####[282]####
        taskinfo.addDependsOn(from);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(1, 2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(1, 2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setTaskIdArgIndexes(0);//####[282]####
        taskinfo.addDependsOn(piece);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[282]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[282]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[282]####
    }//####[282]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[282]####
        // ensure Method variable is set//####[282]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[282]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[282]####
        }//####[282]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[282]####
        taskinfo.setIsPipeline(true);//####[282]####
        taskinfo.setParameters(piece, from, moves);//####[282]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[282]####
        taskinfo.setInstance(this);//####[282]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[282]####
    }//####[282]####
    public void __pt__generateMoves(int piece, int from, ArrayList<Move> moves) {//####[282]####
        if (toMove * piece < 0) //####[283]####
        return;//####[284]####
        int pieceType = Math.abs(piece);//####[285]####
        if (pieceType == 1) //####[287]####
        {//####[287]####
            generatePawnMoves(piece, from, moves);//####[288]####
        } else if (pieceType == 2 || pieceType == 6) //####[291]####
        {//####[291]####
            generateNonSlidingMoves(piece, from, moves);//####[292]####
        } else if (pieceType >= 3 && pieceType <= 5) //####[295]####
        {//####[295]####
            generateSlidingMoves(piece, from, moves);//####[296]####
        }//####[297]####
    }//####[298]####
//####[298]####
//####[300]####
    private void generateNonSlidingMoves(int piece, int from, ArrayList<Move> moves) {//####[301]####
        int[] deltas;//####[302]####
        if (piece == 2 || piece == -2) //####[303]####
        deltas = knightDeltas; else deltas = kingDeltas;//####[304]####
        for (int i = 0; i < deltas.length; i++) //####[308]####
        {//####[308]####
            int to = from + deltas[i];//####[309]####
            if ((to & 0x88) != 0) //####[310]####
            continue;//####[311]####
            if (board0x88[to] > 0 && piece > 0) //####[312]####
            continue;//####[313]####
            if (board0x88[to] < 0 && piece < 0) //####[314]####
            continue;//####[315]####
            moves.add(new Move(this, from, to));//####[316]####
        }//####[317]####
        if (piece == 6) //####[320]####
        {//####[320]####
            if (castlingRights[0]) //####[321]####
            {//####[321]####
                if (board0x88[5] == 0 && board0x88[6] == 0) //####[322]####
                {//####[322]####
                    if (squareAttacked(4, -1) == false && squareAttacked(5, -1) == false && squareAttacked(6, -1) == false) //####[323]####
                    {//####[325]####
                        moves.add(new Move(this, from, from + 2));//####[326]####
                    }//####[327]####
                }//####[328]####
            }//####[329]####
            if (castlingRights[1]) //####[330]####
            {//####[330]####
                if (board0x88[1] == 0 && board0x88[2] == 0 && board0x88[3] == 0) //####[331]####
                {//####[331]####
                    if (squareAttacked(2, -1) == false && squareAttacked(3, -1) == false && squareAttacked(4, -1) == false) //####[332]####
                    {//####[334]####
                        moves.add(new Move(this, from, from - 2));//####[335]####
                    }//####[336]####
                }//####[337]####
            }//####[338]####
        } else if (piece == -6) //####[339]####
        {//####[339]####
            if (castlingRights[2]) //####[340]####
            {//####[340]####
                if (board0x88[117] == 0 && board0x88[118] == 0) //####[341]####
                {//####[341]####
                    if (squareAttacked(116, 1) == false && squareAttacked(117, 1) == false && squareAttacked(118, 1) == false) //####[342]####
                    {//####[344]####
                        moves.add(new Move(this, from, from + 2));//####[345]####
                    }//####[346]####
                }//####[347]####
            }//####[348]####
            if (castlingRights[3]) //####[349]####
            {//####[349]####
                if (board0x88[113] == 0 && board0x88[114] == 0 && board0x88[115] == 0) //####[350]####
                {//####[351]####
                    if (squareAttacked(114, 1) == false && squareAttacked(115, 1) == false && squareAttacked(116, 1) == false) //####[352]####
                    {//####[354]####
                        moves.add(new Move(this, from, from - 2));//####[355]####
                    }//####[356]####
                }//####[357]####
            }//####[358]####
        }//####[359]####
    }//####[360]####
//####[362]####
    private void generateSlidingMoves(int piece, int from, ArrayList<Move> moves) {//####[362]####
        int[] deltas;//####[363]####
        if (piece == 3 || piece == -3) //####[364]####
        deltas = bishopDeltas; else if (piece == 4 || piece == -4) //####[366]####
        deltas = rookDeltas; else deltas = queenDeltas;//####[367]####
        for (int i = 0; i < deltas.length; i++) //####[371]####
        {//####[371]####
            int delta = deltas[i];//####[372]####
            int to = from;//####[373]####
            while (true) //####[374]####
            {//####[374]####
                to += delta;//####[375]####
                if ((to & 0x88) != 0) //####[376]####
                break;//####[377]####
                if (board0x88[to] > 0 && piece > 0 || board0x88[to] < 0 && piece < 0) //####[378]####
                break;//####[380]####
                if (board0x88[to] > 0 && piece < 0 || board0x88[to] < 0 && piece > 0) //####[381]####
                {//####[382]####
                    moves.add(new Move(this, from, to));//####[383]####
                    break;//####[384]####
                }//####[385]####
                moves.add(new Move(this, from, to));//####[386]####
            }//####[387]####
        }//####[388]####
    }//####[389]####
//####[391]####
    private void generatePawnMoves(int piece, int from, ArrayList<Move> moves) {//####[391]####
        if (piece == 1) //####[393]####
        {//####[393]####
            if (board0x88[from + 16] == 0) //####[395]####
            moves.add(new Move(this, from, from + 16));//####[396]####
            if (from / 16 == 1 && board0x88[from + 16] == 0 && board0x88[from + 2 * 16] == 0) //####[398]####
            moves.add(new Move(this, from, from + 2 * 16));//####[400]####
            if (board0x88[from + 15] < 0 && ((from + 15) & 0x88) == 0) //####[402]####
            moves.add(new Move(this, from, from + 15));//####[403]####
            if (board0x88[from + 17] < 0 && ((from + 17) & 0x88) == 0) //####[404]####
            moves.add(new Move(this, from, from + 17));//####[405]####
            if (enPassant != -1 && enPassant / 16 == 5) //####[407]####
            {//####[407]####
                if (from + 15 == enPassant || from + 17 == enPassant) //####[408]####
                moves.add(new Move(this, from, enPassant));//####[409]####
            }//####[410]####
        } else if (piece == -1) //####[414]####
        {//####[414]####
            if (board0x88[from - 16] == 0) //####[416]####
            moves.add(new Move(this, from, from - 16));//####[417]####
            if (from / 16 == 6 && board0x88[from - 16] == 0 && board0x88[from - 2 * 16] == 0) //####[419]####
            moves.add(new Move(this, from, from - 2 * 16));//####[421]####
            if (((from - 15) & 0x88) == 0 && board0x88[from - 15] > 0) //####[423]####
            moves.add(new Move(this, from, from - 15));//####[424]####
            if (((from - 17) & 0x88) == 0 && board0x88[from - 17] > 0) //####[425]####
            moves.add(new Move(this, from, from - 17));//####[426]####
            if (enPassant != -1 && enPassant / 16 == 2) //####[428]####
            {//####[428]####
                if (from - 15 == enPassant || from - 17 == enPassant) //####[429]####
                moves.add(new Move(this, from, enPassant));//####[430]####
            }//####[431]####
        }//####[432]####
    }//####[433]####
//####[436]####
    /** color can be 1 for white or -1 for black *///####[436]####
    public boolean inCheck(int color) {//####[436]####
        int king = -1;//####[438]####
        for (int i = 0; i < 128; i++) //####[439]####
        {//####[439]####
            if ((i & 0x88) != 0) //####[440]####
            continue;//####[441]####
            if (board0x88[i] == 6 * color) //####[442]####
            {//####[442]####
                king = i;//####[443]####
                break;//####[444]####
            }//####[445]####
        }//####[446]####
        return squareAttacked(king, color * -1);//####[448]####
    }//####[449]####
//####[451]####
    public boolean isCheckmate() {//####[451]####
        if (generateAllMoves().size() == 0 && inCheck(toMove)) //####[452]####
        {//####[452]####
            return true;//####[453]####
        } else return false;//####[454]####
    }//####[456]####
//####[458]####
    public boolean isDraw50Move() {//####[458]####
        if (halfmoves >= 100) //####[459]####
        return true; else return false;//####[460]####
    }//####[463]####
//####[465]####
    public boolean isEndgame() {//####[465]####
        boolean wQueen = false;//####[466]####
        boolean bQueen = false;//####[467]####
        int wRooks = 0;//####[468]####
        int bRooks = 0;//####[469]####
        int wMinors = 0;//####[470]####
        int bMinors = 0;//####[471]####
        for (int i = 0; i < 128; i++) //####[473]####
        {//####[473]####
            if ((i & 0x88) != 0) //####[474]####
            continue;//####[475]####
            int piece = board0x88[i];//####[476]####
            switch(piece) {//####[477]####
                case 5://####[477]####
                    wQueen = true;//####[479]####
                case -5://####[479]####
                    bQueen = true;//####[481]####
                case 4://####[481]####
                    wRooks++;//####[483]####
                case -4://####[483]####
                    bRooks++;//####[485]####
                case 3://####[485]####
                    wMinors--;//####[487]####
                case -3://####[487]####
                    bMinors--;//####[489]####
                case 2://####[489]####
                    wMinors--;//####[491]####
                case -2://####[491]####
                    bMinors--;//####[493]####
            }//####[493]####
        }//####[495]####
        boolean endgame = true;//####[497]####
        if (wQueen && (wMinors > 1 || wRooks > 0)) //####[498]####
        endgame = false;//####[499]####
        if (bQueen && (bMinors > 1 || bRooks > 0)) //####[500]####
        endgame = false;//####[501]####
        return endgame;//####[503]####
    }//####[504]####
//####[506]####
    public boolean isRepetition() {//####[506]####
        int hits = 1;//####[507]####
        for (int i = hashHistory.size() - 2; i >= 0; i--) //####[508]####
        {//####[508]####
            if (hashHistory.get(i) == hash) //####[509]####
            hits++;//####[510]####
        }//####[511]####
        if (hits >= 3) //####[513]####
        {//####[513]####
            return true;//####[514]####
        } else return false;//####[515]####
    }//####[517]####
//####[519]####
    public boolean isStalemate() {//####[519]####
        if (generateAllMoves().size() == 0 && !inCheck(toMove)) //####[520]####
        {//####[520]####
            return true;//####[521]####
        } else return false;//####[522]####
    }//####[524]####
//####[526]####
    public void init() {//####[526]####
        fromFEN(STARTING_FEN);//####[527]####
        enPassant = -1;//####[530]####
        for (int i = 0; i < 4; i++) //####[533]####
        castlingRights[i] = true;//####[534]####
        toMove = 1;//####[536]####
        moveN = 0;//####[538]####
    }//####[539]####
//####[541]####
    public boolean[] legalMovesMap(int from) {//####[541]####
        ArrayList<Move> moves = new ArrayList<Move>();//####[542]####
        generateMoves(board0x88[from], from, moves);//####[543]####
        removeIllegalMoves(moves);//####[544]####
        boolean[] map = new boolean[128];//####[546]####
        for (int i = 0; i < moves.size(); i++) //####[547]####
        {//####[547]####
            map[moves.get(i).to] = true;//####[548]####
        }//####[549]####
        return map;//####[551]####
    }//####[552]####
//####[554]####
    private void removeIllegalMoves(ArrayList<Move> moves) {//####[554]####
        Iterator<Move> iter = moves.iterator();//####[555]####
        while (iter.hasNext()) //####[556]####
        {//####[556]####
            int color = toMove;//####[557]####
            Move move = (Move) iter.next();//####[558]####
            doMove(move);//####[559]####
            boolean illegalMove = false;//####[560]####
            if (inCheck(color)) //####[561]####
            illegalMove = true;//####[562]####
            undoMove(move);//####[563]####
            if (illegalMove) //####[564]####
            iter.remove();//####[565]####
        }//####[566]####
    }//####[567]####
//####[573]####
    /**
     * by - if caller is asking whether the square is attacked by white(1) or
     * black(-1)
     *///####[573]####
    private boolean squareAttacked(int square, int by) {//####[573]####
        for (int i = 0; i < knightDeltas.length; i++) //####[575]####
        {//####[575]####
            int square2 = square + knightDeltas[i];//####[576]####
            if ((square2 & 0x88) == 0 && board0x88[square2] == 2 * by) //####[577]####
            return true;//####[578]####
        }//####[579]####
        if (by == -1 && (validSquare(square + 15) && board0x88[square + 15] == -1 || validSquare(square + 17) && board0x88[square + 17] == -1)) //####[582]####
        return true; else if (by == 1 && (validSquare(square - 15) && board0x88[square - 15] == 1 || validSquare(square - 17) && board0x88[square - 17] == 1)) //####[586]####
        return true;//####[589]####
        for (int i = 0; i < queenDeltas.length; i++) //####[592]####
        {//####[592]####
            int delta = queenDeltas[i];//####[593]####
            if (((square + delta) & 0x88) == 0 && board0x88[square + delta] == 6 * by) //####[596]####
            return true;//####[598]####
            int square2 = square;//####[600]####
            while (true) //####[601]####
            {//####[601]####
                square2 += delta;//####[602]####
                if ((square2 & 0x88) != 0) //####[603]####
                break;//####[604]####
                if (board0x88[square2] == 0) //####[605]####
                continue;//####[606]####
                if (board0x88[square2] * by < 0) //####[607]####
                break;//####[608]####
                if (board0x88[square2] * by > 0 && Math.abs(board0x88[square2]) >= 3 && Math.abs(board0x88[square2]) <= 5) //####[609]####
                {//####[611]####
                    if (Math.abs(board0x88[square2]) == 5) //####[612]####
                    return true;//####[613]####
                    if (i <= 3 && Math.abs(board0x88[square2]) == 4) //####[614]####
                    return true;//####[615]####
                    if (i >= 4 && Math.abs(board0x88[square2]) == 3) //####[616]####
                    return true;//####[617]####
                }//####[618]####
                break;//####[619]####
            }//####[620]####
        }//####[621]####
        return false;//####[623]####
    }//####[624]####
//####[626]####
    public String toFEN() {//####[626]####
        String[] symbols = { "", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };//####[627]####
        String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };//####[629]####
        StringBuffer FEN = new StringBuffer(100);//####[631]####
        for (int i = 7; i >= 0; i--) //####[632]####
        {//####[632]####
            int counter = 0;//####[633]####
            for (int j = 0; j <= 7; j++) //####[634]####
            {//####[634]####
                if (board0x88[i * 16 + j] == 0) //####[635]####
                {//####[635]####
                    counter++;//####[636]####
                    continue;//####[637]####
                } else {//####[638]####
                    if (counter > 0) //####[639]####
                    FEN.append(Integer.toString(counter));//####[640]####
                    counter = 0;//####[641]####
                    int index = -1;//####[642]####
                    if (board0x88[i * 16 + j] < 0) //####[643]####
                    index = board0x88[i * 16 + j] * -1 + 6; else if (board0x88[i * 16 + j] > 0) //####[645]####
                    index = board0x88[i * 16 + j];//####[646]####
                    FEN.append(symbols[index]);//####[647]####
                }//####[648]####
            }//####[649]####
            if (counter > 0) //####[650]####
            FEN.append(Integer.toString(counter));//####[651]####
            if (i != 0) //####[652]####
            FEN.append("/");//####[653]####
        }//####[654]####
        if (toMove == 1) //####[657]####
        FEN.append(" w "); else FEN.append(" b ");//####[658]####
        String[] castlingSymbols = { "K", "Q", "k", "q" };//####[663]####
        boolean noCastling = true;//####[664]####
        for (int i = 0; i < 4; i++) //####[665]####
        {//####[665]####
            if (castlingRights[i]) //####[666]####
            {//####[666]####
                FEN.append(castlingSymbols[i]);//####[667]####
                noCastling = false;//####[668]####
            }//####[669]####
        }//####[670]####
        if (noCastling) //####[671]####
        FEN.append("-");//####[672]####
        FEN.append(" ");//####[673]####
        if (enPassant == -1) //####[676]####
        FEN.append("- "); else FEN.append(fileSymbols[enPassant & 7] + Integer.toString(enPassant / 16 + 1) + " ");//####[677]####
        FEN.append(Integer.toString(halfmoves) + " ");//####[683]####
        FEN.append(Integer.toString(moveN));//####[686]####
        return FEN.toString();//####[688]####
    }//####[689]####
//####[691]####
    public void undoMove(Move move) {//####[691]####
        zobrist.undoMove(this, move);//####[693]####
        hashHistory.remove(hashHistory.size() - 1);//####[694]####
        if (toMove == -1) //####[696]####
        moveN--;//####[697]####
        toMove *= -1;//####[698]####
        if (move.piece == 1 && board0x88[move.to] != 1) //####[701]####
        board0x88[move.to] = 1; else if (move.piece == -1 && board0x88[move.to] != -1) //####[703]####
        board0x88[move.to] = -1;//####[704]####
        board0x88[move.from] = move.piece;//####[707]####
        board0x88[move.to] = move.capture;//####[708]####
        if (move.from == 4 && move.to == 6 && move.piece == 6) //####[711]####
        {//####[711]####
            board0x88[7] = 4;//####[712]####
            board0x88[5] = 0;//####[713]####
        } else if (move.from == 4 && move.to == 2 && move.piece == 6) //####[714]####
        {//####[714]####
            board0x88[0] = 4;//####[715]####
            board0x88[3] = 0;//####[716]####
        } else if (move.from == 116 && move.to == 118 && move.piece == -6) //####[717]####
        {//####[717]####
            board0x88[119] = -4;//####[718]####
            board0x88[117] = 0;//####[719]####
        } else if (move.from == 116 && move.to == 114 && move.piece == -6) //####[720]####
        {//####[720]####
            board0x88[112] = -4;//####[721]####
            board0x88[115] = 0;//####[722]####
        }//####[723]####
        halfmoves = move.halfmoves;//####[726]####
        enPassant = move.enPassant;//####[729]####
        if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && move.capture == 0) //####[732]####
        {//####[733]####
            if (move.piece == 1) //####[734]####
            board0x88[move.to - 16] = -1;//####[735]####
            if (move.piece == -1) //####[736]####
            board0x88[move.to + 16] = 1;//####[737]####
        }//####[738]####
        for (int i = 0; i < 4; i++) //####[741]####
        castlingRights[i] = move.castlingRights[i];//####[742]####
    }//####[743]####
//####[745]####
    private boolean validSquare(int square) {//####[745]####
        if ((square & 0x88) == 0) //####[746]####
        return true; else return false;//####[747]####
    }//####[750]####
//####[752]####
    public void printBoard() {//####[752]####
        for (int i = 0; i < board0x88.length; i++) //####[753]####
        {//####[753]####
            if ((i % 8) == 0) //####[754]####
            {//####[754]####
                System.out.println();//####[755]####
            }//####[756]####
            System.out.print(board0x88[i]);//####[757]####
        }//####[758]####
    }//####[759]####
}//####[759]####
