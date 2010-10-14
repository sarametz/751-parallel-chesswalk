/*
 * Copyright (C) 2010 František Hejl
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
