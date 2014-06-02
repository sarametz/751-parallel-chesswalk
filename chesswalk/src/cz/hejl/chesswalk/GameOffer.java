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

    public GameOffer(String id, int rating, String username, String time,
            String increment, boolean rated, int color) {
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
