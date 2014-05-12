<<<<<<< Updated upstream
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
        for (int i = 0; i < 128; i++) //####[261]####
        {//####[261]####
            if ((i & 0x88) != 0) //####[264]####
            continue;//####[265]####
            generateMoves(board0x88[i], i, moves);//####[266]####
        }//####[267]####
        removeIllegalMoves(moves);//####[269]####
        return moves;//####[271]####
    }//####[272]####
//####[274]####
    private static volatile Method __pt__generateMoves_int_int_ArrayListMove_method = null;//####[274]####
    private synchronized static void __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet() {//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            try {//####[274]####
                __pt__generateMoves_int_int_ArrayListMove_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__generateMoves", new Class[] {//####[274]####
                    int.class, int.class, ArrayList.class//####[274]####
                });//####[274]####
            } catch (Exception e) {//####[274]####
                e.printStackTrace();//####[274]####
            }//####[274]####
        }//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(0);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(1);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(1);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(1);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(1);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(0);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, ArrayList<Move> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0, 1);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(2);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(2);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(1);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(2);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(1);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, TaskID<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0, 1);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(2);//####[274]####
        taskinfo.addDependsOn(moves);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(0);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, int from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0, 2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(1);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, TaskID<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0, 2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(1);//####[274]####
        taskinfo.addDependsOn(from);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(int piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(1, 2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(TaskID<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(1, 2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setTaskIdArgIndexes(0);//####[274]####
        taskinfo.addDependsOn(piece);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves) {//####[274]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[274]####
        return generateMoves(piece, from, moves, new TaskInfo());//####[274]####
    }//####[274]####
    private TaskIDGroup<Void> generateMoves(BlockingQueue<Integer> piece, BlockingQueue<Integer> from, BlockingQueue<ArrayList<Move>> moves, TaskInfo taskinfo) {//####[274]####
        // ensure Method variable is set//####[274]####
        if (__pt__generateMoves_int_int_ArrayListMove_method == null) {//####[274]####
            __pt__generateMoves_int_int_ArrayListMove_ensureMethodVarSet();//####[274]####
        }//####[274]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[274]####
        taskinfo.setIsPipeline(true);//####[274]####
        taskinfo.setParameters(piece, from, moves);//####[274]####
        taskinfo.setMethod(__pt__generateMoves_int_int_ArrayListMove_method);//####[274]####
        taskinfo.setInstance(this);//####[274]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[274]####
    }//####[274]####
    public void __pt__generateMoves(int piece, int from, ArrayList<Move> moves) {//####[274]####
        if (toMove * piece < 0) //####[275]####
        return;//####[276]####
        int pieceType = Math.abs(piece);//####[277]####
        if (pieceType == 1) //####[279]####
        {//####[279]####
            generatePawnMoves(piece, from, moves);//####[280]####
        } else if (pieceType == 2 || pieceType == 6) //####[283]####
        {//####[283]####
            generateNonSlidingMoves(piece, from, moves);//####[284]####
        } else if (pieceType >= 3 && pieceType <= 5) //####[287]####
        {//####[287]####
            generateSlidingMoves(piece, from, moves);//####[288]####
        }//####[289]####
    }//####[290]####
//####[290]####
//####[292]####
    private void generateNonSlidingMoves(int piece, int from, ArrayList<Move> moves) {//####[293]####
        int[] deltas;//####[294]####
        if (piece == 2 || piece == -2) //####[295]####
        deltas = knightDeltas; else deltas = kingDeltas;//####[296]####
        for (int i = 0; i < deltas.length; i++) //####[300]####
        {//####[300]####
            int to = from + deltas[i];//####[301]####
            if ((to & 0x88) != 0) //####[302]####
            continue;//####[303]####
            if (board0x88[to] > 0 && piece > 0) //####[304]####
            continue;//####[305]####
            if (board0x88[to] < 0 && piece < 0) //####[306]####
            continue;//####[307]####
            moves.add(new Move(this, from, to));//####[308]####
        }//####[309]####
        if (piece == 6) //####[312]####
        {//####[312]####
            if (castlingRights[0]) //####[313]####
            {//####[313]####
                if (board0x88[5] == 0 && board0x88[6] == 0) //####[314]####
                {//####[314]####
                    if (squareAttacked(4, -1) == false && squareAttacked(5, -1) == false && squareAttacked(6, -1) == false) //####[315]####
                    {//####[317]####
                        moves.add(new Move(this, from, from + 2));//####[318]####
                    }//####[319]####
                }//####[320]####
            }//####[321]####
            if (castlingRights[1]) //####[322]####
            {//####[322]####
                if (board0x88[1] == 0 && board0x88[2] == 0 && board0x88[3] == 0) //####[323]####
                {//####[323]####
                    if (squareAttacked(2, -1) == false && squareAttacked(3, -1) == false && squareAttacked(4, -1) == false) //####[324]####
                    {//####[326]####
                        moves.add(new Move(this, from, from - 2));//####[327]####
                    }//####[328]####
                }//####[329]####
            }//####[330]####
        } else if (piece == -6) //####[331]####
        {//####[331]####
            if (castlingRights[2]) //####[332]####
            {//####[332]####
                if (board0x88[117] == 0 && board0x88[118] == 0) //####[333]####
                {//####[333]####
                    if (squareAttacked(116, 1) == false && squareAttacked(117, 1) == false && squareAttacked(118, 1) == false) //####[334]####
                    {//####[336]####
                        moves.add(new Move(this, from, from + 2));//####[337]####
                    }//####[338]####
                }//####[339]####
            }//####[340]####
            if (castlingRights[3]) //####[341]####
            {//####[341]####
                if (board0x88[113] == 0 && board0x88[114] == 0 && board0x88[115] == 0) //####[342]####
                {//####[343]####
                    if (squareAttacked(114, 1) == false && squareAttacked(115, 1) == false && squareAttacked(116, 1) == false) //####[344]####
                    {//####[346]####
                        moves.add(new Move(this, from, from - 2));//####[347]####
                    }//####[348]####
                }//####[349]####
            }//####[350]####
        }//####[351]####
    }//####[352]####
//####[354]####
    private void generateSlidingMoves(int piece, int from, ArrayList<Move> moves) {//####[354]####
        int[] deltas;//####[355]####
        if (piece == 3 || piece == -3) //####[356]####
        deltas = bishopDeltas; else if (piece == 4 || piece == -4) //####[358]####
        deltas = rookDeltas; else deltas = queenDeltas;//####[359]####
        for (int i = 0; i < deltas.length; i++) //####[363]####
        {//####[363]####
            int delta = deltas[i];//####[364]####
            int to = from;//####[365]####
            while (true) //####[366]####
            {//####[366]####
                to += delta;//####[367]####
                if ((to & 0x88) != 0) //####[368]####
                break;//####[369]####
                if (board0x88[to] > 0 && piece > 0 || board0x88[to] < 0 && piece < 0) //####[370]####
                break;//####[372]####
                if (board0x88[to] > 0 && piece < 0 || board0x88[to] < 0 && piece > 0) //####[373]####
                {//####[374]####
                    moves.add(new Move(this, from, to));//####[375]####
                    break;//####[376]####
                }//####[377]####
                moves.add(new Move(this, from, to));//####[378]####
            }//####[379]####
        }//####[380]####
    }//####[381]####
//####[383]####
    private void generatePawnMoves(int piece, int from, ArrayList<Move> moves) {//####[383]####
        if (piece == 1) //####[385]####
        {//####[385]####
            if (board0x88[from + 16] == 0) //####[387]####
            moves.add(new Move(this, from, from + 16));//####[388]####
            if (from / 16 == 1 && board0x88[from + 16] == 0 && board0x88[from + 2 * 16] == 0) //####[390]####
            moves.add(new Move(this, from, from + 2 * 16));//####[392]####
            if (board0x88[from + 15] < 0 && ((from + 15) & 0x88) == 0) //####[394]####
            moves.add(new Move(this, from, from + 15));//####[395]####
            if (board0x88[from + 17] < 0 && ((from + 17) & 0x88) == 0) //####[396]####
            moves.add(new Move(this, from, from + 17));//####[397]####
            if (enPassant != -1 && enPassant / 16 == 5) //####[399]####
            {//####[399]####
                if (from + 15 == enPassant || from + 17 == enPassant) //####[400]####
                moves.add(new Move(this, from, enPassant));//####[401]####
            }//####[402]####
        } else if (piece == -1) //####[406]####
        {//####[406]####
            if (board0x88[from - 16] == 0) //####[408]####
            moves.add(new Move(this, from, from - 16));//####[409]####
            if (from / 16 == 6 && board0x88[from - 16] == 0 && board0x88[from - 2 * 16] == 0) //####[411]####
            moves.add(new Move(this, from, from - 2 * 16));//####[413]####
            if (((from - 15) & 0x88) == 0 && board0x88[from - 15] > 0) //####[415]####
            moves.add(new Move(this, from, from - 15));//####[416]####
            if (((from - 17) & 0x88) == 0 && board0x88[from - 17] > 0) //####[417]####
            moves.add(new Move(this, from, from - 17));//####[418]####
            if (enPassant != -1 && enPassant / 16 == 2) //####[420]####
            {//####[420]####
                if (from - 15 == enPassant || from - 17 == enPassant) //####[421]####
                moves.add(new Move(this, from, enPassant));//####[422]####
            }//####[423]####
        }//####[424]####
    }//####[425]####
//####[428]####
    /** color can be 1 for white or -1 for black *///####[428]####
    public boolean inCheck(int color) {//####[428]####
        int king = -1;//####[430]####
        for (int i = 0; i < 128; i++) //####[431]####
        {//####[431]####
            if ((i & 0x88) != 0) //####[432]####
            continue;//####[433]####
            if (board0x88[i] == 6 * color) //####[434]####
            {//####[434]####
                king = i;//####[435]####
                break;//####[436]####
            }//####[437]####
        }//####[438]####
        return squareAttacked(king, color * -1);//####[440]####
    }//####[441]####
//####[443]####
    public boolean isCheckmate() {//####[443]####
        if (generateAllMoves().size() == 0 && inCheck(toMove)) //####[444]####
        {//####[444]####
            return true;//####[445]####
        } else return false;//####[446]####
    }//####[448]####
//####[450]####
    public boolean isDraw50Move() {//####[450]####
        if (halfmoves >= 100) //####[451]####
        return true; else return false;//####[452]####
    }//####[455]####
//####[457]####
    public boolean isEndgame() {//####[457]####
        boolean wQueen = false;//####[458]####
        boolean bQueen = false;//####[459]####
        int wRooks = 0;//####[460]####
        int bRooks = 0;//####[461]####
        int wMinors = 0;//####[462]####
        int bMinors = 0;//####[463]####
        for (int i = 0; i < 128; i++) //####[465]####
        {//####[465]####
            if ((i & 0x88) != 0) //####[466]####
            continue;//####[467]####
            int piece = board0x88[i];//####[468]####
            switch(piece) {//####[469]####
                case 5://####[469]####
                    wQueen = true;//####[471]####
                case -5://####[471]####
                    bQueen = true;//####[473]####
                case 4://####[473]####
                    wRooks++;//####[475]####
                case -4://####[475]####
                    bRooks++;//####[477]####
                case 3://####[477]####
                    wMinors--;//####[479]####
                case -3://####[479]####
                    bMinors--;//####[481]####
                case 2://####[481]####
                    wMinors--;//####[483]####
                case -2://####[483]####
                    bMinors--;//####[485]####
            }//####[485]####
        }//####[487]####
        boolean endgame = true;//####[489]####
        if (wQueen && (wMinors > 1 || wRooks > 0)) //####[490]####
        endgame = false;//####[491]####
        if (bQueen && (bMinors > 1 || bRooks > 0)) //####[492]####
        endgame = false;//####[493]####
        return endgame;//####[495]####
    }//####[496]####
//####[498]####
    public boolean isRepetition() {//####[498]####
        int hits = 1;//####[499]####
        for (int i = hashHistory.size() - 2; i >= 0; i--) //####[500]####
        {//####[500]####
            if (hashHistory.get(i) == hash) //####[501]####
            hits++;//####[502]####
        }//####[503]####
        if (hits >= 3) //####[505]####
        {//####[505]####
            return true;//####[506]####
        } else return false;//####[507]####
    }//####[509]####
//####[511]####
    public boolean isStalemate() {//####[511]####
        if (generateAllMoves().size() == 0 && !inCheck(toMove)) //####[512]####
        {//####[512]####
            return true;//####[513]####
        } else return false;//####[514]####
    }//####[516]####
//####[518]####
    public void init() {//####[518]####
        fromFEN(STARTING_FEN);//####[519]####
        enPassant = -1;//####[522]####
        for (int i = 0; i < 4; i++) //####[525]####
        castlingRights[i] = true;//####[526]####
        toMove = 1;//####[528]####
        moveN = 0;//####[530]####
    }//####[531]####
//####[533]####
    public boolean[] legalMovesMap(int from) {//####[533]####
        ArrayList<Move> moves = new ArrayList<Move>();//####[534]####
        generateMoves(board0x88[from], from, moves);//####[535]####
        removeIllegalMoves(moves);//####[536]####
        boolean[] map = new boolean[128];//####[538]####
        for (int i = 0; i < moves.size(); i++) //####[539]####
        {//####[539]####
            map[moves.get(i).to] = true;//####[540]####
        }//####[541]####
        return map;//####[543]####
    }//####[544]####
//####[546]####
    private void removeIllegalMoves(ArrayList<Move> moves) {//####[546]####
        Iterator<Move> iter = moves.iterator();//####[547]####
        while (iter.hasNext()) //####[548]####
        {//####[548]####
            int color = toMove;//####[549]####
            Move move = (Move) iter.next();//####[550]####
            doMove(move);//####[551]####
            boolean illegalMove = false;//####[552]####
            if (inCheck(color)) //####[553]####
            illegalMove = true;//####[554]####
            undoMove(move);//####[555]####
            if (illegalMove) //####[556]####
            iter.remove();//####[557]####
        }//####[558]####
    }//####[559]####
//####[565]####
=======
/*
 * Copyright (C) 2010 Franti��ek Hejl
 *
 * This file is part of Chesswalk.
 *
 * Chesswalk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chesswalk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.hejl.chesswalk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Board {
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public boolean[] castlingRights = new boolean[4];
    public int[] board0x88 = new int[128];
    public int enPassant;
    public int halfmoves = 0;
    public int toMove; // whose turn it is, 1 - white, -1 - black
    public long hash;
    public ArrayList<Long> hashHistory;

    private int moveN;
    private int[] bishopDeltas = { 15, 17, -17, -15 };
    private int[] kingDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };
    private int[] knightDeltas = { 31, 33, 14, 18, -18, -14, -33, -31 };
    private int[] queenDeltas = { 1, -16, -1, 16, 15, 17, -17, -15 };
    private int[] rookDeltas = { 1, -16, -1, 16 };
    private Evaluation evaluation;
    private Zobrist zobrist = new Zobrist();
    
    private static AtomicLong executionTime;
    private static AtomicInteger numOfExecutionTimes;
    private ExecutorService es = Executors.newFixedThreadPool(2);
    
    static{
    	executionTime = new AtomicLong((long) 0.00);
    	numOfExecutionTimes = new AtomicInteger(0);
    	
    }

    public Board() {
    
        evaluation = new Evaluation(this);
        init();
        
    }

    public void doMove(Move move) {
        if (toMove == 1)
            moveN++;
        toMove *= -1;
        int diff = move.to - move.from;

        // update hlafmoves
        move.halfmoves = halfmoves;
        halfmoves++;
        if (move.piece == 1 || move.piece == -1 || move.capture != 0)
            halfmoves = 0;

        // en passant capture
        if ((move.piece == 1 || move.piece == -1)
                && (move.from & 7) != (move.to & 7) && board0x88[move.to] == 0) {
            if (move.piece == 1)
                board0x88[move.to - 16] = 0;
            if (move.piece == -1)
                board0x88[move.to + 16] = 0;
        }

        // transfer pieces
        board0x88[move.from] = 0;
        move.capture = board0x88[move.to];
        board0x88[move.to] = move.piece;

        // promoting
        if (move.piece == 1 && move.from / 16 == 6)
            board0x88[move.to] = 5;
        else if (move.piece == -1 && move.from / 16 == 1)
            board0x88[move.to] = -5;

        // castling
        if (move.from == 4 && move.to == 6 && move.piece == 6) {
            board0x88[7] = 0;
            board0x88[5] = 4;
        } else if (move.from == 4 && move.to == 2 && move.piece == 6) {
            board0x88[0] = 0;
            board0x88[3] = 4;
        } else if (move.from == 116 && move.to == 118 && move.piece == -6) {
            board0x88[119] = 0;
            board0x88[117] = -4;
        } else if (move.from == 116 && move.to == 114 && move.piece == -6) {
            board0x88[112] = 0;
            board0x88[115] = -4;
        }

        // set en passant
        move.enPassant = enPassant;
        if ((move.piece == 1 || move.piece == -1) && Math.abs(diff) == 32)
            enPassant = move.from + diff / 2;
        else
            enPassant = -1;

        // handle castling rights
        for (int i = 0; i < 4; i++)
            move.castlingRights[i] = castlingRights[i]; // copy current rights

        if (board0x88[0] != 4)
            castlingRights[1] = false; // white queenside
        if (board0x88[7] != 4)
            castlingRights[0] = false; // white kingside
        if (board0x88[4] != 6) { // white both
            castlingRights[0] = false;
            castlingRights[1] = false;
        }
        if (board0x88[112] != -4)
            castlingRights[3] = false; // black queenside
        if (board0x88[119] != -4)
            castlingRights[2] = false; // black kingside
        if (board0x88[116] != -6) { // black both
            castlingRights[2] = false;
            castlingRights[3] = false;
        }

        // update hash and hashHistory
        zobrist.doMove(this, move);
        hashHistory.add(hash);
    }

    public int evaluate() {
        return evaluation.evaluate();
    }

    public void fromFEN(String FEN) {
        String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };
        String[] symbols = { "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r",
                "q", "k" };
        int[] pieces = { 1, 2, 3, 4, 5, 6, -1, -2, -3, -4, -5, -6 };

        int pos = -1;
        int file = 0;
        int rank = 7;
        String c = "";
        while (true) {
            pos++;
            c = FEN.substring(pos, pos + 1);
            if (c.equals(" "))
                break;

            // handle piece symbol
            int pieceType = 0;
            for (int i = 0; i < symbols.length; i++) {
                if (c.equals(symbols[i])) {
                    pieceType = pieces[i];
                    break;
                }
            }
            if (pieceType != 0) {
                board0x88[rank * 16 + file] = pieceType;
                file++;
                continue;
            }

            // handle slash
            if (c.equals("/")) {
                file = 0;
                rank--;
                continue;
            }

            // handle number
            int n = Integer.parseInt(c);
            for (int i = 0; i < n; i++) {
                board0x88[rank * 16 + file] = 0;
                file++;
            }
        }

        // read color to move
        pos++;
        c = FEN.substring(pos, pos + 1);
        pos++;
        if (c.equals("w"))
            toMove = 1;
        else if (c.equals("b"))
            toMove = -1;

        // castling rights
        for (int i = 0; i < 4; i++)
            castlingRights[i] = false;
        while (true) {
            pos++;
            c = FEN.substring(pos, pos + 1);
            if (c.equals(" "))
                break;
            else if (c.equals("K"))
                castlingRights[0] = true;
            else if (c.equals("Q"))
                castlingRights[1] = true;
            else if (c.equals("k"))
                castlingRights[2] = true;
            else if (c.equals("q"))
                castlingRights[3] = true;
        }

        // en passant
        pos++;
        c = FEN.substring(pos, pos + 1);
        if (c.equals("-"))
            enPassant = -1;
        else {
            int index;
            for (index = 0; index < fileSymbols.length; index++) {
                if (fileSymbols[index].equals(c))
                    break;
            }
            file = index;
            pos++;
            rank = Integer.parseInt(FEN.substring(pos, pos + 1)) - 1;
            enPassant = rank * 16 + file;
        }
        pos++;

        // halfmoves
        int startPos = pos + 1;
        while (true) {
            pos++;
            c = FEN.substring(pos, pos + 1);
            if (c.equals(" "))
                break;
        }
        halfmoves = Integer.parseInt(FEN.substring(startPos, pos));

        // move number
        startPos = pos + 1;
        while (true) {
            pos++;
            if (pos == FEN.length())
                break;
            c = FEN.substring(pos, pos + 1);
        }
        moveN = Integer.parseInt(FEN.substring(startPos, pos));

        // generate hash
        zobrist.setHash(this);

        // reset hashHistory
        hashHistory = new ArrayList<Long>();
        hashHistory.add(hash);
    }
    

    
    public ArrayList<Move> generateAllMoves() {
    	long start = System.nanoTime();
    	@SuppressWarnings("unchecked")
		Future f = es.submit(new Callable() {
    	    public Object call() throws Exception{
    	    		ArrayList<Move> moves = new ArrayList<Move>();
    	    		for ( int i = 0; i < 64; i++) {
    	            	/*ignore numbers 8-15,24-31,40-47, 56-63, 72-79, 88-95, 104-111, 120-127
    	            	 * only 0-7, 16-23 etc are used as each of the rows on the board*/
    	                if ((i & 0x88) != 0)
    	                    continue;
    	                generateMoves(board0x88[i], i, moves);
    	                i++;
    	            }

    	            removeIllegalMoves(moves);

    	            return moves;
  
    	    }
		});
    	@SuppressWarnings("unchecked")
		Future g = es.submit(new Callable() {
    	    public Object call() throws Exception{
    	    		ArrayList<Move> moves = new ArrayList<Move>();
    	    		for ( int i = 64; i < 128; i++) {
    	            	/*ignore numbers 8-15,24-31,40-47, 56-63, 72-79, 88-95, 104-111, 120-127
    	            	 * only 0-7, 16-23 etc are used as each of the rows on the board*/
    	                if ((i & 0x88) != 0)
    	                    continue;
    	                generateMoves(board0x88[i], i, moves);
    	                i++;
    	            }

    	            removeIllegalMoves(moves);

    	            return moves;
  
    	    }
		});
    	try {
			ArrayList<Move> fList = (ArrayList<Move>)f.get();
			ArrayList<Move> gList = (ArrayList<Move>)g.get();
			fList.addAll(gList);
			System.out.println(System.nanoTime()-start);
			return fList;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<Move>();
    }

    private void generateMoves(int piece, int from, ArrayList<Move> moves) {
    	//long start = System.currentTimeMillis();
        if (toMove * piece < 0)
            return;
        int pieceType = Math.abs(piece);
        // pawns
        if (pieceType == 1) {
            generatePawnMoves(piece, from, moves);
        }
        // knight or king
        else if (pieceType == 2 || pieceType == 6) {
            generateNonSlidingMoves(piece, from, moves);
        }
        // bishop, rook or queen
        else if (pieceType >= 3 && pieceType <= 5) {
            generateSlidingMoves(piece, from, moves);
        }
        
      //  long fin = System.currentTimeMillis();
       // System.out.println(start-fin);
    }

    private void generateNonSlidingMoves(int piece, int from,
            ArrayList<Move> moves) {
        int[] deltas;
        if (piece == 2 || piece == -2)
            deltas = knightDeltas;
        else
            deltas = kingDeltas;

        for (int i = 0; i < deltas.length; i++) {
            int to = from + deltas[i];
            if ((to & 0x88) != 0)
                continue;
            if (board0x88[to] > 0 && piece > 0)
                continue;
            if (board0x88[to] < 0 && piece < 0)
                continue;
            moves.add(new Move(this, from, to));
        }

        // castling moves
        if (piece == 6) {
            if (castlingRights[0]) {
                if (board0x88[5] == 0 && board0x88[6] == 0) {
                    if (squareAttacked(4, -1) == false
                            && squareAttacked(5, -1) == false
                            && squareAttacked(6, -1) == false) {
                        moves.add(new Move(this, from, from + 2));
                    }
                }
            }
            if (castlingRights[1]) {
                if (board0x88[1] == 0 && board0x88[2] == 0 && board0x88[3] == 0) {
                    if (squareAttacked(2, -1) == false
                            && squareAttacked(3, -1) == false
                            && squareAttacked(4, -1) == false) {
                        moves.add(new Move(this, from, from - 2));
                    }
                }
            }
        } else if (piece == -6) {
            if (castlingRights[2]) {
                if (board0x88[117] == 0 && board0x88[118] == 0) {
                    if (squareAttacked(116, 1) == false
                            && squareAttacked(117, 1) == false
                            && squareAttacked(118, 1) == false) {
                        moves.add(new Move(this, from, from + 2));
                    }
                }
            }
            if (castlingRights[3]) {
                if (board0x88[113] == 0 && board0x88[114] == 0
                        && board0x88[115] == 0) {
                    if (squareAttacked(114, 1) == false
                            && squareAttacked(115, 1) == false
                            && squareAttacked(116, 1) == false) {
                        moves.add(new Move(this, from, from - 2));
                    }
                }
            }
        }
    }

    private void generateSlidingMoves(int piece, int from, ArrayList<Move> moves) {
        int[] deltas;
        if (piece == 3 || piece == -3)
            deltas = bishopDeltas;
        else if (piece == 4 || piece == -4)
            deltas = rookDeltas;
        else
            deltas = queenDeltas;

        for (int i = 0; i < deltas.length; i++) {
            int delta = deltas[i];
            int to = from;
            while (true) {
                to += delta;
                if ((to & 0x88) != 0)
                    break;
                if (board0x88[to] > 0 && piece > 0 || board0x88[to] < 0
                        && piece < 0)
                    break;
                if (board0x88[to] > 0 && piece < 0 || board0x88[to] < 0
                        && piece > 0) {
                    moves.add(new Move(this, from, to));
                    break;
                }
                moves.add(new Move(this, from, to));
            }
        }
    }

    private void generatePawnMoves(int piece, int from, ArrayList<Move> moves) {
        // white pawns
        if (piece == 1) {
            // normal move (1 squar ahead)
            if (board0x88[from + 16] == 0)
                moves.add(new Move(this, from, from + 16));
            // 2 squares ahead
            if (from / 16 == 1 && board0x88[from + 16] == 0
                    && board0x88[from + 2 * 16] == 0)
                moves.add(new Move(this, from, from + 2 * 16));
            // capturing moves
            if (board0x88[from + 15] < 0 && ((from + 15) & 0x88) == 0)
                moves.add(new Move(this, from, from + 15));
            if (board0x88[from + 17] < 0 && ((from + 17) & 0x88) == 0)
                moves.add(new Move(this, from, from + 17));
            // en passant capture
            if (enPassant != -1 && enPassant / 16 == 5) {
                if (from + 15 == enPassant || from + 17 == enPassant)
                    moves.add(new Move(this, from, enPassant));
            }
        }

        // black pawns
        else if (piece == -1) {
            // normal move (1 square ahead)
            if (board0x88[from - 16] == 0)
                moves.add(new Move(this, from, from - 16));
            // 2 squares ahead
            if (from / 16 == 6 && board0x88[from - 16] == 0
                    && board0x88[from - 2 * 16] == 0)
                moves.add(new Move(this, from, from - 2 * 16));
            // capturing moves
            if (((from - 15) & 0x88) == 0 && board0x88[from - 15] > 0)
                moves.add(new Move(this, from, from - 15));
            if (((from - 17) & 0x88) == 0 && board0x88[from - 17] > 0)
                moves.add(new Move(this, from, from - 17));
            // en passant capture
            if (enPassant != -1 && enPassant / 16 == 2) {
                if (from - 15 == enPassant || from - 17 == enPassant)
                    moves.add(new Move(this, from, enPassant));
            }
        }
    }

    /** color can be 1 for white or -1 for black */
    public boolean inCheck(int color) {
        // find king
        int king = -1;
        for (int i = 0; i < 128; i++) {
            if ((i & 0x88) != 0)
                continue;
            if (board0x88[i] == 6 * color) {
                king = i;
                break;
            }
        }

        return squareAttacked(king, color * -1);
    }

    public boolean isCheckmate() {
        if (generateAllMoves().size() == 0 && inCheck(toMove)) {
            return true;
        } else
            return false;
    }

    public boolean isDraw50Move() {
        if (halfmoves >= 100)
            return true;
        else
            return false;
    }

    public boolean isEndgame() {
        boolean wQueen = false;
        boolean bQueen = false;
        int wRooks = 0;
        int bRooks = 0;
        int wMinors = 0;
        int bMinors = 0;

        for (int i = 0; i < 128; i++) {
            if ((i & 0x88) != 0)
                continue;
            int piece = board0x88[i];
            switch (piece) {
            case 5:
                wQueen = true;
            case -5:
                bQueen = true;
            case 4:
                wRooks++;
            case -4:
                bRooks++;
            case 3:
                wMinors--;
            case -3:
                bMinors--;
            case 2:
                wMinors--;
            case -2:
                bMinors--;
            }
        }

        boolean endgame = true;
        if (wQueen && (wMinors > 1 || wRooks > 0))
            endgame = false;
        if (bQueen && (bMinors > 1 || bRooks > 0))
            endgame = false;

        return endgame;
    }

    public boolean isRepetition() {
        int hits = 1;
        for (int i = hashHistory.size() - 2; i >= 0; i--) {
            if (hashHistory.get(i) == hash)
                hits++;
        }

        if (hits >= 3) {
            return true;
        } else
            return false;
    }

    public boolean isStalemate() {
        if (generateAllMoves().size() == 0 && !inCheck(toMove)) {
            return true;
        } else
            return false;
    }

    public void init() {
        fromFEN(STARTING_FEN);

        // en passant
        enPassant = -1;

        // castling rights
        for (int i = 0; i < 4; i++)
            castlingRights[i] = true;

        toMove = 1;

        moveN = 0;
    }

    public boolean[] legalMovesMap(int from) {
        ArrayList<Move> moves = new ArrayList<Move>();
        generateMoves(board0x88[from], from, moves);
        removeIllegalMoves(moves);

        boolean[] map = new boolean[128];
        for (int i = 0; i < moves.size(); i++) {
            map[moves.get(i).to] = true;
        }

        return map;
    }

    private void removeIllegalMoves(ArrayList<Move> moves) {
        Iterator<Move> iter = moves.iterator();
        while (iter.hasNext()) {
            int color = toMove;
            Move move = (Move) iter.next();
            doMove(move);
            boolean illegalMove = false;
            if (inCheck(color))
                illegalMove = true;
            undoMove(move);
            if (illegalMove)
                iter.remove();
        }
    }

