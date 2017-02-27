package com.daansander.gamecore.events;

import com.daansander.gamecore.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A superclass for all {@link Game} events
 *
 * @author Daan Meijer
 * @since 1.0
 */
public abstract class GameEvent extends Event {

    private Game game;
    private HandlerList handlerList = new HandlerList();

    /**
     * Constructor for a {@link GameEvent} object
     *
     * @param game the game where the event has been executed from
     */
    protected GameEvent(Game game) {
        this.game = game;
    }

    /**
     * Getter for the {@link GameEvent#game} object
     *
     * @return the {@link Game} object where the event has been executed from
     */
    public Game getGame() {
        return game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
