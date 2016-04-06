package com.spartech.ttt.socketio;

/**
 * Created by Mohammed Aouf ZOUAG on 04/04/2016.
 * <p>
 * A class that describes the kind of possible events during game play.
 */
public class Events {
    public static final String GAME_BEGIN = "gameBegin";
    public static final String OPPONENT_QUIT = "opponentQuit";
    public static final String MOVE_MADE = "moveMade";
    public static final String MAKE_MOVE = "makeMove";
    public static final String INCOMING_REMATCH_REQUEST = "rematchRequest";
}