>>>>>>> Stashed changes
    /**
     * by - if caller is asking whether the square is attacked by white(1) or
     * black(-1)
     *///####[565]####
    private boolean squareAttacked(int square, int by) {//####[565]####
        for (int i = 0; i < knightDeltas.length; i++) //####[567]####
        {//####[567]####
            int square2 = square + knightDeltas[i];//####[568]####
            if ((square2 & 0x88) == 0 && board0x88[square2] == 2 * by) //####[569]####
            return true;//####[570]####
        }//####[571]####
        if (by == -1 && (validSquare(square + 15) && board0x88[square + 15] == -1 || validSquare(square + 17) && board0x88[square + 17] == -1)) //####[574]####
        return true; else if (by == 1 && (validSquare(square - 15) && board0x88[square - 15] == 1 || validSquare(square - 17) && board0x88[square - 17] == 1)) //####[578]####
        return true;//####[581]####
        for (int i = 0; i < queenDeltas.length; i++) //####[584]####
        {//####[584]####
            int delta = queenDeltas[i];//####[585]####
            if (((square + delta) & 0x88) == 0 && board0x88[square + delta] == 6 * by) //####[588]####
            return true;//####[590]####
            int square2 = square;//####[592]####
            while (true) //####[593]####
            {//####[593]####
                square2 += delta;//####[594]####
                if ((square2 & 0x88) != 0) //####[595]####
                break;//####[596]####
                if (board0x88[square2] == 0) //####[597]####
                continue;//####[598]####
                if (board0x88[square2] * by < 0) //####[599]####
                break;//####[600]####
                if (board0x88[square2] * by > 0 && Math.abs(board0x88[square2]) >= 3 && Math.abs(board0x88[square2]) <= 5) //####[601]####
                {//####[603]####
                    if (Math.abs(board0x88[square2]) == 5) //####[604]####
                    return true;//####[605]####
                    if (i <= 3 && Math.abs(board0x88[square2]) == 4) //####[606]####
                    return true;//####[607]####
                    if (i >= 4 && Math.abs(board0x88[square2]) == 3) //####[608]####
                    return true;//####[609]####
                }//####[610]####
                break;//####[611]####
            }//####[612]####
        }//####[613]####
        return false;//####[615]####
    }//####[616]####
