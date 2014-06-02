package cz.hejl.chesswalk.test;

import java.util.ArrayList;

import cz.hejl.chesswalk.Board;
import cz.hejl.chesswalk.Engine;
import cz.hejl.chesswalk.Move;
import cz.hejl.chesswalk.SerialEngine;
import junit.framework.TestCase;

public class TestParallelization extends TestCase {

	Engine engine;
	SerialEngine serialEngine;
	
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

	public void testParallelComputerStarting10Secs(){
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
		double averageNodes = avgN.doubleValue() / nodes.size();
		System.out.print("Parallel Average nodes = "+averageNodes);
	}
	
	public void testSerialComputerStarting10Secs(){
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
		double averageNodes = avgN.doubleValue() / nodes.size();
		System.out.print("Serial Average nodes = "+averageNodes);
	}
}
