package cz.hejl.chesswalk;

import java.util.ArrayList;

import cz.hejl.chesswalk.FicsParser.Rating;
import cz.hejl.chesswalk.OnlineGameActivity.MatchEnd;

public class Listeners {
	
	public static interface GameOffersListener {
		public void onConnException();
		
		public void onMatchStarted(OnlineGameState onlineGameState);
				
		public void onSeekUnavailable();
		
		public void onUpdate(ArrayList<GameOffer> games);
	}
	
	public interface MoveListener {
		public void pieceMoved(Move move);
	}
	
	public interface OnlineGameListener {
		public void onChat(String message);
		
		public void onConnException();

		public void onDrawOffer();

		public void onDrawAnswer(int answer);

		public void onMatchEnd(MatchEnd matchEnd);
		
		public void onOnlineMove(OnlineGameState onlineGameState);

		public void onRatingChange(int[] ratings);
	}

	
	public static interface SeekListener {
		public void onConnException();
		
		public void onMatchStarted(OnlineGameState onlineGameState);
		
		public void onRating(Rating rating);
	}
	
}