//####[618]####
    public String toFEN() {//####[618]####
        String[] symbols = { "", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k" };//####[619]####
        String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };//####[621]####
        StringBuffer FEN = new StringBuffer(100);//####[623]####
        for (int i = 7; i >= 0; i--) //####[624]####
        {//####[624]####
            int counter = 0;//####[625]####
            for (int j = 0; j <= 7; j++) //####[626]####
            {//####[626]####
                if (board0x88[i * 16 + j] == 0) //####[627]####
                {//####[627]####
                    counter++;//####[628]####
                    continue;//####[629]####
                } else {//####[630]####
                    if (counter > 0) //####[631]####
                    FEN.append(Integer.toString(counter));//####[632]####
                    counter = 0;//####[633]####
                    int index = -1;//####[634]####
                    if (board0x88[i * 16 + j] < 0) //####[635]####
                    index = board0x88[i * 16 + j] * -1 + 6; else if (board0x88[i * 16 + j] > 0) //####[637]####
                    index = board0x88[i * 16 + j];//####[638]####
                    FEN.append(symbols[index]);//####[639]####
                }//####[640]####
            }//####[641]####
            if (counter > 0) //####[642]####
            FEN.append(Integer.toString(counter));//####[643]####
            if (i != 0) //####[644]####
            FEN.append("/");//####[645]####
        }//####[646]####
        if (toMove == 1) //####[649]####
        FEN.append(" w "); else FEN.append(" b ");//####[650]####
        String[] castlingSymbols = { "K", "Q", "k", "q" };//####[655]####
        boolean noCastling = true;//####[656]####
        for (int i = 0; i < 4; i++) //####[657]####
        {//####[657]####
            if (castlingRights[i]) //####[658]####
            {//####[658]####
                FEN.append(castlingSymbols[i]);//####[659]####
                noCastling = false;//####[660]####
            }//####[661]####
        }//####[662]####
        if (noCastling) //####[663]####
        FEN.append("-");//####[664]####
        FEN.append(" ");//####[665]####
        if (enPassant == -1) //####[668]####
        FEN.append("- "); else FEN.append(fileSymbols[enPassant & 7] + Integer.toString(enPassant / 16 + 1) + " ");//####[669]####
        FEN.append(Integer.toString(halfmoves) + " ");//####[675]####
        FEN.append(Integer.toString(moveN));//####[678]####
        return FEN.toString();//####[680]####
    }//####[681]####
