package cz.hejl.chesswalk;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class OnlineGameState implements Serializable {

	public boolean whiteToMove;
	public boolean[] castlingRights = new boolean[4];
	public int enPassant;
	public int from;
	public int to;
	public int blackTime;
	public int whiteTime;
	public int sideToMove; // 1: my move, -1: opponents move
	public int moveToBeMade;
	public String blackName;
	public String whiteName;

	private static char[] piecesChars = { '-', 'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k' };
	private static int[] piecesInts = { 0, 1, 2, 3, 4, 5, 6, -1, -2, -3, -4, -5, -6 };
	private static Matcher moveMatcher = Pattern.compile("\\w/(\\w)(\\d)-(\\w)(\\d)").matcher("");
	private static String[] fileSymbols = { "a", "b", "c", "d", "e", "f", "g", "h" };

	private String[] ranks = new String[8];

	public Board getBoard() {
		Board board = new Board();
		for (int i = 0; i <= 7; i++) {
			String s = ranks[i];
			int rank = 7 - i;
			for (int j = 0; j < 8; j++) {
				int pieceType = 0;
				for (int k = 0; k < piecesChars.length; k++) {
					if (s.charAt(j) == piecesChars[k]) {
						pieceType = piecesInts[k];
						break;
					}
				}
				board.board0x88[rank * 16 + j] = pieceType;
			}
		}

		board.enPassant = enPassant;
		board.castlingRights = castlingRights;

		return board;
	}

	public OnlineGameState(Matcher matcher) {
		for (int i = 0; i < 8; i++) {
			ranks[i] = matcher.group(i + 1);
		}
		if (matcher.group(9).equals("W"))
			whiteToMove = true;
		else
			whiteToMove = false;

		int enPassantFile = Integer.parseInt(matcher.group(10));
		int enPassantRank = 2;
		if (whiteToMove)
			enPassantRank = 5;
		enPassant = enPassantRank * 16 + enPassantFile;

		castlingRights[0] = false;
		castlingRights[1] = false;
		castlingRights[2] = false;
		castlingRights[3] = false;
		if (Integer.parseInt(matcher.group(11)) == 1)
			castlingRights[0] = true;
		if (Integer.parseInt(matcher.group(12)) == 1)
			castlingRights[1] = true;
		if (Integer.parseInt(matcher.group(13)) == 1)
			castlingRights[2] = true;
		if (Integer.parseInt(matcher.group(14)) == 1)
			castlingRights[3] = true;
		whiteName = matcher.group(17);
		blackName = matcher.group(18);
		sideToMove = Integer.parseInt(matcher.group(19));
		whiteTime = Integer.parseInt(matcher.group(24));
		blackTime = Integer.parseInt(matcher.group(25));
		moveToBeMade = Integer.parseInt(matcher.group(26));

		if (matcher.group(27).equals("o-o")) {
			if (whiteToMove) {
				from = 116;
				to = 118;
			} else {
				from = 4;
				to = 6;
			}
		} else if (matcher.group(27).equals("o-o-o")) {
			if (whiteToMove) {
				from = 116;
				to = 114;
			} else {
				from = 4;
				to = 2;
			}
		} else if (matcher.group(27).equals("none")) {
			from = -1;
			to = -1;
		} else {
			moveMatcher.reset(matcher.group(27));
			moveMatcher.find();
			String fileStr = moveMatcher.group(1);
			int file1 = -1;
			for (int i = 0; i < 8; i++) {
				if (fileStr.equals(fileSymbols[i])) {
					file1 = i;
					break;
				}
			}
			int rank1 = Integer.parseInt(moveMatcher.group(2)) - 1;

			fileStr = moveMatcher.group(3);
			int file2 = -1;
			for (int i = 0; i < 8; i++) {
				if (fileStr.equals(fileSymbols[i])) {
					file2 = i;
					break;
				}
			}
			int rank2 = Integer.parseInt(moveMatcher.group(4)) - 1;

			from = rank1 * 16 + file1;
			to = rank2 * 16 + file2;
		}
	}
}
