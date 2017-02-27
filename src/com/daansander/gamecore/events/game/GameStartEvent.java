package com.daansander.gamecore.events.game;

import com.daansander.gamecore.Game;
import com.daansander.gamecore.events.GameEvent;

/**
 * Event that is called when a game starts
 * 
 * @author Daan Meijer
 * @since 1.0
 */
public class GameStartEvent extends GameEvent {

    public GameStartEvent(Game game) {
        super(game);
    }
}