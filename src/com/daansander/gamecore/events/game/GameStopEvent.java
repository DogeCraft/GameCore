package com.daansander.gamecore.events.game;

import com.daansander.gamecore.Game;
import com.daansander.gamecore.events.GameEvent;

/**
 * Event that is called when a game is stopped
 * 
 * @author Daan Meijer
 * @since 1.0
 */
public class GameStopEvent extends GameEvent {

    public GameStopEvent(Game game) {
        super(game);
    }
}