package cz.hejl.chesswalk;

public class GameOffer {
	
	boolean computer = false;
	boolean rated;
	int color;
	int rating;
	int timeInt;
	String id;
	String username;
	String time;
	String increment;

	// -------------------------------------------------------------------------------------------------------

	public GameOffer(String id, int rating, String username, String time, String increment, boolean rated,
			int color) {
		this.id = id;
		this.rating = rating;
		this.username = username;
		if (username.endsWith("(C)")) {
			computer = true;
			this.username = username.substring(0, username.length() - 3);
		}
		this.time = time;
		timeInt = Integer.parseInt(time);
		this.increment = increment;
		this.rated = rated;
		this.color = color;
	}
	
}