//####[683]####
    public void undoMove(Move move) {//####[683]####
        zobrist.undoMove(this, move);//####[685]####
        hashHistory.remove(hashHistory.size() - 1);//####[686]####
        if (toMove == -1) //####[688]####
        moveN--;//####[689]####
        toMove *= -1;//####[690]####
        if (move.piece == 1 && board0x88[move.to] != 1) //####[693]####
        board0x88[move.to] = 1; else if (move.piece == -1 && board0x88[move.to] != -1) //####[695]####
        board0x88[move.to] = -1;//####[696]####
        board0x88[move.from] = move.piece;//####[699]####
        board0x88[move.to] = move.capture;//####[700]####
        if (move.from == 4 && move.to == 6 && move.piece == 6) //####[703]####
        {//####[703]####
            board0x88[7] = 4;//####[704]####
            board0x88[5] = 0;//####[705]####
        } else if (move.from == 4 && move.to == 2 && move.piece == 6) //####[706]####
        {//####[706]####
            board0x88[0] = 4;//####[707]####
            board0x88[3] = 0;//####[708]####
        } else if (move.from == 116 && move.to == 118 && move.piece == -6) //####[709]####
        {//####[709]####
            board0x88[119] = -4;//####[710]####
            board0x88[117] = 0;//####[711]####
        } else if (move.from == 116 && move.to == 114 && move.piece == -6) //####[712]####
        {//####[712]####
            board0x88[112] = -4;//####[713]####
            board0x88[115] = 0;//####[714]####
        }//####[715]####
        halfmoves = move.halfmoves;//####[718]####
        enPassant = move.enPassant;//####[721]####
        if ((move.piece == 1 || move.piece == -1) && (move.from & 7) != (move.to & 7) && move.capture == 0) //####[724]####
        {//####[725]####
            if (move.piece == 1) //####[726]####
            board0x88[move.to - 16] = -1;//####[727]####
            if (move.piece == -1) //####[728]####
            board0x88[move.to + 16] = 1;//####[729]####
        }//####[730]####
        for (int i = 0; i < 4; i++) //####[733]####
        castlingRights[i] = move.castlingRights[i];//####[734]####
    }//####[735]####
//####[737]####
    private boolean validSquare(int square) {//####[737]####
        if ((square & 0x88) == 0) //####[738]####
        return true; else return false;//####[739]####
    }//####[742]####
//####[744]####
    public void printBoard() {//####[744]####
        for (int i = 0; i < board0x88.length; i++) //####[745]####
        {//####[745]####
            if ((i % 8) == 0) //####[746]####
            {//####[746]####
                System.out.println();//####[747]####
            }//####[748]####
            System.out.print(board0x88[i]);//####[749]####
        }//####[750]####
    }//####[751]####
}//####[751]####
