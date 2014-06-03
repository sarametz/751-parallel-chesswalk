package cz.hejl.chesswalk.test;

import java.util.ArrayList;

import cz.hejl.chesswalk.Board;
import cz.hejl.chesswalk.Engine;
import cz.hejl.chesswalk.Move;
import cz.hejl.chesswalk.ParallelBoard;
import cz.hejl.chesswalk.SerialEngine;
import junit.framework.TestCase;

public class TestParallelization extends TestCase {

	Engine engine;
	SerialEngine serialEngine;
	
	double averageAlphaBetaParallelDepth = 0;
	double averageSerialDepth = 0;
	double averageGenerateMovesParallelDepth = 0;
	
	double averageAlphaBetaParallelNodes = 0;
	double averageSerialNodes = 0;
	double averageGenerateMovesParallelNodes = 0;
	
	public TestParallelization(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		engine = new Engine();
		serialEngine = new SerialEngine();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPreconditions(){
		assertNotNull("Engine is null",engine);
		assertNotNull("Serial Engine is null",serialEngine);
	}

	public void getParallelComputerStarting10Secs(){
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for(int i = 0; i < 10; i++){
			Move move = engine.bestMove(Board.STARTING_FEN, 10, 10000);
			int nodeCount = engine.nodeCounter.get();
			nodes.add(Integer.valueOf(nodeCount));
		}

		//Look at the average values
		Integer avgN = 0;
		for (Integer n : nodes) {
			avgN += n;
		}
		averageAlphaBetaParallelNodes = avgN.doubleValue() / nodes.size();
		averageAlphaBetaParallelDepth = engine.bestLineDepth;
	}
	
	public void getSerialComputerStarting10Secs(){
		serialEngine.board = new Board(Board.STARTING_FEN);
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for(int i = 0; i < 10; i++){
			Move move = serialEngine.bestMove(10, 10000);
			int nodeCount = serialEngine.nodeCounter;
			nodes.add(Integer.valueOf(nodeCount));
		}

		//Look at the average values
		Integer avgN = 0;
		for (Integer n : nodes) {
			avgN += n;
		}
		averageSerialNodes = avgN.doubleValue() / nodes.size();
		averageSerialDepth = serialEngine.bestLineDepth;
	}
	
	public void getSerialEngineWithParallelBoardComputerStarting10Secs(){
		serialEngine.board = new ParallelBoard(Board.STARTING_FEN);
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for(int i = 0; i < 10; i++){
			Move move = serialEngine.bestMove(10, 10000);
			int nodeCount = serialEngine.nodeCounter;
			nodes.add(Integer.valueOf(nodeCount));
		}

		//Look at the average values
		Integer avgN = 0;
		for (Integer n : nodes) {
			avgN += n;
		}
		averageGenerateMovesParallelNodes = avgN.doubleValue() / nodes.size();
		averageGenerateMovesParallelDepth = serialEngine.bestLineDepth;
	}
	
	public void testParallelComparison(){
		long start = System.currentTimeMillis();
		getSerialComputerStarting10Secs();
		long serialTime = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		getSerialEngineWithParallelBoardComputerStarting10Secs();
		long generateMovesTime = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		getParallelComputerStarting10Secs();
		long alphaBetaTime = System.currentTimeMillis() - start;
		//results
		System.out.println("Average depth and nodes\n"+
							"Serial       :\t" + averageSerialDepth + "\tNodes: \t" + averageSerialNodes +"\t time:"+serialTime+"\n" +
							"GenerateMoves:\t" + averageGenerateMovesParallelDepth + "\tNodes: \t" + averageGenerateMovesParallelNodes +"\t time:"+generateMovesTime+"\n" +
							"AlphaBeta    :\t" + averageAlphaBetaParallelDepth + "\tNodes: \t" + averageAlphaBetaParallelNodes +"\t time:"+alphaBetaTime+"\n"
							);
	}
	
}
